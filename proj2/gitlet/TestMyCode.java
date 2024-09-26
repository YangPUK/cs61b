package gitlet;
import static java.lang.String.valueOf;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.util.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;


public class TestMyCode {
    private TreeMap<String, String> wug;

    private void cFile(String fileName) {
        File file = join(CWD, fileName);
        writeContents(file, valueOf(Math.random() * 10));
    }
    private void add(String fileName, String w) {
        File file = join(CWD, fileName);
        writeContents(file, wug.get(w));
        Main.main(new String[]{"add", fileName});
    }
    private void madd(String fileName) {
        String[] add = {"add", fileName};
        File file = join(CWD, fileName);
        Main.main(add);
    }
    private void wadd(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            writeContents(file, valueOf(Math.random() * 10));
        }
        madd(fileName);
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
    private void merge(String branch) {
        String[] merge = {"merge", branch};
        Main.main(merge);
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



    @Test
    public void testMerge() {
        setup1();
        branch("other");
        add("h", "wug2");
        rm("g");
        add("f", "wug2");
        commit("Add h, remove g, change f");
        checkout("other");
        add("f", "notwug");
        add("k", "wug3");
        commit("Add k, change f");
        checkout("master");
        log();
        merge("other");
    }

    @Test
    public void testMerge2() {
        setup1();
        branch("B1");
        branch("B2");
        checkout("B1");
        add("h","wug");
        commit("Add h");
        checkout("B2");
        add("f", "wug");
        commit("Add f");
        branch("C1");
        add("g", "notwug");
        rm("f");
        commit("Add g, remove f");
        checkout("B1");
        merge("C1");
        merge("B2");
    }

    @Test
    public void merge3() {
        setup2();
        branch("B1");
        add("h", "wug2");
        commit("+ h");
        branch("B2");
        rm("f");
        commit("- f");
        merge("B1");
        checkout("B2");
        merge("master");
    }
    public void setup1() {
        String[] wugs ={"This is a wug." , "This is not a wug.", "And yet another wug.", "Another wug."};
        wug = new TreeMap<>();
        wug.put("wug", wugs[0]);
        wug.put("notwug", wugs[1]);
        wug.put("wug3", wugs[2]);
        wug.put("wug2", wugs[3]);
        Main.main(new String[] {"init"});
    }

    public void setup2() {
        String[] wugs ={"This is a wug." , "This is not a wug.", "And yet another wug.", "Another wug."};
        wug = new TreeMap<>();
        wug.put("wug", wugs[0]);
        wug.put("notwug", wugs[1]);
        wug.put("wug3", wugs[2]);
        wug.put("wug2", wugs[3]);
        Main.main(new String[] {"init"});
        add("f", "wug");
        add("g", "notwug");
        commit("Two files f & g");
    }

}
