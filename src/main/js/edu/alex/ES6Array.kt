package edu.alex

@JsName("Array")
internal external class ES6Array<E> {
    val length: Int

    fun push(element: E)

    fun slice(start: Int, end: Int = definedExternally): ES6Array<E>

    fun splice(start: Int, deleteCount: Int = definedExternally, vararg item: E): ES6Array<E>

    fun indexOf(element: E): Int

    fun some(predicate: (E) -> Boolean): Boolean

    fun entries(): ES6Iterator<E>

    companion object {
        fun <E> from(anythingGoes: dynamic): ES6Array<E>
    }

    fun forEach(action:(E) -> Any)

    fun forEach(action:(E, Int) -> Any)

    fun filter(predicate: (E) -> Boolean): ES6Array<E>
}