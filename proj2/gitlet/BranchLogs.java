package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import static gitlet.Utils.*;

public class BranchLogs implements Serializable {
    public LinkedList<Node> branchList;
    public String branch;
    private File room = Repository.LOGS_DIR;
    public String parentBranch;
    public String parentHash;

    private static class Node implements Serializable {
        String hash;
        String message;
        String timeStamp;
        TreeMap<File, File> filesMap;

        public Node(String hash, String message, String timeStamp, TreeMap<File, File> filesMap) {
            this.hash = hash;
            this.message = message;
            this.timeStamp = timeStamp;
            this.filesMap = filesMap;
        }
    }

    public BranchLogs(String branch, String parentBranch, String parentHash) {
        this.parentBranch = parentBranch;
        this.parentHash = parentHash;
        this.branch = branch;
        branchList = new LinkedList<>();
        this.room = join(room, branch);
    }

    public void add(String hash, String message, String timeStamp, TreeMap<File, File> filesMap) {
        Node node = new Node(hash, message, timeStamp, filesMap);
        branchList.addFirst(node);
    }


    public void showLogs() {
        for (Node node : branchList) {
            System.out.println("===");
            System.out.println("commit " + node.hash);
            System.out.println("Date: " + node.timeStamp);
            System.out.println(node.message + "\n");
        }
    }

    public static void find(String message) {
        boolean found = false;
        List<String> branches = plainFilenamesIn(Repository.LOGS_DIR);
        for (String branch : branches) {
            BranchLogs branchLogs = BranchLogs.readBranch(branch);
            for (Node node : branchLogs.branchList) {
                if (node.message.equals(message)) {
                    found = true;
                    System.out.println(node.hash);
                }
            }
        }
        if (!found) {
            exitWithError("Found no commit with that message");
        }
    }

    public void saveBranch() {
        writeObject(room, this);
    }

    public static BranchLogs readBranch(String branch) {
        return readObject(join(Repository.LOGS_DIR, branch), BranchLogs.class);
    }

    public static BranchLogs readMaster() {
        return readObject(join(Repository.LOGS_DIR, "master"), BranchLogs.class);
    }
}
