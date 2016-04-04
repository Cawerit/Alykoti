package com.example.alykoti.models.devices;

import com.example.alykoti.models.Resource;
import java.sql.Types;

/**
 * Luokka jolla kerätään kooste statusmuutoksista etusivulle
 */
public class StatusChangeSummary extends Resource<StatusChangeSummary> {

	//Tämä on käytännössä muuttumaton, tietokannasta haettava datarakenne
	@Column public String name;
	@Column public String type;
	@Column public String statusType;
	@Column public String valueStr;
	@Column(sqlType = Types.INTEGER) public Integer room;
	@Column(sqlType = Types.INTEGER) public Integer valueNumber;
	@Column(sqlType = Types.TIMESTAMP) public java.sql.Timestamp updated;

	public StatusChangeSummary() {
		super(StatusChangeSummary.class, "latest_status_updates");
	}

	//HUOM! Tämä ei ole aivan tavallinen Resource-luokka, sillä tämän luokan data perustuu näkymään
	//Jota ei ole missään vaiheessa tarkoitus muokata
	//Käytetään lähinnä hyödyksi valmiin query-toteutuksen takia

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer id) {
	}
}
