package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author eBean Deng
 */
public class Repository implements Serializable {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    //* The .gitlet directory.
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File LOGS_DIR = join(GITLET_DIR, "logs");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public TreeMap<String, String> branchesPMap;   //Pointer Map
    public TreeMap<String, File> filesMap;
    public TreeSet<String> stagedFiles = new TreeSet<>();
    public TreeSet<String> removedFiles = new TreeSet<>();
    public TreeSet<String> mergeStagedFiles = new TreeSet<>();
    public String headBranch;
    public static final File REPO_ROOM = join(Repository.GITLET_DIR, "repo");

    public static void setupPeresitence() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            LOGS_DIR.mkdir();
            BLOBS_DIR.mkdir();
            Commit.setup();
        } else {
            exitWithError("A Gitlet version-control system already exists"
                    + " in the current directory.");
        }
    }

    public Repository() {
        branchesPMap = new TreeMap<>();
        branchesPMap.put("master", null);
        headBranch = "master";
        filesMap = new TreeMap<>();
    }

    public void saveRepo() {
        writeObject(REPO_ROOM, this);
    }

    public static Repository loadRepo() {
        return readObject(REPO_ROOM, Repository.class);
    }

    public void record(String hash, String message, String timeStamp) {
        BranchLogs headBranch = readObject(join(LOGS_DIR, this.headBranch), BranchLogs.class);
        for (String file : stagedFiles) {
            File addedFile = join(CWD, file);
            String fileHash = sha1(readContents(addedFile));
            File storeFile = join(BLOBS_DIR, fileHash);
            filesMap.put(file, storeFile);
            writeContents(storeFile, readContents(addedFile));
        }
        for (String file : removedFiles) {
            filesMap.remove(file);
        }
        headBranch.add(hash, message, timeStamp, filesMap);
        branchesPMap.put(this.headBranch, hash);
        clear();
        this.saveRepo();
    }

    public void clear() {
        stagedFiles.clear();
        removedFiles.clear();
        saveRepo();
    }

    private static boolean fileCompare(File file1, File file2) {
        try {
            if (file1 == file2) {
                return true;
            } else if (file1 == null || file2 == null) {
                return false;
            } else if (file1.length() != file2.length()) {
                return false;
            }
            return Files.mismatch(file1.toPath(), file2.toPath()) == -1;
        } catch (IOException e) {
            return false;
        }
    }

    public static void addFile(String fileName) {
        Repository repo = loadRepo();
        File addedFile = join(CWD, fileName);
        if (!addedFile.exists()) {
            exitWithError("File does not exist.");
        } else if (repo.stagedFiles.contains(fileName)) {
            return;
        } else if (!repo.filesMap.containsKey(fileName)) {
            // Remove and add the same file.
            repo.stagedFiles.add(fileName);
            repo.saveRepo();
            return;
        }   //Tracked
        File oddFile = repo.filesMap.get(fileName);
        if (!fileCompare(oddFile, addedFile)) {
            repo.stagedFiles.add(fileName);
            repo.saveRepo();
        } else if (repo.removedFiles.contains(fileName)) {
            repo.removedFiles.remove(fileName);
            repo.saveRepo();
        }
    }

    public static void removeFile(String fileName) {
        Repository repo = loadRepo();
        File removedFile = join(CWD, fileName);
        // Staged.
        if (repo.stagedFiles.contains(fileName)) {
            repo.stagedFiles.remove(fileName);
            repo.saveRepo();
        } else if (repo.filesMap.containsKey(fileName)) {   // Not Staged, but tracked.
            repo.removedFiles.add(fileName);
            // Delete the file.
            if (removedFile.exists()) {
                restrictedDelete(removedFile);
            }
            repo.saveRepo();
        } else {
            exitWithError("No reason to remove the file.");
        }
    }

    public static void showStatus(){
        Repository repo = loadRepo();
        System.out.println("=== Branches ===");
        for (String branch : repo.branchesPMap.keySet()) {
            if (branch.equals(repo.headBranch)) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println("\n=== Staged Files ===");
        for (String stagedFile : repo.stagedFiles) {
            System.out.println(stagedFile);
        }
        System.out.println("\n=== Removed Files ===");
        for (String removedFile : repo.removedFiles) {
            System.out.println(removedFile);
        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        System.out.println("\n=== Untracked Files ===\n");
    }

    public static void createBranch(String branch) {
        Repository repo = loadRepo();
        if (repo.branchesPMap.containsKey(branch)) {
            exitWithError("A branch with that name already exists.");
        }
        repo.setPointer(branch, repo.workingHash());
        BranchLogs branchLogs = new BranchLogs(branch, repo);
        branchLogs.saveBranch();
        repo.saveRepo();
    }

    public static void rmBranch(String branch) {
        Repository repo = loadRepo();
        if (repo.headBranch.equals(branch) || branch.equals("master")) {
            exitWithError("Cannot remove the current branch.");
        } else if (!repo.branchesPMap.containsKey(branch)) {
            exitWithError("A branch with that name does not exists.");
        }
        repo.branchesPMap.remove(branch);
        join(LOGS_DIR, branch).delete();
        repo.saveRepo();
    }

    public static void mergeBranch(String branch) {
        Repository repo = loadRepo();
        // Failure cases.
        if (!repo.branchesPMap.containsKey(branch)) {
            exitWithError("A branch with that name does not exists.");
        } else if (!repo.stagedFiles.isEmpty() || !repo.removedFiles.isEmpty()) {
            exitWithError("You have uncommitted changes.");
        } else if (repo.headBranch.equals(branch)) {
            exitWithError("Cannot merge a branch with itself.");
        }
        BranchLogs givenBranchLogs = BranchLogs.readBranch(branch);
        BranchLogs currBranchLogs = BranchLogs.readBranch(repo.headBranch);
        if (currBranchLogs.parentBranch.equals(branch)) {
            System.out.println("Given branch is an ancestor of"
                    + " the current branch.");
            System.exit(0);
        }
        String splitHash = givenBranchLogs.parentHash;
        TreeMap<String, File> splitMap = givenBranchLogs.findBranchLogs(splitHash);
        TreeMap<String, File> givenMap = givenBranchLogs.headMap;
        TreeMap<String, File> currMap = repo.filesMap;
        List<String> existFiles = plainFilenamesIn(CWD);
        for (String fileName : existFiles) {
            File file = join(CWD, fileName);
            if (!currMap.containsKey(fileName) && (givenMap.containsKey(fileName)
                    || (!givenMap.containsKey(fileName) && splitMap.containsKey(fileName)))) {
                System.out.println("There is an untracked file in the way; delete it,"
                        + " or add and commit it first.");
                System.exit(0);
            }
        }
        if (givenBranchLogs.parentHash.equals(repo.workingHash())) {
            System.out.println("Current branch fast-forwarded.");
            checkoutMap(givenMap);
            repo.clear();
            repo.filesMap = givenMap;
            repo.saveRepo();
            Commit.mergeCommit(branch);
            return;
        }
        Boolean hasConflict = false;
        for (String fileName : existFiles) {
            File file = join(CWD, fileName);
            if (!fileCompare(givenMap.get(fileName), (splitMap.get(fileName)))
                    && fileCompare(repo.filesMap.get(fileName), splitMap.get(fileName))) {
                //Modified or removed in givenBranch, not in currBranch.
                if (givenMap.containsKey(fileName)) {
                    writeContents(file, givenMap.get(fileName));
                    repo.mergeStagedFiles.add(fileName);
                } else {
                    file.delete();
                }
                continue;
            } else if (fileCompare(givenMap.get(fileName), splitMap.get(fileName))
                    && !fileCompare(repo.filesMap.get(fileName), splitMap.get(fileName))) {
                //Modified in currBranch, not in givenBranch.
                if (!repo.filesMap.containsKey(fileName)) {     //NOT SURE!
                    file.delete();
                }
                continue;
            } else if (!fileCompare(givenMap.get(fileName), splitMap.get(fileName))
                    && !fileCompare(repo.filesMap.get(fileName), splitMap.get(fileName))) {
                //Modified in both branch.
                String a = "<<<<<<< HEAD\n";
                String b = "=======\n";
                String c = ">>>>>>>";
                String givenContents = "";
                String currContents = "";
                if (givenMap.containsKey(fileName)) {
                    givenContents = readContentsAsString(givenMap.get(fileName));
                } else if (!repo.filesMap.containsKey(fileName)) {
                    //Deleted in both branch, but somehow created.
                }
                if (repo.filesMap.containsKey(fileName)) {
                    currContents = readContentsAsString(file);
                }
                String res = a.concat(givenContents).concat(b).concat(c);
                writeContents(file, res);
                hasConflict = true;
                continue;
            }
        }
        for (String addFileName : givenMap.keySet()) {
            if (!existFiles.contains(addFileName)) {
                if(splitMap.containsKey(addFileName) && !currMap.containsKey(addFileName)) {
                    writeContents(join(CWD, addFileName), readContents(givenMap.get(addFileName)));
                }
            }
        }
        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
        Commit.mergeCommit(branch);
    }

    // Checkout a file to previous version.
    public static void checkout(String fileName) {
        Repository repo = loadRepo();
        File currFile = join(CWD, fileName);
        File checkoutFile = repo.filesMap.get(fileName);
        if (checkoutFile == null) {
            exitWithError("File does not exist in that commit.");
        } else {
            writeContents(currFile, readContents(checkoutFile));
            if (repo.stagedFiles.contains(fileName)) {
                repo.stagedFiles.remove(fileName);
            }
            if (repo.removedFiles.contains(fileName)) {
                repo.removedFiles.remove(fileName);
            }
            repo.saveRepo();
        }
    }

    // Checkout a file to specific version.
    public static void checkout(String hash, String fileName) {
        Repository repo = loadRepo();
        TreeMap<String, File> filesMap = BranchLogs.findBranchLogs(hash);
        File currFile = join(CWD, fileName);
        File checkoutFile = filesMap.get(fileName);
        if (checkoutFile == null) {
            exitWithError("File does not exist in that commit.");
        } else {
            writeContents(currFile, readContents(checkoutFile));
            repo.filesMap.put(fileName, checkoutFile);
            if (repo.stagedFiles.contains(fileName)) {
                repo.stagedFiles.remove(fileName);
            }
            if (repo.removedFiles.contains(fileName)) {
                repo.removedFiles.remove(fileName);
            }
            repo.saveRepo();
        }
    }

    //Checkout to other branch.
    public static void checkoutBranch(String branch) {
        Repository repo = loadRepo();
        if (repo.headBranch.equals(branch)) {
            exitWithError("No need to checkout the current branch.");
        }
        String branchHash = repo.branchesPMap.get(branch);
        if (branchHash == null) {
            exitWithError("No such branch exists.");
        }
        TreeMap<String, File> branchFilesMap = BranchLogs.findBranchLogs(branchHash);
        Repository.checkoutHelper(branch, branchFilesMap);
    }

    public static void resetHelper(String branch, TreeMap<String, File> branchFilesMap) {
        Repository repo = loadRepo();
        List<String> existFiles = plainFilenamesIn(CWD);
        for (String fileName : existFiles) {
            File file = join(CWD, fileName);
            if ((!repo.filesMap.containsKey(fileName)
                    && branchFilesMap.containsKey(fileName))) {
                exitWithError("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
            }
        }
        checkoutMap(branchFilesMap);
        repo.clear();
        repo.headBranch = branch;
        repo.filesMap = branchFilesMap;
        repo.saveRepo();
    }

    public static void checkoutHelper(String branch, TreeMap<String, File> branchFilesMap) {
        Repository repo = loadRepo();
        List<String> existFiles = plainFilenamesIn(CWD);
        for (String fileName : existFiles) {
            if (repo.stagedFiles.contains(fileName)
                    || (!repo.filesMap.containsKey(fileName)
                    && branchFilesMap.containsKey(fileName))) {
                exitWithError("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
            }
        }
        checkoutMap(branchFilesMap);
        repo.clear();
        repo.headBranch = branch;
        repo.filesMap = branchFilesMap;
        repo.saveRepo();
    }

    private static void checkoutMap(TreeMap<String, File> theMap) {
        Repository repo = loadRepo();
        List<String> existFiles = plainFilenamesIn(CWD);
        for (String fileName : existFiles) {
            File file = join(CWD, fileName);
            if (!theMap.containsKey(fileName) && repo.filesMap.containsKey(fileName)) {
                file.delete();
            }
        }
        for (String writeName : theMap.keySet()) {
            writeContents(join(CWD, writeName), readContents(theMap.get(writeName)));
        }
    }

    //When create a new branch, set a new pointer in the pointerMap.
    public void setPointer(String branch, String hash) {
        branchesPMap.put(branch, hash);
        saveRepo();
    }
    public String workingHash() {
        return branchesPMap.get(headBranch);
    }

    public void rmPointer(String branch) {
        branchesPMap.remove(branch);
        saveRepo();
    }

    public String getBranchHash(String branch) {
        return branchesPMap.get(branch);
    }

}

