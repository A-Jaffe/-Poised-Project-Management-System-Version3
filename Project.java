import java.sql.*;
import java.text.*;
import java.util.Date;

/**
 * The Project class adds and manages project information in the database PoisePMS
 * and displays it to the user. The Project class extends the ErrorChecks class.
 * <p></p>
 * This class contains the methods addProject, editProject, finaliseProject,
 * viewIncomplete, viewOverdue, findProject and printAllFromTable.
 * <p></p>
 * @author Aaron Jaffe
 */
public class Project extends ErrorChecks {

    /**
     * This method allows the user to add new project information which is then added
     * to the 'main_project_info' table in the PoisePMS database.
     * <p>
     * It prompts user for input and then connects to the external database to add
     * project information.
     * <p>
     * @param statement statement object connects to MySQL to perform SQL commands
     * @throws SQLException occurs if there is an error accessing the database information
     */
    public void addProject(Statement statement) throws SQLException {
        // Request project details from user
        System.out.print("\nPlease add a new project number: ");
        int projNumber = intCheck();

        System.out.print("\nPlease add a new project name: ");
        String projName = stringCheck();

        System.out.print("\nPlease add a building type: ");
        String buildType = stringCheck();

        System.out.print("\nPlease add a address for the project: ");
        String address = stringCheck();

        System.out.print("\nPlease add an ERF number: ");
        String erf = stringCheck();

        System.out.print("\nPlease add a total fee for the project: ");
        double totalFee = doubleCheck();

        System.out.print("\nPlease add the current amount paid for the project: ");
        double amountPaid = doubleCheck();

        System.out.print("Please add a deadline for the project (e.g. 3-Dec-2020): ");
        String deadline = stringCheck();

        String finalised = "No";
        String completedDate = "null";

        // Write project new project information to PoisePMS database
        statement.executeUpdate(
                "INSERT INTO main_project_info VALUES (" + projNumber + ", " + "'" + projName +
                        "'" + ", " + "'" + buildType + "'" + ", " + "'" + address + "'" + ", " +
                        "'" + erf + "'" + ", " + totalFee + ", " + amountPaid + ", " + "'" +
                        deadline + "'" + ", " + "'" + finalised + "'" + ", " +
                        "'" + completedDate + "'" + ");");

        // Message and project details displayed to user
        System.out.println("\nYour new project was successfully added.\nProject Details:\n");
        printAllFromTable(statement);
    }

    /**
     * This method allows the user to update project information, namely
     * due date and total amount paid.
     * <p>
     * It displays a sub-menu to the user with either  options.
     * The edited information is then recorded in the main-project-info table
     * in the external PoisePMS database.
     * <p>
     * @param statement statement object connects to MySQL to perform SQL commands
     * @throws SQLException occurs if there is an error accessing the database information
     */
    public void editProject(Statement statement) throws SQLException {

        // Request project number choice from user
        System.out.println("Please enter the number of the project you wish to update: \n");
        int projChoice = intCheck();

        // Request type of edit from the user
        System.out.println("Would you like to:" +
                "\n1. Edit the project due date or" +
                "\n2. Edit the total amount paid to date?" +
                "\nChoose either 1 or 2");
        int editChoice = intCheck();

        // If choice is 1, request new deadline from user
        if (editChoice == 1) {
            System.out.println("Please enter a new project deadline: ");
            String newDeadline = stringCheck();

            // Write update to PoisePMS database
            statement.executeUpdate(
                    "UPDATE main_project_info SET deadline = '" + newDeadline + "'" + " WHERE projNumber = " + projChoice
            );

            // Message and updated project information displayed to user
            System.out.println("Your project info has been successfully updated.\nProject Details:\n: ");
            printAllFromTable(statement);
        }

        // If choice is 2, request new amount paid from user
        else if (editChoice == 2) {
            System.out.println("Please enter the amount paid so far: ");
            double amountPaid = doubleCheck();

            // Write update to PoisePMS database
            statement.executeUpdate(
                    "UPDATE main_project_info SET amountPaid = " + amountPaid + " WHERE projNumber = " + projChoice
            );

            // Message and updated project information displayed to user
            System.out.println("Your project info has been successfully updated.\nProject Details:\n");
            printAllFromTable(statement);
        }
    }

