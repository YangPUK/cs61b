package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {
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

    @Override
    public void addFirst(T x){
        if (size == array.length) {
            resize(size * 2);
        }
        array[head] = x;
        head--;
        if (head < 0) head = array.length - 1;
        size++;
    }

    @Override
    public void addLast(T x){
        if (size == array.length) {
            resize(size * 2);
        }
        array[tail] = x;
        tail++;
        if (tail == array.length) tail = 0;
        size++;
    }

    @Override
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
        T tmp = array[head];
        array[head] = null;
        return tmp;
    }

    @Override
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
        T tmp = array[tail];
        array[tail] = null;
        return tmp;
    }

    @Override
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

    public T getLast(){
        return get(size -1);
    }

    @Override
    public int size(){
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();

    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof ArrayDeque)) return false;
        if (((ArrayDeque<?>) o).size() != size){
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (get(i) != ((ArrayDeque<?>) o).get(i)){
                return false;
            }
        }
        return true;
    }

    public Iterator<T> iterator(){
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T>{
        private int wizPos;

        public ArrayDequeIterator() {
            wizPos = 0;
        }

        public boolean hasNext(){
            return wizPos < size;
        }

        public T next(){
            T result = get(wizPos++);
            return result;
        }
    }
}













