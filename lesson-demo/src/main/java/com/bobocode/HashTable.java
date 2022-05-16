package com.bobocode;

import java.util.Objects;

/**
 * A simple implementation of the Hash Table that allows storing a generic key-value pair. The table itself is based
 * on the array of {@link Node} objects.
 * <p>
 * An initial array capacity is 16.
 * <p>
 * Every time a number of elements is equal to the array size that tables gets resized
 * (it gets replaced with a new array that it twice bigger than before). E.g. resize operation will replace array
 * of size 16 with a new array of size 32. PLEASE NOTE that all elements should be reinserted to the new table to make
 * sure that they are still accessible  from the outside by the same key.
 *
 * @param <K> key type parameter
 * @param <V> value type parameter
 */
public class HashTable<K, V> {
    private static final int INITIAL_CAPACITY = 16;
    private static final int CAPACITY_MULTIPLIER = 2;
    private Node<K, V>[] buckets;
    private int size;

    public HashTable() {
        buckets = newBuckets(INITIAL_CAPACITY);
    }

    /**
     * Puts a new element to the table by its key. If there is an existing element by such key then it gets replaced
     * with a new one, and the old value is returned from the method. If there is no such key then it gets added and
     * null value is returned.
     *
     * @param key   element key
     * @param value element value
     * @return old value or null
     */
    public V put(K key, V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        ensureCapacity();

        int index = Math.abs(key.hashCode() % buckets.length);
        var bucket = buckets[index];

        if (bucket == null) {
            buckets[index] = new Node<>(key, value);
            size++;
            return null;
        }

        return appendToBucket(key, value, bucket);
    }

    private void ensureCapacity() {
        if (size == buckets.length) {
            reindexElements();
        }
    }

    private void reindexElements() {
        size = 0;
        var oldBuckets = buckets;
        buckets = newBuckets(oldBuckets.length * CAPACITY_MULTIPLIER);
        for (var bucket : oldBuckets) {
            while (bucket != null) {
                put(bucket.key, bucket.value);
                bucket = bucket.next;
            }
        }
    }

    private V appendToBucket(K key, V value, Node<K, V> bucket) {
        while (true) {
            if (key.equals(bucket.key)) {
                V oldValue = bucket.value;
                bucket.value = value;
                return oldValue;
            }
            if (bucket.next == null) {
                bucket.next = new Node<>(key, value);
                size++;
                return null;
            }
            bucket = bucket.next;
        }
    }

    /**
     * Prints a content of the underlying table (array) according to the following format:
     * 0: key1:value1 -> key2:value2
     * 1:
     * 2: key3:value3
     * ...
     */
    public void printTable() {
        for (int i = 0; i < buckets.length; i++) {
            System.out.print(i + ": ");
            printBucket(buckets[i]);
            System.out.println();
        }
    }

    private static void printBucket(Node<?, ?> bucket) {
        while (bucket != null) {
            System.out.print(bucket.key + ":" + bucket.value);
            if (bucket.next != null) {
                System.out.print(" -> ");
            }
            bucket = bucket.next;
        }
    }

    @SuppressWarnings("unchecked")
    private Node<K, V>[] newBuckets(int capacity) {
        return new Node[capacity];
    }

    /**
     * A generic class Node that supports two type params: one for the key and one for the value.
     *
     * @param <K> key type parameter
     * @param <V> value type parameter
     */
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}