package com.example.alykoti.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Import the credentials (NOTE: the jar file needs to be in the build path)
import static alykotidb.Connector.*;

/**
 * Manages the connection and basic actions to the database
 */
public class Database {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");//NOTE: Mysql driver needs to be in build path
            conn = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
            System.out.println("Connection established");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

}
