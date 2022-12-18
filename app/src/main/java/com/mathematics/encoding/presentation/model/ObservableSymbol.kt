package com.mathematics.encoding.presentation.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mathematics.encoding.data.model.Symbol

class ObservableSymbol(
    name: String = "",
    probabilityString: String = "",
    nameErrorMessage: String = "",
    probabilityErrorMessage: String = "",
) {
    var name by mutableStateOf(name)
    var probabilityString by mutableStateOf(probabilityString)

    var nameErrorMessage by mutableStateOf(nameErrorMessage)
    var probabilityErrorMessage by mutableStateOf(probabilityErrorMessage)


    val nullableProbability: Double? get() = probabilityString
        .toDoubleOrNull()
        ?.takeIf { it in 0.0..1.0 }

    val probability: Double get() = nullableProbability ?: 0.0

    val hasNameError get() = nameErrorMessage.isNotBlank()
    val hasProbabilityError get() = probabilityErrorMessage.isNotBlank()

    val hasError get() = hasNameError || hasProbabilityError



    fun clear() {
        name = ""
        probabilityString = ""
        nameErrorMessage = emptySymbolNameMessage
        probabilityErrorMessage = incorrectSymbolProbabilityMessage
    }


    fun toSymbol() = Symbol(name, probability)
}



const val emptySymbolNameMessage = "Пустой символ"

const val incorrectSymbolProbabilityMessage = "Некорректная вероятность"
