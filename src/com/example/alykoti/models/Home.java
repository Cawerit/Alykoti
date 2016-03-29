package com.example.alykoti.models;

import com.example.alykoti.services.DatabaseService;
import com.mysql.fabric.xmlrpc.base.Data;
import com.sun.corba.se.spi.orbutil.fsm.Guard;

import java.sql.*;
import java.util.ArrayList;
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

}
