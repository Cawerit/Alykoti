package com.example.alykoti;


import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("alykoti")
public class AlykotiUI extends UI {

	static Navigator navigator;
	//tyyppiturvallisuuden takia:
	protected static final String ADMINTOP = "adminTop";
	protected static final String HOMES = "homes";
	protected static final String USERS = "users";
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = AlykotiUI.class)
	public static class Servlet extends VaadinServlet {
	
	}

	@Override
	protected void init(VaadinRequest request) {
		
		navigator = new Navigator(this, this);
		navigator.addView("", new LoginView());
		navigator.addView(ADMINTOP, new AdminTopView());
		navigator.addView(HOMES, new HomesView(ADMINTOP, HOMES));
		navigator.addView(USERS, new UsersView());
		
		
		
		setContent(new LoginView());

		}
	
  
    
}


