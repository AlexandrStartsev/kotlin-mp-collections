package edu.alex

import kotlin.test.*

class TestMapEntries {

    @Test
    fun testEntriesIterate() {
        val map1 = RefMap("1" to "a", "2" to "b")
        val map2 = RefMap("3" to "c", "2" to "b")

        assertEquals("b", map2.entries.filter { it.key != "3" }.first()?.value)

        map2.entries.iterator().let {
            it.next()
            it.remove()
        }

        assertEquals("b", map2["2"])
    }

    @Test
    fun testEntriesContainsPrimitives() {
        val map1 = RefMap("1" to "a", "2" to "b")
        val map2 = RefMap("3" to "c", "2" to "b")

        map2.entries.iterator().let {
            it.next()
            it.remove()
        }

        assertEquals("b", map2.entries.filter { it.key != "3" }.first()?.value)
        assertEquals("b", map2["2"])

        val entry = map2.entries.first()!!

        assertTrue { map1.entries.contains(entry) }
        assertTrue { map2.entries.contains(map1.entries.filter { it.key != "1" }.first()!!) }
    }

    @Test
    fun testEntriesContainsNonPrimitives() {
        data class T(val a: String)
        val t1 = T("1")

        val map1 = RefMap(t1 to "a")
        val map2 = RefMap(T("1") to "a")
        val map3 = RefMap(t1 to "a")

        assertFalse { map1.entries.contains(map2.entries.first()!!) }
        assertTrue { map1.entries.contains(map3.entries.first()!!) }

        val t2 = T("2")
        val map4 = RefMap(t1 to t2)
        val map5 = RefMap(t1 to t2)
        val map6 = RefMap(t1 to T("2"))

        assertTrue { map4.entries.contains(map5.entries.first()!!) }
        assertFalse { map4.entries.contains(map6.entries.first()!!) }
    }

    @Test
    fun testEntriesRemovePrimitives() {
        val map1 = RefMap("1" to "a", "2" to "b", "3" to "c")
        val map2 = RefMap("3" to "c", "2" to "b", "4" to "d")


        map1.entries.remove(map2.entries.first { (key, value) -> key != "3"}!!)

        assertEquals(2, map1.size)
        assertEquals(2, map1.values.size)

        assertTrue { map2.entries.retainAll(map1.entries) }
        assertFalse { map2.entries.retainAll(map1.entries) }

        assertEquals("c", map2.values.first()!!)
        assertEquals("c", map2["3"])
        assertEquals(1, map2.size)
    }

    @Test
    fun testEntriesRemoveNonPrimitives() {
        data class T(val a: Int? = 1)
        val t1 = T()
        val t2 = T()
        val t3 = T()
        val t4 = T()
        var map1 = RefMap(t1 to t1, t2 to t2, t3 to t3)
        val map2 = RefMap(t1 to t1, t2 to t2, t3 to t4)


        assertTrue { map1.entries.removeAll(map2.entries) }
        assertFalse { map1.entries.removeAll(map2.entries) }
        assertEquals(1, map1.size)
        assertSame(t3, map1[t3])
        assertNotSame(t4, map1[t3])

        map1 = RefMap(t1 to t1, t2 to t2, t3 to t3)
        assertTrue { map1.containsKey(t1) }

        assertTrue { map1.entries.remove(map2.entries.first()!!) }
        assertFalse { map1.entries.remove(map2.entries.first()!!) }

        assertFalse { map1.containsKey(t1) }
    }

    @Test
    fun testEntryOutsideModification() {
        val map = RefMap<String, String>()
        map.put("1", "a")
        map.put("2", "b")

        val entry = map.entries.filter { it.key == "2" }.first()

        assertEquals("b", entry?.value)
        assertEquals("b", map.put("2", "c"))

        assertEquals("b", entry?.value)
        assertEquals("b", entry?.setValue("e"))
        assertEquals("e", entry?.value)
        assertEquals("e", map["2"])

        map["2"] = "f"
        assertEquals("e", entry?.value)

        map.remove("2")
        assertEquals("e", entry?.value)
        assertEquals("e", entry?.setValue("f"))
        assertEquals("f", map["2"])

        /*
            Behavior below is a natural behavior of java HashMap and is
                different for javascript map (see javascript's RefMap.RefMapEntry notes)

                Hence, javascript behavior needs to be emulated for java version
            // entry is attached
            assertEquals("b", entry.value)
            assertEquals("b", map.put("2", "c"))

            // modification to entry affects map, and value returned corresponds to last map's value, not entry's
            assertEquals("c", entry.setValue("d"))
            assertEquals("d", map["2"])

            map.remove("2")

            // entry detached from map but still can be operated upon
            assertEquals("d", entry.value)
            assertNull(map["2"])



            assertEquals("d", entry.setValue("e"))
            assertEquals("e", entry.value)

            // no longer in map
            assertNull(map["2"])


            // adding new entry with same key to map doesn't reflect on detached entry
            map.put("2", "q")
            assertEquals("e", entry.value)

            // and neither modification to detached entry affects the map
            entry.setValue("f")
            assertEquals("q", map["2"])

            map.put("2", "f")
            entry.setValue("g")
            assertEquals("f", map["2"])
        */

        /* For compatibility, both JS and JVM version of Entry.setValue will be uni-directional
         It will update map value, but changes to map value will not reflect on entry
         */

    }
}