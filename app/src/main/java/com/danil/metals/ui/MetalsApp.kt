package com.danil.metals.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danil.metals.R
import com.danil.metals.ui.screens.AccountScreen
import com.danil.metals.ui.screens.EditScreen
import com.danil.metals.ui.screens.FilterScreen
import com.danil.metals.ui.screens.MapScreen
import com.danil.metals.ui.screens.ResearchScreen
import com.danil.metals.ui.screens.SettingsScreen

lateinit var globalNav: NavHostController

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MetalsApp(
    viewModel: MetalsViewModel,
    uiState: MetalsUiState,
    activity: AppCompatActivity,
    navController: NavHostController = rememberNavController()
) {
    globalNav = navController
    Column(
        Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Box(
            Modifier
                .height(LocalConfiguration.current.screenHeightDp.dp - dimensionResource(R.dimen.bottom_menu))
        ) {
            Row(
                Modifier.fillMaxSize()
            ) {
                NavHost(
                    navController = navController,
                    startDestination = MetalsViewModel.Screens.AccountScreen.name,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    composable(route = MetalsViewModel.Screens.MapScreen.name) {
                        MapScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            activity = activity,
                            navController = navController
                        )
                        viewModel.setCurrentScreen(MetalsViewModel.Screens.MapScreen)
                    }

                    composable(route = MetalsViewModel.Screens.AccountScreen.name) {
                        AccountScreen(
                            viewModel = viewModel,
                            uiState = uiState
                        )
                        viewModel.setCurrentScreen(MetalsViewModel.Screens.AccountScreen)
                    }

                    composable(route = MetalsViewModel.Screens.SettingsScreen.name) {
                        SettingsScreen(
                            viewModel = viewModel,
                            uiState = uiState
                        )
                        viewModel.setCurrentScreen(MetalsViewModel.Screens.SettingsScreen)
                    }

                    composable(route = MetalsViewModel.Screens.ResearchScreen.name) {
                        ResearchScreen(viewModel, uiState, navController, activity)
                        viewModel.setCurrentScreen(MetalsViewModel.Screens.ResearchScreen)
                    }

                    composable(route = MetalsViewModel.Screens.EditScreen.name) {
                        EditScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            navController = navController
                        )
                        viewModel.setCurrentScreen(MetalsViewModel.Screens.EditScreen)
                    }

                    composable(route = MetalsViewModel.Screens.FilterScreen.name) {
                        FilterScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            navController = navController
                        )
                        viewModel.setCurrentScreen(MetalsViewModel.Screens.FilterScreen)
                    }
                }

            }

            if (uiState.loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        stringResource(R.string.loading),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        Row(
            Modifier
                .height(dimensionResource(R.dimen.bottom_menu))
                .fillMaxWidth()
                .shadow(elevation = 20.dp)
                .background(color = MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                modifier = Modifier
                    .background(
                        color = if (uiState.currentScreen == MetalsViewModel.Screens.SettingsScreen)
                            MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.background,
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .size(dimensionResource(id = R.dimen.selected_menu_item)),
                onClick = {
                    if (uiState.currentScreen != MetalsViewModel.Screens.SettingsScreen)
                        navController.navigate(MetalsViewModel.Screens.SettingsScreen.name)
                }
            ) {
                Icon(
                    Icons.Rounded.Settings,
                    contentDescription = null,
                    tint = if (uiState.currentScreen == MetalsViewModel.Screens.SettingsScreen)
                        MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(
                modifier = Modifier
                    .background(
                        color = if (uiState.currentScreen == MetalsViewModel.Screens.MapScreen)
                            MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.background,
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .size(dimensionResource(id = R.dimen.selected_menu_item)),
                onClick = {
                    if (uiState.currentScreen != MetalsViewModel.Screens.MapScreen)
                        navController.navigate(MetalsViewModel.Screens.MapScreen.name)
                }
            ) {
                Icon(
                    Icons.Rounded.Map,
                    contentDescription = null,
                    tint = if (uiState.currentScreen == MetalsViewModel.Screens.MapScreen)
                        MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(modifier = Modifier
                .background(
                    color = if (uiState.currentScreen == MetalsViewModel.Screens.AccountScreen)
                        MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.background,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .size(dimensionResource(id = R.dimen.selected_menu_item)),
                onClick = {
                    if (uiState.currentScreen != MetalsViewModel.Screens.AccountScreen)
                        navController.navigate(MetalsViewModel.Screens.AccountScreen.name)
                }
            ) {
                Icon(
                    Icons.Rounded.AccountBox,
                    contentDescription = null,
                    tint = if (uiState.currentScreen == MetalsViewModel.Screens.AccountScreen)
                        MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onBackground
                )
            }
        }
        if (uiState.warning != null) {
            AlertDialog(
                onDismissRequest = { viewModel.showWarning(null) },
                confirmButton = {
                    Button(
                        onClick = uiState.warningAction,
                        colors = buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text(
                            stringResource(uiState.warning.second),
                            color = MaterialTheme.colorScheme.onTertiary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                text = {
                    Text(
                        stringResource(uiState.warning.first),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                containerColor = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding) / 2)
            )
        }

    }

}