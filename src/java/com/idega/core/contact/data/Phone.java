package com.idega.core.contact.data;


public interface Phone extends com.idega.data.IDOLegacyEntity
{
	 public java.lang.String getNumber();
	 public int getPhoneTypeId();
	 public void setDefaultValues();
	 public void setNumber(java.lang.String p0);
	 public void setPhoneTypeId(int p0);
	 public ContactPurpose getContactPurpose();
	public void setContactPurpose(ContactPurpose contactPurpose);
}
