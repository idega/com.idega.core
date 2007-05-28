package com.idega.bean;

public class PropertiesBean implements AbstractProperties {
	
	private String server = null;
	private String login = null;
	private String password = null;

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public String getServer() {
		return server;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setServer(String server) {
		this.server = server;
	}

}
