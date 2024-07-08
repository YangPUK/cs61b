package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.checkerframework.checker.units.qual.A;

public class randomizedTest {
    public static void main(String[] args) {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> K = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                K.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int sizeL = L.size();
                int sizeK = K.size();
                System.out.println("sizeL: " + sizeL + " and sizeK: " + sizeK);
            } else if (operationNumber == 2) {
                //getLast
                if(L.size()>0 && K.size() > 0){
                    System.out.println("getLastL: " + L.getLast() + " getLastK: " + K.getLast());
                }else{
                    System.out.println("Error: " + "size is 0");
                }
            }else if (operationNumber == 3) {
                // removeLast
                if(L.size()>0 && K.size() > 0){
                    System.out.println("removeLastL: " + L.removeLast() + " removeLastK: " + K.removeLast());
                }else{
                    System.out.println("Error: " + "size is 0");
                }
            }

        }
    }
}
