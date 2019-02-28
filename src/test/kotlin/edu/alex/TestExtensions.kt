package edu.alex

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestExtensions {

    @Test
    fun collectionEqualsOrdered() {
        assertTrue {
            RefList<String>().collectionEqualsOrdered(RefList())
        }
        assertTrue {
            RefList<String>().collectionEqualsOrdered(RefSet())
        }
        assertFalse {
            RefList("1", "3", "2").collectionEqualsOrdered(RefSet("1", "2", "3"))
        }
        assertTrue {
            RefList("1", "2", "3").collectionEqualsOrdered(RefSet("1", "2", "3"))
        }
        assertTrue {
            RefSet("1", "2", "3").collectionEqualsOrdered(RefSet("1", "2", "3"))
        }
        assertFalse {
            RefSet("1", "3", "2").collectionEqualsOrdered(RefSet("1", "2", "3"))
        }

    }
}