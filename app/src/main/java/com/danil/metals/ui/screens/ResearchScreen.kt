package com.danil.metals.ui.screens

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.danil.metals.R
import com.danil.metals.ui.MetalsUiState
import com.danil.metals.ui.MetalsViewModel
import com.danil.metals.ui.representation
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView

private lateinit var collection: MapObjectCollection

@Composable
fun ResearchScreen(
    viewModel: MetalsViewModel,
    uiState: MetalsUiState,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(
                start = dimensionResource(R.dimen.padding),
                end = dimensionResource(id = R.dimen.padding)
            )
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding) * 2))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Rounded.ArrowBack, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                uiState.showingLocation.name, style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding)))

        AndroidView(
            factory = {
                View.inflate(it, R.layout.main_layout, null)
            },
            update = { view ->
                val mapView = view.findViewById<MapView>(R.id.mapview)
                mapView.mapWindow.map
                    .move(
                        CameraPosition(
                            uiState.showingPoint,
                            uiState.zoom,
                            0f,
                            0f,
                        )
                    )

                try {
                    collection.clear()
                } catch (e: Exception) {
                    collection = mapView.mapWindow.map.mapObjects.addCollection()
                }

                collection.addPolygon(Polygon(LinearRing(uiState.showingLocation.points), listOf()))
                    .apply {
                        strokeWidth = 0f
                        fillColor = ContextCompat.getColor(
                            viewModel.metalsRepository.context(),
                            viewModel.selectColor(uiState.showingLocation)
                        )
                    }

            },
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .height(dimensionResource(R.dimen.minimap))
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding)))

        Text(
            stringResource(R.string.data), style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding)))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(dimensionResource(id = R.dimen.padding) / 2)
        ) {

            Text(
                text = stringResource(R.string.zc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding) / 2))

            Text(
                viewModel.calculateZc(uiState.showingLocation).toInt().toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )


            Spacer(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding) / 2)
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.line_height))
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraSmall
                    )
            )

            Text(
                text = stringResource(R.string.level),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding) / 2))

            Text(
                stringResource(viewModel.selectLevel(uiState.showingLocation)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Spacer(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding) / 2)
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.line_height))
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraSmall
                    )
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding)))

        var metals by rememberSaveable { mutableStateOf(true) }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.metals), style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            IconButton(onClick = { metals = !metals }) {
                Icon(
                    if (metals) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (metals) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding)))

            for (elem in uiState.showingLocation.elements.entries) {
                Text(
                    text = elem.key,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding) / 2))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(dimensionResource(id = R.dimen.padding) / 2)
                ) {
                    Text(
                        text = stringResource(R.string.containment),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding) / 2))

                    Text(
                        elem.value[0].toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )


                    Spacer(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding) / 2)
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.line_height))
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                    )

                    Text(
                        text = stringResource(R.string.coefficient),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding) / 2))

                    Text(
                        elem.value[1].toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Spacer(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding) / 2)
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.line_height))
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                    )

                    Text(
                        text = stringResource(R.string.degree_of_pollution),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding) / 2))

                    Text(
                        stringResource(representation[elem.value[2].toInt()]!!),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Spacer(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding) / 2)
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.line_height))
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                    )

                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding) / 2))

            }
        }
        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        var descriptions by rememberSaveable { mutableStateOf(true) }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.addition), style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            IconButton(onClick = { descriptions = !descriptions }) {
                Icon(
                    if (descriptions) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (descriptions) {
            Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

            for (elem in uiState.showingLocation.descriptions) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(dimensionResource(id = R.dimen.padding) / 2)
                ) {
                    Text(
                        text = elem,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Justify
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding) / 2))
            }
        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))
    }

}