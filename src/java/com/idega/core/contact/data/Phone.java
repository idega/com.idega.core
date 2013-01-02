package com.idega.core.contact.data;


public interface Phone extends Contact
{
	 public java.lang.String getNumber();
	 public int getPhoneTypeId();
	 public void setDefaultValues();
	 public void setNumber(java.lang.String p0);
	 public void setPhoneTypeId(int p0);
	 public PhoneType getPhoneType();
}
