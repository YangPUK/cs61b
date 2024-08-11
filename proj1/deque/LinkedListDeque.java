package deque;

public class LinkedListDeque<T> implements Deque<T> {
    private class Node {
        public Node prev;
        public T item;
        public Node next;

        public Node(Node p, T i, Node n) {
            prev = p;
            item = i;
            next = n;
        }
    }


    /* The first item is at sentinel.next*/
    private Node sentinel;
    //private Node last;
    private int size;

    /* Creates new list*/
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        size = 0;
    }

    public void LinkedListDeque(T x) {
        sentinel.next = new Node(sentinel, x, sentinel);
        sentinel.prev = sentinel.next;
        size = 1;
    }
    public void addFirst(T x) {
        if (sentinel.next == null) {
            LinkedListDeque(x);
        }

        else {
            sentinel.next = new Node(sentinel, x, sentinel.next);
            sentinel.next.next.prev = sentinel.next;
            size++;
        }
    }

    public void addLast(T x) {
        if (sentinel.next == null) {
            LinkedListDeque(x);
        }
        else {
            sentinel.prev = new Node(sentinel.prev, x, sentinel);
            sentinel.prev.prev.next = sentinel.prev;
            size++;
        }
    }

    public boolean isEmpty() {
        if (size == 0) return true;
        return false;
    }

    public int size() {
        return size;}

    public void printDeque() {
        Node p = sentinel.next;
        for (int i = 0; i < size; i++) {
            System.out.print(p.item + " ");
            p = p.next;
        }
    }

    public T removeFirst() {
        if (size == 0) return null;
        T result = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return result;
    }

    public T removeLast() {
        if (size == 0) return null;
        T result = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        return result;
    }

    public T get(int index) {
        if (index >= size || index < 0)return null;
        Node p = sentinel.next;
        while (index > 0){
            p = p.next;
            index--;
        }
        return p.item;
    }

    private T getHelper(Node p, int index) {
        if (index == 0) return p.item;
        return getHelper(p.next, --index);
    }
    public T getRecursive(int index){
        if (index >= size || index < 0) return null;
        Node p = sentinel.next;
        if (index == 0){return p.item;}
        return getHelper(p, index);
    }

}
