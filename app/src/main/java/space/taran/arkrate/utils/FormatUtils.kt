package space.taran.arkrate.utils

fun Double.removeFractionalPartIfEmpty(): String {
    val integerPart = this.toInt()
    val fractionalPart = integerPart - this
    return if (fractionalPart == 0.0)
        integerPart.toString()
    else
        this.toString()
}

fun Float.removeFractionalPartIfEmpty(): String {
    val integerPart = this.toInt()
    val fractionalPart = integerPart - this
    return if (fractionalPart == 0.0f)
        integerPart.toString()
    else
        this.toString()
}