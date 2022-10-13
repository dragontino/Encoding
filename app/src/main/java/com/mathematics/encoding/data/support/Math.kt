package com.mathematics.encoding.data.support

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

const val countSigns = 3


data class OrdinaryFraction(val numerator: Int, val denominator: Int): Comparable<OrdinaryFraction> {
    operator fun times(n: Int): OrdinaryFraction =
        OrdinaryFraction(numerator * n, denominator)

    private infix fun commonDenominator(fraction: OrdinaryFraction): Int =
        numerator lcm fraction.denominator

    operator fun plus(other: OrdinaryFraction): OrdinaryFraction {
        val commonDenominator = this commonDenominator other
        val numerator = (this * commonDenominator).toInt() + (other * commonDenominator).toInt()

        return OrdinaryFraction(
            numerator = numerator / (numerator gcd commonDenominator),
            denominator = commonDenominator / (commonDenominator gcd numerator)
        )
    }

    override fun toString(): String =
        "$numerator / $denominator"

    override fun compareTo(other: OrdinaryFraction): Int = when (denominator) {
        other.denominator -> numerator.compareTo(other.numerator)
        else -> {
            val commonDenominator = this commonDenominator other
            OrdinaryFraction(
                numerator = (this * commonDenominator).toInt(),
                denominator = commonDenominator
            ).compareTo(
                OrdinaryFraction(
                    numerator = (other * commonDenominator).toInt(),
                    denominator = commonDenominator
                )
            )
        }
    }
}


fun OrdinaryFraction.toInt() = numerator / denominator


fun Double.toSimpleFraction(): OrdinaryFraction {
    var denominator = 1
    while ((this * denominator) % 1 != 0.0) {
        denominator *= 10
    }
    val numerator = (this * denominator).toInt()
    val gcd = numerator gcd denominator
    return OrdinaryFraction(
        numerator = numerator / gcd,
        denominator = denominator / gcd
    )
}


fun Double.round(countSigns: Int): Double {
    val bigDecimal = if (this.isNaN() || this == Double.POSITIVE_INFINITY)
        BigDecimal(0.0)
    else
        BigDecimal(this)
    return bigDecimal.setScale(countSigns, RoundingMode.HALF_UP).toDouble()
}


infix fun Int.lcm(n: Int): Int =
    abs(this * n) / (this gcd n)


infix fun Int.gcd(n: Int): Int =
    if (n <= 0) this
    else n gcd this % n


infix fun Double.compare(other: Double): Int =
    this.toSimpleFraction().compareTo(other.toSimpleFraction())