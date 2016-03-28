package com.example.alykoti.components;

import com.example.alykoti.models.SimpleItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;

/**
 * Widget to represent SimpleItems
 *
 */
public class SimpleWidget extends HorizontalLayout{
	Button statusButton;
	
	/**
	 * Create a button to represent and alter the state of a SimpleItem
	 * @param item the SimpleItem to represent
	 */
	public SimpleWidget(SimpleItem item) {
		statusButton = new Button();
		statusButton.setIcon(item.getIcon());
		statusButton.addClickListener(new ClickListener(){
			@Override
			public void buttonClick(ClickEvent event) {
				item.changeValue();
				statusButton.setIcon(item.getIcon());
			}
			
		});
	addComponent(statusButton);
	}



}
