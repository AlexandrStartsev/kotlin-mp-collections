package edu.alex

class RefSet<K>: MutableSet<K>  {
    private var set = ES6Set<K>()

    override val size: Int get() = set.size

    override fun add(element: K): Boolean {
        val before = size
        set.add(element)
        return before != size
    }

    override fun addAll(elements: Collection<K>): Boolean {
        val before = size
        elements.forEach { set.add(it) }
        return before != size
    }

    override fun clear() = set.clear()

    override fun contains(element: K) = set.has(element)

    override fun containsAll(elements: Collection<K>) = elements.all { set.has(it) }

    override fun isEmpty() = size == 0

    override fun iterator(): MutableIterator<K> = IteratorHandler(set.values(), { set.delete(it.value) }, { it.value } )

    override fun remove(element: K): Boolean {
        val before = size
        set.delete(element)
        return before != size
    }

    override fun removeAll(elements: Collection<K>): Boolean {
        val before = size
        elements.forEach { set.delete(it) }
        return before != size
    }

    // TODO: at some point it s easier to re-create internal set. Idk.
    override fun retainAll(elements: Collection<K>): Boolean {
        val before = size
        set.forEach { it, _, _ -> if(!elements.contains(it)) set.delete(it) }
        return before != size
    }
}