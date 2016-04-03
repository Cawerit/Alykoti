package com.example.alykoti.components;

import com.example.alykoti.AlykotiUI;
import com.example.alykoti.models.Device;
import com.example.alykoti.models.devices.DeviceStatus;
import com.example.alykoti.services.ObserverService;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import java.sql.SQLException;

/**
 * Komponentti joka näyttää jonkin laitteen tilan progressbarilla tms
 */
public abstract class SensorComponent extends VerticalLayout {

	private DeviceStatus status;
	private Device dataSource;
	private boolean followingValueChanges = false;

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
		if(!followingValueChanges){//Varmistetaan että listener asetetaan vain kerran
			followingValueChanges = true;
			//Luodaan päivittäjä joka päivittää sliderin tilan tietokantaan
			getNotifier().addValueChangeListener(new ValueChangeListener());
			((AlykotiUI) AlykotiUI.getCurrent()).subscribeObserver(dataSource, this::onNext);
		}
	}

	/**
	 * Palauttaa komponentin joka säätää statuksen arvoa.
	 * Tämä metodi on implementoitava jotta komponentin arvo voidaan "sitoa" tietokannan arvoon.
	 * @return
	 */
	public abstract Property.ValueChangeNotifier getNotifier();

	/**
	 * Tämän metodin ylikirjoittamalla voidaan komponentin tilaa päivittää kun sen data muuttuu.
	 * @param newStatus Uusi laitteen tila
	 */
	public abstract void onNext(DeviceStatus newStatus);

	private void onNext(Object o){
		if(o != null && o instanceof Device){
			Device changedDevice = (Device) o;
			DeviceStatus newStatus = changedDevice.statuses.get(getStatus().statusType);
			this.status = newStatus;
			onNext(newStatus);
		}
	}

	public Device getDataSource(){
		return dataSource;
	}

	public DeviceStatus getStatus(){
		return status;
	}

	public String getStatusName(){
		String statusName = getStatus().statusType.toString("fi");
		return statusName == null ? null : statusName.toLowerCase();
	}

	/**
	 * Callback jota vaadin kutsuu kun komponentin arvo muuttuu, jolloin se voidaan päivittää
	 * tietokantaan.
	 */
	private class ValueChangeListener implements Property.ValueChangeListener {
		@Override
		public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
			Object value = valueChangeEvent.getProperty().getValue();
			Device currentDevice = getDataSource();
			DeviceStatus status = getStatus();
			if(currentDevice != null && status != null && value != null){
				//Statukset ovat käytännössä muuntumattomia objekteja, tehdään uusi tilalle
				DeviceStatus newStatus =
						value instanceof Double ? new DeviceStatus(status.statusType, ((Double)value).intValue())
					:	new DeviceStatus(status.statusType, (String) value);
				try {
					currentDevice.setStatus(newStatus);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			//Ilmoitetaan muille käyttäjille/näkymille muutoksesta
			ObserverService.getInstance().update(currentDevice);
		}
	}
}

	
