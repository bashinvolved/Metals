package com.danil.metals.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.LocationDisabled
import androidx.compose.material.icons.rounded.LocationSearching
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.UnfoldLess
import androidx.compose.material.icons.rounded.UnfoldMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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

@Composable
fun MapScreen(
    uiState: MetalsUiState,
    viewModel: MetalsViewModel,
    navController: NavHostController,
    activity: AppCompatActivity
) {
    var screenHadSwitched by remember { mutableStateOf(true) }
    Box {
        AndroidView(
            factory = {
                View.inflate(it, R.layout.main_layout, null)
            },
            update = { view ->
                // get access to XML widget
                val mapView = view.findViewById<MapView>(R.id.mapview)

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
                            .map { it.latitude + it.longitude } != lastKnownPolyPoints || screenHadSwitched) {
                        if (screenHadSwitched) screenHadSwitched = false
                        collection.clear()
                        collection = mapView.mapWindow.map.mapObjects.addCollection()
                        lastKnownPolyPoints = uiState.exploredLocations.map { it.points }
                            .reduce { result, it -> result + it }.map { it.latitude + it.longitude }
                        for (location in uiState.exploredLocations) {
                            val polygon = Polygon(LinearRing(location.points), listOf())
                            collection.addPolygon(polygon).apply {
                                strokeWidth = 0f
                                fillColor =
                                    ContextCompat.getColor(
                                        activity,
                                        viewModel.selectColor(location)
                                    )
                                userData = location
                                addTapListener { mapObject, point ->
                                    viewModel.setShowings(
                                        mapObject.userData as ExploredLocation,
                                        point
                                    )
                                    if (!uiState.editMode) {
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
                            .reduce { result, it -> result + it }.map { it.latitude + it.longitude }
                    collection = mapView.mapWindow.map.mapObjects.addCollection()
                }



                if (uiState.realLocation != null) {
                    val imageProvider = ImageProvider.fromResource(activity, R.drawable.mmark)
                    collection.addPlacemark().apply {
                        geometry = Point(uiState.realLocation[0], uiState.realLocation[1])
                        setIcon(imageProvider)
                    }
                }

                if (uiState.editMode) {
                    val imageProvider = ImageProvider.fromResource(activity, R.drawable.pointer)
                    for (elem in uiState.showingLocation.points) {
                        collection.addPlacemark().apply {
                            geometry = Point(elem.latitude, elem.longitude)
                            setIcon(imageProvider)
                        }
                    }
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
                ),
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
                                    ContextCompat.getString(activity, R.string.permission_needed),
                                    Toast.LENGTH_LONG
                                ).show()
                                locationButtonRole = true
                            }
                        } else {
                            viewModel.setRealLocation()
                            locationButtonRole = true
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
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Rounded.FilterList,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    IconButton(onClick = {
                        viewModel.returnUiState()
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

    }
}