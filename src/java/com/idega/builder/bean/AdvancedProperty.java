package com.idega.builder.bean;

public class AdvancedProperty {
	
	private int status;
	
	private String id = null;
	private String value = null;
	
	public AdvancedProperty() {}
	
	public AdvancedProperty(String id) {
		this();
		this.id = id;
	}
	
	public AdvancedProperty(String id, String value) {
		this(id);
		this.value = value;
	}
	
	public AdvancedProperty(int status, String value) {
		this();
		this.status = status;
		this.value = value;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String toString() {
		return "Status: " + getStatus() + ", value: " + getValue();
	}
	
}