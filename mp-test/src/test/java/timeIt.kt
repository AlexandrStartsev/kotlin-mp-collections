package edu.alex

actual fun timeIt(block: () -> Unit): Int {
    return kotlin.system.measureTimeMillis(block).toInt()
}
