import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Consumer;

/**
 * Computes the Fibonacci of a specified number, the factorial of a specified
 * number, or estimates the value of 'e' using the specified number of 
 * iterations of a Taylor series
 *
 * If the number of commandline arguments is invalid the program prints a
 * help menu
 *
 * @author Ky Kartchner
 * @version 1.0
 */
public class Assign1
{
    public static void main(String[] args) {
        // Print help menu if number of arguments is 0 or not a multiple of 2:
        if (args.length == 0 || (args.length % 2 != 0)){ 
            printHelpMenu();
            return;
        }

        // Loop through and execute the command line arguments if valid:
        for (int i = 0; i < args.length; ++i){
            switch (args[i]){
                case "-fib":
                    // number for fib should be next argument (arg[i+1])
                    executeIfValid(Assign1::printFibonacci, args[++i], 0, 40,
                            "Fibonacci valid range is "); // skip i+1 by incrementing a second time
                    break;
                case "-fac":
                    executeIfValid(Assign1::printFactorial, args[++i], 0, Integer.MAX_VALUE,
                            "Factorial valid range is ");
                    break;
                case "-e":
                    executeIfValid(Assign1::printEuler, args[++i], 1, Integer.MAX_VALUE,
                            "Valid e iterations range is ");
                    break;
                default:
                    System.out.println("Unknown command line argument: " + args[i++]);
                    break;
            }
        }
    }

    /** 
     * Executes the provided function if numArg is valid and in range
     *
     * @param action The action to perform if valid
     * @param numArg The number to be parsed and passed into the action
     * @param min Lowerbound for num
     * @param max Upperbound for num
     * @
     */
    private static void executeIfValid(Consumer<Integer> action, String numArg,
            int min, int max, String rangeErrMsg){ 
        try {
            long num = Long.parseLong(numArg); // Throws NumberFormatException if numArg invalid 

            boolean inRange = (min <= num && num <= max);
            if (inRange){
                action.accept((int)num); // Execute provided function with num passed in as argument
            } else {
                System.out.println(rangeErrMsg + "[" + min + ", " + max + "]");
            }

        } catch (NumberFormatException e){
            System.out.println("Commandline error: " + e);
        }
    }

    /**
     * Prints a help menu with the list of program options
     */
    private static void printHelpMenu(){
        System.out.print("--- Assign 1 Help ---" 
                + "\n-fib [n] : Compute the Fibonacci of [n]; valid range [0, 40]"
                + "\n-fac [n] : Compute the factorial of [n]; valid range [0, "
                + Integer.MAX_VALUE + "]"
                + "\n-e [n] : Compute the value of 'e' using [n] iterations; valid range [1, "
                + Integer.MAX_VALUE + "]\n"); 
    }

    /**
     * Calculates and prints the nth number in the Fibonacci sequence
     *
     * @param n The value to use for n
     */
    private static void printFibonacci (int n) {
        int numA = 1;
        int numB = 1;

        int fib = 1;
        for (int i = 1; i < n; ++i){ // Only enters loop if n is greater than 1
            fib = numA + numB;
            numA = numB;
            numB = fib;
        }

        System.out.println("Fibonacci of " + n + " is " + fib);
    }

    /**
     * Calculates and prints n factorial
     *
     * @param n The value to use for n!
     */
    private static void printFactorial(int n) {
        BigInteger factorial = BigInteger.ONE; // Factorial is 1 for 1! and 0!
        // Loop results in factorial equalling n * n-1 * n-2 ... etc.
        for (int i = n; i > 1; --i){ // Only enters loop for n > 1 
            factorial = factorial.multiply(BigInteger.valueOf(i)); 
        }

        System.out.println("Factorial of " + n + " is " + factorial);
    }

    /**
     * Approximates and prints Euler's number using the Taylor series (1/1 + 1/2! +...+ 1/n!)
     * 
     * @param n The number of iterations to use in the Taylor Series
     */
    private static void printEuler(int n){
        BigDecimal approximation = BigDecimal.ZERO; // Approximation starts at 0 
        BigDecimal denominator = BigDecimal.ONE; // First term in Taylor series should be 1/1

        for (int i = 1; i <= n; ++i){ // Loop n times
            // Add 1/denominator to approximation:
            approximation = approximation.add(BigDecimal.ONE.divide(denominator,
                        1000, RoundingMode.HALF_UP));  
            // denominator  *= i to result in 1/1 then 1/1*2 then 1/1*2*3 up to 1/n! 
            denominator = denominator.multiply(BigDecimal.valueOf(i)); 
        }

        System.out.printf("Value of e using %d iterations is %.16f\n",  n,
                approximation.doubleValue());
    }
}
