package com.mathematics.encoding.data.repository

import com.mathematics.encoding.data.support.countSigns
import com.mathematics.encoding.data.support.round
import com.mathematics.encoding.presentation.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlin.math.abs

class EncodingRepository {

    suspend fun generateCodesByFano(text: String, considerGap: Boolean): List<SymbolWithCode> {
        val symbols = calculateProbabilities(text, considerGap)
        return when {
            symbols.isEmpty() -> emptyList()
            symbols.size == 1 -> listOf(SymbolWithCode(symbols[0], "1"))
            else -> generateCodesByFano(symbols)
        }
    }


    suspend fun generateCodesByFano(symbols: List<Symbol>): List<SymbolWithCode> {
        val hashMap = HashMap<Symbol, StringBuilder>()
        symbols.forEach { symbol ->
            symbol.name = symbol.name.uppercase()
            if (symbol.probability != 0.0)
                hashMap[symbol] = StringBuilder()
        }
        return createCodesByFano(hashMap).map { it.build() }
    }



    private fun calculateProbabilities(text: String, considerGap: Boolean): List<Symbol> {
        val filteredText = text.filter {
            it.isDigit() || it.isLetter() || (considerGap && it == ' ')
        }

        return filteredText
            .ifEmpty { return emptyList() }
            .map {
                when (it) {
                    ' ' -> '⎵'
                    else -> it
                }.uppercase()
            }
            .groupingBy { it }
            .eachCount()
            .map {
                Symbol(
                    name = it.key,
                    probability = (it.value.toDouble() / filteredText.length).round(countSigns)
                )
            }
    }



    private suspend fun createCodesByFano(symbolsMap: HashMap<Symbol, StringBuilder>): List<SymbolWithCodeBuilder> {
        val sumProbabilities = symbolsMap.keys.sumProbabilities

        fun f(x: Double): Double =
            abs(2 * x - sumProbabilities)


        if (symbolsMap.size == 1) {
            return symbolsMap.toSymbolBuilderList()
        }

        val sortedList = symbolsMap.keys.sorted()

        if (symbolsMap.size == 2) {
            sortedList.asReversed().forEachIndexed { index, symbol ->
                symbolsMap.getOrDefault(symbol, StringBuilder()).append(index)
            }
            return symbolsMap.toSymbolBuilderList().sortedByDescending { it }
        }

        return supervisorScope {
            var sum = 0.0
            var idx = 0

            while (idx < sortedList.size / 2) {
                val currentProbability = sortedList[idx].probability
                if (f(sum + currentProbability) < f(sum))
                    sum += currentProbability
                else
                    break

                idx++
            }

            if (sortedList.size % 4 != 0 && sortedList.size % 2 == 0 && idx == sortedList.size / 2) {
                idx--
            }

            val firstPart = sortedList.subList(0, idx)
//            Log.d("EncodingRepository", "firstList item = ${firstPart[0]}")
            val secondPart = sortedList.subList(idx, sortedList.size)
            val result = ArrayList<SymbolWithCodeBuilder>()



            for ((i, arr) in arrayOf(secondPart, firstPart).withIndex().reversed()) {
                arr.forEach { symbolsMap.getOrDefault(it, StringBuilder()).append(i) }

                result += withContext(Dispatchers.Default) {
                    createCodesByFano(arr.associateWith { symbolsMap[it] ?: StringBuilder() }
                            as HashMap<Symbol, StringBuilder>)
                }
            }

//            result.sortDescending()
            return@supervisorScope result
        }
    }
}