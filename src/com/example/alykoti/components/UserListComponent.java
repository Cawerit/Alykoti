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

	private Device device;
	private HorizontalLayout userlist = new HorizontalLayout();

	public UserListComponent(){}

	public UserListComponent(Device device){
		super();
		this.device = device;
		List<User> users = device.users;

		Label header = new Label("Käyttäjät jotka näkevät tämän laitteen tilan");
		header.setStyleName("h5");
		addComponent(header);
		addComponent(userlist);

		for(User u : users)
			userlist.addComponent(new UserComponent(u, this::removeUserComponent));

		//Luodaan nappi  jolla voidaan lisätä lisää käyttäjiä
		Button addUser = new Button("Lisää käyttäjä", FontAwesome.USER_PLUS);
		addUser.setDescription("Anna käyttäjälle oikeus muokata laitteen tilaa");
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

			Button save = new Button("Tallenna", FontAwesome.USER_PLUS);
			save.addClickListener(ignored -> {
				Object val = userSelection.getValue();
				if(val != null){
					User u = (User) val;
					try {
						device.addUser(u);
						//Lisätään uuden käyttäjän komponentti listaan
						Component newComponent = new UserComponent(u, this::removeUserComponent);
						userlist.addComponent(newComponent);
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
		System.out.println("Remove user " + u.getUser() + " " + device);
		try {
			device.removeUser(u.getUser());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		userlist.removeComponent(u);
	}

}
