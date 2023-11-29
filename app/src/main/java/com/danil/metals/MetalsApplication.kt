package com.danil.metals

import android.app.Application
import com.danil.metals.data.AppContainer
import com.danil.metals.data.DefaultAppContainer

class MetalsApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}