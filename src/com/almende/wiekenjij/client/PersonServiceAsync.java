package com.almende.wiekenjij.client;

import java.util.List;

import com.almende.wiekenjij.shared.Auth;
import com.almende.wiekenjij.shared.Person;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>PersonService</code>.
 */
public interface PersonServiceAsync {
	public void getPersons(AsyncCallback<List<Person>> callback);
	public void findPerson(String name, AsyncCallback<Person> callback);
	public void storePerson(Person person, AsyncCallback<String> callback);
	public void deletePerson(Person person, AsyncCallback<String> callback);
	
	public void getAuth(AsyncCallback<Auth> callback);
}
