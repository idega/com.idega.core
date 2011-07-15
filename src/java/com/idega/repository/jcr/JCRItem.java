package com.idega.repository.jcr;

import java.io.Serializable;

import javax.jcr.Property;
import javax.jcr.lock.Lock;

import com.idega.repository.bean.RepositoryItem;

public abstract class JCRItem extends RepositoryItem {

	private static final long serialVersionUID = 7937464763729451494L;

	private String checkedOut;

	public abstract boolean isLocked();
	public abstract Lock lock(boolean isDeep, boolean isSessionScoped);
	public abstract void unlock();
	public abstract void unCheckOut();

	public abstract Property setProperty(String property, Serializable value);

	//	TODO
	public String getCheckedOut() {
		return checkedOut;
	}

	public void setCheckedOut(String checkedOut) {
		this.checkedOut = checkedOut;
	}

}