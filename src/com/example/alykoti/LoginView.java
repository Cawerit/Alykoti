package com.example.alykoti;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginView extends VerticalLayout implements View {
    
    public LoginView() {
    	
    	setSizeFull();
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        Button loginButton = new Button("login");
        username.setValue("username");
        password.setValue("username");

        loginButton.addClickListener(click -> AlykotiUI.navigator.navigateTo(AlykotiUI.ADMINTOP));
        
        addComponent(username);
        addComponent(password);
        addComponent(loginButton);
        setComponentAlignment(loginButton, Alignment.MIDDLE_CENTER);
        setComponentAlignment(username, Alignment.MIDDLE_CENTER);
        setComponentAlignment(password, Alignment.MIDDLE_CENTER);
    
    }

	@Override
	public void enter(ViewChangeEvent event) {
		
	}        
}

