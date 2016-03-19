package com.example.alykoti;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public abstract class SideMenuView extends VerticalLayout implements View {
	VerticalLayout menu;
	Button back;
	Panel menuPanel;
	static String parentView;
	static String url;
        
    public SideMenuView(String parentView, String url) {
    	this.parentView = parentView;
    	this.url = url;
    	setSizeFull();
    	VerticalLayout menu = new VerticalLayout();
    	Button back = new Button("back");
    	Panel menuPanel = new Panel();
    	addComponent(back);
    	addComponent(menu);
    	addComponent(menuPanel);
		back.addClickListener(click -> AlykotiUI.navigator.navigateTo(parentView));

    }
    
    public abstract void addMenuItem();
    public abstract void removeMenuItem();

	class ButtonListener implements Button.ClickListener {
    	String menuItem;
        public ButtonListener(String menuItem) {
            this.menuItem = menuItem;
        }

        @Override
        public void buttonClick(ClickEvent event) {
            // Navigate to a specific state
        	AlykotiUI.navigator.navigateTo("AlykotiUI." + url + "/" + menuItem);
        }
    }

	class MenuItemViewer extends VerticalLayout {
		public MenuItemViewer(String menuItem){
			Label testi = new Label("You are viewing " + menuItem);
			addComponent(testi);
			setComponentAlignment(testi, Alignment.MIDDLE_CENTER);
			
		}
	}

	
	@Override
    public void enter(ViewChangeEvent event) {
        if (event.getParameters() == null
            || event.getParameters().isEmpty()) {
        	menuPanel.setContent(new Label("huonosti kavi"));
            return;
        } else
            menuPanel.setContent(new MenuItemViewer(event.getParameters()));
    }
	
}


