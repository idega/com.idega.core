package com.idega.core.contact.data;


public interface Email extends com.idega.data.IDOLegacyEntity,com.idega.core.contact.data.EmailDataView
{
 public void setEmailAddress(java.lang.String p0);
 public int getEmailTypeId();
 public java.lang.String getEmailAddress();
 public java.lang.String getEmailAddressMailtoFormatted();
 public java.lang.String getEmailAddressMailtoFormattedWithSubject(String subject);
 public void setEmailTypeId(int p0);
}
