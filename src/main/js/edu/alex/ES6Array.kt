package edu.alex

@JsName("Array")
internal external class ES6Array<E> {
    val length: Int

    fun push(element: E)

    fun splice(start: Int): ES6Array<E>

    fun splice(start: Int, deleteCount: Int): ES6Array<E>

    fun splice(start: Int, deleteCount: Int, vararg item: E): ES6Array<E>

    operator fun get(index: Int): E

    operator fun set(index: Int, element: E): E

    fun indexOf(element: E): Int

    fun some(predicate: (E) -> Boolean): Boolean

    fun entries(): ES6Iterator<E>
}