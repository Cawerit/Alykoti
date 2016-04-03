package com.example.alykoti;

import com.example.alykoti.components.AdminNavbar;
import com.example.alykoti.models.User;
import com.example.alykoti.services.AuthService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.apache.http.auth.AUTH;

/**
 * Yläluokka kaikille muille näkymille paitsi LoginViewille.
 * Näkymä huolehtii oikean navigaatiopalkin näyttämisestä ja käyttäjän
 * oikeuksien hallinnasta
 */
public class AppView extends VerticalLayout implements View {

   //Käyttäjätaso joka vaaditaan tämä näkymän tarkasteluun
    private final AuthService.Role accessibleForRole;

    private User currentUser;
    //Yläpalkki
    private HorizontalLayout navBar;

    public AppView(AuthService.Role accessibleForRole){
        this.accessibleForRole = accessibleForRole;
        this.navBar = new HorizontalLayout();
        addComponent(navBar);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.currentUser = AuthService.getInstance().getCurrentUser();
        if(currentUser == null){
            AlykotiUI.NAVIGATOR.navigateTo("");
        } else {
            HorizontalLayout newNavBar = currentUser.getRole() == AuthService.Role.ADMIN ? new AdminNavbar() : new HorizontalLayout();
            if(navBar == null){
                addComponent(newNavBar);
            } else {
                replaceComponent(navBar, newNavBar);
            }
            this.navBar = newNavBar;
            MenuBar userSettings = new MenuBar();
            MenuBar.MenuItem settings = userSettings.addItem(currentUser.getUsername(), FontAwesome.USER, null);
            settings.setDescription("Käyttäjän asetukset");
            settings.addItem("Profiili", FontAwesome.GEAR, click -> {
            	AlykotiUI.NAVIGATOR.navigateTo(AlykotiUI.USERINFO + "/" + currentUser.getId());
            });
            settings.addItem("Kirjaudu ulos", FontAwesome.SIGN_OUT, click -> {
                AuthService.getInstance().logout();
                AlykotiUI.NAVIGATOR.navigateTo("");
            });
            //Muutetaan navbarin käyttäjänhallinta
            navBar.addComponent(userSettings);
            navBar.setComponentAlignment(userSettings, Alignment.MIDDLE_RIGHT);
        }
    }

}

