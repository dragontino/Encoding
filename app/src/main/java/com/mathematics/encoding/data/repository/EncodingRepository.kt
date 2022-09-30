package com.mathematics.encoding.data.repository

import com.mathematics.encoding.presentation.model.Symbol
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs

class EncodingRepository {

    suspend fun generateCodesByFano(symbols: List<Symbol>): List<String> {
        val hashMap = HashMap<Symbol, StringBuilder>()
        symbols.forEach {
            if (it.probability != 0.0)
                hashMap[it] = StringBuilder()
        }
        return createCodesByFano(hashMap).map { it.toString() }
    }


    private suspend fun createCodesByFano(symbolsMap: HashMap<Symbol, StringBuilder>): List<StringBuilder> {
        val arraySum = symbolsMap.keys.sumOf { it.probability }

        fun f(x: Double): Double =
            abs(2 * x - arraySum)



        if (symbolsMap.size == 1) {
            return symbolsMap.values.toList()
        }

        if (symbolsMap.size == 2) {
            val keys = symbolsMap.keys.sortedBy { it.probability }
            for (i in keys.indices)
                symbolsMap.getOrDefault(keys[i], StringBuilder()).append(i)

            return symbolsMap.values.toList()
        }

        return supervisorScope {
            val sortedList = symbolsMap.keys.sortedByDescending { it.probability }
            var sum = 0.0
            var idx = 0

            while (true) {
                if (idx == sortedList.size)
                    break

                val currentProbability = sortedList[idx].probability
                if (f(sum + currentProbability) < f(sum))
                    sum += currentProbability
                else
                    break

                idx++
            }

            val firstPart = sortedList.subList(0, idx)
            val secondPart = sortedList.subList(idx, sortedList.size)
            val result = ArrayList<StringBuilder>()

            for ((i, arr) in arrayOf(secondPart, firstPart).withIndex()) {
                arr.forEach { symbolsMap.getOrDefault(it, StringBuilder()).append(i) }

                result += withContext(Dispatchers.Default) {
                    createCodesByFano(arr.associateWith { symbolsMap[it] ?: StringBuilder() }
                            as HashMap<Symbol, StringBuilder>)
                }
            }

            return@supervisorScope result
        }
    }
}