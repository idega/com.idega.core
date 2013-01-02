package com.idega.core.contact.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.core.data.GenericTypeBMPBean;

public class ContactPurposeBMPBean extends GenericTypeBMPBean implements ContactPurpose{
	private static final long serialVersionUID = -2634693412540826529L;
	public final static String TABLE_NAME = "ic_contact_purpose";
	
	@Override
	public String getEntityName() {
		return TABLE_NAME;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Integer> ejbFindContactPurposes(int maxAmount) throws FinderException {
		String query = "SELECT * FROM "  + TABLE_NAME;
		if(maxAmount > 0){
			return super.idoFindPKsBySQL(query, maxAmount);
		}else{
			return super.idoFindPKsBySQL(query);
		}
	}

}
