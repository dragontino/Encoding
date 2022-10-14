package com.mathematics.encoding.presentation.model

import androidx.lifecycle.MutableLiveData
import com.mathematics.encoding.data.support.compare

internal interface Probability {
    var probability: Double
}


data class Symbol(
    var name: String = "",
    override var probability: Double = -1.0,
    var nameError: String = "",
    var probabilityError: String = ""
) : Comparable<Symbol>, Probability {
    val hasError: Boolean get() = nameError.isNotBlank() || probabilityError.isNotBlank()
    val error: String get() = when {
        nameError.isNotBlank() -> nameError
        probabilityError.isNotBlank() -> probabilityError
        else -> ""
    }

    val nameLiveData = MutableLiveData(name)
    val probabilityLiveData = MutableLiveData(
        probability.let {
            when {
                it < 0 || it > 1 -> ""
                it % 1 == 0.0 -> probability.toInt().toString()
                else -> probability.toString()
            }
        }
    )

    fun updateNameWithoutCheck(name: String) {
        nameLiveData.value = name
    }

    fun updateStringProbability(value: String) {
        probabilityLiveData.value = value
    }

    fun clear(index: Int) {
        name = ""
        probability = -1.0
        nameError = emptySymbolNameMessage(index)
        probabilityError = incorrectSymbolProbabilityMessage(index)
        nameLiveData.value = ""
        probabilityLiveData.value = ""
    }


    override fun toString(): String {
        return "name = $name, probability = $probability"
    }

    override fun compareTo(other: Symbol): Int = when {
        this.probability compare other.probability != 0 ->
            other.probability compare this.probability
        else -> this.name.compareTo(other.name)
    }
}


fun emptySymbolNameMessage(index: Int): String =
    "Пустой символ в строке ${index + 1}"

fun incorrectSymbolProbabilityMessage(index: Int): String =
    "Некорректная вероятность в строке ${index + 1}"


data class SymbolWithCodeBuilder(
    val symbolName: String,
    override var probability: Double,
    val code: StringBuilder
) : Comparable<SymbolWithCodeBuilder>, Probability {

    override fun compareTo(other: SymbolWithCodeBuilder): Int {
        val compareCodeLengths = this.code.length.compareTo(other.code.length)
        val compareCodeValues = this.code.toString().toInt().compareTo(other.code.toString().toInt())

        return when {
            compareCodeLengths != 0 -> -compareCodeLengths
            compareCodeValues != 0 -> compareCodeValues
            else -> this.probability.compareTo(other.probability)
        }
    }

}


data class SymbolWithCode(
    val symbolName: String,
    override var probability: Double,
    val code: String
) : Probability {
    constructor(symbol: Symbol, code: String): this(symbol.name, symbol.probability, code)
}


internal val Collection<Probability>.sumProbabilities: Double
    get() = sumOf { it.probability }


internal val Array<out Probability>.sumProbabilities: Double
    get() = sumOf { it.probability }


internal fun Map<Symbol, StringBuilder>.toSymbolBuilderList() = map {
    SymbolWithCodeBuilder(it.key.name, it.key.probability, it.value)
}


internal fun SymbolWithCodeBuilder.build() =
    SymbolWithCode(symbolName, probability, code.toString())


//[0.05, 0.05, 0.2, 0.3, 0.4]
//internal fun Collection<Double>.calculateFrequencies(): List<Int> {
//    val result = map { it.toSimpleFraction() }
//    val commonDenominator = result.reduce { left, right ->
//        val denominator = left.second lcm right.second
//        Pair(left.first, denominator)
//    }.second
//    return result.map {
//        it.first * (commonDenominator / it.second)
//    }
//}


//internal fun Symbol.attachProbability(probability: Double): SymbolWithProbability =
//    SymbolWithProbability(name, this.probability, probability)