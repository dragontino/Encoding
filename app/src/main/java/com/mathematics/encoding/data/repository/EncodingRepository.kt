package com.mathematics.encoding.data.repository

import android.util.Log
import com.mathematics.encoding.presentation.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlin.math.abs

class EncodingRepository {

    suspend fun generateCodesByFano(symbols: List<Symbol>): List<SymbolWithCode> {
        val hashMap = HashMap<Symbol, StringBuilder>()
        symbols.forEach {
            if (it.probability != 0.0)
                hashMap[it] = StringBuilder()
        }
        return createCodesByFano(hashMap).map { it.toSymbolWithCode() }
    }


    private suspend fun createCodesByFano(symbolsMap: HashMap<Symbol, StringBuilder>): List<SymbolWithCodeBuilder> {
        val arraySum = symbolsMap.keys.sumOf { it.probability }

        fun f(x: Double): Double =
            abs(2 * x - arraySum)



        if (symbolsMap.size == 1) {
            return symbolsMap.toSymbolWithCodeList()
        }

        if (symbolsMap.size == 2) {
            val keys = symbolsMap.keys.sortedBy { it.probability }
            for (i in keys.indices)
                symbolsMap.getOrDefault(keys[i], StringBuilder()).append(i)

            return symbolsMap.toSymbolWithCodeList()
        }

        return supervisorScope {
            val sortedList = symbolsMap.keys.sortedByDescending { it.probability }
            var sum = 0.0
            var idx = 0

            while (true) {
                if (idx == sortedList.size)
                    break

                val currentProbability = sortedList[idx].probability
                Log.d("EncodingRepository", "currentProbability = $currentProbability")
                if (f(sum + currentProbability) < f(sum))
                    sum += currentProbability
                else
                    break

                idx++
            }

            Log.d("EncodingRepository", "idx = $idx")

            val firstPart = sortedList.subList(0, idx)
            Log.d("EncodingRepository", "firstList item = ${firstPart[0]}")
            val secondPart = sortedList.subList(idx, sortedList.size)
            val result = ArrayList<SymbolWithCodeBuilder>()

            for ((i, arr) in arrayOf(secondPart, firstPart).withIndex()) {
                arr.forEach { symbolsMap.getOrDefault(it, StringBuilder()).append(i) }

                result += withContext(Dispatchers.Default) {
                    createCodesByFano(arr.associateWith { symbolsMap[it] ?: StringBuilder() }
                            as HashMap<Symbol, StringBuilder>)
                }
            }

            return@supervisorScope result.sortedBy { it.code.length }
        }
    }
}