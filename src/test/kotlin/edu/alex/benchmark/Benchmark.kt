package edu.alex.benchmark

import edu.alex.RefList
import edu.alex.RefSet
import edu.alex.addAll
import edu.alex.intersect
import kotlin.test.Test

class Benchmark {

    private fun makeRandomString(size: Int = 20): String {
        val all = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (0 until size).map { (0 until all.length).random() }.fold("") { a, b -> a + all[b] }
    }

    @Test
    fun testSpeed() {
        val arr = (0 until 100).map { makeRandomString() }
        val arr2 = arr.subList(20, 70) + (0 until 50).map { makeRandomString() }

        println("HashSet 1M insertions " + timeIt {
            (0 until 10000).forEach { val set = HashSet<String>(); set.addAll (arr) }
        })
        println("RefSet 1M insertions " + timeIt {
            (0 until 10000).forEach { val set = RefSet<String>(); set.addAll (arr) }
        })
        println("HashSet 1M contains " + timeIt {
            val set = HashSet( arr )
            (0 until 1000000).forEach { set.contains(arr[it%100] ) }
        })
        println("RefSet 1M contains " + timeIt {
            val set = RefSet( arr )
            (0 until 1000000).forEach { set.contains(arr[it%100] ) }
        })
        println("HashSet 10K intersect " + timeIt {
            val set1 = HashSet( arr )
            (0 until 1000).forEach { set1.intersect(ArrayList( arr2 )) }
        })
        println("RefSet 10K intersect " + timeIt {
            val set1 = RefSet( arr )
            (0 until 1000).forEach { set1.intersect(RefList( arr2 )) }
        })
    }

}