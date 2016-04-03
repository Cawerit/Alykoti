package com.example.alykoti.components.sensors;

import com.example.alykoti.components.SensorComponent;
import com.example.alykoti.models.devices.DeviceStatus;
import com.vaadin.data.Property;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.ui.Slider;

import java.util.Date;

/**
 * Slider jolla voi säätää laitteen numeerista tilaa
 */
public class SliderComponent extends SensorComponent {

	private final Slider slider;

	public SliderComponent(DeviceStatus status, int minValue, int maxValue) {
		super(status);
		slider = new Slider(minValue, maxValue);
		addComponent(slider);
		slider.setValue((double) status.valueNumber);
	}


	@Override
	public Property.ValueChangeNotifier getNotifier() {
		return slider;
	}

	@Override
	public void onNext(DeviceStatus newStatus) {
		if(newStatus != null){
			slider.setValue((double) newStatus.valueNumber);
		}
	}

	@Override
	protected DeviceStatus valueToStatus(Object newValue) {
		//Vaadin slider palauttaa arvot doublena
		int value = newValue != null && newValue instanceof Double ? ((Double) newValue).intValue() : 0;
		return new DeviceStatus(getStatus().statusType, value, new Date().getTime());
	}
}
