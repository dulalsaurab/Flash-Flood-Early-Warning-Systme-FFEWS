/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package majorproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author suraj
 */
public class DatabaseConnection {

    private static Connection con;

    public static Connection getCon() {
        return con;
    }
    public DatabaseConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");

        //get connection to the database; database_url,database_user,database_password required
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hydrology", "root", "");
    }
    
}
