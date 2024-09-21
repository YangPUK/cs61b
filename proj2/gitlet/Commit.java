package gitlet;

import static gitlet.Utils.*;

// TODO: any imports you need here
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author ebean
 */
public class Commit {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */
    // Init inital commit.
    public static void setup(){
        BranchLogs masterBranch = new BranchLogs("master", null, null);
        masterBranch.saveBranch();
        String message = "initial commit";
        String timeStamp = "Date: Wed Dec 31 16:00:00 1969 -0800";
        String hash = sha1(timeStamp, message);
        Repository repo = new Repository();
        repo.record(hash, message, timeStamp);
    }

    //Make a usual commit.
    public static void makeCommit(String message) {
        Repository repo = new Repository();
        String commit = message;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        String timeStamp = sdf.format(new Date());
        String hash = sha1(message, sdf, repo.filesMap);
        repo.record(hash, commit, timeStamp);
    }

    //make a merge commit.
    public static void mergeCommit(String msg) {}

    public static void showLogs() {
        Repository repo = Repository.loadRepo();
        String head = repo.head;
        BranchLogs masterBranch = BranchLogs.readMaster();
        if (head.equals("master")) {
            masterBranch.showLogs();
            return;
        }
        BranchLogs headBranch = BranchLogs.readBranch(head);
        headBranch.showLogs();
        masterBranch.showLogs();
    }

    public static void showGlobalLogs() {
        List<String> branches = plainFilenamesIn(Repository.LOGS_DIR);
        for (String branch : branches) {
            BranchLogs branchLogs = BranchLogs.readBranch(branch);
            branchLogs.showLogs();
        }
    }


}

