package com.example.alykoti;

import com.example.alykoti.components.AdminNavbar;
import com.example.alykoti.models.User;
import com.example.alykoti.services.AuthService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

/**
 * Yläluokka kaikille muille näkymille paitsi LoginViewille
 */
public class AppView extends VerticalLayout implements View {

    public final HorizontalLayout navBar;

    Label userLabel = new Label();

    public AppView(){
        this.navBar = new AdminNavbar();
        Button logout = new Button("Logout");
        logout.setIcon(FontAwesome.BACKWARD);
        logout.addClickListener(click -> AlykotiUI.NAVIGATOR.navigateTo(""));
        navBar.addComponent(logout);
        navBar.setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);
        addComponent(navBar);
        addComponent(userLabel);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        User current = AuthService.getInstance().getCurrentUser();
        if(current != null){
            userLabel.setValue(current.getUsername());
        }
    }

}

