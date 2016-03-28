package com.example.alykoti.components;

import com.example.alykoti.models.Sensor;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * Widget that represents the state of a Sensor with a ProgressBar
 *
 */
public class SensorWidget extends VerticalLayout {
	private Sensor sensor;
	private ProgressBar bar;
	private HorizontalLayout buttons;
	private HorizontalLayout sensorStatus;
	private Button inc, dec;
	private Label sensorValue;
	private int currentValue, minValue, maxValue;
	
	/**
	 * Add ProgressBar and Label to represent Sensor value and buttons to alter it
	 * @param sensor the Sensor to represent
	 */
	public SensorWidget(Sensor sensor) {
		this.sensor = sensor;
		this.currentValue = sensor.getValue();
		this.minValue = sensor.getMinValue();
		this.maxValue = sensor.getMaxValue();
		
		bar = new ProgressBar(calibrateToBar(currentValue));
		
		inc = new Button();
		inc.setIcon(FontAwesome.PLUS);
		inc.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				changeValue(true);
			}
			
		});		
		dec = new Button();
		dec.setIcon(FontAwesome.MINUS);
		dec.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				changeValue(false);
			}	
		});
		buttons = new HorizontalLayout();
		buttons.addComponent(inc);
		buttons.addComponent(dec);
	
		sensorStatus = new HorizontalLayout();
		sensorValue = new Label(); 
		sensorValue.setValue(currentValue + sensor.getSymbol());
		sensorStatus.addComponent(bar);
		sensorStatus.addComponent(sensorValue);
		
		addComponent(sensorStatus);
		addComponent(buttons);

	}
	
	/**
	 * Change the value of the represented Sensor
	 * @param increase boolean to determine whether to increase or decrease value
	 */
	private void changeValue(boolean increase) {
		currentValue = sensor.getValue();
		int newValue = currentValue;
		if(increase && currentValue < maxValue) newValue = currentValue + 1;
		else if(currentValue > minValue) newValue = currentValue - 1;
		float newBarValue = (calibrateToBar(newValue));
		bar.setValue(newBarValue);
		sensor.setValue(newValue);
		sensorValue.setValue(newValue + sensor.getSymbol());
		}
	
	/**
	 * Calibrate Sensor value to ProgressBar value
	 * @param value the value of the Sensor
	 * @return value to set to bar
	 */
	private float calibrateToBar(int value) {
		return ((float) (value - minValue)) / ((float) (maxValue - minValue));
	}
}

	
