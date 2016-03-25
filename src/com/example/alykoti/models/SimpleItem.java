package com.example.alykoti.models;

import com.vaadin.server.FontAwesome;


public class SimpleItem {
	private String name;
	private int id;
	private FontAwesome iconOn;
	private FontAwesome iconOff;
	private boolean status;
	
	public SimpleItem(String name, int id, FontAwesome iconOn, FontAwesome iconOff) {
		this.name = name;
		this.id = id;
		this.iconOn = iconOn;
		this.iconOff = iconOff;
		status = false;
	}

	public String getName() {
		return name;
	}
	public int getId() {
		return id;
	}
	public boolean isStatus() {
		return status;
	}
	public FontAwesome getIcon(){
		if(status == true) return iconOn;
		return iconOff;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setIconOn(FontAwesome iconOn) {
		this.iconOn = iconOn;
	}
	public void setIconOff(FontAwesome iconOff) {
		this.iconOff = iconOff;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}

	public void changeValue(){
		status = status ? false : true;
	}
}
