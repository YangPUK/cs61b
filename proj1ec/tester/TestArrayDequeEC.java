package tester;
import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import java.util.ArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizedTest() {
        int N = 50;
        StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ads = new ArrayDequeSolution<>();
        String ErrMessage = "";
        for (int i = 0; i < N; i++) {
            int opNum = StdRandom.uniform(0,5);
            if (opNum == 0) {
                //addFirst
                int valNum = StdRandom.uniform(0,100);
                ads.addFirst(valNum);
                sad.addFirst(valNum);
                ErrMessage += "addFirst(" + valNum + ")\n";
                assertEquals(ErrMessage,ads.get(0), sad.get(0));
            }
            if (opNum == 1) {
                //addLast
                int valNum = StdRandom.uniform(0,100);
                ads.addLast(valNum);
                sad.addLast(valNum);
                ErrMessage += "addLast(" + valNum + ")\n";
                assertEquals(ErrMessage ,ads.get(ads.size() - 1), sad.get(sad.size() - 1));
            }
            if (opNum == 2) {
                //removeFirst
                if (sad.isEmpty() && ads.isEmpty()) {
                    ErrMessage += "removeFirst()\n";
                    continue;
                }
                Integer res = ads.removeFirst();
                ErrMessage += "removeFirst(): " + res + "\n";
                assertEquals(ErrMessage, res, sad.removeFirst());
            }
            if (opNum == 3) {
                //removeLast
                if (sad.isEmpty() && ads.isEmpty()) {
                    ErrMessage += "removeLast()\n";
                    continue;
                }
                Integer res = ads.removeLast();
                ErrMessage += "removeLast(): " + res + "\n";
                assertEquals(ErrMessage, res, sad.removeLast());
            }
        }
    }
}

