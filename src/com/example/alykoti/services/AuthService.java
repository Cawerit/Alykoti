package com.example.alykoti.services;

import java.sql.PreparedStatement;
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

    private static final String SIGNUP_STATEMENT =
            "INSERT INTO users (username, password, role, salt) SELECT ?, SHA2(?, 224), ?, ?";

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
        private Role(String sqlValue){
            this.sqlValue = sqlValue;
        }
        @Override
        public String toString(){
            return this.sqlValue;
        }
    }

}
