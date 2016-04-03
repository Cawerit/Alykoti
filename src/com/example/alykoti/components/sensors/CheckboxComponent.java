package com.example.alykoti.components.sensors;

import com.example.alykoti.components.SensorComponent;
import com.example.alykoti.models.devices.DeviceStatus;
import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;

import java.util.Date;

public class CheckboxComponent extends SensorComponent {

	private final CheckBox checkbox;

	public CheckboxComponent(DeviceStatus status) {
		super(status);
		String statusName = getStatusName();
		checkbox = new CheckBox();
		setValue(status.valueNumber);
		checkbox.setDescription("Muuta " + statusName);
		addComponent(checkbox);
	}

	@Override
	public Property.ValueChangeNotifier getNotifier() {
		return checkbox;
	}

	@Override
	public void onNext(DeviceStatus newStatus) {
		if(newStatus != null){
			setValue(newStatus.valueNumber);
		}
	}
	@Override
	protected DeviceStatus valueToStatus(Object newValue) {
		//Vaadin slider palauttaa arvot doublena
		int value = Boolean.TRUE.equals(newValue) ? 1 : 0;
		return new DeviceStatus(getStatus().statusType, value, new Date().getTime());
	}


	/**
	 * DeviceStatus-oliot osaavat käsitellä statustaan vain stringeinä/numeroina,
	 * joten muunnetaan numerosta booleaniksi ennen komponentin arvon muuttamista.
	 */
	private void setValue(int numericValue){
		checkbox.setValue(numericValue == 1);
	}
}
