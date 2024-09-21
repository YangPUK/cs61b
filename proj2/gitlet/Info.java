package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;

import static gitlet.Utils.*;

public class Info implements Serializable {
    public TreeMap<String, LinkedList<Node>> branchesMap = new TreeMap<>();
    private TreeMap<String, String> pointerMap = new TreeMap<>();
    private int size = 0;
    public TreeMap<File, File> filesMap = new TreeMap<>();
    public TreeSet<String> Branches = new TreeSet<>();
    public TreeSet<String> stagedFiles = new TreeSet<>();
    public TreeSet<String> removedFiles = new TreeSet<>();
    private String headBranch;


    public File current_DIR = join(Repository.BLOBS_DIR, String.valueOf(size));
    public static final File infoRoom = join(Repository.GITLET_DIR, "repo");

    private static class Node implements Serializable {
        String hash;
        String parent;
        String commit;
        TreeMap<File, File> filesMap;

        public Node(String hash, String commit, String parent, TreeMap<File, File> filesMap) {
            this.hash = hash;
            this.commit = commit;
            this.parent = parent;
            this.filesMap = filesMap;
        }
    }

    public Info(){
        if(!current_DIR.exists()) {
            current_DIR.mkdir();
        }
        headBranch = "master";
        Branches.add(headBranch);
    }

    public void saveInfo() {
        writeObject(infoRoom, this);
    }

    public static Info loadInfo() {
        return readObject(infoRoom, Info.class);
    }

    public void record(String hash, String commit, String branch) {
        LinkedList<Node> branchList = branchesMap.get(branch);
        // Init commit?
        if(branchList == null) {
            branchList = new LinkedList<>();
            pointerMap.put(branch, null);
        }
        String parent = pointerMap.get(branch);
        pointerMap.put(branch, hash);
        Node node = new Node(hash, commit, parent, this.filesMap);
        branchList.addLast(node);
        branchesMap.put(headBranch, branchList);
        current_DIR.mkdir();
        for (String file : stagedFiles) {
            File addedFile = join(Repository.CWD, file);
            File storeFile = filesMap.get(addedFile);
            File parentDIR = storeFile.getParentFile();
            if (!parentDIR.exists()) {
                parentDIR.mkdirs();
            }
            writeContents(storeFile, readContents(addedFile));
        }
        size++;
        current_DIR = join(Repository.BLOBS_DIR, String.valueOf(size));
        clear();
        this.saveInfo();
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
        Info info = loadInfo();
        File addedFile = join(Repository.CWD, fileName);
        File storeFile = join(info.current_DIR, fileName);
        if (!addedFile.exists()) {
            exitWithError("File does not exist.");
        }
        // File not in files map.
        else if (!info.filesMap.containsKey(addedFile)) {
            info.stagedFiles.add(fileName);
            info.filesMap.put(addedFile, storeFile);
            info.saveInfo();
            return;
        }
        // Already staged.
        else if (info.stagedFiles.contains(fileName)) {
            return;
        }
        // Not staged.
        File oddFile = info.filesMap.get(addedFile);
//        String addedContent = readContentsAsString(addedFile);
        if (Info.fileCompare(oddFile, addedFile)) {
            info.stagedFiles.add(fileName);
            info.filesMap.put(addedFile, storeFile);
            info.saveInfo();
            return;
        }
    }

    public static void removeFile(String fileName) {
        Info info = loadInfo();
        File removedFile = join(Repository.CWD, fileName);
        if (!removedFile.exists()) {
            exitWithError("File does not exist.");
        }
        // Staged.
        if (info.stagedFiles.contains(fileName)) {
            info.stagedFiles.remove(fileName);
            info.filesMap.remove(removedFile);
            info.saveInfo();
            return;
        }
        // Not Staged, but tracked.
        else if (info.filesMap.containsKey(removedFile)) {
            info.filesMap.remove(removedFile);
            info.removedFiles.add(fileName);
            // Delete the file.
            if (removedFile.exists()) {
                restrictedDelete(removedFile);
            }
            info.saveInfo();
            return;
        } else {
            exitWithError("No reason to remove the file.");
        }
    }

    public static void showStatus(){
        Info info = loadInfo();
        System.out.println("===Branches===\n");
        for(String branch : info.Branches){
            if (branch.equals(info.headBranch)) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println("\n===stagedFiles===\n");
        for(String stagedFile : info.stagedFiles){
            System.out.println(stagedFile);
        }
        System.out.println("\n===removedFiles===\n");
        for(String removedFile : info.removedFiles){
            System.out.println(removedFile);
        }
    }

    public String getHeadBranch() {
        return headBranch;
    }

    public static void creatBranch(String branch) {
        Info info = loadInfo();
        if (info.pointerMap.containsKey(branch)) {
            exitWithError("A branch with that name already exists.");
        }
        info.setPointer(branch, info.headHash());
        info.headBranch = branch;
        info.saveInfo();
    }

    public static void rmBranch(String branch) {
        Info info = loadInfo();
        if (!info.pointerMap.containsKey(branch)) {
            exitWithError("A branch with that name does not exists");
        }
        if (info.headBranch.equals(branch)) {
            exitWithError("Cannot remove the current branch.");
        }
        info.pointerMap.remove(branch);
        info.saveInfo();
    }



    //When create a new branch, set a new pointer in the pointerMap.
    public void setPointer(String branch, String hash) {
        pointerMap.put(branch, hash);
    }
    public String headHash() {
        return pointerMap.get(headBranch);
    }
}
