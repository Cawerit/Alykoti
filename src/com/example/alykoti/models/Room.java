package com.example.alykoti.models;

import java.util.ArrayList;
import java.util.List;

public class Room extends Resource<Room> {

	@Column String name;
	@Column Integer home;//Id sille kodille johon tämä viittaa

	private Integer id;

	private ArrayList<Sensor> sensors;
	private ArrayList<Item> items;

	public Room(String name, Integer id){
		super(Room.class, "rooms");
		setName(name);
		setId(id);
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
}
