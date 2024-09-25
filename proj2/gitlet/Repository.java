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
    public TreeSet<String> rmFiles = new TreeSet<>();
    public TreeSet<String> mergeStagedFiles = new TreeSet<>();
    public TreeMap<String, File> conflictMap = new TreeMap<>();
    public String headBranch;
    public static final File REPO_ROOM = join(Repository.GITLET_DIR, "repo");
    private boolean hasConflict = false;

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
        BranchLogs headBranchLog = readObject(join(LOGS_DIR, this.headBranch), BranchLogs.class);
        addRemove(hash);
        headBranchLog.add(hash, message, timeStamp, filesMap, rmFiles);
        clear();
        saveRepo();
    }

    public void mergeRecord(TreeMap<String, File> givenMap, TreeSet<String> givenDel) {
        givenMap.putAll(conflictMap);
        for (String fileName : filesMap.keySet()) {
            if (!givenMap.containsKey(fileName) && !givenDel.contains(fileName)) {
                givenMap.put(fileName, filesMap.get(fileName));
            }
        }
        filesMap = givenMap;
        saveRepo();
    }

    public void addRemove(String hash) {
        for (String file : stagedFiles) {
            File addedFile = join(CWD, file);
            String fileHash = sha1(readContents(addedFile));
            File storeFile = join(BLOBS_DIR, fileHash);
            filesMap.put(file, storeFile);
            writeContents(storeFile, readContents(addedFile));
        }
        for (String file : rmFiles) {
            filesMap.remove(file);
        }
        branchesPMap.put(this.headBranch, hash);
    }

    public void clear() {
        stagedFiles.clear();
        rmFiles.clear();
        mergeStagedFiles.clear();
        conflictMap.clear();
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
        } else if (repo.rmFiles.contains(fileName)) {
            repo.rmFiles.remove(fileName);
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
            repo.rmFiles.add(fileName);
            // Delete the file.
            if (removedFile.exists()) {
                restrictedDelete(removedFile);
            }
            repo.saveRepo();
        } else {
            exitWithError("No reason to remove the file.");
        }
    }

    public static void showStatus() {
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
        for (String removedFile : repo.rmFiles) {
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

    private static void mergeError(String branch) {
        Repository repo = loadRepo();
        if (!repo.branchesPMap.containsKey(branch)) {
            exitWithError("A branch with that name does not exists.");
        } else if (!repo.stagedFiles.isEmpty() || !repo.rmFiles.isEmpty()) {
            exitWithError("You have uncommitted changes.");
        } else if (repo.headBranch.equals(branch)) {
            exitWithError("Cannot merge a branch with itself.");
        }
        BranchLogs givenBranchLogs = BranchLogs.readBranch(branch);
        if (givenBranchLogs.parentBranch.equals(repo.branchesPMap.get(branch))) {
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
    }

    public static void mergeBranch(String branch) {
        Repository repo = loadRepo();
        mergeError(branch);
        BranchLogs givenBranchLogs = BranchLogs.readBranch(branch);
        TreeMap<String, File> givenMap = givenBranchLogs.headMap;
        TreeMap<String, File> currMap = repo.filesMap;
        if (givenBranchLogs.parentHash.equals(repo.workingHash())
                || givenBranchLogs.contains(repo.headHash())) {
            System.out.println("Current branch fast-forwarded.");
            checkoutMap(givenMap);
            repo.clear();
            repo.filesMap = givenMap;
            repo.saveRepo();
            Commit.mergeCommit(branch);
            return;
        }
        TreeSet<String> currDel = BranchLogs.rmFiles(repo.headBranch);
        TreeSet<String> givenDel = givenBranchLogs.rmFiles;
        for (String fileName : givenMap.keySet()) {  //Files modified in givenMap.
            repo = Repository.loadRepo();
            File file = join(CWD, fileName);
            if (fileCompare(givenMap.get(fileName), currMap.get(fileName))) {
                //The same content.
                continue;
            } else if (!currMap.containsKey(fileName) && !currDel.contains(fileName)) {
                //Untracked in curr
//                repo.mergeStagedFiles.add(fileName);
                writeContents(file, readContents(givenMap.get(fileName)));
//                repo.saveRepo();
                continue;
            } else {
                mergeConflict(fileName, branch, givenMap);
                continue;
            }
        }
        for (String fileName : givenDel) {  //Files delete in given
            repo = Repository.loadRepo();
            File file = join(CWD, fileName);
            if (currDel.contains(fileName)) {
                //Both delete
                continue;
            } else if (!currMap.containsKey(fileName) || currDel.contains(fileName)) {
                //Untracked or both delete
                file.delete();
                repo.rmFiles.add(fileName);
                repo.saveRepo();
            } else {
                mergeConflict(fileName, branch, givenMap);
                continue;
            }
        }
//        for (String fileName : existFiles) {
//            File file = join(CWD, fileName);
//            if (!fileCompare(givenMap.get(fileName), (splitMap.get(fileName)))
//                    && fileCompare(repo.filesMap.get(fileName), splitMap.get(fileName))) {
//                //Modified or removed in givenBranch, not in currBranch.
//                if (givenMap.containsKey(fileName)) {
//                    writeContents(file, readContents(givenMap.get(fileName)));
//                    repo.mergeStagedFiles.add(fileName);
//                } else {
//                    file.delete();
//                }
//                continue;
//            } else if (fileCompare(givenMap.get(fileName), splitMap.get(fileName))
//                    && !fileCompare(repo.filesMap.get(fileName), splitMap.get(fileName))) {
//                //Modified in currBranch, not in givenBranch.
//                if (!repo.filesMap.containsKey(fileName)) {     //NOT SURE!
//                    givenMap.remove(fileName);
//                    file.delete();
//                } else {
//                    givenMap.put(fileName, currMap.get(fileName));
//                }
//                continue;
//            } else if (!fileCompare(givenMap.get(fileName), splitMap.get(fileName))
//                    && !fileCompare(repo.filesMap.get(fileName), splitMap.get(fileName))) {
//                //Modified in both branch.
//                String givenContents = "";
//                String currContents = "";
//                if (givenMap.containsKey(fileName)) {
//                    givenContents = readContentsAsString(givenMap.get(fileName));
//                } else if (!repo.filesMap.containsKey(fileName)) {
//                    //Deleted in both branch, but somehow created.
//                    file.delete();
//                }
//                if (repo.filesMap.containsKey(fileName)) {
//                    currContents = readContentsAsString(file);
//                }
//                String res = "<<<<<<< HEAD\n" + currContents
//                        + "=======\n" + givenContents + ">>>>>>>\n";
//                String fileHash = sha1(res);
//                File storeFile = join(BLOBS_DIR, fileHash);
//                givenMap.put(fileName, storeFile);
//                writeContents(join(BLOBS_DIR, fileHash), res);
//                writeContents(file, res);
//                hasConflict = true;
//                continue;
//            }
//        }
//        for (String addFileName : givenMap.keySet()) {
//            if (!existFiles.contains(addFileName)) {
//                if(fileCompare(splitMap.get(addFileName),givenMap.get(addFileName))
//                        && !currMap.containsKey(addFileName)) {
//                    continue;
//                }
//                File addFile = join(CWD, addFileName);
//                writeContents(addFile, readContents(givenMap.get(addFileName)));
//            }
//        }
//        if (hasConflict) {
//            System.out.println("Encountered a merge conflict.");
//        }
//        repo.filesMap = givenMap;
//        repo.clear();
//        repo.saveRepo();
////        BranchLogs.mergeSplit(givenBranchLogs.headHash, branch);
//        Commit.mergeCommit(branch);
        if (Repository.loadRepo().hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
        repo.mergeRecord(givenMap, givenDel);
        Commit.mergeCommit(branch);
    }

    private static void mergeConflict(String fileName, String branch, TreeMap<String, File> givenMap) {
        Repository repo = loadRepo();
        String givenContents = "";
        String currContents = "";
        File file = join(CWD, fileName);
        if (givenMap.containsKey(fileName)) {
            givenContents = readContentsAsString(givenMap.get(fileName));
        }
        if (repo.filesMap.containsKey(fileName)) {
            currContents = readContentsAsString(file);
        }
        String res = "<<<<<<< HEAD\n" + currContents
                + "=======\n" + givenContents + ">>>>>>>\n";
        String fileHash = sha1(res);
        File storeFile = join(BLOBS_DIR, fileHash);
        repo.conflictMap.put(fileName, storeFile);
        repo.hasConflict = true;
        repo.saveRepo();
        writeContents(join(BLOBS_DIR, fileHash), res);
        writeContents(file, res);
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
            if (repo.rmFiles.contains(fileName)) {
                repo.rmFiles.remove(fileName);
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
            if (repo.rmFiles.contains(fileName)) {
                repo.rmFiles.remove(fileName);
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

    public String headHash() {
        return branchesPMap.get(headBranch);
    }

    public TreeSet<String> getDelFiles() {
        return BranchLogs.readBranch(headBranch).rmFiles;

    }


}

