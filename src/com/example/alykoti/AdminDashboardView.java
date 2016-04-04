package com.example.alykoti;

import com.example.alykoti.models.devices.DeviceStatus;
import com.example.alykoti.models.devices.StatusChangeSummary;
import com.example.alykoti.services.AuthService;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Etusivu adminille
 */
public class AdminDashboardView extends AppView {

	private final Layout container = new VerticalLayout();

    public AdminDashboardView() {
        super(AuthService.Role.ADMIN);
		Label header = new Label("Viimeisimmät muutokset");
		header.setStyleName("h3");
		addComponent(header);
		addComponent(container);
		setMargin(true);
    }

    @Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		container.removeAllComponents();
		List<StatusChangeSummary> prevChanges = null;
		try {
			prevChanges = new StatusChangeSummary().query();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if(prevChanges != null){
			DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			for(StatusChangeSummary change : prevChanges){
				//Selvitetään statuksen tyyppi
				DeviceStatus.Type type = DeviceStatus.Type.fromString(change.statusType);
				String statusType = type.toString("fi");
				if(statusType == null) statusType = "";
				//Muutosaika muodossa päivä.kuukausi.vuosi tunti:minuutti
				String changeTime = formatter.format(change.updated);
				String val;
				if(DeviceStatus.Type.POWER.equals(type) || DeviceStatus.Type.LOCKED.equals(type)){
					//Jos kyseessä on päällä/pois valinta, muunnetaan intistä tekstiksi
					val = change.valueNumber == 1 ? "päällä" : "pois päältä";
				} else {
					//Muutoin tulostetaan suoraan laitteen arvo
					val = "" + (change.valueStr == null ? change.valueNumber : ("\"" + change.valueStr + "\""));
				}

				String txt = changeTime + ": Laitteen \"" + change.name + "\" " + " huoneessa " + change.room + " " +
						statusType.toLowerCase() + " on nyt " + val + ".";

				Label row = new Label(txt);
				container.addComponent(row);
			}
		}
	}

}
