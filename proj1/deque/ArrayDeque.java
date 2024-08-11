package deque;

public class ArrayDeque<T> {
    private T[] array;
    private int size;
    private int head;
    private int tail;

    /* Creates new list*/
    public ArrayDeque() {
        array = (T[]) new Object[8];
        size = 0;
        head = 7;
        tail = 0;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        for (int i = 0, p = head + 1; i < size; i++, p++) {
            if (p == array.length){
                p = 0;
            }
            a[i] = array[p];
        }
        array = a;
        head = capacity - 1;
        tail = size;
    }

    public void addFirst(T x){
        if (size == array.length) {
            resize(size * 2);
        }
        array[head] = x;
        head--;
        size++;
    }

    public void addLast(T x){
        if (size == array.length) {
            resize(size * 2);
        }
        array[tail] = x;
        tail++;
        size++;
    }

    public T removeFirst(){
        if (size == 0) {
            return null;
        }
        if (array.length >= 16 && size < (array.length/4)){
            resize(array.length/2);
        }
        head++;
        if (head == array.length) {
            head = 0;
        }
        size--;
        return array[head];
    }

    public T removeLast(){
        if (size == 0) {
            return null;
        }
        if (array.length >= 16 && size < (array.length/4)){
            resize(array.length/2);
        }
        if (tail == 0){
            tail = array.length;
        }
        tail--;
        size--;
        return array[tail];
    }

    public boolean isEmpty(){
        if (size == 0){
            return true;
        }
        return false;
    }

    public T get(int index){
        if (index < 0 || index >= size){
            return null;
        }
        int p = head + 1;
        if (p + index < array.length){
            return array[p + index];
        }
        return array[p + index - array.length];
    }

    public int size(){
        return size;
    }

    public void printDeque() {
        int p = head + 1;
        for (int i = 0; i < size; i++, p++) {
            if (p == array.length){
                p = 0;
            }
            System.out.print(array[p] + " ");
        }
    }
}













