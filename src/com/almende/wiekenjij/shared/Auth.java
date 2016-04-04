package com.almende.wiekenjij.shared;

import java.io.Serializable;

public class Auth implements Serializable {
	private static final long serialVersionUID = 1L;

	public Auth() {}
	
	public String username = null;
	public String loginUrl = null;
	public String logoutUrl = null;
	public Boolean isUserLoggedIn = false;
	public Boolean isUserAdmin = false;
}
