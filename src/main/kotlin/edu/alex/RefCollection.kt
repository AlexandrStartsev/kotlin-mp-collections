package edu.alex

interface RefCollection<K> {
    // Essential operations
    val size: Int

    fun iterator(): MutableIterator<K>

    fun clear()

    fun contains(element: @UnsafeVariance K): Boolean

    fun remove(element: K): Boolean

    fun forEach(action: (K) -> Unit) {
        for (element in this.iterator()) action(element)
    }

    fun add(element: K): Boolean = throw UnsupportedOperationException("Add is not supported on values")

    fun isEmpty() = size == 0

    fun filter(predicate: (K) -> Boolean): RefCollection<K> = this.filter(RefList()) { predicate(it) }
}