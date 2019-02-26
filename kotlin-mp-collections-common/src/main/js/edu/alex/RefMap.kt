package edu.alex

class RefMap<K, V>: AbstractRefMap<K, V> {
    private val map = ES6Map<K, V>()

    constructor()

    constructor(pairs: Collection<Pair<K, V>>) {
        pairs.forEach { put(it.first, it.second) }
    }

    constructor(pairs: RefCollection<Pair<K, V>>) {
        pairs.forEach { put(it.first, it.second) }
    }

    constructor(vararg pairs: Pair<K, V>) {
        for(it in pairs) put(it.first, it.second)
    }

    /**
     * Javascript Map's "entries" produced by iterator.next() are created as standalone
     * objects (Array[key, value]). Direct modification on such entry will not reflect on map, and vice versa.
     *
     * Since "setValue" is important, I'll be writing value to map, but changes in map will not reflect on entry
     */

    internal inner class RefMapEntry(override val key: K, var lastValue: V): AbstractRefMap.RefEntry<K, V> {
        override val value: V get() = lastValue
        override fun setValue(newValue: V): V {
            val tmp = lastValue
            put(key, newValue)
            lastValue = newValue
            return tmp
        }
    }

    private val createEntry: (it: IteratorValue<Array<dynamic>>) -> AbstractRefMap.RefEntry<K, V> = { RefMapEntry(it.value[0], it.value[1]) }

    override val entries: AbstractRefSet<AbstractRefMap.RefEntry<K, V>>
        get() = object: AbstractRefSet<AbstractRefMap.RefEntry<K, V>> {
            override val size: Int
                get() = map.size

            override fun add(element: AbstractRefMap.RefEntry<K, V>) = throw UnsupportedOperationException("Add is not supported on values")

            override fun clear() = map.clear()

            override fun contains(element: AbstractRefMap.RefEntry<K, V>) = map.get(element.key)?.let { it === element.value } ?: false

            override fun isEmpty() = size == 0

            override fun iterator(): MutableIterator<AbstractRefMap.RefEntry<K, V>> =
                    IteratorHandler(map.entries(), { map.delete(it.value[0]) }, createEntry)

            override fun remove(element: AbstractRefMap.RefEntry<K, V>) =
                    if(map.get(element.key) === element.value) map.delete(element.key) else false

            override fun retainAll(elements: RefCollection<AbstractRefMap.RefEntry<K, V>>): Boolean {
                // we don't want to make a refset out of elements because we'll be comparing references to Entry, not keys
                val c = RefMap<K, V>()
                elements.forEach { c.map.set(it.key, it.value) }
                val before = size
                val it = iterator()
                while (it.hasNext())
                    if (!c.containsEntry(it.next()))
                        it.remove()
                return before != size
            }
        }

    override val keys: AbstractRefSet<K>
        get() = object: AbstractRefSet<K> {
            override val size: Int get() = map.size

            override fun add(element: K) = throw UnsupportedOperationException("Add is not supported on values")

            override fun clear() = map.clear()

            override fun contains(element: K) = map.has(element)

            override fun iterator(): MutableIterator<K> = IteratorHandler(map.keys(), { map.delete(it.value) }, { it.value } )

            override fun remove(element: K) = map.delete(element)

            override fun retainAll(elements: RefCollection<K>): Boolean {
                val c = if(elements is AbstractRefSet) elements else RefSet(elements)
                val before = size
                map.forEach { _, it, _ -> if(!c.contains(it)) map.delete(it) }
                return before != size
            }
        }

    override val values: RefCollection<V>
        get() = object: RefCollection<V> {
            override val size: Int get() = map.size

            override fun add(element: V) = throw UnsupportedOperationException("Add is not supported on values")

            override fun clear() = map.clear()

            override fun contains(element: V) = entries.any { it.value === element }

            override fun isEmpty() = map.size == 0

            override fun iterator(): MutableIterator<V> = IteratorHandler(map.entries(), { map.delete(it.value[0]) }, { it.value[1] } )

            override fun remove(element: V) = entries.first { it.value === element }?.let { map.delete(it.key) } ?: false

            override fun retainAll(elements: RefCollection<V>): Boolean {
                val c = if(elements is AbstractRefSet) elements else RefSet(elements)
                val before = size
                map.forEach { value, key, _ -> if(!c.contains(value)) map.delete(key) }
                return before != size
            }
        }

    override val size: Int get() = map.size

    override fun clear() = map.clear()

    override fun containsKey(key: K?) = map.has(key)

    override fun containsValue(value: V) = values.contains(value)

    override fun get(key: K?): V? {
        val v = map.get(key)
        return if(jsTypeOf(v) === "undefined") null else v
    }

    override fun isEmpty() = size == 0

    override fun put(key: K, value: V): V? {
        val old = get(key)
        map.set(key, value)
        return old
    }

    override fun remove(key: K?): V? {
        val old = get(key)
        map.delete(key)
        return old
    }

    override fun set(key: K, value: V) {
        map.set(key, value)
    }

    override fun delete(key: K?) {
        map.delete(key)
    }
}