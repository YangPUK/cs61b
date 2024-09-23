package gitlet;
import static java.lang.String.valueOf;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;


public class TestMyCode {

    @Test
    public void testRest() {
        String[] init = {"init"};
        String[] adda = {"add", "a"};
        String[] addb = {"add", "b"};
        String[] addc = {"add", "c"};
        String[] addd = {"add", "d"};
        String[] adde = {"add", "e"};
        String[] branch = {"branch", "test"};
        String[] commit = {"commit", valueOf(Math.random()*10)};
        String[] reset = {"reset" };
        String[] checkout = {"checkout", "test"};
        Main.main(init);
        Main.main(adda);
        Main.main(commit);
        Main.main(addb);
        Main.main(commit);
        Main.main(branch);
        Main.main(addc);
        Main.main(commit);
        Main.main(checkout);
        Main.main(addd);
        Main.main(commit);
        Main.main(adde);
        Main.main(commit);
        Main.main(reset);
    }
    @Test
    public void myTest() {
        List<String> a = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            a.add(valueOf(i));
        }
        a.subList(0, 5).clear();
        System.out.println(a);
    }
}
