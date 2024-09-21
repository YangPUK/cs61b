package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import static gitlet.Utils.*;

public class Info implements Serializable {
    public LinkedList<Node> logsList = new LinkedList<>();
    public int size = 0;
    public HashMap<File, File> filesMap = new HashMap<>();
    public LinkedList<String> Branches = new LinkedList<>();
    public LinkedList<String> stagedFiles = new LinkedList<>();
    public LinkedList<String> removedFiles = new LinkedList<>();


    public File current_DIR = join(Repository.BLOBS_DIR, String.valueOf(size));
    public static final File infoRoom = join(Repository.GITLET_DIR, "repo");

    private static class Node implements Serializable {
        String hash;
        HashMap<File, File> filesMap;

        public Node(String hash, HashMap<File, File> filesMap) {
            this.hash = hash;
            this.filesMap = filesMap;
        }
    }

    public Info(){
        if(!current_DIR.exists()) {
            current_DIR.mkdir();
        }
        Branches.add("*master");
    }

    public void saveInfo() {
        writeObject(infoRoom, this);
    }

    public static Info loadInfo() {
        return readObject(infoRoom, Info.class);
    }

    public void record(String hash) {
        Node node = new Node(hash, filesMap);
        logsList.add(node);
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
        // Not satged.
        File oddFile = info.filesMap.get(addedFile);
        String addedContent = readContentsAsString(addedFile);
        if (!readContentsAsString(oddFile).equals(addedContent)) {
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
}
