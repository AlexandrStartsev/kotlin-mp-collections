package edu.alex

expect class RefList<E>: AbstractRefList<E>
expect class RefSet<E>: AbstractRefSet<E>

fun <E> newRefList(): RefList<E> = RefList()

inline fun <T, R> RefCollection<T>.fold(initial: R, operation: (acc: R, T) -> R): R {
    var accumulator = initial
    for (element in this.iterator()) accumulator = operation(accumulator, element)
    return accumulator
}

inline fun <T> RefCollection<T>.filter(crossinline predicate: (T) -> Boolean): RefCollection<T> = this.filter(RefList()) { predicate(it) }

inline fun <T, M: RefCollection<T>> RefCollection<T>.filter(destination: M, crossinline predicate: (T) -> Boolean): M {
    this.forEach { if(predicate(it))  destination.add(it) }
    return destination
}

inline fun <T> RefCollection<T>.first(predicate: (T) -> Boolean): T? {
    for (element in this.iterator()) if (predicate(element)) return element
    return null
}

inline fun <T> RefCollection<T>.first(): T? = this.iterator().takeIf { it.hasNext() }?.next()

inline fun <T> RefCollection<T>.all(predicate: (T) -> Boolean): Boolean {
    for (element in this.iterator()) if (!predicate(element)) return false
    return true
}

inline fun <T> RefCollection<T>.any(predicate: (T) -> Boolean): Boolean {
    for (element in this.iterator()) if (predicate(element)) return true
    return false
}

inline fun <T, R, M: RefCollection<R>> RefCollection<T>.mapTo(destination: M, crossinline transform: (T) -> R): M {
    this.forEach { destination.add(transform(it)) }
    return destination
}

inline fun <T, R, M: MutableCollection<R>> RefCollection<T>.mapToCollection(destination: M, crossinline transform: (T) -> R): M {
    this.forEach { destination.add(transform(it)) }
    return destination
}

inline fun <T, M: MutableCollection<T>> RefCollection<T>.toCollection(destination: M): M {
    this.forEach { destination.add(it) }
    return destination
}

inline fun <T, R> RefCollection<T>.map(crossinline transform: (T) -> R): RefList<R> {
    return mapTo(newRefList(), transform)
}

/*
fun <T> RefCollection<T>.joinToString() {
    this.iterator()
}


fun retainAll(elements: Collection<K>): Boolean

fun retainAll(elements: RefCollection<K>): Boolean*/