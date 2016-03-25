package com.example.alykoti.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Import the credentials (NOTE: the jar file needs to be in the build path, not included in version control)
import static alykotidb.Connector.*;

/**
 * Manages the connection and basic actions to the database
 */
public class DatabaseService {

    private static DatabaseService instance;

    private DatabaseService(){}

    public static DatabaseService getInstance(){
        if(instance == null) instance = new DatabaseService();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");//NOTE: Mysql driver needs to be in build path
            conn = DriverManager.getConnection(getUrl() + "/alykoti", getUsername(), getPassword());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

}
