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
import com.idega.util.IWTimestamp;

@Entity
@Table(name = Group.ENTITY_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = Group.COLUMN_GROUP_TYPE, discriminatorType = DiscriminatorType.STRING)
@NamedQueries({
	@NamedQuery(name = "group.findAll", query = "select g from Group g"),
	@NamedQuery(name = "group.findAllByGroupType", query = "select g from Group g where g.groupType = :groupType"),
	@NamedQuery(name = "group.findAllByGroupTypes", query = "select g from Group g where g.groupType in (:groupTypes)"),
	@NamedQuery(name = "group.findByGroupTypeAndName", query = "select g from Group g where g.groupType = :groupType and g.name = :name"),
	@NamedQuery(name = "group.findAllByAbbreviation", query = "select g from Group g where g.abbreviation = :abbreviation"),
	@NamedQuery(name = "group.findByUniqueID", query = "select g from Group g where g.uniqueID = :uniqueID")
})
public abstract class Group implements Serializable, UniqueIDCapable, MetaDataCapable, ICTreeNode {

	private static final long serialVersionUID = -9014094183053434782L;

	public static final String ENTITY_NAME = "ic_group";
	public static final String COLUMN_GROUP_ID = "ic_group_id";
	public static final String COLUMN_GROUP_TYPE = "group_type";

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

	@ManyToOne
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

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Metadata.class)
	@JoinTable(name = SQL_RELATION_METADATA, joinColumns = { @JoinColumn(name = COLUMN_GROUP_ID) }, inverseJoinColumns = { @JoinColumn(name = Metadata.COLUMN_METADATA_ID) })
	private Set<Metadata> metadata;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Group.class)
	@JoinTable(name = GroupRelation.ENTITY_NAME, joinColumns = { @JoinColumn(name = GroupRelation.COLUMN_RELATED_GROUP, referencedColumnName = COLUMN_GROUP_ID) }, inverseJoinColumns = { @JoinColumn(name = GroupRelation.COLUMN_GROUP) })
	private List<Group> parents;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Group.class)
	@JoinTable(name = GroupRelation.ENTITY_NAME, joinColumns = { @JoinColumn(name = COLUMN_GROUP_ID) }, inverseJoinColumns = { @JoinColumn(name = GroupRelation.COLUMN_RELATED_GROUP, referencedColumnName = COLUMN_GROUP_ID) })
	private List<Group> children;
	
    @OneToMany(mappedBy = "pk.group")
    private final List<TopNodeGroup> topNodeGroups = new ArrayList();

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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (this.groupID == null) {
			if (other.groupID != null)
				return false;
		}
		else if (!this.groupID.equals(other.groupID))
			return false;
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
	public String getUniqueId() {
		return uniqueID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.UniqueIDCapable#setUniqueId(java.lang.String)
	 */
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

	/**
	 * @return the groupType
	 */
	public GroupType getGroupType() {
		return this.groupType;
	}

	/**
	 * @param groupType
	 *          the groupType to set
	 */
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
	public List<Address> getAddresses() {
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

	public String getMetaData(String metaDataKey) {
		Set<Metadata> list = getMetadata();
		for (Metadata metaData : list) {
			if (metaData.getKey().equals(metaDataKey)) {
				return metaData.getValue();
			}
		}
		
		return null;
	}

	public Map<String, String> getMetaDataAttributes() {
		Map<String, String> map = new HashMap<String, String>();

		Set<Metadata> list = getMetadata();
		for (Metadata metaData : list) {
			map.put(metaData.getKey(), metaData.getValue());
		}
		
		return map;
	}

	public Map<String, String> getMetaDataTypes() {
		Map<String, String> map = new HashMap<String, String>();

		Set<Metadata> list = getMetadata();
		for (Metadata metaData : list) {
			map.put(metaData.getKey(), metaData.getType());
		}
		
		return map;
	}

	public boolean removeMetaData(String metaDataKey) {
		Metadata metadata = getMetadata(metaDataKey);
		if (metadata != null) {
			getMetadata().remove(metadata);
		}
		
		return false;
	}

	public void renameMetaData(String oldKeyName, String newKeyName, String value) {
		Metadata metadata = getMetadata(oldKeyName);
		if (metadata != null) {
			metadata.setKey(newKeyName);
			if (value != null) {
				metadata.setValue(value);
			}
		}
	}

	public void renameMetaData(String oldKeyName, String newKeyName) {
		renameMetaData(oldKeyName, newKeyName, null);
	}

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

	public void setMetaData(String metaDataKey, String value) {
		setMetaData(metaDataKey, value, null);
	}

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

	public void updateMetaData() throws SQLException {
		//Does nothing...
	}
	
	/* ICTreeNode implementation */
	public boolean getAllowsChildren() {
		return true;
	}

	public ICTreeNode getChildAtIndex(int childIndex) {
		return children.get(childIndex);
	}

	public int getChildCount() {
		return children.size();
	}
	
	public Collection getChildren() {
		return children;
	}
	
	public Iterator getChildrenIterator() {
		return children.iterator();
	}
	
	public String getId() {
		return getID().toString();
	}

	public int getIndex(ICTreeNode node) {
		return Integer.parseInt(node.getId());
	}
	
	public int getNodeID() {
		return getID().intValue();
	}
	
	public String getNodeName() {
		return getName();
	}

	public String getNodeName(Locale locale) {
		return getNodeName();
	}

	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getNodeName(locale);
	}
	
	public ICTreeNode getParentNode() {
		if (parents != null && !parents.isEmpty()) {
			return parents.iterator().next();
		}
		return null;
	}
	
	public List<Group> getParentGroups() {
		return parents;
	}
	
	public int getSiblingCount() {
		ICTreeNode parent = getParentNode();
		if (parent != null) {
			return parent.getChildCount() - 1;
		}
		return 0;
	}

	public boolean isLeaf() {
		return children == null || children.isEmpty();
	}
}