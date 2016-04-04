package com.example.alykoti.models.devices;

import com.example.alykoti.components.SensorComponent;
import com.example.alykoti.components.sensors.CheckboxComponent;
import com.example.alykoti.components.sensors.CommentComponent;
import com.example.alykoti.components.sensors.SliderComponent;

/**
 * Wrapper-luokka laitteiden tilan hallintaan.
 * Laitteiden tila esitetään joko numeerisessa tai string-muodossa.
 */
public class DeviceStatus {

	public final String valueStr;
	public final Integer valueNumber;
	public final Type statusType;
	public final long updated;

	public DeviceStatus(Type statusType, String value, long updated){
		this.statusType = statusType;
		this.valueNumber = null;
		this.valueStr = value;
		this.updated = updated;
	}

	public DeviceStatus(Type statusType, Integer value, long updated){
		this.statusType = statusType;
		valueNumber = value;
		valueStr = null;
		this.updated = updated;
	}

	@Override
	public String toString(){
		String value = valueNumber != null ? ""+ valueNumber
				: valueStr != null ? ("\"" + valueStr + "\"")
				: null;
		return "{ " + statusType + ": " + value + " }";
	}

	/**
	 * Yhdistää laitetyypit niiden UI esitykseen
	 * @return
	 */
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
			case COMMENT:
				return new CommentComponent(this);
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
		LOCKED("LOCKED", "Lukossa"),
		COMMENT("COMMENT", "Kommentti laitteesta");

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
