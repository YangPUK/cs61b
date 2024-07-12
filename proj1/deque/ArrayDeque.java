package deque;

public class ArrayDeque<Item> {
    private Item[] items;
    private int size;
    private int head;
    private int tail;

    /* Creates new list*/
    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        head = 7;
        tail = 0;
    }

    private void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        for (int i = 0, p = head + 1; i < items.length; i++, p++) {
            if (p == items.length){
                p = 0;
            }
            a[i] = items[p];
        }
        head = capacity - 1;
        tail = size;
    }

    public void addFirst(Item x){
        if (size == items.length) {
            resize(size * 2);
        }
        items[head] = x;
        head--;
        size++;
    }

    public void addLast(Item x){
        if (size == items.length) {
            resize(size * 2);
        }
        items[tail] = x;
        tail++;
        size++;
    }

    public Item removeFirst(){
        if (items.length >= 16 || size < (items.length/4)){
            resize(items.length/2);
        }
        head++;
        size--;
        return items[head];
    }

    public Item removeLast(){
        if (items.length >= 16 || size < (items.length/4)){
            resize(items.length/2);
        }
        tail--;
        size--;
        return items[tail];
    }

    public boolean isEmpty(){
        if (size == 0){
            return true;
        }
        return false;
    }

    public Item get(int Index){
        if (size == 0){
            return null;
        }
        return items[Index];
    }

    public int size(){
        return size;
    }

    public void printDeque() {
        int p = head + 1;
        for (int i = 0; i < size; i++) {
            if (p == items.length){
                p = 0;
            }
            System.out.print(items[p] + " ");
            p++;
        }
    }
}













