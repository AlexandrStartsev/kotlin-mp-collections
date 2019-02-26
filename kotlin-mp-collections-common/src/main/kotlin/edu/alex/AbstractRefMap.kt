package edu.alex

interface AbstractRefMap<K, V> {

    interface RefEntry<K, V> {
        val key: K
        val value: V
        fun setValue(newValue: V): V
        val pair get() = key to value
    }

    val size: Int

    fun containsKey(key: K?): Boolean

    fun containsValue(value: @UnsafeVariance V): Boolean

    operator fun get(key: K?): V?

    val keys: AbstractRefSet<K>

    val values: RefCollection<V>

    val entries: AbstractRefSet<RefEntry<K, V>>

    fun put(key: K, value: V): V?

    fun remove(key: K?): V?

    fun clear()

    // ===
    fun isEmpty() = size == 0

    fun containsEntry(key: K?, value: V?): Boolean = get(key)?.let { it === value } ?: false

    fun containsEntry(entry: RefEntry<K, V>): Boolean = containsEntry(entry.key, entry.value)

    fun containsEntry(pair: Pair<K, V>): Boolean = containsEntry(pair.first, pair.second)

    fun iterator(): MutableIterator<RefEntry<K, V>> = entries.iterator()

    operator fun set(key: K, value: V) { put(key, value) }

    fun getOrDefault(key: K?, defaultValue: @UnsafeVariance V): V = get(key) ?: defaultValue

    fun getOrPut(key: K, supplier: () -> V): V = get(key) ?: { val value = supplier(); put(key, value); value }()

    fun putAll(from: Map<K, V>) = from.forEach { put(it.key, it.value) }

    fun putAll(from: AbstractRefMap<K, V>) = from.forEach { put(it.key, it.value) }

    fun forEach(action: (RefEntry<K, V>) -> Unit) { for(entry in iterator()) action(entry) }

    fun delete(key: K?) { remove(key) }
}
