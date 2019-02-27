package edu.alex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TestMap {

    @Test
    fun testMap() {
        val nm = RefMap<String, String>()

        nm.put("1", "2")

        assertEquals("2", nm["1"])

    }

    @Test
    fun testEntrySetAdd() {
        val map = RefMap<String, String>()

        assertFailsWith(UnsupportedOperationException::class) {
            map.entries.add(object: AbstractRefMap.RefEntry<String, String> {
                override val key = "1"
                override val value = "2"
                override fun setValue(newValue: String) = "-"
            })
        }

        assertFailsWith(UnsupportedOperationException::class) {
            map.keys.add("123")
        }

        assertFailsWith(UnsupportedOperationException::class) {
            map.values.add("123")
        }
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