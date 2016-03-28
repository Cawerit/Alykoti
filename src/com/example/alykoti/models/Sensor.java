package com.example.alykoti.models;

import com.example.alykoti.components.SensorWidget;
import com.vaadin.ui.Layout;

/**
 * Sensors whose value can be represented numerically
 */
public class Sensor implements Item{
	private String name;
	private int id, value, minValue, maxValue;
	private String symbol;
	
	public Sensor(String name, int initialValue, int min, int max, String symbol) {
		this.name = name;
		this.value = initialValue;
		this.minValue = min;
		this.maxValue = max;
		//this.id = edellinenid++
		this.symbol = symbol;	
	}

	public int getMinValue() {
		return minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public Layout getRepresentation() {
		return new SensorWidget(this);
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
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
		
	}
}
