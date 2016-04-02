package com.example.alykoti.components;

import com.example.alykoti.models.User;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
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
			name = new Label(),
			remove = new Label("");
	private final CustomLayout online = new CustomLayout();


	public UserComponent(User u, Consumer<UserComponent> onRemove){
		super();
		this.user = u;
		setMargin(true);

		String color = u.isOnline() ? "color:green;" : "";
		online.setTemplateContents(
				"<span style=\"" + color + " font-size: 80%;\" >" +
						FontAwesome.CIRCLE.getHtml() +
				"</span>&nbsp;"
		);
		addComponent(online);

		name.setValue(u.getUsername());
		name.setDescription(u.getUsername() + (u.isOnline() ? " on" : " ei ole") + " online.");
		addComponent(name);

		HorizontalLayout removeContainer = new HorizontalLayout();
		removeContainer.setDescription("Estä käyttäjää muokkaamasta laitteen tilaa");
		remove.setIcon(FontAwesome.USER_TIMES);
		removeContainer.addLayoutClickListener(click -> onRemove.accept(UserComponent.this));
		removeContainer.addComponent(remove);
		addComponent(removeContainer);
	}

	public User getUser(){
		return user;
	}

}
