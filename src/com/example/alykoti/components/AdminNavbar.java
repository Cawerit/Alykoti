package com.example.alykoti.components;


import com.example.alykoti.AlykotiUI;
import com.example.alykoti.commands.AddHomeCommand;
import com.example.alykoti.commands.AddUserCommand;
import com.example.alykoti.models.Home;
import com.example.alykoti.models.User;
import com.example.alykoti.services.AuthService;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

import java.awt.*;
import java.sql.SQLException;

public class AdminNavbar extends HorizontalLayout {

    public final MenuItem homes, users;

    public AdminNavbar(){
        super();
        setWidth("100%");
        MenuBar menuBar = new MenuBar();

        homes = menuBar.addItem("Homes", FontAwesome.HOME, null);
        homes.setDescription("Manage homes");
        homes.addItem("Add home", FontAwesome.PLUS, new AddHomeCommand(this::addHomeToList));

        users = menuBar.addItem("Users", FontAwesome.USERS, null);
        users.setDescription("Manage users");
        users.addItem("Add user", FontAwesome.PLUS, new AddUserCommand(this::addUserToList));

        try {
            new User().query().forEach(this::addUserToList);
            new Home().query().forEach(this::addHomeToList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addComponent(menuBar);
    }

    //Menu click handlers
    private void addUserToList(User u){
        users.addItem(u.getUsername(), null, (MenuItem selectedItem) -> {
        	AlykotiUI.getCurrent().getNavigator().navigateTo(AlykotiUI.USERINFO + "/" + u.getId());
        });
    }
    private void addHomeToList(Home h) { homes.addItem(h.getName(), null, (MenuItem selectedItem) -> {
        AlykotiUI.getCurrent().getNavigator().navigateTo(AlykotiUI.HOME_VIEW + "/" + h.getId());
    }); }

}
