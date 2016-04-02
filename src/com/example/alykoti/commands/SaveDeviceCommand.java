package com.example.alykoti.commands;


import com.example.alykoti.models.Device;
import com.example.alykoti.models.devices.DeviceType;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class SaveDeviceCommand implements Button.ClickListener {

	private Integer roomId;
	private Consumer<Device> onSave;

	public SaveDeviceCommand(Integer roomId, Consumer<Device> onSave){
		this.roomId = roomId;
		this.onSave = onSave;
	}

	@Override
	public void buttonClick(Button.ClickEvent clickEvent) {
		//Luodaan vaihtoehto uuden laitteen lsiäämiselle
		Device editDevice = new Device();//Olio jota voidaan käyttää uuden laitteen tallennukseen
		editDevice.setRoom(roomId);
		Window subWindow = new Window("Lisää laite");
		VerticalLayout subContent = new VerticalLayout();
		subContent.setMargin(true);
		subWindow.setContent(subContent);
		TextField deviceName = new TextField("Laitteen nimi");
		subContent.addComponent(deviceName);
		ListSelect deviceTypes = new ListSelect("Laitteen tyyppi");
		for(DeviceType val : DeviceType.values()){
			Object key = deviceTypes.addItem(val);
			deviceTypes.setItemCaption(key, val.toString("fi"));
		}
		deviceTypes.setWidth("100%");
		deviceTypes.setNullSelectionAllowed(false);
		subContent.addComponent(deviceTypes);
		Button save = new Button("Tallenna", FontAwesome.SAVE);
		save.addClickListener(saveClick -> {
			editDevice.setName(deviceName.getValue());
			Object deviceType = deviceTypes.getValue();
			if(deviceType != null){
				editDevice.setType((DeviceType) deviceType);
			} else return;
			try {
				editDevice.create();
				if(onSave != null){
					onSave.accept(editDevice);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				subWindow.close();
			}
		});
		subContent.addComponent(save);
		subWindow.center();
		UI.getCurrent().addWindow(subWindow);
	}
}
