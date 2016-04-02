package com.example.alykoti.models;

import com.example.alykoti.services.AuthService;
import com.example.alykoti.services.DatabaseService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User extends Resource<User> {

	@Column String username;
    @Column String role;//Resource.Column annotaatio vaatii tämän olevan string
    private Integer id;
	private AuthService.Role _role;//Säilötään tähän varsinainen Role-enum kun se saadaan

    public User(String username, AuthService.Role role, Integer id) {
		super(User.class, "users");
        this.username = username;
        this.id = id;
		if(role != null){
			this._role = role;
			this.role = role.toString();
		}
    }

	public User(){ this(null, null, null); }

    public User(String username, AuthService.Role role){
        this(username, role, null);
    }

    public String getUsername() {
        return username;
    }
	public void setUsername(String username) { this.username = username; }
    public Integer getId() { return id; }

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public AuthService.Role getRole() {
        if(_role != null) return _role;
		else if(role != null) {
			_role = AuthService.Role.fromString(role);
			return _role;
		}
		else return null;
    }

    /**
     * Palauttaa tiedon siitä, onko annettu käyttäjänimi varattu
     * @param username
     * @return
     * @throws SQLException
     */
    public static boolean usernameExists(String username) throws SQLException {
        return DatabaseService.getInstance().useConnection((conn) -> {
            PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?");
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

    @Override
    public boolean equals(Object o){
        Integer id = getId();
        return (o != null && o instanceof User && id != null) && id.equals(((User) o).getId());
    }

	@Override
	public String toString(){
		return this.getUsername();
	}

}
