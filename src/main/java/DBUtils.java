import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by clara on 11/10/16.  Utility methods and constants for the database.
 *
 */

public class DBUtils {

    static String DB_CONNECTION_URL = "jdbc:mysql://localhost:3306/";
    static String DB_NAME = "assets";           //todo create database assets
    static final String USER = "clara";      //todo change to your own username
    static final String PASSWORD = "clara";      //TODO change to your own password

    //todo grant permissions to your user by running a command like this in your MySQL shell
    //grant create, select, insert on assets.* to 'username'@'localhost'

    static void registerDriver() {

        try {
            String Driver = "com.mysql.cj.jdbc.Driver";
            Class.forName(Driver);
        } catch (ClassNotFoundException cnfe) {
            System.out.println("No database drivers found. Ensure you've imported MySQL dependencies with Maven, \n" +
                    "and if running from the command line, that you've copied the dependencies to your project. \nExiting Program");
            System.exit(-1);
        }
    }


    static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_CONNECTION_URL + DB_NAME, USER, PASSWORD);
        } catch (SQLException sqle) {
            System.out.println("Unable to connect to database.");
            System.out.println("Verify that your MySQL is running\n" +
                            "The " + DB_NAME + " database exists\n" +
                            "You have the correct username and password\n" +
                            "You have granted permissions to your user\n" +
                            "Exiting Program");

            sqle.printStackTrace();

            System.exit(-1);

        }

        return null;   //required, even though we exit program in case of error.
    }
}
