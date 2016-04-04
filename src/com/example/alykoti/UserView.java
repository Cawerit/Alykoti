package com.example.alykoti;

import com.example.alykoti.components.DeviceStatusComponent;
import com.example.alykoti.models.*;
import com.example.alykoti.services.AuthService;
import com.vaadin.data.Item;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Käyttäjän etusivunäkymä
 */
public class UserView extends AppView {
	HorizontalLayout bar;

	private Label welcome = new Label();
	private Layout roomContainer = new VerticalLayout();
	
	public UserView() {
		super(AuthService.Role.USER);

		welcome.addStyleName("h2");
		addComponent(welcome);
		addComponent(roomContainer);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		Notification.show("Welcome!");
		User currentUser = AuthService.getInstance().getCurrentUser(this.getUI());
		String name = currentUser == null ? "" : currentUser.getUsername();
		welcome.setValue("Tervetuloa " + name);
		initRooms(currentUser);
	}

	private void initRooms(User current){
		roomContainer.removeAllComponents();
		//Haetaan kaikki laitteet joihin käyttäjällä on pääsy
		Device deviceQuery = new Device();
		deviceQuery.users.add(current);

		List<Device> devicesForUser = null;
		try {
			devicesForUser = deviceQuery.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if(devicesForUser == null || devicesForUser.size() == 0){
			Notification.show("Sinua ei ole vielä lisätty minkään laitteen hallitsijaksi. Pyydä admin-käyttäjiltä apua.", Notification.Type.WARNING_MESSAGE);
			return;
		}

		//Haetaan vielä tiedot niistä huoneista joihin yllä kerätyt laitteet kuuluvat
		Stream<Integer> roomIdStream = devicesForUser.stream().map(Device::getRoom);
		List<Integer> roomIds = roomIdStream.distinct().collect(Collectors.toList());
		List<Room> userRooms = null;
		try {
			userRooms = new Room().query(roomIds);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if(userRooms == null) {
			notifyError();
			return;
		}

		Stream<Integer> homeIdStream = userRooms.stream().map(Room::getHomeId);
		List<Integer> homeIds = homeIdStream.distinct().collect(Collectors.toList());
		List<Home> homes = null;
		try {
			homes = new Home().query(homeIds);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(homes == null){
			notifyError();
			return;
		}


		TreeTable homeTable = new TreeTable();
		homeTable.addContainerProperty("Koti, huone, laite", String.class, null);
		homeTable.addContainerProperty("Tila", DeviceStatusComponent.class, null);
		homeTable.setWidth("100%");
		homeTable.setHeight("50%");


		//Kaikki data on haettu, listataan käyttäjän huoneet komponentteina
		for(int i=0, n=homeIds.size(); i<n; i++){
			Integer homeId = homeIds.get(i);
			homeTable.addItem(new Object[]{findResource(homes, homeId).getName(), null}, "home_" + homeId);
		}
		for(int i=0, n=roomIds.size(); i<n; i++){
			Integer roomId = roomIds.get(i);
			Room room = findResource(userRooms, roomId);
			String colId = "room_" + roomId;
			homeTable.addItem(new Object[]{room.getName(), null}, colId);
			homeTable.setParent(colId, "home_"+room.getHomeId());
		}

		for(Device d : devicesForUser){
			String name = d.getName();
			//Esitetään laitteen nimi joko muodossa "nimi (tyyppi)" tai "tyyppi"
			if(name == null||name.length() == 0){
				name = d.getType().toString("fi");
			} else name += " (" + d.getType().toString("fi") + ")";

			Object[] cols = new Object[]{
				name,
				new DeviceStatusComponent(d)
			};
			String colId = "device_"+d.getId();
			homeTable.addItem(cols, colId);
			homeTable.setParent(colId, "room_"+d.getRoom());
			homeTable.setChildrenAllowed(colId, false);
		}

		roomContainer.addComponent(homeTable);
	}

	private void notifyError(){
		Notification.show("Jokin meni pieleen, ota yhteyttä ylläpitoon", Notification.Type.ERROR_MESSAGE);
	}

	private <T extends IResource> T findResource(List<T> haystack, Integer needle){
		for(T hay : haystack){
			if(needle.equals(hay.getId())) return hay;
		}
		return null;
	}


}
