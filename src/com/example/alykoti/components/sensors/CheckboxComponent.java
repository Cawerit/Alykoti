package com.example.alykoti.components.sensors;

import com.example.alykoti.components.SensorComponent;
import com.example.alykoti.models.devices.DeviceStatus;
import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;

public class CheckboxComponent extends SensorComponent {

	private final CheckBox checkbox;

	public CheckboxComponent(DeviceStatus status) {
		super(status);
		String statusName = getStatusName();
		checkbox = new CheckBox();
		checkbox.setValue(status.valueNumber == 1);
		checkbox.setDescription("Muuta " + statusName);
		addComponent(checkbox);
	}

	@Override
	public Property.ValueChangeNotifier getNotifier() {
		return checkbox;
	}
}
