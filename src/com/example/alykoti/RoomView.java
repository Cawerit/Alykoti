package com.example.alykoti;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class RoomView extends VerticalLayout implements View {
	HorizontalLayout bar;
	Button prev;
	Button next;
	Button back;
	
	public RoomView() {
		setSizeFull();
		
		bar = new HorizontalLayout();
		bar.setSpacing(false);
		bar.setWidth("100%");
		addComponent(bar);
		
		HorizontalLayout left = new HorizontalLayout();
		
		prev = new Button("Previous room");
		prev.setSizeUndefined();
		prev.setIcon(FontAwesome.CHEVRON_CIRCLE_LEFT);
		prev.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				//siirry edelliseen huoneeseen
			}
		});
		
		next = new Button("Next room!");
		next.setIcon(FontAwesome.CHEVRON_CIRCLE_RIGHT);
		next.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				//siirry seuraavaan huoneeseen
			}
		});
		
		back = new Button("Back");
		back.setIcon(FontAwesome.BACKWARD);
		back.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				//back:in paamaara pitaisi maaraytya sen mukaan, onko kayttaja admin vai ei
				AlykotiUI.NAVIGATOR.navigateTo(AlykotiUI.ADMINTOP);
			}
		});
		
		left.addComponent(prev);
		left.addComponent(next);
		bar.addComponent(left);
		bar.addComponent(back);
		
		bar.setComponentAlignment(back, Alignment.TOP_RIGHT);
		bar.setComponentAlignment(left, Alignment.TOP_LEFT);
		
		
	}
	

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
