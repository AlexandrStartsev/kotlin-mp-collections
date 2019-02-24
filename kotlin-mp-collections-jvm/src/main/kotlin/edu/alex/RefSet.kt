package edu.alex

import java.util.*
import edu.alex.ObjectReference.wrap

class RefSet<K>: MutableSet<K> {

    private val backingSet: MutableSet<ObjectReference<K>>

    // JS set iterator follows insertion order, so we also use LinkedHashSet for JVM implementation
    constructor () {
        backingSet = LinkedHashSet()
    }

    constructor (c: Collection<K>) {
        backingSet = LinkedHashSet()
        addAll(c)
    }

    constructor (vararg args: K) {
        backingSet = LinkedHashSet()
        Arrays.stream<K>(args).forEach { add(it) }
    }

    internal constructor(backingSet: MutableSet<ObjectReference<K>>) {
        this.backingSet = backingSet
    }

    override fun isEmpty() = backingSet.isEmpty()

    override val size: Int get() = backingSet.size

    override fun iterator() = backingSet.iterator().let {
        object : MutableIterator<K> {
            override fun hasNext() = it.hasNext()
            override fun next() = it.next().get()
            override fun remove() = it.remove()
        }
    }

    override fun contains(element: K) = backingSet.contains(wrap(element))

    override fun add(element: K) = backingSet.add(wrap(element))

    override fun remove(element: K) = backingSet.remove(wrap(element))

    override fun containsAll(elements: Collection<K>) = elements.all { contains(it) }

    override fun addAll(elements: Collection<K>) = elements.fold(false) { anyChange, e -> add(e) || anyChange }

    override fun retainAll(elements: Collection<K>) = backingSet.retainAll( elements.map { wrap(it) })

    override fun removeAll(elements: Collection<K>) = elements.fold(false) { anyChange, e -> remove(e) || anyChange }

    override fun clear() = backingSet.clear()

    override fun toString() = joinToString(prefix = "[", postfix = "]")

    fun toMutableSet(): MutableSet<K> = RefSet(this)
}