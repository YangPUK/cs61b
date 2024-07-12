package deque;

public class LinkedListDeque<Item> {
    private class IntNode {
        public IntNode prev;
        public Item item;
        public IntNode next;

        public IntNode(IntNode p, Item i, IntNode n) {
            prev = p;
            item = i;
            next = n;
        }
    }


    /* The first item is at sentinel.next*/
    private IntNode sentinel;
    //private IntNode last;
    private int size;

    /* Creates new list*/
    public void LinkedListDeque() {
        sentinel = new IntNode(null, null, null);
        //last = new IntNode(null, null, null);
        size = 0;
    }

    public void LinkedListDeque(Item x) {
        sentinel = new IntNode(null, null, null);
        sentinel.next = new IntNode(sentinel, x, sentinel);
        sentinel.prev = sentinel.next;
        size = 1;
    }
    public void addFirst(Item x) {
        if (sentinel == null) LinkedListDeque(x);
        else {
            sentinel.next = new IntNode(sentinel, x, sentinel.next);
            sentinel.next.next.prev = sentinel.next;
            size++;
        }
    }

    public void addLast(Item x) {
        if (sentinel == null) LinkedListDeque(x);
        else {
            sentinel.prev = new IntNode(sentinel.prev, x, sentinel);
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
        IntNode p = sentinel.next;
        for (int i = 0; i < size; i++) {
            System.out.print(p.item + " ");
            p = p.next;
        }
    }

    public Item removeFirst() {
        if (size == 0) return null;
        Item result = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return result;
    }

    public Item removeLast() {
        if (size == 0) return null;
        Item result = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        return result;
    }

    public Item get(int index) {
        if (index >= size || index < 0)return null;
        IntNode p = sentinel.next;
        while (index > 0){
            p = p.next;
            index--;
        }
        return p.item;
    }

    public Item getRecursive(int index){
        return null;
    }

}
