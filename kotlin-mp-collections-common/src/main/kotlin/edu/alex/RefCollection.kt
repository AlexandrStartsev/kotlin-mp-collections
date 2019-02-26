package edu.alex

interface RefCollection<K> {
    // Essential operations
    val size: Int

    fun iterator(): MutableIterator<K>

    fun add(element: K): Boolean

    fun clear()

    fun contains(element: @UnsafeVariance K): Boolean

    fun remove(element: K): Boolean

    fun retainAll(elements: RefCollection<K>): Boolean

    // Functionality below reasonably implemented through basic functionality. Override for performance boost in specific cases

    fun isEmpty() = size == 0

    fun retainAll(elements: Collection<K>) = retainAll(RefSet(elements))

    fun addAll(elements: Collection<K>) = elements.fold(false) { anyChange, e -> add(e) || anyChange }

    fun addAll(elements: RefCollection<K>) = elements.fold(false) { anyChange, e -> add(e) || anyChange }

    fun containsAll(elements: Collection<@UnsafeVariance K>) = elements.all { contains(it) }

    fun containsAll(elements: RefCollection<K>) = elements.all { contains(it) }

    fun removeAll(elements: Collection<K>) = elements.fold(false) { anyChange, e -> remove(e) || anyChange }

    fun removeAll(elements: RefCollection<K>) = elements.fold(false) { anyChange, e -> remove(e) || anyChange }

    fun forEach(action: (K) -> Unit) {
        for (element in this.iterator()) action(element)
    }
}