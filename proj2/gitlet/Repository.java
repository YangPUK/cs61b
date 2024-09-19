package gitlet;

import java.io.File;
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
    public static final File GITLET_DIR = join(CWD, ".gitlets");
    public static final File LOGS_DIR = join(GITLET_DIR, "logs");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File BLOBS = join(BLOBS_DIR, "blob");
    public static final File commits = join(LOGS_DIR, "commits");
    public static final File trees = join(GITLET_DIR, "trees");

    //Init command.
    public static void setupPeresitence() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            LOGS_DIR.mkdir();
            BLOBS_DIR.mkdir();
            String branch = "main\n";
            writeContents(trees, branch);
            Commit.setup("initial commit");
        } else {
            exitWithError("A Gitlet version-control system already exists in the current directory.");
        }
    }

    //Add command.
    public static void addFiles(String fileName) {
        File file = join(BLOBS_DIR, fileName);

    }
}

