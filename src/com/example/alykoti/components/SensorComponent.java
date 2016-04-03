package com.example.alykoti.components;

import com.example.alykoti.models.Device;
import com.example.alykoti.models.devices.DeviceStatus;
import com.vaadin.ui.VerticalLayout;
/**
 * Komponentti joka näyttää jonkin laitteen tilan progressbarilla tms
 */
public abstract class SensorComponent extends VerticalLayout {

	private DeviceStatus status;
	private Device dataSource;
	
	/**
	 * Add ProgressBar and Label to represent Sensor value and buttons to alter it
	 */
	public SensorComponent(DeviceStatus status) {
		super();
		this.status = status;
	}

	/**
	 * Asettaa laitteen, johon tila perustuu. Tämä mahdollistaa esim
	 * laitteen tilan päivittämisen, joten sen kutsuminen on yleensä tarpeeen.
	 */
	public void setDataSource(Device dataSource){
		this.dataSource = dataSource;
	}

	public Device getDataSource(){
		return dataSource;
	}

	public DeviceStatus getStatus(){
		return status;
	}

}

	
