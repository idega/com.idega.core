package com.idega.core.file.data;

import java.util.Collection;

import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOEntity;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.MetaDataCapable;
import com.idega.data.TreeableEntity;
import com.idega.io.serialization.Storable;
import com.idega.repository.data.Resource;
import com.idega.user.data.User;

public interface ICFile extends IDOEntity, TreeableEntity<ICFile>, MetaDataCapable, Resource, Storable {
 public final static String UFN_NAME = "NAME";
 public java.sql.Timestamp getCreationDate();
 public boolean getDeleted();
 public int getDeletedByUserId();
 public java.sql.Timestamp getDeletedWhen();
 public java.lang.String getDescription();
 public java.lang.Integer getFileSize();
 public java.io.InputStream getFileValue();
 public java.io.OutputStream getFileValueForWrite();
 public com.idega.core.localisation.data.ICLocale getICLocale();
 public int getLanguage();
 public java.util.Locale getLocale();
 public int getLocaleId();
 public java.lang.String getMimeType();
 public java.sql.Timestamp getModificationDate();
 public java.lang.String getName();
 public void initializeAttributes();
 @Override
 public boolean isLeaf();
 public void setCreationDate(java.sql.Timestamp p0);
 public void setDeleted(boolean p0);
 public void setDescription(java.lang.String p0);
 public void setFileSize(java.lang.Integer p0);
 public void setFileSize(int p0);
 public void setFileValue(java.io.InputStream p0);
 public void setLanguage(int p0);
 public void setLocale();
 public void setMimeType(java.lang.String p0);
 public void setModificationDate(java.sql.Timestamp p0);
 public void setName(java.lang.String p0);
 public void superDelete()throws java.sql.SQLException;
 public void unDelete(boolean p0)throws java.sql.SQLException;
 public void delete()throws java.sql.SQLException;
 public boolean isFolder();
 public boolean isEmpty();
 public String getLocalizationKey();
 public void setLocalizationKey(String key);
 public void setFileUri(String uri);
 public String getFileUri();

 	public Collection<User> getDownloadedBy();
	public void addDownloadedBy(User downloader) throws IDOAddRelationshipException;
	public void removeDownloadedBy(User downloader) throws IDORemoveRelationshipException;

	public Integer getHash();
	public void setHash(Integer hash);

	@Override
	public ICFile getParentNode();
	
	@Override
	public int getIndex(ICFile node);
}