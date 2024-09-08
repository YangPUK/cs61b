package bstmap;

import java.util.*;

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

    // Return the corresponding BSTNode
    private BSTNode<K, V> containsHelper(K key, BSTNode<K, V> node) {
        if (node == null) {
            return null;
        }
        else if (key.compareTo(node.key) == 0) {
            return node;
        }
        else if (key.compareTo(node.key) < 0) {
            return containsHelper(key, node.left);
        }
        else {
            return containsHelper(key, node.right);
        }
    }
    public boolean containsKey(K key) {
        BSTNode<K, V> node = containsHelper(key, root);
        if (node == null) {
            return false;
        }
        return true;
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

    //Recursive method to get all keys string
    private String printHelper(BSTNode<K, V> node) {
        if (node == null) {
            return "";
        }
        return printHelper(node.left) +  node.key.toString() + "\n" + printHelper(node.right);
    }
    public void printInOrder() {
        String s;
        s = printHelper(root);
        System.out.println(s);
    }

    // A linkedList ordered by keys
    private LinkedList<K> queueHelper(BSTNode<K, V> node) {
        if (node == null) {
            return new LinkedList<K>();
        }
        LinkedList<K> m = new LinkedList<>();
        m.add(node.key);
        if (node.left == null && node.right == null) {
            return m;
        }
        else if (node.left != null && node.right == null) {
            LinkedList<K> l = queueHelper(node.left);
            l.addAll(m);
            return l;
        }
        else if (node.left == null && node.right != null) {
            LinkedList<K> r = queueHelper(node.right);
            m.addAll(r);
            return m;
        }
        else {
            LinkedList<K> l = queueHelper(node.left);
            LinkedList<K> r = queueHelper(node.right);
            l.addAll(m);
            l.addAll(r);
            return l;
        }
    }
    public Set<K> keySet() {
        LinkedList<K> queue = queueHelper(root);
        Set<K> set = new HashSet<>();
        for (K i : queue) {
            set.add(i);
        }
        return set;
    }

    private BSTNode<K, V> getMin(BSTNode<K, V> node) {
        if (node == null) {
            return null;
        }
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
    private BSTNode<K, V> getMax(BSTNode<K, V> node) {
        if (node == null) {
            return null;
        }
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }
    public V remove(K key) {
        BSTNode<K, V> node = containsHelper(key, root);
        if (node == null) {
            return null;
        }
        V result = node.value;
        //No leaf
        if (node.left == null && node.right == null) {
            if (node.parent == null) {
                root = null;
                size = 0;
            }
            else if (node.parent.left == node) {
                node.parent.left = null;
            }
            else {
                node.parent.right = null;
            }
            return result;
        }
        //Has leaves
        BSTNode<K, V> next = getMin(node.right);
        BSTNode<K, V> prev = getMax(node.left);
        if (next == null) {
            if (prev.parent == node) {
                node.left = prev.left;
                if (node.left != null) {
                    node.left.parent = node;
                }
            }
            else {
                prev.parent.right = prev.left;
                if (prev.left != null) {
                    prev.left.parent = prev.parent;
                }
            }
            node.key = prev.key;
            node.value = prev.value;
        }
        else {
            if (next.parent == node) {
                node.right = next.right;
                if (next.right != null) {
                    next.right.parent = node;
                }
            }
            else {
                next.parent.left = next.right;
                if (next.right != null) {
                    next.right.parent = next.parent;
                }
            }
            node.key = next.key;
            node.value = next.value;
        }
        size -= 1;
        return result;
    }

    public V remove(K key, V value) {
        BSTNode<K, V> node = containsHelper(key, root);
        if (node == null) {
            return null;
        }
        else if (node.value.equals(value)) {
            return remove(key);
        }
        else {
            return null;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return queueHelper(root).iterator();
    }
}



