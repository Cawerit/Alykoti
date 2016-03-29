package com.example.alykoti;

import com.example.alykoti.components.RoomComponent;
import com.example.alykoti.models.Home;
import com.example.alykoti.models.Room;
import com.example.alykoti.services.AuthService;
import com.sun.istack.internal.Nullable;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TreeTable;

import java.sql.SQLException;
import java.util.List;

public class HomeView extends AppView {

    public final Accordion accordion = new Accordion();
	public final Label homeLabel = new Label();

    public HomeView(){
		super(AuthService.Role.USER);
		addComponent(homeLabel);
		addComponent(accordion);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event){
        super.enter(event);
		Integer homeId = Integer.parseInt(event.getParameters());//Kodin id saadaan url parametreista
		//Tyhjätään vanha sisältö
		accordion.removeAllComponents();
		//Luodaan ylin palkki, joka sisältää napin huoneen lisäämiseen
		Room newRoom = new Room();
		newRoom.setHomeId(homeId);
		TabSheet.Tab addRoomComponent = accordion.addTab(new RoomComponent(newRoom, (Room room) -> {
			//Callback lisää uuden huoneen listaan
			TabSheet.Tab newTab = accordion.addTab(new RoomComponent(room), room.getName());
		}), "Lisää huone", FontAwesome.PLUS);

        Home home = new Home(homeId);
		try {
			home.pull();//Päivitetään koti-olion sisältö tietokannasta
			homeLabel.setValue(home.getName());
			List<Room> rooms = home.getRooms();
			for(Room r : rooms)
				accordion.addTab(new RoomComponent(r), r.getName(), FontAwesome.HOME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

}
