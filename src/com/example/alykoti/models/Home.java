package com.example.alykoti.models;

import com.example.alykoti.services.DatabaseService;
import java.sql.*;
import java.util.List;

public class Home extends Resource<Home> {

	@Column String name;
	private Integer id;
		
	public Home(String name, Integer id) {
		super(Home.class, "homes");
		this.name = name;
		this.id = id;
	}

	public Home(String name){
		this(name, null);
	}
	public Home(Integer id) { this(null, id); }
	public Home(){
		this(null, null);
	}

		
	public String getName(){ return name; }
	@Override
	public Integer getId() { return id; }

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public List<Room> getRooms() throws SQLException {
		Room roomQuery = new Room();
		roomQuery.setHomeId(getId());
		return roomQuery.query();
	}

	public Integer getFirstRoomId() throws SQLException {
		final String sql = "SELECT id FROM rooms WHERE home = ? ORDER BY id ASC LIMIT 1;";
		try(
				Connection conn = DatabaseService.getInstance().getConnection();
				PreparedStatement statement = conn.prepareStatement(sql);
		){
			statement.setInt(1, getId());
			ResultSet res = statement.executeQuery();
			if(res.first()){
				return res.getInt("id");
			}
		}
		return null;
	}

}
