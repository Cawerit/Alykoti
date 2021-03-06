package com.example.alykoti.models;

import com.example.alykoti.services.AuthService;
import com.example.alykoti.services.DatabaseService;
import com.mysql.fabric.xmlrpc.base.Data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class User extends Resource<User> {

	@Column String username;
    @Column String role;//Resource.Column annotaatio vaatii tämän olevan string
	@Resource.Column(sqlType = Types.BOOLEAN) Boolean online;
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

	public boolean isOnline(){
		return online;
	}

	/**
	 * Asettaa käyttäjän online-statuksen ja tallentaa muutoksen tietokantaan
	 * @param value Onko käyttäjä online?
	 * @throws SQLException
	 */
	public void isOnline(boolean value) throws SQLException {
		online = value;
		if(getId() != null) {
			try (
					Connection conn = DatabaseService.getInstance().getConnection();
					PreparedStatement statement = conn.prepareStatement(SET_ONLINE_SQL);
			) {
				statement.setBoolean(1, online);
				statement.setInt(2, getId());
				statement.executeUpdate();
			}
		}
	}

	/**
	 * Asettaa käyttäjän online-statuksen, mutta EI tallenna sitä tietokantaan.
	 * Tätä metodia kutsutaan lähinnä kun käyttäjän online-tila on juuri HAETTU tietokannasta
	 * eikä sitä siksi ole järkevä tallentaa sinne uudestaan.
	 * @param value
	 */
	protected void setOnline(boolean value){
		online = value;
	}

	private static final String SET_ONLINE_SQL =
			"UPDATE users SET online = ? WHERE id = ?";

}
