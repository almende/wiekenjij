package com.almende.wiekenjij.client;

import java.util.List;

import com.almende.wiekenjij.shared.Auth;
import com.almende.wiekenjij.shared.Person;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("person")
public interface PersonService extends RemoteService {
	public List<Person> getPersons() throws Exception;
	public Person findPerson(String name) throws Exception;
	public String storePerson(Person person) throws Exception;
	public String deletePerson(Person person) throws Exception;	
	
	public Auth getAuth();
}
