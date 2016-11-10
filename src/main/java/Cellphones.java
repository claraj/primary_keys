import java.io.*;
import java.sql.*;
import java.util.Scanner;

/**
 * Created by clara on 11/10/16.
 * Simple example for tracking cellphones owned by a company.
 * Primary keys generated in code with values 1, 2, 3 ....
 * and program will need to keep track of the number used to avoid conflicts.
 *
 * The next primary key to use is saved to a file when the program ends.
 *
 * This is a lot of work for the program - see Desktops and Laptops for alternatives
 *
 */
public class Cellphones {

    static int primaryKeyCounter;

    final static String CELLPHONE_TABLE_NAME = "cellphones";
    final static String ID_COL = "id";
    final static String MANUFACTURER_COL = "manufacturer";
    final static String MODEL_COL = "model";

    private static String PRIMARY_KEY_FILE_NAME = "pk.txt";

    static Scanner stringScanner = new Scanner(System.in);

    public static void main(String[] args) {

        DBUtils.registerDriver();

        createTable();

        configurePrimaryKey();

        insertTestData();

        insertNewCellphone();
        findCellphoneByModel();

        savePrimaryKey();

        stringScanner.close();

    }

    private static void savePrimaryKey() {

        //Overwrite previous file with new value
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRIMARY_KEY_FILE_NAME))) {
            writer.write(Integer.toString(primaryKeyCounter));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    private static void configurePrimaryKey() {

        //Need to keep track of the last value used by saving
        //it somewhere permanently as program executes, and reading it when program is started.
        //remember you can't re-use primary keys.

        try (BufferedReader reader = new BufferedReader(new FileReader(PRIMARY_KEY_FILE_NAME))) {
            String key = reader.readLine();
            primaryKeyCounter = Integer.parseInt(key);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            primaryKeyCounter = 0;  //todo is this the best thing to do, if the file can't be read?
        } catch (NumberFormatException nfe) {
            primaryKeyCounter = 0;  //todo is this the best thing to do, if the file's data can't be interpreted as an integer?
        }

    }


    private static void createTable() {

        try (Connection connection = DBUtils.getConnection();
                Statement createTableStatement = connection.createStatement()) {

            //The SQL to create the cellphone table is
            // CREATE TABLE cellphones (id INT NOT NULL, manufacturer VARCHAR(100), model VARCHAR(100) PRIMARY KEY(id) )
            String createTableSQLtemplate = "CREATE TABLE %s (%s INT NOT NULL, %s VARCHAR(100), %s VARCHAR(100) PRIMARY KEY(%s) )";
            String createTableSQL = String.format(createTableSQLtemplate, CELLPHONE_TABLE_NAME, ID_COL, MANUFACTURER_COL, MODEL_COL, ID_COL);
            System.out.println("The SQL to be executed is: " + createTableSQL);


            createTableStatement.execute(createTableSQL);

            System.out.println("Created cellphone table");

            connection.close();
            createTableStatement.close();

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

    }

    /* Insert some example data */
    private static void insertTestData() {

        try (Connection connection = DBUtils.getConnection()) {

            // Example of the SQL to execute
            //   INSERT INTO cellphones VALUES ( 1, 'Samsung', 'Galaxy Note 7' )
            //   INSERT INTO cellphones VALUES ( 1, 'Apple', 'iPhone 6' )

            String insertSQL = String.format("INSERT INTO %s VALUES (? , ? , ?) " , CELLPHONE_TABLE_NAME);
            PreparedStatement insertTestDataStatement = connection.prepareStatement(insertSQL);

            //Add one row of test data
            insertTestDataStatement.setInt(1, primaryKeyCounter);
            insertTestDataStatement.setString(2, "Samsung");
            insertTestDataStatement.setString(3, "Galaxy Note 7");
            insertTestDataStatement.execute();

            //increment primary key counter
            primaryKeyCounter++;

            //Add another row of test data
            insertTestDataStatement.setInt(1, primaryKeyCounter);
            insertTestDataStatement.setString(2, "Apple");
            insertTestDataStatement.setString(3, "iPhone 6");
            insertTestDataStatement.execute();

            //increment primary key counter.
            //Remembering to do this is a pain! See other examples for alternative approaches.
            primaryKeyCounter++;

            insertTestDataStatement.close();
            connection.close();

            System.out.println("Added two rows of test data");

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    private static void insertNewCellphone() {

        System.out.println("Enter manufacturer of new cellphone");
        String manuf = stringScanner.nextLine();
        System.out.println("Enter model of new cellphone");
        String model = stringScanner.nextLine();

        try (Connection connection = DBUtils.getConnection()) {

            String insertSQL = String.format("INSERT INTO %s VALUES (? , ? , ?) " , CELLPHONE_TABLE_NAME);
            PreparedStatement insertTestDataStatement = connection.prepareStatement(insertSQL);

            //Add one row of test data
            insertTestDataStatement.setInt(1, primaryKeyCounter);
            insertTestDataStatement.setString(2, manuf);
            insertTestDataStatement.setString(3, model);
            insertTestDataStatement.execute();

            //increment primary key counter
            primaryKeyCounter++;

            System.out.println("Added new cellphone.");

            insertTestDataStatement.close();
            connection.close();

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }


    //An example query. Could write other queries if desired.

    private static void findCellphoneByModel() {

        System.out.println("Enter model name to search for in cellphone table");
        String modelToFind = stringScanner.nextLine();

        try (Connection connection = DBUtils.getConnection()) {

            String searchSQL = String.format("SELECT * FROM %s WHERE %s = ? " , CELLPHONE_TABLE_NAME, MODEL_COL);
            PreparedStatement searchStatement = connection.prepareStatement(searchSQL);

            //Add one row of test data
            searchStatement.setString(1, modelToFind);

            ResultSet rs = searchStatement.executeQuery();

            System.out.println("Results of your query: ");
            while (rs.next()) {

                int id = rs.getInt(ID_COL);
                String manf = rs.getString(MANUFACTURER_COL);
                String model = rs.getString(MODEL_COL);

                System.out.println(String.format("id = %d Manufacturer = %s Model = %s", id, manf, model));

            }
            System.out.println("End of results");

            rs.close();
            searchStatement.close();
            connection.close();

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}

