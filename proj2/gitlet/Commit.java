package gitlet;

import static gitlet.Utils.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author ebean
 */
public class Commit {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    // Init inital commit.
    public static void setup() {
        BranchLogs masterBranch = new BranchLogs("master");
        masterBranch.saveBranch();
        String message = "initial commit";
        String timeStamp = "Wed Dec 31 16:00:00 1969 -0800";
        String hash = sha1(timeStamp, message);
        Repository repo = new Repository();
        repo.record(hash, message, timeStamp);
    }

    //Make a usual commit.
    public static void makeCommit(String message) {
        Repository repo = Repository.loadRepo();
        if (repo.stagedFiles.size() == 0
                && repo.rmFiles.size() == 0) {
            exitWithError("No changes added to the commit");
        } else if (message.isBlank()) {
            exitWithError("Please enter a commit message.");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        String timeStamp = sdf.format(new Date());
        String hash = sha1(message, timeStamp, repo.filesMap.toString());
        repo.record(hash, message, timeStamp);

    }

    //make a merge commit.
    public static void mergeCommit(String branch) {
        Repository repo = Repository.loadRepo();
        String message = "Merged " + branch + " into " + repo.headBranch + ".";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        String timeStamp = sdf.format(new Date());
        String hash = sha1(message, timeStamp, repo.filesMap.toString());
        repo.branchesPMap.put(repo.headBranch, hash);
        repo.saveRepo();
        String[] parents = {repo.getBranchHash(repo.headBranch), repo.getBranchHash(branch)};
        BranchLogs currBranch = BranchLogs.readBranch(repo.headBranch);
        BranchLogs givenBranch = BranchLogs.readBranch(branch);
        currBranch.mergeAdd(hash, message, timeStamp, repo.filesMap, parents, givenBranch.rmFiles);
        currBranch.setParent(hash, repo.headBranch);
    }

    public static void showLogs() {
        Repository repo = Repository.loadRepo();
        String head = repo.headBranch;
        BranchLogs masterBranch = BranchLogs.readMaster();
        if (head.equals("master")) {
            masterBranch.showLogs();
            return;
        }
        BranchLogs headBranch = BranchLogs.readBranch(head);
        headBranch.showLogs();
        headBranch.parentLogs();
    }

    public static void showGlobalLogs() {
        List<String> branchFiles = plainFilenamesIn(Repository.LOGS_DIR);
        for (String branchName : branchFiles) {
            BranchLogs branchLogs = BranchLogs.readBranch(branchName);
            branchLogs.showGLogs();
        }
    }
    //:)

}

