package timingtest;
import edu.princeton.cs.algs4.Stopwatch;
import org.checkerframework.checker.units.qual.A;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, int opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts;
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList();
        AList<Double> times = new AList();
        int opCount = 10000;
        for(int i = 1000; i<=128000; i*=2) {
            Ns.addLast(i);
            SLList tmp = new SLList();
            for (int j=0; j<i; j++){
                tmp.addLast(0);
            }
            Stopwatch sw = new Stopwatch();
            for(int k=0; k<opCount; k++) {
                tmp.addLast(0);
            }
            times.addLast(sw.elapsedTime());

        }
        printTimingTable(Ns, times, opCount);
    }

}
