package com.mathematics.encoding.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TextInputViewModel : ViewModel() {
    var isKeyboardShowing by mutableStateOf(false)

    var text by mutableStateOf("")

    var isError by mutableStateOf(false)

    var considerGap by mutableStateOf(false)

    // TODO 14.12.2022: заменить
    var updateConsiderGap: (Boolean) -> Unit by mutableStateOf({})
}