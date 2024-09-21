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
    public static final File GITLET_DIR = join(CWD, ".gitlets");
    public static final File LOGS_DIR = join(GITLET_DIR, "logs");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File master = join(LOGS_DIR, "master");

    private TreeMap<String, String> branchesPMap;   //Pointer Map
    public TreeMap<File, File> filesMap;
    public TreeSet<String> stagedFiles = new TreeSet<>();
    public TreeSet<String> removedFiles = new TreeSet<>();
    public String head;
    private int size;
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
        BranchLogs headBranch = readObject(join(BLOBS_DIR, head), BranchLogs.class);
        headBranch.add(hash, message, timeStamp, filesMap);
        branchesPMap.put(head, hash);
        for (String file : stagedFiles) {
            File addedFile = join(CWD, file);
            File storeFile = filesMap.get(addedFile);
            writeContents(storeFile, readContents(addedFile));
        }
        size++;
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
        String hash = sha1(readContents(addedFile));
        File storeFile = join(BLOBS_DIR, hash);
        // Not tracked.
        if (!repo.filesMap.containsKey(addedFile)) {
            repo.stagedFiles.add(fileName);
            repo.filesMap.put(addedFile, storeFile);
            repo.saveRepo();
            return;
        }
        // Already staged.
        else if (repo.stagedFiles.contains(fileName)) {
            return;
        }
        // Not staged.
        File oddFile = repo.filesMap.get(addedFile);
//        String addedContent = readContentsAsString(addedFile);
        if (fileCompare(oddFile, addedFile)) {
            repo.stagedFiles.add(fileName);
            repo.filesMap.put(addedFile, storeFile);
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
        if (repo.stagedFiles.contains(fileName)) {
            repo.stagedFiles.remove(fileName);
            repo.filesMap.remove(removedFile);
            repo.saveRepo();
            return;
        }
        // Not Staged, but tracked.
        else if (repo.filesMap.containsKey(removedFile)) {
            repo.filesMap.remove(removedFile);
            repo.removedFiles.add(fileName);
            // Delete the file.
            if (removedFile.exists()) {
                restrictedDelete(removedFile);
            }
            repo.saveRepo();
            return;
        } else {
            exitWithError("No reason to remove the file.");
        }
    }

    public static void showStatus(){
        Repository repo = loadRepo();
        System.out.println("===Branches===\n");
        for(String branch : repo.branchesPMap.keySet()){
            if (branch.equals(repo.head)) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println("\n===stagedFiles===\n");
        for(String stagedFile : repo.stagedFiles){
            System.out.println(stagedFile);
        }
        System.out.println("\n===removedFiles===\n");
        for(String removedFile : repo.removedFiles){
            System.out.println(removedFile);
        }
    }

    public static void creatBranch(String branch) {
        Repository repo = loadRepo();
        if (repo.branchesPMap.containsKey(branch)) {
            exitWithError("A branch with that name already exists.");
        }
        repo.setPointer(branch, repo.headHash());
        BranchLogs branchLogs = new BranchLogs(branch, repo.head, repo.headHash());
        repo.head = branch;
        branchLogs.saveBranch();
        repo.saveRepo();
    }

    public static void rmBranch(String branch) {
        Repository repo = loadRepo();
        if (repo.head.equals(branch)) {
            exitWithError("Cannot remove the current branch.");
        }
        else if (!repo.branchesPMap.containsKey(branch)) {
            exitWithError("A branch with that name does not exists");
        }
        repo.branchesPMap.remove(branch);
        repo.saveRepo();
    }

    //When create a new branch, set a new pointer in the pointerMap.
    public void setPointer(String branch, String hash) {
        branchesPMap.put(branch, hash);
    }
    public String headHash() {
        return branchesPMap.get(head);
    }
}

