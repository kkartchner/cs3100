import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

/**
 * Universal shell/command line interpreter for Windows, Linux, and macOS. Prompts with the current directory and
 * allows for the following commands and functionality:
 * "ptime"                       - Prints the total amount of time the shell has waited for child processes
 * "history"                     - Prints a list of commands that have been entered
 * "^ [N]"                       - Execute command number [N] from command history.
 * "list"                        - Print contents of the current directory in form similar to UNIX ls -l command
 * (i.e. "[permissions for user] [size in bytes] [date last modified] [name of file]"
 * "cd [directory name]"         - Change to specified directory or change to home directory if "cd" by itself.
 * "[command 1] | [command 2]"   - Pipe the output of the first external command to the input of the second.
 * "exit"                        - Terminates the shell.
 *
 * @author Ky Kartchner
 */
public class Assign3 {
    /**
     * Stores the command history
     */
    private static ArrayList<String> commandHistory = new ArrayList<>();

    /**
     * Stores the total time spent in (waiting for) child processes
     */
    private static long totalChildProcessTime = 0;

    /**
     * Run shell prompt until exited.
     *
     * @param args (not used)
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.printf("[%s]: ", System.getProperty("user.dir")); // Print "[current directory]: "

            String command = input.nextLine(); // Get user input
            if (!command.isEmpty()) {          // Only run non-blank input:
                commandHistory.add(command);
                runCommand(command);
            }
        }
    }

    /**
     * Calls the appropriate method to run the specified command if valid.
     *
     * @param command The command to run.
     */
    private static void runCommand(String command) {
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
        System.out.printf("Total time in child processes: %.4f\n", totalChildProcessTime / 1000.0);
    }

    /**
     * Show the command history
     * Command to execute: "history"
     */
    private static void listHistory() {
        System.out.println("-- Command History --");
        for (int i = 0; i < commandHistory.size(); ++i) {
            System.out.println((i + 1) + " : " + commandHistory.get(i));
        }
    }

    /**
     * List the contents of the current directory.
     * Command to execute: "list"
     */
    private static void listDirectory() {
        File currentDir = new File(System.getProperty("user.dir"));
        if (currentDir.listFiles() != null) {
            for (File file : currentDir.listFiles()) {
                printFileInfo(file);
            }
        }
    }

