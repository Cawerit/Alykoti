package com.example.alykoti.components;

import com.example.alykoti.models.User;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import java.util.function.Consumer;

/**
 * Simppeli komponentti käyttäjän nimen näyttämiseen
 */
public class UserComponent extends HorizontalLayout {

	private final User user;

	//subkomponentit
	private final Label
			online = new Label(),
			name = new Label(),
			remove = new Label("");


	public UserComponent(User u, Consumer<UserComponent> onRemove){
		this.user = u;
		String color = "\"" + (u.isOnline() ? "green" : "red") + "\"";
		online.setValue("<span color=" + color + ">" + FontAwesome.CIRCLE.getHtml() + "</span>");
		online.setCaption(u.getUsername() + (u.isOnline() ? " on" : " ei ole") + " online.");
		addComponent(online);

		name.setValue(u.getUsername());
		addComponent(name);

		remove.setIcon(FontAwesome.USER_TIMES);
		remove.addContextClickListener(click -> onRemove.accept(UserComponent.this));
		addComponent(remove);
	}

	public User getUser(){
		return user;
	}

}
