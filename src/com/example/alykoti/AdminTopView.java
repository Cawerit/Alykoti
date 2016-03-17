package com.example.alykoti;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;

public class AdminTopView extends HorizontalLayout implements View {

    public AdminTopView() {

    	setSizeFull();
		Notification.show("Welcome!");
        Button homes = new Button("Homes");
        Button users = new Button("Users");

        homes.setWidth("50%");
        users.setWidth("50%");
        homes.setHeight("150px");
        users.setHeight("150px");
       	addComponent(homes);
       	addComponent(users);
        
        setComponentAlignment(homes, Alignment.MIDDLE_CENTER);
        setComponentAlignment(users, Alignment.MIDDLE_CENTER);

        homes.addClickListener(click -> AlykotiUI.navigator.navigateTo(AlykotiUI.HOMES));
        users.addClickListener(click -> AlykotiUI.navigator.navigateTo(AlykotiUI.USERS));
    }
    
	@Override
	public void enter(ViewChangeEvent event) {

	}

}
