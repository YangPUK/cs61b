package gitlet;

import java.io.File;
import java.io.Serializable;
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
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    private LinkedList<Node> logsList = new LinkedList<>();
    private int size = 0;
    private HashMap<String, String> filesMap = new HashMap<>();
    private File current_DIR = join(BLOBS_DIR, String.valueOf(size));

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File LOGS_DIR = join(GITLET_DIR, "logs");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File commits = join(LOGS_DIR, "commits");
    public static File repoRoom = join(GITLET_DIR, "repo");

    private static class Node {
        String hash;
        HashMap<String, String> filesMap;

        public Node(String hash, HashMap<String, String> filesMap) {
            this.hash = hash;
            this.filesMap = filesMap;
        }
    }
    //Init command.
    public static void setupPeresitence() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            LOGS_DIR.mkdir();
            BLOBS_DIR.mkdir();
            Commit.setup();
//            String branch = "main\n";
//            writeContents(trees, branch);
        } else {
            exitWithError("A Gitlet version-control system already exists in the current directory.");
        }
    }

    private void saveRepo() {
        writeObject(repoRoom, this);
    }

    private static Repository loadRepo() {
        return readObject(repoRoom, Repository.class);
    }

    public static void record(String hash) {
        Repository repo = Repository.loadRepo();
        Node logNode = new Node(hash, repo.filesMap);
        repo.logsList.add(logNode);
        repo.size++;
        repo.saveRepo();
    }


    //Add command.
    public static void addFiles(String fileName) {
        Repository repo = Repository.loadRepo();
        if (!repo.current_DIR.exists()) {
            repo.current_DIR.mkdir();
        }
        File addedFile = join(CWD,fileName);
        File storeFile = join(repo.current_DIR, fileName);
        System.out.println(repo.current_DIR.toString());
        if(!addedFile.exists()) {
            exitWithError("File does not exist.");
        }
        if (!repo.filesMap.containsKey(fileName)) {
            writeContents(storeFile, readContents(addedFile));
            repo.filesMap.put(fileName, String.valueOf(repo.size));
            return;
        }

        File existFile = join(BLOBS_DIR, repo.filesMap.get(fileName));
        if (addedFile.hashCode() != existFile.hashCode()) {
            writeContents(storeFile, readContents(addedFile));
            repo.filesMap.put(fileName, String.valueOf(repo.size));
        }
    }
}

