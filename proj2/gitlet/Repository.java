package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;

import static gitlet.Utils.*;


// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File LOGS_DIR = join(GITLET_DIR, "logs");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
//    public static final File master = join(LOGS_DIR, "master");

    private TreeMap<String, String> branchesPMap;   //Pointer Map
    public TreeMap<File, File> filesMap;
    public TreeSet<String> stagedFiles = new TreeSet<>();
    public TreeSet<String> removedFiles = new TreeSet<>();
    public TreeSet<String> mergeStagedFiles = new TreeSet<>();
    public String workingBranch;
    public String head;
    public static final File repoRoom = join(Repository.GITLET_DIR, "repo");

    public static void setupPeresitence() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            LOGS_DIR.mkdir();
            BLOBS_DIR.mkdir();
            Commit.setup();
        } else {
            exitWithError("A Gitlet version-control system already exists in the current directory.");
        }
    }

    public Repository() {
        branchesPMap = new TreeMap<>();
        branchesPMap.put("master", null);
        workingBranch = "master";
        head = "master";
        filesMap = new TreeMap<>();
    }

    public void saveRepo() {
        writeObject(repoRoom, this);
    }

    public static Repository loadRepo() {
        return readObject(repoRoom, Repository.class);
    }

    public void record(String hash, String message, String timeStamp) {
        BranchLogs headBranch = readObject(join(LOGS_DIR, workingBranch), BranchLogs.class);
        for (String file : stagedFiles) {
            File addedFile = join(CWD, file);
            String fileHash = sha1(readContents(addedFile));
            File storeFile = join(BLOBS_DIR, fileHash);
            filesMap.put(addedFile, storeFile);
            writeContents(storeFile, readContents(addedFile));
        }
        for (String file : removedFiles) {
            File removedFile = join(CWD, file);
            filesMap.remove(removedFile);
            // Delete the file.
            if (removedFile.exists()) {
                restrictedDelete(removedFile);
            }
        }
        headBranch.add(hash, message, timeStamp, filesMap);
        branchesPMap.put(workingBranch, hash);
        clear();
        headBranch.saveBranch();
        this.saveRepo();
    }

    public void clear() {
        stagedFiles.clear();
        removedFiles.clear();
    }

    private static boolean fileCompare(File file1, File file2) {
        try {
            if (file1.length() != file2.length()) {
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
        }
        //Staged
        else if (repo.stagedFiles.contains(fileName)) {
            return;
        }
//        String hash = sha1(readContents(addedFile));
//        File storeFile = join(BLOBS_DIR, hash);
        // Not tracked.
        if (!repo.filesMap.containsKey(addedFile)) {
            // Remove and add the same file.
            if (repo.removedFiles.contains(fileName) &&
                    fileCompare(repo.filesMap.get(fileName), addedFile)) {
                repo.removedFiles.remove(fileName);
                return;
            }
            repo.stagedFiles.add(fileName);
//            repo.filesMap.put(addedFile, storeFile);
            repo.saveRepo();
            return;
        }
        // Already staged.
        File oddFile = repo.filesMap.get(addedFile);
        if (!fileCompare(oddFile, addedFile)) {
            repo.stagedFiles.add(fileName);
//            repo.filesMap.put(addedFile, storeFile);
            repo.saveRepo();
            return;
        }
    }

    public static void removeFile(String fileName) {
        Repository repo = loadRepo();
        File removedFile = join(CWD, fileName);
        if (!removedFile.exists()) {
            exitWithError("File does not exist.");
        }
        // Staged.
        else if (repo.stagedFiles.contains(fileName)) {
            repo.stagedFiles.remove(fileName);
            repo.saveRepo();
        }
        // Not Staged, but tracked.
        else if (repo.filesMap.containsKey(removedFile)) {
            repo.removedFiles.add(fileName);
            repo.saveRepo();
        } else {
            exitWithError("No reason to remove the file.");
        }
    }

    public static void showStatus(){
        Repository repo = loadRepo();
        System.out.println("===Branches===");
        for(String branch : repo.branchesPMap.keySet()){
            if (branch.equals(repo.workingBranch)) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println("\n===stagedFiles===");
        for(String stagedFile : repo.stagedFiles){
            System.out.println(stagedFile);
        }
        System.out.println("\n===removedFiles===");
        for(String removedFile : repo.removedFiles){
            System.out.println(removedFile);
        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        System.out.println("\n=== Untracked Files ===");
    }

    public static void createBranch(String branch) {
        Repository repo = loadRepo();
        if (repo.branchesPMap.containsKey(branch)) {
            exitWithError("A branch with that name already exists.");
        }
        repo.setPointer(branch, repo.headHash());
        BranchLogs branchLogs = new BranchLogs(branch, repo.workingBranch, repo.headHash());
        repo.workingBranch = branch;
        branchLogs.saveBranch();
        repo.saveRepo();
    }

    public static void rmBranch(String branch) {
        Repository repo = loadRepo();
        if (repo.workingBranch.equals(branch) || branch.equals("master")) {
            exitWithError("Cannot remove the current branch.");
        }
        else if (!repo.branchesPMap.containsKey(branch)) {
            exitWithError("A branch with that name does not exists");
        }
        repo.branchesPMap.remove(branch);
        repo.saveRepo();
    }

    public static void mergeBranch(String branch) {
        Repository repo = loadRepo();
        BranchLogs givenBranchLogs = BranchLogs.readBranch(branch);
        BranchLogs currBranchLogs = BranchLogs.readBranch(repo.workingBranch);
        if (currBranchLogs.contains(repo.branchesPMap.get(branch))) {
            System.out.println("Given branch is an ancestor of" +
                    " the current branch.");
            return;
        }
        else if (givenBranchLogs.parentHash.equals(repo.headHash())) {
            checkout(branch);
            System.out.println("Current branch fast-forwarded.");
            return;
        } else {
            List<String> existFiles = plainFilenamesIn(CWD);
            TreeMap<File, File> filesMap = givenBranchLogs.findBranchLogs(branch);
            TreeMap<File, File> splitTreeMap = currBranchLogs.findBranchLogs(givenBranchLogs.parentHash);
            for (String fileName : existFiles) {
                File file = join(CWD, fileName);
                if (!repo.filesMap.get(file).equals(filesMap.get(file)) &&
                        repo.filesMap.get(file).equals(splitTreeMap.get(file))) {
                    repo.mergeStagedFiles.add(fileName);
                    writeContents(file, filesMap.get(file));
                } else if (!repo.filesMap.get(file).equals(splitTreeMap.get(file)) &&
                        splitTreeMap.get(file).equals(filesMap.get(file))) {
                    continue;
                } else {
                    return;
                }

            }
        }






    }

    public static void checkout(String fileName) {
        Repository repo = loadRepo();
        File currFile = join(CWD, fileName);
        File checkoutFile = repo.filesMap.get(currFile);
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

    public static void checkout(String hash, String fileName) {
        Repository repo = loadRepo();
        TreeMap<File, File> filesMap = BranchLogs.findBranchLogs(hash);
        File currFile = join(CWD, fileName);
        File checkoutFile = filesMap.get(currFile);
        if (checkoutFile == null) {
            exitWithError("File does not exist in that commit.");
        } else {
            writeContents(currFile, readContents(checkoutFile));
            repo.filesMap.put(currFile, checkoutFile);
            if (repo.stagedFiles.contains(fileName)) {
                repo.stagedFiles.remove(fileName);
            }
            if (repo.removedFiles.contains(fileName)) {
                repo.removedFiles.remove(fileName);
            }
            repo.saveRepo();
        }

    }

    public static void checkoutBranch(String branch) {
        Repository repo = loadRepo();
        if (repo.workingBranch.equals(branch)) {
            exitWithError("No need to checkout the current branch.");
        }
        BranchLogs branchLogs = BranchLogs.readBranch(branch);
        TreeMap<File, File> filesMap = branchLogs.branchList.getFirst().filesMap;
        List<String> existFiles = plainFilenamesIn(CWD);
        for (String fileName : existFiles) {
            File existFile = join(CWD, fileName);
            if (repo.stagedFiles.contains(existFile) ||
                    (!repo.filesMap.containsKey(existFile) &&
                    filesMap.containsKey(existFile)) ) {
                exitWithError("There is an untracked file in the way;" +
                        " delete it, or add and commit it first.");
            }
        }
        for (File file : filesMap.keySet()) {
            writeContents(file, readContents(filesMap.get(file)));
        }
        repo.clear();
        repo.workingBranch = branch;
        repo.filesMap = filesMap;
        repo.saveRepo();
    }

    public static void reset(String commitHash) {
        BranchLogs.findBranchLogs(commitHash);
    }

    //When create a new branch, set a new pointer in the pointerMap.
    public void setPointer(String branch, String hash) {
        branchesPMap.put(branch, hash);
    }
    public String headHash() {
        return branchesPMap.get(workingBranch);
    }
}

