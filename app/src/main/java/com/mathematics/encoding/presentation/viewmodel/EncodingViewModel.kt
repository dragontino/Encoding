package com.mathematics.encoding.presentation.viewmodel

import androidx.lifecycle.*
import com.mathematics.encoding.data.repository.EncodingRepository
import com.mathematics.encoding.presentation.model.Symbol
import com.mathematics.encoding.presentation.model.SymbolWithCode
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@FlowPreview
class EncodingViewModel(private val encodingRepository: EncodingRepository) : ViewModel() {
    companion object {
        @Volatile
        private var INSTANCE: EncodingViewModel? = null

        fun getInstance(owner: ViewModelStoreOwner, factory: EncodingViewModelFactory): EncodingViewModel {
            val temp = INSTANCE
            if (temp != null)
                return temp

            synchronized(this) {
                val instance = ViewModelProvider(owner, factory)[EncodingViewModel::class.java]
                INSTANCE = instance

                return instance
            }
        }
    }

    private val symbolListLiveData: MutableLiveData<Array<Symbol>> = MutableLiveData(emptyArray())
    val symbols: LiveData<Array<Symbol>> = symbolListLiveData

    val inputtedText: MutableLiveData<String> = MutableLiveData("")

    private val debounceOnClick: MutableStateFlow<(() -> Unit)?> = MutableStateFlow(null)


    init {
        viewModelScope.launch {
            debounceOnClick
                .debounce(50)
                .collect {
                    it?.invoke()
                }
        }
    }

    fun onItemClick(onClick: () -> Unit) {
        debounceOnClick.value = onClick
    }


    fun addSymbol(symbol: Symbol) {
        val array = symbols.value ?: emptyArray()
        symbolListLiveData.value = array + symbol
    }


    fun addSymbols(count: Int) {
        val oldArray = symbols.value ?: emptyArray()
        symbolListLiveData.value = oldArray + Array(count) { Symbol() }
    }


    fun clearSymbol(index: Int) {
        symbolListLiveData.value = symbols.value?.apply { this[index].clear(index) }
    }


    fun deleteSymbol(index: Int) {
        val array = symbols.value ?: emptyArray()
        if (index < 0 || index >= array.size) return

        symbolListLiveData.value =
            array.sliceArray(0 until index) +
                    array.sliceArray(index + 1 until array.size)
    }


    fun inputText(text: String) {
        inputtedText.value = text
    }




    suspend fun generateCodesByFano(symbols: List<Symbol>): List<SymbolWithCode> =
        encodingRepository.generateCodesByFano(symbols)


    suspend fun generateCodesByFano(text: String, considerGap: Boolean): List<SymbolWithCode> =
        encodingRepository.generateCodesByFano(text, considerGap)
}