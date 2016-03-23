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

	static Navigator NAVIGATOR;
	//tyyppiturvallisuuden takia:
	protected static final String ADMINTOP = "adminTop";
	protected static final String USERVIEW = "user";
	
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
		NAVIGATOR.setErrorView(new ErrorView());
		
		
		setContent(new LoginView());

		
		}
	
	
	
  
    
}


