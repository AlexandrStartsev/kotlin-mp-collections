package edu.alex.benchmark

import edu.alex.*
import kotlin.test.Test

class Benchmark {

    private val doBenchmarks = true //false

    private fun makeRandomString(size: Int = 20): String {
        val all = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (0 until size).map { (0 until all.length).random() }.fold("") { a, b -> a + all[b] }
    }

    private fun prepareTestDate(size: Int, stringSize: Int = 20): Pair<Array<String>, Array<String>> {
        val arr = (0 until size).map { makeRandomString(stringSize) }
        val quarter = size/4
        val arr2 = arr.subList(quarter, size - quarter) + (0 until size/2).map { makeRandomString(stringSize) }
        return arr.toTypedArray() to arr2.toTypedArray()
    }

    @Test
    fun listContainsAllVsToSet() {
        if(!doBenchmarks) return

        fun doTest(size: Int, midBreak: Boolean = false) {
            val list = RefList<String>()
            for (i in 1..size) list.add(i.toString())

            val other = RefList(list)

            if(midBreak)
                other[size/2 + 1] = "x" // assuming on average middle element doesn't match

            val iter = if(size < 5) 1000000 else 100000

            println("$size: $iter contains " + timeIt {
                (0 until iter).forEach {
                    other.all { list.contains(it) }
                }
            })

            println("$size: $iter toSet and contains " + timeIt {
                (0 until iter).forEach {
                    val set = RefSet(list)
                    other.all { set.contains(it) }
                }
            })
        }
        doTest(1)
        doTest(3)
        doTest(3, true)
        doTest(6)
        doTest(6, true)
        doTest(10)
        doTest(10, true)
        doTest(50)
        doTest(50, true)
    }

    @Test
    fun testSetSpeed() {
        if(!doBenchmarks) return

        val (arr, arr2) = prepareTestDate(100).let { it.first.toList() to it.second.toList() }
        val refList = RefList(arr)


        println("HashSet 10K creations from list of 100 " + timeIt {
            (0 until 10000).forEach { HashSet(arr) }
        })
        println("RefSet 10K creations from list of 100 " + timeIt {
            (0 until 10000).forEach { RefSet(refList) }
        })
        println("HashSet 10K insertions of 100 " + timeIt {
            (0 until 10000).forEach { val set = HashSet<String>(); set.addAll (arr) }
        })
        println("RefSet 10K insertions of 100 " + timeIt {
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

    @Test
    fun testListSpeed() {
        if(!doBenchmarks) return

        val (arrayList, _) = prepareTestDate(100).let { it.first.toList() to it.second.toList() }
        val refList = RefList(arrayList)

        val hashSet = HashSet(arrayList)
        val refSet = RefSet(refList)

        println("ArrayList 100K creations from list of 100 " + timeIt {
            (0 until 100000).forEach { ArrayList(arrayList) }
        })
        println("RefList 100K creations from list of 100 " + timeIt {
            (0 until 100000).forEach { RefList(refList) }
        })
        println("ArrayList 10K creations from set of 100 " + timeIt {
            (0 until 10000).forEach { ArrayList(hashSet) }
        })
        println("RefList 10K creations from set of 100 " + timeIt {
            (0 until 10000).forEach { RefList(refSet) }
        })
        println("ArrayList 10K insertions of 100 elements " + timeIt {
            (0 until 10000).forEach { val lst = ArrayList<Int>(); for (i in 1..100) lst.add(i) }
        })
        println("RefList 10K insertions of 100 elements " + timeIt {
            (0 until 10000).forEach { val lst = RefList<Int>(); for (i in 1..100) lst.add(i) }
        })
        println("ArrayList 10K insertions + removals of 100 elements " + timeIt {
            (0 until 10000).forEach { val lst = ArrayList<Int>(); for (i in 1..100) lst.add(i); for (i in 2..100) lst.remove(1) }
        })
        println("RefList 10K insertions + removals of 100 elements " + timeIt {
            (0 until 10000).forEach { val lst = RefList<Int>(); for (i in 1..100) lst.add(i); for (i in 2..100) lst.remove(1) }
        })
        println("ArrayList 1M contains " + timeIt {
            val lst = ArrayList<Int>(); for (i in 1..100) lst.add(i)
            (0 until 1000000).forEach { lst.contains(53) }
        })
        println("RefList 1M contains " + timeIt {
            val lst = RefList<Int>(); for (i in 1..100) lst.add(i)
            (0 until 1000000).forEach { lst.contains(53) }
        })
        // Now how about string contains ?
        println("ArrayList 1M contains (String) " + timeIt {
            val lst = ArrayList<String>(); for (i in 1..100) lst.add(i.toString())
            (0 until 1000000).forEach { lst.contains("53") }
        })
        println("RefList 1M contains (String) " + timeIt {
            val lst = RefList<String>(); for (i in 1..100) lst.add(i.toString())
            (0 until 1000000).forEach { lst.contains("53") }
        })
    }

}