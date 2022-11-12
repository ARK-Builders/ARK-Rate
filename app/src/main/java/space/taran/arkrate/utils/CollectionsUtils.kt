package space.taran.arkrate.utils

fun <T> List<T>.replace(targetItem: T, newItem: T) = map {
    if (it == targetItem) {
        newItem
    } else {
        it
    }
}