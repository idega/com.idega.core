package com.idega.core.data;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Locale;

import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOLegacyEntity;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;

public interface ICCategory extends com.idega.data.TreeableEntity,IDOLegacyEntity,com.idega.core.business.Category
{
 public void setValid(boolean p0);
 public boolean getValid();
 public void setLocaleId(int p0);
 public int getBusinessId();
 public java.lang.String getCategoryType();
 public void setName(java.lang.String p0);
 public void initializeAttributes();
 public void setDescription(java.lang.String p0);
 public java.lang.String getDescription();
 public void setType(java.lang.String p0);
 public java.sql.Timestamp getCreated();
 public java.lang.String getName();
 public void setBusinessId(int p0);
 public int getParentId();
 public java.lang.String getType();
 public int getLocaleId();
 public void setParentId(int p0);
 public void setCreated(java.sql.Timestamp p0);
 public String getName(Locale locale) ;
 public String getDescription(Locale locale);
 public ICCategoryTranslation getCategoryTranslation(Locale locale)throws RemoteException;
 // Gimmi 8.04.2003
 public void setOwnerGroupId(int ownerGroupId);
 public int getOwnerGroupId();
 public void addFile(ICFile file) throws IDOAddRelationshipException;
 public void removeFile(ICFile file) throws IDORemoveRelationshipException;
 public Collection getFiles() throws IDORelationshipException;
 public java.sql.Timestamp getInvalidationDate();
 public void setInvalidationDate(java.sql.Timestamp date);
 
}
