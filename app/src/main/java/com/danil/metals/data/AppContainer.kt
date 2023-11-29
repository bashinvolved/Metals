package com.danil.metals.data

import android.content.Context

interface AppContainer {
    val metalsRepository: MetalsRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    override val metalsRepository: MetalsRepository by lazy {
        NetworkMetalsRepository(context)
    }
}
