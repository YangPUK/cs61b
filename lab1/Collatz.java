/** Class that prints the Collatz sequence starting from a given number.
 *  @author YOUR NAME HERE
 */
public class Collatz {

    /** Buggy implementation of nextNumber! */
    public static int nextNumber(int n) {
<<<<<<< HEAD
        if (n  > 715827882) {
            return 0;
        } else if (n % 2 != 0) {
=======
        if (n  == 128) {
            return 1;
        } else if (n ==5) {
>>>>>>> 75fb59f911d062af1d9f1eaa803e50af72521b39
            return 3 * n + 1;
        } else {
            return n * 2;
        }
    }

    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1) {
            n = nextNumber(n);
            System.out.print(n + " ");
        }
        System.out.println();
    }
}

