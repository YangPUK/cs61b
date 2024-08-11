package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static java.lang.Math.abs;
import static org.junit.Assert.*;

import java.util.Comparator;

public class MaxArrayDequeTest {
    public class standardCompartor implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1-o2;
        }

        public standardCompartor() {}
    }

    public class absCompartor implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return abs(o1)-abs(o2);
        }

        public absCompartor() {}
    }
    @Test
    public void randomTest(){
        Comparator<Integer> c1 = new standardCompartor();
        Comparator<Integer> c2 = new absCompartor();
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<>(c1);
        for(int i = 0; i < 10; i++){
            mad1.addLast(i);
            mad1.printDeque();
        }
        for(int i = -1; i > -10; i--){
            mad1.addFirst(i);
            mad1.printDeque();
        }
        System.out.println(mad1.get(0));
        System.out.println(mad1.get(mad1.size()));
    }

    @Test
    public void randomizedTest(){
        MaxArrayDeque<Integer> L = new MaxArrayDeque<>();
        MaxArrayDeque<Integer> K = new MaxArrayDeque<>();
            int N = 20;
            for (int i = 0; i < N; i += 1) {
                int operationNumber = StdRandom.uniform(0, 6);
                if (operationNumber == 0) {
                    // addLast
                    int randVal = StdRandom.uniform(0, 100);
                    L.addLast(randVal);
                    K.addLast(randVal);
                    System.out.println("addLast(" + randVal + ")");
                } else if (operationNumber == 1) {
                    // addFirst
                    int randVal = StdRandom.uniform(0, 100);
                    L.addFirst(randVal);
                    K.addFirst(randVal);
                    System.out.println("addLast(" + randVal + ")");
                } else if (operationNumber == 2) {
                    //getLast
                    if(L.size()>0 && K.size() > 0){
                        System.out.println("getLastL: " + L.get(L.size()-1) + " getLastK: " + K.get(K.size()-1));
                    }else{
                        System.out.println(  "size is 0");
                    }
                }else if(operationNumber == 3){
                    //getFirst
                    if(L.size()>0 && K.size() > 0){
                        System.out.println("getFirstL: " + L.get(0) + " getFirstK: " + K.get(0));
                    }else{
                        System.out.println( "size is 0");
                    }
                }else if (operationNumber == 4) {
                    // removeLast
                    if(L.size()>0 && K.size() > 0){
                        System.out.println("removeLastL: " + L.removeLast() + " removeLastK: " + K.removeLast());
                    }else{
                        System.out.println( "size is 0");
                    }
                }else if (operationNumber == 5) {
                    // removeFitst
                    if(L.size()>0 && K.size() > 0){
                        System.out.println("removeFirstL: " + L.removeFirst() + " removeFirstK: " + K.removeFirst());
                    }else{
                        System.out.println( "size is 0");
                    }
                }
            }
            K.printDeque();
            L.printDeque();
    }
}
