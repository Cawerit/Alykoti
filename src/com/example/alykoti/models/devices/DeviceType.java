package com.example.alykoti.models.devices;

/**
 * Listaa laitteiden mahdolliset tyypit
 */
public enum DeviceType {

	STEREO("STEREO"),
	LAMP("LAMP", "lamppu"),
	FRIDGE("FRIDGE", "jääkaappi"),
	DOOR("DOOR", "sähköovi");

	private String sqlColumn;
	private String translationFi;

	DeviceType(String sqlColumn, String translationFi){
		this.sqlColumn = sqlColumn;
		this.translationFi = translationFi;
	}

	DeviceType(String sqlColumn){
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
	public static DeviceType fromString(String str){
		for(DeviceType d : DeviceType.values()){
			if(d.toString().equals(str)) return d;
		}
		return null;
	}

}
