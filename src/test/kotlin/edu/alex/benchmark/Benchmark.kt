package edu.alex.benchmark

import edu.alex.*
import kotlin.test.*

class Benchmark {

    private val doBenchmarks = false

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
    fun listFilter() {
        if(!doBenchmarks) return

        val refList: RefCollection<Int> = RefList()
        val arrayList = ArrayList<Int>()
        for (i in 1..100) {
            refList.add(i)
            arrayList.add(i)
        }
        println("1000000@ ArrayList(size: 100) filter: " + timeIt {
            (0 until 1000000).forEach { arrayList.filter { it and 3 == 0 } }
        })
        println("1000000@ RefList(size: 100) filter: " + timeIt {
            (0 until 1000000).forEach { refList.filter { it and 3 == 0 } }
        })
    }

    @Test
    fun listContainsVsSetContains() {
        if(!doBenchmarks) return

        fun <K> body(iter: Int, collection: RefCollection<K>, element: K, shouldContain: Boolean) {
            (0 until iter).forEach {
                collection.contains(element)
                collection.contains(element)
                collection.contains(element)
                collection.contains(element)
                collection.contains(element)
                collection.contains(element)
                collection.contains(element)
                collection.contains(element)
                collection.contains(element)
                collection.contains(element)
            }
            if(shouldContain) assertTrue { collection.contains(element) } else assertFalse { collection.contains(element) }
        }

        fun doTest(size: Int, midBreak: Boolean = false) {
            val list = RefList<String>()
            val set = RefSet<String>()
            for (i in 1..size) {
                list.add(i.toString())
                set.add(i.toString())
            }

            val element = if(midBreak) (size/2).toString() else "x"
            val iter = if(size <= 10) 10000000 else 1000000
            val halfWay = if(midBreak) "found half way" else "not found"

            println("$iter@ RefList(size: $size) contains $halfWay " + timeIt {
                body(iter/10, list, element, midBreak)
            })

            println("$iter@ RefSet(size: $size) contains $halfWay " + timeIt {
                body(iter/10, set, element, midBreak)
            })
        }

        println("10000000@ singleton contains: " + timeIt {
            body(1000000, singleton("a"), "a", true)
        })
        doTest(1)
        doTest(10)
        doTest(10, true)
        doTest(100)
        doTest(100, true)
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
            val halfWay = if(midBreak) "fail half way" else "match"

            println("$iter@ RefList(size $size) containsAll $halfWay " + timeIt {
                (0 until iter).forEach {
                    other.all { list.contains(it) }
                }
            })

            println("$iter@ RefList(size $size) toSet and containsAll $halfWay " + timeIt {
                (0 until iter).forEach {
                    val set = RefSet(list)
                    other.all { set.contains(it) }
                }
            })
        }
        doTest(1)
        doTest(10)
        doTest(10, true)
        doTest(50)
        doTest(50, true)
    }

    @Test
    fun testSetSpeed() {
        if(!doBenchmarks) return

        val (arr, arr2) = prepareTestDate(100).let { it.first.toList() to it.second.toList() }
        val (arr10k, _) = prepareTestDate(10000).let { it.first.toList() to it.second.toList() }
        val refList = RefList(arr)
        val refList10k = RefList(arr10k)
        val indices = (0 until 1000000).map { it%100 }.toTypedArray()


        println("HashSet 100 creations from list of 10K " + timeIt {
            (0 until 100).forEach { HashSet(arr10k) }
        })
        println("RefSet 100 creations from list of 10K " + timeIt {
            (0 until 100).forEach { RefSet(refList10k) }
        })
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
            indices.forEach { set.contains( arr2[it] ) }
        })
        println("RefSet 1M contains " + timeIt {
            val set = RefSet( arr )
            indices.forEach { set.contains( arr2[it] ) }
        })
        println("HashSet 10K intersect 100x100 " + timeIt {
            val set1 = HashSet( arr )
            (0 until 1000).forEach { set1.intersect(ArrayList( arr2 )) }
        })
        println("RefSet 10K intersect 100x100 " + timeIt {
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
            assertEquals(100, ArrayList(arrayList).size)
        })
        println("RefList 100K creations from list of 100 " + timeIt {
            (0 until 100000).forEach { RefList(refList) }
            assertEquals(100, RefList(refList).size)
        })
        println("ArrayList 10K creations from set of 100 " + timeIt {
            (0 until 10000).forEach { ArrayList(hashSet) }
            assertEquals(100, ArrayList(hashSet).size)
        })
        println("RefList 10K creations from set of 100 " + timeIt {
            (0 until 10000).forEach { RefList(refSet) }
            assertEquals(100, RefList(refSet).size)
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