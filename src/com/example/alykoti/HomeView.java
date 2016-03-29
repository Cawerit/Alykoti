package com.example.alykoti;

import com.example.alykoti.components.RoomComponent;
import com.example.alykoti.models.Home;
import com.example.alykoti.models.Room;
import com.example.alykoti.services.AuthService;
import com.sun.istack.internal.Nullable;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import java.sql.SQLException;
import java.util.List;

public class HomeView extends AppView {

    public final Accordion accordion = new Accordion();
	//public final Label homeLabel = new Label();
	public final VerticalLayout content = new VerticalLayout();
	public final Panel homePanel = new Panel();
	public final Button roomButton = new Button();
	
    public HomeView(){
		super(AuthService.Role.USER);
		//addComponent(homeLabel);
		content.setSizeFull();
		homePanel.setSizeUndefined();
		homePanel.setContent(accordion);
		content.addComponent(homePanel);
		content.addComponent(roomButton);
		content.setComponentAlignment(homePanel, Alignment.MIDDLE_CENTER);
		content.setComponentAlignment(roomButton, Alignment.TOP_CENTER);
		addComponent(content);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event){
        super.enter(event);
		Integer homeId = Integer.parseInt(event.getParameters());//Kodin id saadaan url parametreista
		//Tyhj‰t‰‰n vanha sis‰ltˆ
		accordion.removeAllComponents();
		//Luodaan ylin palkki, joka sis‰lt‰‰ napin huoneen lis‰‰miseen
		Room newRoom = new Room();
		newRoom.setHomeId(homeId);
		TabSheet.Tab addRoomComponent = accordion.addTab(new RoomComponent(newRoom, (Room room) -> {
			//Callback lis√§√§ uuden huoneen listaan
			TabSheet.Tab newTab = accordion.addTab(new RoomComponent(room), room.getName());
		}), "Lis‰‰ huone", FontAwesome.PLUS);
		
        Home home = new Home(homeId);
		try {
			home.pull();//P‰ivitet‰‰n koti-olion sis‰ltˆ tietokannasta
			homePanel.setCaption(home.getName());
			List<Room> rooms = home.getRooms();
			for(Room r : rooms)
				accordion.addTab(new RoomComponent(r), r.getName(), FontAwesome.HOME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		//Nappi, jolla siirryt‰‰n huonen‰kym‰‰n
		roomButton.setCaption("Open Room View");
		roomButton.setIcon(FontAwesome.EYE);
		roomButton.addClickListener(new ClickListener(){
			@Override
			public void buttonClick(ClickEvent event) {
				//Menn‰‰n nyt uri-fragmentilla kodin ensimm‰iseen huoneeseen
				int firstRoomId = 0;
				try {
					firstRoomId = home.getRooms().get(0).getId();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				AlykotiUI.NAVIGATOR.navigateTo(AlykotiUI.ROOMVIEW + "/" + firstRoomId);
			}
		});
		
    }

}
