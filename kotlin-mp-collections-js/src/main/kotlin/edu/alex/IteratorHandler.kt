package edu.alex

internal class IteratorHandler<T, K>: MutableIterator<K> {
    private val cursor: ES6Iterator<T>
    private var prev: IteratorValue<T>? = null
    private var head: IteratorValue<T>

    private var removeCb: (it: IteratorValue<T>) -> Unit
    private var nextCb: (it: IteratorValue<T>) -> K?

    constructor(cursor: ES6Iterator<T>, removeCb: (it: IteratorValue<T>) -> Unit, nextCb: (it: IteratorValue<T>) -> K? ) {
        this.cursor = cursor
        this.removeCb = removeCb
        this.nextCb = nextCb
        head = cursor.next()
    }

    override fun hasNext() = !head.done

    override fun next(): K {
        if(hasNext()) {
            var tmp: dynamic = nextCb(head)
            if(jsTypeOf(tmp) === "undefined") tmp = null
            prev = head
            head = cursor.next()
            return tmp
        }
        throw NoSuchElementException()
    }

    override fun remove() {
        prev?.takeIf { !it.done }?.let {
            removeCb(it)
            prev = null
            return@remove
        }
        throw IllegalStateException()
    }
}