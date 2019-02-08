import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Assign3 {
    private static ArrayList<String> commandHistory = new ArrayList<>();

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.printf("[%s]: ", System.getProperty("user.dir"));

            String command = input.nextLine();
            if (!command.isEmpty()) {
                commandHistory.add(command);
                runCommand(command);
            }
        }
    }

    /**
     * Runs the specified command if valid.
     *
     * @param command The command to run.
     */
    private static void runCommand(@NotNull String command) {
        if (!command.isEmpty()) {  // Ensure at least command name (commandArgs[0]) is included.
            String[] commandArgs = splitCommand(command);
            switch (commandArgs[0]) {  // Test if commandArgs[0] is a built in shell command:
                case "ptime":               //   If so, perform that operation
                    showProcessTime();
                    break;
                case "history":
                    listHistory();
                    break;
                case "^":
                    int commandNum = commandArgs.length > 1 ? Integer.valueOf(commandArgs[1]) : 0;
                    boolean numInBounds = (0 < commandNum && commandNum <= commandHistory.size());

                    if (numInBounds) {
                        boolean willLoop = commandHistory.get(commandNum - 1).equals("^ " + commandNum);
                        if (!willLoop) {
                            runCommand(commandHistory.get(commandNum - 1));
                        } else {
                            System.out.printf("Command \"%s\" would created an infinite loop. Command not executed\n",
                                    command);
                        }
                    }
                    break;
                case "list":
                    listDirectory();
                    break;
                case "cd":
                    String toPath = commandArgs.length > 1 ? commandArgs[1] : "~";
                    changeDirectory(toPath);
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:                     //   Else, attempt to execute it as an external program
                    runAsExternal(commandArgs);
                    break;
            }

        }
    }

    /**
     * Display the number of seconds (4 digits past the decimal) spent executing (waiting for) child process.
     * Command to execute: ptime
     */
    private static void showProcessTime() {
        //TODO: "ptime": Display the number of seconds (4 digits past the decimal) spent executing (waiting for) child process.
        System.out.println("showProcessTime function called");
    }

    /**
     * Show the command history
     * Command to execute: history
     */
    private static void listHistory() {
        System.out.println("-- Command History --");
        for (int i = 0; i < commandHistory.size(); ++i) {
            System.out.println((i + 1) + " : " + commandHistory.get(i));
        }
    }

    /**
     * List the contents of the current directory.
     * Command to execute: list
     */
    private static void listDirectory() {
        File currentDir = new File(System.getProperty("user.dir"));


        for (File file : currentDir.listFiles()) {
            printFileInfo(file);
        }
    }

    /**
     * Print the information of the specified file into a format similar to the "ls -l" bash command.
     * (i.e. "[permissions for current user] [size in bytes] [date last modified] [name of file]").
     *
     * @param file The file to print information about.
     */
    private static void printFileInfo(@NotNull File file) {
        char d = file.isDirectory() ? 'd' : '-';
        char r = file.canRead() ? 'r' : '-';
        char w = file.canWrite() ? 'w' : '-';
        char x = file.canExecute() ? 'x' : '-';

        Date lastModifiedDate = new Date(file.lastModified());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm");

        System.out.printf("%c%c%c%c %10s %s %s", d, r, w, x, file.length(), dateFormat.format(lastModifiedDate),
                file.getName());
        System.out.println();
    }

    /**
     * Change the directory to specified location or to home directory.
     * Command to execute: cd [path] (no path defaults to "~")
     *
     * @param path The path of the directory to change to.
     */
    private static void changeDirectory(@NotNull String path) {
        if (path.equals("~")) {
            // Change to home directory
            System.setProperty("user.dir", System.getProperty("user.home"));
        } else if (path.equals("..")) {
            // TODO: "cd": Change to specified path of directory
            // Check if specified path exists
            //      If it does, change to it
            //      Else, print an error message
        }
    }

    //TODO: "|": Pipe between two external commands.

    private static void runAsExternal(@NotNull String[] commandArgs) {
        //TODO: runAsExternal(): Run external command if valid

        System.out.printf("runAsExternal called on command %s\n", commandArgs[0]);

    }


    /**
     * Split the user command by spaces, but preserving them when inside double-quotes.
     * Code Adapted from:
     * https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-
     * single-or-double
     */
    @NotNull
    private static String[] splitCommand(String command) {
        java.util.List<String> matchList = new java.util.ArrayList<>();

        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(command);

        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                matchList.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                matchList.add(regexMatcher.group());
            }
        }

        return matchList.toArray(new String[matchList.size()]);
    }
}
