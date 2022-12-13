package com.mathematics.encoding.presentation.viewmodel

import androidx.lifecycle.*
import com.mathematics.encoding.data.repository.EncodingRepository
import com.mathematics.encoding.presentation.model.Symbol
import com.mathematics.encoding.presentation.model.SymbolWithCode
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(FlowPreview::class)
class EncodingViewModel(private val encodingRepository: EncodingRepository) : ViewModel() {
    companion object {
        @Volatile
        private var INSTANCE: EncodingViewModel? = null

        fun getInstance(owner: ViewModelStoreOwner, factory: ViewModelFactory): EncodingViewModel {
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


    fun inputText(text: String) {
        inputtedText.value = text
    }




    suspend fun generateCodesByFano(symbols: List<Symbol>): List<SymbolWithCode> =
        encodingRepository.generateCodesByFano(symbols)


    suspend fun generateCodesByFano(text: String, considerGap: Boolean): List<SymbolWithCode> =
        encodingRepository.generateCodesByFano(text, considerGap)
}