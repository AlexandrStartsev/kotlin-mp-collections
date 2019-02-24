package edu.alex;

import static edu.alex.ObjectReference.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RefMap<K, V> implements Map<K, V> {

    private final static <K, V> Entry<ObjectReference<K>, V> wrapEntry(Entry<K, V> e) {
        return new AbstractMap.SimpleEntry<>(wrap(e.getKey()), e.getValue());
    }

    private final static <K, V> Entry<K, V> unwrapEntry(Entry<ObjectReference<K>, V> e) {
        return new AbstractMap.SimpleEntry<>(e.getKey().get(), e.getValue());
    }

    private final static <K, V> Stream<Entry<ObjectReference<K>, V>> toWrappedStream(Collection<?>  c) {
        return c.stream()
                .filter(e -> e instanceof Entry)
                .map(e -> wrapEntry((Entry)e));
    }


    private final Map<ObjectReference<K>, V> backingMap = new LinkedHashMap<>();

    @Override
    public int size() {
        return backingMap.size();
    }

    @Override
    public boolean isEmpty() {
        return backingMap.isEmpty();
    }

    @Override
    public V remove(Object key) {
        return backingMap.remove(wrap(key));
    }

    @Override
    public V get(Object key) {
        return backingMap.get(wrap(key));
    }

    @Override
    public boolean containsKey(Object key) {
        return backingMap.containsKey(wrap(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return backingMap.containsValue(value);
    }

    @Override
    public V put(K key, V value) {
        return backingMap.put(wrap(key), value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach((key, value) -> backingMap.put(wrap(key), value));
    }

    @Override
    public void clear() {
        backingMap.clear();
    }

    @Override
    public Collection<V> values() {
        return backingMap.values();
    }

    @Override
    public Set<K> keySet() {
        return new RefSet(backingMap.keySet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Entry<K, V>> entrySet() {
        final Set<Entry<ObjectReference<K>, V>> backingSet = backingMap.entrySet();

        return new Set<Entry<K, V>>() {
            @Override
            public int size() {
                return backingSet.size();
            }

            @Override
            public boolean isEmpty() {
                return backingSet.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if(o instanceof Entry) {
                    final Entry<K, V> entry = (Entry) o;
                    return backingSet.contains(wrapEntry(entry));
                }
                return false;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                final Iterator<Entry<ObjectReference<K>, V>> it = backingSet.iterator();

                return new Iterator<Entry<K, V>>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public Entry<K, V> next() {
                        return unwrapEntry(it.next());
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }

            @Override
            public Object[] toArray() {
                return backingSet.stream()
                        .map(RefMap::unwrapEntry).toArray();
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return backingSet.stream()
                        .map(RefMap::unwrapEntry)
                        .collect(Collectors.toList()).toArray(a);
            }

            @Override
            public boolean add(Entry<K, V> kvEntry) {
                return backingSet.add(wrapEntry(kvEntry));
            }

            @Override
            public boolean remove(Object o) {
                if(o instanceof Entry) {
                    final Entry<K, V> entry = (Entry) o;
                    return backingSet.remove(wrapEntry(entry));
                }
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return c.stream()
                        .allMatch(this::contains);
            }

            @Override
            public boolean addAll(Collection<? extends Entry<K, V>> c) {
                return c.stream()
                        .map(RefMap::wrapEntry)
                        .reduce(false, (anyChange, o) -> backingSet.add(o) || anyChange, (a, b) -> a || b);
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return backingSet.retainAll(toWrappedStream(c)
                        .collect(Collectors.toSet()));
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return toWrappedStream(c)
                        .reduce(false, (anyChange, e) -> backingSet.remove(e) || anyChange, (a, b) -> a || b);
            }

            @Override
            public void clear() {
                backingSet.clear();
            }
        };
    }
}
