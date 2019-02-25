package edu.alex

fun <T> Iterable<T>.toMutableSet(): MutableSet<T> = when (this) {
    is RefSet<T> -> RefSet(this)
    is Collection<T> -> LinkedHashSet(this)
    else -> toCollection(LinkedHashSet())
}

infix fun <T> Iterable<T>.intersect(other: Iterable<T>): Set<T> {
    // it makes sense to make new set out of non-RefSet, as retainAll will have to convert incoming collection into set anyway
    if(this is RefSet) {
        val set = RefSet(other)
        set.retainAll(this)
        return set
    }
    val set = this.toMutableSet()
    set.retainAll(other)
    return set
}