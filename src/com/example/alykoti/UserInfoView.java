package com.example.alykoti;

import java.sql.SQLException;

import com.example.alykoti.models.User;
import com.example.alykoti.services.AuthService;
import com.example.alykoti.services.AuthService.Role;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
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
	HorizontalLayout buttons = new HorizontalLayout();
	Button save = new Button("Save settings");
	Button views = new Button("Manage user's views");
	
	public UserInfoView() {
		//TODO: Kayttajan roolin kysely:
		super(AuthService.Role.ADMIN);
		username.setCaption("Change username");
		old.setCaption("Old password");
		pass1.setCaption("New password");
		pass2.setCaption("Repeat password");
	
		userPanel.setSizeUndefined();
		panelContent.setSizeFull();
		panelContent.addComponent(username);
		panelContent.addComponent(old);
		panelContent.addComponent(pass1);
		panelContent.addComponent(pass2);
		panelContent.addComponent(buttons);
		buttons.addComponent(save);
		buttons.addComponent(views);
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
		
		if(save.getListeners(ClickEvent.class).size() == 0) {
			save.addClickListener(click -> {
				update(user.getId(), username.getValue(), pass1.getValue());
			});
		}
	}
	
	private void update(int id, String username, String password) {
		try {
			AuthService.getInstance().updateUser(id, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
