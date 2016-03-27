package com.example.alykoti.models;

import com.example.alykoti.services.AuthService;
import com.example.alykoti.services.DatabaseService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User {

    private String username;
    private AuthService.Role role;
    private Integer id;

    public User(String username, AuthService.Role role, Integer id) {
        this.username = username;
        this.role = role;
        this.id = id;
    }

    public User(String username, AuthService.Role role){
        this(username, role, null);
    }

    public String getUsername() {
        return username;
    }
    public Integer getId() { return id; }
    public AuthService.Role getRole() {
        return role;
    }

    /**
     * Gets all users from the db
     * @return
     */
    public static List<User> query() throws SQLException {
        return DatabaseService.getInstance().useConnection((conn) -> {
           ResultSet result = conn
                   .prepareStatement("SELECT id, username, role FROM users")
                   .executeQuery();
            ArrayList<User> users = new ArrayList<>();
            while(result.next()){
                users.add(User.fromResultSet(result));
            }
            return users;
        });
    }

    /**
     * Palauttaa tiedon siitä, onko annettu käyttäjänimi varattu
     * @param username
     * @return
     * @throws SQLException
     */
    public static boolean usernameExists(String username) throws SQLException {
        return DatabaseService.getInstance().useConnection((conn) -> {
            PreparedStatement statement = conn.prepareStatement(COUNT_USERS_STATEMENT);
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            return result.first() && result.getInt(1) != 0;
        });
    }

    /**
     * Luo User-olion annetusta ResultSetistä.
     * Huom! ResultSet-olion kursorin tulee olla valmiiksi oikeassa kohdassa
     * ja tuloksen on sisällettävä vähintään kolumnit "username", "role" ja "id".
     * @param result
     * @return
     * @throws SQLException
     */
    public static User fromResultSet(ResultSet result) throws SQLException {
        return new User(
                result.getString("username"),
                AuthService.Role.fromString(result.getString("role")),
                result.getInt("id")
        );
    }

    private static final String COUNT_USERS_STATEMENT =
            "SELECT COUNT(*) FROM users WHERE username = ?";

    @Override
    public String toString(){
        return "User { id " + getId() + ", username: " + getUsername() + ", role: " + getRole().toString() + " }";
    }
    @Override
    public boolean equals(Object o){
        Integer id = getId();
        return (o != null && o instanceof User && id != null) && id.equals(((User) o).getId());
    }

}
