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

    fun contains(key: K, value: V): Boolean = when(value) {
        null -> containsKey(key) && get(key) === null
        else -> get(key)?.let { it === value } ?: false
    }

    fun contains(entry: RefEntry<K, V>): Boolean = contains(entry.key, entry.value)

    fun contains(pair: Pair<K, V>): Boolean = contains(pair.first, pair.second)

    fun iterator(): MutableIterator<RefEntry<K, V>> = entries.iterator()

    operator fun set(key: K, value: V) { put(key, value) }

    fun getOrDefault(key: K?, defaultValue: @UnsafeVariance V): V = get(key) ?: defaultValue

    fun getOrPut(key: K, supplier: () -> V): V = get(key) ?: { val value = supplier(); put(key, value); value }()

    fun putAll(from: Map<K, V>) = from.forEach { put(it.key, it.value) }

    fun putAll(from: AbstractRefMap<K, V>) = from.forEach { value, key -> put(key, value) }

    fun delete(key: K?) { remove(key) }

    fun forEach(action: (V, K) -> Unit)
}
