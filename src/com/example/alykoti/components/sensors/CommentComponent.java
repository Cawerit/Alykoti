package com.example.alykoti.components.sensors;

import com.example.alykoti.components.SensorComponent;
import com.example.alykoti.models.devices.DeviceStatus;
import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.StringDecorator;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextArea;

import java.util.Date;

/**
 * Komponentti jolla voi kiinnittää laitteisiin kommentteja.
 * Kommenttikomponentilla voi esimerkiksi kiinnittää jääkaapin tms
 * kosketusnäyttöön käteviä muistutuksia, kuten "Osta maitoa".
 */
public class CommentComponent extends SensorComponent {

	private final TextArea text;

	public CommentComponent(DeviceStatus status) {
		super(status);
		text = new TextArea();
		addComponent(text);
		text.setValue(status.valueStr);
		text.setMaxLength(400);
		text.setHeight("90px");
		text.setWidth("100%");
	}

	@Override
	public Property.ValueChangeNotifier getNotifier() {
		return text;
	}

	@Override
	protected void onNext(DeviceStatus newStatus) {
		if(newStatus != null) text.setValue(newStatus.valueStr);
	}

	@Override
	protected DeviceStatus valueToStatus(Object value) {
		String val = value != null && value instanceof String ? ((String) value) : null;
		return new DeviceStatus(getStatus().statusType, val, new Date().getTime());
	}
}
