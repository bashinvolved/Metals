package com.danil.metals.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.text.intl.Locale
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.danil.metals.MetalsApplication
import com.danil.metals.R
import com.danil.metals.data.ExploredLocation
import com.danil.metals.data.MetalsRepository
import com.danil.metals.ui.screens.lastKnownPolyPoints
import com.google.firebase.firestore.ListenerRegistration
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.VisibleRegion
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.Session.SearchListener
import com.yandex.runtime.Error
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

class MetalsViewModel(val metalsRepository: MetalsRepository) : ViewModel() {
    val uiState = MutableStateFlow(MetalsUiState())
    var exploredLocationsBeforeEditing = listOf<ExploredLocation>()

    init {
        if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            setLanguage(Locale.current.language)
        }
        setLanguages()

        if (metalsRepository.auth().currentUser != null) {
            uiState.update {
                it.copy(accountEmail = metalsRepository.auth().currentUser!!.email)
            }
            listenVerification()
        }
        listenMarkers()
    }

    lateinit var markersListener: ListenerRegistration
    fun listenMarkers() {
        markersListener =
            metalsRepository.requestToGetCollRef("Markers")
                .addSnapshotListener { snapshot, error ->
                    if (snapshot != null && !uiState.value.editMode) {
                        val exploredLocations: MutableList<ExploredLocation> = mutableListOf()
                        for (document in snapshot) {
                            exploredLocations.add(
                                ExploredLocation(
                                    id = document.id,
                                    name = document.getString("name")!!,
                                    descriptions = document.get("descriptions") as List<String>,
                                    elements = document.get("elements") as Map<String, List<Double>>,
                                    points = (document.get("latitudes") as List<Double>).zip(
                                        document.get("longitudes")
                                                as List<Double>
                                    ).map { pair -> Point(pair.first, pair.second) }
                                )
                            )
                        }
                        uiState.update {
                            it.copy(exploredLocations = exploredLocations.toList())
                        }
                    }
                }
    }

    lateinit var verificationListener: ListenerRegistration
    fun listenVerification() {
        if (uiState.value.accountEmail != null && uiState.value.verified == null) {
            verificationListener =
                metalsRepository.requestToGetDocRef("Users", uiState.value.accountEmail!!)
                    .addSnapshotListener { snapshot, error ->
                        if (snapshot != null && snapshot.exists()) {
                            uiState.update {
                                it.copy(verified = snapshot.getBoolean("verified"))
                            }
                        }
                    }
        }
    }

    enum class Screens {
        MapScreen,
        AccountScreen,
        SettingsScreen,
        ResearchScreen,
        EditScreen,
        FilterScreen
    }

    fun setLocation(latitude: Double, longitude: Double, zoom: Float, azimuth: Float, tilt: Float) {
        if (abs(uiState.value.location[0] - latitude) < 0.00001 && abs(uiState.value.location[1] - longitude) < 0.00001) {
            return
        }
        uiState.update {
            it.copy(
                location = listOf(latitude, longitude),
                azimuth = azimuth,
                tilt = tilt,
                zoom = zoom
            )
        }
    }

    fun setRealLocation(latitude: Double = 360.0, longitude: Double = 360.0) {
        if (latitude == 360.0) {
            uiState.update { it.copy(realLocation = null) }
            return
        }
        uiState.update {
            it.copy(realLocation = listOf(latitude, longitude))
        }
        setLocation(latitude, longitude, 12f, uiState.value.azimuth, uiState.value.tilt)
    }

    fun setShowings(
        location: ExploredLocation = ExploredLocation(),
        point: Point = Point(0.0, 0.0)
    ) {
        uiState.update {
            it.copy(
                showingLocation = location,
                showingPoint = point
            )
        }
    }

    fun setSearchedPoint(point: Point?) {
        uiState.update { it.copy(searchedPoint = point) }
    }

    fun setMode(mode: Boolean) {
        uiState.update {
            it.copy(editMode = mode)
        }
    }

    fun setLocationButtonRole(role: Boolean) {
        uiState.update { it.copy(locationButtonRole = role) }
    }

    fun setLocationAdded() {
        uiState.update {
            it.copy(locationAdded = true)
        }
    }

    fun returnUiState() {
        uiState.update {
            it.copy(exploredLocations = exploredLocationsBeforeEditing)
        }
    }

