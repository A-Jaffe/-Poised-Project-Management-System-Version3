import java.util.*;
import java.sql.*;
import java.text.*;

/**
 * The PoisedMain clas, it extends ErrorChecks and runs the main program method and
 * calls methods from the Project and Person classes based on the menu selection of the user.
 * It provides the user with a main menu of project management tasks.
 * <p></p>
 * @author Aaron Jaffe
 */
public class PoisedMain extends ErrorChecks {

    /**
     * This is the main method, it provides the user with a menu.
     * <p></p>
     * @param args runs the main method
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException {
        // Create Project object to call methods based on user menuChoice
        Project projManager = new Project();

        // Display welcome message and menu to user
        System.out.println("Welcome to the Poised Management System!");
        while(true) {
            System.out.println("""
                    \nWould you like to:
                    1) View existing projects
                    2) Add a new project
                    3) Update the details of a project
                    4) Finalize a project
                    5) View incomplete projects
                    6) View overdue projects
                    7). Find a project
                    8) Log Out""");

            int menuChoice = intCheck();

            // A try-catch block is used to connect to the MySQL server and access the PoisePMS database.
            try {
                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/PoisePMS?useSSL=false",
                        "seconduser",
                        "r00tus3r2");

                // Statement object created.
                Statement statement = connection.createStatement();

                if (menuChoice == 1) {
                    projManager.printAllFromTable(statement);
                }

                else if (menuChoice == 2) {
                    projManager.addProject(statement);

                    System.out.println("New project added!");
                }

                else if (menuChoice == 3) {
                    projManager.editProject(statement);
                }

                else if (menuChoice == 4) {
                    projManager.finaliseProject(statement);
                }

                else if (menuChoice == 5) {
                    projManager.viewIncomplete(statement);
                }

                else if (menuChoice == 6) {
                    projManager.viewOverdue(statement);
                }

                else if (menuChoice == 7) {
                    projManager.findProject(statement);
                }

                else if (menuChoice == 8) {
                    System.out.println("You have been logged out.");
                    break;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}