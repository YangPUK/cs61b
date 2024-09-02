package tester;
import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import java.util.ArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizedTest() {
        int N = 20;
        StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ads = new ArrayDequeSolution<>();
        for (int i = 0; i < N; i++) {
            int opNum = StdRandom.uniform(0,5);
            if (opNum == 0) {
                //addFirst
                int valNum = StdRandom.uniform(0,100);
                ads.addFirst(valNum);
                sad.addFirst(valNum);
                System.out.println("addFirst(" + valNum + ")");
                assertEquals("call addFirst",ads.get(0), sad.get(0));
            }
            if (opNum == 1) {
                //addLast
                int valNum = StdRandom.uniform(0,100);
                ads.addLast(valNum);
                sad.addLast(valNum);
                System.out.println("addLast( " + valNum + ")");
                assertEquals("call addLast",ads.get(ads.size() - 1), sad.get(sad.size() - 1));
            }
            if (opNum == 2) {
                //removeFirst
                if (sad.isEmpty() && ads.isEmpty()) {
                    System.out.println("removeFirst()");
                    continue;
                }
                Integer res = ads.removeFirst();
                System.out.println("removeFirst(): " + res);
                assertEquals("call removeFirst", res, sad.removeFirst());
            }
            if (opNum == 3) {
                //removeFirst
                if (sad.isEmpty() && ads.isEmpty()) {
                    System.out.println("removeLast()");
                    continue;
                }
                Integer res = ads.removeLast();
                System.out.println("removeLast(): " + res);
                assertEquals("call removeLast", res, sad.removeLast());
            }
        }
    }
}

