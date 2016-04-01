package com.example.alykoti.models;

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
		return new ArrayList<>();
		//return res;
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
	
	public int getAdjacent(boolean next){
		try {
			Home home = new Home(this.home);
			home.pull();
			List<Room> rooms = home.getRooms();
			int i = 0;
			for(i = 0; i < rooms.size(); i++){
				if(this.id == rooms.get(i).getId()){
					break;
				}
			}
			int roomid = rooms.get(0).getId();
			if(next) {
				if(rooms.size() > i + 1) roomid = rooms.get(i + 1).getId();
			} else if (i == 0) {
				roomid = rooms.get(rooms.size() - 1).getId();
			}
			return roomid;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
