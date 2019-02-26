package edu.alex

import java.util.LinkedHashMap
import edu.alex.ObjectReference.Companion.wrap

class RefMap<K, V>: AbstractRefMap<K, V> {

    private companion object {
        fun <K, V> wrapEntry(e: AbstractRefMap.RefEntry<K, V>): MutableMap.MutableEntry<ObjectReference<K>, V> {
            return object : MutableMap.MutableEntry<ObjectReference<K>, V> {
                override val key = wrap(e.key)
                override val value: V get() = e.value
                override fun setValue(value: V): V = e.setValue(value)
            }
        }
    }

    private fun unwrapEntry(e: MutableMap.MutableEntry<ObjectReference<K>, V>): AbstractRefMap.RefEntry<K, V> {
        var lastValue = e.value
        return object : AbstractRefMap.RefEntry<K, V> {
            override val key = e.key.get()
            override val value: V get() = lastValue

            override fun setValue(value: V): V {
                val tmp = lastValue
                lastValue = value
                backingMap[e.key] = value
                return tmp
            }
            /** TODO: do i need that ? perhaps, or otherwise:
                 a1={}, a2={}
                 map({a1: "1"}).entries.intersect( map({a2: "1"}).entries )
                 will yield {a1: "1"}
            */
            /*override fun hashCode() = e.key.hashCode() + value.hashCode()

            override fun equals(other: Any?) = when(other) {
                is MutableMap.MutableEntry<*, *> -> other.key === key && other.value == value
                else -> false
            }*/
        }
    }


    private val backingMap = LinkedHashMap<ObjectReference<K>, V>()

    constructor(pairs: Collection<Pair<K, V>>) {
        pairs.forEach { put(it.first, it.second) }
    }

    constructor(pairs: RefCollection<Pair<K, V>>) {
        pairs.forEach { put(it.first, it.second) }
    }

    constructor(vararg pairs: Pair<K, V>) {
        for(it in pairs) put(it.first, it.second)
    }

    override val size: Int get() = backingMap.size

    override fun clear() = backingMap.clear()

    override fun containsKey(key: K?) = backingMap.containsKey(wrap(key))

    override fun containsValue(value: V) = backingMap.containsValue(value)

    override fun get(key: K?): V? = backingMap[wrap(key)]

    override fun put(key: K, value: V): V? = backingMap.put(wrap(key), value)

    override fun remove(key: K?): V? = backingMap.remove(wrap(key))

    override val values: RefCollection<V> get() =
        object : RefCollection<V> {
            val values = backingMap.values
            override val size get() = values.size

            override fun add(element: V) = throw UnsupportedOperationException("Add is not supported on values")

            override fun clear() = backingMap.clear()

            override fun contains(element: V) = values.any { it === element }

            override fun iterator() = values.iterator()

            override fun remove(element: V) = entries.first { it.value === element }?.let { backingMap.remove(wrap(it.key)); true } ?: false

            override fun retainAll(elements: RefCollection<V>) = TODO("not implemented")
        }

    override val keys: AbstractRefSet<K> get() = RefSet(backingMap.keys)

    override val entries: AbstractRefSet<AbstractRefMap.RefEntry<K, V>> get() =
        object : AbstractRefSet<AbstractRefMap.RefEntry<K, V>> {
            private val entrySet = backingMap.entries

            override val size: Int get() = entrySet.size

            override fun add(element: AbstractRefMap.RefEntry<K, V>) = entrySet.add(wrapEntry(element))

            override fun remove(element: AbstractRefMap.RefEntry<K, V>) = entrySet.remove(wrapEntry(element))

            override fun clear() = entrySet.clear()

            override fun iterator(): MutableIterator<AbstractRefMap.RefEntry<K, V>> {
                var it = entrySet.iterator()
                return object: MutableIterator<AbstractRefMap.RefEntry<K, V>> {
                    override fun hasNext() = it.hasNext()
                    override fun next() = unwrapEntry(it.next())
                    override fun remove() = it.remove()
                }
            }

            override fun retainAll(elements: RefCollection<AbstractRefMap.RefEntry<K, V>>) =
                    entrySet.retainAll( elements.mapToCollection(ArrayList()) { wrapEntry(it) })

            override fun contains(element: AbstractRefMap.RefEntry<K, V>) = entrySet.contains(wrapEntry(element))
        }
}