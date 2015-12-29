/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.DiscriminatorOptions;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.idega.core.builder.data.bean.ICPage;
import com.idega.core.contact.data.bean.Email;
import com.idega.core.contact.data.bean.Phone;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.bean.ICFile;
import com.idega.core.idgenerator.business.IdGenerator;
import com.idega.core.idgenerator.business.IdGeneratorFactory;
import com.idega.core.location.data.bean.Address;
import com.idega.core.net.data.bean.ICNetwork;
import com.idega.core.net.data.bean.ICProtocol;
import com.idega.data.MetaDataCapable;
import com.idega.data.UniqueIDCapable;
import com.idega.data.bean.Metadata;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.user.dao.GroupDAO;
import com.idega.user.dao.UserDAO;
import com.idega.user.data.GroupNode;
import com.idega.user.data.User;
import com.idega.util.DBUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.expression.ELUtil;

@Entity
@Table(name = Group.ENTITY_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = Group.COLUMN_GROUP_TYPE, discriminatorType = DiscriminatorType.STRING)
@DiscriminatorOptions(force = true)
@NamedQueries({
	@NamedQuery(name = "group.findAll", query = "select g from Group g"),
	@NamedQuery(name = "group.findAllByGroupType", query = "select g from Group g where g.groupType = :groupType"),
	@NamedQuery(name = "group.findAllByGroupTypes", query = "select g from Group g where g.groupType in (:groupTypes)"),
	@NamedQuery(name = "group.findByGroupTypeAndName", query = "select g from Group g where g.groupType = :groupType and g.name = :name"),
	@NamedQuery(name = "group.findAllByAbbreviation", query = "select g from Group g where g.abbreviation = :abbreviation"),
	@NamedQuery(name = "group.findByUniqueID", query = "select g from Group g where g.uniqueID = :uniqueID"),
	@NamedQuery(name = Group.QUERY_FIND_BY_IDS, query = "select g from Group g where g.groupID in (:ids)")
})
@XmlTransient
@Cacheable
public abstract class Group implements Serializable, UniqueIDCapable, MetaDataCapable, ICTreeNode<Group>, GroupNode<Group> {

	private static final long serialVersionUID = -9014094183053434782L;

	public static final String	QUERY_FIND_BY_IDS = "group.findByIDs",

								ENTITY_NAME = "ic_group",
								COLUMN_GROUP_ID = "ic_group_id",
								COLUMN_GROUP_TYPE = "group_type";

	private static final String COLUMN_UNIQUE_ID = "unique_id";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_EXTRA_INFO = "extra_info";
	private static final String COLUMN_CREATED = "created";
	private static final String COLUMN_HOME_PAGE_ID = "home_page_id";
	private static final String COLUMN_HOME_FOLDER_ID = "home_folder_id";
	private static final String COLUMN_ALIAS_TO_GROUP = "alias_id";
	private static final String COLUMN_PERMISSION_CONTROLLING_GROUP = "perm_group_id";
	private static final String COLUMN_IS_PERMISSION_CONTROLLING_GROUP = "is_perm_controlling";
	private static final String COLUMN_SHORT_NAME = "short_name";
	private static final String COLUMN_ABBREVIATION = "abbr";
	private static final String COLUMN_GROUP_MODERATOR_ID = "group_moderator_id";

