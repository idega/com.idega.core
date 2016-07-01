/*
 * $Id: ICPageBMPBean.java,v 1.17 2009/01/14 15:12:24 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.core.builder.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.dao.IBPageNameDAO;
import com.idega.core.file.data.ICFile;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.net.data.ICProtocol;
import com.idega.core.user.data.User;
import com.idega.data.GenericEntity;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.data.TreeableEntityBMPBean;
import com.idega.data.UniqueIDCapable;
import com.idega.data.query.Column;
import com.idega.data.query.Criteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.OR;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.idegaweb.IWUserContext;
import com.idega.io.serialization.ObjectReader;
import com.idega.io.serialization.ObjectWriter;
import com.idega.io.serialization.Storable;
import com.idega.presentation.IWContext;
import com.idega.repository.data.Resource;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.expression.ELUtil;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
 */
public class ICPageBMPBean extends TreeableEntityBMPBean<ICPage> implements ICPage, Storable, Resource, UniqueIDCapable {

	private static final long serialVersionUID = 5624201999388048294L;

	public final static String ENTITY_NAME = "IB_PAGE";
	private final static String FILE_COLUMN = "FILE_ID";
	private final static String NAME_COLUMN = "NAME";
	private final static String TEMPLATE_ID_COLUMN = "TEMPLATE_ID";
	private final static String TYPE_COLUMN = "PAGE_TYPE";
	private final static String SUBTYPE_COLUMN = "PAGE_SUB_TYPE";
	private final static String LOCKED_COLUMN = "LOCKED_BY";
	private final static String DELETED_COLUMN = "DELETED";
	private final static String DELETED_BY_COLUMN = "DELETED_BY";
	private final static String DELETED_WHEN_COLUMN = "DELETED_WHEN";
	private final static String TREE_ORDER = "TREE_ORDER";
	public final static String IS_CATEGORY = "IS_CATEGORY";
	private final static String PAGE_FORMAT="PAGE_FORMAT";
	private final static String PAGE_URI="PAGE_URI";
	private static final String DOMAIN_ID = "IB_DOMAIN_ID";
	private static final String WEBDAV_URI = "WEBDAV_URI";
	//private static final String IS = "IS";
	private static final String NULL = "NULL";
	public static final String HIDE_PAGE_IN_MENU = "HIDE_PAGE_IN_MENU";
	public static final String PAGE_IS_PUBLISHED = "PAGE_IS_PUBLISHED";
	public static final String PAGE_IS_LOCKED = "PAGE_IS_LOCKED";
	private ICFile _file;


	public final static String PAGE = "P";
	public final static String TEMPLATE = "T";
	public final static String DRAFT = "D";
	public final static String FOLDER = "F";
	public final static String DPT_TEMPLATE = "A";
	public final static String DPT_PAGE = "B";
	public final static String SUBTYPE_SIMPLE_TEMPLATE = "SIMPLE_TEMPLATE";
	public final static String SUBTYPE_SIMPLE_TEMPLATE_PAGE = "SIMPLE_TEMPLATE_PAGE";

	public final static String DELETED = CoreConstants.Y;
	public final static String NOT_DELETED = "N";

	public static final String	FORMAT_IBXML = "IBXML",
								FORMAT_IBXML2 = "IBXML2",
								FORMAT_HTML = "HTML",
								FORMAT_JSP_1_2 = "JSP_1_2",
								FORMAT_FACELET = "FACELET";

	/**
	 *
	 */
	public ICPageBMPBean() {
		super();
	}

	/**
	 *
	 */
	public ICPageBMPBean(int id) throws SQLException {
		super(id);
	}

