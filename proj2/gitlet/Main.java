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
                Repository.addFile(args[1]);
                break;
            case "rm":
                needInit();
                validateNumArgs("rm", args, 2);
                Repository.removeFile(args[1]);
                break;
            case "commit":
                needInit();
                validateNumArgs("commit", args, 2);
                Commit.makeCommit(args[1]);
                break;
            case "log":
                needInit();
                validateNumArgs("log", args, 1);
                Commit.showLogs();
                break;
            case "global-log":
                needInit();
                validateNumArgs("log-global", args, 1);
                Commit.showGlobalLogs();
                break;
            case "status":
                needInit();
                validateNumArgs("status", args, 1);
                Repository.showStatus();
                break;
            case "branch":
                needInit();
                validateNumArgs("branch", args, 2);
                Repository.createBranch(args[1]);
                break;
            case "rm-branch":
                needInit();
                validateNumArgs("rm-branch", args, 2);
                Repository.rmBranch(args[1]);
                break;
            case "find":
                needInit();
                validateNumArgs("find", args, 2);
                BranchLogs.find(args[1]);
                break;
            case "checkout":
                needInit();
                validateNumArgs("checkout", args, 2);
                if (args.length == 2) {
                    // checkout [branch name]
                    Repository.checkoutBranch(args[1]);
                }
                else if (args.length == 3) {
                    // checkout -- [file name]
                    if(!args[1].equals("--")) {
                        Utils.exitWithError("Incorrect operands.");
                    } else {
                        Repository.checkout(args[2]);
                    }
                }
                else if (args.length == 4) {
                    //checkout [commit id] -- [file name]
                    if(!args[2].equals("--")) {
                        Utils.exitWithError("Incorrect operands.");
                    } else {
                        Repository.checkout(args[1], args[3]);
                    }
                }
                break;
            case "reset":
                needInit();
                validateNumArgs("reset", args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                needInit();
                validateNumArgs("merge", args, 2);
                Repository.mergeBranch(args[1]);
                break;
            default:
                Utils.exitWithError("No command with that name exists.");
        }
    }

    private static void needInit() {
        if (!Repository.GITLET_DIR.exists()) {
            Utils.exitWithError("Not in an initialized Gitlet directory.");
        }
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        switch (cmd) {
            case "checkout":
                if (args.length < n || args.length > n + 2) {
                    Utils.
                            exitWithError("Incorrect operands.");
                }
                break;
            default:
                if (args.length != n) {
                    Utils.exitWithError("Incorrect operands.");
                }
                break;
        }
    }
}


