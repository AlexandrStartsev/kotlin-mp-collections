package edu.alex

expect class RefList<E>: AbstractRefList<E>
expect class RefSet<E>: AbstractRefSet<E>

inline fun <T> RefCollection<T>.forEach(action: (T) -> Unit) {
    for (element in this.iterator()) action(element)
}

inline fun <K, V> AbstractRefMap<K, V>.forEach(action: (AbstractRefMap.RefEntry<K, V>) -> Unit) {
    for(entry in iterator()) action(entry)
}

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
    return mapTo(RefList(), transform)
}

fun <K> RefCollection<K>.retainAll(elements: Collection<K>): Boolean {
    if(elements is Set) {
        var modified = false
        val it = this.iterator()
        while(it.hasNext()) {
            if(!elements.contains(it.next())) {
                it.remove()
                modified = true
            }
        }
        return modified
    }
    return this.retainAll(RefSet(elements))
}

fun <K> RefCollection<K>.retainAll(elements: RefCollection<K>): Boolean {
    val other = if(elements is AbstractRefSet) elements else RefSet(elements)
    var modified = false
    val it = this.iterator()
    while(it.hasNext()) {
        if(!other.contains(it.next())) {
            it.remove()
            modified = true
        }
    }
    return modified
}

fun <K> RefCollection<K>.addAll(elements: Collection<K>) = elements.fold(false) { anyChange, e -> add(e) || anyChange }

fun <K> RefCollection<K>.addAll(elements: RefCollection<K>) = elements.fold(false) { anyChange, e -> add(e) || anyChange }

fun <K> RefCollection<K>.containsAll(elements: Collection<@UnsafeVariance K>) = elements.all { contains(it) }

fun <K> RefCollection<K>.containsAll(elements: RefCollection<K>) = elements.all { contains(it) }

fun <K> RefCollection<K>.removeAll(elements: Collection<K>) = elements.fold(false) { anyChange, e -> remove(e) || anyChange }

fun <K> RefCollection<K>.removeAll(elements: RefCollection<K>) = elements.fold(false) { anyChange, e -> remove(e) || anyChange }

fun <K> RefCollection<K>.intersect(elements: RefCollection<K>): AbstractRefSet<K> = RefSet(this).let { it.retainAll(elements); it }

fun <K> RefCollection<K>.distinct(): AbstractRefSet<K> = when(this) {
    is AbstractRefSet -> this
    else -> RefSet(this)
}

fun <K, V> RefCollection<V>.groupBy(keyExtractor: (V) -> K): AbstractRefMap<K, V> {
    val map = RefMap<K, V>()
    this.forEach { map.set(keyExtractor(it), it) }
    return map
}


operator fun <K, V> AbstractRefMap.RefEntry<K, V>.component1() = this.key
operator fun <K, V> AbstractRefMap.RefEntry<K, V>.component2() = this.value

// TODO
fun <T> RefCollection<T>.joinToString() = toCollection(ArrayList()).joinToString()

fun <E> collectionsEqualOrdered(first: RefCollection<E>, second: RefCollection<E>): Boolean {
    if(first === second) return true
    if(first !is RefCollectionView && second !is RefCollectionView && first.size != second.size) return false
    val it1 = first.iterator()
    val it2 = second.iterator()
    while(it1.hasNext() && it2.hasNext() && it1.next() === it2.next()) ;
    return !it1.hasNext() && !it2.hasNext()
}


fun <E> collectionsEqual(first: RefCollection<E>, second: RefCollection<E>): Boolean {
    if(first.size != second.size) return false
    val (set, nonSet) = if(first is AbstractRefSet) first to second else if(second is AbstractRefSet) second to first else RefSet(first) to second
    nonSet.forEach {
        if(!set.contains(it))
            return@collectionsEqual false
    }
    return true
}