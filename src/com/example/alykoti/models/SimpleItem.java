package com.example.alykoti.models;

import com.example.alykoti.components.SimpleComponent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;

/**
 * Items that can be represented with a simple on/off button
 */
public class SimpleItem implements Item {
	private String name;
	private int id;
	private FontAwesome iconOn;
	private FontAwesome iconOff;
	private boolean status;
	
	public SimpleItem(String name, FontAwesome iconOn, FontAwesome iconOff) {
		this.name = name;
		//this.id = edellinenid++;
		this.iconOn = iconOn;
		this.iconOff = iconOff;
		status = false;
	}

	public boolean isStatus() {
		return status;
	}
	public FontAwesome getIcon(){
		if(status == true) return iconOn;
		return iconOff;
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

	public SimpleComponent getRepresentation() {
		return new SimpleComponent(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
}
