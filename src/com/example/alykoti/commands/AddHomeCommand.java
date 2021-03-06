package com.example.alykoti.commands;

import com.example.alykoti.models.Home;
import com.vaadin.ui.*;

import java.sql.SQLException;
import java.util.function.Consumer;

public class AddHomeCommand implements MenuBar.Command {

    Consumer<Home> onSave;

    public AddHomeCommand(Consumer<Home> onSave){
        this.onSave = onSave;
    }

    @Override
    public void menuSelected(MenuBar.MenuItem menuItem) {
        Window subWindow = new Window("Lisää koti");
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subWindow.setContent(subContent);
        TextField housename = new TextField("Älykodin nimi");
        subContent.addComponent(housename);
        Button add = new Button("Tallenna");
        add.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                Home home = new Home(housename.getValue());
                try {
                    home.create();
                    onSave.accept(home);
                    subWindow.close();
                } catch (SQLException e) {
                    e.printStackTrace();
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
