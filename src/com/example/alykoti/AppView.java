package com.example.alykoti;

import com.example.alykoti.components.AdminNavbar;
import com.example.alykoti.models.User;
import com.example.alykoti.services.AuthService;
import com.sun.istack.internal.NotNull;
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
    //Yläpalkin valikko käyttäjän asetuksille
    private MenuBar userSettings;

    public AppView(AuthService.Role accessibleForRole){
        this.accessibleForRole = accessibleForRole;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        User current = AuthService.getInstance().getCurrentUser();
        if(current == null){
            AlykotiUI.NAVIGATOR.navigateTo("");
        } else if(!current.equals(currentUser)){
            changeUser(current);
        }
        currentUser = current;
    }

    /**
     * Päivittää navipalkin kun tarpeen (esim käyttäjän vaihtuessa)
     * @param currentUser Uusi kirjautunut käyttäjä
     */
    private void changeUser(@NotNull User currentUser){
        if(this.currentUser == null || currentUser.getRole() != this.currentUser.getRole()){
            //Käyttäjätaso eri, koko navipalkki pitää vetää uusiksi
            this.currentUser = currentUser;
            HorizontalLayout newNavBar = currentUser.getRole() == AuthService.Role.ADMIN ? new AdminNavbar() : new HorizontalLayout();
            if(navBar == null){
                addComponent(newNavBar);
            } else {
                replaceComponent(navBar, newNavBar);
            }
            this.navBar = newNavBar;
        }
        //Vaikka käyttäjätaso ei vaihtuisi, pitää ainakin käyttäjän omat asetukset vaihtaa
        MenuBar newUserSettingsBar = new MenuBar();
        MenuBar.MenuItem settings = newUserSettingsBar.addItem(currentUser.getUsername(), FontAwesome.USER, null);
        settings.setDescription("Käyttäjän asetukset");
        settings.addItem("Profiili", FontAwesome.GEAR, null);//TODO: Käyttäjän asetussivu
        settings.addItem("Kirjaudu ulos", FontAwesome.SIGN_OUT, click -> {
            AuthService.getInstance().logout();
            AlykotiUI.NAVIGATOR.navigateTo("");
        });
        //Muutetaan navbarin käyttäjänhallinta
        if(userSettings == null){
            navBar.addComponent(newUserSettingsBar);
        } else {
            navBar.replaceComponent(userSettings, newUserSettingsBar);
        }
        this.userSettings = newUserSettingsBar;
        navBar.setComponentAlignment(userSettings, Alignment.MIDDLE_RIGHT);
    }

}

