package com.example.alykoti.models;

import java.util.ArrayList;

public class Room {
	private String name;
	private ArrayList<Sensor> sensors;
	private ArrayList<Item> items;
	private int initialTemp = 21;
	private int initialHum = 50;

	public Room(String name) {
		this.name = name;
		//Huoneilla voi olla luotaessa jo oletussensoreita?
		//sensors = new ArrayList<Sensor>();
		//sensors.add(new Sensor("Temperature", initialTemp, 0, 60, "\u00B0 C"));
		//sensors.add(new Sensor("Humidity", initialHum, 0, 100, "\u0025"));
	}

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




}
