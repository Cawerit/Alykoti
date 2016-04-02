package com.example.alykoti.components;

import com.example.alykoti.models.User;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserListComponent extends VerticalLayout {

	List<User> users;

	public UserListComponent(){}

	public UserListComponent(List<User> users){
		this.users = users;
		Label header = new Label("Käyttäjät jotka näkevät tämän laitteen tilan");
		header.setStyleName("h1");
		addComponent(header);
		List<String> asString = users.stream().map(User::getUsername).collect(Collectors.toList());
		addComponent(new Label(String.join(", ", asString)));
	}

}
