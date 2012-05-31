package com.idega.core.contact.data;


import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class ContactPurposeHomeImpl extends IDOFactory implements ContactPurposeHome{

	private static final long serialVersionUID = -3095244442933155843L;

	@Override
	public ContactPurpose create() throws CreateException {
		return (ContactPurpose)super.createIDO();
	}

	@Override
	protected Class<? extends IDOEntity> getEntityInterfaceClass() {
		return ContactPurpose.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<ContactPurpose> getContactPurposes(int maxAmount) {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		try {
			Collection<Integer> ids = ((ContactPurposeBMPBean)entity).ejbFindContactPurposes(maxAmount);
			this.idoCheckInPooledEntity(entity);
			return findByPrimaryKeyCollection(ids);
		} catch (FinderException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "failed finding any remote services");
			return Collections.emptyList();
		}
	}

}
