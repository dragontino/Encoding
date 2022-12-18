package com.mathematics.encoding.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mathematics.encoding.R

class TextInputViewModel : ViewModel() {
    var isKeyboardShowing by mutableStateOf(false)

    var text by mutableStateOf("")

    var isError by mutableStateOf(false)

    var errorMessage by mutableStateOf("")

    var showErrorMessage by mutableStateOf(true)

    var considerGap by mutableStateOf(false)

    // TODO 14.12.2022: заменить
    var updateConsiderGap: (Boolean) -> Unit by mutableStateOf({})


    fun checkInputText(context: Context) = when {
        text.isEmpty() || (text.isBlank() && !considerGap) -> {
            errorMessage = context.getString(R.string.need_text)
            false
        }
        !text.all { it.isDigit() || it.isLetter() || it in "!.,?!-–—\n " } -> {
            errorMessage = context.getString(R.string.remove_incorrect_symbols)
            false
        }
        !text.any {
            it.isDigit() || it.isLetter() || (considerGap && it == ' ')
        } -> {
            errorMessage = context.getString(R.string.get_correct_symbols)
            false
        }
        else -> true
    }.also { isError = !it }
}