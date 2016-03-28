package com.example.alykoti;

import com.example.alykoti.models.Home;
import com.example.alykoti.services.AuthService;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.TreeTable;

import java.sql.SQLException;

public class HomeView extends AppView {

    public HomeView(){
        super(AuthService.Role.USER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event){
        super.enter(event);
        String params = event.getParameters();
        Home home = null;
        if(params != null && params.length() >= 1){
            try {
                home = Home.get(Integer.parseInt(params));
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        }
        if(home != null){
            TreeTable treeTable = new TreeTable("Kodit");
            treeTable.addContainerProperty("Kodit", String.class, null);
            treeTable.addContainerProperty("Tilanne", String.class, null);
            treeTable.addContainerProperty("Käyttäjät", String.class, null);

            treeTable.addItem(new Object[]{home.getName(), null, null}, 0);
            System.out.println("Koti "+ home.getName());
            addComponent(treeTable);
        }
    }

}
