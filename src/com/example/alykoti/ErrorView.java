package com.example.alykoti;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ErrorView extends VerticalLayout implements View {

	public ErrorView() {
		Button back = new Button("Back");
		back.addClickListener(click ->  AlykotiUI.NAVIGATOR.navigateTo(""));
		Label errorMessage = new Label("Sorry, requested resource not available");
		addComponent(errorMessage);
		addComponent(back);
		
		setComponentAlignment(errorMessage, Alignment.MIDDLE_CENTER);
		//setComponentAlignment(back, Alignment.MIDDLE_CENTER);
	}
			
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
