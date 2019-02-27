package edu.alex.benchmark

actual fun timeIt(block: () -> Unit): Int {
    return kotlin.system.measureTimeMillis(block).toInt()
}
