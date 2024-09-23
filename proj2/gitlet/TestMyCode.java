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
    public void testInit() {
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
        File gitlet = Repository.GITLET_DIR;
        gitlet.delete();
        Main.main(init);
        cFile("f.txt");
        cFile("g.txt");
        add("g.txt");
        add("f.txt");
    }
    public void setup2() {
        commit("Two files");
    }
}
