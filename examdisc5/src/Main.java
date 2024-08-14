import java.util.*;
public class Main {
    public static void main(String[] args) {
        List<Integer> l1 = new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();
        List<Integer> l3 = new ArrayList<>();
        List<Iterator<Integer>> a = new ArrayList<>();
        IteratorOfIterators<Integer> iter = new IteratorOfIterators<>(a);

        l1.add(1);
        l1.add(3);
        l1.add(4);
        l1.add(5);
        l3.add(2);
        a.add(l1.iterator());
        a.add(l2.iterator());
        a.add(l3.iterator());

        while (iter.hasNext()) {
            System.out.print(iter.next() + " ");
        }

        Animal a1 = new Animal();
        Animal a2 = new Animal();
        int i = a1.speak(a2);

    }
}