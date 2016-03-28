package com.example.alykoti;

import com.example.alykoti.models.User;
import com.example.alykoti.services.AuthService;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import java.sql.SQLException;

public class LoginView extends VerticalLayout implements View {
    
    public LoginView() {
    	
    	setHeight("100%");
        TextField username = new TextField("Username");
        username.setIcon(FontAwesome.USER);
        username.focus();

        PasswordField password = new PasswordField("Password");
        password.setIcon(FontAwesome.KEY);
        
        Button loginButton = new Button("login");
        loginButton.setIcon(FontAwesome.CHECK);    
        loginButton.addClickListener(new Button.ClickListener() {
        	@Override
			public void buttonClick(ClickEvent event) {
                User user = null;
                try {
                    user = AuthService.getInstance().login(username.getValue(), password.getValue());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if(user != null){
//                    if(user.getRole() == AuthService.Role.ADMIN) AlykotiUI.NAVIGATOR.navigateTo(AlykotiUI.ADMINTOP);
                    if(user.getRole() == AuthService.Role.ADMIN) AlykotiUI.NAVIGATOR.navigateTo(AlykotiUI.ADMIN_DASHBOARD_VIEW);
                    else AlykotiUI.NAVIGATOR.navigateTo(AlykotiUI.USERVIEW);
                } else {
                    Notification.show("Kayttajatunnus tai salasana vaarin", Notification.Type.WARNING_MESSAGE);
                }
			}
        		
        });
        loginButton.setClickShortcut(KeyCode.ENTER);
        
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

