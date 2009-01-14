package com.idega.core.builder.data;

import java.util.Collection;
import java.util.Locale;
import com.idega.data.IDOLegacyEntity;
import com.idega.data.UniqueIDCapable;
import com.idega.io.serialization.Storable;
import com.idega.repository.data.Resource;

public interface ICPage extends com.idega.data.TreeableEntity, IDOLegacyEntity, Resource, Storable,UniqueIDCapable {

	public void delete(int p0) throws java.sql.SQLException;

	public void delete() throws java.sql.SQLException;

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

	/**
	 * Gets the id/key of the template of this page as a String.
	 * Returns null if no template is set (templateId<=0)
	 * @return
	 */
	public String getTemplateKey();

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

	/**
	 * Sets the id/key of the template of this page as a String
	 * @return
	 */
	public void setTemplateKey(String templateKey);

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

	public boolean getIsFormattedInJSP();

	public boolean getIsFormattedInFacelet();

	public boolean getIsFormattedInIBXML2();
	
	/**
	 * Gets the id/key of the page as a String
	 * 
	 * @return
	 */
	public String getPageKey();

	public String getDefaultPageURI();

	public void setDefaultPageURI(String pageUri);
	
	public String getWebDavUri();
	public void setWebDavUri(String fileUri);
	
	public ICDomain getDomain();
	
	public int getDomainId();
	
	public void setDomain(ICDomain domain);
	
	public void setHidePageInMenu(boolean hidePageInMenu);
	
	public boolean isHidePageInMenu();
	
	public Collection ejbFindBySubType(String subType, boolean deleted)  throws javax.ejb.FinderException;
	
	public void setPublished(boolean published);
	
	public boolean isPublished();
	
	public void setLocked(boolean locked);
	
	public boolean isLocked();
}
