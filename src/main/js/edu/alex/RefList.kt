package edu.alex

actual class RefList<E>: AbstractRefList<E> {
    internal val backingArray: ES6Array<E>

    constructor () {
        backingArray = ES6Array()
    }

    constructor (c: Iterable<E>) {
        backingArray = ES6Array()
        c.forEach { add(it) }
    }

    constructor (c: RefCollection<E>) {
        when(c) {
            is RefList -> backingArray = c.backingArray.slice(0)
            //is RefSet -> backingArray = ES6Array.from(c.backingSet) strangely, this is a much slower way ...
            else -> {
                backingArray = ES6Array()
                c.forEach { add(it) }
            }
        }
    }

    constructor (vararg args: E) {
        backingArray = ES6Array()
        args.forEach { add(it) }
    }

    private constructor(backingArray: ES6Array<E>) {
        this.backingArray = backingArray
    }

    override val size: Int get() = backingArray.length

    override fun add(element: E): Boolean {
        backingArray.push(element)
        return true
    }

    override fun clear() {
        backingArray.splice(0)
    }

    override fun contains(element: E): Boolean = indexOf(element) >= 0

    override fun get(index: Int): E {
        if( index < 0 || index >= size )
            throw IndexOutOfBoundsException()
        var tmp = backingArray.asDynamic()[index]
        if(jsTypeOf(tmp) === "undefined") tmp = null
        return tmp
    }

    override fun indexOf(element: E): Int = backingArray.indexOf(element)

    override fun iterator() = object : MutableIterator<E> {
        var index = 0
        var state = false
        override fun hasNext() = backingArray.length > index
        override fun next(): E {
            if(hasNext()) {
                state = true
                var tmp = backingArray.asDynamic()[index++]
                if(jsTypeOf(tmp) === "undefined") tmp = null
                return tmp
            }
            throw NoSuchElementException()
        }
        override fun remove() {
            if(!state) {
                throw IllegalStateException()
            }
            backingArray.splice(--index, 1)
            state = false
        }
    }

    override fun remove(element: E): Boolean = when(val idx = indexOf(element)) {
        -1 -> false
        else -> { removeAt(idx); true }
    }

    override fun add(index: Int, element: E) {
        if(index < 0 || index > size)
            throw IndexOutOfBoundsException()
        backingArray.splice(index, 0, element)
    }

    override fun removeAt(index: Int) {
        if( index < 0 || index >= size )
            throw IndexOutOfBoundsException()
        backingArray.splice(index, 1)
    }

    override fun set(index: Int, element: E): E {
        if( index < 0 || index >= size )
            throw IndexOutOfBoundsException()
        val old = backingArray.asDynamic()[index]
        backingArray.asDynamic()[index] = element
        return old
    }

    override fun slice(start: Int, end: Int?): AbstractRefList<E> {
        if (start < 0 || start >= size)
            throw IndexOutOfBoundsException()
        return when (end) {
            null -> RefList(backingArray.slice(start))
            else -> {
                if (end < start || end > size) throw IndexOutOfBoundsException()
                RefList(backingArray.slice(start, end))
            }
        }
    }

    override fun forEach(action: (E) -> Unit) {
        backingArray.forEach(action)
    }

    override fun filter(predicate: (E) -> Boolean) = RefList(this.backingArray.filter { predicate(it) })
}