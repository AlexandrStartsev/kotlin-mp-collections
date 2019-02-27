package edu.alex

import edu.alex.ObjectReference.Companion.wrap

actual class RefList<E>: AbstractRefList<E> {
    private val backingList = java.util.ArrayList<ObjectReference<E>>()

    override val size: Int
        get() = backingList.size

    override fun add(element: E) = backingList.add(wrap(element))

    override fun clear() = backingList.clear()

    override fun contains(element: E) = backingList.contains(wrap(element))

    override fun get(index: Int) = backingList[index].get()

    override fun indexOf(element: E) = backingList.indexOf(wrap(element))

    override fun isEmpty() = backingList.isEmpty()

    override fun iterator() = object : MutableIterator<E> {
        val it = backingList.iterator()
        override fun hasNext() = it.hasNext()
        override fun next() = it.next().get()
        override fun remove() = it.remove()
    }

    override fun remove(element: E) = backingList.remove(wrap(element))

    override fun retainAll(elements: RefCollection<E>): Boolean {
        val c = if(elements is AbstractRefSet) elements else RefSet(elements)
        val it = iterator()
        val before = size
        while(it.hasNext()) {
            if(c.contains(it.next()))
                it.remove()
        }
        return before != size
    }

    override fun retainAll(elements: Collection<E>) = retainAll(RefSet(elements))

    override fun add(index: Int, element: E) = backingList.add(index, wrap(element))

    override fun removeAt(index: Int) {
        backingList.removeAt(index)
    }

    override fun set(index: Int, element: E): E = backingList.set(index, wrap(element)).get()
}