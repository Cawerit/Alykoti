package com.example.alykoti.models;

import com.example.alykoti.services.AuthService;
import com.example.alykoti.services.DatabaseService;
import com.sun.istack.internal.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Room {
	private Integer id;
	private String name;
	private Integer homeId;

	public Room(String name, Integer homeId, Integer id){
		this.id = id;
		this.name = name;
		this.homeId = homeId;
	}

	public Room(String name, Integer homeId){
		this(name, homeId, null);
	}

	/**
	 * Hakee listan huoneista
	 * @param homeId - Jos annettu, haku rajataan tämän kodin huoneisiin
	 * @return
	 */
	public static List<Room> query(@Nullable Integer homeId) throws SQLException {
		return DatabaseService.getInstance().useConnection(conn -> {
			String sql = "SELECT id, name, home FROM rooms";
			if(homeId != null){
				sql += " WHERE home = ?";
			}
			sql += ";";
			PreparedStatement prepared = conn.prepareStatement(sql);
			if(homeId != null){
				prepared.setInt(1, homeId);
			}
			ResultSet result = prepared.executeQuery();
			List<Room> rooms = new ArrayList<>();
			while(result.next()){
				rooms.add(Room.fromResultSet(result));
			}
			return rooms;
		});
	}
	public static List<Room> query() throws SQLException { return query(null); }

	public Integer getId() { return id; }

	public String getName() { return name; }

	public Home getHome() throws SQLException {
		assert homeId != null : "Null parent can't be fetched!";
		return Home.get(this.homeId);
	}

	public static Room fromResultSet(ResultSet result) throws SQLException {
		return new Room(result.getString("name"), result.getInt("home"), result.getInt("id"));
	}
}
