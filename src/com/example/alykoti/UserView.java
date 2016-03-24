package com.example.alykoti;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class UserView extends VerticalLayout implements View {
	HorizontalLayout bar;
	
	public UserView() {
		bar = new HorizontalLayout();
		bar.setWidth("100%");
		Button logout = new Button("Logout");
		logout.setIcon(FontAwesome.BACKWARD);
        logout.addClickListener(click -> AlykotiUI.NAVIGATOR.navigateTo(""));
        bar.addComponent(logout);
        addComponent(bar);
        bar.setComponentAlignment(logout, Alignment.TOP_RIGHT);        
        
		Notification.show("Welcome!");

		//etsi kayttajan nakyma ja nayta se
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
