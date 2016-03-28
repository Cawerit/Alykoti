package com.example.alykoti.components;


import com.example.alykoti.models.Room;
import com.sun.istack.internal.NotNull;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class RoomComponent extends VerticalLayout {

	public RoomComponent(@NotNull Room room){
		super();
		addComponent(new Label(room.getName()));
	}

	public RoomComponent(@NotNull String homeId){
		super();
		TextField name = new TextField("Nimi");
		Button save = new Button("Tallenna", FontAwesome.SAVE);
		save.addClickListener(click -> {

		});
	}

}
