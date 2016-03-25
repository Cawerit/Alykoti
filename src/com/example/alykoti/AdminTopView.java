package com.example.alykoti;

import com.example.alykoti.models.User;
import com.example.alykoti.services.AuthService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import java.sql.SQLException;

import com.example.alykoti.models.Home;

public class AdminTopView extends VerticalLayout implements View {
	HorizontalLayout bar;
	MenuBar menubar;
	VerticalLayout content;
	MenuItem homes;
	MenuItem users;
	Button logout;
	
	public AdminTopView() {
		bar = new HorizontalLayout();
		bar.setWidth("100%");
		addComponent(bar);
		menubar = new MenuBar();

		homes = menubar.addItem("Homes", null, null);
       	homes.setDescription("Manage homes");
       	homes.setIcon(FontAwesome.HOME);
       	homes.addItem("Add home", FontAwesome.PLUS, addHome());
       	//TODO: Hae tietokannasta kodit ja lisaa ne menuun
       	
		users = menubar.addItem("Users", null, null);
       	users.setDescription("Manage users");
       	users.setIcon(FontAwesome.USER);
       	users.addItem("Add user", FontAwesome.PLUS, addUser());
       	//TODO: Hae tietokannasta kayttajat ja lisaa ne menuun
       	
		bar.addComponent(menubar);
		
		logout = new Button("Logout");
		logout.setIcon(FontAwesome.BACKWARD);
        logout.addClickListener(click -> AlykotiUI.NAVIGATOR.navigateTo(""));
        bar.addComponent(logout);
        bar.setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);
        
        //Container, jonka sisaan avautuu kodit ja kayttajat
		content = new VerticalLayout();
		content.setSizeFull();
		addComponent(content);
		setComponentAlignment(content, Alignment.MIDDLE_CENTER);
				
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
		        subContent.addComponent(housename);
		        Button add = new Button("Add");
		        add.addClickListener(new ClickListener() {
					
		        	@Override
					public void buttonClick(ClickEvent event) {
		        		Home home = new Home(housename.getValue());
						homes.addItem(home.getName(), null, new MenuBar.Command() {
							//lisataan menun kotinappulaan komento avata kotinakyma
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
		        TextField password = new TextField("Password");
		        CheckBox setAsAdmin = new CheckBox("Set as administrator");
		        subContent.addComponent(username);
		        subContent.addComponent(password);
		        subContent.addComponent(setAsAdmin);
		        Button add = new Button("Add");
		        add.addClickListener(new ClickListener() {
					
		        	@Override
					public void buttonClick(ClickEvent event) {

						User user = null;
						try {
							String usr = username.getValue();
							String pwd = password.getValue();
							AuthService.Role role; 
							if(setAsAdmin.getValue()) role = AuthService.Role.ADMIN;
							else role = AuthService.Role.USER;
							//Signup
							user = AuthService.getInstance().signup(usr, pwd, role);
						} catch (SQLException e) {
							e.printStackTrace();
						}

						if(user != null){
							users.addItem(user.getUsername(), null, new MenuBar.Command() {
								//lisataan menun kayttajanappulaan komento avata kayttajanakyma
								@Override
								public void menuSelected(MenuItem selectedItem) {
									if (content.getComponentCount() > 0) content.removeAllComponents();
									viewUser(selectedItem.getText());
								} });
							subWindow.close();
						}
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
	
	//Avataan kodin tiedot. 
	//TODO: Pitaisi hakea tietokannasta kodin nimea (id:ta) vastaava koti ja sen esineet
	//TODO: Tama kotinakyma olisi ehka parempi laittaa omaksi luokakseen
	private void viewHome(String name) {
		Accordion acc = new Accordion();
		acc.setCaption(name);
		acc.setIcon(FontAwesome.HOME);
		acc.setWidth("40%");
		acc.setHeight("100%");
		
		Layout testTab = new VerticalLayout();
		testTab.setCaption("room0");
		testTab.addComponent(new Label("valot"));
		testTab.addComponent(new Label("pakastin"));
		acc.addTab(testTab);
		
		acc.addTab(new Label("hello!")).setCaption("room1");
		acc.addTab(new Label("hi!")).setCaption("room2");

		Button roomButton = new Button("Open Room View");
		roomButton.setIcon(FontAwesome.EYE);
		roomButton.addClickListener(new ClickListener(){
			@Override
			public void buttonClick(ClickEvent event) {
				//jokaisesta kodista paastava RoomViewiin
				//TODO: uri-fragmentilla(?) oikeaan taloon
				AlykotiUI.NAVIGATOR.navigateTo(AlykotiUI.ROOMVIEW); 
			}
		});
		
		
		content.addComponent(acc);
		content.addComponent(roomButton);
		content.setComponentAlignment(acc, Alignment.MIDDLE_CENTER);
		content.setComponentAlignment(roomButton, Alignment.TOP_CENTER);

	}

	//Avataan kayttajan tiedot. Pitaisi hakea muistista kayttajan nimea vastaava olio.
	//TODO: Tastakin oma luokkansa?
	private void viewUser(String name) {
		Panel userPanel = new Panel(name);
		userPanel.setSizeUndefined();
		userPanel.setIcon(FontAwesome.USER);
		content.addComponent(userPanel);
		
		Button addView = new Button("Add view");
       	addView.setIcon(FontAwesome.PLUS);
		addView.addClickListener(new ClickListener() {	
			
			@Override
			public void buttonClick(ClickEvent event) {
				Window subWindow = new Window("Add view");
		        VerticalLayout subContent = new VerticalLayout();
		        subContent.setMargin(true);
		        subWindow.setContent(subContent);
		        
		        //liita kayttaja kotiin ja lisaa kotiin tavaroita
		        ListSelect homesList = new ListSelect("Homes");
		        homesList.addItems("Here be homes", "Moar Homes");
		        homesList.setNullSelectionAllowed(false);
		        homesList.setRows(3);
		        
		        CheckBox checkbox1 = new CheckBox("Add item to view");
		        
		        Button add = new Button("Add");
		        add.addClickListener(new ClickListener() {
		        	@Override
					public void buttonClick(ClickEvent event) {
						//lisaa kayttajalle nakyma
						subWindow.close();		
					}
		        });
		        subContent.addComponent(add);
		        subContent.addComponent(homesList);
		        subContent.addComponent(checkbox1);
		        
		        subWindow.center();
		        UI.getCurrent().addWindow(subWindow);
			}
		});
		
		VerticalLayout panelContent = new VerticalLayout();
		panelContent.addComponent(addView);
		Label label = new Label("User's current view");
		panelContent.addComponent(label);
		panelContent.setSizeUndefined();
		panelContent.setMargin(true);
		userPanel.setContent(panelContent);
		content.setComponentAlignment(userPanel, Alignment.MIDDLE_CENTER);
	}
	
	
 
	@Override
	public void enter(ViewChangeEvent event) {

	}

}
