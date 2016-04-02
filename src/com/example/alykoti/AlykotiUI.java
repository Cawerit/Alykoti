package com.example.alykoti;


import javax.servlet.annotation.WebServlet;

import com.example.alykoti.models.User;
import com.example.alykoti.services.AuthService;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

import java.sql.SQLException;

@PreserveOnRefresh
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
	public static class Servlet extends VaadinServlet implements SessionDestroyListener {

		@Override
		public void sessionDestroy(SessionDestroyEvent event) {
			//Käyttäjän kirjautuessa ulos, merkataan tämä myös tietokantaan
			//jotta muut käyttäjät näkevät
			User u = AuthService.getInstance().getCurrentUser();
			if(u != null){
				System.out.println("Kirjataan käyttäjä " + u.getUsername() + " ulos");
				//Tehtävä voidaan hoitaa toisessa säikeessä koska se ei ole niin kiireellinen
				new Thread(() -> {
					try {
						u.setOnline(false);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}).start();
			}
		}
		
	}

	@Override
	protected void init(VaadinRequest request) {
		
		NAVIGATOR = new Navigator(this, this);
		NAVIGATOR.addView("", new LoginView());
		//NAVIGATOR.addView(ADMINTOP, new AdminTopView());
		NAVIGATOR.addView(USERVIEW, new UserView());
		NAVIGATOR.addView(ROOMVIEW, new RoomView());
		NAVIGATOR.addView(HOME_VIEW, new HomeView());
		NAVIGATOR.addView(ADMIN_DASHBOARD_VIEW, AdminDashboardView.class);

		NAVIGATOR.setErrorView(new ErrorView());

		setPollInterval(1000);
		
		setContent(new LoginView());
		
		}
}


