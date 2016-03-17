package com.example.alykoti;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class UsersView extends VerticalLayout implements View{  

	public UsersView() {
		setSizeFull(); 
		Button back = new Button("back");
		back.addClickListener(click -> AlykotiUI.navigator.navigateTo("adminTop"));
		addComponent(back);
		addComponent(new Label("Käyttäjälista"));
		
		setComponentAlignment(back, Alignment.TOP_LEFT);
		
    }
    
	@Override
	public void enter(ViewChangeEvent event) {

	}
}


