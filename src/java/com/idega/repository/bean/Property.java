package com.idega.repository.bean;

import java.io.Serializable;

public class Property implements Serializable {

	private static final long serialVersionUID = 1011290186664096195L;

	private String key;
	private Serializable value;

	public Property() {
		super();
	}

	public Property(String key, Serializable value) {
		this();

		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Serializable getValue() {
		return value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return getKey() + ": " + getValue();
	}
}