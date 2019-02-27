package edu.alex

interface RefCollectionView

inline fun <T> RefCollection<T>.viewFilter(crossinline predicate: (T) -> Boolean): RefCollection<T> {
    val self = this
    return object : RefCollection<T>, RefCollectionView {
        override val size: Int
            get() {
                var n = 0
                self.forEach { if(predicate(it)) n++ }
                return n
            }

        override fun add(element: T) = predicate(element) && self.add(element)

        override fun clear() = self.iterator().let { while(it.hasNext()) if(predicate(it.next())) it.remove() }

        override fun contains(element: T) = predicate(element) && self.contains(element)

        override fun iterator() = object : MutableIterator<T> {
            val iter = self.iterator()
            var peek: T? = null
            var last: T? = null

            override fun hasNext(): Boolean {
                if(peek != null) return true
                while(iter.hasNext()) {
                    iter.next().takeIf { predicate(it) }.let {
                        peek = it
                        return@hasNext true
                    }
                }
                return false
            }

            override fun next(): T {
                if(hasNext())
                    peek?.let {
                        last = it
                        peek = null
                        return@next it
                    }
                throw NoSuchElementException()
            }

            override fun remove() {
                last?.let {
                    iter.remove()
                    last = null
                    return@remove
                }
                throw IllegalStateException()
            }
        }

        override fun remove(element: T) = predicate(element) && self.remove(element)
    }
}
