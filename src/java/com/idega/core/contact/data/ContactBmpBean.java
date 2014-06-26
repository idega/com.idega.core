package com.idega.core.contact.data;

import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.RemoveException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.util.ListUtil;

public abstract class ContactBmpBean extends GenericEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = -757671147177963517L;

	public ContactBmpBean(){
		super();
	}

	public ContactBmpBean(int id) throws SQLException {
		super(id);
	}


	@Override
	public void initializeAttributes() {
		addManyToManyRelationShip(ContactPurpose.class);
	}

	public ContactPurpose getContactPurpose(){
		Collection<ContactPurpose> contactPurposes;
		try{
			contactPurposes = getContactPurposes();
		}catch (Exception e) {
			contactPurposes = null;
		}
		if(ListUtil.isEmpty(contactPurposes)){
			return null;
		}
		return contactPurposes.iterator().next();
	}

	public void setContactPurpose(ContactPurpose contactPurpose) throws IDOAddRelationshipException{
		try {
			idoRemoveFrom(ContactPurpose.class);
		} catch (IDORemoveRelationshipException e) {

		}
		addContactPurpose(contactPurpose);
	}

	public void setContactPurpose(String contactPurposeId){
		try{
			idoRemoveFrom(ContactPurpose.class);
		}catch (Exception e) {
		}
		if(contactPurposeId == null){
			return;
		}
		try {
			idoAddTo(ContactPurpose.class, contactPurposeId);
		} catch (Exception e) {
		}
	}

	public void addContactPurpose(ContactPurpose contactPurpose) throws IDOAddRelationshipException{
		idoAddTo(contactPurpose);
	}

	public Collection<ContactPurpose> getContactPurposes() throws IDORelationshipException{
		return idoGetRelatedEntities(ContactPurpose.class);
	}

	public void setContactPurposes(Collection<ContactPurpose> contactPurposes) throws IDORelationshipException{
		idoRemoveFrom(ContactPurpose.class);
		for(ContactPurpose contactPurpose : contactPurposes){
			addContactPurpose(contactPurpose);
		}
	}

	public void removeContactPurpose(ContactPurpose contactPurpose) throws IDORemoveRelationshipException{
		idoRemoveFrom(contactPurpose);
	}


	@Override
	public void remove() throws RemoveException {
		try {
			idoRemoveFrom(ContactPurpose.class);
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.remove();
	}


}
