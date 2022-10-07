package com.mathematics.encoding.presentation.viewmodel

import androidx.lifecycle.*
import com.mathematics.encoding.data.repository.EncodingRepository
import com.mathematics.encoding.presentation.model.Symbol
import com.mathematics.encoding.presentation.model.SymbolWithCode

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


    fun addSymbol(symbol: Symbol) {
        val array = symbols.value ?: emptyArray()
        symbolListLiveData.value = array + symbol
    }


    fun addSymbols(count: Int) {
        val oldArray = symbols.value ?: emptyArray()
        symbolListLiveData.value = oldArray + Array(count) { Symbol() }
    }


    fun clearSymbols(defaultSize: Int) {
        val size = symbols.value?.size ?: defaultSize
        symbolListLiveData.value = Array(size) { Symbol() }
    }


    fun deleteSymbolsFromLast(count: Int = 1) {
        val array = symbols.value ?: emptyArray()
        symbolListLiveData.value = array.sliceArray(0..array.lastIndex - count)
    }




    suspend fun generateCodesByFano(symbols: List<Symbol>): List<SymbolWithCode> =
        encodingRepository.generateCodesByFano(symbols)


    suspend fun generateCodesByFano(text: String, considerGap: Boolean): List<SymbolWithCode> =
        encodingRepository.generateCodesByFano(text, considerGap)
}