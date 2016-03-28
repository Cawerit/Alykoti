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

	public static Navigator NAVIGATOR;
	//tyyppiturvallisuuden takia:
	public static final String
			ADMINTOP = "adminTop",
			USERVIEW = "user",
			ROOMVIEW = "room",
			HOME_VIEW = "home",
			ADMIN_DASHBOARD_VIEW = "admin-dashboard";
	
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
		NAVIGATOR.addView(HOME_VIEW, new HomeView());
		NAVIGATOR.addView(ADMIN_DASHBOARD_VIEW, AdminDashboardView.class);

		NAVIGATOR.setErrorView(new ErrorView());

		setPollInterval(1000);
		
		setContent(new LoginView());

		
		}
}


