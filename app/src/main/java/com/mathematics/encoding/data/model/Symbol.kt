package com.mathematics.encoding.data.model

import com.mathematics.encoding.data.support.compare

data class Symbol(
    var name: String = "",
    var probability: Double = -1.0,
) : Comparable<Symbol> {


    override fun compareTo(other: Symbol): Int = when {
        this.probability compare other.probability != 0 ->
            other.probability compare this.probability
        else ->
            this.name.compareTo(other.name)
    }
}