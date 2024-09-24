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
        commit("Add f, change f");
        checkout("master");
        log();
        merge("other");
    }

    public void setup1() {
        String[] wugs ={"this is a wug" , "this is not a wug", "and yet another wug", "another wug"};
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
