package hashmap;

import java.util.ArrayList;
import java.util.Collection;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int initialSize = 16;
    private double LoadFactor = 0.75;
    private int size = 16;


    /** Constructors */
    public MyHashMap() {
        buckets = new Collection[initialSize];
    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        buckets = new Collection[this.initialSize];
        size = initialSize;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.LoadFactor = maxLoad;
        buckets = new Collection[this.initialSize];
        size = initialSize;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return null;
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return null;
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return null;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        buckets = null;
        initialSize = 16;
        LoadFactor = 0.75;
        size = 0;
    }

    private Node containsHelper(K key) {
        for (Collection<Node> c : buckets) {
            for (Node n : c) {
                if (n.key.equals(key)) {
                    return n;
                }
            }
        }
        return null;
    }
    @Override
    public boolean containsKey(K key) {
        if (containsHelper(key) == null) {
            return false;
        }
        return true;
    }

    @Override
    public V get(K key) {
        Node node = containsHelper(key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        Node hasNode = containsHelper(key);
        if (hasNode == null) {
            int pos = key.hashCode()%initialSize;
            buckets[pos].add(createNode(key, value));
        }
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }


}
