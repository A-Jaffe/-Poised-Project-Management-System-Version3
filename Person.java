import java.sql.*;

/**
 * The Person class is used to retrieve and display person details from the
 * project_person_details table in the PoisePMS external database.
 * <p></p>
 * @author Aaron Jaffe
 */
public class Person {

    /**
     * This method retrieves a project's customer information to be displayed
     * in an easy-to-read format when an invoice is generated in the
     * finaliseProject method.
     * <p>
     * It selects a customer's details from the project_person_details table
     * in the external PoisePMS database by matching project number.
     * <p>
     * @param statement statement object connects to MySQL to perform SQL commands
     * @param projNumber an integer entered by the user to locate a specific project object
     * @throws SQLException occurs if there is an error accessing the database information
     */

    public void displayCustomer(Statement statement, int projNumber) throws SQLException {

        ResultSet customerDetails = statement.executeQuery("SELECT name, telephone, physAddress, email FROM project_person_details WHERE projNumber = " + projNumber
                + " AND position = 'Customer'");

        // Customer details displayed using iterator in table.
        while (customerDetails.next()) {
            System.out.println(
                    "\nCustomer Name: " + customerDetails.getString("name")
                            + "\nContact Number: " + customerDetails.getInt("telephone")
                            + "\nPhysical Address: " + customerDetails.getString("physAddress")
                            + "\nEmail Address: " + customerDetails.getString("email"));
        }
    }
}
