import java.lang.Runtime;
import java.util.Properties;

/**
 * Reports certain properties and runtime information as specified through
 * commandline arguments
 *
 * @author Ky Kartchner
 * @version 1.0
 */
public class Assign2 {
    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        Properties properties = System.getProperties();

        System.out.println();

        for (String arg : args) {
            switch (arg) {
                case "-cpu":
                    // Report the number of CPUs (physical and logical) available:
                    System.out.println("Processors   : " + runtime.availableProcessors());
                    break;
                case "-mem":
                    // Report the available free memory, total memory, and max memory:
                    System.out.println("Free Memory  :\t" + String.format("%,12d", runtime.freeMemory()));
                    System.out.println("Total Memory :\t" + String.format("%,12d", runtime.totalMemory()));
                    System.out.println("Max Memory   :\t" + String.format("%,12d", runtime.maxMemory()));
                    break;
                case "-dirs":
                    // Report the process working directory and the user's home directory:
                    System.out.println("Working Directory   : " + properties.getProperty("user.dir"));
                    System.out.println("User Home Directory : " + properties.getProperty("user.home"));
                    break;
                case "-os":
                    // Report the OS name and OS version:
                    System.out.println("OS Name             : " + properties.getProperty("os.name"));
                    System.out.println("OS Version          : " + properties.getProperty("os.version"));
                    break;
                case "-java":
                    // Report the following items about the JVM:
                    //   Java vendor
                    System.out.println("Java Vendor         : " + properties.getProperty("java.vendor"));
                    //   Java runtime name
                    System.out.println("Java Runtime        : " + properties.getProperty("java.runtime.name"));
                    //   Java version:
                    System.out.println("Java Version        : " + properties.getProperty("java.version"));
                    //   Java VM version:
                    System.out.println("Java VM Version     : " + properties.getProperty("java.vm.version"));
                    //   Java VM name:
                    System.out.println("Java VM Name        : " + properties.getProperty("java.vm.name"));
                    break;
                default:
                    System.out.println("unknown command: " + arg);
                    break;
            }
        }

        System.out.println();
    }
}
