package com.mathematics.encoding.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.mathematics.encoding.data.repository.EncodingRepository
import com.mathematics.encoding.presentation.model.Symbol
import com.mathematics.encoding.presentation.model.SymbolWithCode
import com.mathematics.encoding.presentation.view.navigation.TabItems


class EncodingViewModel(private val encodingRepository: EncodingRepository) : ViewModel() {

    var resultList = listOf<SymbolWithCode>()
        private set

    var isLoading by mutableStateOf(true)

    internal var currentTabItem by mutableStateOf(TabItems.SymbolsProbabilities)

    var isAnimationRunning by mutableStateOf(false)


    suspend fun calculateCodesByFano(text: String, considerGap: Boolean) {
        resultList = generateCodesByFano(text, considerGap)
    }

    suspend fun calculateCodesByFano(symbols: List<Symbol>) {
        resultList = generateCodesByFano(symbols)
    }

    fun clearResult() {
        resultList = emptyList()
    }




    private suspend fun generateCodesByFano(symbols: List<Symbol>): List<SymbolWithCode> =
        encodingRepository.generateCodesByFano(symbols)


    private suspend fun generateCodesByFano(text: String, considerGap: Boolean): List<SymbolWithCode> =
        encodingRepository.generateCodesByFano(text, considerGap)
}