package com.idega.repository.bean;

import java.io.Serializable;
import java.util.Date;

public class RepositoryItemVersionInfo implements Serializable {

	private static final long serialVersionUID = -5131971135536552140L;

	private Date created;
	private String id;
	private Double version;

	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Double getVersion() {
		return version;
	}
	public void setVersion(Double version) {
		this.version = version;
	}

	public String toString() {
		return "Version " + getVersion() + " with ID "+ getId() + " created " + getCreated();
	}
}