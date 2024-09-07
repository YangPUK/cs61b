package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private int size;
    private BSTNode<K, V> root;

    private class BSTNode<K, V> {
        private BSTNode<K, V> left, right, parent;
        private K key;
        private V value;

        public BSTNode(K key, V value, BSTNode<K, V> left, BSTNode<K, V> right, BSTNode<K, V> parent) {
            this.left = left;
            this.right = right;
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
    }

    public BSTMap() {
        size = 0;
        root = null;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    private boolean containsHelper(K key, BSTNode<K, V> node) {
        if (node == null) {
            return false;
        }
        if (key.compareTo(node.key) == 0) {
            return true;
        }
        else if (key.compareTo(node.key) < 0) {
            return containsHelper(key, node.left);
        } else {
            return containsHelper(key, node.right);
        }
    }
    public boolean containsKey(K key) {
        return containsHelper(key, root);
    }

    public V get(K key) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return size;
    }

    public void put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        return;
    }


    // Exception
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}



