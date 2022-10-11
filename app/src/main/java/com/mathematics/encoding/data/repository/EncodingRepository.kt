package com.mathematics.encoding.data.repository

import android.util.Log
import com.mathematics.encoding.compare
import com.mathematics.encoding.countSigns
import com.mathematics.encoding.presentation.model.*
import com.mathematics.encoding.round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlin.math.abs

class EncodingRepository {

    private companion object {
        const val extraSymbols = ".,!?:;*()-—–\n"
    }

    suspend fun generateCodesByFano(text: String, considerGap: Boolean): List<SymbolWithCode> {
        val symbols = calculateProbabilities(text, considerGap)
        return if (symbols.size == 1)
            listOf(SymbolWithCode(symbols[0], "1"))
        else
            generateCodesByFano(symbols)
    }


    suspend fun generateCodesByFano(symbols: List<Symbol>): List<SymbolWithCode> {
        val hashMap = HashMap<Symbol, StringBuilder>()
        symbols.forEach {
            if (it.probability != 0.0)
                hashMap[it] = StringBuilder()
        }
        return createCodesByFano(hashMap).map { it.toSymbolWithCode() }
    }



    private fun calculateProbabilities(text: String, considerGap: Boolean): List<Symbol> {
        var result = text
        for (c in extraSymbols)
            result = result.replace(c.toString(), "")

        if (!considerGap) result = result.replace(" ", "")

        return result
            .map {
                when (it) {
                    'ё' -> 'е'
                    'ъ' -> 'ь'
                    ' ' -> '⎵'
                    else -> it
                }.uppercase()
            }
            .groupingBy { it }
            .eachCount()
            .map {
                Symbol(
                    name = it.key,
                    probability = (it.value.toDouble() / result.length).round(countSigns)
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

        val sortedList = symbolsMap.keys.sortedWith { s1, s2 ->
            Log.d("EncodingRepository", "s1 = $s1 \ns2 = $s2")
             if (s1.probability compare s2.probability != 0) {
                Log.d("EncodingRepository", "Проверка")
                s2.probability compare s1.probability
            } else
                s1.name.compareTo(s2.name)
        }

        Log.d("EncodingRepository", "отсортированный массив = $sortedList")

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

            for ((i, arr) in arrayOf(secondPart, firstPart).withIndex()) {
                arr.forEach { symbolsMap.getOrDefault(it, StringBuilder()).append(i) }

                result += withContext(Dispatchers.Default) {
                    createCodesByFano(arr.associateWith { symbolsMap[it] ?: StringBuilder() }
                            as HashMap<Symbol, StringBuilder>)
                }
            }

            return@supervisorScope result.sortedByDescending { it }
        }
    }
}