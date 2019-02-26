package edu.alex

@JsName("Map")
internal external class ES6Map<K, V> {
    fun clear()

    fun delete(key: K?): Boolean

    /** key = iterator.next().value[0], value = iterator.next().value[1]  */
    fun entries(): ES6Iterator<Array<dynamic>>

    fun forEach(callback: (value: V, key: K, map: ES6Map<K, V>) -> Any)

    fun get(key: K?): V?

    fun has(key: K?): Boolean

    fun keys(): ES6Iterator<K>

    fun set(key: K, value: V): ES6Map<K, V>

    fun values(): ES6Iterator<V>

    val size: Int
}