	public static final String SQL_RELATION_EMAIL = "ic_group_email";
	public static final String SQL_RELATION_ADDRESS = "ic_group_address";
	public static final String SQL_RELATION_PHONE = "ic_group_phone";
	public static final String SQL_RELATION_METADATA = "ic_group_ic_metadata";
	public static final String SQL_RELATION_PROTOCOL = "ic_group_protocol";
	public static final String SQL_RELATION_NETWORK = "ic_group_network";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = Group.COLUMN_GROUP_ID)
	private Integer groupID;

	@Column(name = COLUMN_UNIQUE_ID, length = 36, nullable = false, unique = true)
	private String uniqueID;

	@Column(name = COLUMN_NAME)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_GROUP_TYPE, insertable = false, updatable = false)
	private GroupType groupType;

	@Column(name = COLUMN_DESCRIPTION)
	private String description;

	@Column(name = COLUMN_EXTRA_INFO)
	private String extraInfo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_CREATED)
	private Date created;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_HOME_PAGE_ID)
	private ICPage homePage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_HOME_FOLDER_ID)
	private ICFile homeFolder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_ALIAS_TO_GROUP)
	private Group alias;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_PERMISSION_CONTROLLING_GROUP)
	private Group permissionControllingGroup;

	@Column(name = COLUMN_IS_PERMISSION_CONTROLLING_GROUP)
	private Character isPermissionControllingGroup;

	@Column(name = COLUMN_SHORT_NAME)
	private String shortName;

	@Column(name = COLUMN_ABBREVIATION)
	private String abbreviation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_GROUP_MODERATOR_ID)
	private Group groupModerator;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = ICNetwork.class)
	@JoinTable(name = SQL_RELATION_NETWORK, joinColumns = { @JoinColumn(name = COLUMN_GROUP_ID) }, inverseJoinColumns = { @JoinColumn(name = ICNetwork.COLUMN_NETWORK_ID) })
	private List<ICNetwork> networks;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = ICProtocol.class)
	@JoinTable(name = SQL_RELATION_PROTOCOL, joinColumns = { @JoinColumn(name = COLUMN_GROUP_ID) }, inverseJoinColumns = { @JoinColumn(name = ICProtocol.COLUMN_PROTOCOL_ID) })
	private List<ICProtocol> protocols;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Phone.class)
	@JoinTable(name = SQL_RELATION_PHONE, joinColumns = { @JoinColumn(name = COLUMN_GROUP_ID) }, inverseJoinColumns = { @JoinColumn(name = Phone.COLUMN_PHONE_ID) })
	private List<Phone> phones;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Email.class)
	@JoinTable(name = SQL_RELATION_EMAIL, joinColumns = { @JoinColumn(name = COLUMN_GROUP_ID) }, inverseJoinColumns = { @JoinColumn(name = Email.COLUMN_EMAIL_ID) })
	private List<Email> emails;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Address.class)
	@JoinTable(name = SQL_RELATION_ADDRESS, joinColumns = { @JoinColumn(name = COLUMN_GROUP_ID) }, inverseJoinColumns = { @JoinColumn(name = Address.COLUMN_ADDRESS_ID) })
	private List<Address> addresses;

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Metadata.class)
	@JoinTable(name = SQL_RELATION_METADATA, joinColumns = { @JoinColumn(name = COLUMN_GROUP_ID) }, inverseJoinColumns = { @JoinColumn(name = Metadata.COLUMN_METADATA_ID) })
	private Set<Metadata> metadata;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Group.class)
	@JoinTable(name = GroupRelation.ENTITY_NAME, joinColumns = { @JoinColumn(name = GroupRelation.COLUMN_RELATED_GROUP, referencedColumnName = COLUMN_GROUP_ID) }, inverseJoinColumns = { @JoinColumn(name = GroupRelation.COLUMN_GROUP) })
	private List<Group> parents;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Group.class)
	@JoinTable(name = GroupRelation.ENTITY_NAME, joinColumns = { @JoinColumn(name = COLUMN_GROUP_ID) }, inverseJoinColumns = { @JoinColumn(name = GroupRelation.COLUMN_RELATED_GROUP, referencedColumnName = COLUMN_GROUP_ID) })
	private List<Group> children;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.group")
    private final List<TopNodeGroup> topNodeGroups = new ArrayList<TopNodeGroup>();

    @Transient
    private boolean hasUsers;

	@PrePersist
	public void setDefaultValues() {
		if (getUniqueId() == null) {
			IdGenerator uidGenerator = IdGeneratorFactory.getUUIDGenerator();
			setUniqueId(uidGenerator.generateId());
		}
		if (getCreated() == null) {
			setCreated(IWTimestamp.getTimestampRightNow());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.groupID == null) ? 0 : this.groupID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (this == obj) {
				return true;
			}

			if (obj == null) {
				return false;
			}

			if (getClass() != obj.getClass()) {
				return false;
			}

			Group other = (Group) obj;
			if (this.groupID == null) {
				if (other.groupID != null) {
					return false;
				}
			} else if (!this.groupID.equals(other.groupID)) {
				return false;
			}
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error while checking if equals " + this + " and " + obj, e);
		}

		return true;
	}

	public Integer getID() {
		return this.groupID;
	}

	public void setID(Integer groupID) {
		this.groupID = groupID;
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

	@Override
	public String getType() {
		GroupType type = getGroupType();
		return type == null ? null : type.getGroupType();
	}

	public GroupType getGroupType() {
		DBUtil.getInstance().lazyLoad(groupType);
		return groupType;
	}

	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *          the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the extraInfo
	 */
	public String getExtraInfo() {
		return this.extraInfo;
	}

	/**
	 * @param extraInfo
	 *          the extraInfo to set
	 */
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return this.created;
	}

	/**
	 * @param created
	 *          the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the homePage
	 */
	public ICPage getHomePage() {
		if (!DBUtil.getInstance().isInitialized(homePage)) {
			homePage = DBUtil.getInstance().lazyLoad(homePage);
		}
		return this.homePage;
	}

	/**
	 * @param homePage
	 *          the homePage to set
	 */
	public void setHomePage(ICPage homePage) {
		this.homePage = homePage;
	}

	/**
	 * @return the homeFolder
	 */
	public ICFile getHomeFolder() {
		if (!DBUtil.getInstance().isInitialized(homeFolder)) {
			homeFolder = DBUtil.getInstance().lazyLoad(homeFolder);
		}
		return this.homeFolder;
	}

	/**
	 * @param homeFolder
	 *          the homeFolder to set
	 */
	public void setHomeFolder(ICFile homeFolder) {
		this.homeFolder = homeFolder;
	}

	/**
	 * @return the alias
	 */
	public Group getAlias() {
		if (!DBUtil.getInstance().isInitialized(alias)) {
			alias = DBUtil.getInstance().lazyLoad(alias);
		}
		return this.alias;
	}

	/**
	 * @param alias
	 *          the alias to set
	 */
	public void setAlias(Group alias) {
		this.alias = alias;
	}

	/**
	 * @return the permissionControllingGroup
	 */
	public Group getPermissionControllingGroup() {
		if (!DBUtil.getInstance().isInitialized(permissionControllingGroup)) {
			permissionControllingGroup = DBUtil.getInstance().lazyLoad(permissionControllingGroup);
		}
		return this.permissionControllingGroup;
	}

	/**
	 * @param permissionControllingGroup
	 *          the permissionControllingGroup to set
	 */
	public void setPermissionControllingGroup(Group permissionControllingGroup) {
		this.permissionControllingGroup = permissionControllingGroup;
	}

	/**
	 * @return the isPermissionControllingGroup
	 */
	public Character getIsPermissionControllingGroup() {
		return this.isPermissionControllingGroup;
	}

	/**
	 * @param isPermissionControllingGroup
	 *          the isPermissionControllingGroup to set
	 */
	public void setIsPermissionControllingGroup(Character isPermissionControllingGroup) {
		this.isPermissionControllingGroup = isPermissionControllingGroup;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return this.shortName;
	}

	/**
	 * @param shortName
	 *          the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @return the abbreviation
	 */
	public String getAbbreviation() {
		return this.abbreviation;
	}

	/**
	 * @param abbreviation
	 *          the abbreviation to set
	 */
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * @return the groupModerator
	 */
	public Group getGroupModerator() {
		if (!DBUtil.getInstance().isInitialized(groupModerator)) {
			groupModerator = DBUtil.getInstance().lazyLoad(groupModerator);
		}
		return this.groupModerator;
	}

	/**
	 * @param groupModerator
	 *          the groupModerator to set
	 */
	public void setGroupModerator(Group groupModerator) {
		this.groupModerator = groupModerator;
	}

	/**
	 * @return the networks
	 */
	public List<ICNetwork> getNetworks() {
		DBUtil.getInstance().lazyLoad(networks);
		return this.networks;
	}

	/**
	 * @param networks
	 *          the networks to set
	 */
	public void setNetworks(List<ICNetwork> networks) {
		this.networks = networks;
	}

	/**
	 * @return the protocols
	 */
	public List<ICProtocol> getProtocols() {
		DBUtil.getInstance().lazyLoad(protocols);
		return this.protocols;
	}

	/**
	 * @param protocols
	 *          the protocols to set
	 */
	public void setProtocols(List<ICProtocol> protocols) {
		this.protocols = protocols;
	}

	/**
	 * @return the phones
	 */
	public List<Phone> getPhones() {
		DBUtil.getInstance().lazyLoad(phones);
		return this.phones;
	}

	/**
	 * @param phones
	 *          the phones to set
	 */
	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	/**
	 * @return the emails
	 */
	public List<Email> getEmails() {
		DBUtil.getInstance().lazyLoad(emails);
		return this.emails;
	}

	/**
	 * @param emails
	 *          the emails to set
	 */
	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}

	/**
	 * @return the addresses
	 */
	@LazyCollection(LazyCollectionOption.EXTRA)
	public List<Address> getAddresses() {
		DBUtil.getInstance().lazyLoad(addresses);
		return this.addresses;
	}

	/**
	 * @param addresses
	 *          the addresses to set
	 */
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	/**
	 * @return the metadata
	 */
	public Set<Metadata> getMetadata() {
//		DBUtil.getInstance().lazyLoad(metadata);
		return this.metadata;
	}

	/**
	 * @param metadata
	 *          the metadata to set
	 */
	public void setMetadata(Set<Metadata> metadata) {
		this.metadata = metadata;
	}

	/**
	 * @return Returns the topNodeGroups.
	 */
	public List<TopNodeGroup> getTopNodeGroups() {
		DBUtil.getInstance().lazyLoad(topNodeGroups);
		return this.topNodeGroups;
	}

	/* MetaDataCapable implementation */
	private Metadata getMetadata(String key) {
		Set<Metadata> list = getMetadata();
		for (Metadata metaData : list) {
			if (metaData.getKey().equals(key)) {
				return metaData;
			}
		}

		return null;
	}

	@Override
	public String getMetaData(String metaDataKey) {
		Set<Metadata> list = getMetadata();
		for (Metadata metaData : list) {
			if (metaData.getKey().equals(metaDataKey)) {
				return metaData.getValue();
			}
		}

		return null;
	}

	@Override
	public Map<String, String> getMetaDataAttributes() {
		Map<String, String> map = new HashMap<String, String>();

		Set<Metadata> list = getMetadata();
		for (Metadata metaData : list) {
			map.put(metaData.getKey(), metaData.getValue());
		}

		return map;
	}

	@Override
	public Map<String, String> getMetaDataTypes() {
		Map<String, String> map = new HashMap<String, String>();

		Set<Metadata> list = getMetadata();
		for (Metadata metaData : list) {
			map.put(metaData.getKey(), metaData.getType());
		}

		return map;
	}

	@Override
	public boolean removeMetaData(String metaDataKey) {
		Metadata metadata = getMetadata(metaDataKey);
		if (metadata != null) {
			getMetadata().remove(metadata);
		}

		return false;
	}

	@Override
	public void renameMetaData(String oldKeyName, String newKeyName, String value) {
		Metadata metadata = getMetadata(oldKeyName);
		if (metadata != null) {
			metadata.setKey(newKeyName);
			if (value != null) {
				metadata.setValue(value);
			}
		}
	}

	@Override
	public void renameMetaData(String oldKeyName, String newKeyName) {
		renameMetaData(oldKeyName, newKeyName, null);
	}

	@Override
	public void setMetaData(String metaDataKey, String value, String type) {
		Metadata metadata = getMetadata(metaDataKey);
		if (metadata == null) {
			metadata = new Metadata();
			metadata.setKey(metaDataKey);
		}
		metadata.setValue(value);
		if (type != null) {
			metadata.setType(type);
		}

		getMetadata().add(metadata);

	}

	@Override
	public void setMetaData(String metaDataKey, String value) {
		setMetaData(metaDataKey, value, null);
	}

	@Override
	public void setMetaDataAttributes(Map<String, String> map) {
		for (String key : map.keySet()) {
			String value = map.get(key);

			Metadata metadata = getMetadata(key);
			if (metadata == null) {
				metadata = new Metadata();
				metadata.setKey(key);
			}
			metadata.setValue(value);

			getMetadata().add(metadata);
		}
	}

	@Override
	public void updateMetaData() throws SQLException {
		//Does nothing...
	}

	/* ICTreeNode implementation */
	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public Group getChildAtIndex(int childIndex) {
		if (!ListUtil.isEmpty(getChildren())) {
			return getChildren().get(childIndex);
		}
		return null;
	}

	@Override
	public int getChildCount() {
		if (!ListUtil.isEmpty(getChildren())) {
			return getChildren().size();
		}
		return 0;
	}

	@Override
	public List<Group> getChildren() {
		DBUtil.getInstance().lazyLoad(children);
		return children;
	}

	@Override
	public Iterator<Group> getChildrenIterator() {
		if (!ListUtil.isEmpty(getChildren())) {
			return getChildren().iterator();
		}
		return null;
	}

	@Override
	public String getId() {
		return getID().toString();
	}

	@Override
	public int getIndex(Group node) {
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
	public Group getParentNode() {
		if (!ListUtil.isEmpty(getParentGroups())) {
			return getParentGroups().iterator().next();
		}
		return null;
	}

	@Override
	public List<Group> getParentGroups() {
		DBUtil.getInstance().lazyLoad(parents);
		return parents;
	}

	@Override
	public int getSiblingCount() {
		Group parent = getParentNode();
		if (parent != null) {
			return parent.getChildCount() - 1;
		}
		return 0;
	}

	@Override
	public boolean isLeaf() {
		return ListUtil.isEmpty(getChildren());
	}

	@Override
	public String toString() {
		return getId();
	}

	private boolean isUser() {
		GroupType type = getGroupType();
		return type != null && User.USER_GROUP_TYPE.equals(type.getGroupType());
	}

	private com.idega.user.data.bean.User getUserForGroup() {
		UserDAO userDAO = ELUtil.getInstance().getBean(UserDAO.class);
		return userDAO.getUser(getID());
	}

	@Override
	public List<Group> getParentGroups(Map<String, Collection<Integer>> cachedParents, Map<String, Group> cachedGroups) throws EJBException {
		List<Group> theReturn = new ArrayList<Group>();
		try {
			Group parent = null;
			Collection<Group> parents = getCollectionOfParents(cachedParents, cachedGroups);
			for (Iterator<Group> parIter = parents.iterator(); parIter.hasNext();) {
				parent = parIter.next();
				if (parent != null && !theReturn.contains(parent)) {
					theReturn.add(parent);
				}
			}
			if (isUser()) {
				try {
					com.idega.user.data.bean.User user = getUserForGroup();
					Group usersPrimaryGroup = user.getPrimaryGroup();
					String key = usersPrimaryGroup == null ? null : String.valueOf(usersPrimaryGroup.getID());
					if (cachedGroups != null && key != null) {
						if (cachedGroups.containsKey(key)) {
							usersPrimaryGroup = cachedGroups.get(key);
						} else {
							cachedGroups.put(key, usersPrimaryGroup);
						}
					}
					else {
						usersPrimaryGroup = user.getPrimaryGroup();
					}
					if (usersPrimaryGroup != null && !theReturn.contains(usersPrimaryGroup)) {
						theReturn.add(usersPrimaryGroup);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EJBException(e.getMessage());
		}
		return theReturn;
	}

	private Collection<Group> getCollectionOfParents(Map<String, Collection<Integer>> cachedParents, Map<String, Group> cachedGroups) throws Exception {
		GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);

		Collection<Integer> col = null;
		String key = getID().toString();
		if (cachedParents != null) {
			if (cachedParents.containsKey(key)) {
				col = cachedParents.get(key);
			} else {
				col = groupDAO.findParentGroupsIds(getID());
				cachedParents.put(key, col);
			}
		} else {
			col = groupDAO.findParentGroupsIds(getID());
		}

		Collection<Group> returnCol = new ArrayList<Group>();
		Group parent = null;
		Integer parentID = null;
		for (Iterator<Integer> iter = col.iterator(); iter.hasNext();) {
			parentID = iter.next();
			key = parentID.toString();
			if (cachedGroups != null) {
				if (cachedGroups.containsKey(key)) {
					parent = cachedGroups.get(key);
				} else {
					parent = groupDAO.findGroup(parentID);
					cachedGroups.put(key, parent);
				}
			}
			else {
				parent = groupDAO.findGroup(parentID);
			}
			returnCol.add(parent);
		}

		return returnCol;
	}

	public void setHasUsers(boolean hasUsers) {
		this.hasUsers = hasUsers;
	}

	public boolean hasUsers() {
		return hasUsers;
	}

}