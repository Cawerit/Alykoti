package com.example.alykoti.models.devices;

import com.example.alykoti.components.SensorComponent;
import com.example.alykoti.components.sensors.CheckboxComponent;
import com.example.alykoti.components.sensors.SliderComponent;

/**
 * Wrapper-luokka laitteiden tilan hallintaan.
 * Laitteiden tila esitetään joko numeerisessa tai string-muodossa.
 */
public class DeviceStatus {

	public final String valueStr;
	public final Integer valueNumber;
	public final Type statusType;

	public DeviceStatus(Type statusType, String value){
		this.statusType = statusType;
		this.valueNumber = null;
		this.valueStr = value;
	}

	public DeviceStatus(Type statusType, Integer value){
		this.statusType = statusType;
		valueNumber = value;
		valueStr = null;
	}

	@Override
	public String toString(){
		String value = valueNumber != null ? ""+ valueNumber
				: valueStr != null ? ("\"" + valueStr + "\"")
				: null;
		return "{ " + statusType + ": " + value + " }";
	}

	public SensorComponent toComponent(){
		switch(statusType){
			case BRIGHTNESS:
			case TEMPERATURE:
			case VOLUME:
				return new SliderComponent(this, 0, 100);
			case POWER:
			case OPEN:
			case LOCKED:
				return new CheckboxComponent(this);
			default:
				return null;
		}
	}

	public enum Type {

		BRIGHTNESS("BRIGHTNESS", "Kirkkaus"),
		TEMPERATURE("TEMPERATURE", "Lämpotila"),
		POWER("POWER", "Virta"),
		VOLUME("VOLUME", "Äänenvoimakkuus"),
		OPEN("OPEN", "Avoinna"),
		LOCKED("LOCKED", "Lukossa");

		private String sqlColumn;
		private String translationFi;

		Type(String sqlColumn, String translationFi){
			this.sqlColumn = sqlColumn;
			this.translationFi = translationFi;
		}

		Type(String sqlColumn){
			this(sqlColumn, sqlColumn.toLowerCase());
		}

		@Override
		public String toString(){
			return this.sqlColumn;
		}

		public String toString(String lang){
			switch (lang){
				case "fi":
					return translationFi;
			}
			return null;
		}

		/**
		 * Luo DeviceType-olion tietokannasta haetun tekstin perusteella
		 * @param str
		 * @return
		 */
		public static Type fromString(String str){
			for(Type d : Type.values()){
				if(d.toString().equals(str)) return d;
			}
			return null;
		}
	}

}
