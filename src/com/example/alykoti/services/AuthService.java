package com.example.alykoti.services;

import com.example.alykoti.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthService {

    public void signup(String username, String password, Role role) throws SQLException {
        String salt = genSalt();
        PreparedStatement statement = databaseService
                .getConnection()
                .prepareStatement(SIGNUP_STATEMENT);
        statement.setString(1, username);
        statement.setString(2, password+salt);
        statement.setString(3, role.toString());
        statement.setString(4, salt);
        statement.execute();
    }

    public User login(String username, String password) throws SQLException {
        PreparedStatement statement = databaseService
                .getConnection()
                .prepareStatement(LOGIN_STATEMENT);
        statement.setString(1, username);
        statement.setString(2, password);

        ResultSet result = statement.executeQuery();
        if(result.first()){
            return new User(result.getString("username"), Role.fromString(result.getString("role")));
        } else {
            System.out.println("No user was found");
            return null;//No users with that name & password
        }

    }

    private static final String SIGNUP_STATEMENT =
            "INSERT INTO users (username, password, role, salt) SELECT ?, SHA2(?, 224), ?, ?";

    private static final String LOGIN_STATEMENT =
            "SELECT username, role " +
                    "FROM users " +
                    "WHERE username = ? AND password = SHA2(CONCAT(?, salt), 224);";

    //DI stuff
    private static AuthService instance;
    private DatabaseService databaseService;

    private String genSalt(){
        return UUID.randomUUID().toString();
    }

    private AuthService(DatabaseService databaseService){
        this.databaseService = databaseService;//Inject dependency
    }

    public static AuthService getInstance() {
        if(instance == null) instance = new AuthService(DatabaseService.getInstance());
        return instance;
    }

    /**
     * User roles
     */
    public enum Role {

        USER("USER"),
        ADMIN("ADMIN");

        private final String sqlValue;
        Role(String sqlValue){
            this.sqlValue = sqlValue;
        }
        @Override
        public String toString(){
            return this.sqlValue;
        }

        public static Role fromString(String str){
            for(Role r : Role.values()){
                if(r.toString().equals(str)) return r;
            }
            return null;
        }
    }

}
