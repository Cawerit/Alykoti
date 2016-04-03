package com.example.alykoti.components;

import com.example.alykoti.models.Device;
import com.example.alykoti.models.devices.DeviceStatus;
import com.vaadin.data.Item;
import com.vaadin.ui.Table;

import java.util.Collection;
import java.util.List;

public class DeviceStatusComponent extends Table {

	public static final String TITLE_STATUS_NAME = "",//Ei näytetä otsikkoa statuksen nimen kohdalla
								TITLE_STATUS_VALUE = "Tila";

	private Device device;

	public DeviceStatusComponent(Device device) {
		super();
		this.device = device;
		update();
		setWidth("100%");
		setHeightUndefined();
		setHeightUndefined();
	}

	public void update(){
		addContainerProperty(TITLE_STATUS_NAME, String.class, null);
		addContainerProperty(TITLE_STATUS_VALUE, SensorComponent.class, null);

		for(DeviceStatus stat : device.statuses.values()){
			Object rowId = addItem();
			Item row = getItem(rowId);
			row.getItemProperty(TITLE_STATUS_NAME).setValue(stat.statusType.toString("fi"));
			SensorComponent sensor = stat.toComponent();
			sensor.setDataSource(device);
			row.getItemProperty(TITLE_STATUS_VALUE).setValue(sensor);
		}
	}

	public void update(Device newDevice){
		removeAllItems();
		this.device = newDevice;
		update();
	}

}
