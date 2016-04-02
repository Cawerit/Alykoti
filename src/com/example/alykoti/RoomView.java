package com.example.alykoti;

import com.example.alykoti.components.DeviceStatusComponent;
import com.example.alykoti.components.RoomComponent;
import com.example.alykoti.components.UserListComponent;
import com.example.alykoti.models.Device;
import com.example.alykoti.models.Home;
import com.example.alykoti.models.Room;
import com.example.alykoti.models.User;
import com.example.alykoti.models.devices.DeviceStatus;
import com.example.alykoti.models.devices.DeviceType;
import com.example.alykoti.services.AuthService;
import com.example.alykoti.services.AuthService.Role;
import com.vaadin.client.ui.Icon;
import com.vaadin.data.Item;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import java.sql.SQLException;
import java.util.List;

public class RoomView extends AppView implements View {
	
	public final VerticalLayout content = new VerticalLayout();
	public final HorizontalLayout buttons = new HorizontalLayout();
	public final Button prev = new Button();
	public final Button next = new Button();
	public final Accordion roomAcccordion = new Accordion();
	private Room room = new Room();
	private Integer roomId = 0;
	private Integer homeId = 0;
	
	public RoomView() {
		super(AuthService.Role.ADMIN);
		content.setSizeFull();
		addComponent(content);
		content.addComponent(roomAcccordion);
		content.addComponent(buttons);
		buttons.addComponent(prev);
		buttons.addComponent(next);
		content.setComponentAlignment(roomAcccordion, Alignment.MIDDLE_CENTER);
		content.setComponentAlignment(buttons, Alignment.TOP_CENTER);
		buttons.setComponentAlignment(prev, Alignment.TOP_CENTER);
		buttons.setComponentAlignment(next, Alignment.TOP_CENTER);
		roomAcccordion.setHeightUndefined();
		roomAcccordion.setWidth("50%");
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		roomAcccordion.removeAllComponents();

		prev.setCaption("Edellinen huone");
		prev.setSizeUndefined();
		prev.setIcon(FontAwesome.CHEVRON_CIRCLE_LEFT);
		if(next.getListeners(ClickEvent.class).size() == 0) {
			prev.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					//siirry edelliseen huoneeseen
					int prev = room.getAdjacent(false);
					AlykotiUI.NAVIGATOR.navigateTo(AlykotiUI.ROOMVIEW + "/" + homeId + "/" + prev);
				}
			});
		}
		next.setCaption("Seuraava huone");
		next.setIcon(FontAwesome.CHEVRON_CIRCLE_RIGHT);
		if(next.getListeners(ClickEvent.class).size() == 0) {
			next.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					//siirry seuraavaan huoneeseen
					int next = room.getAdjacent(true);
					AlykotiUI.NAVIGATOR.navigateTo(AlykotiUI.ROOMVIEW + "/" + homeId + "/" + next);
				}
			});
		}
		

		//Haetaan URL:sta kodin ja huoneen id:t
		String fragment = event.getParameters();
		for(int i = 0; i < fragment.length(); i++){
			if(fragment.charAt(i) == '/'){
				homeId = Integer.parseInt(fragment.substring(0, i));
				roomId = Integer.parseInt(fragment.substring(i + 1, fragment.length()));
			}
		}
		 
		room.setId(roomId);
		try {
			room.pull();//Päivitetään sisältö tietokannasta
			roomAcccordion.setCaption(room.getName());
			List<Device> devices = room.getDevices();
			for(Device d : devices) addToAccordion(d);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	
	
	private void addToAccordion (Device d){
		VerticalLayout tabContent = new VerticalLayout();
		tabContent.setWidth("100%");
		DeviceStatusComponent statusTable = new DeviceStatusComponent(d.statuses.values());
		UserListComponent userlist = new UserListComponent(d.users);
		tabContent.addComponent(statusTable);
		tabContent.addComponent(userlist);
		roomAcccordion.addTab(tabContent, d.getName());
	}

}
