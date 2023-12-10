package com.danil.metals.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.LocationDisabled
import androidx.compose.material.icons.rounded.LocationSearching
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.UnfoldLess
import androidx.compose.material.icons.rounded.UnfoldMore
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.danil.metals.R
import com.danil.metals.data.ExploredLocation
import com.danil.metals.ui.MetalsUiState
import com.danil.metals.ui.MetalsViewModel
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

lateinit var cameraListener: CameraListener
lateinit var inputListener: InputListener
private lateinit var collection: MapObjectCollection
lateinit var lastKnownPolyPoints: List<Double>
lateinit var mapView: MapView

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    uiState: MetalsUiState,
    viewModel: MetalsViewModel,
    navController: NavHostController,
    activity: AppCompatActivity
) {
    var searchMode by rememberSaveable { mutableStateOf(false) }
    Box {
        AndroidView(
            factory = {
                View.inflate(it, R.layout.main_layout, null)
            },
            update = { view ->
                // get access to XML widget
                mapView = view.findViewById(R.id.mapview)

                // position the map
                mapView.mapWindow.map
                    .move(
                        CameraPosition(
                            Point(uiState.location[0], uiState.location[1]),
                            uiState.zoom,
                            uiState.azimuth,
                            uiState.tilt,
                        )
                    )

                if (!uiState.listenersImplemented) {
                    viewModel.setListenersImplemented()
                    cameraListener = object : CameraListener {
                        override fun onCameraPositionChanged(
                            p0: Map,
                            cameraPosition: CameraPosition,
                            p2: CameraUpdateReason,
                            finished: Boolean
                        ) {
                            if (finished) {
                                if (navController.currentDestination?.route != MetalsViewModel.Screens.EditScreen.name)
                                    viewModel.setLocation(
                                        cameraPosition.target.latitude,
                                        cameraPosition.target.longitude,
                                        cameraPosition.zoom,
                                        cameraPosition.azimuth,
                                        cameraPosition.tilt
                                    )
                            }
                        }
                    }

                    inputListener = object : InputListener {
                        override fun onMapTap(map: Map, point: Point) {
                            if (viewModel.uiState.value.editMode) {
                                if (viewModel.uiState.value.locationAdded) {
                                    viewModel.addExploredLocation(point)
                                    viewModel.setShowings(
                                        viewModel.uiState.value.exploredLocations.last(),
                                        point
                                    )
                                } else {
                                    viewModel.setLastExploredPoint(
                                        viewModel.uiState.value.showingLocation,
                                        point
                                    )
                                }
                            }
                        }

                        override fun onMapLongTap(map: Map, point: Point) {
                            if (viewModel.uiState.value.editMode) {
                                navController.navigate(MetalsViewModel.Screens.EditScreen.name)
                            }

                        }

                    }

                }

                mapView.mapWindow.map.addCameraListener(cameraListener)
                mapView.mapWindow.map.addInputListener(inputListener)

                try {
                    if (uiState.exploredLocations.map { it.points }
                            .reduce { result, it -> result + it }
                            .map { it.latitude + it.longitude } != lastKnownPolyPoints ||
                        uiState.editMode || uiState.currentScreen != uiState.previousScreen) {
                        if (uiState.currentScreen != uiState.previousScreen)
                            viewModel.setCurrentScreen(uiState.currentScreen)
                        collection.clear()
                        collection = mapView.mapWindow.map.mapObjects.addCollection()
                        lastKnownPolyPoints = uiState.exploredLocations.map { it.points }
                            .reduce { result, it -> result + it }
                            .map { it.latitude + it.longitude }
                        for (location in uiState.exploredLocations) {
                            val fusedContainment = location.elements.values.sortedBy { it[0].toDouble() }
                            val fusedCoefficient = location.elements.values.sortedBy { it[1].toDouble() }

                            if (uiState.zcRangeStart.isNotEmpty())
                                if (viewModel.calculateZc(location) < uiState.zcRangeStart.toDouble())
                                    continue
                            if (uiState.zcRangeEnd.isNotEmpty())
                                if (viewModel.calculateZc(location) > uiState.zcRangeEnd.toDouble())
                                    continue

                            if (uiState.containmentRangeStart.isNotEmpty())
                                if (fusedContainment.first()[0] < uiState.containmentRangeStart.toDouble())
                                    continue
                            if (uiState.containmentRangeEnd.isNotEmpty())
                                if (fusedContainment.last()[0] > uiState.containmentRangeEnd.toDouble())
                                    continue

                            if (uiState.coefficientRangeStart.isNotEmpty())
                                if (fusedCoefficient.first()[1] < uiState.coefficientRangeStart.toDouble())
                                    continue
                            if (uiState.coefficientRangeEnd.isNotEmpty())
                                if (fusedCoefficient.last()[1] > uiState.coefficientRangeEnd.toDouble())
                                    continue

                            val polygon = Polygon(LinearRing(location.points), listOf())
                            collection.addPolygon(polygon).apply {
                                strokeWidth = 1f
                                strokeColor = ContextCompat.getColor(activity, R.color.black)
                                fillColor =
                                    ContextCompat.getColor(
                                        activity,
                                        viewModel.selectColor(location)
                                    )
                                userData = location
                                addTapListener { mapObject, point ->
                                    if (viewModel.uiState.value.editMode &&
                                        viewModel.uiState.value.showingLocation == (mapObject.userData as ExploredLocation)) {
                                        viewModel.setLastExploredPoint(
                                            viewModel.uiState.value.showingLocation,
                                            point
                                        )
                                    } else {
                                        viewModel.setShowings(
                                            mapObject.userData as ExploredLocation,
                                            point
                                        )
                                    }
                                    if (!viewModel.uiState.value.editMode) {
                                        navController.navigate(MetalsViewModel.Screens.ResearchScreen.name)
                                    }
                                    true
                                }
                            }
                        }

                    }
                } catch (e: Exception) {
                    lastKnownPolyPoints =
                        if (uiState.exploredLocations.isEmpty()) listOf() else uiState.exploredLocations.map { it.points }
                            .reduce { result, it -> result + it }
                            .map { it.latitude + it.longitude }
                    collection = mapView.mapWindow.map.mapObjects.addCollection()
                }



                if (uiState.realLocation != null) {
                    val imageProvider = ImageProvider.fromResource(activity, R.drawable.me)
                    collection.addPlacemark().apply {
                        geometry = Point(uiState.realLocation[0], uiState.realLocation[1])
                        setIcon(imageProvider)
                    }
                    lastKnownPolyPoints = listOf()
                }

                if (uiState.editMode) {
                    val imageProvider = ImageProvider.fromResource(activity, R.drawable.pointer)
                    for (elem in uiState.showingLocation.points) {
                        collection.addPlacemark().apply {
                            geometry = Point(elem.latitude, elem.longitude)
                            setIcon(imageProvider)
                        }
                    }
                    lastKnownPolyPoints = listOf()
                }

                if (searchMode && uiState.searchedPoint != null) {
                    val imageProvider = ImageProvider.fromResource(activity, R.drawable.mmark)
                    collection.addPlacemark().apply {
                        geometry = uiState.searchedPoint
                        setIcon(imageProvider)
                    }
                    lastKnownPolyPoints = listOf()
                }

            }
        )

        val locationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { }
        )

        var menuExpanded by rememberSaveable { mutableStateOf(false) }
        Column(
            Modifier
                .width(50.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = MaterialTheme.shapes.small
                )
                .align(Alignment.CenterEnd)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = MaterialTheme.shapes.small
                )
                .animateContentSize(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMedium,
                        dampingRatio = Spring.DampingRatioNoBouncy
                    )
                )
                .zIndex(10f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { menuExpanded = !menuExpanded }
            ) {
                Icon(
                    if (!menuExpanded) Icons.Rounded.UnfoldMore else Icons.Rounded.UnfoldLess,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            var locationButtonRole by rememberSaveable { mutableStateOf(true) }
            if (!uiState.editMode)
                IconButton(onClick = {
                    try {
                        if (locationButtonRole) {
                            if (ContextCompat.checkSelfPermission(
                                    activity,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) ==
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                LocationServices.getFusedLocationProviderClient(activity)
                                    .lastLocation.addOnSuccessListener {
                                        try {
                                            viewModel.setRealLocation(it.latitude, it.longitude)
                                            locationButtonRole = false
                                        } catch (e: Exception) {
                                            viewModel.showWarning(
                                                Pair(
                                                    R.string.try_enable,
                                                    R.string.ok
                                                )
                                            ) {
                                                viewModel.showWarning(null)
                                            }
                                        }
                                    }
                            } else {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                Toast.makeText(
                                    activity,
                                    ContextCompat.getString(
                                        activity,
                                        R.string.permission_needed
                                    ),
                                    Toast.LENGTH_LONG
                                ).show()
                                locationButtonRole = true
                            }
                        } else {
                            viewModel.setRealLocation()
                            locationButtonRole = true
                            lastKnownPolyPoints = listOf()
                        }
                    } catch (e: Exception) {
                        viewModel.showWarning(Pair(R.string.uncathed_exception, R.string.ok)) {
                            viewModel.showWarning(null)
                        }
                        locationButtonRole = true
                    }
                }) {
                    Icon(
                        if (locationButtonRole) Icons.Rounded.LocationSearching else Icons.Rounded.LocationDisabled,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            if (menuExpanded) {
                IconButton(onClick = {
                    if (uiState.verified == true) {
                        viewModel.setMode(!uiState.editMode)
                        if (uiState.editMode) {
                            viewModel.pushExplored()
                        } else {
                            viewModel.setShowings(uiState.exploredLocations.last())
                            viewModel.exploredLocationsBeforeEditing = uiState.exploredLocations
                        }
                    } else {
                        viewModel.showWarning(
                            Pair(
                                if (uiState.accountEmail != null)
                                    R.string.wait_for_moderation else
                                    R.string.have_to_sign_in, R.string.sign_in
                            )
                        ) {
                            viewModel.showWarning(null)
                            navController.navigate(MetalsViewModel.Screens.AccountScreen.name)
                        }

                    }
                }) {
                    Icon(
                        if (!uiState.editMode) Icons.Rounded.Edit else Icons.Rounded.Done,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (!uiState.editMode) {
                    IconButton(onClick = {
                        searchMode = !searchMode
                        lastKnownPolyPoints = listOf()
                        viewModel.setSearchedPoint(null)
                    }) {
                        Icon(
                            if (searchMode) Icons.Rounded.SearchOff else Icons.Rounded.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(onClick = {
                        navController.navigate(MetalsViewModel.Screens.FilterScreen.name)
                    }) {
                        Icon(
                            Icons.Rounded.FilterList,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    IconButton(onClick = {
                        viewModel.returnUiState()
                        lastKnownPolyPoints = listOf()
                        viewModel.setMode(false)
                    }) {
                        Icon(
                            Icons.Rounded.Undo,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(onClick = { viewModel.deleteLastExploredPoint() }) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(onClick = {
                        viewModel.setLocationAdded()
                    }) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

            }
        }
        var searchLine by rememberSaveable { mutableStateOf("") }
        var searchIsExpanded by rememberSaveable { mutableStateOf(true) }
        if (searchMode)
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(
                        start = dimensionResource(id = R.dimen.padding) / 4,
                        top = dimensionResource(id = R.dimen.padding) / 2,
                        end = dimensionResource(id = R.dimen.padding) * 2
                    )
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchLine,
                        onValueChange = {
                            searchLine = it
                            viewModel.trySearch(searchLine, mapView.mapWindow.map.visibleRegion)
                        },
                        label = {
                            Text(
                                stringResource(R.string.type_to_search),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        colors = textFieldColors(
                            textColor = MaterialTheme.colorScheme.onBackground,
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = { searchIsExpanded = !searchIsExpanded }) {
                        Icon(
                            if (searchIsExpanded) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.line_height) * 2)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding) / 2))

                if (uiState.searchResult.isNotEmpty() && searchIsExpanded)
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        for (elem in uiState.searchResult) {
                            Row(
                                modifier = Modifier.width(dimensionResource(id = R.dimen.search_area) * 1.55f)
                            ) {
                                Column(
                                    modifier = Modifier.width(dimensionResource(id = R.dimen.search_area) * 1.45f)
                                ) {
                                    Text(
                                        elem.first.split('%').first(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        elem.first.split('%').last(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .padding(dimensionResource(id = R.dimen.padding) / 2)
                                            .fillMaxWidth()
                                            .height(dimensionResource(R.dimen.line_height))
                                            .background(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = MaterialTheme.shapes.extraSmall
                                            )
                                    )
                                }
                                Spacer(Modifier.width(dimensionResource(id = R.dimen.padding)))
                                IconButton(
                                    onClick = {
                                        viewModel.setLocation(
                                            elem.second.latitude,
                                            elem.second.longitude,
                                            uiState.zoom,
                                            uiState.azimuth,
                                            uiState.tilt
                                        )
                                        viewModel.setSearchedPoint(elem.second)
                                        lastKnownPolyPoints = listOf()
                                    },
                                    modifier = Modifier
                                        .wrapContentSize(unbounded = true)
                                        .background(
                                            color = MaterialTheme.colorScheme.secondary,
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                ) {
                                    Icon(
                                        Icons.Rounded.ArrowForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                        }
                    }
            }

    }
}