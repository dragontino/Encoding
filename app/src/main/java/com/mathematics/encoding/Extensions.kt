package com.mathematics.encoding

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.mathematics.encoding.presentation.model.SymbolWithCode
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.log2

@ExperimentalMaterialApi
fun ModalBottomSheetState.isExpanded() =
    targetValue == ModalBottomSheetValue.Expanded


const val loadingTimeMillis = 400L
const val countSigns = 3

@ExperimentalMaterialApi
fun ModalBottomSheetState.isHalfExpanded() =
    targetValue == ModalBottomSheetValue.HalfExpanded


val List<SymbolWithCode>.averageCodeLength: Double
get() = sumOf { it.code.length * it.probability }


val List<SymbolWithCode>.entropy: Double
get() = fold(0.0) { sum, element ->
    sum - element.probability * log2(element.probability)
}


fun Double.round(countSigns: Int): Double {
    val bigDecimal = if (this.isNaN() || this == Double.POSITIVE_INFINITY)
        BigDecimal(0.0)
    else
        BigDecimal(this)
    return bigDecimal.setScale(countSigns, RoundingMode.HALF_UP).toDouble()
}



@ExperimentalMaterialApi
suspend fun ModalBottomSheetState.open(stiffness: Float = Spring.StiffnessMediumLow) {
    animateTo(
        targetValue = ModalBottomSheetValue.HalfExpanded,
        anim = spring(stiffness = stiffness)
    )
}


@ExperimentalMaterialApi
suspend fun ModalBottomSheetState.close(stiffness: Float = Spring.StiffnessMediumLow) =
    animateTo(
        targetValue = ModalBottomSheetValue.Hidden,
        anim = spring(stiffness = stiffness)
    )


fun showToast(context: Context, text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, text, duration).show()
}


operator fun Color.plus(other: Color): Color {
    return Color(ColorUtils.blendARGB(this.toArgb(), other.toArgb(), 1f))
}


// Математические операции
fun Double.toSimpleFraction(): Pair<Int, Int> {
    var denominator = 1
    while ((this * denominator) % 1 != 0.0) {
        denominator *= 10
    }
    val numerator = (this * denominator).toInt()
    val gcd = numerator gcd denominator
    return Pair(
        first = numerator / gcd,
        second = denominator / gcd
    )
}

infix fun Int.lcm(n: Int): Int =
    abs(this * n) / (this gcd n)

infix fun Int.gcd(n: Int): Int =
    if (n <= 0) this
    else n gcd this % n

infix fun Double.compare(other: Double): Int {
    val firstFraction = this.toSimpleFraction()
    val secondFraction = other.toSimpleFraction()
    val commonDenominator = firstFraction.second lcm secondFraction.second
    return (firstFraction * commonDenominator).compareTo(secondFraction * commonDenominator)
}

private operator fun Pair<Int, Int>.times(n: Int): Int {
    return first * n / second
}