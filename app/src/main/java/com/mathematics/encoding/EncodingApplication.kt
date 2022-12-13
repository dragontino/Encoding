package com.mathematics.encoding

import android.app.Application
import com.mathematics.encoding.presentation.viewmodel.ViewModelFactory

class EncodingApplication: Application() {
    val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(this)
    }
}