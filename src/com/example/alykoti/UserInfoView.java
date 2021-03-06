package com.example.alykoti;

import java.sql.SQLException;

import com.example.alykoti.models.User;
import com.example.alykoti.services.AuthService;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class UserInfoView extends AppView {
	Panel userPanel = new Panel();
	VerticalLayout panelContent = new VerticalLayout();
	TextField username = new TextField();
	PasswordField old = new PasswordField();
	PasswordField pass1 = new PasswordField();
	PasswordField pass2 = new PasswordField();
	User user = new User();
	Button save = new Button("Save settings");
	
	/**
	 * Create UserInfoView.
	 */
	public UserInfoView() {
		super(AuthService.Role.USER);
		username.setCaption("Vaihda käyttäjänimi");
		old.setCaption("Vanha salasana");
		pass1.setCaption("Uusi salasana");
		pass2.setCaption("Toista salasana");
	
		userPanel.setSizeUndefined();
		panelContent.setSizeFull();
		panelContent.addComponent(username);
		panelContent.addComponent(old);
		panelContent.addComponent(pass1);
		panelContent.addComponent(pass2);
		panelContent.addComponent(save);
		save.setClickShortcut(KeyCode.ENTER);
		
		userPanel.setContent(panelContent);
		addComponent(userPanel);
		panelContent.setComponentAlignment(username, Alignment.TOP_CENTER);
		panelContent.setComponentAlignment(old, Alignment.TOP_CENTER);
		panelContent.setComponentAlignment(pass1, Alignment.TOP_CENTER);
		panelContent.setComponentAlignment(pass2, Alignment.TOP_CENTER);
		setComponentAlignment(userPanel, Alignment.MIDDLE_CENTER);
			
	}
	
	@Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        super.enter(event);
		user.setId(Integer.parseInt(event.getParameters()));
		try {
			user.pull();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		userPanel.setCaption(user.getUsername());
		username.setValue(user.getUsername());
		old.clear();
		pass1.clear();
		pass2.clear();
		if(save.getListeners(ClickEvent.class).size() == 0) {
			save.addClickListener(click -> {
				update(user, username.getValue(), old.getValue(), pass1.getValue(), pass2.getValue());
			});
		}
	}
	
	/**
	 * Update the user's username and password, if fields are filled correctly
	 * @param id 
	 * @param username 
	 * @param password
	 * @param password2
	 */
	private void update(User user, String username, String oldPassword, String password, String password2) {
		try {
			AuthService instance = AuthService.getInstance();
			//Check fields with AuthService.login
			if(username != null && password.equals(password2) && instance.login(user.getUsername(), oldPassword, this.getUI()).equals(user)) {
				instance.updateUser(user.getId(), username, password);	
				Notification.show("Username and/or password updated");
			} else {
				Notification.show("Some fields filled incorrectly", Notification.Type.WARNING_MESSAGE);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Notification.show("Error with connection", Notification.Type.WARNING_MESSAGE);
		//If AuthService.login returns null:
		} catch(NullPointerException e) {
			Notification.show("Wrong password", Notification.Type.WARNING_MESSAGE);
		}
	}
}
