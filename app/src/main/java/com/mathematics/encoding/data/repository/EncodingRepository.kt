package com.mathematics.encoding.data.repository

import com.mathematics.encoding.data.model.*
import com.mathematics.encoding.data.model.sumProbabilities
import com.mathematics.encoding.data.support.countSigns
import com.mathematics.encoding.data.support.round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlin.math.abs

class EncodingRepository {

    suspend fun generateCodesByFano(text: String, considerGap: Boolean): List<CodedSymbol> {
        val symbols = calculateProbabilities(text, considerGap)
        return when {
            symbols.isEmpty() -> emptyList()
            symbols.size == 1 -> listOf(symbols[0].attachCode("1"))
            else -> generateCodesByFano(symbols)
        }
    }


    suspend fun generateCodesByFano(symbols: List<Symbol>): List<CodedSymbol> {
        return createCodesByFano(
            codedSymbols = symbols
                .filter { it.probability != 0.0 }
                .map {
                    CodedSymbol(
                        name = it.name.uppercase(),
                        probability = it.probability
                    )
                }.toSet()
        )
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



    private suspend fun createCodesByFano(codedSymbols: Set<CodedSymbol>): List<CodedSymbol> {
        val sumProbabilities = codedSymbols.sumProbabilities

        fun f(x: Double): Double =
            abs(2 * x - sumProbabilities)


        val sortedList = codedSymbols.sortedByDescending { it.detachCode() }

        return when (sortedList.size) {
            1 -> codedSymbols.toList()
            2 -> {
                sortedList.forEachIndexed { index, codedSymbol ->
                    codedSymbol.attachToCode(1 - index)
                }
                sortedList
            }
            else -> supervisorScope {
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

                if (
                    sortedList.size % 4 != 0
                    && sortedList.size % 2 == 0
                    && idx == sortedList.size / 2
                ) {
                    idx--
                }

                val firstPart = sortedList.subList(0, idx)
                val secondPart = sortedList.subList(idx, sortedList.size)
                val result = mutableListOf<CodedSymbol>()

                arrayOf(firstPart, secondPart).forEachIndexed { index, arr ->
                    arr.forEach { symbol ->
                        symbol.attachToCode(1 - index)
                    }

                    result += withContext(Dispatchers.Default) {
                        createCodesByFano(arr.toSet())
                    }
                }
//            for ((i, arr) in arrayOf(secondPart, firstPart).withIndex().reversed()) {
//                arr.forEach { symbol ->
//                    codedSymbols.find { it == symbol }?.attachToCode(i)
//                }
//
//                result += withContext(Dispatchers.Default) {
//                    createCodesByFano(arr)
//                }
//            }

                return@supervisorScope result
            }
        }
    }
}