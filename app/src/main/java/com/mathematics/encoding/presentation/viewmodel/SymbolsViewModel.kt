package com.mathematics.encoding.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.mathematics.encoding.R
import com.mathematics.encoding.data.support.showToast
import com.mathematics.encoding.presentation.model.ObservableSymbol
import com.mathematics.encoding.presentation.model.emptySymbolNameMessage
import com.mathematics.encoding.presentation.model.incorrectSymbolProbabilityMessage
import kotlin.math.abs

class SymbolsViewModel : ViewModel() {

    val symbolsList = SnapshotStateList<ObservableSymbol>()

    var showDialog by mutableStateOf(false)

    var currentSymbolPosition by mutableStateOf(-1)

    fun openDialog(symbolPosition: Int) {
        currentSymbolPosition = symbolPosition
        showDialog = true
    }


    fun checkSymbolsInput(context: Context): Boolean {
        symbolsList.mapIndexed { index, symbol ->
            symbol.nameErrorMessage = when {
                symbol.name.isBlank() -> emptySymbolNameMessage

                symbolsList
                    .slice(0 until index)
                    .find { it.name == symbol.name } != null ->
                    context.getString(R.string.symbol_is_existing, symbol.name)

                else -> ""
            }

            symbol.probabilityErrorMessage = if (symbol.nullableProbability == null) {
                incorrectSymbolProbabilityMessage
            } else ""
        }

        return when {
            symbolsList.find { it.hasError } != null -> {
                showToast(context, context.getString(R.string.need_good_data))
                false
            }
            abs(symbolsList.sumOf { it.probability } - 1) > 0.005 -> {
                showToast(context, context.getString(R.string.sum_probabilities_needs_to_equals_1))
                false
            }
            else -> true
        }
    }
}