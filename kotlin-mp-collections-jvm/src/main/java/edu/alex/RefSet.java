package edu.alex;

import static edu.alex.ObjectReference.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RefSet<K> implements Set<K> {

    private final static <T> Stream<ObjectReference<T>> toWrappedStream(Collection<T>  c) {
        return c.stream().map(ObjectReference::wrap);
    }

    private final Set<ObjectReference<? extends K>> backingSet;

    public RefSet() {
        backingSet = new LinkedHashSet<>();
    }

    RefSet(Set<ObjectReference<? extends K>> backingSet) {
        this.backingSet = backingSet;
    }

    @Override
    public boolean isEmpty() {
        return backingSet.isEmpty();
    }

    @Override
    public int size() {
        return backingSet.size();
    }

    @Override
    public Iterator<K> iterator() {
        final Iterator<ObjectReference<? extends K>> it = backingSet.iterator();
        return new Iterator<K>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public K next() {
                return it.next().get();
            }

            @Override
            public void remove() {
                it.remove();
            }
        };
    }

    @Override
    public boolean contains(Object o) {
        return backingSet.contains(wrap(o));
    }

    @Override
    public Object[] toArray() {
        return backingSet.stream().map(ObjectReference::get).toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return backingSet.stream()
                .map(ObjectReference::get)
                .collect(Collectors.toList()).toArray(a);
    }

    @Override
    public boolean add(K e) {
        return backingSet.add(wrap(e));
    }

    @Override
    public boolean remove(Object o) {
        return backingSet.remove(wrap(o));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return toWrappedStream(c)
                .allMatch(backingSet::contains);
    }

    @Override
    public boolean addAll(Collection<? extends K> c) {
        return toWrappedStream(c)
                .reduce(false, (anyChange, o) -> backingSet.add(o) || anyChange, (a, b) -> a || b);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return backingSet.retainAll(toWrappedStream(c).collect(Collectors.toSet()));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return toWrappedStream(c)
                .reduce(false, (anyChange, o) -> backingSet.remove(o) || anyChange, (a, b) -> a || b);
    }

    @Override
    public void clear() {
        backingSet.clear();
    }
}
