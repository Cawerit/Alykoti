package com.example.alykoti.components.sensors;

import com.example.alykoti.components.SensorComponent;
import com.example.alykoti.models.Device;
import com.example.alykoti.models.devices.DeviceStatus;
import com.vaadin.data.Property;
import com.vaadin.ui.Slider;

import java.sql.SQLException;

/**
 * Slider jolla voi säätää laitteen numeerista tilaa
 */
public class SliderComponent extends SensorComponent {

	private final Slider slider;
	private boolean followingValueChanges = false;

	public SliderComponent(DeviceStatus status, int minValue, int maxValue) {
		super(status);
		slider = new Slider(minValue, maxValue);
		addComponent(slider);
		slider.setValue((double) status.valueNumber);
	}

	@Override
	public void setDataSource(Device device){
		super.setDataSource(device);
		if(!followingValueChanges){//Varmistetaan että listener asetetaan vain kerran
			followingValueChanges = true;
			//Luodaan päivittäjä joka päivittää sliderin tilan tietokantaan
			slider.addValueChangeListener(new SliderValueChangeListener());
		}
	}

	private class SliderValueChangeListener implements Property.ValueChangeListener {
		@Override
		public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
			int value = slider.getValue().intValue();
			Device currentDevice = getDataSource();
			DeviceStatus status = getStatus();
			if(currentDevice != null && status != null){
				//Statukset ovat käytännössä muuntumattomia objekteja, tehdään uusi tilalle
				DeviceStatus newStatus = new DeviceStatus(status.statusType, value);
				try {
					currentDevice.setStatus(newStatus);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
