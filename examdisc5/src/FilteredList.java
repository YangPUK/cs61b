import java.util.*;

class FilteredList<T> implements Iterable<T> {
    private List<T> list;
    private Predicate<T> pred;

    public FilteredList(List<T> L, Predicate<T> filter) {
        this.list = L;
        this.pred = filter;
    }

    public Iterator<T> iterator() {
        return new FilteredListIterator();
        }

        private class FilteredListIterator implements Iterator<T> {
            int index;

        public FilteredListIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            if (index >= list.size()) {
                return false;
            }
            if (pred.test(list.get(index))) {
                return true;
            }
            index++;
            return hasNext();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
                }
            T answer = list.get(index);
            index += 1;
            return answer;
            }
        }
}
