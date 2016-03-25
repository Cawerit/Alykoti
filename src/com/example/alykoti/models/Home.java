package com.example.alykoti.models;

public class Home {
	private String name;
	private int id;
		
	public Home(String name) {
		this.name = name;
		//this.id = viimeksilisatty.getId()++;
	}
		
	public String getName(){ return name; }
	public int getId() { return id; }
}
