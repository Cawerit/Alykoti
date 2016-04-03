package com.example.alykoti;

import com.example.alykoti.commands.SaveDeviceCommand;
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
	private Button prev = new Button();
	private Button next = new Button();
	public final Accordion roomAcccordion = new Accordion();
	private Room room = new Room();
	private final Label roomname = new Label();
	
	public RoomView() {
		super(AuthService.Role.ADMIN);
		content.setSizeFull();
		roomname.setStyleName("h1");
		addComponent(roomname);
		addComponent(content);
		content.addComponent(roomAcccordion);
		content.addComponent(buttons);
		content.setComponentAlignment(roomAcccordion, Alignment.MIDDLE_CENTER);
		content.setComponentAlignment(buttons, Alignment.TOP_CENTER);
		roomAcccordion.setHeightUndefined();
		roomAcccordion.setWidth("50%");
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		roomAcccordion.removeAllComponents();
		buttons.removeAllComponents();
		prev = new Button(); next = new Button();
		buttons.addComponent(prev);
		buttons.addComponent(next);
		buttons.setComponentAlignment(prev, Alignment.TOP_CENTER);
		buttons.setComponentAlignment(next, Alignment.TOP_CENTER);
		try {
			//Kodin ja huoneen id saadaan viimeisistä kahdesta url parametrista
			String[] params = event.getParameters().split("/");
			if(params.length < 2) {//fail, invalid url :(
				AlykotiUI.getCurrent().getNavigator().navigateTo(AlykotiUI.ADMIN_DASHBOARD_VIEW);
				return;
			}
			Integer homeId = Integer.parseInt(params[params.length-2]);
			Integer roomId = Integer.parseInt(params[params.length-1]);
			room.setId(roomId);
			room.setHomeId(homeId);

			//Hataan "seruaavan" ja "edellisen" huoneen idt
			Room.AdjacentIds adjacent = room.getAdjacent();

			if (adjacent == null || adjacent.getPrev() == null) {
				prev.setDisableOnClick(true);
				prev.setDescription("Ei enempää huoneita");
			} else prev.setDisableOnClick(false);

			if (adjacent == null || adjacent.getNext() == null) {
				next.setDisableOnClick(true);
				next.setDescription("Ei enempää huoneita");
			} else next.setDisableOnClick(false);


			prev.setCaption("Edellinen huone");
			prev.setSizeUndefined();
			prev.setIcon(FontAwesome.CHEVRON_CIRCLE_LEFT);
			prev.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					//siirry edelliseen huoneeseen
					if(adjacent.getPrev() != null)
						AlykotiUI.getCurrent().getNavigator().navigateTo(AlykotiUI.ROOMVIEW + "/" + homeId + "/" + adjacent.getPrev());
				}
			});
			next.setCaption("Seuraava huone");
			next.setIcon(FontAwesome.CHEVRON_CIRCLE_RIGHT);

			next.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					//siirry seuraavaan huoneeseen
					if(adjacent.getNext() != null)
						AlykotiUI.getCurrent().getNavigator().navigateTo(AlykotiUI.ROOMVIEW + "/" + homeId + "/" + adjacent.getNext());
				}
			});

			try {
				room.pull();//Päivitetään sisältö tietokannasta
				roomAcccordion.setCaption("Huoneen laitteet");
				roomname.setValue(room.getName());
				List<Device> devices = room.getDevices();
				for (Device d : devices) addToAccordion(d);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			Button addDevice = new Button("Lisää laite", FontAwesome.PLUS);
			addDevice.addClickListener(new SaveDeviceCommand(roomId, this::addToAccordion));
			buttons.addComponent(addDevice);

		} catch(Exception e){
			e.printStackTrace();
		}
    }
	
	
	private void addToAccordion (Device d){
		VerticalLayout tabContent = new VerticalLayout();
		tabContent.setWidth("100%");
		DeviceStatusComponent statusTable = new DeviceStatusComponent(d);
		UserListComponent userlist = new UserListComponent(d);
		tabContent.addComponent(statusTable);
		tabContent.addComponent(userlist);
		roomAcccordion.addTab(tabContent, d.getName() + " (" + d.getType().toString("fi") + ")");
	}

}