    /**
     * This method allows the user to finalise a project located in the main_project_info table
     * in the external 'PoisePMS' database.
     * <p>
     * The user is prompted to enter a project number and if there is
     * an outstanding amount on the project, an invoice is generated and
     * displayed with customer details.
     * If the total fee has been paid in full, the project is just marked
     * as finalised and a completion date is added.
     * <p>
     * @param statement statement object connects to MySQL to perform SQL commands
     * @throws SQLException occurs if there is an error accessing the database information
     */
    public void finaliseProject(Statement statement) throws SQLException {

        // Request project number to finalise from user
        System.out.print("Please enter the number of the project that you wish to finalise: ");
        int projChoice = intCheck();

        // Select the totalFee and amountPaid columns from the PoisePMS database.
        ResultSet projFees = statement.executeQuery(
                "SELECT totalFee, amountPaid FROM main_project_info WHERE projNumber = " + projChoice);
        double totalFee = 0;
        double amountPaid = 0;

        // Iterate through table and store each value in corresponding variables.
        while (projFees.next()) {
            totalFee = projFees.getDouble("totalFee");
            amountPaid = projFees.getDouble("amountPaid");
        }

        // If the project has been paid in full, no invoice is generated
        if (totalFee == amountPaid) {
            System.out.println("This project has already been paid in full. No invoice to be generated.");

            // Request the projects completed date from the user
            System.out.print("Please add a completion date for the project: ");
            String completedDate = stringCheck();

            // Write completed date to completedDate column in the PoisePMS database
            statement.executeUpdate(
                    "UPDATE main_project_info SET completedDate = " + "'" + completedDate +
                            "'" + " WHERE projNumber = " + projChoice);

            // Mark project as finalised by writing 'Yes' to the finalise column in the table.
            statement.executeUpdate(
                    "UPDATE main_project_info SET finalised = 'Yes' WHERE projNumber = " + projChoice);

            // Message and finalised project information displayed to the user
            System.out.println("Your project has been successfully finalised.\nProject Details:\n");
            printAllFromTable(statement);
        }

        // If there is an outstanding balance, retrieve customer on project information
        // Generate invoice with customer information and outstanding balance
        else if (totalFee != amountPaid) {
            System.out.println("There is still an outstanding amount to be paid for this project. View your invoice below: \n");

            // Create person object and call method to display customer
            Person customer = new Person();
            customer.displayCustomer(statement, projChoice);

            // Display outstanding balance calculation
            System.out.println("\nAmount Outstanding: R" + (totalFee - amountPaid));

            // Request completed date from the user
            System.out.print("\nPlease add a completion date for the project: ");
            String completedDate = stringCheck();

            // Write completed date to the completedDate column in PoisePMS database
            statement.executeUpdate(
                    "UPDATE main_project_info SET completedDate = " + "'" + completedDate
                            + "'" + " WHERE projNumber = " + projChoice);

            // Mark project as finalised by writing 'Yes' to the finalise column in the table.
            statement.executeUpdate(
                    "UPDATE main_project_info SET finalised = 'Yes' WHERE projNumber = " + projChoice);

            // Message and finalised project details displayed to the user
            System.out.println("Your project has been successfully finalised.\nProject Details:\n");
            printAllFromTable(statement);
        }
    }

    /**
     * This method allows users to view all project objects that are incomplete
     * in the main_project_info table in the external 'PoisePMS' database.
     * <p>
     * @param statement statement object connects to MySQL to perform SQL commands
     * @throws SQLException occurs if there is an error accessing the database information
     */
    public void viewIncomplete(Statement statement) throws SQLException {

        System.out.println("\nIncomplete Projects: \n");

        // Read projects from PoisePMS database where finalised column is 'No'
        ResultSet incompleteProj = statement.executeQuery(
                "SELECT * FROM main_project_info WHERE finalised = 'No' AND completedDate = 'null'");

        // Incomplete projects are displayed using a table iterator.
        while (incompleteProj.next()) {
            System.out.println(
                    "Project Number: \t" + incompleteProj.getInt("projNumber")
                            + "\nProject Name: \t" + incompleteProj.getString("projName")
                            + "\nBuilding Type: \t" + incompleteProj.getString("buildType")
                            + "\nPhysical Address: " + incompleteProj.getString("address")
                            + "\nERF Number: \t" + incompleteProj.getString("erf")
                            + "\nTotal Fee: \tR" + incompleteProj.getFloat("totalFee")
                            + "\nAmount Paid: \t" + incompleteProj.getFloat("amountPaid")
                            + "\nDeadline: \t" + incompleteProj.getString("deadline")
                            + "\nFinalised: \t" + incompleteProj.getString("finalised")
                            + "\nCompletion Date: " + incompleteProj.getString("completedDate")
                            + "\n");
        }
    }

