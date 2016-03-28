package com.example.alykoti.models;

import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;

public interface Item {
	
	public String getName();
	public void setName(String name);
	public int getId();
	public void setId(int id);
	public Layout getRepresentation();

}