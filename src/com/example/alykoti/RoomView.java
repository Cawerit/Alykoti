package com.example.alykoti;

import com.example.alykoti.models.SimpleItem;
import com.vaadin.client.ui.Icon;
import com.vaadin.data.Item;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class RoomView extends VerticalLayout implements View {
	HorizontalLayout bar;
	VerticalLayout content;
	Button prev;
	Button next;
	Button back;
	Table roomTable;
	
	public RoomView() {
		
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
		
		next = new Button("Next room");
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
		
		content = new VerticalLayout();
		content.setSizeFull();
		addComponent(content);
		
		roomTable = new Table("Roomnumber");
		roomTable.addContainerProperty("Item", String.class, null);
		roomTable.addContainerProperty("Status", Button.class, null);
		roomTable.setPageLength(roomTable.size());
		content.addComponent(roomTable);
		content.setComponentAlignment(roomTable, Alignment.MIDDLE_CENTER);
		
		SimpleItem test = new SimpleItem("Test1", 1, FontAwesome.TOGGLE_ON, FontAwesome.TOGGLE_OFF);
		SimpleItem test2 = new SimpleItem("Test2", 2, FontAwesome.PLAY, FontAwesome.PAUSE);
		addToTable(test);
		addToTable(test2);
	}
	
	//kutsutaan listalla itemeita tai haetaan huoneen itemit tietokannasta
	private void addToTable(SimpleItem test){
		Object newItem = roomTable.addItem();
		Item tableRow = roomTable.getItem(newItem);
		Button statusButton = new Button();
		statusButton.setIcon(test.getIcon());
		statusButton.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				test.changeValue();
				statusButton.setIcon(test.getIcon()); //olis kiva jos tahan sais laitettua valueChangeListenerin tjsp
			}
			
		});
		tableRow.getItemProperty("Item").setValue(test.getName());
		tableRow.getItemProperty("Status").setValue(statusButton);
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
