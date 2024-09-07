package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author ebean
 */


public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.exitWithError("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs("init", args, 1);
                Repository.setupPeresitence();
                break;
            case "add":
                needInit();
                validateNumArgs("add", args, 2);
                Repository.addFiles(args[1]);
                break;
            case "commit":
                needInit();
                validateNumArgs("commit", args, 2);
                Commit.mCommit(args[1]);
                break;
        }
        Utils.exitWithError("No command with that name exists");
    }

    private static void needInit() {
        if (!Repository.GITLET_DIR.exists()) {
            Utils.exitWithError("Not in an initialized Gitlet directory.");
        }
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        switch (cmd) {
            case "init", "commit", "add":
                if (args.length != n) {
                    Utils.exitWithError("Incorrect operands.");
                }
                break;
            case "whatever":
                if (args.length < n) {
                    Utils.exitWithError("Incorrect operands.");
                }
                break;
        }
    }
}


