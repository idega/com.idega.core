package com.idega.core.builder.data;

import java.util.Locale;

import com.idega.data.IDOLegacyEntity;


public interface ICPage extends com.idega.data.TreeableEntity, IDOLegacyEntity
{
 public void delete(int p0)throws java.sql.SQLException;
 public void delete()throws java.sql.SQLException;
 public boolean getDeleted();
 public int getDeletedBy();
 public java.sql.Timestamp getDeletedWhen();
 public com.idega.core.file.data.ICFile getFile();
 public int getLockedBy();
 public java.lang.String getName();
 public java.lang.String getName(Locale locale);
 public java.io.InputStream getPageValue();
 public java.io.OutputStream getPageValueForWrite();
 public java.lang.String getSubType();
 public int getTemplateId();
 public java.lang.String getType();
 public boolean isDraft();
 public boolean isDynamicTriggeredPage();
 public boolean isDynamicTriggeredTemplate();
 public boolean isFolder();
 public boolean isLeaf();
 public boolean isPage();
 public boolean isTemplate();
 public void setDefaultValues();
 public void setDeleted(boolean p0);
 public void setFile(com.idega.core.file.data.ICFile p0);
 public void setIsDraft();
 public void setIsFolder();
 public void setIsPage();
 public void setIsTemplate();
 public void setLockedBy(int p0);
 public void setName(java.lang.String p0);
 public void setOwner(com.idega.idegaweb.IWUserContext p0);
 public void setPageValue(java.io.InputStream p0);
 public void setSubType(java.lang.String p0);
 public void setTemplateId(int p0);
 public void setType(java.lang.String p0);
 public void setTreeOrder(int p0);
 public void setTreeOrder(java.lang.Integer p0);
 public int getTreeOrder();
 public boolean isCategory();
 public void setIsCategory(boolean isCategory);
	public void setFormat(String format);
	public String getFormat();
	public boolean getIsFormattedInIBXML();
	public boolean getIsFormattedInHTML();
}
