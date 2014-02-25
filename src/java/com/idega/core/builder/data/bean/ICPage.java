/**
 *
 */
package com.idega.core.builder.data.bean;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.data.ICPageBMPBean;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.bean.ICFile;
import com.idega.core.idgenerator.business.IdGenerator;
import com.idega.core.idgenerator.business.IdGeneratorFactory;
import com.idega.core.net.data.bean.ICProtocol;
import com.idega.data.UniqueIDCapable;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.io.serialization.ObjectReader;
import com.idega.io.serialization.ObjectWriter;
import com.idega.io.serialization.Storable;
import com.idega.presentation.IWContext;
import com.idega.repository.data.Resource;
import com.idega.user.data.bean.User;
import com.idega.util.xml.XMLData;

@Entity
@Table(name = ICPage.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "page.findAll", query = "select p from ICPage p")
})
public class ICPage implements Serializable, UniqueIDCapable, Storable, Resource, ICTreeNode<ICPage> {

	private static final long serialVersionUID = -8452915401731298916L;

	public static final String ENTITY_NAME = "ib_page";
	public static final String COLUMN_PAGE_ID = "ib_page_id";

	private static final String COLUMN_UNIQUE_ID = "unique_id";
	private static final String COLUMN_FILE = "file_id";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_TEMPLATE_ID = "template_id";
	private static final String COLUMN_TYPE = "page_type";
	private static final String COLUMN_SUBTYPE = "page_sub_type";
	private static final String COLUMN_LOCKED_BY = "locked_by";
	private static final String COLUMN_DELETED = "deleted";
	private static final String COLUMN_DELETED_BY = "deleted_by";
	private static final String COLUMN_DELETED_WHEN = "deleted_when";
	private static final String COLUMN_TREE_ORDER = "tree_order";
	private static final String COLUMN_IS_CATEGORY = "is_category";
	private static final String COLUMN_PAGE_FORMAT = "page_format";
	private static final String COLUMN_PAGE_URI = "page_uri";
	private static final String COLUMN_DOMAIN_ID = "ib_domain_id";
	private static final String COLUMN_WEBDAV_URI = "webdav_uri";
	private static final String COLUMN_HIDE_PAGE_IN_MENU = "hide_page_in_menu";
	private static final String COLUMN_PAGE_IS_PUBLISHED = "page_is_published";
	private static final String COLUMN_PAGE_IS_LOCKED = "page_is_locked";

	public static final String PAGE = "P";
	public static final String TEMPLATE = "T";
	public static final String DRAFT = "D";
	public static final String FOLDER = "F";
	public static final String DPT_TEMPLATE = "A";
	public static final String DPT_PAGE = "B";
	public static final String SUBTYPE_SIMPLE_TEMPLATE = "SIMPLE_TEMPLATE";
	public static final String SUBTYPE_SIMPLE_TEMPLATE_PAGE = "SIMPLE_TEMPLATE_PAGE";

	public static final String FORMAT_IBXML = ICPageBMPBean.FORMAT_IBXML;
	public static final String FORMAT_IBXML2 = ICPageBMPBean.FORMAT_IBXML2;
	public static final String FORMAT_HTML = ICPageBMPBean.FORMAT_HTML;
	public static final String FORMAT_JSP_1_2 = ICPageBMPBean.FORMAT_JSP_1_2;
	public static final String FORMAT_FACELET = ICPageBMPBean.FORMAT_FACELET;

