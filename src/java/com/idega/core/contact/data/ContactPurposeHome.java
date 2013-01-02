package com.idega.core.contact.data;

import java.util.Collection;

import javax.ejb.CreateException;

public interface ContactPurposeHome extends com.idega.data.IDOHome{
	 public ContactPurpose create() throws CreateException;
	 public Collection<ContactPurpose> getContactPurposes(int maxAmount);
}
