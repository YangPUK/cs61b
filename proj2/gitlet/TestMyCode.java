package gitlet;
import static java.lang.String.valueOf;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.util.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;


public class TestMyCode {
    String[] init = {"init"};
    String[] branch = {"branch", "other"};
    String[] reset = {"reset" };
    String[] checkout = {"checkout", "test"};
    String[] log = {"log"};

    private void cFile(String fileName) {
        File file = join(CWD, fileName);
        writeContents(file, valueOf(Math.random() * 10));
    }
    private void add(String fileName) {
        String[] add = {"add", fileName};
        Main.main(add);
    }
    private void commit(String msg) {
        String[] commit = {"commit", msg};
        Main.main(commit);
    }
    private void reset(String fileName) {}
    private void rm(String fileName) {
        String[] rm = {"rm", fileName};
        Main.main(rm);
    }

    @Test
    public void testFine() {
        setup1();
        rm("f.txt");
        commit("Remove one file");
        log();



    }

    @Test
    public void testRest() {
        String[] reset = {"reset", "b620"};
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

    @Test
    public void setup1() {
        Main.main(init);
        cFile("f.txt");
        cFile("g.txt");
        add("g.txt");
        add("f.txt");
        commit("Two files");
    }
    @Test
    public void setup2() {
    }
    public void log() {
        Main.main(log);
    }
}
