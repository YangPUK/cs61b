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
    String[] reset = {"reset" };
    String[] checkout = {"checkout", "test"};
    String[] log = {"log"};

    private void cFile(String fileName) {
        File file = join(CWD, fileName);
        writeContents(file, valueOf(Math.random() * 10));
    }
    private void add(String fileName) {
        String[] add = {"add", fileName};
        File file = join(CWD, fileName);
        if (!file.exists()) {
            cFile(fileName);
        }
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
    private void checkout(String branch) {
        Main.main(new String[]{"checkout", branch});
    }

    @Test
    public void testFine() {
        setup1();
        branch("other");
        add("h.txt");
        rm("g.txt");
        commit("Add h remove g");
        checkout("other");
        rm("f.txt");
        add("k.txt");
        commit("Add k remove f");
        System.out.println("other logs");
        log();
        checkout("master");
        System.out.println("master logs");
        log();
        add("m.txt");
    }

    @Test
    public void testRest() {
        commit("add m");
        String[] reset = {"reset", "2c7cf20"};
        Main.main(reset);
        log();
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
        Main.main(new String[]{"log"});
    }
    private void branch(String branch) {
        Main.main(new String[]{"branch", branch});
    }
    private void rest (String hash) {
        Main.main(new String[]{"rest", hash});
    }
}
