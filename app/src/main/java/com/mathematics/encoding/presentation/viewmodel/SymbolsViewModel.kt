package com.mathematics.encoding.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.mathematics.encoding.presentation.model.ObservableSymbol

class SymbolsViewModel : ViewModel() {

    val symbolsList = SnapshotStateList<ObservableSymbol>()

    var showDialog by mutableStateOf(false)

    var currentSymbolPosition by mutableStateOf(-1)

    fun openDialog(symbolPosition: Int) {
        currentSymbolPosition = symbolPosition
        showDialog = true
    }
}