	/**
	 *
	 */
	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(getColumnName(), "Nafn", true, true, String.class);
		addAttribute(getColumnFile(), "File", true, true, Integer.class, "many-to-one", ICFile.class);
		addAttribute(getColumnTemplateID(), "Template", true, true, Integer.class, "many-to-one", ICPage.class);
		addAttribute(getColumnType(), "Type", true, true, String.class, 1);
		addAttribute(getColumnSubType(), "Sub type", true, true, String.class);
		addAttribute(getColumnLockedBy(), "Locked by", true, true, Integer.class, "many-to-one", User.class);
		addAttribute(getColumnDeleted(), "Deleted", true, true, String.class, 1);
		addAttribute(getColumnDeletedBy(), "Deleted by", true, true, Integer.class, "many-to-one", User.class);
		addAttribute(getColumnDeletedWhen(), "Deleted when", true, true, Timestamp.class);
		addAttribute(getColumnTreeOrder(), "Ordering of pages in a level in the page tree", true, true, Integer.class);
		addAttribute(IS_CATEGORY, "Is used as a page category", true, true, Boolean.class);
		addManyToManyRelationShip(ICProtocol.class, "ib_page_ic_protocol");
		addAttribute(PAGE_FORMAT, "Format", true, true, String.class, 30);
		addAttribute(PAGE_URI, "URI", String.class);
		addAttribute(HIDE_PAGE_IN_MENU, "Hide page in navigation menu", true, true, Boolean.class);
		addAttribute(PAGE_IS_PUBLISHED, "Set page published/unpublished", true, true, Boolean.class);
		addAttribute(PAGE_IS_LOCKED, "Set page locked/unlocked", true, true, Boolean.class);

		addManyToOneRelationship(DOMAIN_ID,ICDomain.class);
		addUniqueIDColumn();
		addIndex(getUniqueIdColumnName());

		addAttribute(WEBDAV_URI, "Repository path", String.class);

