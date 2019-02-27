package edu.alex.benchmark

actual fun timeIt(block: () -> Unit): Int {
    val cur = js("+new Date()")
    block()
    return js("+new Date()") - cur
}

