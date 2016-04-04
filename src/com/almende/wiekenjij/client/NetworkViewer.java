package com.almende.wiekenjij.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.almende.wiekenjij.shared.Person;
import com.almende.wiekenjij.shared.Relation;
import com.chap.links.client.Network;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;

public class NetworkViewer extends Composite {
	// store the row numbers of the loaded persons in a map
	private Map<String, Integer> ids = new HashMap<String, Integer>();
	DataTable nodes = null;
	DataTable links = null;
	private Map<String, String> domains = Constants.getDomains();
    private Map<String, Integer> frequencies = Constants.getFrequencies(); 
	
	public NetworkViewer (List<Person> persons) {
		// load frequencies and domains

        for (int i = 0; i < persons.size(); i++) {
        	Person person = persons.get(i);
        	
        	Integer personId = addPerson(person.name);
        	
	        for (int j = 0; j < person.relations.size(); j++) {
	        	Relation relation = person.relations.get(j);
	        	
	        	Integer relationId = addPerson(relation.name);
	        	addRelation(personId, relationId, 
	        			relation.domain, relation.frequency);
	        }			        
        }
		
		Network.Options options = Network.Options.create();
		options.setWidth("600px");
		options.setHeight("500px");
		options.setBorderColor("lightgray");
		options.setLinksDefaultLength(120);
		
		Network network = new Network(nodes, links, options);
		
		VerticalPanel content = new VerticalPanel();
		content.add(new HTML("<div style='font-style: italic; margin-bottom: 10px; '>" +
			"Waarschuwing: deze weergave is nog niet uitgewerkt..." +
			"</div>"));
		content.add(network);
		
		String legend = "<div style='width: 600px; line-height: 150%; " +
			"padding-top: 10px;'>";
		for (String domain : domains.keySet()) {
			if (domain != null && !domain.isEmpty()) {
				String color = domains.get(domain);
				legend += "<span style='background-color:" + color + ";'> " +
					"&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;" + domain + " &nbsp;";
			}
		}
		legend += "</div>";
		content.add(new HTML(legend));

		initWidget(content);
	}


    private Integer addPerson(String name) {
		if (nodes == null) {
			// Create nodes table
	        nodes = DataTable.create();
	        nodes.addColumn(DataTable.ColumnType.NUMBER, "id");
	        nodes.addColumn(DataTable.ColumnType.STRING, "text");
		}
		
    	Integer id = ids.get(name);
    	if (id == null) {
    		id = nodes.getNumberOfRows();
	        nodes.addRow();
	        nodes.setValue(id, 0, id);
	        nodes.setValue(id, 1, name);
	        ids.put(name, id);
    	}
    	
    	return id;
    }
    
    private void addRelation(Integer from, Integer to, 
    		String domain, String frequency) {
    	if (links == null) {
            // Create links table
            links = DataTable.create();
            links.addColumn(DataTable.ColumnType.NUMBER, "from");
            links.addColumn(DataTable.ColumnType.NUMBER, "to");
            links.addColumn(DataTable.ColumnType.STRING, "color");
            links.addColumn(DataTable.ColumnType.NUMBER, "value");
            links.addColumn(DataTable.ColumnType.STRING, "style");
            links.addColumn(DataTable.ColumnType.STRING, "title");
    	}
    	
    	Integer id = links.getNumberOfRows();
    	String color = domains.get(domain);
    	if (color == null) {
    		color = "black";
    	}
    	Integer value = frequencies.get(frequency);
    	if (value == null) {
    		value = 0;
    	}
    	//String style = "arrow"; // TODO?
    	String style = "line";
    	String title = domain + "<br>" + frequency;
    	
    	links.addRow();
    	links.setValue(id, 0, from);
    	links.setValue(id, 1, to);
    	links.setValue(id, 2, color);
    	links.setValue(id, 3, value);
    	links.setValue(id, 4, style);
    	links.setValue(id, 5, title);
    }
}
