package com.mathematics.encoding.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mathematics.encoding.data.model.CodedSymbol
import com.mathematics.encoding.data.model.Symbol
import com.mathematics.encoding.data.repository.EncodingRepository
import com.mathematics.encoding.presentation.view.navigation.TabItems
import kotlinx.coroutines.delay


class EncodingViewModel(private val encodingRepository: EncodingRepository) : ViewModel() {

    var resultList = listOf<CodedSymbol>()
        private set

    var isLoading by mutableStateOf(true)

    internal var currentTabItem by mutableStateOf(TabItems.SymbolsProbabilities)

    var isAnimationRunning by mutableStateOf(false)


    suspend fun calculateCodesByFano(text: String, considerGap: Boolean) {
        isLoading = true
        resultList = generateCodesByFano(text, considerGap)
        delay(100)
        isLoading = false
    }

    suspend fun calculateCodesByFano(symbols: List<Symbol>) {
        isLoading = true
        resultList = generateCodesByFano(symbols)
        delay(100)
        isLoading = false
    }

    fun clearResult() {
        if (resultList.isNotEmpty()) resultList = emptyList()
    }




    private suspend fun generateCodesByFano(symbols: List<Symbol>) =
        encodingRepository.generateCodesByFano(symbols)


    private suspend fun generateCodesByFano(text: String, considerGap: Boolean) =
        encodingRepository.generateCodesByFano(text, considerGap)
}