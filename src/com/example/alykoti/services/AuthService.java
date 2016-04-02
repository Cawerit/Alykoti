package com.example.alykoti.services;

import com.example.alykoti.AlykotiUI;
import com.example.alykoti.models.User;
import com.vaadin.ui.UI;

import java.sql.*;
import java.util.UUID;

public class AuthService {

    public User signup(String username, String password, Role role) throws SQLException {
        String salt = genSalt();
        Integer id = null;
        try (
			Connection conn = databaseService.getConnection();
            PreparedStatement statement = conn
                    .prepareStatement(SIGNUP_STATEMENT, Statement.RETURN_GENERATED_KEYS)
		){
            statement.setString(1, username);
            statement.setString(2, password + salt);
            statement.setString(3, role.toString());
            statement.setString(4, salt);
            statement.execute();

            ResultSet result = statement.getGeneratedKeys();
            if(result.first()){
                id = result.getInt(1);
            }
        }
        return new User(username, role, id);
    }

    public User login(String username, String password) throws SQLException {
        try(
				Connection conn = databaseService.getConnection();
            	PreparedStatement statement = conn.prepareStatement(LOGIN_STATEMENT)
		){
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();
            if (result.first()) {
                User user = User.fromResultSet(result);
                setCurrentUser(user);
                new Thread(() -> {
                    //Asetetaan käyttäjä online (tämä voidaan hoitaa toisessa säikeessä ja siten
                    //antaa käyttäjän jatkaa sisäänkirjautumista vaikka häntä ei vielä olekaan merkattu online.
                    //Online-tietoa käytetään lähinnä muiden käyttäjien informoimiseen asiasta (ei niin tärkeää)
                    try {
						user.isOnline(true);
                    } catch (Exception e){
                        System.out.println("Couldn't set user " + user.getId() + " online");
                        e.printStackTrace();
                    }
                }).start();
                return user;
            } else {
                System.out.println("No user was found");
                return null;//No users with that name & password
            }
        }
    }
    
    public void updateUser(int id, String username, String password) throws SQLException {
        String salt = genSalt();
        Connection conn = databaseService.getConnection();
        try {
            PreparedStatement statement = conn
                    .prepareStatement(UPDATE_STATEMENT);
            statement.setString(1, username);
            statement.setString(2, password + salt);
            statement.setString(3, salt);
            statement.setInt(4, id);
            statement.execute();
        } finally {
        	try {
        		conn.close();
        	} catch(SQLException ignored) {	}
        }
    }

    public void logout(){
		AuthService auth = AuthService.getInstance();
		User current = auth.getCurrentUser();
		if(current != null){
			try {
				//Päivitetään tieto ettei käyttäjä ole enää online
				current.isOnline(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		AlykotiUI.getCurrent();
	}

    private static final String SIGNUP_STATEMENT =
            "INSERT INTO users (username, password, role, salt) SELECT ?, SHA2(?, 224), ?, ?";

    private static final String LOGIN_STATEMENT =
            "SELECT username, role, id " +
                    "FROM users " +
                    "WHERE username = ? AND password = SHA2(CONCAT(?, salt), 224);";
    
    private static final String UPDATE_STATEMENT = 
    		"UPDATE users SET username = ?, password = SHA2(?, 224), salt = ?" +
    				"WHERE id = ?;";

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

    private static final String CURRENT_USER_SESSION_VAR = "CURRENT_USER";

    public User getCurrentUser(){
        UI ui = AlykotiUI.getCurrent();
        Object value = ui.getSession().getAttribute(CURRENT_USER_SESSION_VAR);
        return value == null ? null : (User) value;
    }

    private void setCurrentUser(User user){
        System.out.println("Set current user");
        UI ui = AlykotiUI.getCurrent();
        ui.getSession().setAttribute(CURRENT_USER_SESSION_VAR, user);
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
