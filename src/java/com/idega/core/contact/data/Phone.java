package com.idega.core.contact.data;

import com.idega.data.ExplicitlySynchronizedEntity;


public interface Phone extends Contact, ExplicitlySynchronizedEntity
{
	 public java.lang.String getNumber();
	 public int getPhoneTypeId();
	 public void setDefaultValues();
	 public void setNumber(java.lang.String p0);
	 public void setPhoneTypeId(int p0);
	 public PhoneType getPhoneType();
}
