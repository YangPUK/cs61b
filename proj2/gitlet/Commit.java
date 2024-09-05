package gitlet;

import static gitlet.Utils.*;

// TODO: any imports you need here

import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Formatter;

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
        String sha1 =sha1(msg);
        msg = "Date: Wed Dec 31 16:00:00 1969 -0800\n" + msg + "\n";
        msg = "commit " + sha1 + "\n" + msg;
        msg = "===\n" + msg;
        writeContents(Repository.commits, msg);
    }

    public static void mCommit(String msg) {
        String sha1 =sha1(msg);
        String history = readContentsAsString(Repository.commits);
        Formatter timeStamp = new Formatter();
        timeStamp.format("%1$ta %1$tb %1$te %1$ts %1tY", new java.util.Date());
        msg = "Date: " + timeStamp.toString() + "\n" + msg;
        msg = "Commit: " + sha1 + "\n" + msg;
        msg = "===\n" + msg;
        writeContents(Repository.commits, msg);
    }

    public static void mergeCommit(String msg) {}
}
