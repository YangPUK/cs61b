package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
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
    public String headHash;
    public TreeMap<String, File> headMap;
    public HashMap<String, Node> splitPoint = new HashMap<>();

    public static class Node implements Comparable<Node>, Serializable {
        private String hash;
        private String message;
        private String timeStamp;
        private String[] parents;
        public TreeMap<String, File> filesMap;

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null || !(o instanceof Node)) {
                return false;
            }
            if (hash == ((Node) o).hash) {
                return true;
            }
            return false;
        }


        public Node (String hash, String message, String timeStamp, TreeMap<String, File> filesMap) {
            this.hash = hash;
            this.message = message;
            this.timeStamp = timeStamp;
            this.filesMap = filesMap;
        }
        public void addParents(String[] parents) {
            this.parents = parents;
        }

        @Override
        public int compareTo(Node o) {
            return hash.compareTo(o.hash);
        }
    }

    public BranchLogs(String branch) {
        this.branch = branch;
        branchList = new LinkedList<>();
        this.room = join(room, branch);
        this.parentBranch = "I am an orphan";
        this.parentHash = "I am an orphan";
    }

    public BranchLogs(String branch, Repository repo) {
        this.parentBranch = repo.headBranch;
        this.parentHash = repo.workingHash();
        this.headHash = parentHash;
        this.headMap = repo.filesMap;
        this.branch = branch;
        branchList = new LinkedList<>();
        this.room = join(room, branch);
    }

    public void add(String hash, String message, String timeStamp, TreeMap<String, File> filesMap) {
        Node node = new Node(hash, message, timeStamp, filesMap);
        branchList.addFirst(node);
        headHash = hash;
        headMap = filesMap;
        saveBranch();
    }

    public void mergeAdd(String hash, String message, String timeStamp,
                         TreeMap<String, File> filesMap, String[] parents, String mBranch) {
        Node node = new Node(hash, message, timeStamp, filesMap);
        node.addParents(parents);
        branchList.addFirst(node);
        headHash = hash;
        headMap = filesMap;
        saveBranch();
    }

    public boolean contains(String hash) {
        for (Node node : branchList) {
            if (node.hash.equals(hash)) {
                return true;
            }
        }
        return false;
    }
    
    private int getHeadIndex() {
        int index = 0;
        for (Node node : branchList) {
            if (node.hash.equals(headHash)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public void parentLogs() {
        BranchLogs parentBranchLogs = readBranch(parentBranch);
        parentBranchLogs.headHash = parentHash;
        parentBranchLogs.showLogs();
    }

    public void showLogs() {
        for (int i = getHeadIndex(); i < branchList.size(); i++) {
            Node node = branchList.get(i);
            System.out.println("===");
            System.out.println("commit " + node.hash);
            if (node.parents != null) {
                System.out.println("Merge: " + node.parents[0].substring(0, 7)
                        + " " + node.parents[1].substring(0, 7));
            }
            System.out.println("Date: " + node.timeStamp);
            System.out.println(node.message + "\n");
        }
    }

    public void showGLogs() {
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

    public static TreeMap<String, File> findBranchLogs(String hash) {
        if (hash == null) {
            exitWithError("Hash is null");
        }
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

    public static void resetBranch(String hash) {
        if (hash == null) {
            exitWithError("Hash is null");
        }
        int n = hash.length();
        if (n <= 40) {
            List<String> branches = plainFilenamesIn(Repository.LOGS_DIR);
            for (String branch : branches) {
                BranchLogs branchLogs = BranchLogs.readBranch(branch);
                for (Node node : branchLogs.branchList) {
                    if (node.hash.substring(0, n).equals(hash)) {
                        Repository repo = Repository.loadRepo();
                        repo.setPointer(branch, node.hash);
                        branchLogs.setHead(node.hash);
                        Repository.resetHelper(branch, node.filesMap);
                        return;
                    }
                }
            }
        }
        exitWithError("No commit with that id exists.");
    }

//    public static TreeMap<String, File> findSplitMap(String branch) {
//        Repository repo = Repository.loadRepo();
//        BranchLogs given;
//        String currName = repo.headBranch;
//        while (!currName.equals("I am an orphan")) {
//            BranchLogs curr = readBranch(currName);
//            given = readBranch(branch);
//            while (!given.parentBranch.equals("I am an orphan")) {
//                if (given.parentBranch.equals(currName)) {
//                    return curr.findBranchLogs(given.parentHash);
//                }
//                given = readBranch(given.parentBranch);
//            }
//            currName = curr.parentBranch;
//        }
//        exitWithError("Could not find split map for branch " + branch);
//        return null;
//    }

    public static TreeMap<String, File> findSplitMap(String branch) {
        Repository repo = Repository.loadRepo();
        BranchLogs given;
        String currName = repo.headBranch;
        String givenName = branch;
        while (!currName.equals("I am an orphan")) {
            BranchLogs curr = readBranch(currName);
            for (Node currNode : curr.branchList) {
                while (!givenName.equals(currName) && !givenName.equals("master")) {
                    given = BranchLogs.readBranch(givenName);
                    if (given.parentHash.equals(currNode.hash)) {
                        return currNode.filesMap;
                    }
                    if(given.splitPoint.containsKey(currNode.hash)) {
                        return given.splitPoint.get(currNode.hash).filesMap;
                    }
                    givenName = given.parentBranch;
                }
                givenName = branch;
            }
            currName = curr.parentBranch;
        }
        exitWithError("Could not find split map for branch " + branch);
        return null;
    }

    private void setHead(String hash) {
        headHash = hash;
        saveBranch();
    }

    public boolean isEmpty() {
        return branchList.isEmpty();
    }

    public void saveBranch() {
        writeObject(room, this);
    }

    public static BranchLogs readBranch(String branch) {
        File file = join(Repository.LOGS_DIR, branch);
        if (!file.exists()) {
            exitWithError("No such branch exists.");
        }
        return readObject(file, BranchLogs.class);
    }

    public static BranchLogs readMaster() {
        return readObject(join(Repository.LOGS_DIR, "master"), BranchLogs.class);
    }

    public void setParent(String hash, String parent) {
        parentHash = hash;
        parentBranch = parent;
        saveBranch();
    }

    public static BranchLogs readCurrLog() {
        return readBranch(Repository.loadRepo().headBranch);
    }
    public void setSplit(String parentHash, String hash) {
        if (branchList.isEmpty()) {
            parentHash = this.parentHash;
            BranchLogs branchLogs = BranchLogs.readBranch(parentBranch);
            branchLogs.setSplit(parentHash, hash);
            return;
        }
        splitPoint.put(hash, getNode(parentHash));
        saveBranch();
    }

    public Node getNode(String hash) {
        for (Node node : branchList) {
            if (node.hash.equals(hash)) {
                return node;
            }
        }
        exitWithError("No such node exists.");
        return null;
    }

    public static void mergeSplit(String hash, String givenbranch) {
        List<String> branches = plainFilenamesIn(Repository.LOGS_DIR);
        for (String branch: branches) {
            BranchLogs branchLogs = BranchLogs.readBranch(branch);
            if (branchLogs.contains(hash));
            branchLogs.parentBranch = givenbranch;
            branchLogs.parentHash = hash;
            branchLogs.saveBranch();
        }
    }

}
