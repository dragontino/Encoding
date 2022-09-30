package com.mathematics.encoding.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mathematics.encoding.data.repository.EncodingRepository

@Suppress("UNCHECKED_CAST")
class EncodingViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EncodingViewModel::class.java)) {
            return EncodingViewModel(EncodingRepository()) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}