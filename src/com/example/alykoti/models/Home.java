package com.example.alykoti.models;

import com.example.alykoti.services.DatabaseService;
import com.mysql.fabric.xmlrpc.base.Data;
import com.sun.corba.se.spi.orbutil.fsm.Guard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Home {
	private String name;
	private Integer id;
		
	public Home(String name, Integer id) {
		this.name = name;
		this.id = id;
	}

	public Home(String name){
		this(name, null);
	}

	public void save() throws SQLException {
		Integer generatedId = DatabaseService.getInstance().useConnection((conn) -> {
			PreparedStatement statement;
			if(getId() == null) {
				statement = conn
						.prepareStatement("INSERT INTO homes (name) VALUES (?);", Statement.RETURN_GENERATED_KEYS);
			} else {
				statement = conn
						.prepareStatement("UPDATE homes SET name = ? WHERE id = ?;");
				statement.setInt(2, getId());
			}
			statement.setString(1, getName());
			statement.execute();
			if(getId() == null) {
				ResultSet result = statement.getGeneratedKeys();
				if (result.first()) {
					return result.getInt(1);
				} else {
					return null;
				}
			} else return null;
		});
		if(generatedId != null){
			this.id = generatedId;
		}
	}

	public static List<Home> query() throws SQLException {
		return DatabaseService.getInstance().useConnection((conn) -> {
			ResultSet result = conn
					.prepareStatement(QUERY_HOMES_STATEMENT + ";")
					.executeQuery();
			ArrayList<Home> homes = new ArrayList<>();
			while(result.next()){
				homes.add(fromResultSet(result));
			}
			return homes;
		});
	}

	public static Home get(Integer id) throws SQLException {
		assert id != null : "Can't execute a get without id! Use query instead.";

		return DatabaseService.getInstance().useConnection(conn -> {
			PreparedStatement statement = conn
					.prepareStatement(QUERY_HOMES_STATEMENT + " WHERE h.id = ?;");
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			if(result.first()){
				return fromResultSet(result);
			} else return null;
		});
	}

	public static Home fromResultSet(ResultSet result) throws SQLException {
		return new Home(result.getString("name"), result.getInt("id"));
	}

	private static final String QUERY_HOMES_STATEMENT =
			"SELECT h.id as id, h.name as name, r.name, r.id FROM " +
				"homes h " +
					"LEFT JOIN rooms r ON h.id = r.home";
		
	public String getName(){ return name; }
	public Integer getId() { return id; }
}
