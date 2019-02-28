package edu.alex

import java.util.LinkedHashMap
import edu.alex.ObjectReference.Companion.wrap

/**
 * I was on the fence whether keep values as plain object (and have .values() return Collection instead of RefCollection),
 * or wrap it.
 * It wouldn't be a problem from performance perspective as both jvm and js versions can have fast .keys().contains
 * and .entries().contains based on map key.
 *
 * If somebody wants to put objects as values and compare by reference there s always an option to override equals,
 * and certainly wrapping and unwrapping value takes toll.
 *
 * But in a same time it would be somewhat confusing to have keys compared by reference and values by .equals
 * Also, JS side would have to account for values .equals to remain consistent
 *
 */

class RefMap<K, V>: AbstractRefMap<K, V> {
    private companion object {
        fun <K, V> wrapEntry(e: AbstractRefMap.RefEntry<K, V>): MutableMap.MutableEntry<ObjectReference<K>, ObjectReference<V>> {
            return object : MutableMap.MutableEntry<ObjectReference<K>, ObjectReference<V>> {
                override val key = wrap(e.key)
                override val value: ObjectReference<V> get() = wrap(e.value)
                override fun setValue(value: ObjectReference<V>): ObjectReference<V> = wrap(e.setValue(value.get()))
            }
        }
    }

    private fun unwrapEntry(e: MutableMap.MutableEntry<ObjectReference<K>, ObjectReference<V>>): AbstractRefMap.RefEntry<K, V> {
        var lastValue = e.value.get()
        return object : AbstractRefMap.RefEntry<K, V> {
            override val key = e.key.get()
            override val value: V get() = lastValue

            override fun setValue(newValue: V): V {
                val tmp = lastValue
                lastValue = newValue
                backingMap[e.key] = wrap(newValue)
                return tmp
            }
        }
    }


    private val backingMap = LinkedHashMap<ObjectReference<K>, ObjectReference<V>>()

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

    override fun containsValue(value: V) = backingMap.containsValue(wrap(value))

    override fun get(key: K?): V? = backingMap[wrap(key)]?.get()

    override fun put(key: K, value: V): V? = backingMap.put(wrap(key), wrap(value))?.get()

    override fun remove(key: K?): V? = backingMap.remove(wrap(key))?.get()

    override val values: RefCollection<V> get() =
        object : RefCollection<V> {
            val values = backingMap.values
            override val size get() = values.size

            override fun clear() = backingMap.clear()

            override fun contains(element: V) = values.any { it === element }

            override fun iterator() = values.iterator().let {
                object : MutableIterator<V> {
                    override fun hasNext() = it.hasNext()
                    override fun next() = it.next().get()
                    override fun remove() = it.remove()
                }
            }

            override fun remove(element: V) = values.remove(wrap(element))
        }

    override val keys: AbstractRefSet<K> get() =
        object : AbstractRefSet<K> {
            override val size: Int get() = backingMap.size

            override fun iterator() = backingMap.keys.iterator().let {
                object : MutableIterator<K> {
                    override fun hasNext() = it.hasNext()
                    override fun next() = it.next().get()
                    override fun remove() = it.remove()
                }
            }

            override fun contains(element: K) = backingMap.containsKey(wrap(element))

            override fun remove(element: K): Boolean {
                val w = wrap(element)
                return when (backingMap.containsKey(w)) {
                    true -> {
                        backingMap.remove(w)
                        true
                    }
                    else -> false
                }
            }

            override fun clear() = backingMap.clear()
        }

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

            override fun contains(element: AbstractRefMap.RefEntry<K, V>) = entrySet.contains(wrapEntry(element))
        }

    override fun forEach(action: (V, K) -> Unit) {
        backingMap.forEach { refValue, refKey -> action(refKey.get(), refValue.get()) }
    }
}