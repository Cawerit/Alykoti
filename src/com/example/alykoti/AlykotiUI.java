package com.example.alykoti;


import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;


@SuppressWarnings("serial")
@Theme("alykoti")
public class AlykotiUI extends UI {

	static Navigator NAVIGATOR;
	//tyyppiturvallisuuden takia:
	protected static final String ADMINTOP = "adminTop";
	protected static final String USERVIEW = "user";
	protected static final String ROOMVIEW = "room";
	static final String USERGROUPSVIEW = "user-groups";
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = AlykotiUI.class)
	public static class Servlet extends VaadinServlet {
	
		
	}

	@Override
	protected void init(VaadinRequest request) {
		
		NAVIGATOR = new Navigator(this, this);
		NAVIGATOR.addView("", new LoginView());
		NAVIGATOR.addView(ADMINTOP, new AdminTopView());
		NAVIGATOR.addView(USERVIEW, new UserView());
		NAVIGATOR.addView(ROOMVIEW, new RoomView());
		NAVIGATOR.addView(USERGROUPSVIEW, new AppView());
		NAVIGATOR.setErrorView(new ErrorView());
		
		
		setContent(new LoginView());

		
		}
	
	
	
  
    
}


