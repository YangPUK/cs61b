import java.util.*;

public class DMSComparator implements Comparator<Animal> {
    @Override
    public int compare(Animal o1, Animal o2) {
        int first = o1.speak(new Animal());
        int second = o2.speak(new Animal());
        int third = o1.speak(new Dog());
        int fourth = o2.speak(new Dog());

        if (first + third - second - fourth == 0) {
            return 0;
        } else if (first + third - second - fourth == 1 || first + third - second - fourth == 2) {
            return 1;
        } else {
            return -1;
        }
    }
}
