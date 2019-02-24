package edu.alex

import java.util.LinkedHashMap
import edu.alex.ObjectReference.wrap

class RefMap<K, V>: MutableMap<K, V> {

    private companion object {
        fun <K, V> wrapEntry(e: MutableMap.MutableEntry<K, V>): MutableMap.MutableEntry<ObjectReference<K>, V> {
            return object : MutableMap.MutableEntry<ObjectReference<K>, V> {
                override val key = wrap<K>(e.key)
                override val value: V get() = e.value
                override fun setValue(value: V): V = e.setValue(value)
            }
        }
    }

    private fun unwrapEntry(e: MutableMap.MutableEntry<ObjectReference<K>, V>): MutableMap.MutableEntry<K, V> {
        var lastValue = e.value
        return object : MutableMap.MutableEntry<K, V> {
            override val key = e.key.get()
            override val value: V get() = lastValue

            override fun setValue(value: V): V {
                val tmp = lastValue
                lastValue = value
                backingMap[e.key] = value
                return tmp
            }
        }
    }


    private val backingMap = LinkedHashMap<ObjectReference<K>, V>()
    private val entrySet = backingMap.entries

    override val size: Int get() = backingMap.size

    override fun clear() = backingMap.clear()

    override fun containsKey(key: K) = backingMap.containsKey(wrap(key))

    override fun containsValue(value: V) = backingMap.containsValue(value)

    override fun get(key: K): V? = backingMap[wrap(key)]

    override fun isEmpty() = backingMap.isEmpty()

    override fun put(key: K, value: V): V? = backingMap.put(wrap(key), value)

    override fun putAll(from: Map<out K, V>) = from.forEach { (key, value) -> put(key, value) }

    override fun remove(key: K): V? = backingMap.remove(wrap(key))

    override val values: MutableCollection<V> get() = backingMap.values

    override val keys: MutableSet<K> get() = RefSet(backingMap.keys)

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() =
        object : MutableSet<MutableMap.MutableEntry<K, V>> {
            override val size: Int get() = entrySet.size

            override fun add(element: MutableMap.MutableEntry<K, V>) = entrySet.add(wrapEntry(element))

            override fun remove(element: MutableMap.MutableEntry<K, V>) = entrySet.remove(wrapEntry(element))

            override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>) =
                    elements.fold(false) { anyChange, e -> add(e) || anyChange }

            override fun clear() = entrySet.clear()

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
                var it = entrySet.iterator()
                return object: MutableIterator<MutableMap.MutableEntry<K, V>> {
                    override fun hasNext() = it.hasNext()
                    override fun next() = unwrapEntry(it.next())
                    override fun remove() = it.remove()
                }
            }

            override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>) =
                    elements.fold(false) { anyChange, e -> remove(e) || anyChange }

            override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>) =
                    entrySet.retainAll( elements.map { wrapEntry(it) })

            override fun contains(element: MutableMap.MutableEntry<K, V>) = entrySet.contains(wrapEntry(element))

            override fun containsAll(elements: Collection<MutableMap.MutableEntry<K, V>>) = elements.all { contains(it) }

            override fun isEmpty() = size == 0
        }

    fun set(key: K, value: V): RefMap<K, V> {
        put(key, value)
        return this
    }

    fun delete(key: K?): RefMap<K, V> {
        remove(key)
        return this
    }
}