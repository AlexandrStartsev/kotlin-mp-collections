package edu.alex

actual class RefList<E>: AbstractRefList<E> {
    private val backingArray: dynamic //ES6Array<E>()

    constructor () {
        backingArray = js("[]")
    }

    constructor (c: Iterable<E>) {
        backingArray = js("[]")
        c.forEach { add(it) }
    }

    constructor (c: RefCollection<E>) {
        if(c is RefList) {
            backingArray = c.backingArray.slice(0)
        } else {
            backingArray = js("[]")
            c.forEach { add(it) }
        }
    }

    constructor (vararg args: E) {
        backingArray = js("[]")
        args.forEach { add(it) }
    }

    private constructor(backingArray: dynamic, nothing: Boolean) {
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

    override fun contains(element: E): Boolean = backingArray.some { it -> it === element }

    override fun get(index: Int): E {
        if( index < 0 || index >= size )
            throw IndexOutOfBoundsException()
        var a = backingArray[index]
        js("typeof a === 'undefined' && (a = null)")
        return a
    }

    override fun indexOf(element: E): Int = backingArray.indexOf(element)

    override fun iterator() = object : MutableIterator<E> {
        var index = 0
        var state = false
        override fun hasNext() = backingArray.length > index
        override fun next(): E {
            if(hasNext()) {
                state = true
                var tmp: dynamic = backingArray[index++]
                if(jsTypeOf(tmp) === "undefined") tmp = null
                return tmp
            }
            throw NoSuchElementException()
        }
        override fun remove() {
            if(state == false) {
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
        val old = backingArray[index]
        backingArray[index] = element
        return old
    }

    override fun slice(start: Int, end: Int?): AbstractRefList<E> {
        if (start < 0 || start >= size)
            throw IndexOutOfBoundsException()
        return when (end) {
            null -> RefList<E>(backingArray.slice(start), true)
            else -> {
                if (end < start || end > size) throw IndexOutOfBoundsException()
                RefList<E>(backingArray.slice(start, end), true)
            }
        }
    }
}