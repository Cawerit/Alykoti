package com.example.alykoti;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AdminTopView extends VerticalLayout implements View {
	HorizontalLayout bar;
	MenuBar menubar;
	VerticalLayout content;
	MenuItem homes;
	MenuItem users;
	
	public AdminTopView() {
		//palkki, jonka sisalla on menu ja logout-nappula
		bar = new HorizontalLayout();
		bar.setWidth("100%");
		addComponent(bar);
		menubar = new MenuBar();

		homes = menubar.addItem("Homes", null, null);
       	homes.setDescription("Manage homes");
       	homes.setIcon(FontAwesome.HOME);
       	homes.addItem("Add home", FontAwesome.PLUS, addHome());
       	
		users = menubar.addItem("Users", null, null);
       	users.setDescription("Manage users");
       	users.setIcon(FontAwesome.USER);
       	users.addItem("Add user", FontAwesome.PLUS, addUser());
       	//Pitäisi lukea tallennentut käyttäjät ja kodit ja lisätä ne menuun
       	
		bar.addComponent(menubar);
		
		Button logout = new Button("Logout");
		logout.setIcon(FontAwesome.BACKWARD);
        logout.addClickListener(click -> AlykotiUI.NAVIGATOR.navigateTo(""));
        bar.addComponent(logout);
        bar.setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);
        
        //Container, jonka sisaan avautuu kodit ja kayttajat
		content = new VerticalLayout();
		content.setSizeFull();

		addComponent(content);
				
	}
	
	//Uuden kodin luominen ja lisaaminen menuun
	public MenuBar.Command addHome() {
		return new MenuBar.Command() {			
			public void menuSelected(MenuItem selectedItem) {				
				Window subWindow = new Window("Add home");
		        VerticalLayout subContent = new VerticalLayout();
		        subContent.setMargin(true);
		        subWindow.setContent(subContent);
		        TextField housename = new TextField("House name");
		        TextField houseid = new TextField("House ID"); // ehka house id:n pitaisi tulla automaattisesti eika nain
		        subContent.addComponent(housename);
		        subContent.addComponent(houseid);
		        Button add = new Button("Add");
		        add.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						Home home = new Home(housename.getValue(), Integer.parseInt(houseid.getValue()));
						homes.addItem(home.getName(), null, new MenuBar.Command() {
							@Override
							public void menuSelected(MenuItem selectedItem) {
								if (content.getComponentCount() > 0) content.removeAllComponents();
								viewHome(selectedItem.getText());
							} });
						subWindow.close();
					}
		        	
		        });
		        subContent.addComponent(add);
		        // Center it in the browser window
		        subWindow.center();
		        // Open it in the UI 
		        UI.getCurrent().addWindow(subWindow);
			}
		};
	}
	
	//Uuden kayttajan luominen ja lisaaminen menuun 
	public MenuBar.Command addUser() {
		return new MenuBar.Command() {			
			public void menuSelected(MenuItem selectedItem) {
				Window subWindow = new Window("Add user");
		        VerticalLayout subContent = new VerticalLayout();
		        subContent.setMargin(true);
		        subWindow.setContent(subContent);
		        TextField username = new TextField("Username");
		        subContent.addComponent(username);
		        Button add = new Button("Add");
		        add.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						User user = new User(username.getValue());
						users.addItem(user.getName(), null, new MenuBar.Command() {
							@Override
							public void menuSelected(MenuItem selectedItem) {
								if (content.getComponentCount() > 0) content.removeAllComponents();
								viewUser(selectedItem.getText());
							} });
						subWindow.close();		
					}
		        });
		        subContent.addComponent(add);
		        // Center it in the browser window
		        subWindow.center();
		        // Open it in the UI 
		        UI.getCurrent().addWindow(subWindow);
			}
		};
	}
	
	//Avataan kodin tiedot. Pitaisi hakea muistista kodin nimea (mieluummin id:ta) vastaava koti-olio
	public void viewHome(String name) {
		Accordion acc = new Accordion();
		acc.setCaption(name);
		acc.setIcon(FontAwesome.HOME);
		acc.setWidth("50%");
		acc.addTab(new Label("hello")).setCaption("hello");
		acc.addTab(new Label("how")).setCaption("hello");
		content.addComponent(acc);
		content.setComponentAlignment(acc, Alignment.MIDDLE_CENTER);
	}

	//Avataan kayttajan tiedot. Pitaisi hakea muistista kayttajan nimea vastaava olio
	public void viewUser(String name) {
		Accordion acc = new Accordion();
		acc.setCaption(name);
		acc.setIcon(FontAwesome.USER);
		acc.setWidth("50%");
		acc.addTab(new Label("hello")).setCaption("hello");
		acc.addTab(new Label("how")).setCaption("hello");
		content.addComponent(acc);
		content.setComponentAlignment(acc, Alignment.MIDDLE_CENTER);
	}
	
	//Tein tallaiset tahan nyt testitarkoituksiin
	public class Home {
		private String name;
		private int id;
			
		public Home(String name, int id) {
			this.name = name;
			this.id = id;
		}
		
		public String getName(){ return name; }
		public int getId() { return id; }
	}
		
	public class User {
		private String name;
			
		public User(String name) {
			this.name = name;
		}
		
		public String getName(){ return name; }
	}
	
	
 
	@Override
	public void enter(ViewChangeEvent event) {

	}

}
