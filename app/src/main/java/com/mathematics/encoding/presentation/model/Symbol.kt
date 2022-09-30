package com.mathematics.encoding.presentation.model

data class Symbol(var name: String = "", var probability: Double = -1.0)


data class SymbolWithCodeBuilder(val symbol: Symbol, val code: StringBuilder)


fun Map<Symbol, StringBuilder>.toSymbolWithCodeList() = map {
    SymbolWithCodeBuilder(it.key, it.value)
}

data class SymbolWithCode(val symbol: Symbol, val code: String) {
    constructor(name: String, probability: Double, code: String) :
            this(Symbol(name, probability), code)
}

fun SymbolWithCodeBuilder.toSymbolWithCode() =
    SymbolWithCode(symbol, code.toString())