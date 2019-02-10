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

public class Assign3 {
    private static ArrayList<String> commandHistory = new ArrayList<>();
    private static long totalChildProcessTime = 0;

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
        if (currentDir.listFiles() != null) {
            for (File file : currentDir.listFiles()) {
                printFileInfo(file);
            }
        }
    }

    /**
     * Print the information of the specified file into a format similar to the "ls -l" bash command.
     * (i.e. "[permissions for current user] [size in bytes] [date last modified] [name of file]").
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

    private static void runAsExternal(String[] command) {
        boolean noWait = command[command.length - 1].equals("&"); // No waiting if last argument is ampersand
        if (noWait) {       // Prevent wait ampersand (&) from being passed in as an argument if needed
            command[command.length - 1] = "";
        }

        int indexOfPipe = command.length;
        for (int i = 0; i < command.length; ++i){
            if (command[i].equals("|")){
                indexOfPipe = i;
                break;
            }
        }

        boolean piping = (indexOfPipe != command.length);
        if (piping){
            pipeExternal(command, indexOfPipe, noWait);
        } else {
            runExternal(command, noWait);
        }
    }

    private static void pipeExternal(String[] command, int indexOfPipe, boolean noWait){
        String[] command1 = new String[indexOfPipe];
        System.arraycopy(command, 0, command1, 0, command1.length);

        String[] command2 = new String[Math.abs(command.length - indexOfPipe - 1)]; 
        System.arraycopy(command, indexOfPipe+1, command2, 0, command2.length);

        /* Create process builders */
        ProcessBuilder processBuilder1 = new ProcessBuilder(command1);
        ProcessBuilder processBuilder2 = new ProcessBuilder(command2);

        /* Redirect input and output */
        processBuilder1.redirectInput(ProcessBuilder.Redirect.INHERIT);
        processBuilder2.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        File currentDir = new File(System.getProperty("user.dir"));
        processBuilder1.directory(currentDir);
        processBuilder2.directory(currentDir);

        try { // Try to run the processes
            long start = System.currentTimeMillis(); // Save current time for start time
            Process process1 = processBuilder1.start();
            Process process2 = processBuilder2.start();

            pipeProcesses(process1, process2);

            // Wait if noWait is false:
            if (!noWait) { 
                process1.waitFor();   
                process2.waitFor();
            }

            long end = System.currentTimeMillis();   // Save current time for end time
            totalChildProcessTime += (end - start);  // Add the elapsed time (end - start) to total time

        } catch (Exception e) {
            System.out.println("Problem with piping: " + e);

        }
    }

    private static void pipeProcesses (Process p1, Process p2) throws Exception{
        // Write output of p1 to input of p2:
        int data = 0;
        while ((data = p1.getInputStream().read()) != -1){
            p2.getOutputStream().write(data);
        }
        p2.getOutputStream().flush();
        p2.getOutputStream().close();
    }
    /**
    */
    private static void runExternal(String[] command, boolean noWait){
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.directory(new File(System.getProperty("user.dir")));

        try { // Try to run the process:
            long start = System.currentTimeMillis(); // Save current time for start time
            Process process = processBuilder.start();                  // Start the process

            if (!noWait) {                           // If it should wait
                process.waitFor();                         //     Then wait
            }

            long end = System.currentTimeMillis();   // Save current time for end time
            totalChildProcessTime += (end - start);  // Add the elapsed time (end - start) to total time

        } catch (IOException e) {   // If there are problems running the process
            System.out.println("Invalid command: " + command[0]);
        } catch (Exception e) {
            System.out.println("Other problem: " + e);
        }
    }

    /**
     * Split the user command by spaces, but preserving them when inside double-quotes.
     * Code Adapted from:
     * https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-
     * single-or-double
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
