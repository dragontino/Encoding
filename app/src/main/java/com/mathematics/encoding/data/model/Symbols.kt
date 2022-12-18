package com.mathematics.encoding.data.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.mathematics.encoding.data.support.compare
import java.io.StringReader


internal interface Symbols {
    val name: String
    val probability: Double
}


data class Symbol(
    override var name: String = "",
    override var probability: Double = -1.0,
) : Comparable<Symbol>, Symbols {


    override fun compareTo(other: Symbol) = when(
        val compareProbabilities = this.probability compare other.probability
    ) {
        0 -> -this.name.compareTo(other.name)
        else -> compareProbabilities
    }

    override fun toString() = "name = $name, probability = $probability"
}




class CodedSymbol(
    override val name: String,
    override val probability: Double = -1.0,
    initialCode: String = "",
) : Comparable<CodedSymbol>, Symbols {
    private val codeBuilder = StringBuilder(initialCode)

    val code: String get() = codeBuilder.toString()


    fun attachToCode(value: Int) {
        if (value == 0 || value == 1) codeBuilder.append(value)
    }

    override fun compareTo(other: CodedSymbol): Int {
        val compareCodeLengths = this.code.length.compareTo(other.code.length)
        val compareCodeValues = this.code.compareTo(other.code)

        return when {
            compareCodeLengths != 0 -> -compareCodeLengths
            compareCodeValues != 0 -> compareCodeValues
            else -> this.probability.compareTo(other.probability)
        }
    }


    override fun toString() = "name = $name, probability = $probability, code = $code"
}


fun Symbol.attachCode(code: String = "") =
    CodedSymbol(name, probability, code)


fun CodedSymbol.detachCode() =
    Symbol(name, probability)




internal fun Collection<CodedSymbol>.parseToJson(): String =
    Gson().toJson(this)




internal fun String.parseToCodedSymbolList(): List<CodedSymbol> {
    val type = object : TypeToken<List<CodedSymbol>>() {}.type
    val reader = JsonReader(StringReader(this)).apply { isLenient = true }
    return Gson().fromJson(reader, type)
}




internal val Collection<Symbols>.sumProbabilities: Double
    get() = sumOf { it.probability }




internal fun String.encode(codes: Collection<CodedSymbol>): String {
    val result = StringBuilder()
    this.forEach { char ->
        val encodedChar = codes.find { it.name == char.uppercase() }?.code
        result.append(encodedChar ?: "")
    }
    return result.toString()
}