package com.example.alykoti.services;

import java.sql.*;
import java.util.stream.Stream;

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

    private static Integer getIntegerValue(ResultSet r, int result) throws SQLException {
		return r.wasNull() ? null : result;
	}
	/**
	 * ResultSetin getInt ei hoida null-arvoja hyvin. Tällä metodilla
	 * ResultSetistä voi hakea arvon Integerinä (tai nullin jos arvo on null)
	 * @param r ResultSet jonka kursori on valmiiksi oikeassa kohdassa
	 * @param columnName
	 */
	public static Integer getInteger(ResultSet r, String columnName) throws SQLException {
		return getIntegerValue(r, r.getInt(columnName));
	}

	public static Integer getInteger(ResultSet r, int columnIndex) throws SQLException {
		return getIntegerValue(r, r.getInt(columnIndex));
	}

	public static void setInteger(PreparedStatement s, int index, Integer value) throws SQLException {
		if(value == null){
			s.setNull(index, Types.INTEGER);
		} else {
			s.setInt(index, value);
		}
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
