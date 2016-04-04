package com.almende.wiekenjij.server;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import com.almende.wiekenjij.client.PersonService;
import com.almende.wiekenjij.shared.Auth;
import com.almende.wiekenjij.shared.Person;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.AnnotationObjectDatastore;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PersonServiceImpl extends RemoteServiceServlet implements
		PersonService {
	private ObjectDatastore datastore = new AnnotationObjectDatastore();
	
	final static public UserService userService = 
		UserServiceFactory.getUserService();
	
	public PersonServiceImpl() {
	}
	
	public Auth getAuth() {
		String servletUrl = getServletUrl();
		
		Auth auth = new Auth();
		if (userService.getCurrentUser() != null) {
			auth.username = userService.getCurrentUser().getEmail();
			auth.isUserLoggedIn = userService.isUserLoggedIn();
			auth.isUserAdmin = userService.isUserAdmin();
		}
		auth.loginUrl = userService.createLoginURL(servletUrl);
		auth.logoutUrl = userService.createLogoutURL(servletUrl);
		
		return auth;
	}
	
	private void checkAuth() throws Exception {
		Auth auth = getAuth();
		
		if (!auth.isUserLoggedIn) {
			throw new Exception("Not logged in");
		}
		
		if (!auth.isUserAdmin) {
			throw new Exception ("Not logged in as a known user");
		}
	}


	private String getServletUrl() {
		String id = SystemProperty.applicationId.get();
		String url = "https://" + id + ".appspot.com";
		return url;
	}
	
	public List<Person> getPersons() throws Exception {
		checkAuth();
		
		List<Person> all = new ArrayList<Person>();

		QueryResultIterator<Person> persons = datastore.find(Person.class);

		while (persons.hasNext()) {
			Person person = persons.next();
			if (person != null)
				all.add(person);
		}
		
		return all;
	}
	
	public Person findPerson(String name) throws Exception {
		checkAuth();

		Iterator<Person> persons = datastore.find()
    		.type(Person.class)
			.addFilter("name", FilterOperator.EQUAL, name)
			.now();

		if (persons.hasNext()) {
			return persons.next();
		}

		return null;
	}
	
	public String storePerson(Person person) throws Exception {
		checkAuth();
		
		datastore.store().instance(person).now();
		return "person stored";
	}
	
	public String deletePerson(Person person) throws Exception {
		checkAuth();
		
		datastore.associate(person);
		datastore.delete(person);
		return "Person deleted";
	}
}