		addIndex("idx_page_uri",PAGE_URI);

	}

	/**
	 *
	 */
	@Override
	public void insertStartData() throws Exception {
	}

	/**
	 *
	 */
	@Override
	public String getEntityName() {
		return (ENTITY_NAME);
	}

	/**
	 *
	 */
	@Override
	public void setDefaultValues() {
		//setColumn("image_id",1);
	}

	/**
	 *
	 */
	@Override
	public String getName() {
		return (getStringColumnValue(getColumnName()));
	}

	@Override
	public String getName(Locale locale) {
		/*int localeID = ICLocaleBusiness.getLocaleId(locale);
		try {
			IBPageNameHome home = (IBPageNameHome) com.idega.data.IDOLookup.getHome(IBPageName.class);
			IBPageName name = home.findByPageIdAndLocaleId(((Integer) getPrimaryKey()).intValue(), localeID);
			return name.getPageName();
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			//Nothing found...
		}
		*/
		return getName();
	}
	
	@Autowired
	IBPageNameDAO ibPageNameDAO;
	
	public IBPageNameDAO getIBPageNameDAO() {
		if(this.ibPageNameDAO == null) {
			this.ibPageNameDAO = ELUtil.getInstance().getBean("ibPageNameDAO");
		}
		return this.ibPageNameDAO;
	}
	
	public String getNameByCurrentLocale() {
		return getIBPageNameDAO().getNameByPageAndLocale(getID(),
				ICLocaleBusiness.getLocaleId(CoreUtil.getCurrentLocale()));
	}

	/**
	 *
	 */
	@Override
	public void setName(String name) {
		setColumn(getColumnName(), name);
	}

	/**
	 *
	 */
	@Override
	public int getTemplateId() {
		return (getIntColumnValue(getColumnTemplateID()));
	}

	/**
	 *
	 */
	@Override
	public void setTemplateId(int id) {
		setColumn(getColumnTemplateID(), id);
	}

	/**
	 *
	 */
	@Override
	public String getType() {
		return (getStringColumnValue(getColumnType()));
	}

	/**
	 *
	 */
	@Override
	public String getSubType() {
		return (getStringColumnValue(getColumnSubType()));
	}

	/**
	 *
	 */
	@Override
	public int getLockedBy() {
		return (getIntColumnValue(getColumnLockedBy()));
	}

	/**
	 *
	 */
	@Override
	public void setLockedBy(int id) {
		setColumn(getColumnLockedBy(), id);
	}

	/**
	 *
	 */
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
	public boolean isCategory() {
		return getBooleanColumnValue(IS_CATEGORY, false);
	}

	@Override
	public void setIsCategory(boolean isCategory) {
		setColumn(IS_CATEGORY, isCategory);
	}

	/**
	 *
	 */
	@Override
	public void setDeleted(boolean deleted) {
		if (deleted) {
			setColumn(getColumnDeleted(), DELETED);
			setDeletedWhen(IWTimestamp.getTimestampRightNow());
			//      setDeletedBy(iwc.getUserId());
		} else {
			setColumn(getColumnDeleted(), NOT_DELETED);
			//      setDeletedBy(-1);
			//      setDeletedWhen(null);
		}
	}

	/**
	 *
	 */
	@Override
	public int getDeletedBy() {
		return (getIntColumnValue(getColumnDeletedBy()));
	}

	/**
	 *
	 */
	private void setDeletedBy(int id) {
		//    if (id == -1)
		//      setColumn(getColumnDeletedBy(),(Object)null);
		//    else
		setColumn(getColumnDeletedBy(), id);
	}

	/**
	 *
	 */
	@Override
	public Timestamp getDeletedWhen() {
		return ((Timestamp)getColumnValue(getColumnDeletedWhen()));
	}

	/**
	 *
	 */
	private void setDeletedWhen(Timestamp when) {
		setColumn(getColumnDeletedWhen(), when);
	}

	/**
	 *
	 */
	@Override
	public void setType(String type) {
		if ((type.equals(PAGE)) || (type.equals(TEMPLATE)) || (type.equals(DRAFT)) || (type.equals(DPT_TEMPLATE)) || (type.equals(DPT_PAGE))) {
			setColumn(getColumnType(), type);
		}
	}

	/**
	 *
	 */
	@Override
	public void setSubType(String type) {
		setColumn(getColumnSubType(), type);
	}

	/*
	 *
	 */
	private int getFileID() {
		return (getIntColumnValue(getColumnFile()));
	}

	/**
	 * Gets the file
	 */
	@Override
	public ICFile getFile() {
		// if we already have an instance of the file we do not
		// want to loose it, especially not if a filevalue has been
		// written to it, else the filevalue gets lost.
		if(this._file==null){
			int fileID = getFileID();
			if ( fileID != -1) {
				this._file = (ICFile)getColumnValue(getColumnFile());
			}
		}
		return (this._file);
	}

	/**
	 *
	 */
	@Override
	public void setFile(ICFile file) {
		file.setMimeType(com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_XML);
		setColumn(getColumnFile(), file);
		this._file = file;
	}

	/**
	 *
	 */
	@Override
	public void setPageValue(InputStream stream) {
		ICFile file = getFile();
		if (file == null) {
			try {
				file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();
				setFile(file);
			} catch (IDOLookupException e) {
				e.printStackTrace();
			} catch (CreateException e) {
				e.printStackTrace();
			}

		}
		file.setFileValue(stream);
	}

	/**
	 *
	 */
	@Override
	public InputStream getPageValue() {
		try {
			ICFile file = getFile();
			if (file != null) {
				return (file.getFileValue());
			}
		} catch (Exception e) {
		}

		return (null);
	}

	/**
	 *
	 */
	@Override
	public OutputStream getPageValueForWrite() {
		ICFile file = getFile();
		if (file == null) {
			try {
				file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();
				setFile(file);
			} catch (IDOLookupException e) {
				e.printStackTrace();
			} catch (CreateException e) {
				e.printStackTrace();
			}
		}
		OutputStream theReturn = file.getFileValueForWrite();

		return (theReturn);
	}

	/**
	 *
	 */
	public static String getColumnName() {
		return (NAME_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnTemplateID() {
		return (TEMPLATE_ID_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnFile() {
		return (FILE_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnType() {
		return (TYPE_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnSubType() {
		return (SUBTYPE_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnLockedBy() {
		return (LOCKED_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnDeleted() {
		return (DELETED_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnDeletedBy() {
		return (DELETED_BY_COLUMN);
	}

	/**
	 *
	 */
	public static String getColumnDeletedWhen() {
		return (DELETED_WHEN_COLUMN);
	}

	/**
	 *
	 */
	@Override
	public synchronized void update() throws SQLException {
		ICFile file = getFile();
		if (file != null) {
			try {
				if (file.getPrimaryKey() == null) {
					file.store();
					file.setName(this.getName());
					file.setMimeType("text/xml");
					setFile(file);
				} else {
					file.setName(this.getName());
					file.setMimeType("text/xml");
					file.store();
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		} else {
			System.out.println("IBPage, file == null in update");
		}
		super.update();
	}

	/**
	 *
	 */
	@Override
	public void insert() throws SQLException {
		ICFile file = getFile();
		if (file != null) {
			//System.out.println("file != null in insert");
			file.store();
			setFile(file);
		} else {
			//System.out.println("file == null in insert");
		}
		super.insert();
	}

	/**
	 *
	 */
	@Override
	public void delete() throws SQLException {
		throw new SQLException("Use delete(int userId) instead");
	}

	/**
	 *
	 */
	@Override
	public void delete(int userId) throws SQLException {
		setColumn(getColumnDeleted(), DELETED);
		setDeletedWhen(IWTimestamp.getTimestampRightNow());
		setDeletedBy(userId);

		super.update();
	}

	/**
	 *
	 */
	@Override
	public void setIsPage() {
		setType(PAGE);
	}

	/**
	 *
	 */
	@Override
	public void setIsTemplate() {
		setType(TEMPLATE);
	}

	/**
	 *
	 */
	@Override
	public void setIsDraft() {
		setType(DRAFT);
	}

	/**
	 *
	 */
	@Override
	public void setIsFolder() {
		setType(FOLDER);
	}

	/**
	 *
	 */
	@Override
	public boolean isPage() {
		String type = getType();
		if (type.equals(PAGE)) {
			return (true);
		}
		else {
			return (false);
		}
	}

	/**
	 *
	 */
	@Override
	public boolean isTemplate() {
		String type = getType();
		if (type.equals(TEMPLATE)) {
			return (true);
		}
		else {
			return (false);
		}
	}

	/**
	 *
	 */
	@Override
	public boolean isDraft() {
		String type = getType();
		if (type.equals(DRAFT)) {
			return (true);
		}
		else {
			return (false);
		}
	}

	/**
	 *
	 */
	@Override
	public boolean isFolder() {
		String type = getType();
		if (type.equals(FOLDER)) {
			return (true);
		}
		else {
			return (false);
		}
	}

	/**
	 *
	 */
	@Override
	public boolean isDynamicTriggeredPage() {
		String type = getType();
		if (type.equals(DPT_PAGE)) {
			return (true);
		}
		else {
			return (false);
		}
	}

	/**
	 *
	 */
	@Override
	public boolean isDynamicTriggeredTemplate() {
		String type = getType();
		if (type.equals(DPT_TEMPLATE)) {
			return (true);
		}
		else {
			return (false);
		}
	}

	/**
	 *
	 */
	@Override
	public boolean isLeaf() {
		if (getType().equals(FOLDER)) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public void setOwner(IWUserContext iwuc) {
		try {
			iwuc.getApplicationContext().getIWMainApplication().getAccessController().setCurrentUserAsOwner(this, iwuc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setTreeOrder(int order) {
		setColumn(TREE_ORDER, order);
	}

	@Override
	public void setTreeOrder(Integer order) {
		setColumn(TREE_ORDER, order);
	}

	@Override
	public int getTreeOrder() {
		return getIntColumnValue(TREE_ORDER);
	}

	public static String getColumnTreeOrder() {
		return (TREE_ORDER);
	}

	@Override
	public Object write(ObjectWriter writer, IWContext iwc) throws RemoteException {
		return BuilderServiceFactory.getBuilderPageWriterService(iwc).write(this, writer, iwc);
	}

	@Override
	public Object read(ObjectReader reader, IWContext iwc) throws RemoteException {
		return reader.read(this, iwc);
	}

	@Override
	public void setFormat(String format){
		this.setColumn(PAGE_FORMAT,format);
	}

	@Override
	public String getFormat(){
		String format = getStringColumnValue(PAGE_FORMAT);
		//This is to maintain backwards compatabilty, default is IBXML:
		if(format==null) {
			format=FORMAT_IBXML;
		}
		setFormat(format);
		return format;
	}

	@Override
	public boolean getIsFormattedInIBXML(){
		String format = getFormat();
		if(format!=null){
			return format.equals(FORMAT_IBXML);
		}
		return true;
	}

	@Override
	public boolean getIsFormattedInIBXML2(){
		String format = getFormat();
		if(format!=null){
			return format.equals(FORMAT_IBXML2);
		}
		return true;
	}

	@Override
	public boolean getIsFormattedInHTML(){
		String format = getFormat();
		if(format!=null){
			return format.equals(FORMAT_HTML);
		}
		return false;
	}

	@Override
	public boolean getIsFormattedInJSP(){
		String format = getFormat();
		if(format!=null){
			return format.equals(FORMAT_JSP_1_2);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#getEntityState()
	 */
	@Override
	public int getEntityState() {
		//we need to override this method and also check if the embedded ICFile has been changed, if it has changed we need generic entity to
		//update the bean
		ICFile file = getFile();
		if ( file!=null && (((GenericEntity)file).getEntityState() == STATE_NOT_IN_SYNCH_WITH_DATASTORE) ){
			this.setEntityState(STATE_NOT_IN_SYNCH_WITH_DATASTORE);
		}

		return super.getEntityState();
	}

	public Collection ejbFindByTemplate(Integer templateID)throws javax.ejb.FinderException{
	    	Table table = new Table(this);
	    	SelectQuery query = new SelectQuery(table);
	    	query.addColumn(new Column(table, getIDColumnName()));
	    	query.addCriteria(new MatchCriteria(table,getColumnTemplateID(),MatchCriteria.EQUALS,templateID));
	    	return idoFindPKsByQuery(query);
	}

	public Integer ejbFindByPageUri(String pageUri,int domainId)throws javax.ejb.FinderException{
	    	Table table = new Table(this);
	    	SelectQuery query = new SelectQuery(table);
	    	query.addColumn(new Column(table, getIDColumnName()));
	    	query.addCriteria(new MatchCriteria(table,PAGE_URI,MatchCriteria.EQUALS,pageUri));
	    	//query.addCriteria(new MatchCriteria(table,DOMAIN_ID,MatchCriteria.EQUALS,domainId));
	    	return (Integer)idoFindOnePKByQuery(query);
	}

	@Override
	public Collection ejbFindBySubType(String subType, boolean deleted)  throws javax.ejb.FinderException {

		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
    	query.addColumn(new Column(table, getIDColumnName()));
    	query.addCriteria(new MatchCriteria(table, getColumnSubType(), MatchCriteria.EQUALS, subType));

    	if(deleted)
    		query.addCriteria(new MatchCriteria(table, getColumnDeleted(), MatchCriteria.ISNOT, MatchCriteria.NULL));
    	else
    		query.addCriteria(new MatchCriteria(table, getColumnDeleted(), MatchCriteria.IS, MatchCriteria.NULL));

    	return idoFindPKsByQuery(query);
	}

	public Integer ejbFindExistingPageByPageUri(String pageUri,int domainId)throws javax.ejb.FinderException{
	    Table table = new Table(this);
	    	SelectQuery query = new SelectQuery(table);
	    	query.addColumn(new Column(table, getIDColumnName()));
	    	query.addCriteria(new MatchCriteria(table,PAGE_URI,MatchCriteria.EQUALS,pageUri));
	    	query.addCriteria(new MatchCriteria(table,DELETED_COLUMN,MatchCriteria.IS,NULL));
	    	//query.addCriteria(new MatchCriteria(table,DOMAIN_ID,MatchCriteria.EQUALS,domainId));
	    	return (Integer)idoFindOnePKByQuery(query);
	}

	public Integer ejbFindByWebDavUri(String webDavUri) throws FinderException {
		Table table = new Table(this);
    	SelectQuery query = new SelectQuery(table);
    	query.addColumn(new Column(table, getIDColumnName()));
    	query.addCriteria(new MatchCriteria(table, WEBDAV_URI, MatchCriteria.EQUALS, webDavUri));
    	query.addCriteria(new MatchCriteria(table, DELETED_COLUMN, MatchCriteria.IS, NULL));
    	return (Integer) idoFindOnePKByQuery(query);
	}

	/**
	 * Gets the id/key of the template of this page as a String.
	 * Returns null if no template is set (templateId<=0)
	 * @return
	 */
	@Override
	public String getTemplateKey() {
		if(getTemplateId()>0){
			return Integer.toString(getTemplateId());
		}
		else{
			return null;
		}
	}

	/**
	 * Sets the id/key of the template of this page as a String
	 * @return
	 */
	@Override
	public void setTemplateKey(String templateKey) {
		setTemplateId(Integer.parseInt(templateKey));
	}
	/**
	 * Gets the id/key of the page as a String
	 * @return
	 */
	@Override
	public String getPageKey(){
		return getPrimaryKey().toString();
	}


	@Override
	public String getDefaultPageURI(){
		String uri = getStringColumnValue(PAGE_URI);
		return uri;
	}

	@Override
	public void setDefaultPageURI(String pageUri){
		setColumn(PAGE_URI,pageUri);
	}

	@Override
	public String getWebDavUri(){
		String uri = getStringColumnValue(WEBDAV_URI);
		return uri;
	}

	@Override
	public void setWebDavUri(String fileUri){
		setColumn(WEBDAV_URI,fileUri);
	}

	@Override
	public ICDomain getDomain(){
		ICDomain domain = (ICDomain) getColumnValue(DOMAIN_ID);
		return domain;
	}

	@Override
	public int getDomainId(){
		int domainId = getIntColumnValue(DOMAIN_ID);
		return domainId;
	}

	@Override
	public void setDomain(ICDomain domain){
		setColumn(DOMAIN_ID,domain);
	}

	/**
	 * @return
	 */
	public Collection ejbFindAllPagesWithoutUri() throws FinderException{
	    Table table = new Table(this);
	    	SelectQuery query = new SelectQuery(table);
	    	query.addColumn(new Column(table, getIDColumnName()));
	    	query.addCriteria(new MatchCriteria(table,PAGE_URI,MatchCriteria.IS,(String)null));
	    	return idoFindPKsByQuery(query);
	}

	public Collection<?> ejbFindAllTemplatesWithWebDavUri() throws FinderException {
		Table table = new Table(this);
    	SelectQuery query = new SelectQuery(table);
    	query.addColumn(new Column(table, getIDColumnName()));
    	query.addCriteria(new MatchCriteria(table, TYPE_COLUMN, MatchCriteria.EQUALS, TEMPLATE));

    	query.addCriteria(new MatchCriteria(table, WEBDAV_URI, MatchCriteria.LIKE, "%/themes/%"));

    	Criteria isNull = new MatchCriteria(new Column(table, DELETED_COLUMN), MatchCriteria.IS, MatchCriteria.NULL);
		Criteria isFalse = new MatchCriteria(new Column(table, DELETED_COLUMN), MatchCriteria.EQUALS, false);
    	query.addCriteria(new OR(isNull, isFalse));
    	return idoFindPKsByQuery(query);
	}

	/**
	 * @return
	 */
	public Collection ejbFindAllSimpleTemplates() throws FinderException{
	    SelectQuery query = idoSelectQuery();
	    query.addCriteria(new MatchCriteria(idoQueryTable(),getColumnSubType(),MatchCriteria.LIKE,SUBTYPE_SIMPLE_TEMPLATE));
	    	return idoFindPKsByQuery(query);
	}

	@Override
	public boolean isHidePageInMenu() {
		return getBooleanColumnValue(HIDE_PAGE_IN_MENU);
	}

	@Override
	public void setHidePageInMenu(boolean hidePageInMenu) {
		setColumn(HIDE_PAGE_IN_MENU, hidePageInMenu);
	}

	@Override
	public boolean isPublished() {
		return getBooleanColumnValue(PAGE_IS_PUBLISHED);
	}

	@Override
	public void setPublished(boolean published) {
		setColumn(PAGE_IS_PUBLISHED, published);
	}

	@Override
	public boolean isLocked() {
		return getBooleanColumnValue(PAGE_IS_LOCKED);
	}

	@Override
	public void setLocked(boolean locked) {
		setColumn(PAGE_IS_LOCKED, locked);
	}

	public Collection ejbFindAllByPhrase(String phrase, List<String> idsToAvoid) throws FinderException {
		IDOQuery query = idoQuery("select ").append(getIDColumnName()).append(" from ").append(getEntityName()).appendWhere().append("lower(").append(NAME_COLUMN);
		query.append(")").appendLike().appendSingleQuote().append(CoreConstants.PERCENT).append(phrase.toLowerCase()).append(CoreConstants.PERCENT).appendSingleQuote();
		if (idsToAvoid != null && !idsToAvoid.isEmpty()) {
			query.appendAnd().append(getIDColumnName()).appendNotInCollection(idsToAvoid);
		}
		return idoFindPKsByQuery(query);
	}

	public Collection ejbFindAllByPrimaryKeys(List<String> primaryKeys) throws FinderException {
		IDOQuery query = idoQuery("select ").append(getIDColumnName()).append(" from ").append(getEntityName());
		query.appendWhere(getIDColumnName()).appendInCollection(primaryKeys);
		return idoFindPKsByQuery(query);
	}

	public Collection ejbFindAllByName(String name, boolean findOnlyNotDeleted) throws FinderException {
		IDOQuery query = idoQuery("select ").append(getIDColumnName()).append(" from ").append(getEntityName());
		query.appendWhere(NAME_COLUMN).appendEqualSign().appendSingleQuote().append(name).appendSingleQuote();

		if (findOnlyNotDeleted) {
			query.appendAnd().append(DELETED_COLUMN).appendNOTEqual().appendSingleQuote().append(CoreConstants.Y).appendSingleQuote();
		}

		return idoFindPKsByQuery(query);
	}

	@Override
	public boolean getIsFormattedInFacelet() {
		String format = getFormat();
		if(format!=null){
			return format.equals(FORMAT_FACELET);
		}
		return false;
	}

	public Collection ejbFindAllPagesAndTemplates() throws FinderException {
	    Table table = new Table(this);
    	SelectQuery query = new SelectQuery(table);
    	query.addColumn(new Column(table, getIDColumnName()));
    	return idoFindPKsByQuery(query);
	}

	/**
	 * 
	 * @return all {@link ICPage}s, that is not deleted or {@link Collections#emptyList()} on failure;
	 */
	public Collection<Integer> ejbFindAll() {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(getEntityName());
		sql.append(" where (");
		sql.append(ICPageBMPBean.getColumnType());
		sql.append(" = '");
		sql.append(ICPageBMPBean.PAGE);
		sql.append("' or ");
		sql.append(ICPageBMPBean.getColumnType());
		sql.append(" = '");
		sql.append(ICPageBMPBean.DPT_PAGE);
		sql.append("') and (");
		sql.append(ICPageBMPBean.getColumnDeleted());
		sql.append(" = '");
		sql.append(ICPageBMPBean.NOT_DELETED);
		sql.append("' or ");
		sql.append(ICPageBMPBean.getColumnDeleted());
		sql.append(" is null)");

		sql.append(" order by ").append(ICPageBMPBean.getColumnTreeOrder())
		.append(", ").append(getIDColumnName()).append(" desc");

		try {
			return idoFindPKsBySQL(sql.toString());
		} catch (FinderException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, "Failed to get results by query: '" + sql.toString() + "'");
		}

		return Collections.emptyList();
	}
}