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
//    public String headHash;
    public TreeMap<File, File> headMap;

    public static class Node implements Serializable {
        public String hash;
        public String message;
        private String timeStamp;
        public TreeMap<File, File> filesMap;

        public Node(String hash, String message, String timeStamp, TreeMap<File, File> filesMap) {
            this.hash = hash;
            this.message = message;
            this.timeStamp = timeStamp;
            this.filesMap = filesMap;
        }
    }

    public BranchLogs (String branch) {
        this.branch = branch;
        branchList = new LinkedList<>();
        this.room = join(room, branch);
    }

    public BranchLogs(String branch, Repository repo) {
        this.parentBranch = repo.workingBranch;
        this.parentHash = repo.workingHash();
//        this.headHash = parentHash;
        this.headMap = repo.filesMap;
        this.branch = branch;
        branchList = new LinkedList<>();
        this.room = join(room, branch);
    }

    public void add(String hash, String message, String timeStamp, TreeMap<File, File> filesMap) {
        Node node = new Node(hash, message, timeStamp, filesMap);
        branchList.addFirst(node);
//        headHash = hash;
        headMap = filesMap;
    }

    public boolean contains(String hash) {
        for (Node node : branchList) {
            if (node.hash.equals(hash)) {
                return true;
            }
        }
        return false;
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

    public static TreeMap<File, File> findBranchLogs(String hash) {
        int n = hash.length();
        if (n <= 40) {
            List<String> branches = plainFilenamesIn(Repository.LOGS_DIR);
            for (String branch : branches) {
                BranchLogs branchLogs = BranchLogs.readBranch(branch);
                for (Node node : branchLogs.branchList) {
                    if (node.hash.substring(0, n).equals(hash)) {
                        return node.filesMap;
                    }
                }
            }
        }
        exitWithError("No commit with that id exists.");
        return null;
    }

    public void saveBranch() {
        writeObject(room, this);
    }

    public static BranchLogs readBranch(String branch) {
        try {
            return readObject(join(Repository.LOGS_DIR, branch), BranchLogs.class);
        } catch (Exception e) {
            exitWithError("No such branch exists.");
        }
        return null;
    }

    public static BranchLogs readMaster() {
        return readObject(join(Repository.LOGS_DIR, "master"), BranchLogs.class);
    }
}
