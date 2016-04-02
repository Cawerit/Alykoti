package com.example.alykoti.components;

import com.example.alykoti.models.devices.DeviceStatus;
import com.vaadin.data.Item;
import com.vaadin.ui.Table;

import java.util.Collection;
import java.util.List;

public class DeviceStatusComponent extends Table {

	public static final String TITLE_STATUS_NAME = "",//Ei näytetä otsikkoa statuksen nimen kohdalla
								TITLE_STATUS_VALUE = "Tila",
								TITLE_VISIBLE_FOR_USERS = "Näkyvissä käyttäjille";

	private Collection<DeviceStatus> statuses;

	public DeviceStatusComponent(Collection<DeviceStatus> statuses) {
		this.statuses = statuses;
		update();
		setWidth("100%");
		setHeightUndefined();
	}

	private void update(){
		addContainerProperty(TITLE_STATUS_NAME, String.class, null);
		addContainerProperty(TITLE_STATUS_VALUE, String.class, null);

		for(DeviceStatus stat : statuses){
			Object rowId = addItem();
			Item row = getItem(rowId);
			row.getItemProperty(TITLE_STATUS_NAME).setValue(stat.statusType.toString("fi"));
			row.getItemProperty(TITLE_STATUS_VALUE).setValue(stat.valueNumber.toString());
		}
	}

	public void update(List<DeviceStatus> statuses){
		removeAllItems();
		this.statuses = statuses;
		update();
	}

}
