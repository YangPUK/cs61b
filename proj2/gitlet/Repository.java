package gitlet;

import java.io.File;
import java.util.LinkedList;
import java.util.HashMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File LOGS_DIR = join(GITLET_DIR, "logs");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File commits = join(LOGS_DIR, "commits");

//    private static class Node {
//        String hash;
//        HashMap<String, String> filesMap;
//
//        public Node(String hash, HashMap<String, String> filesMap) {
//            this.hash = hash;
//            this.filesMap = filesMap;
//        }
//    }
    //Init command.
    public static void setupPeresitence() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            LOGS_DIR.mkdir();
            BLOBS_DIR.mkdir();
            Info repoInfo = new Info();
            repoInfo.saveInfo();
            Commit.setup();
        } else {
            exitWithError("A Gitlet version-control system already exists in the current directory.");
        }
    }

//    private void saveRepo() {
//        writeObject(repoRoom, this);
//    }
//
//    private static Repository loadRepo() {
//        return readObject(repoRoom, Repository.class);
//    }


    //Add command.
    public static void addFiles(String fileName) {
        Info repoInfo = Info.loadInfo();
        File addedFile = join(CWD,fileName);
        File storeFile = join(repoInfo.current_DIR, fileName);
        System.out.println(repoInfo.current_DIR.toString());
        if(!addedFile.exists()) {
            exitWithError("File does not exist.");
        }
        if (!repoInfo.filesMap.containsKey(fileName)) {
            writeContents(storeFile, readContents(addedFile));
            repoInfo.filesMap.put(fileName, storeFile.toString());
            repoInfo.saveInfo();
            return;
        }
        File existFile = new File(repoInfo.filesMap.get(fileName));
        System.out.println(existFile);
        System.out.println(addedFile);
        if (!readContentsAsString(addedFile).equals(readContentsAsString(existFile))) {
            System.out.println("True");
            writeContents(storeFile, readContents(addedFile));
            repoInfo.filesMap.put(fileName, storeFile.toString());
            repoInfo.saveInfo();
        }
    }
}

