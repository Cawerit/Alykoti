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
        this.currentUser = AuthService.getInstance().getCurrentUser(this.getUI());
        if(currentUser == null || !hasRights(currentUser.getRole())){
            //Jos käyttäjä ei ole kirjautunut sisään tai hänellä ei ole oikeuksia tarkastella tätä näkymää, siirrytään kirjautumissivulle
            AlykotiUI.getCurrent().getNavigator().navigateTo("");
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
            	AlykotiUI.getCurrent().getNavigator().navigateTo(AlykotiUI.USERINFO + "/" + currentUser.getId());
            });
            settings.addItem("Kirjaudu ulos", FontAwesome.SIGN_OUT, click -> {
                AuthService.getInstance().logout(this.getUI());
                AlykotiUI.getCurrent().getNavigator().navigateTo("");
            });
            //Muutetaan navbarin käyttäjänhallinta
            navBar.addComponent(userSettings);
            navBar.setComponentAlignment(userSettings, Alignment.MIDDLE_RIGHT);
        }
    }


    private boolean hasRights(AuthService.Role currentUserRole){
        if(currentUserRole == null) return false;
        if(currentUserRole.equals(AuthService.Role.ADMIN)) return true;//Adminilla on pääsy kaikkiin sivuihin
        //Tavallinen käyttäjä pääsee näkymään jos niin on erikseen määritetty
        return accessibleForRole == null || accessibleForRole.equals(AuthService.Role.USER);
    }

}