    /**
     * This method allows the user to view all projects that are overdue
     * in the main_project_info table in the external 'PoisePMS' database.
     * <p>
     * This method runs through all deadlines of incomplete projects, compares the deadline date with the
     * current date and displays overdue projects in an easy-to-read format.
     * If no overdue projects are present, an error message is displayed to the user.
     * <p>
     * @param statement statement object connects to MySQL to perform SQL commands
     * @throws SQLException occurs if there is an error accessing the database information
     * @throws ParseException occurs if a date string is in the wrong format to be parsed
     */
    public void viewOverdue(Statement statement) throws SQLException, ParseException {

        boolean projCheck = false;
        String[] info;
        int[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        String[] monthName = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int monthNum = 0;

        // Read deadline column from PoisePMS database
        ResultSet projDateCheck = statement.executeQuery("SELECT deadline FROM main_project_info WHERE finalised = 'No' AND completedDate = 'null'");

        // Iterate through the deadline dates
        while (projDateCheck.next()) {
            /* The deadline date in the project is stored in the string variable 'date_info'.
             * This variable is then split into an array called 'info' by removing the dash
             * from the date. The first indexed value of 'info' is then parsed and stored
             * into an integer variable called 'dayDue'.
             */
            String dateInfo = projDateCheck.getString("deadline");
            info = dateInfo.split("-");
            int dayDue = Integer.parseInt(info[0]);

            /* The second indexed value from the info array is stored in the variable 'monthInfo'.
             * It is then split further to store only three letters of the month name
             * into string variable 'month' (e.g. 'Dec').
             */
            String monthInfo = info[1];
            String monthDue = (monthInfo.substring(0,2));

            /* A year variable is also created and
             * assigned the parsed value from the third index in 'info' array.
             */
            int yearDue = Integer.parseInt(info[2]);

            /* Loop to compare the substring 'monthDue' with the monthName string array.
             * Once matched with an abbreviated month of the year, the corresponding number
             * from the integer array 'months' is stored in the 'monthNum' variable.
             */
            for (int i = 0; i < monthName.length; i++) {
                if (monthDue.equalsIgnoreCase(monthName[i])) {
                    monthNum = months[i];
                }
            }

            // Get the current date and store it as a string.
            String currentDate = "" + java.time.LocalDate.now();

            // Creating a new simple date format object.
            SimpleDateFormat dateObj = new SimpleDateFormat("yyyy-MM-dd");

            // Dates dateNow and dateDue are then created by parsing string info from 'currentDate'
            // and date info gathered from the file above, respectively.
            Date dateNow = dateObj.parse(currentDate);
            Date dateDue = dateObj.parse(dayDue + "-" + monthNum + "-" + yearDue);

            // If the current date has passed the deadline for the project, it is overdue.
            if (dateNow.compareTo(dateDue) < 0) {
                // projCheck is set to 'true'
                projCheck = true;

                // All the columns for that project are selected and displayed.
                System.out.println("\nOverdue Projects: \n");
                ResultSet overdueProj = statement.executeQuery(
                        "SELECT * from main_project_info WHERE deadline = '" + dateInfo + "'");

                // Iterate and display all info related to the overdue project.
                while (overdueProj.next()) {
                    System.out.println(
                            "Project Number: \t" + overdueProj.getInt("projNumber")
                                    + "\nProject Name: \t" + overdueProj.getString("projName")
                                    + "\nBuilding Type: \t" + overdueProj.getString("buildType")
                                    + "\nPhysical Address: " + overdueProj.getString("address")
                                    + "\nERF Number: \t" + overdueProj.getString("erf")
                                    + "\nTotal Fee: \tR" + overdueProj.getFloat("totalFee")
                                    + "\nAmount Paid: \t" + overdueProj.getFloat("amountPaid")
                                    + "\nDeadline: \t" + overdueProj.getString("deadline")
                                    + "\nFinalised: \t" + overdueProj.getString("finalised")
                                    + "\nCompletion Date: " + overdueProj.getString("completedDate")
                                    + "\n");
                }
            }

            // Set projCheck to false if there are no overdue projects
            else {
                projCheck = false;
            }
        }

        // If there are no overdue projects, display message
        if (projCheck == false) {
            System.out.println("There are no overdue projects on the system.");
        }
    }

    /**
     * This method allows the user to search for a project byb project number or name.
     * <p>
     * Using either name or number, the project is then located in the PoisePMS
     * database and displayed in an easy-to-read format.
     * <p>
     * @param statement statement object connects to MySQL to perform SQL commands
     * @throws SQLException occurs if there is an error accessing the database information
     */
    public void findProject(Statement statement) throws SQLException {
        // Request if user would like to search by project name or number
        System.out.print("Would you like to search for your project by project number (1) or " +
                "project name (2)? \nPlease select either 1 or 2.");
        int search_Choice = intCheck();

        // If choice is 1, request name of project
        if (search_Choice == 1) {
            System.out.print("\nPlease enter the number of the project you wish to view: ");
            int proj_num = intCheck();

            // Retrieve relevant project from PoisePMS and display to the user
            System.out.println("\nProject Details: \n");
            ResultSet searchProj = statement.executeQuery("SELECT * from main_project_info WHERE projNumber = " + proj_num);

            // Iterate through project selected by user and display all information.
            while (searchProj.next()) {
                System.out.println(
                        "Project Number: \t" + searchProj.getInt("projNumber")
                                + "\nProject Name: \t" + searchProj.getString("projName")
                                + "\nBuilding Type: \t" + searchProj.getString("buildType")
                                + "\nPhysical Address: " + searchProj.getString("address")
                                + "\nERF Number: \t" + searchProj.getString("erf")
                                + "\nTotal Fee: \tR" + searchProj.getFloat("totalFee")
                                + "\nAmount Paid: \t" + searchProj.getFloat("amountPaid")
                                + "\nDeadline: \t" + searchProj.getString("deadline")
                                + "\nFinalised: \t" + searchProj.getString("finalised")
                                + "\nCompletion Date: " + searchProj.getString("completedDate")
                                + "\n");
            }
        }

        // If choice is 2, request number of project from user
        else if (search_Choice == 2) {
            System.out.print("\nPlease enter the name of the project you wish to locate: ");
            String projName = stringCheck();

            // Retrieve relevant project from PoisePMS and display to the user
            System.out.println("\nProject Details: \n");
            ResultSet searchProj = statement.executeQuery(
                    "SELECT * from main_project_info WHERE projName = '" + projName + "'");

            // Iterate through project selected by user and display all information.
            while (searchProj.next()) {
                System.out.println(
                        "Project Number: \t" + searchProj.getInt("projNumber")
                                + "\nProject Name: \t" + searchProj.getString("projName")
                                + "\nBuilding Type: \t" + searchProj.getString("buildType")
                                + "\nPhysical Address: " + searchProj.getString("address")
                                + "\nERF Number: \t" + searchProj.getString("erf")
                                + "\nTotal Fee: \tR" + searchProj.getFloat("totalFee")
                                + "\nAmount Paid: \t" + searchProj.getFloat("amountPaid")
                                + "\nDeadline: \t" + searchProj.getString("deadline")
                                + "\nFinalised: \t" + searchProj.getString("finalised")
                                + "\nCompletion Date: " + searchProj.getString("completedDate")
                                + "\n");
            }
        }
    }

    /**
     * This method displays all information from the main_project_info table
     * in the PoisePMS database in an easy-to-read format.
     * <p>
     * @param statement statement object connects to MySQL to perform SQL commands
     * @throws SQLException occurs if there is an error accessing the database information
     */
    public void printAllFromTable(Statement statement) throws SQLException{

        // Select all records from the main_project_info table in the PoisePMS database
        ResultSet displayAll = statement.executeQuery("SELECT * FROM main_project_info");

        // Iterate through info in each column to display to the user.
        while (displayAll.next()) {
            System.out.println(
                    "Project Number: \t" + displayAll.getInt("projNumber")
                            + "\nProject Name: \t" + displayAll.getString("projName")
                            + "\nBuilding Type: \t" + displayAll.getString("buildType")
                            + "\nPhysical Address: " + displayAll.getString("address")
                            + "\nERF Number: \t" + displayAll.getString("erf")
                            + "\nTotal Fee: \tR" + displayAll.getFloat("totalFee")
                            + "\nAmount Paid: \t" + displayAll.getFloat("amountPaid")
                            + "\nDeadline: \t" + displayAll.getString("deadline")
                            + "\nFinalised: \t" + displayAll.getString("finalised")
                            + "\nCompletion Date: " + displayAll.getString("completedDate")
                            + "\n");
        }
    }
}
