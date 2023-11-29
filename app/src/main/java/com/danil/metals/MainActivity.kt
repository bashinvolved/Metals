package com.danil.metals

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danil.metals.data.apiKey
import com.danil.metals.databinding.MainLayoutBinding
import com.danil.metals.ui.MetalsApp
import com.danil.metals.ui.MetalsViewModel
import com.danil.metals.ui.theme.MetalsTheme
import com.yandex.mapkit.MapKitFactory


var apiApplied: Boolean = false
lateinit var viewModel: MetalsViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!apiApplied) {
            apiApplied = true
            MapKitFactory.setApiKey(apiKey)
            MapKitFactory.initialize(this)
        }
        binding = MainLayoutBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        setContent {
            viewModel = viewModel(factory = MetalsViewModel.Factory)
            val uiState by viewModel.uiState.collectAsState()
            viewModel.setTheme(isSystemInDarkTheme())

            MetalsTheme(
                uiState.isDark
            ) {
                MetalsApp(viewModel, uiState, this)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
        try {
            viewModel.listenMarkers()
            viewModel.listenVerification()
        } catch (_: UninitializedPropertyAccessException) { }
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        try {
            viewModel.markersListener.remove()
            viewModel.verificationListener.remove()
        } catch (_: UninitializedPropertyAccessException) { }
        super.onStop()
    }
}
