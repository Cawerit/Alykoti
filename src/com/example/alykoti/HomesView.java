package com.example.alykoti;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class HomesView extends VerticalLayout implements View {
	VerticalLayout menu;
	Panel equalPanel;
	Button back;
        
    public HomesView() {
    	setSizeFull();
    	//menu.addComponent(new Button("Koti1", new ButtonListener("koti1")));
    	
    }
    
	@Override
	public void enter(ViewChangeEvent event) {

	}

	class ButtonListener implements Button.ClickListener {
    	String menuitem;
        public ButtonListener(String menuitem) {
            this.menuitem = menuitem;
        }

        @Override
        public void buttonClick(ClickEvent event) {
            // Navigate to a specific state
        	AlykotiUI.navigator.navigateTo(AlykotiUI.USERS + "/" + menuitem);
        }
    }


}
