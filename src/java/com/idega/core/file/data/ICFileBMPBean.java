package com.idega.core.file.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;

import javax.ejb.FinderException;

import com.idega.core.dao.ApplicationBindingDAO;
import com.idega.core.data.bean.ApplicationBinding;
import com.idega.core.localisation.data.ICLocale;
import com.idega.core.persistence.Param;
import com.idega.core.user.data.User;
import com.idega.core.version.data.ICItem;
import com.idega.core.version.data.ICVersion;
import com.idega.core.version.util.ICVersionQuery;
import com.idega.data.BlobWrapper;
import com.idega.data.EntityControl;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOQuery;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.IDORuntimeException;
import com.idega.data.IDOStoreException;
import com.idega.data.MetaDataCapable;
import com.idega.data.TreeableEntityBMPBean;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWCacheManager;
import com.idega.io.serialization.ObjectReader;
import com.idega.io.serialization.ObjectWriter;
import com.idega.io.serialization.Storable;
import com.idega.presentation.IWContext;
import com.idega.repository.bean.RepositoryItem;
import com.idega.repository.data.Resource;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.FileUtil;
import com.idega.util.IOUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="bjarni@idega.is">Bjarni Vilhjalmsson</a>,<a href="tryggvi@idega.is">Tryggvi Larusson</a>,<a href="aron@idega.is">Aron Birkir</a>
 * @version 1.0
 */

public class ICFileBMPBean extends TreeableEntityBMPBean<ICFile> implements ICFile, MetaDataCapable, Resource, Storable {

	private static final long serialVersionUID = 6911355143485852117L;

	public final static String IW_BUNDLE_IDENTIFIER = "com.idega.core";

	private static final String ENTITY_NAME = "IC_FILE";
	private static final String FILE_VALUE = "FILE_VALUE";
	public static String IC_ROOT_FOLDER_CACHE_KEY = "IC_ROOT_FOLDER";
	public static String IC_ROOT_FOLDER_NAME = "ICROOT";
	public static String IC_APPLICATION_BINDING_TYPE_SYSTEM_FOLDER = "system_folder";
	private static final String FILE_URI_IN_SLIDE = "FILE_URI_IN_SLIDE";

	public static final int NODETYPE_FOLDER = 0;
	public static final int NODETYPE_FILE = 1;

	public final static String DELETED = CoreConstants.Y;
	public final static String NOT_DELETED = "N";

	private static final String COLUMN_HASH = "HASH_VALUE";

	private static final String TABLENAME_ICFILE_ICITEM = "ic_file_ic_item";
	private static final String TABLENAME_ICFILE_ICVERSION = "ic_file_ic_version";
	private static final String FILE_DOWNLOADERS = ENTITY_NAME + "_DOWNLOADERS";

	public ICFileBMPBean() {
		super();
		this._sortLeafs = true;
	}

	public ICFileBMPBean(int id) throws SQLException {
		super(id);
		this._sortLeafs = true;
	}

	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		//Removed LanguageIDColumn in favor of Locale
		//addAttribute(getColumnNameLanguageId(),"Language",true,true, Integer.class,"many-to-one",ICLanguage.class);
		addManyToOneRelationship(getColumnNameLocale(), "Locale", ICLocale.class);
		//    addManyToOneRelationship(getColumnNameMimeType(),"Mime Type of file",ICMimeType.class);
		addAttribute(getColumnNameMimeType(), "Mime Type of file", true, true, String.class, 100, "many-to-one", ICMimeType.class);
		addAttribute(getColumnNameName(), "File name", true, true, String.class, 255);
		addAttribute(getColumnNameDescription(), "Description", true, true, String.class, 1000);
		addAttribute(getColumnNameFileValue(), "The file value", true, true, com.idega.data.BlobWrapper.class);
		addAttribute(getColumnNameCreationDate(), "Creation date", true, true, java.sql.Timestamp.class);
		addAttribute(getColumnNameModificationDate(), "Modification date", true, true, java.sql.Timestamp.class);
		addAttribute(getColumnNameFileSize(), "file size in bytes", true, true, Integer.class);
		addAttribute(COLUMN_HASH, "Hash value", true, true, Integer.class);

		addAttribute(getColumnDeleted(), "Deleted", true, true, String.class, 1);
		addAttribute(getColumnDeletedBy(), "Deleted by", true, true, Integer.class, "many-to-one", User.class);
		addAttribute(getColumnDeletedWhen(), "Deleted when", true, true, Timestamp.class);
		addAttribute(getColumnNameLocalizationKey(), "Localization key", true, true, String.class, 255);
		addAttribute(FILE_URI_IN_SLIDE, "File URI", true, true, String.class, 1000);

