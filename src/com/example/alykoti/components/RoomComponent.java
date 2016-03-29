package com.example.alykoti.components;


import com.example.alykoti.models.Room;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import java.sql.SQLException;
import java.util.function.Consumer;

public class RoomComponent extends VerticalLayout {

	public RoomComponent(@NotNull Room room, @Nullable Consumer<Room> onSave){
		super();

		TextField name = new TextField("Nimi");
		if(room.getName() != null) name.setValue(room.getName());

		boolean newItem = room.getId() == null;

		Button save = new Button("Tallenna", FontAwesome.SAVE);
		save.addClickListener(click -> {
			room.setName(name.getValue());
			try {
				if(newItem) {
					room.create();
				} else {
					room.update();
				}
				if(onSave != null){
					onSave.accept(room);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});

		addComponent(name);
		addComponent(save);
		setWidth("50%");
	}

	public RoomComponent(@NotNull Room room){
		this(room, null);
	}

}
