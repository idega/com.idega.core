package com.idega.repository.bean;

import java.io.Serializable;
import java.util.Date;

public class RepositoryItemVersionInfo implements Serializable {

	private static final long serialVersionUID = -5131971135536552140L;

	private Date created;
	private String id, name, path, comment, checkedOut, checkedIn;
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

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public String getCheckedOut() {
		return checkedOut;
	}
	public void setCheckedOut(String checkedOut) {
		this.checkedOut = checkedOut;
	}
	public String getCheckedIn() {
		return checkedIn;
	}
	public void setCheckedIn(String checkedIn) {
		this.checkedIn = checkedIn;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "Version " + getVersion() + " with ID "+ getId() + " created " + getCreated();
	}
}