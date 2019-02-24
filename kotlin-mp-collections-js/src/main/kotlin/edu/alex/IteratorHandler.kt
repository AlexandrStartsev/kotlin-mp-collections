package edu.alex

internal class IteratorHandler<T, K>(private val backingIterator: ES6Iterator<T>,
                                     private val removeCb: (it: IteratorValue<T>) -> Unit,
                                     private val nextCb: (it: IteratorValue<T>) -> K?): MutableIterator<K> {

    private var last: IteratorValue<T>? = null
    private var head: IteratorValue<T> = backingIterator.next()

    override fun hasNext() = !head.done

    override fun next(): K {
        if(hasNext()) {
            var tmp: dynamic = nextCb(head)
            if(jsTypeOf(tmp) === "undefined") tmp = null
            last = head
            head = backingIterator.next()
            return tmp
        }
        throw NoSuchElementException()
    }

    override fun remove() {
        last?.takeIf { !it.done }?.let {
            removeCb(it)
            last = null
            return@remove
        }
        throw IllegalStateException()
    }
}