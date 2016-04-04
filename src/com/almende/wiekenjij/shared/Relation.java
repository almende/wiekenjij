package com.almende.wiekenjij.shared;

import java.io.Serializable;

public class Relation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String name;        // Name of the friend
	public String domain;      // School, Sports, neighbors, ...
	public String frequency;   // every day, once per month, ...

	public Relation() {	
	}
	
	public Relation(String name, String domain, String frequency) {
		this.name = name;
		this.domain = domain;
		this.frequency = frequency;
	}
}
