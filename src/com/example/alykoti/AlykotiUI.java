package com.example.alykoti;


import javax.servlet.annotation.WebServlet;

import com.example.alykoti.models.IUpdatable;
import com.example.alykoti.models.Resource;
import com.example.alykoti.models.User;
import com.example.alykoti.services.AuthService;
import com.example.alykoti.services.ObserverService;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

import java.sql.SQLException;

@SuppressWarnings("serial")
@Theme("alykoti")
public class AlykotiUI extends UI {

	private Navigator navigator;
	//Tähän kerätään muutoksia seuraavien laitteiden lista
	private ObserverService.ObserverCollection observers;

	//tyyppiturvallisuuden takia:
	public static final String
			USERVIEW = "user",
			ROOMVIEW = "room",
			HOME_VIEW = "home",
			ADMIN_DASHBOARD_VIEW = "admin-dashboard";
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = AlykotiUI.class, closeIdleSessions=true)
	public static class Servlet extends VaadinServlet {

		
	}

	@Override
	protected void init(VaadinRequest request) {

		navigator = new Navigator(this, this);
		navigator.addView("", new LoginView());
		//NAVIGATOR.addView(ADMINTOP, new AdminTopView());
		navigator.addView(USERVIEW, new UserView());
		navigator.addView(ROOMVIEW, new RoomView());
		navigator.addView(HOME_VIEW, new HomeView());
		navigator.addView(ADMIN_DASHBOARD_VIEW, AdminDashboardView.class);

		navigator.setErrorView(new ErrorView());

		//Hoidetaan muutosten observointi
		setPollInterval(3000);
		navigator.addViewChangeListener(new UpdateObservers());
		addPollListener(pollEvent -> {
			if(observers != null){
				observers.update();
			}
		});
		
		setContent(new LoginView());
	}

	public void subscribeObserver(IUpdatable r, ObserverService.IObserver observer){
		observers.subscribe(r, observer);
	}

	public Navigator getNavigator(){
		return navigator;
	}

	private class UpdateObservers implements ViewChangeListener {
		@Override
		public boolean beforeViewChange(ViewChangeEvent viewChangeEvent) {
			//Luodaan uusi observerColleciton
			observers = ObserverService.getInstance().createObserverCollection();
			return true;
		}
		@Override
		public void afterViewChange(ViewChangeEvent viewChangeEvent) {}

	}

}


