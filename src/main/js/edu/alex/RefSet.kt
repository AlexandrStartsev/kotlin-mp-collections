package edu.alex

actual class RefSet<K>: AbstractRefSet<K>  {
    internal val backingSet: ES6Set<K>

    constructor () {
        backingSet = ES6Set()
    }

    constructor (c: Iterable<K>) {
        backingSet = ES6Set()
        c.forEach { add(it) }
    }

    constructor (c: RefCollection<K>) {
        when(c) {
            is RefList -> backingSet = ES6Set(c.backingArray)
            is RefSet -> backingSet = ES6Set(c.backingSet)
            else -> {
                backingSet = ES6Set()
                c.forEach { add(it) }
            }
        }
    }

    constructor (vararg args: K) {
        backingSet = ES6Set()
        args.forEach { add(it) }
    }

    override val size: Int get() = backingSet.size

    override fun add(element: K): Boolean {
        val before = size
        backingSet.add(element)
        return before != size
    }

    override fun clear() = backingSet.clear()

    override fun contains(element: K) = backingSet.has(element)

    override fun iterator(): MutableIterator<K> = IteratorHandler(backingSet.values(), { backingSet.delete(it.value) }, { it.value } )

    override fun remove(element: K) = backingSet.delete(element)

    override fun forEach(action: (K) -> Unit) {
        backingSet.forEach(action)
    }

    /*override fun retainAll(elements: RefCollection<K>): Boolean {
        val c = if( elements is AbstractRefSet ) elements else RefSet(elements)
        val before = size
        backingSet.forEach { it, _, _ -> if(!c.contains(it)) backingSet.delete(it) }
        return before != size
    }*/

    //override fun toString() = joinToString(prefix = "[", postfix = "]")
}