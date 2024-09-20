package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import static gitlet.Utils.join;
import static gitlet.Utils.*;

public class Info implements Serializable {
    public LinkedList<Node> logsList = new LinkedList<>();
    public int size = 0;
    public HashMap<String, String> filesMap = new HashMap<>();
    public File current_DIR = join(Repository.BLOBS_DIR, String.valueOf(size));
    public static final File infoRoom = join(Repository.GITLET_DIR, "repo");

    private static class Node implements Serializable {
        String hash;
        HashMap<String, String> filesMap;

        public Node(String hash, HashMap<String, String> filesMap) {
            this.hash = hash;
            this.filesMap = filesMap;
        }
    }

    public Info(){
        if(!current_DIR.exists()) {
            current_DIR.mkdir();
        }
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
        size++;
        current_DIR = join(Repository.BLOBS_DIR, String.valueOf(size));
        current_DIR.mkdir();
        this.saveInfo();
    }
}
