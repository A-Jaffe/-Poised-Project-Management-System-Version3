import java.util.Scanner;

/**
 * The ErrorChecks class runs input checks for all user input.
 * It contains three check methods for various data types.
 * <p></p>
 * @author Aaron Jaffe
 */
public class ErrorChecks {  // Main class declaration.
    static final String ERROR_MESSAGE = "Incorrect entry, please try again: ";

    /**
     * The intCheck method takes in input from the user and
     * ensures that an integer is returned.
     * @return returns integer value
     */
    public static int intCheck() {
        while (true) {
            Scanner input = new Scanner(System.in);
            String integer = input.nextLine();

            try {
                return Integer.parseInt(integer);
            }
            catch (NumberFormatException ex) {
                System.out.println(ERROR_MESSAGE);
            }
        }
    }

    /**
     * The stringCheck method takes in input from the user and
     * ensures that a string is returned.
     * @return returns string value
     */
    public static String stringCheck() {
        while (true) {  // While loop repeatedly re-prompts for input until correct.
            Scanner input = new Scanner(System.in);
            String string = input.nextLine().toLowerCase();

            if ((string.equals("")) || (string.length() > 25)) {
                System.out.println(ERROR_MESSAGE);
            }
            else {
                return string;
            }
        }
    }

    /**
     * The doubleCheck method takes in input from the user and
     * ensures that a double is returned.
     * @return returns double value
     */
    public static double doubleCheck() {
        while (true) {
            Scanner input = new Scanner(System.in);
            String doubleInput = input.nextLine();

            try {
                return Double.parseDouble(doubleInput);
            }
            catch (NumberFormatException ex) {
                System.out.println(ERROR_MESSAGE);  // Error message displayed if parsing is not possible.
            }
        }
    }
}