    /**
     * Helper function to print the information of the specified file into a format similar to the "ls -l" bash
     * command.(i.e. "[permissions for current user] [size in bytes] [date last modified] [name of file]").
     *
     * @param file The file to print information about.
     */
    private static void printFileInfo(File file) {
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
     * Command to execute: cd [directory name] (no path defaults to "~")
     *
     * @param path The path of the directory to change to.
     */
    private static void changeDirectory(String path) {
        if (path.equals("~")) {                                     // Change to home directory
            System.setProperty("user.dir", System.getProperty("user.home"));
        } else if (path.equals("..")) {                             // Change to parent directory
            File currentDir = new File(System.getProperty("user.dir"));
            if (currentDir.getParent() != null) {
                System.setProperty("user.dir", currentDir.getParent());
            }
        } else {                                                    // Change to specified directory if valid:
            String currentDir = System.getProperty("user.dir");     //      Store name of current directory
            File proposed = new File(currentDir + "/" + path); // Create File for checking if path exists

            if (proposed.exists()) {                                // Check if specified path exists
                if (proposed.isDirectory()) {                       //      If it is a directory, then change to it
                    Path directoryPath = Paths.get(currentDir, path);
                    System.setProperty("user.dir", directoryPath.toString());
                } else {                                            //      Otherwise print an error.
                    System.out.printf("cd failed: \"%s\" is not a directory.\n", path);
                }
            } else {                                                // Else, print an error message
                System.out.printf("cd failed: Directory \"%s\" not found. Does it exist?\n", path);
            }
        }
    }

    /**
     * Runs the specified command as a single external command (or as two piped commands if args contain "|").
     * Shell waits for child processes to finish before allowing for further commands unless ampersand(&) is present at
     * the end of the command.
     *
     * @param command The command to execute.
     */
    private static void runAsExternal(String[] command) {
        boolean noWait = command[command.length - 1].equals("&"); // No waiting if last argument is ampersand
        if (noWait) {       // Prevent wait ampersand (&) from being passed in as an argument if needed
            command[command.length - 1] = "";
        }

        int indexOfPipe = command.length;
        for (int i = 0; i < command.length; ++i) {
            if (command[i].equals("|")) {
                indexOfPipe = i;
                break;
            }
        }

        boolean piping = (indexOfPipe != command.length);
        if (piping) {
            pipeExternal(command, indexOfPipe, noWait);
        } else {
            runExternal(command, noWait);
        }
    }

    /**
     * Pipe two external commands together.
     *
     * @param command     The full command being run.,
     * @param indexOfPipe The index of the pipe symbol ("|") in the command array.
     * @param noWait      If true the shell will not wait for child process to finish. Otherwise, it will.
     */
    private static void pipeExternal(String[] command, int indexOfPipe, boolean noWait) {
        // Copy strings on left side of "|" in command array into command1 sub array:
        String[] command1 = new String[indexOfPipe];
        System.arraycopy(command, 0, command1, 0, command1.length);

        // Copy strings on right side of "|" in command array into command2 sub array:
        String[] command2 = new String[Math.abs(command.length - indexOfPipe - 1)];
        System.arraycopy(command, indexOfPipe + 1, command2, 0, command2.length);

        // Create process builders:
        ProcessBuilder processBuilder1 = new ProcessBuilder(command1);
        ProcessBuilder processBuilder2 = new ProcessBuilder(command2);

        // Redirect input and output to properly inherit from shell:
        processBuilder1.redirectInput(ProcessBuilder.Redirect.INHERIT);
        processBuilder2.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        // Make both process builders inherit their starting directory from the current directory of the shell:
        File currentDir = new File(System.getProperty("user.dir"));
        processBuilder1.directory(currentDir);
        processBuilder2.directory(currentDir);

        try { // Try to run the processes
            long start = System.currentTimeMillis(); // Save current time for start time
            Process process1 = processBuilder1.start(); // Start both processes:
            Process process2 = processBuilder2.start();

            pipeProcesses(process1, process2);      // Pipe process1's output to process2's input


            if (!noWait) {                          // Wait if noWait is false:
                process1.waitFor();
                process2.waitFor();
            }

            long end = System.currentTimeMillis();   // Save current time for end time
            totalChildProcessTime += (end - start);  // Add the elapsed time (end - start) to total time

        } catch (Exception e) {
            System.out.println("Problem with piping: " + e);

        }
    }

    /**
     * Helper function to write the output of a process to the input of another process.
     *
     * @param p1 The "input from" process.
     * @param p2 The "output to" process.
     * @throws Exception Throws any read exceptions to be caught by pipeExternal parent function.
     */
    private static void pipeProcesses(Process p1, Process p2) throws Exception {
        // Write output of p1 to input of p2:
        int data;
        while ((data = p1.getInputStream().read()) != -1) { // Read from p1's input stream while it still has data
            p2.getOutputStream().write(data);               //      Then write that date to p2's output stream
        }
        p2.getOutputStream().flush();                    // Flush and close p2's output stream:
        p2.getOutputStream().close();
    }

    /**
     * Runs a single process from the specified command.
     *
     * @param command The command to run.
     * @param noWait  If true the shell will not wait for child process to finish. Otherwise, it will.
     */
    private static void runExternal(String[] command, boolean noWait) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT); // Inherit from shell's input.
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT); // Output to shell's output.
        // Set new process directory to shell's current directory:
        processBuilder.directory(new File(System.getProperty("user.dir")));

        try {                                       // Try to run the process:
            long start = System.currentTimeMillis();    // Save current time for start time
            Process process = processBuilder.start();  // Start the process

            if (!noWait) {                           // Wait if noWait is false:
                process.waitFor();
            }

            long end = System.currentTimeMillis();   // Save current time for end time
            totalChildProcessTime += (end - start);  // Add the elapsed time (end - start) to total time

        } catch (IOException e) {                   // If there are problems running the process
            System.out.println("Invalid command: " + command[0]);
        } catch (Exception e) {                     // Or any other problems:
            System.out.println("Other problem: " + e);
        }
    }

    /**
     * "Split the user command by spaces, but preserving them when inside double-quotes.
     * Code Adapted from:
     * https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-
     * single-or-double"
     */
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
