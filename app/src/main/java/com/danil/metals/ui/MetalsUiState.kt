package com.danil.metals.ui

import com.danil.metals.R
import com.danil.metals.data.ExploredLocation
import com.yandex.mapkit.geometry.Point

data class MetalsUiState(
    // main
    val isDark: Boolean = true,
    val location: List<Double> = listOf(55.754581, 37.625348),
    val realLocation: List<Double>? = null,
    val azimuth: Float = 0.0f,
    val zoom: Float = 5.0f,
    val tilt: Float = 0.0f,
    val languages: List<String> = listOf("en", "ru"),
    val loading: Boolean = false,
    val listenersImplemented: Boolean = false,
    val currentScreen: MetalsViewModel.Screens = MetalsViewModel.Screens.AccountScreen,
    val previousScreen: MetalsViewModel.Screens = MetalsViewModel.Screens.AccountScreen,

    // research
    val exploredLocations: List<ExploredLocation> = listOf(),

    val showingPoint: Point = Point(0.0, 0.0),
    val showingLocation: ExploredLocation = ExploredLocation(),

    // account
    val accountEmail: String? = null,
    val verified: Boolean? = null,
    val passwordError: Pair<Boolean, Int> = Pair(false, 0),
    val emailError: Pair<Boolean, Int> = Pair(false, 0),
    val warning: Pair<Int, Int>? = null,
    val warningAction: () -> Unit = {},

    // mapScreen
    val editMode: Boolean = false,
    val locationAdded: Boolean = false,
    val searchResult: List<Pair<String, Point>> = listOf(),
    val searchedPoint: Point? = null,

    //filters
    val zcRangeStart: String = "",
    val zcRangeEnd: String = "",
    val containmentRangeStart: String = "",
    val containmentRangeEnd: String = "",
    val coefficientRangeStart: String = "",
    val coefficientRangeEnd: String = "",
)

val representation = mapOf(
    Pair(1, R.string.low_pollution_degree),
    Pair(2, R.string.average_pollution_degree),
    Pair(3, R.string.high_pollution_degree),
    Pair(4, R.string.very_high_pollution_degree)
)
