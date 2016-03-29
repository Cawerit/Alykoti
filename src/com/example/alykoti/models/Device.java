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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public String getStatusValueStr() {
		return statusValueStr;
	}

	public void setStatusValueStr(String statusValueStr) {
		this.statusValueStr = statusValueStr;
	}

	public Integer getStatusValueNumber() {
		return statusValueNumber;
	}

	public void setStatusValueNumber(Integer statusValueNumber) {
		this.statusValueNumber = statusValueNumber;
	}

	public java.sql.Date getUpdated() {
		return updated;
	}

	public void setUpdated(java.sql.Date updated) {
		this.updated = updated;
	}
}
