package dev.arkbuilders.rate.utils

fun <T> List<T>.replace(
    targetItem: T,
    newItem: T,
) = map {
    if (it == targetItem) {
        newItem
    } else {
        it
    }
}
