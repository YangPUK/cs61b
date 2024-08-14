import java.util.*;
public class IteratorOfIterators<Integer> implements Iterator<Integer> {
    private List<Iterator<Integer>> a;
    private int index;

    public IteratorOfIterators(List<Iterator<Integer>> a) {
        this.a = a;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        for (int i = 0; i < a.size(); i++, index++) {
            index = index % a.size();
            if (a.get(index).hasNext()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        return a.get(index++).next();
    }
}
