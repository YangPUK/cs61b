package deque;

public class ArrayDeque<Item> {
    private Item[] items;
    private int size;
    private int head;
    private int tail;
    private int capacity;

    /* Creates new list*/
    public void ArrayDeque() {
        capacity = 8;
        items = (Item[]) new Object[capacity];
        size = 0;
        head = capacity - 1;
        tail = 0;
    }

    private void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        for (int i = 0, p = head + 1; i < items.length; i++, p++) {
            if (p == size){
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
    }

    public void addLast(Item x){
        if (size == items.length) {
            resize(size * 2);
        }
        items[tail] = x;
        tail++;
    }

    public Item removeFirst(){
        if (capacity >= 16 || size < (capacity/4)){
            resize(capacity/2);
        }
        head++;
        return items[head];
    }

    public Item removeLast(){
        if (capacity >= 16 || size < (capacity/4)){
            resize(capacity/2);
        }
        tail--;
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
}













