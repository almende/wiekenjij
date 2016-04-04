package com.almende.wiekenjij.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.code.twig.annotation.Id;

public class Person implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id public Long id;
	public String test = "Buurtlab_2011";   // TODO: make test editable
	public String name;
	public Integer age;
	public SEX sex;
	
	public enum SEX {MALE, FEMALE};
	
	public List<Relation> relations = new ArrayList<Relation>();
	
	public Person() {	
	}
	
	public Person(String name, Integer age, SEX sex) {
		this.name = name;
		this.age = age;
		this.sex = sex;
	}
}
