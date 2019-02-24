package edu.alex

class RefSet<K>: MutableSet<K>  {
    private val backingSet = ES6Set<K>()

    constructor ()

    constructor (c: Collection<K>) {
        addAll(c)
    }

    constructor (vararg args: K) {
        args.forEach { add(it) }
    }

    override val size: Int get() = backingSet.size

    override fun add(element: K): Boolean {
        val before = size
        backingSet.add(element)
        return before != size
    }

    override fun addAll(elements: Collection<K>) = elements.fold(false) { anyChange, e -> add(e) || anyChange }

    override fun clear() = backingSet.clear()

    override fun contains(element: K) = backingSet.has(element)

    override fun containsAll(elements: Collection<K>) = elements.all { contains(it) }

    override fun isEmpty() = size == 0

    override fun iterator(): MutableIterator<K> = IteratorHandler(backingSet.values(), { backingSet.delete(it.value) }, { it.value } )

    override fun remove(element: K) = backingSet.delete(element)

    override fun removeAll(elements: Collection<K>) = elements.fold(false) { anyChange, e -> remove(e) || anyChange }

    override fun retainAll(elements: Collection<K>): Boolean {
        val c = if(elements is RefSet) elements else RefSet(elements)
        val before = size
        backingSet.forEach { it, _, _ -> if(!c.contains(it)) backingSet.delete(it) }
        return before != size
    }

    override fun toString() = joinToString(prefix = "[", postfix = "]")

    fun toMutableSet(): MutableSet<K> = RefSet(this)
}