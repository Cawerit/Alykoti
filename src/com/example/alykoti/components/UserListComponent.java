package com.example.alykoti.components;

import com.example.alykoti.AlykotiUI;
import com.example.alykoti.models.Device;
import com.example.alykoti.models.User;
import com.example.alykoti.models.devices.DeviceType;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserListComponent extends VerticalLayout {

	List<User> users;

	public UserListComponent(){}

	public UserListComponent(Device device){
		this.users = device.users;
		Label header = new Label("Käyttäjät jotka näkevät tämän laitteen tilan");
		header.setStyleName("h6");
		addComponent(header);
		List<String> asString = users.stream().map(User::getUsername).collect(Collectors.toList());
		addComponent(new Label(String.join(", ", asString)));

		Button addUser = new Button("Lisää käyttäjiä", FontAwesome.USER_PLUS);
		addComponent(addUser);
		addUser.addClickListener(click -> {
			Window subWindow = new Window("Lisää käyttäjä");
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

			Button cancel = new Button("Peruuta", FontAwesome.UNDO);
			cancel.addClickListener(ignored -> subWindow.close());
			subContent.addComponent(cancel);

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
			subContent.addComponent(save);
			AlykotiUI.getCurrent().addWindow(subWindow);
		});

	}
}
