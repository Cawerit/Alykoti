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

    /**
     * Gets a connection to the db and safely runs the given function with it.
     * Useful when making simple requests to the db.
     * @param f
     * @param <R>
     * @return
     */
    public <R> R useConnection(CheckedSqlFunction<R> f) throws SQLException {
        Connection conn = null;
        R result = null;
        try {
            conn = this.getConnection();
            result = f.apply(conn);
        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @FunctionalInterface
    public interface CheckedSqlFunction<R> {
        R apply(Connection c) throws SQLException;
    }

}
