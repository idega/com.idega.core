package com.idega.core.data;

import javax.ejb.*;

public interface Email extends com.idega.data.IDOLegacyEntity,com.idega.core.business.EmailDataView
{
 public java.lang.String getEmailAddress();
 public int getEmailTypeId();
 public void setEmailAddress(java.lang.String p0);
 public void setEmailTypeId(int p0);
}
