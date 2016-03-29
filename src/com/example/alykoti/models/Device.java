package com.example.alykoti.models;

import java.sql.Types;
import java.util.Date;

public class Device extends Resource<Device> {

	private Integer id;

	@Column String name;
	@Column String type;
	@Column(sqlType = Types.INTEGER) Integer room;
	@Column(sqlType = Types.INTEGER) Integer userId;
	@Column String userName;
	@Column String statusType;
	@Column String statusValueStr;
	@Column(sqlType = Types.INTEGER) Integer statusValueNumber;
	@Column(sqlType = Types.DATE) java.sql.Date updated;

	public Device(Integer id){
		super(Device.class, "device_full_info");
		setId(id);
	}
	public Device(){
		this(null);
	}

	public void setRoom(Integer room){
		this.room = room;
	}

	public Integer getRoom(){
		return this.room;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}
}
