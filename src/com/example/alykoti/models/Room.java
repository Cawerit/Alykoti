package com.example.alykoti.models;

import com.example.alykoti.services.DatabaseService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Room extends Resource<Room> {

	@Column String name;
	@Column(sqlType=java.sql.Types.INTEGER) Integer home;//Id sille kodille johon tämä viittaa

	private Integer id;

	private ArrayList<Sensor> sensors;
	private ArrayList<Item> items;

	public Room(String name, Integer id){
		super(Room.class, "rooms");
		setName(name);
		setId(id);
	}

	public List<Device> getDevices() throws SQLException {
		return this.getDevices(new Device());
	}

	public List<Device> getDevices(Device like) throws SQLException {
		Integer prev = like.getRoom();
		like.setRoom(getId());
		List<Device> res = like.query();
		like.setRoom(prev);
		return res;
	}

	public Room(String name) { this(name, null); }
	public Room(Integer id){ this(null, id); }
	public Room(){ this(null, null); }

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Sensor> getSensors() {
		return sensors;
	}

	public void setSensors(ArrayList<Sensor> sensors) {
		this.sensors = sensors;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public Integer getHomeId(){ return home; }
	public void setHomeId(Integer homeId){ this.home = homeId; }


	@Override
	public Integer getId() {
		return id;
	}
	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Hakee saman kodin edellisen ja seuraavan huoneen id:n mukaan järjestettynä
	 * @return
	 * @throws SQLException
	 */
	public AdjacentIds getAdjacent() throws SQLException {
		try(
				Connection conn = DatabaseService.getInstance().getConnection();
				PreparedStatement statement = conn.prepareStatement(adjacentSql)
		) {
			statement.setInt(1, home);
			statement.setInt(2, getId());
			statement.setInt(3, home);
			statement.setInt(4, getId());

			ResultSet res = statement.executeQuery();
			AdjacentIds result = new AdjacentIds();
			while(res.next()){
				int val = res.getInt("id");
				if(val < getId()) result.prev = val;
				else result.next = val;
			}
			System.out.println("Looking for adjacent ids \n" + statement.toString() + "\n" + result);
			return result;
		}
	}

	public static final class AdjacentIds {
		private int prev;
		private int next;
		public Integer getPrev() { return prev == 0 ? null : prev; }
		public Integer getNext() { return next == 0 ? null : next; }
		@Override
		public String toString(){
			return "AdjacentIds { prev: " + prev + ", next: " + next + " }";
		}
	}

	private final static String adjacentSql =
			//Haetaan edellisen rivin id
			"(SELECT id FROM rooms WHERE home = ? AND id > ? ORDER BY id ASC LIMIT 1)" +
			" UNION " + //Liitetään siihen seuraavan rivin id
			"(SELECT id FROM rooms WHERE home = ? AND id < ? ORDER BY id DESC LIMIT 1)";

}
