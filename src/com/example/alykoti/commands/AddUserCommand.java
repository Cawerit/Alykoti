package com.example.alykoti.commands;

import com.example.alykoti.models.User;
import com.example.alykoti.services.AuthService;
import com.vaadin.ui.*;

import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AddUserCommand implements MenuBar.Command {

    private Consumer<User> onSave;

    public AddUserCommand(Consumer<User> onSave){
        this.onSave = onSave;
    }

    @Override
    public void menuSelected(MenuBar.MenuItem menuItem) {
        Window subWindow = new Window("Add user");
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subWindow.setContent(subContent);

        TextField username = new TextField("Username");
        TextField password = new TextField("Password");
        CheckBox setAsAdmin = new CheckBox("Set as administrator");
        subContent.addComponent(username);
        subContent.addComponent(password);
        subContent.addComponent(setAsAdmin);
        Button add = new Button("Add");
        add.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {

                User user = null;
                try {

                    String usr = username.getValue();
                    String pwd = password.getValue();

                    if(User.usernameExists(usr)){
                        Notification.show("Käyttäjänimi on jo varattu");
                        return;
                    }

                    AuthService.Role role = setAsAdmin.getValue() ?
                            AuthService.Role.ADMIN : AuthService.Role.USER;
                    //Signup
                    user = AuthService.getInstance().signup(usr, pwd, role);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if(user != null){
                    onSave.accept(user);
                    subWindow.close();
                }
            }
        });
        subContent.addComponent(add);
        // Center it in the browser window
        subWindow.center();
        // Open it in the UI
        UI.getCurrent().addWindow(subWindow);
    }
}