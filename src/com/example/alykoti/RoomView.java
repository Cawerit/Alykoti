package com.example.alykoti;

import com.example.alykoti.components.RoomComponent;
import com.example.alykoti.models.Device;
import com.example.alykoti.models.Home;
import com.example.alykoti.models.Room;
import com.example.alykoti.services.AuthService;
import com.example.alykoti.services.AuthService.Role;
import com.vaadin.client.ui.Icon;
import com.vaadin.data.Item;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import java.sql.SQLException;
import java.util.List;

public class RoomView extends AppView implements View {
	
	public final VerticalLayout content = new VerticalLayout();
	public final HorizontalLayout buttons = new HorizontalLayout();
	public final Button prev = new Button();
	public final Button next = new Button();
	public final Table roomTable = new Table();
	private Room room = new Room();
	
	public RoomView() {
		super(AuthService.Role.ADMIN);
		addComponent(content);
		content.addComponent(roomTable);
		content.addComponent(buttons);
		buttons.addComponent(prev);
		buttons.addComponent(next);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		
		roomTable.addContainerProperty("Item", String.class, null);
		roomTable.addContainerProperty("Status", String.class, null);
		roomTable.setPageLength(roomTable.size());
		prev.setCaption("Previous room");
		prev.setSizeUndefined();
		prev.setIcon(FontAwesome.CHEVRON_CIRCLE_LEFT);
		prev.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				//siirry edelliseen huoneeseen
			}
		});
		next.setCaption("Next room");
		next.setIcon(FontAwesome.CHEVRON_CIRCLE_RIGHT);
		next.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				//siirry seuraavaan huoneeseen
			}
		});
		
		content.setSizeFull();
		content.addComponent(roomTable);
		content.addComponent(buttons);
		buttons.addComponent(prev);
		buttons.addComponent(next);
		content.setComponentAlignment(roomTable, Alignment.MIDDLE_CENTER);
		content.setComponentAlignment(buttons, Alignment.TOP_CENTER);
		buttons.setComponentAlignment(prev, Alignment.TOP_CENTER);
		buttons.setComponentAlignment(next, Alignment.TOP_CENTER);
		
		Integer roomId = Integer.parseInt(event.getParameters());
		room.setId(roomId);
		try {
			room.pull();//Päivitetään huone-olion sisältö tietokannasta
			roomTable.setCaption(room.getName());
			List<Device> devices = room.getDevices();
			for(Device d : devices)
				addToTable(d);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	
	
	private void addToTable (Device d){
		Object newItem = roomTable.addItem();
		Item tableRow = roomTable.getItem(newItem);
		tableRow.getItemProperty("Item").setValue(d.getName());
		tableRow.getItemProperty("Status").setValue(d.getStatusValueStr());
	}

}
