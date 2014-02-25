/*
 * Created on 23.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.core.file.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Collator;
import java.util.Collection;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.core.localisation.data.ICLocale;
import com.idega.data.BlobWrapper;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.TreeableEntityWrapper;
import com.idega.io.serialization.ObjectReader;
import com.idega.io.serialization.ObjectWriter;
import com.idega.presentation.IWContext;
import com.idega.user.data.User;

/**
 * Title:		ICFileWrapperBean
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class ICFileWrapperBean extends TreeableEntityWrapper<ICFile> implements ICFile {

	private static final long serialVersionUID = -6927389169887750294L;

	/**
	 * @param locale
	 * @throws IDOLookupException
	 */
	public ICFileWrapperBean(Object primaryKey) throws IDOLookupException, FinderException {
		super(primaryKey);
	}

	/**
	 * @param locale
	 * @throws IDOLookupException
	 */
	public ICFileWrapperBean(Object primaryKey, ICLocale locale) throws IDOLookupException, FinderException {
		super(primaryKey, locale);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityWrapper#getMainClass()
	 */
	@Override
	protected Class<ICFile> getMainClass() {
		return ICFile.class;
	}

	@Override
	protected Class<ICFile> getVersionClass() {
		return ICFile.class;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityWrapper#useVersions()
	 */
	@Override
	protected boolean useVersions() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityWrapper#useTranslations()
	 */
	@Override
	protected boolean useTranslations() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getCreationDate()
	 */
	@Override
	public Timestamp getCreationDate() {
		return ((ICFile)this.getMainEntity()).getCreationDate();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getDeleted()
	 */
	@Override
	public boolean getDeleted() {
		return ((ICFile)this.getMainEntity()).getDeleted();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getDeletedByUserId()
	 */
	@Override
	public int getDeletedByUserId() {
		return ((ICFile)this.getMainEntity()).getDeletedByUserId();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getDeletedWhen()
	 */
	@Override
	public Timestamp getDeletedWhen() {
		return ((ICFile)this.getMainEntity()).getDeletedWhen();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getDescription()
	 */
	@Override
	public String getDescription() {
		if (useVersions()) {
			return ((ICFile)this.getMainEntity()).getDescription();
		} else {
			return ((ICFile)this.getCurrentOpenVersionEntity()).getDescription();
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getFileSize()
	 */
	@Override
	public Integer getFileSize() {
		if (useVersions()) {
			return ((ICFile)this.getMainEntity()).getFileSize();
		} else {
			return ((ICFile)this.getCurrentOpenVersionEntity()).getFileSize();
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getFileValue()
	 */
	@Override
	public InputStream getFileValue() {
		if (useVersions()) {
			return ((ICFile)this.getMainEntity()).getFileValue();
		} else {
			return ((ICFile)this.getCurrentOpenVersionEntity()).getFileValue();
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getFileValueForWrite()
	 */
	@Override
	public OutputStream getFileValueForWrite() {
		if (useVersions()) {
			return ((ICFile)this.getMainEntity()).getFileValueForWrite();
		} else {
			return ((ICFile)this.getCurrentOpenVersionEntity()).getFileValueForWrite();
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getICLocale()
	 */
	@Override
	public Locale getLocale() {
		return ((ICFile)this.getMainEntity()).getLocale();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getLanguage()
	 */
	@Override
	public int getLanguage() {
		return ((ICFile)this.getMainEntity()).getLanguage();

	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getLocaleId()
	 */
	@Override
	public int getLocaleId() {
		return ((ICFile)this.getMainEntity()).getLocaleId();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getMimeType()
	 */
	@Override
	public String getMimeType() {
		if (useVersions()) {
			return ((ICFile)this.getMainEntity()).getMimeType();
		} else {
			return ((ICFile)this.getCurrentOpenVersionEntity()).getMimeType();
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getModificationDate()
	 */
	@Override
	public Timestamp getModificationDate() {
		return ((ICFile)this.getMainEntity()).getModificationDate();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getName()
	 */
	@Override
	public String getName() {
		if (useVersions()) {
			return ((ICFile)this.getMainEntity()).getName();
		} else {
			return ((ICFile)this.getCurrentOpenVersionEntity()).getName();
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return ((ICFile)this.getMainEntity()).isLeaf();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#setCreationDate(java.sql.Timestamp)
	 */
	@Override
	public void setCreationDate(Timestamp p0) {
		if (useVersions()) {
			((ICFile)this.getMainEntity()).setCreationDate(p0);
		} else {
			((ICFile)this.getCurrentOpenVersionEntity()).setCreationDate(p0);
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#setDeleted(boolean)
	 */
	@Override
	public void setDeleted(boolean p0) {
		((ICFile)this.getMainEntity()).setDeleted(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String p0) {
		if (useVersions()) {
			((ICFile)this.getMainEntity()).setDescription(p0);
		} else {
			((ICFile)this.getCurrentOpenVersionEntity()).setDescription(p0);
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#setFileSize(java.lang.Integer)
	 */
	@Override
	public void setFileSize(Integer p0) {
		if (useVersions()) {
			((ICFile)this.getMainEntity()).setFileSize(p0);
		} else {
			((ICFile)this.getCurrentOpenVersionEntity()).setFileSize(p0);
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#setFileSize(int)
	 */
	@Override
	public void setFileSize(int p0) {
		if (useVersions()) {
			((ICFile)this.getMainEntity()).setFileSize(p0);
		} else {
			((ICFile)this.getCurrentOpenVersionEntity()).setFileSize(p0);
		}

	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#setFileValue(java.io.InputStream)
	 */
	@Override
	public void setFileValue(InputStream p0) {
		if (useVersions()) {
			((ICFile)this.getMainEntity()).setFileValue(p0);
		} else {
			((ICFile)this.getCurrentOpenVersionEntity()).setFileValue(p0);
		}

	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#setLanguage(int)
	 */
	@Override
	public void setLanguage(int p0) {
		((ICFile)this.getMainEntity()).setLanguage(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#setLocale()
	 */
	@Override
	public void setLocale() {
		((ICFile)this.getMainEntity()).setLocale();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#setMimeType(java.lang.String)
	 */
	@Override
	public void setMimeType(String p0) {
		if (useVersions()) {
			((ICFile)this.getMainEntity()).setMimeType(p0);
		} else {
			((ICFile)this.getCurrentOpenVersionEntity()).setMimeType(p0);
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#setModificationDate(java.sql.Timestamp)
	 */
	@Override
	public void setModificationDate(Timestamp p0) {
		((ICFile)this.getMainEntity()).setModificationDate(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#setName(java.lang.String)
	 */
	@Override
	public void setName(String p0) {
		if (useVersions()) {
			((ICFile)this.getMainEntity()).setName(p0);
		} else {
			((ICFile)this.getCurrentOpenVersionEntity()).setName(p0);
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#superDelete()
	 */
	@Override
	public void superDelete() throws SQLException {
		throw new UnsupportedOperationException("method superDelete() not supported in ICFileWrapper");
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#unDelete(boolean)
	 */
	@Override
	public void unDelete(boolean p0) throws SQLException {
		throw new UnsupportedOperationException("method unDelete() not supported in ICFileWrapper");
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#initializeAttributes()
	 */
	@Override
	public void initializeAttributes() {
		// do nothing
		//((ICFile)this.getMainEntity()).initializeAttributes();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#delete()
	 */
	@Override
	public void delete() throws SQLException {
		((ICFile)this.getMainEntity()).delete();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#getBlobWrapperFileValue()
	 */
	public BlobWrapper getBlobWrapperFileValue() {
		throw new UnsupportedOperationException("method getBlobWrapperFileValue() not yet implemented in ICFileWrapper");
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#setBlobWrapperFileValue(com.idega.data.BlobWrapper)
	 */
	public void setBlobWrapperFileValue(BlobWrapper p0) {
		throw new UnsupportedOperationException("method setBlobWrapperFileValue() not yet implemented  in ICFileWrapper");
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#isFolder()
	 */
	@Override
	public boolean isFolder()
	{
		return ((ICFile)this.getMainEntity()).isFolder();
	}

	@Override
	public int compareTo(IDOEntity entity) {
		try {
			Collator coll = null;
			if (getLocale() != null) {
				coll = Collator.getInstance(getLocale());
			}
			else {
				coll = Collator.getInstance();
			}

			return coll.compare(this.getPrimaryKey(), entity.getPrimaryKey());
		}
		catch (ClassCastException e) {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#leafsFirst()
	 */
	@Override
	public boolean leafsFirst() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#sortLeafs()
	 */
	@Override
	public boolean sortLeafs() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#setLeafsFirst(boolean)
	 */
	@Override
	public void setLeafsFirst(boolean b) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#setToSortLeafs(boolean)
	 */
	@Override
	public void setToSortLeafs(boolean b) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.idega.core.file.data.ICFile#getLocalizationKey()
	 */
	@Override
	public String getLocalizationKey() {
		return ((ICFile)this.getMainEntity()).getLocalizationKey();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.file.data.ICFile#setLocalizationKey(java.lang.String)
	 */
	@Override
	public void setLocalizationKey(String key) {
		((ICFile)this.getMainEntity()).setLocalizationKey(key);
	}

	@Override
	public boolean isEmpty() {
		return ((ICFile)this.getMainEntity()).isEmpty();
	}

	@Override
	public void renameMetaData(String oldKeyName, String newKeyName) {
		((ICFile) this.getMainEntity()).renameMetaData(oldKeyName, newKeyName);
	}

	@Override
	public void renameMetaData(String oldKeyName, String newKeyName, String value) {
		((ICFile) this.getMainEntity()).renameMetaData(oldKeyName, newKeyName, value);
	}

	@Override
	public String getDatasource() {
		return ((ICFile) this.getMainEntity()).getDatasource();
	}

	@Override
	public void setFileUri(String uri) {
		((ICFile) this.getMainEntity()).setFileUri(uri);
	}

	@Override
	public String getFileUri() {
		return ((ICFile) this.getMainEntity()).getFileUri();
	}

	// implements Storable
	@Override
	public Object write(ObjectWriter writer, IWContext iwc) throws RemoteException {
		return writer.write(this, iwc);
	}

	// implements Storable
	@Override
	public Object read(ObjectReader reader, IWContext iwc) throws RemoteException {
		return reader.read(this, iwc);
	}

	@Override
	public void addDownloadedBy(User downloader) throws IDOAddRelationshipException {
		throw new UnsupportedOperationException("This method is not yet implemented  in ICFileWrapper");
	}

	@Override
	public Collection<User> getDownloadedBy() {
		throw new UnsupportedOperationException("This method is not yet implemented  in ICFileWrapper");
	}

	@Override
	public void removeDownloadedBy(User downloader) throws IDORemoveRelationshipException {
		throw new UnsupportedOperationException("This method is not yet implemented  in ICFileWrapper");
	}

	@Override
	public Integer getHash() {
		throw new UnsupportedOperationException("This method is not yet implemented  in ICFileWrapper");
	}

	@Override
	public void setHash(Integer hash) {
		throw new UnsupportedOperationException("This method is not yet implemented  in ICFileWrapper");
	}
}