//    fun setNetworkAvailableness(isAvailable: Boolean) {
//        uiState.update {
//            it.copy(networkAvailable = isAvailable)
//        }
//    }

    private fun setLanguages() {
        val languages = mutableListOf(AppCompatDelegate.getApplicationLocales()[0]!!.language)
        for (elem in uiState.value.languages) {
            if (elem != languages.first())
                languages.add(elem)
        }
        uiState.update { it.copy(languages = languages) }
    }

    fun setLanguage(code: String) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(code)
        )
        setLanguages()
    }

    private var doOnce = true
    fun setTheme(isDark: Boolean) {
        if (doOnce) {
            doOnce = false
            uiState.update {
                it.copy(isDark = isDark)
            }
        }
    }

    fun changeTheme() {
        uiState.update { it.copy(isDark = uiState.value.isDark.not()) }
    }

    fun setListenersImplemented(value: Boolean = true) {
        uiState.update {
            it.copy(listenersImplemented = value)
        }
    }

    fun setFilter(property: Int, type: Boolean, value: String) {
        when (property) {
            1 -> if (type) uiState.update { it.copy(zcRangeStart = value) } else
                uiState.update { it.copy(zcRangeEnd = value) }
            2 -> if (type) uiState.update { it.copy(containmentRangeStart = value) } else
                uiState.update { it.copy(containmentRangeEnd = value) }
            else -> if (type) uiState.update { it.copy(coefficientRangeStart = value) } else
                uiState.update { it.copy(coefficientRangeEnd = value) }
        }
    }

    fun setCurrentScreen(screen: Screens) {
        uiState.update { it.copy(currentScreen = screen, previousScreen = uiState.value.currentScreen) }
    }

    fun deferredRecomposition() {
        viewModelScope.launch {
            delay(500)
            lastKnownPolyPoints = listOf()
        }
    }

    fun setLastExploredPoint(exploredLocation: ExploredLocation, point: Point) {
        val index = uiState.value.exploredLocations.indexOf(exploredLocation)
        val updated = ExploredLocation(
            id = exploredLocation.id,
            name = exploredLocation.name,
            elements = exploredLocation.elements,
            descriptions = exploredLocation.descriptions,
            points = exploredLocation.points + listOf(point)
        )
        uiState.update {
            it.copy(
                exploredLocations = uiState.value.exploredLocations.slice(0 until index) + listOf(
                    updated
                ) + if (index == uiState.value.exploredLocations.lastIndex) listOf() else
                    uiState.value.exploredLocations.slice(index + 1..uiState.value.exploredLocations.lastIndex),
                showingLocation = updated
            )
        }
    }

    fun deleteLastExploredPoint() {
        val index = uiState.value.exploredLocations.indexOf(uiState.value.showingLocation)
        val updated = ExploredLocation(
            id = uiState.value.showingLocation.id,
            name = uiState.value.showingLocation.name,
            elements = uiState.value.showingLocation.elements,
            points = uiState.value.showingLocation.points.slice(0 until uiState.value.showingLocation.points.lastIndex),
            descriptions = uiState.value.showingLocation.descriptions
        )
        uiState.update {
            it.copy(
                exploredLocations = uiState.value.exploredLocations.slice(0 until index)
                        + listOf(updated) + if (uiState.value.exploredLocations.lastIndex == index) listOf() else
                    uiState.value.exploredLocations.slice(index + 1..uiState.value.exploredLocations.lastIndex),
                showingLocation = updated
            )
        }
    }

    fun addExploredLocation(point: Point) {
        uiState.update {
            it.copy(
                exploredLocations = uiState.value.exploredLocations +
                        listOf(ExploredLocation(points = listOf(point))), locationAdded = false
            )
        }
    }

    fun updateExploredLocation(
        name: String,
        elements: Map<String, List<Double>>,
        descriptions: List<String>
    ) {
        if (name == uiState.value.showingLocation.name)
            if (elements == uiState.value.showingLocation.elements)
                if (descriptions == uiState.value.showingLocation.descriptions)
                    return
        val index = uiState.value.exploredLocations.indexOf(uiState.value.showingLocation)
        val updated = ExploredLocation(
            id = uiState.value.exploredLocations[index].id,
            name = name,
            elements = elements,
            descriptions = descriptions,
            points = uiState.value.exploredLocations[index].points
        )
        uiState.update {
            it.copy(
                exploredLocations = uiState.value.exploredLocations.slice(0 until index) +
                        listOf(updated) + if (index == uiState.value.exploredLocations.lastIndex) listOf() else
                    uiState.value.exploredLocations.slice(index + 1..uiState.value.exploredLocations.lastIndex),
                showingLocation = updated
            )
        }
    }

    fun pushExplored() {
        for (elem in uiState.value.exploredLocations) {
            if (elem !in exploredLocationsBeforeEditing) {
                val data = hashMapOf(
                    "name" to elem.name,
                    "descriptions" to elem.descriptions,
                    "elements" to elem.elements,
                    "latitudes" to elem.points.map { it.latitude },
                    "longitudes" to elem.points.map { it.longitude }
                )
                if (elem.id.isNotEmpty()) {
                    if (elem.points.count() < 3) {
                        viewModelScope.launch {
                            metalsRepository.requestToDeleteDocument("Markers", elem.id)
                        }
                        continue
                    }
                    viewModelScope.launch {
                        metalsRepository.requestToCreateDocument("Markers", elem.id, data)
                    }
                } else {
                    if (elem.points.count() < 3)
                        continue
                    viewModelScope.launch {
                        metalsRepository.requestToCreateDocument("Markers", null, data)
                    }
                }
            }
        }
    }

    fun calculateZc(location: ExploredLocation): Double {
        return location.elements.values.sumOf { it[0] / it[1] } - location.elements.size + 1
    }

    fun selectColor(location: ExploredLocation): Int {
        return when (calculateZc(location)) {
            in 0.0..16.0 -> R.color.acceptable_place
            in 16.0..32.0 -> R.color.moderately_dangerous_place
            in 32.0..128.0 -> R.color.dangerous_place
            else -> R.color.very_dangerous_place
        }
    }

    fun selectLevel(location: ExploredLocation): Int {
        return when (selectColor(location)) {
            R.color.acceptable_place -> R.string.level_1
            R.color.moderately_dangerous_place -> R.string.level_2
            R.color.dangerous_place -> R.string.level_3
            else -> R.string.level_4
        }
    }

    fun resetErrors() {
        uiState.update {
            it.copy(emailError = Pair(false, 0), passwordError = Pair(false, 0))
        }
    }

    fun showWarning(warning: Pair<Int, Int>?, warningAction: () -> Unit = {}) {
        uiState.update {
            it.copy(warning = warning, warningAction = warningAction)
        }
    }

    private val searchSessionListener = object : SearchListener {
        override fun onSearchResponse(response: Response) {
            val searchResult: MutableList<Pair<String, Point?>> = mutableListOf()
            for (elem in response.collection.children.iterator()) {
                searchResult.add(Pair((elem.obj?.name ?: "") + '%' + (elem.obj?.descriptionText ?: ""), elem.obj?.geometry?.get(0)?.point))
            }
            uiState.update { it.copy(searchResult = searchResult.filter
                { entry -> entry.second != null } as List<Pair<String, Point>>)
            }
        }

        override fun onSearchError(error: Error) {
        }
    }
    private val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private var searchSession: Session? = null
    @RequiresApi(Build.VERSION_CODES.P)
    fun trySearch(text: String, region: VisibleRegion) {
        if (text.isEmpty()) {
            uiState.update { it.copy(searchResult = listOf()) }
            return
        }
        val polygon = VisibleRegionUtils.toPolygon(region)
        searchSession?.cancel()
        searchSession = searchManager.submit(
            text,
            polygon,
            SearchOptions().apply {
                resultPageSize = 8
            },
            searchSessionListener
        )
    }

    fun trySignIn(email: String, password: String) {
        if (email.isBlank()) {
            uiState.update {
                it.copy(emailError = Pair(true, R.string.empty_email))
            }
            return
        }
        if (password.isBlank()) {
            uiState.update {
                it.copy(passwordError = Pair(true, R.string.empty_password))
            }
            return
        }
        uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            metalsRepository.requestToSignIn(email, password)
                .addOnSuccessListener {
                    uiState.update {
                        it.copy(accountEmail = email)
                    }
                    listenVerification()
                }
                .addOnFailureListener {
                    uiState.update { state ->
                        when (it.message) {
                            metalsRepository.context()
                                .getString(R.string.email_badly_formatted) -> {
                                state.copy(emailError = Pair(true, R.string.email_badly_formatted))
                            }

                            metalsRepository.context().getString(R.string.wrong_password) -> {
                                state.copy(passwordError = Pair(true, R.string.wrong_password))
                            }

                            metalsRepository.context().getString(R.string.invalid_password) -> {
                                state.copy(passwordError = Pair(true, R.string.invalid_password))
                            }

                            metalsRepository.context()
                                .getString(R.string.email_is_not_registered) -> {
                                state.copy(
                                    emailError = Pair(
                                        true,
                                        R.string.email_is_not_registered
                                    )
                                )
                            }

                            metalsRepository.context()
                                .getString(R.string.malformed_credentials) -> {
                                state.copy(
                                    emailError = Pair(true, R.string.malformed_credentials),
                                    passwordError = Pair(true, R.string.malformed_credentials)
                                )
                            }

                            else -> {
                                state.copy(
                                    emailError = Pair(true, R.string.uncathed_exception),
                                    passwordError = Pair(true, R.string.uncathed_exception)
                                )
                            }
                        }
                    }

                }
                .addOnCompleteListener {
                    uiState.update { it.copy(loading = false) }
                }
        }
    }

    fun trySignUp(email: String, password: String) {
        if (email.isBlank()) {
            uiState.update {
                it.copy(emailError = Pair(true, R.string.empty_email))
            }
            return
        }
        if (password.isBlank()) {
            uiState.update {
                it.copy(passwordError = Pair(true, R.string.empty_password))
            }
            return
        }
        uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            metalsRepository.requestToCreateDocument("Users", email, hashMapOf("verified" to false))
                .addOnSuccessListener {
                    metalsRepository.requestToSignUp(email, password)
                        .addOnCompleteListener {
                            trySignIn(email, password)
                        }
                }
                .addOnFailureListener {
                    uiState.update { it.copy(loading = false) }
                }
        }
    }

    fun trySignOut() {
        verificationListener.remove()
        metalsRepository.auth().signOut()
        uiState.update {
            it.copy(accountEmail = null, verified = null)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MetalsApplication)
                val metalsRepository = application.container.metalsRepository
                MetalsViewModel(metalsRepository = metalsRepository)
            }
        }
    }
}