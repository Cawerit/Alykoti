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
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = AlykotiUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
	
		navigator = new Navigator(this, this);
		navigator.addView("", new LoginView());
		navigator.addView("main", new MainView());
		setContent(new LoginView());
		}
	
    static public class LoginView extends VerticalLayout implements View {
        public static final String NAME = "";
        
        public LoginView() {
            TextField username = new TextField();
            PasswordField password = new PasswordField();
            Button loginButton = new Button("login");
            username.setValue("username");
            password.setValue("username");

            loginButton.addClickListener(click -> navigator.navigateTo("main"));
            
            Notification.show("Heippahei");
            addComponent(username);
            addComponent(password);
            addComponent(loginButton);
        }

		@Override
		public void enter(ViewChangeEvent event) {
			
		}        
    }
    static public class MainView extends VerticalLayout implements View{
    	public MainView(){ 
    		addComponent(new Label("NONII"));
    	}

		@Override
		public void enter(ViewChangeEvent event) {
			System.out.println("Nyt kutsuttiin MainView.enter()");
			
		}
    }
    
}