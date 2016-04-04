package com.almende.wiekenjij.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.almende.wiekenjij.shared.Auth;
import com.almende.wiekenjij.shared.Person;
import com.almende.wiekenjij.shared.Relation;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.VisualizationUtils;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WieKenJij implements EntryPoint {
	//final DockLayoutPanel dock = new DockLayoutPanel(Unit.PX); 
	final HTML header = new HTML(
			"<div style='color: white;'>" + 
			"<h1>wie ken jij?</h1>" + 
			"<i>je sociale netwerk in kaart</i>" + 
			"</div>");
	final HTML footer = new HTML(
	"<table align='center' width='100%'><tr><td class='footer'>" +
	"<a href='http://wiekenjij.appspot.com' target= '_blank'>" + 
	"http://wiekenjij.appspot.com</a>" +
	"</td><td class='footer'>" +
	"<a href='http://http://code.google.com/webtoolkit/' target='_blank'>" + 
	"<img src='img/gwt-logo.png' class='logo' title='Dit is een Google Web Toolkit applicatie'></a>" +
	"</td><td class='footer'>" +
	"<a href='http://www.buurtlab.nl' target='_blank' title='Dit onderzoeksproject wordt uitgevoerd door Buurtlab'>" + 
	"<img src='img/buurtlab_logo_klein.jpg' class='logo'></a>" +
	"</td><td class='footer'>" +
	"<a href='http://www.almende.com' target='_blank'>" + 
	"<img src='img/almende-logo.png' class='logo' title='Deze applicatie is ontwikkeld door Almende' ></a>" +
	"</td><td class='footer'>" +
    "Achtergrond van " +
		"<a href='http://manicho.deviantart.com/art/Rainbowfest-wall-67367936' target= '_blank'>" + 
		"machino</a>" +
	"</td></tr></table>");
	final VerticalPanel menu = new VerticalPanel();
	
	final ListBox lstTests = new ListBox();    // Available tests on this account
	final Anchor btnViewer= new Anchor("Netwerk bekijken");
	final VerticalPanel panelPersons = new VerticalPanel();
	final String test = "Buurtlab_2011";	// Current test
	final SimplePanel content = new SimplePanel();
	final Button newPerson = new Button("Toevoegen");
	final MultiWordSuggestOracle nameSuggestions = new MultiWordSuggestOracle();
	final SuggestBox findPerson = new SuggestBox(nameSuggestions);
	
	boolean visualisationsLoaded = false;
	
	List<Person> persons = new ArrayList<Person>();
	List<String> names = new ArrayList<String>();
	
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final PersonServiceAsync rpc = GWT.create(PersonService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		 Runnable onLoadCallback = new Runnable() {
	          public void run() {
	        	  visualisationsLoaded = true;
	          }
		 };
		 
        // Load the visualization api, passing the onLoadCallback to be called
        // when loading is done.
        VisualizationUtils.loadVisualizationApi(onLoadCallback);
		
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.addStyleName("vertical");
		HorizontalPanel hPanel = new HorizontalPanel();
		
		// menu
		// menu is invisible until logged in as admin
		menu.setVisible(false);
		
		// header
		header.addStyleName("header");
		vPanel.add(header);
		vPanel.add(hPanel);
		
		// footer
		footer.addStyleName("footer");
		vPanel.add(footer);
		
		final Anchor btnAuth = new Anchor();
		btnAuth.setVisible(false);
		btnAuth.addStyleName("auth");
		rpc.getAuth(new AsyncCallback<Auth>() {
			@Override
			public void onSuccess(Auth auth) {
				if (auth.isUserLoggedIn) {
					btnAuth.setText("" + auth.username + " uitloggen");
					btnAuth.setHref(auth.logoutUrl);

					if (auth.isUserAdmin) {
						menu.setVisible(true);
						updatePersons();
					}
					else {
						Window.alert(
							"Je bent ingelogd als " + auth.username + ", " +
							"maar deze account heeft geen toegang tot deze " +
							"applicatie.\n\n" +
							"Log eerst uit, en log dan opnieuw in met een " +
							"geldige account.");
					}
				}
				else {
					btnAuth.setText("Inloggen");
					btnAuth.setHref(auth.loginUrl);
				}
				btnAuth.setVisible(true);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO nicer error handling
				Window.alert(caught.getMessage());
			}
		});
		vPanel.add(btnAuth);
		
		// menu
		// TODO: load the available tests on this account
		lstTests.addItem(test);
		lstTests.setTitle("Selecteer een onderzoek");
		menu.add(new HTML("<h2>Onderzoek</h2>"));
		menu.add(lstTests);
		menu.add(btnViewer);
		btnViewer.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				displayNetwork();
			}
		});
		menu.add(new HTML("<h2>Deelnemers</h2>"));
		menu.add(newPerson);
		menu.add(new HTML("<br>"));
		newPerson.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				displayPerson(new Person());
			}
		});
		findPerson.setLimit(10);   // Set the limit to 10 suggestions
		findPerson.getTextBox().addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == 13) {
					findPerson(findPerson.getText());
					event.stopPropagation();
				}
			}
		});
		findPerson.addSelectionHandler(
				new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(
					SelectionEvent<SuggestOracle.Suggestion> event) {
				findPerson(findPerson.getText());
			}
		});
		menu.add(findPerson);
		menu.add(panelPersons);
		menu.addStyleName("menu");
		hPanel.add(menu);

		// content
		content.addStyleName("content");
		hPanel.add(content);
				
		RootPanel.get("content").add(vPanel);
	}
	
	private void hidePerson() {
		content.clear();
	}
	
	private void displayPerson(Person person) {
		PersonForm personForm = new PersonForm();
		personForm.setPerson(person);
		personForm.setNameSuggestions(names);
		
		personForm.setSaveCallback(new AsyncCallback<Person>() {
			@Override
			public void onSuccess(Person person) {
				rpc.storePerson(person, new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO nicer error handling
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(String result) {
						hidePerson();
						updatePersons();
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
		personForm.setCancelCallback(new AsyncCallback<Person>() {
			@Override
			public void onSuccess(Person person) {
				hidePerson();
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});		
		personForm.setDeleteCallback(new AsyncCallback<Person>() {
			@Override
			public void onSuccess(Person person) {
				rpc.deletePerson(person, new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO nicer error handling
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(String result) {
						hidePerson();
						updatePersons();
					}});
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
		
		content.setWidget(personForm);
	}
	
	private void displayNetwork() {
		if (!visualisationsLoaded) {
			Window.alert("Geduld alsjeblieft,\n" +
					"ben nog bezig met laden\nvan visualisatie library.\n\n" +
					"Probeer het over een paar seconden nog eens...");
			return;
		}
		
		AsyncCallback<List<Person>> callback = new AsyncCallback<List<Person>>() {
			@Override
			public void onSuccess(List<Person> persons) {
				NetworkViewer network = new NetworkViewer(persons);
				content.setWidget(network);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO nicer error handling
				Window.alert(caught.getMessage());
			}
		};
		
		rpc.getPersons(callback);

	}
	
	private void updatePersons() {
		rpc.getPersons(new AsyncCallback<List<Person> > () {
			@Override
			public void onFailure(Throwable caught) {
				// TODO nicer error handling
				Window.alert(caught.getMessage());
			}
			
			@Override
			public void onSuccess(List<Person> refreshedPersons) {
				persons = refreshedPersons;
				names.clear();
				nameSuggestions.clear();
				
				for (Person person : persons) {
					if (person.name != null && 
							!names.contains(person.name)) {
						names.add(person.name);
					}
				}	

				// in name suggestions, only take the direct names of persons,
				// not of relations
				nameSuggestions.addAll(names);
				
				for (Person person : persons) {
					List<Relation> relations = person.relations;
					if (relations != null) {
						for (Relation relation : relations) {
							if (relation.name != null &&
									!names.contains(relation.name)) {
								names.add(relation.name);
							}					
						}
					}
				}
				
				Collections.sort(names);
				
				displayPersons(persons);
			}
		});		
	}
	
	private void findPerson(String name) {
		for (Person person : persons) {
			if (name.equals(person.name)) {
				displayPerson(person);
				return;
			}
		}
		
		Window.alert("Deelnemer '" + name + "' niet gevonden.");
	}

	private void displayPersons(List<Person> persons) {
		panelPersons.clear();
		
		List<String> orderedNames = new ArrayList<String>();
		for (Person person : persons) {
			orderedNames.add(person.name);
		}
		Collections.sort(orderedNames);
		
		for (final String name : orderedNames) {
			Anchor a = new Anchor(name);
			a.setStyleName("listitem");
			
			a.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					rpc.findPerson(name, new AsyncCallback<Person>() {
						@Override
						public void onFailure(Throwable caught) {
							// TODO nicer error handling
							Window.alert(caught.getMessage());
						}

						@Override
						public void onSuccess(Person person) {
							displayPerson(person);
						}
					});
				}
			});
			
			panelPersons.add(a);
		}
	}
}
