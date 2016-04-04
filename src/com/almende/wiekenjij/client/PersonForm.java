package com.almende.wiekenjij.client;

import java.util.List;

import com.almende.wiekenjij.shared.Person;
import com.almende.wiekenjij.shared.Relation;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PersonForm extends Composite  {
	Person person = new Person();
	
	Button save = new Button("Opslaan");
	Button cancel = new Button("Annuleren");
	Button delete = new Button("Verwijderen");
	Button addRelation = new Button("Toevoegen");
	
	TextBox name = new TextBox();
	TextBox age = new TextBox();
	ListBox sex = new ListBox();
	FlexTable rel = new FlexTable();
	
	// suggestions for names and domains
	MultiWordSuggestOracle names = new MultiWordSuggestOracle();
	MultiWordSuggestOracle domains = new MultiWordSuggestOracle();
	
	final String MALE = "Jongen";
	final String FEMALE = "Meisje";
	
	// callback methods
	AsyncCallback<Person> onSave = null;
	AsyncCallback<Person> onCancel = null;
	AsyncCallback<Person> onDelete = null;
	
	PersonForm () {
		VerticalPanel panel = new VerticalPanel();		

		// personal data
		panel.add(new HTML("<h2>Wie ben jij?</h2>"));
		sex.addItem(MALE);
		sex.addItem(FEMALE);
		Grid grid = new Grid(4, 2);
		grid.setWidget(0, 0, new Label("Naam"));
		grid.setWidget(0, 1, name);
		grid.setWidget(1, 0, new Label("Leeftijd"));
		grid.setWidget(1, 1, age);
		grid.setWidget(2, 0, new Label("Jongen/Meisje"));
		grid.setWidget(2, 1, sex);
		panel.add(grid);
		name.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				person.name = name.getText();
			}
		});
		age.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				person.age = Integer.parseInt(age.getText());
			}
		});
		sex.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				person.sex = (name.getText() == MALE) ? 
						Person.SEX.MALE : Person.SEX.FEMALE;
			}
		});

		// relations
		addRelation.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				person.relations.add(new Relation());
				displayRelations();
			}
		});
		panel.add(new HTML("<h2>Wie ken jij?</h2>"));
		panel.add(rel);
		panel.add(new HTML("<br>"));
		panel.add(addRelation);
		displayRelations();
		
		// button menu
		panel.add(new HTML("<h2>Klaar?</h2>"));
		Grid menu = new Grid(1, 3);
		menu.setWidget(0, 0, save);
		menu.setWidget(0, 1, cancel);
		menu.setWidget(0, 2, delete);
		panel.add(menu);
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (onSave != null) {
					onSave.onSuccess(person);
				}
			}
		});
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (onCancel != null) {
					onCancel.onSuccess(person);
				}
			}
		});
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean sure = Window.confirm("Weet je zeker dat je " + 
						person.name + " wilt verwijderen?");
				if (sure && onDelete != null) {
					onDelete.onSuccess(person);
				}
			}
		});
		
		initWidget(panel);
	}
	
	private void displayRelations() {
		rel.clear();

		if (person.relations.size() > 0) {
			rel.setWidget(0, 0, new Label("Naam"));
			rel.setWidget(0, 1, new Label("Ken ik van"));
			rel.setWidget(0, 2, new Label("Spreek ik"));
			
			int row = 1;
			for (final Relation relation : person.relations) {
				final SuggestBox name = new SuggestBox(names);
		        name.setLimit(10);   // Set the limit to 10 suggestions 
				name.setText(relation.name);
				rel.setWidget(row, 0, name );
				name.getTextBox().addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						relation.name = name.getText();
					}
				});
				name.addSelectionHandler(
						new SelectionHandler<SuggestOracle.Suggestion>() {
					@Override
					public void onSelection(
							SelectionEvent<SuggestOracle.Suggestion> event) {
						relation.name = name.getText();
					}
				});
				
				/* TODO: cleanup
				final SuggestBox domain = new SuggestBox(domains);
				domain.setText(relation.domain);
				domain.setLimit(10);   // Set the limit to 10 suggestions 
				rel.setWidget(row, 1, domain );
				domain.getTextBox().addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						relation.domain = domain.getText();
					}
				});				
				domain.addSelectionHandler(
						new SelectionHandler<SuggestOracle.Suggestion>() {
					@Override
					public void onSelection(
							SelectionEvent<SuggestOracle.Suggestion> event) {
						relation.domain = domain.getText();
					}
				});
				*/
				final ListBox domain = new ListBox();
				for (String d : Constants.getDomainNames()) {
					domain.addItem(d);
				}
				
				if (relation.domain != null) {
					for (int i = 0; i < domain.getItemCount(); i++ ) {
						if (relation.domain.equals(domain.getItemText(i))) {
							domain.setSelectedIndex(i);
						}
					}
				}
				rel.setWidget(row, 1, domain );
				domain.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						int index = domain.getSelectedIndex();
						if (index != -1) {
							relation.domain = domain.getItemText(index);
						}
					}
				});
				
				
				final ListBox frequency = new ListBox();
				for (String f : Constants.getFrequencyNames()) {
					frequency.addItem(f);
				}
				
				if (relation.frequency != null) {
					for (int i = 0; i < frequency.getItemCount(); i++ ) {
						if (relation.frequency.equals(frequency.getItemText(i))) {
							frequency.setSelectedIndex(i);
						}
					}
				}
				rel.setWidget(row, 2, frequency );
				frequency.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						int index = frequency.getSelectedIndex();
						if (index != -1) {
							relation.frequency = frequency.getItemText(index);
						}
					}
				});
				
				final Button delete = new Button("Verwijderen");
				delete.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						person.relations.remove(relation);
						displayRelations();
					}
				});
				rel.setWidget(row, 3, delete );
				
				row ++;
			}
		}
	}
	
	public void setSaveCallback(AsyncCallback<Person> onSave) {
		this.onSave = onSave;
	}
	
	public void setCancelCallback(AsyncCallback<Person> onCancel) {
		this.onCancel = onCancel;
	}
	
	public void setDeleteCallback(AsyncCallback<Person> onDelete) {
		this.onDelete = onDelete;
	}	
	
	public void setNameSuggestions(List<String> nameSuggestions) {
		names.clear();
		if (nameSuggestions != null) {
			names.addAll(nameSuggestions);
		}
	}
	
	public void setPerson(Person person) {
		this.person = person;
		
		name.setText(person.name);
		age.setText((person.age != null ? person.age : "") + "");
		sex.setSelectedIndex((person.sex == Person.SEX.FEMALE) ? 1 : 0);

		displayRelations();
	}
	
	public Person getPerson() {
		return person;
	}
}
