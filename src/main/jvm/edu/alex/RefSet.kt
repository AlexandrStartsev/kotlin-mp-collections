package edu.alex

import java.util.*
import edu.alex.ObjectReference.Companion.wrap

actual class RefSet<K>: AbstractRefSet<K> {

    private val backingSet: MutableSet<ObjectReference<K>> = LinkedHashSet()

    // JS set iterator follows insertion order, so we also use LinkedHashSet for JVM implementation
    constructor ()

    constructor (c: Iterable<K>) {
        c.forEach { add(it) }
    }

    constructor (c: RefCollection<K>) {
        c.forEach { add(it) }
    }

    constructor (vararg args: K) {
        args.forEach { add(it) }
    }

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

    override fun clear() = backingSet.clear()

   // override fun toString() = joinToString(prefix = "[", postfix = "]")
}