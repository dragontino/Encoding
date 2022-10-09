package com.mathematics.encoding.presentation.model

data class Symbol(var name: String = "", var probability: Double = -1.0)


data class SymbolWithCodeBuilder(val symbol: Symbol, val code: StringBuilder) :
    Comparable<SymbolWithCodeBuilder> {

    override fun compareTo(other: SymbolWithCodeBuilder): Int {
        val compareProbabilities = this.symbol.probability.compareTo(other.symbol.probability)
        val compareCodeLengths = this.code.length.compareTo(other.code.length)

        return when {
            compareCodeLengths == 0 -> this.code.toString().toInt() - other.code.toString().toInt()
            compareProbabilities == 0 -> -compareCodeLengths
            else -> compareProbabilities
        }
    }

}


fun Map<Symbol, StringBuilder>.toSymbolWithCodeList() = map {
    SymbolWithCodeBuilder(it.key, it.value)
}

data class SymbolWithCode(val symbol: Symbol, val code: String) {
    constructor(name: String, probability: Double, code: String) :
            this(Symbol(name, probability), code)
}

fun SymbolWithCodeBuilder.toSymbolWithCode() =
    SymbolWithCode(symbol, code.toString())