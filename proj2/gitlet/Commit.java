package gitlet;

import static gitlet.Utils.*;

// TODO: any imports you need here

import java.util.Date; // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
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
    public static void setup(String msg){
        msg = "Date: Wed Dec 31 16:00:00 1969 -0800\n" + msg;
        msg = "===\n" + msg;
        writeContents(Repository.commits, msg);
    }
    public static void makeCommit(String msg) {
        String history = readContentsAsString(Repository.commits);
        String timeStamp = java.util.Formatter("yyyy.MM.dd").format(new java.util.Date());
        String sha1 = Utils.sha1("1");
    }
}
