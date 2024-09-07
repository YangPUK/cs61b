package bstmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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
        else if (key.compareTo(node.key) == 0) {
            return true;
        }
        else if (key.compareTo(node.key) < 0) {
            return containsHelper(key, node.left);
        }
        else {
            return containsHelper(key, node.right);
        }
    }
    public boolean containsKey(K key) {
        return containsHelper(key, root);
    }

    private V getHelper(K key, BSTNode<K, V> node) {
        if (node == null) {
            return null;
        }
        else if (key.compareTo(node.key) == 0) {
            return node.value;
        }
        else if (key.compareTo(node.key) < 0) {
            return getHelper(key, node.left);
        }
        else {
            return getHelper(key, node.right);
        }
    }
    public V get(K key) {
        return getHelper(key, root);
    }

    private void putHelper(K key, V value, BSTNode<K, V> node, BSTNode<K, V> parent, boolean isLeft) {
        if (node == null) {
            if (isLeft) {
                parent.left = new BSTNode(key, value, null, null, parent);
            }
            else {
                parent.right = new BSTNode(key, value, null, null, parent);
            }
            this.size += 1;
        }
        else if(key.compareTo(node.key) == 0) {
            node.value = value;
        }
        else if (key.compareTo(node.key) < 0) {
            putHelper(key, value, node.left, node, true);
        }
        else {
            putHelper(key, value, node.right, node, false);
        }
    }
    public void put(K key, V value) {
        if (size() == 0) {
            this.root = new BSTNode(key, value, null, null, null);
            this.size += 1;
            return;
        }
        putHelper(key, value, root, root, true);
    }

    public int size() {
        return size;
    }

    private String printHelper(BSTNode<K, V> node) {
        if (node == null) {
            return "";
        }
        return printHelper(node.left) + "\n" + node.key.toString() + "\n" + printHelper(node.right);
    }
    public void printInOrder() {
        String s;
        s = printHelper(root);
        System.out.println(s.toString());
    }


    private BSTNode<K, V> getFirst() {
        BSTNode<K, V> node = root;
        if (node == null) {
            return null;
        }
        while (node.left != null) {
            node = node.left;
        }
        return node;
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



