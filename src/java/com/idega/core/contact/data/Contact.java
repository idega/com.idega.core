package com.idega.core.contact.data;

import java.util.Collection;

import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOLegacyEntity;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;

public interface Contact extends IDOLegacyEntity{

	public ContactPurpose getContactPurpose();
	
	public void setContactPurpose(ContactPurpose contactPurpose) throws IDOAddRelationshipException;
	
	public void setContactPurpose(String contactPurposeId);
	
	public void addContactPurpose(ContactPurpose contactPurpose) throws IDOAddRelationshipException;
	
	public Collection<ContactPurpose> getContactPurposes() throws IDORelationshipException;
	
	public void setContactPurposes(Collection<ContactPurpose> contactPurposes) throws IDORelationshipException;
	
	public void removeContactPurpose(ContactPurpose contactPurpose) throws IDORemoveRelationshipException;
}
