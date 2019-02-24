package edu.alex

class RefMap<K, V>: MutableMap<K, V> {
    private val map = ES6Map<K, V>()

    constructor()

    private constructor(elements: Collection<MutableMap.MutableEntry<K, V>>) {
        elements.forEach { put(it.key, it.value) }
    }

    /**
     * Javascript Map's "entries" produced by iterator.next() are created as standalone
     * objects (Array[key, value]). Direct modification on such entry will not reflect on map, and vice versa.
     *
     * Since "setValue" is important, I'll be writing value to map, but changes in map will not reflect on entry
     */

    internal inner class RefMapEntry(override val key: K, var lastValue: V): MutableMap.MutableEntry<K, V> {
        override val value: V get() = lastValue
        override fun setValue(newValue: V): V {
            val tmp = lastValue
            put(key, newValue)
            lastValue = newValue
            return tmp
        }
    }

    private val createEntry: (it: IteratorValue<Array<dynamic>>) -> MutableMap.MutableEntry<K, V> = { RefMapEntry(it.value[0], it.value[1]) }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = object: MutableSet<MutableMap.MutableEntry<K, V>> {
            override val size: Int
                get() = map.size

            override fun add(element: MutableMap.MutableEntry<K, V>) = throw UnsupportedOperationException("Add is not supported on values")

            override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>) = elements.fold(false) { prev, cur -> add(cur) || prev }

            override fun clear() = map.clear()

            override fun contains(element: MutableMap.MutableEntry<K, V>) = map.get(element.key)?.let { it === element.value } ?: false

            override fun containsAll(elements: Collection<MutableMap.MutableEntry<K, V>>) = elements.all { contains(it) }

            override fun isEmpty() = size == 0

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> =
                    IteratorHandler(map.entries(), { map.delete(it.value[0]) }, createEntry)

            override fun remove(element: MutableMap.MutableEntry<K, V>) =
                    if(map.get(element.key) === element.value) map.delete(element.key) else false

            override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>) = elements.fold(false) { prev, cur -> remove(cur) || prev }

            override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
                val c = RefMap(elements).entries
                val before = size
                val it = iterator()
                while (it.hasNext())
                    if(!c.contains(it.next()))
                        it.remove()
                return before != size
            }
        }

    override val keys: MutableSet<K>
        get() = object: MutableSet<K> {
            override val size: Int get() = map.size

            override fun add(element: K) = throw UnsupportedOperationException("Add is not supported on values")

            override fun addAll(elements: Collection<K>) = elements.fold(false) { prev, cur -> add(cur) || prev }

            override fun clear() = map.clear()

            override fun contains(element: K) = map.has(element)

            override fun containsAll(elements: Collection<K>) = elements.all { contains(it) }

            override fun isEmpty() = size == 0

            override fun iterator(): MutableIterator<K> = IteratorHandler(map.keys(), { map.delete(it.value) }, { it.value } )

            override fun remove(element: K) = map.delete(element)

            override fun removeAll(elements: Collection<K>) = elements.fold(false) { prev, cur -> remove(cur) || prev }

            override fun retainAll(elements: Collection<K>): Boolean {
                val c = if(elements is RefSet) elements else RefSet(elements)
                val before = size
                map.forEach { _, it, _ -> if(!c.contains(it)) map.delete(it) }
                return before != size
            }
        }

    override val values: MutableCollection<V>
        get() = object: MutableCollection<V> {
            override val size: Int get() = map.size

            override fun add(element: V) = throw UnsupportedOperationException("Add is not supported on values")

            override fun addAll(elements: Collection<V>) = elements.fold(false) { prev, cur -> add(cur) || prev }

            override fun clear() = map.clear()

            override fun contains(element: V) = entries.any { it.value === element }

            override fun containsAll(elements: Collection<V>) = elements.all { contains(it) }

            override fun isEmpty() = map.size == 0

            override fun iterator(): MutableIterator<V> = IteratorHandler(map.entries(), { map.delete(it.value[0]) }, { it.value[1] } )

            override fun remove(element: V) = entries.first { it.value === element }?.let { map.delete(it.key) }

            override fun removeAll(elements: Collection<V>) = elements.fold(false) { prev, cur -> remove(cur) || prev }

            override fun retainAll(elements: Collection<V>): Boolean {
                val c = if(elements is RefSet) elements else RefSet(elements)
                val before = size
                map.forEach { value, key, _ -> if(!c.contains(value)) map.delete(key) }
                return before != size
            }
        }

    override val size: Int get() = map.size

    override fun clear() = map.clear()

    override fun containsKey(key: K) = map.has(key)

    override fun containsValue(value: V) = values.contains(value)

    override fun get(key: K): V? {
        val v = map.get(key)
        return if(jsTypeOf(v) === "undefined") null else v
    }

    override fun isEmpty() = size == 0

    override fun put(key: K, value: V): V? {
        val old = get(key)
        map.set(key, value)
        return old
    }

    override fun putAll(from: Map<out K, V>) = from.forEach { (key, value) -> set(key, value) }

    override fun remove(key: K): V? {
        val old = get(key)
        map.delete(key)
        return old
    }

    fun set(key: K, value: V): RefMap<K, V> {
        map.set(key, value)
        return this
    }

    fun delete(key: K?): RefMap<K, V> {
        map.delete(key)
        return this
    }
}