package nl.tudelft.ec2interface.logging;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/*
* DbConnector enable connection to the database.
* Mostly adapted from http://webhelp.ucs.ed.ac.uk/services/mysql/example2-java.php
*/
public class DbConnector {
    
    String dbURL = "";
    String dbUsername = "";
    String dbPassword = "";
    
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    /*
     * Retrieve the login data, dbURL, dbUserName and dbPassword.
     * This data should be read by ConfigurationReader from Configuration.txt.
     */
    public DbConnector(String dbURL, String dbUsername, String dbPassword)
    {
            this.dbURL = dbURL;
            this.dbUsername = dbUsername;
            this.dbPassword = dbPassword;
    }

    public PreparedStatement insertData(String sqlStatement){
            
            try {
                    return conn.prepareStatement(sqlStatement);
            } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
            return null;
            
    }

    /*
     * Read from database using SQLStatement
     */
    public ResultSet Read(String SQLStatement) {
            
            conn = null;
            stmt = null;
            rs = null;

            try {

                    Class.forName("com.mysql.jdbc.Driver");
                    conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
                    stmt = conn.createStatement();

                    if (stmt.execute(SQLStatement)) {
                            rs = stmt.getResultSet();
                    } else {
                            System.err.println("select failed");
                    }
                    return rs;

            } catch (ClassNotFoundException ex) {
                    System.out.println("DbConnector.Read() failed");
                    System.err.println("Failed to load mysql driver");
                    System.err.println(ex);
                    return rs;
            } catch (SQLException ex) {
                    System.out.println("DbConnector.Read() failed");
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
                    return rs;
            }
    }

    public void executeUpdate(String SQLStatement) {
        conn = null;
        stmt = null;
        rs = null;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
            stmt = conn.createStatement();

            stmt.executeUpdate(SQLStatement);

        } catch (ClassNotFoundException ex) {
            System.out.println("DbConnector.Read() failed");
            System.err.println("Failed to load mysql driver");
            System.err.println(ex);
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 1061) { // ignore double indices
                System.out.println("DbConnector.executeUpdate() failed");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                System.out.println("SQLStatement: " + SQLStatement);
            }
        }
    }

    /*
     * Close database connection
     */
    public void close()
    {
            if (rs != null) {
                    try {
                            rs.close();
                    } catch (SQLException ex) { /* ignore */
                    }
                    rs = null;
            }
            if (stmt != null) {
                    try {
                            stmt.close();
                    } catch (SQLException ex) { /* ignore */
                    }
                    stmt = null;
            }
            if (conn != null) {
                    try {
                            conn.close();
                    } catch (SQLException ex) { /* ignore */
                    }
                    conn = null;
            }
    }
    
    public void executeSQLFile(String filepath)
    {
            String content ="";
            
            executeUpdate("SET GLOBAL max_allowed_packet = 30000000");
            try {
                    content = new Scanner(new File(filepath)).useDelimiter("\\Z").next();
            } catch (IOException e) {
                    e.printStackTrace();
            }
            
            //Can't process more than one query at one time. Need to split them.
            String queries[] = content.split(";");
            for(int i = 0; i<queries.length; i++)
            {
                    executeUpdate(queries[i]);
            }
    }
    
    /*
     * Print the ResultSet that was retrieved from the database.
     * Purely for testing.
     */
    public void PrintResultSet(ResultSet rs) {
            
            try {
                    while (rs.next()) {
                            //String entry = rs.toString();
                            int columnCount = rs.getMetaData().getColumnCount();
                            for(int i = 1; i<columnCount+1; i++)
                            {
                                    String entry = rs.getString(i) +" ";
                                    System.out.print(entry);
                            }
                            System.out.println();
                    }
            } catch (SQLException ex) {
                    System.out.println("DbConnector.PrintResultSet() failed");
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
            }
    }

}