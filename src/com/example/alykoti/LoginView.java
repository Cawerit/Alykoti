package com.example.alykoti;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginView extends VerticalLayout implements View {
    
    public LoginView() {
    	
    	setHeight("100%");
        TextField username = new TextField("Username");
        username.setIcon(FontAwesome.USER);
        
        PasswordField password = new PasswordField("Password");
        password.setIcon(FontAwesome.KEY);
        
        Button loginButton = new Button("login");
        loginButton.setIcon(FontAwesome.CHECK);    
        loginButton.addClickListener(new Button.ClickListener() {
			//Login pitaa hoitaa kunnolla eika nain
        	@Override
			public void buttonClick(ClickEvent event) {
				if (username.getValue().equals("admin")) AlykotiUI.NAVIGATOR.navigateTo(AlykotiUI.ADMINTOP);
				else AlykotiUI.NAVIGATOR.navigateTo(AlykotiUI.USERVIEW);
			}
        	
        });
        
        Panel loginPanel = new Panel("Smarthome 3000");
        loginPanel.setSizeUndefined();
        addComponent(loginPanel);
        
        VerticalLayout panelContent = new VerticalLayout();
        panelContent.setSizeUndefined();
        panelContent.addComponent(username);
        panelContent.addComponent(password);
        panelContent.addComponent(loginButton);
        panelContent.setMargin(true);
        loginPanel.setContent(panelContent);
        setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
      
    }

	@Override
	public void enter(ViewChangeEvent event) {
		
	}        
}