		addManyToManyRelationShip(ICItem.class, TABLENAME_ICFILE_ICITEM);
		addManyToManyRelationShip(ICVersion.class, TABLENAME_ICFILE_ICVERSION);
		addManyToManyRelationShip(com.idega.user.data.User.class, FILE_DOWNLOADERS);

		addMetaDataRelationship(); //can have extra info in the ic_metadata table

		addIndex("IDX_IC_FILE_1", getColumnNameName());

	}

	@Override
	public String getEntityName() {
		return (ENTITY_NAME);
	}

	public static String getEntityTableName() {
		return "IC_FILE";
	}

	public static String getColumnNameMimeType() {
		return "MIME_TYPE";
	}
	public static String getColumnNameName() {
		return UFN_NAME;
	}
	public static String getColumnNameDescription() {
		return "DESCRIPTION";
	}
	public static String getColumnNameFileValue() {
		return "FILE_VALUE";
	}
	public static String getColumnNameCreationDate() {
		return "CREATION_DATE";
	}
	public static String getColumnNameModificationDate() {
		return "MODIFICATION_DATE";
	}
	public static String getColumnNameFileSize() {
		return "FILE_SIZE";
	}
	public static String getColumnNameLocale() {
		return "IC_LOCALE_ID";
	}
	public static String getColumnNameLocalizationKey() {
		return "IC_LOCALIZATION_KEY";
	}
	public static String getColumnDeleted() {
		return "DELETED";
	}
	public static String getColumnDeletedBy() {
		return "DELETED_BY";
	}
	public static String getColumnDeletedWhen() {
		return "DELETED_WHEN";
	}

	/**
	 * @deprecated Replaced with getColumnLocale()
	 */
	@Deprecated
	public static String getColumnNameLanguageId() {
		return "IC_LANGUAGE_ID";
	}

	public static String getColumnFileValue() {
		return FILE_VALUE;
	}

	@Override
	public int getLanguage() {
		return getIntColumnValue(getColumnNameLanguageId());
	}

	@Override
	public String getMimeType() {
		return getStringColumnValue(getColumnNameMimeType());
	}

	@Override
	public String getName() {
		return getStringColumnValue(getColumnNameName());
	}

	@Override
	public String getLocalizationKey() {
		return getStringColumnValue(getColumnNameLocalizationKey());
	}

	@Override
	public String getDescription() {
		return getStringColumnValue(getColumnNameDescription());
	}

	public BlobWrapper getBlobWrapperFileValue() {
		return (BlobWrapper)getColumnValue(getColumnFileValue());
	}

	@Override
	public boolean isEmpty() {
		return isStoredValueNull(getColumnNameFileValue());
	}

	@Override
	public InputStream getFileValue() {
		InputStream stream = getInputStreamColumnValue(getColumnFileValue());
		if (stream == null) {
			String uri = getFileUri();
			if (!StringUtil.isEmpty(uri)) {
				try {
					File tmp = CoreUtil.getFileFromRepository(uri);
					if (tmp instanceof RepositoryItem) {
						stream = ((RepositoryItem) tmp).getInputStream();
					} else {
						stream = new FileInputStream(tmp);
					}
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Error getting input stream from " + uri + ", file ID: " + getPrimaryKey(), e);
				}
			}
		}
		return stream;
	}

	@Override
	public Timestamp getCreationDate() {
		return (Timestamp)getColumnValue(getColumnNameCreationDate());
	}

	@Override
	public Timestamp getModificationDate() {
		return (Timestamp)getColumnValue(getColumnNameModificationDate());
	}

	@Override
	public Integer getFileSize() {
		Integer size = (Integer) getColumnValue(getColumnNameFileSize());
		if (size == null) {
			BlobWrapper blobWrapper = getBlobWrapperFileValue();
			if (blobWrapper == null) {
				return 0;
			}
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			try {
				FileUtil.streamToOutputStream(blobWrapper.getBlobInputStream(), output);
				size = output.toByteArray().length;
				setFileSize(size);
				this.store();
			} catch (Exception e) {
			} finally {
				IOUtil.closeOutputStream(output);
			}
		}
		return size;
	}

	@Override
	public void setLanguage(int language) {
		setColumn(getColumnNameLanguageId(), new Integer(language));
	}

	@Override
	public void setMimeType(String mimeType) {
		setColumn(getColumnNameMimeType(), mimeType);
	}

	@Override
	public void setName(String Name) {
		setColumn(getColumnNameName(), Name);
	}

	@Override
	public void setLocalizationKey(String key) {
		setColumn(getColumnNameLocalizationKey(), key);
	}

	@Override
	public void setDescription(String description) {
		setColumn(getColumnNameDescription(), description);
	}

	@Override
	public void setFileSize(Integer fileSize) {
		setColumn(getColumnNameFileSize(), fileSize);
	}

	@Override
	public void setFileSize(int fileSize) {
		setColumn(getColumnNameFileSize(), fileSize);
	}

	public void setBlobWrapperFileValue(BlobWrapper fileValue) {
		setColumn(getColumnFileValue(), fileValue);
	}

	@Override
	public void setFileValue(InputStream fileValue) {
		setColumn(getColumnFileValue(), fileValue);
	}

	@Override
	public OutputStream getFileValueForWrite() {
		return getColumnOutputStream(getColumnFileValue());
	}

	@Override
	public void setCreationDate(Timestamp creationDate) {
		setColumn(getColumnNameCreationDate(), creationDate);
	}

	@Override
	public void setModificationDate(Timestamp modificationDate) {
		setColumn(getColumnNameModificationDate(), modificationDate);
	}

	@Override
	public void insert() throws SQLException {
		this.setCreationDate(com.idega.util.IWTimestamp.getTimestampRightNow());
		super.insert();
	}

	@Override
	public void update() throws SQLException {
		this.setModificationDate(com.idega.util.IWTimestamp.getTimestampRightNow());
		super.update();
	}

	@Override
	public Locale getLocale() {
		ICLocale icLocale = this.getICLocale();
		if (icLocale != null) {
			return icLocale.getLocaleObject();
		}
		return null;
	}

	@Override
	public ICLocale getICLocale() {
		return (ICLocale)super.getColumnValue(ICFileBMPBean.getColumnNameLocale());
	}

	@Override
	public int getLocaleId() {
		return super.getIntColumnValue(ICFileBMPBean.getColumnNameLocale());
	}

	@Override
	public void setLocale() {
		super.getIntColumnValue(ICFileBMPBean.getColumnNameLocale());
	}

	// and here are the delete functions

	@Override
	public boolean isLeaf() {
		if (com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER.equalsIgnoreCase(this.getMimeType())) {
			return false;
		}
		return true;
	}

	@Override
	public boolean getDeleted() {
		String deleted = getStringColumnValue(getColumnDeleted());

		if ((deleted == null) || (deleted.equals(NOT_DELETED))) {
			return (false);
		}
		else if (deleted.equals(DELETED)) {
			return (true);
		}
		else {
			return (false);
		}
	}

	@Override
	public void setDeleted(boolean deleted) {
		if (deleted) {
			setColumn(getColumnDeleted(), DELETED);
		} else {
			setColumn(getColumnDeleted(), NOT_DELETED);
		}
	}

	@Override
	public int getDeletedByUserId() {
		return (getIntColumnValue(getColumnDeletedBy()));
	}

	private void setDeletedByUserId(int id) {
		setColumn(getColumnDeletedBy(), id);
	}

	@Override
	public Timestamp getDeletedWhen() {
		return ((Timestamp)getColumnValue(getColumnDeletedWhen()));
	}

	private void setDeletedWhen(Timestamp when) {
		setColumn(getColumnDeletedWhen(), when);
	}

	/**
	 * Overrides the delete function because we keep all files
	 * throws every child into the trash also. Recursive function if the file has children
	 */
	@Override
	public void delete() throws SQLException {
		setDeleted(true);
		setDeletedWhen(IWTimestamp.getTimestampRightNow());
		try {
			IWContext iwc = IWContext.getInstance();
			int userId = iwc.getUserId();
			if (userId != -1) {
				setDeletedByUserId(userId);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		ICFile file = getParentNode();
		if (file != null) {
			file.removeChild(this);
		}

		Iterator<ICFile> iter = getChildrenIterator();
		if (iter != null) {
			while (iter.hasNext()) {
				ICFile item = iter.next();
				item.delete();
			}
		}

		update();

	}
	/** undeletes this file **/
	@Override
	public void unDelete(boolean setICRootAsParent) throws SQLException {
		setDeleted(false);
		setDeletedWhen(IWTimestamp.getTimestampRightNow());
		try {
			IWContext iwc = IWContext.getInstance();
			setDeletedByUserId(iwc.getUserId());
			if (setICRootAsParent) {
				IWCacheManager cm = iwc.getIWMainApplication().getIWCacheManager();
				ICFile parent = (ICFile)cm.getCachedEntity(com.idega.core.file.data.ICFileBMPBean.IC_ROOT_FOLDER_CACHE_KEY);
				if (parent != null) {
					parent.addChild(this);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		update();

	}

	/**
	 * This method delete the file from the database permenantly. Recursive function if the file has children.
	 * Use with caution!
	 */
	@Override
	public void superDelete() throws SQLException {
		ICFile file = getParentNode();
		if (file != null) {
			file.removeChild(this);
		}

		Iterator<ICFile> iter = getChildrenIterator();
		if (iter != null) {
			while (iter.hasNext()) {
				ICFile item = iter.next();
				item.superDelete();
			}
		}
		this.removeAllMetaData();
		this.update();
		super.delete();
	}

	public Integer ejbFindByFileName(String name) throws FinderException {
		Collection files = idoFindPKsBySQL("select " + getIDColumnName() + " from " + getTableName() + " where " + ICFileBMPBean.getColumnNameName() + " = '" + name + "' and (" + ICFileBMPBean.getColumnDeleted() + "='N' or " + ICFileBMPBean.getColumnDeleted() + " is null)");
		if (!files.isEmpty()) {
			return (Integer)files.iterator().next();
		}
		else {
			throw new FinderException("File was not found");
		}
	}

	public Integer ejbFindEntityOfSpecificVersion(ICVersion version) throws FinderException {
		ICVersionQuery query = new ICVersionQuery();
		query.appendFindEntityOfSpecificVersionQuery(TABLENAME_ICFILE_ICVERSION, version);

		return (Integer)this.idoFindOnePKByQuery(query);
	}

	/**
	 * @deprecated Legacy
	 * @return
	 * @throws FinderException
	 */
	@Deprecated
	public Collection ejbFindAllDescendingOrdered() throws FinderException {
		String query = "select " + getIDColumnName() + " from " + this.getTableName() + " order by " + getIDColumnName() + " desc";
		return idoFindPKsBySQL(query);
	}

	public Object ejbFindRootFolder() throws FinderException {
		ApplicationBindingDAO appDAO = ELUtil.getInstance().getBean(ApplicationBindingDAO.class);

		try {
			ApplicationBinding bind = appDAO.getSingleResultByInlineQuery(
					"from " + ApplicationBinding.class.getSimpleName() + " app where app.key = :key",
					ApplicationBinding.class,
					new Param("key", ICFileBMPBean.IC_ROOT_FOLDER_NAME)
			);
			if (bind != null) {
				return new Integer(bind.getValue());
			}
		} catch (Exception e) {}

		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(this);
		query.appendWhereEqualsQuoted(ICFileBMPBean.getColumnNameName(), ICFileBMPBean.IC_ROOT_FOLDER_NAME);
		query.appendAndEqualsQuoted(com.idega.core.file.data.ICFileBMPBean.getColumnNameMimeType(), com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);

		Object folderPK = idoFindOnePKByQuery(query);

		if (folderPK != null) {
			try {
	//			ICApplicationBinding b = bHome.create();
	//			b.setKey(com.idega.core.file.data.ICFileBMPBean.IC_ROOT_FOLDER_NAME);
	//			b.setBindingType(IC_APPLICATION_BINDING_TYPE_SYSTEM_FOLDER);
	////				b.setValue(folderPK.toString());
	//			b.store();
				appDAO.put(ICFileBMPBean.IC_ROOT_FOLDER_NAME, folderPK.toString(), IC_APPLICATION_BINDING_TYPE_SYSTEM_FOLDER);

				ICFile folder = ((ICFileHome)getEJBLocalHome()).findByPrimaryKey(folderPK);
				folder.setLocalizationKey(IC_ROOT_FOLDER_NAME);
				folder.store();
			}  catch (IDOStoreException e1) {
				e1.printStackTrace();
				throw new IDORuntimeException(e1,this);
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new IDORuntimeException(e1,this);
			}
		} else {
			getLogger().warning("Folder PK is null");
		}

		return folderPK;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICFile#isFolder()
	 */
	@Override
	public boolean isFolder() {
		if (getMimeType().equals(com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Object write(ObjectWriter writer, IWContext iwc) throws RemoteException {
		return writer.write(this, iwc);
	}

	@Override
	public Object read(ObjectReader reader, IWContext iwc) throws RemoteException {
		return reader.read(this, iwc);
	}

	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		if (getLocalizationKey() != null) {
			IWBundle bundle = iwac.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
			String defaultName = "Untitled";
			String name = bundle.getResourceBundle(locale).getLocalizedString(getLocalizationKey(),defaultName);
			return (!defaultName.equals(name))?name:getNodeName();
		} else {
			return getNodeName();
		}

	}

	public Collection ejbFindChildren(ICFile parent, Collection visibleMimeTypes, Collection hiddenMimeTypes, String orderBy) throws FinderException {
		return ejbFindChildren(parent, visibleMimeTypes, hiddenMimeTypes, orderBy, -1, -1);
	}

	public Collection ejbFindChildren(ICFile parent, Collection visibleMimeTypes, Collection hiddenMimeTypes, String orderBy, int starting, int numberOfReturns) throws FinderException {
		try {
			String thisTable = parent.getEntityDefinition().getSQLTableName();
			String treeTable = EntityControl.getTreeRelationShipTableName(parent);
			String idColumnName = parent.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
			String childIDColumnName = EntityControl.getTreeRelationShipChildColumnName(parent);
			IDOQuery buffer = idoQuery();

			if(parent.getPrimaryKey() instanceof Integer){
				buffer.append("select " ).append( thisTable ).append(".").append(getIDColumnName()).append(" from ").append( thisTable).append(",").append(treeTable).append(" where ").append(thisTable).append(".").append(idColumnName).append(" = ").append(treeTable).append(".").append(childIDColumnName).append(" and ").append(treeTable).append(".").append(idColumnName).append( " = ").append(parent.getPrimaryKey().toString());
			}
			else{//add the ' for strings, dates etc.
				buffer.append("select " ).append( thisTable ).append(".").append(getIDColumnName()).append(" from ").append( thisTable).append(",").append(treeTable).append(" where ").append(thisTable).append(".").append(idColumnName).append(" = ").append(treeTable).append(".").append(childIDColumnName).append(" and ").append(treeTable).append(".").append(idColumnName).append( " = '").append(parent.getPrimaryKey().toString()).append("'");
			}

			if(visibleMimeTypes != null && !visibleMimeTypes.isEmpty()){
				buffer.appendAnd().append(getColumnNameMimeType()).appendInForStringCollectionWithSingleQuotes(visibleMimeTypes);
			}

			if(hiddenMimeTypes != null && !hiddenMimeTypes.isEmpty()){
				buffer.appendAnd().append(getColumnNameMimeType()).appendNotInForStringCollectionWithSingleQuotes(hiddenMimeTypes);
			}


			if (orderBy != null && !orderBy.equals("")) {
				buffer.append(" order by ").append(thisTable).append( ".").append(orderBy);
			}
			//System.out.println(buffer.toString());
			return idoFindPKsBySQL( buffer.toString(), numberOfReturns, starting);

		} catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace(System.err);
			return ListUtil.getEmptyList();
		}
	}

	/**
	 * Overriding store to avoid use of an input stream that can't be read again (it has already been read by the store() method)
	 **/
	@Override
	public void store() throws IDOStoreException {
		super.store();
		BlobWrapper wrapper = getBlobColumnValue(getColumnNameFileValue());
		wrapper.setInputStreamForBlobWrite(null);
	}

	@Override
	public void setFileUri(String uri) {
		setColumn(FILE_URI_IN_SLIDE, uri);
	}

	@Override
	public String getFileUri() {
		return getStringColumnValue(FILE_URI_IN_SLIDE);
	}

	@Override
	public void addDownloadedBy(com.idega.user.data.User downloader) throws IDOAddRelationshipException {
		this.idoAddTo(downloader);
	}

	@Override
	public Collection<com.idega.user.data.User> getDownloadedBy() {
		try {
			return super.idoGetRelatedEntities(com.idega.user.data.User.class);
		} catch (IDORelationshipException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void removeDownloadedBy(com.idega.user.data.User downloader) throws IDORemoveRelationshipException {
		this.idoRemoveFrom(downloader);
	}

	@Override
	public Integer getHash() {
		return getIntegerColumnValue(COLUMN_HASH);
	}

	@Override
	public void setHash(Integer hash) {
		setValue(COLUMN_HASH, hash);
	}

	public Object ejbFindByHash(Integer hash) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));

		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_HASH), MatchCriteria.EQUALS, hash));

		return this.idoFindOnePKByQuery(query);
	}
}
