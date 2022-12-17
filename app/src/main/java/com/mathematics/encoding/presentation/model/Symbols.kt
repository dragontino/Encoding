package com.mathematics.encoding.presentation.model

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.mathematics.encoding.data.support.compare
import java.io.StringReader

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
        nameError = emptySymbolNameMessage
        probabilityError = incorrectSymbolProbabilityMessage
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
    val symbolName: String = "",
    override var probability: Double = -1.0,
    val code: String = ""
) : Probability, Parcelable {

    constructor(parcel: Parcel) : this(
        symbolName = parcel.readString() ?: "",
        probability = parcel.readDouble(),
        code = parcel.readString() ?: "",
    )

    constructor(symbol: Symbol, code: String): this(symbol.name, symbol.probability, code)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(symbolName)
        parcel.writeDouble(probability)
        parcel.writeString(code)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<SymbolWithCode> {
        override fun createFromParcel(parcel: Parcel): SymbolWithCode {
            return SymbolWithCode(parcel)
        }

        override fun newArray(size: Int): Array<SymbolWithCode?> {
            return arrayOfNulls(size)
        }
    }
}


internal fun SymbolWithCode.parseToJson(): String =
    Gson().toJson(this)

internal fun Collection<SymbolWithCode>.parseToJson(): String =
    Gson().toJson(this)

internal fun String.parseToSymbolWithCode(): SymbolWithCode =
    Gson().fromJson(this, SymbolWithCode::class.java)

internal fun String.parseToSymbolWithCodeList(): List<SymbolWithCode> {
    val type = object : TypeToken<List<SymbolWithCode>>() {}.type
    val reader = JsonReader(StringReader(this)).apply { isLenient = true }
    return Gson().fromJson(reader, type)
}


internal fun String.encode(codes: Collection<SymbolWithCode>): String {
    val result = StringBuilder()
    this.forEach { char ->
        val encodedChar = codes.find { it.symbolName == char.uppercase() }?.code
        result.append(encodedChar ?: "")
    }
    return result.toString()
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