package com.mathematics.encoding.presentation.model

import com.mathematics.encoding.lcm
import com.mathematics.encoding.toSimpleFraction

internal interface Probability {
    var probability: Double
}


data class Symbol(
    var name: String = "",
    override var probability: Double = -1.0,
    var nameError: String = "",
    var probabilityError: String = ""
) : Probability {
    val hasError: Boolean get() = nameError.isNotBlank() || probabilityError.isNotBlank()
    val error: String get() = when {
        nameError.isNotBlank() -> nameError
        probabilityError.isNotBlank() -> probabilityError
        else -> ""
    }

    override fun toString(): String {
        return "name = $name, probability = $probability"
    }
}


data class SymbolWithCodeBuilder(
    val symbolName: String,
    override var probability: Double,
    val code: StringBuilder
) : Comparable<SymbolWithCodeBuilder>, Probability {

    override fun compareTo(other: SymbolWithCodeBuilder): Int {
        val compareProbabilities = this.probability.compareTo(other.probability)
        val compareCodeLengths = this.code.length.compareTo(other.code.length)

        return when {
            compareProbabilities != 0 -> compareProbabilities
            compareCodeLengths != 0 -> -compareCodeLengths
            else -> this.code.toString().toInt() - other.code.toString().toInt()
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


internal fun SymbolWithCodeBuilder.toSymbolWithCode() =
    SymbolWithCode(symbolName, probability, code.toString())


//[0.05, 0.05, 0.2, 0.3, 0.4]
internal fun Collection<Double>.calculateFrequencies(): List<Int> {
    val result = map { it.toSimpleFraction() }
    val commonDenominator = result.reduce { left, right ->
        val denominator = left.second lcm right.second
        Pair(left.first, denominator)
    }.second
    return result.map {
        it.first * (commonDenominator / it.second)
    }
}


//internal fun Symbol.attachProbability(probability: Double): SymbolWithProbability =
//    SymbolWithProbability(name, this.probability, probability)