package com.mathematics.encoding

import android.app.Application
import com.mathematics.encoding.presentation.viewmodel.EncodingViewModelFactory
import com.mathematics.encoding.presentation.viewmodel.SettingsViewModelFactory

class EncodingApplication: Application() {
    val settingsViewModelFactory: SettingsViewModelFactory by lazy {
        SettingsViewModelFactory(this)
    }

    val encodingViewModelFactory: EncodingViewModelFactory by lazy {
        EncodingViewModelFactory(this)
    }
}