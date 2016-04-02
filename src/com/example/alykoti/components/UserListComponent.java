package com.example.alykoti.components;

import com.example.alykoti.AlykotiUI;
import com.example.alykoti.models.Device;
import com.example.alykoti.models.User;
import com.example.alykoti.models.devices.DeviceType;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserListComponent extends VerticalLayout {

	Device device;

	public UserListComponent(){}

	public UserListComponent(Device device){
		this.device = device;
		List<User> users = device.users;

		Label header = new Label("Käyttäjät jotka näkevät tämän laitteen tilan");
		header.setStyleName("h5");
		addComponent(header);

		for(User u : users)
			addComponent(new UserComponent(u, this::removeUserComponent));

		//Luodaan nappi  jolla voidaan lisätä lisää käyttäjiä
		Button addUser = new Button("Lisää käyttäjiä", FontAwesome.USER_PLUS);
		addComponent(addUser);
		addUser.addClickListener(click -> {
			//Avataan modaali
			Window subWindow = new Window("Lisää käyttäjä");
			subWindow.center();
			VerticalLayout subContent = new VerticalLayout();
			subContent.setMargin(true);
			subWindow.setContent(subContent);
			ListSelect userSelection = new ListSelect("Valitse käyttäjä");
			List<User> options = null;
			try {
				//Hataan kaikki käyttäjät jotta voidaan näyttää nimet listassa
				options = new User().query();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(options == null){
				subWindow.close();
				return;
			}
			for(User u : options){
				if(users.indexOf(u) != -1) continue;
				Object key = userSelection.addItem(u);
				userSelection.setItemCaption(key, u.getUsername());
			}
			userSelection.setWidth("100%");
			userSelection.setNullSelectionAllowed(false);
			subContent.addComponent(userSelection);

			HorizontalLayout bottomButtons = new HorizontalLayout();
			subContent.addComponent(bottomButtons);

			Button cancel = new Button("Peruuta", FontAwesome.UNDO);
			cancel.addClickListener(ignored -> subWindow.close());
			bottomButtons.addComponent(cancel);

			Button save = new Button("Lisää käyttäjä", FontAwesome.USER_PLUS);
			save.addClickListener(ignored -> {
				Object val = userSelection.getValue();
				if(val != null){
					User u = (User) val;
					try {
						device.addUser(u);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				subWindow.close();
			});
			bottomButtons.addComponent(save);

			AlykotiUI.getCurrent().addWindow(subWindow);
		});
	}

	private void removeUserComponent(UserComponent u){
		try {
			device.removeUser(u.getUser());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		removeComponent(u);
	}

}
