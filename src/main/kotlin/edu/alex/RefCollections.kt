package edu.alex

expect class RefList<E>: AbstractRefList<E>
expect class RefSet<E>: AbstractRefSet<E>

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

fun <K> wrapLookupIfReasonable(itemLookup: RefCollection<K>, itemsToLook: RefCollection<K>? = null): RefCollection<K> {
    if(itemLookup is AbstractRefSet || itemLookup.size < 50 || itemsToLook != null && itemsToLook.size < 50 ) return itemLookup
    return RefSet(itemLookup)
}

//fun <K> RefCollection<K>.retainAll(elements: Collection<K>) = this.retainAll(if(elements.size > 10) RefSet(elements) else RefList(elements))

fun <K> RefCollection<K>.retainAll(elements: RefCollection<K>): Boolean {
    val other = wrapLookupIfReasonable(elements, this)
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

fun <K> RefCollection<K>.removeAll(elements: Collection<K>) = elements.fold(false) { anyChange, e -> remove(e) || anyChange }

fun <K> RefCollection<K>.removeAll(elements: RefCollection<K>) = elements.fold(false) { anyChange, e -> remove(e) || anyChange }

fun <K> RefCollection<K>.intersect(elements: RefCollection<K>): AbstractRefSet<K> = RefSet(this).let { it.retainAll(elements); it }

fun <K, M: RefCollection<K>> RefCollection<K>.intersectTo(destination: M, elements: RefCollection<K>): M {
    val (lookup, toLook) = if(elements is AbstractRefSet) elements to this else wrapLookupIfReasonable(this, elements) to elements
    toLook.forEach {
        if(lookup.contains(it)) destination.add(it)
    }
    return destination
}

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

fun <E> RefCollection<E>.collectionEqualsOrdered(other: RefCollection<E>): Boolean {
    if(this === other) return true
    if(this !is RefCollectionView && other !is RefCollectionView && this.size != other.size) return false
    val it1 = this.iterator()
    val it2 = other.iterator()
    while(it1.hasNext() && it2.hasNext() && it1.next() === it2.next()) ;
    return !it1.hasNext() && !it2.hasNext()
}
fun <K> RefCollection<K>.containsAll(elements: Collection<K>) = containsAll(RefList(elements))

fun <K> RefCollection<K>.containsAll(elements: RefCollection<K>): Boolean {
    if(this === elements) return true
    if(elements is AbstractRefSet && elements.size > size) return false
    val set = wrapLookupIfReasonable(this, elements)
    return elements.all { set.contains(it) }
}