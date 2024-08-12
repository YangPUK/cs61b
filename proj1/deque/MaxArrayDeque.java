package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comp;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comp = c;
    }

    public T max() {
        if (isEmpty()) {
        return null;
        }
        T tmpMax = get(0);
        for (int i = 1; i < size(); i++) {
            if (comp.compare(tmpMax, get(i)) < 0) {
              tmpMax = get(i);
            }
        }
        return tmpMax;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T tmpMax = get(0);
        for (int i = 1; i < size(); i++) {
            if (c.compare(tmpMax, get(i)) < 0) {
                tmpMax = get(i);
            }
        }
        return tmpMax;
    }
}
