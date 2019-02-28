package edu.alex

@JsName("Set")
internal external class ES6Set<K> {
    constructor()

    constructor(anythingGoes: dynamic)

    val size: Int

    fun add(key: K): ES6Set<K>

    fun clear()

    fun delete(key: K?): Boolean

    fun entries(): ES6Iterator<Array<K>>

    fun forEach(callback: (value: K, key: K, set: ES6Set<K>) -> Any)

    fun has(key: K?): Boolean

    fun values(): ES6Iterator<K>

    fun forEach(action:(K) -> Any)
}