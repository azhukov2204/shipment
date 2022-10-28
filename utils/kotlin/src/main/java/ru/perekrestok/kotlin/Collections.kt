package ru.perekrestok.kotlin

fun <T> T.asList(): List<T> = listOf(this)

inline fun <T> Collection<T>.ifNotEmpty(action: () -> Unit) {
    if (isNotEmpty()) {
        action()
    }
}

inline fun <T> Collection<T>.containsBy(predicate: (T) -> Boolean): Boolean {
    return this.find { predicate(it) } != null
}

fun <E> Iterable<E>.replaceBy(new: E, predicate: (E) -> Boolean) = map { if (predicate(it)) new else it }

fun <T> MutableSet<T>.addOrRemove(value: T): Set<T> {
    if (contains(value)) {
        remove(value)
    } else {
        add(value)
    }
    return this.toSet()
}

fun <T> T.addToList(mutableList: MutableList<T>) {
    mutableList.add(this)
}

fun <T> T.addToListIf(needToAdd: Boolean, mutableList: MutableList<T>) {
    takeIf { needToAdd }?.addToList(mutableList)
}

fun <T> List<T>.addToList(mutableList: MutableList<T>) {
    mutableList.addAll(this)
}

fun <T> MutableList<T>.addIfNotNull(item: T?) {
    item?.let { add(item) }
}
