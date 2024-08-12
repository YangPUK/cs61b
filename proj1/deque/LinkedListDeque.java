package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private Node prev;
        private T item;
        private Node next;

        public Node(Node p, T i, Node n) {
            prev = p;
            item = i;
            next = n;
        }
    }


    /* The first item is at sentinel.next*/
    private Node sentinel;
    private int size;

    /* Creates new list*/
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        size = 0;
    }

    void initailize(T x) {
        sentinel.next = new Node(sentinel, x, sentinel);
        sentinel.prev = sentinel.next;
        size = 1;
    }

    @Override
    public void addFirst(T x) {
        if (sentinel.next == null) {
            initailize(x);
        } else {
            sentinel.next = new Node(sentinel, x, sentinel.next);
            sentinel.next.next.prev = sentinel.next;
            size++;
        }
    }

    @Override
    public void addLast(T x) {
        if (sentinel.next == null) {
            initailize(x);
        } else {
            sentinel.prev = new Node(sentinel.prev, x, sentinel);
            sentinel.prev.prev.next = sentinel.prev;
            size++;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = sentinel.next;
        for (int i = 0; i < size; i++) {
            System.out.print(p.item + " ");
            p = p.next;
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T result = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return result;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T result = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size--;
        return result;
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0){
            return null;
        }
        Node p = sentinel.next;
        while (index > 0) {
            p = p.next;
            index--;
        }
        return p.item;
    }

    private T getHelper(Node p, int index) {
        if (index == 0) {
            return p.item;
        }
        return getHelper(p.next, --index);
    }

    public T getRecursive(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        Node p = sentinel.next;
        if (index == 0){
            return p.item;
        }
        return getHelper(p, index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<?> oa = (Deque<?>) o;
        if (oa.size() != size){
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!get(i).equals(oa.get(i))) {
                return false;
            }
        }
        return true;
    }

    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {
        private int wizPos;

        public LinkedListIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public T next(){
            return get(wizPos++);
        }
    }
}
