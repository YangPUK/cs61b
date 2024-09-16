package hashmap;

import java.util.*;

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
    private double loadFactor = 0.75;
    private int size = 0;
    private Set<K> keysSet;


    /** Constructors */
    public MyHashMap() {
        buckets = createTable(initialSize);
        bucketHelper(buckets);
        keysSet = new HashSet<>();
    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        buckets = createTable(initialSize);
        bucketHelper(buckets);
        keysSet = new HashSet<>();
    }

    private void bucketHelper(Collection<Node>[] b) {
        for (int i = 0; i < b.length; i++) {
            b[i] = createBucket();
        }
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
        this.loadFactor = maxLoad;
        buckets = createTable(initialSize);
        size = 0;
        keysSet = new HashSet();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
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
        return new ArrayList<>();
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
        return new Collection[tableSize];
    }

    @Override
    public void clear() {
        buckets = createTable(initialSize);
        bucketHelper(buckets);
        size = 0;
        keysSet = new HashSet<>();
    }

    private Node nodeFinder(K key) {
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
        if (nodeFinder(key) == null) {
            return false;
        }
        return true;
    }

    @Override
    public V get(K key) {
        Node node = nodeFinder(key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    @Override
    public int size() {
        return this.size;
    }

    private void resize(int newSize) {
        initialSize = newSize;
        Collection<Node>[] newBuckets = createTable(initialSize);
        bucketHelper(newBuckets);
        for (Collection<Node> bucket : buckets) {
            for (Node n : bucket) {
                int pos = Math.floorMod(n.key.hashCode(), initialSize);
                newBuckets[pos].add(n);
            }
        }
        buckets = newBuckets;
    }

    @Override
    public void put(K key, V value) {
        Node hasNode = nodeFinder(key);
        if (hasNode == null) {
            if (initialSize * loadFactor <= size + 1) {
                resize(2*initialSize);
            }
            int pos = Math.floorMod(key.hashCode(), initialSize);
            buckets[pos].add(createNode(key, value));
            keysSet.add(key);
            size++;
        }
        else {
            hasNode.value = value;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return keysSet.iterator();
    }

    @Override
    public Set<K> keySet() {
        return keysSet;
    }

    private class myNode {
        Node thatNode;
        int posI;
        public myNode(int pos, Node n) {
            thatNode = n;
            this.posI = pos;
        }
    }
    private myNode removeHelper(K key) {
        for (int i = 0; i < buckets.length; i++) {
            for (Node n : buckets[i]) {
                if (n.key.equals(key)) {
                    myNode pos = new myNode(i, n);
                    return pos;
                }
            }
        }
        return null;
    }
    @Override
    public V remove(K key) {
        myNode n = removeHelper(key);
        if (n == null) {
            return null;
        }
        else {
            V res = n.thatNode.value;
            buckets[n.posI].remove(n.thatNode);
            size--;
            keysSet.remove(key);
            return res;
        }
    }

    @Override
    public V remove(K key, V value) {
        Node n = nodeFinder(key);
        V res = n.value;
        n.value = value;
        return res;
    }


}
