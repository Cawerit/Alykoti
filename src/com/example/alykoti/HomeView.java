package com.example.alykoti;

import com.example.alykoti.components.RoomComponent;
import com.example.alykoti.models.Home;
import com.example.alykoti.models.HomeTest;
import com.example.alykoti.models.Room;
import com.example.alykoti.services.AuthService;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.TreeTable;

import java.sql.SQLException;
import java.util.List;

public class HomeView extends AppView {

    public final Accordion accordion = new Accordion();

    public HomeView(){
		super(AuthService.Role.USER);
		addComponent(accordion);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event){
        super.enter(event);
        String params = event.getParameters();//Kodin id saadaan url parametreista
        Home home = null;
		List<Room> rooms = null;
        if(params != null && params.length() >= 1){
            try {
				Integer homeId = Integer.parseInt(params);
                home = Home.get(homeId);
				rooms = Room.query(homeId);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        }

		HomeTest test = new HomeTest();

		try {
			long start = System.currentTimeMillis();
			List<Home> homes2 = Home.query();
			System.out.println("1. Got " + homes2.size() + " in " + (System.currentTimeMillis() - start));

			start = System.currentTimeMillis();
			List<HomeTest> homes = test.query();
			System.out.println("2. Got " + homes.size() + " in " + (System.currentTimeMillis() - start));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if(home != null){
//			accordion.removeAllComponents();//Remove old table rows to replace with new stuff
//            if(rooms != null) {
//				for (Room r : rooms)
//					accordion.addComponent(new RoomComponent(r));
//			}
//			accordion.addComponent(new RoomComponent(null));
        }
    }

}
