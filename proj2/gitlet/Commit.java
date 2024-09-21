package gitlet;

import static gitlet.Utils.*;

// TODO: any imports you need here
import java.text.SimpleDateFormat;
import java.util.Date;

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
        String msg = "Date: Wed Dec 31 16:00:00 1969 -0800\n";
        msg += "initial commit\n\n";
        String hash = sha1(msg);
        msg = "commit " + hash + "\n" + msg;
        msg = "===\n" + msg;
        writeContents(Repository.commits, msg);
        Info repoInfo = new Info();
        repoInfo.record(hash);
    }

    //Make a usual commit.
    public static void mCommit(String msg) {
        String history = readContentsAsString(Repository.commits);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        String timeStamp = sdf.format(new Date());
        msg = "Date: " + timeStamp + "\n" + msg;
        String hash = sha1(msg);
        msg = "commit " + hash + "\n" + msg;
        msg = "===\n" + msg + "\n\n";
        StringBuilder res = new StringBuilder().append(msg).append(history);
        writeContents(Repository.commits, res.toString());
        Info repoInfo = Info.loadInfo();
        repoInfo.record(hash);
    }

    //make a merge commit.
    public static void mergeCommit(String msg) {}

    public static void showLog() {
        System.out.println(readContentsAsString(Repository.commits));
    }
}

