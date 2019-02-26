package edu.alex

import java.util.*
import edu.alex.ObjectReference.Companion.wrap

actual class RefSet<K>: AbstractRefSet<K> {

    private val backingSet: MutableSet<ObjectReference<K>>

    // JS set iterator follows insertion order, so we also use LinkedHashSet for JVM implementation
    constructor () {
        backingSet = LinkedHashSet()
    }

    constructor (c: Iterable<K>) {
        backingSet = LinkedHashSet()
        c.forEach { add(it) }
    }

    constructor (c: RefCollection<K>) {
        backingSet = LinkedHashSet()
        c.forEach { add(it) }
    }

    constructor (vararg args: K) {
        backingSet = LinkedHashSet()
        args.forEach { add(it) }
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

    override fun retainAll(elements: Collection<K>) = backingSet.retainAll( elements.map { wrap(it) })

    // TODO maybe just implement?
    override fun retainAll(elements: RefCollection<K>) = backingSet.retainAll( elements.mapToCollection(java.util.HashSet()) { wrap(it) })

    override fun clear() = backingSet.clear()

   // override fun toString() = joinToString(prefix = "[", postfix = "]")
}