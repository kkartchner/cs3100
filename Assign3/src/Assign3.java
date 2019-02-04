import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Runtime;
import java.util.Properties;
import java.util.Scanner;

public class Assign3 {
    public static void main(String[] args) {
        // Test if args[0] is a built in shell command
        //   If so, perform that operation
        //   Else, attempt to execute it as external program
        
        Scanner sc = new Scanner(System.in);
        Properties properties = Runtime.getProperties();

        while (true){
            prompt();

            String command = sc.next();
            System.out.println(command);
            //String cmdArgs = splitCommand(sc.nextLine());    
       }
        /* 
           if (args.length > 0){
           switch (args[0]){
           case "ptime":
           break;
           case "history":
           break;
           case "list":
           break;
           case "cd":
           break;
           case "exit":
           System.exit(0);
           break;
           default:
        // Try to run as external command
        break;
           } 
           }


        // String[] commands = new String[args.length-1];
        // for (String s : args){
        //     System.out.println(s);
        // }
        //
        */
    }
    /**
     * Display the shell prompt
     *
     * "[currentdir]:"
     */
    private static void prompt(){
        System.out.println("[%s]: ",); 
    }

    /**
     * Command: ptime 
     *
     * Display the number of seconds (4 digits past the decimal) spent executing (waiting for) child process.
     */
    private static void ptime(){
    }

    /**
     * Command: history
     *
     * Show the command history
     */
    private static void history(){

    }

    /**
     * Command: list
     *
     * List the contents of the current directory.
     */
    private static void list(){

    } 

    /**
     * Command: cd
     *
     * Change the directory to specified location or to home directory. 
     * */
    private static void cd(String path){
        if (path == ""){
            // change to home directory
        } else {
            // check if specified path exists
            //      If it does, change to it
            //      Else, print an error message
        }

        // Print current directory 
    }


    /*
     * Split the user command by spaces, but preserving them when inside double-quotes.
     * Code Adapted from: 
     * https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-single-or-double
     */
    public static String[] splitCommand(String command) {
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