	public static final String SQL_RELATION_PAGE_TREE = "ib_page_tree";
	public static final String SQL_RELATION_PROTOCOL = "ib_page_ic_protocol";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_PAGE_ID)
	private Integer pageID;

	@Column(name = COLUMN_UNIQUE_ID, length = 36, nullable = false, unique = true)
	private String uniqueID;

	@Column(name = COLUMN_NAME)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = COLUMN_FILE)
	private ICFile file;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_TEMPLATE_ID)
	private ICPage template;

	@Column(name = COLUMN_TYPE, length = 1)
	private String type;

	@Column(name = COLUMN_SUBTYPE)
	private String subType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_LOCKED_BY)
	private User lockedBy;

	@Column(name = COLUMN_DELETED, length = 1)
	private Character isDeleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_DELETED_BY)
	private User deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_DELETED_WHEN)
	private Date deletedWhen;

	@Column(name = COLUMN_TREE_ORDER)
	private int treeOrder;

	@Column(name = COLUMN_IS_CATEGORY, length = 1)
	private Character isCategory;

	@Column(name = COLUMN_PAGE_FORMAT, length = 30)
	private String pageFormat;

	@Column(name = COLUMN_PAGE_URI)
	private String pageURI;

	@Column(name = COLUMN_WEBDAV_URI)
	private String webDavURI;

	@Column(name = COLUMN_HIDE_PAGE_IN_MENU, length = 1)
	private Character hidePageInMenu;

	@Column(name = COLUMN_PAGE_IS_PUBLISHED, length = 1)
	private Character isPublished;

	@Column(name = COLUMN_PAGE_IS_LOCKED, length = 1)
	private Character isLocked;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_DOMAIN_ID)
	private ICDomain domain;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = ICProtocol.class)
	@JoinTable(name = SQL_RELATION_PROTOCOL, joinColumns = { @JoinColumn(name = COLUMN_PAGE_ID) }, inverseJoinColumns = { @JoinColumn(name = ICProtocol.COLUMN_PROTOCOL_ID) })
	private List<ICProtocol> protocols;

	@ManyToOne(optional = true)
	@JoinTable(name = SQL_RELATION_PAGE_TREE, joinColumns = { @JoinColumn(name = "child_" + COLUMN_PAGE_ID, referencedColumnName = COLUMN_PAGE_ID) }, inverseJoinColumns = { @JoinColumn(name = COLUMN_PAGE_ID) })
	private ICPage parent;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = ICPage.class)
	@JoinTable(name = SQL_RELATION_PAGE_TREE, joinColumns = { @JoinColumn(name = COLUMN_PAGE_ID) }, inverseJoinColumns = { @JoinColumn(name = "child_" + COLUMN_PAGE_ID, referencedColumnName = COLUMN_PAGE_ID) })
	private List<ICPage> children;

	@PrePersist
	@PreUpdate
	public void setDefaultValues() {
		if (getUniqueId() == null) {
			IdGenerator uidGenerator = IdGeneratorFactory.getUUIDGenerator();
			setUniqueId(uidGenerator.generateId());
		}
	}

	/**
	 * @return the pageID
	 */
	public Integer getID() {
		return this.pageID;
	}

	/**
	 * @param pageID
	 *          the pageID to set
	 */
	public void setID(Integer pageID) {
		this.pageID = pageID;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *          the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the file
	 */
	public ICFile getFile() {
		return this.file;
	}

	/**
	 * @param file
	 *          the file to set
	 */
	public void setFile(ICFile file) {
		this.file = file;
	}

	/**
	 * @return the template
	 */
	public ICPage getTemplate() {
		return this.template;
	}

	/**
	 * @param template
	 *          the template to set
	 */
	public void setTemplate(ICPage template) {
		this.template = template;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type
	 *          the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the subType
	 */
	public String getSubType() {
		return this.subType;
	}

	/**
	 * @param subType
	 *          the subType to set
	 */
	public void setSubType(String subType) {
		this.subType = subType;
	}

	/**
	 * @return the lockedBy
	 */
	public User getLockedBy() {
		return this.lockedBy;
	}

	/**
	 * @param lockedBy
	 *          the lockedBy to set
	 */
	public void setLockedBy(User lockedBy) {
		this.lockedBy = lockedBy;
	}

	/**
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		if (this.isDeleted == null) {
			return false;
		}
		return this.isDeleted == 'Y';
	}

	/**
	 * @param isDeleted
	 *          the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted ? 'Y' : 'N';
	}

	/**
	 * @return the deletedBy
	 */
	public User getDeletedBy() {
		return this.deletedBy;
	}

	/**
	 * @param deletedBy
	 *          the deletedBy to set
	 */
	public void setDeletedBy(User deletedBy) {
		this.deletedBy = deletedBy;
	}

	/**
	 * @return the deletedWhen
	 */
	public Date getDeletedWhen() {
		return this.deletedWhen;
	}

	/**
	 * @param deletedWhen
	 *          the deletedWhen to set
	 */
	public void setDeletedWhen(Date deletedWhen) {
		this.deletedWhen = deletedWhen;
	}

	/**
	 * @return the treeOrder
	 */
	public int getTreeOrder() {
		return this.treeOrder;
	}

	/**
	 * @param treeOrder
	 *          the treeOrder to set
	 */
	public void setTreeOrder(int treeOrder) {
		this.treeOrder = treeOrder;
	}

	/**
	 * @return the isCategory
	 */
	public boolean isCategory() {
		if (this.isCategory == null) {
			return false;
		}
		return this.isCategory == 'Y';
	}

	/**
	 * @param isCategory
	 *          the isCategory to set
	 */
	public void setCategory(boolean isCategory) {
		this.isCategory = isCategory ? 'Y' : 'N';
	}

	/**
	 * @return the pageFormat
	 */
	public String getPageFormat() {
		return this.pageFormat;
	}

	/**
	 * @param pageFormat
	 *          the pageFormat to set
	 */
	public void setPageFormat(String pageFormat) {
		this.pageFormat = pageFormat;
	}

	/**
	 * @return the pageURI
	 */
	public String getPageURI() {
		return this.pageURI;
	}

	/**
	 * @param pageURI
	 *          the pageURI to set
	 */
	public void setPageURI(String pageURI) {
		this.pageURI = pageURI;
	}

	/**
	 * @return the webDavURI
	 */
	public String getWebDavURI() {
		return this.webDavURI;
	}

	/**
	 * @param webDavURI
	 *          the webDavURI to set
	 */
	public void setWebDavURI(String webDavURI) {
		this.webDavURI = webDavURI;
	}

	/**
	 * @return the hidePageInMenu
	 */
	public boolean isHidePageInMenu() {
		if (this.hidePageInMenu == null) {
			return false;
		}
		return this.hidePageInMenu == 'Y';
	}

	/**
	 * @param hidePageInMenu
	 *          the hidePageInMenu to set
	 */
	public void setHidePageInMenu(boolean hidePageInMenu) {
		this.hidePageInMenu = hidePageInMenu ? 'Y' : 'N';
	}

	/**
	 * @return the isPublished
	 */
	public boolean isPublished() {
		if (this.isPublished == null) {
			return false;
		}
		return this.isPublished == 'Y';
	}

	/**
	 * @param isPublished
	 *          the isPublished to set
	 */
	public void setPublished(Character isPublished) {
		this.isPublished = isPublished;
	}

	/**
	 * @return the isLocked
	 */
	public boolean isLocked() {
		if (this.isLocked == null) {
			return false;
		}
		return this.isLocked == 'Y';
	}

	/**
	 * @param isLocked
	 *          the isLocked to set
	 */
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked ? 'Y' : 'N';
	}

	/**
	 * @return the domain
	 */
	public ICDomain getDomain() {
		return this.domain;
	}

	/**
	 * @param domain
	 *          the domain to set
	 */
	public void setDomain(ICDomain domain) {
		this.domain = domain;
	}

	/**
	 * @return the protocols
	 */
	public List<ICProtocol> getProtocols() {
		return this.protocols;
	}

	/**
	 * @param protocols
	 *          the protocols to set
	 */
	public void setProtocols(List<ICProtocol> protocols) {
		this.protocols = protocols;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.data.UniqueIDCapable#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		return uniqueID;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.data.UniqueIDCapable#setUniqueId(java.lang.String)
	 */
	@Override
	public void setUniqueId(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	/* Storable implementation */
	@Override
	public Object write(ObjectWriter writer, IWContext iwc) throws RemoteException {
		return BuilderServiceFactory.getBuilderPageWriterService(iwc).write(null, writer, iwc);
	}

	@Override
	public Object read(ObjectReader reader, IWContext iwc) throws RemoteException {
		return reader.read((XMLData) null, iwc);
	}

	/* ICTreeNode implementation */
	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public ICPage getChildAtIndex(int index) {
		return children.get(index);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public Collection<ICPage> getChildren() {
		return children;
	}

	@Override
	public Iterator<ICPage> getChildrenIterator() {
		return children.iterator();
	}

	@Override
	public String getId() {
		return getID().toString();
	}

	@Override
	public int getIndex(ICPage node) {
		return Integer.parseInt(node.getId());
	}

	@Override
	public int getNodeID() {
		return getID().intValue();
	}

	@Override
	public String getNodeName() {
		return getName();
	}

	@Override
	public String getNodeName(Locale locale) {
		return getNodeName();
	}

	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getNodeName(locale);
	}

	@Override
	public ICPage getParentNode() {
		return parent;
	}

	@Override
	public int getSiblingCount() {
		ICPage parent = getParentNode();
		if (parent != null) {
			return parent.getChildCount() - 1;
		}
		return 0;
	}

	@Override
	public boolean isLeaf() {
		return children == null || children.isEmpty();
	}
}