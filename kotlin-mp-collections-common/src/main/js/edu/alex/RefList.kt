package edu.alex

actual class RefList<E>: AbstractRefList<E> {
    private val backingArray = js("[]") //ES6Array<E>()

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

    override fun retainAll(elements: RefCollection<E>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun add(index: Int, element: E) {
        if(index < 0 || index > size)
            throw IndexOutOfBoundsException()
        backingArray.splice(index, 0, element)
    }

    override fun removeAt(index: Int) {
        backingArray.splice(index, 1)
    }

    override fun set(index: Int, element: E): E {
        val old = backingArray[index]
        backingArray[index] = element
        return old
    }
}