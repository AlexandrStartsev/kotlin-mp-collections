package edu.alex

class RefMap<K, V>: MutableMap<K, V> {
    private val map = ES6Map<K, V>()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = object: MutableSet<MutableMap.MutableEntry<K, V>> {
            override val size: Int
                get() = map.size

            override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
                val before = size
                map.set(element.key, element.value)
                return before != size
            }

            override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
                val before = size
                elements.forEach { map.set(it.key, it.value) }
                return before != size
            }

            override fun clear() = map.clear()

            override fun contains(element: MutableMap.MutableEntry<K, V>) = map.get(element.key)?.let { it === element.value } ?: false

            override fun containsAll(elements: Collection<MutableMap.MutableEntry<K, V>>) = elements.all { contains(it) }

            override fun isEmpty() = size == 0

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>>
                    = IteratorHandler(map.entries(), { map.delete(it.value[0]) }, {
                val k = it.value[0]
                var v = it.value[1]
                object : MutableMap.MutableEntry<K, V> {
                    override val key: K get() = k
                    override val value: V get() = v
                    override fun setValue(newValue: V): V {
                        val old = v
                        v = newValue
                        map.set(k, newValue)
                        return old
                    }
                }
            })

            override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
                val before = size
                if(map.get(element.key) === element.value) map.delete(element.key)
                return before != size
            }

            override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
                val before = size
                elements.forEach {
                    if (map.get(it.key) === it.value) map.delete(it.key)
                }
                return before != size
            }

            override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
                val before = size
                val it = iterator()
                while (it.hasNext())
                    if(!elements.contains(it.next()))
                        it.remove()
                return before != size
            }
        }

    override val keys: MutableSet<K>
        get() = object: MutableSet<K> {
            override val size: Int get() = map.size

            override fun add(element: K) = throw UnsupportedOperationException("Add is not supported on values")

            override fun addAll(elements: Collection<K>) = throw UnsupportedOperationException("Add is not supported on values")

            override fun clear() = map.clear()

            override fun contains(element: K) = map.has(element)

            override fun containsAll(elements: Collection<K>) = elements.all { contains(it) }

            override fun isEmpty() = size == 0

            override fun iterator(): MutableIterator<K> = IteratorHandler(map.keys(), { map.delete(it.value) }, { it.value } )

            override fun remove(element: K): Boolean {
                val before = size
                map.delete(element)
                return before != size
            }

            override fun removeAll(elements: Collection<K>): Boolean {
                val before = size
                elements.forEach { map.delete(it) }
                return before != size
            }

            // TODO: at some point it s easier to re-create internal set. Idk.
            override fun retainAll(elements: Collection<K>): Boolean {
                val before = size
                map.forEach { _, it, _ -> if(!elements.contains(it)) map.delete(it) }
                return before != size
            }
        }

    override val values: MutableCollection<V>
        get() = object: MutableCollection<V> {
            override val size: Int get() = map.size

            override fun add(element: V) = throw UnsupportedOperationException("Add is not supported on values")

            override fun addAll(elements: Collection<V>) = throw UnsupportedOperationException("Add is not supported on values")

            override fun clear() = map.clear()

            override fun contains(element: V): Boolean {
                val it = map.values()
                var p = it.next()
                while(!p.done) {
                    if(p.value === element) return true
                    p = it.next()
                }
                return false
            }

            override fun containsAll(elements: Collection<V>) = elements.all { contains(it) }

            override fun isEmpty() = map.size == 0

            override fun iterator(): MutableIterator<V> = IteratorHandler(map.entries(), { map.delete(it.value[0]) }, { it.value[1] } )

            override fun remove(element: V): Boolean {
                val it = map.entries()
                var p = it.next()
                while(!p.done) {
                    if(p.value[1] === element) {
                        map.delete(p.value[0])
                        return true
                    }
                    p = it.next()
                }
                return false
            }

            override fun removeAll(elements: Collection<V>) = elements.fold(false) { prev, cur -> remove(cur) || prev }

            // TODO: at some point it s easier to re-create internal set. Idk.
            override fun retainAll(elements: Collection<V>): Boolean {
                val before = size
                map.forEach { value, key, _ -> if(!elements.contains(value)) map.delete(key) }
                return before != size
            }
        }

    override val size: Int get() = map.size

    override fun clear() {
        map.clear()
    }

    override fun containsKey(key: K) = map.has(key)

    override fun containsValue(value: V) = values.contains(value)

    override fun get(key: K): V? {
        val v = map.get(key)
        return if(jsTypeOf(v) === "undefined") null else v
    }

    override fun isEmpty() = size == 0

    override fun put(key: K, value: V): V? {
        val old = get(key)
        map.set(key, value)
        return old
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (key, value) -> map.set(key, value) }
    }

    override fun remove(key: K): V? {
        val old = get(key)
        map.delete(key)
        return old
    }
}

/*object: MutableIterator<V> {
private val cursor = map.entries()
private var last: IteratorValue<Array<dynamic>>? = null
private var head = cursor.next()

override fun hasNext() = !head.done

override fun next(): V {
if(hasNext()) {
    val tmp = head.value[1]
    last = head
    head = cursor.next()
    return if(jsTypeOf(tmp) === "undefined") _null else tmp
}
throw NoSuchElementException()
}

override fun remove() {
last?.takeIf { !it.done }?.let {
    map.delete(it.value[0])
    last = null
    return@remove
}
throw IllegalStateException()
}
}






                    object : MutableIterator<K> {
                private val cursor = map.keys()
                private var last: IteratorValue<K>? = null
                private var head = cursor.next()

                override fun hasNext() = !head.done

                override fun next(): K {
                    if(hasNext()) {
                        val tmp = head.value
                        last = head
                        head = cursor.next()
                        return if(jsTypeOf(tmp) === "undefined") _null else tmp
                    }
                    throw NoSuchElementException()
                }

                override fun remove() {
                    last?.takeIf { !it.done }?.let {
                        map.delete(it.value)
                        last = null
                        return@remove
                    }
                    throw IllegalStateException()
                }
            }


                    = object: MutableIterator<MutableMap.MutableEntry<K, V>> {
                private val cursor = map.entries()
                private var last: IteratorValue<Array<dynamic>>? = null
                private var head = cursor.next()

                override fun hasNext() = !head.done

                override fun next(): MutableMap.MutableEntry<K, V> {
                    if(hasNext()) {
                        var k = head.value[0]
                        var v = head.value[1]
                        last = head
                        head = cursor.next()
                        return object: MutableMap.MutableEntry<K, V> {
                            override val key: K get() = if(jsTypeOf(k) === "undefined") _null else k

                            override val value: V get() = if(jsTypeOf(v) === "undefined") _null else v

                            override fun setValue(newValue: V): V {
                                val old = v
                                v = newValue
                                map.set(k, newValue)
                                return old
                            }
                        }
                    }
                    throw NoSuchElementException()
                }

                override fun remove() {
                    last?.takeIf { !it.done }?.let {
                        map.delete(it.value[0])
                        last = null
                        return@remove
                    }
                    throw IllegalStateException()
                }
            }
*/
