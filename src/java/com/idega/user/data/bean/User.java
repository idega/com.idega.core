/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.data.bean.ICRole;
import com.idega.core.accesscontrol.data.bean.UserLogin;
import com.idega.core.builder.data.bean.ICPage;
import com.idega.core.contact.data.bean.Email;
import com.idega.core.contact.data.bean.EmailType;
import com.idega.core.contact.data.bean.Phone;
import com.idega.core.file.data.bean.ICFile;
import com.idega.core.idgenerator.business.IdGenerator;
import com.idega.core.idgenerator.business.IdGeneratorFactory;
import com.idega.core.localisation.data.bean.ICLanguage;
import com.idega.core.location.data.bean.Address;
import com.idega.core.location.data.bean.AddressType;
import com.idega.data.MetaDataCapable;
import com.idega.data.UniqueIDCapable;
import com.idega.data.bean.Metadata;
import com.idega.idegaweb.IWMainApplication;
import com.idega.user.business.UserBusiness;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.DBUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

@Entity
@Table(name = User.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "user.findAll", query = "select u from User u where u.deleted != 'Y' order by u.firstName, u.lastName, u.middleName"),
	@NamedQuery(name = "user.findAllByPrimaryGroup", query = "select u from User u where u.primaryGroup = :primaryGroup and u.deleted != 'Y' order by u.firstName, u.lastName, u.middleName"),
	@NamedQuery(name = "user.findByPersonalID", query = "select u from User u where u.personalID = :personalID"),
	@NamedQuery(name = User.QUERY_FIND_BY_PERSONAL_IDS, query = "select u from User u where u.personalID IN (:personalIDs)"),
	@NamedQuery(name = "user.findByUniqueID", query = "select u from User u where u.uniqueId = :uniqueId"),
	@NamedQuery(name = "user.findByLastName", query = "select u from User u where u.lastName = :lastName"),
	@NamedQuery(name = "user.findByNames", query = "select u from User u where u.firstName like :firstName or u.middleName like :middleName or u.lastName like :lastName and u.deleted != 'Y' order by u.firstName, u.lastName, u.middleName"),
	@NamedQuery(
			name = User.QUERY_FIND_BY_PRIMARY_KEYS,
			query = "SELECT u FROM User u WHERE u.userID IN (:primaryKeys)"),
	@NamedQuery(name = User.QUERY_FIND_BY_PHONE_NUMBER, query = "select distinct u from User u join u.phones up where up.number = :number"),
	@NamedQuery(name = User.QUERY_FIND_BY_METADATA, query = "select distinct u from User u join u.metadata um where um.key = :key and um.value = :value"),
	@NamedQuery(
			name = User.QUERY_FIND_BY_GROUPS_IDS_AND_ACTIVE_AT_GIVEN_TIMEFRAME,
			query = "SELECT DISTINCT user FROM User AS user, GroupRelation AS gr WHERE gr.group.id in (:groupsIds) AND (" +
				"(gr.status = '" + GroupRelation.STATUS_ACTIVE + "' or gr.status = '" + GroupRelation.STATUS_ACTIVE_PENDING + "') " +
				"OR ((gr.status = '" + GroupRelation.STATUS_PASSIVE + "' or gr.status = '" + GroupRelation.STATUS_PASSIVE_PENDING + "') " +
				"AND gr.terminationDate IS NOT NULL AND gr.terminationDate >= :dateFrom AND gr.terminationDate < :dateTo) " +
			") " +
			"AND user.id = gr.relatedGroup.id AND gr.relatedGroupType.groupType = '" + UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE + "'"
	),
	@NamedQuery(
			name = User.QUERY_FIND_BY_GROUPS_IDS_AND_ACTIVE_BEFORE_GIVEN_TIMEFRAME,
			query = "SELECT DISTINCT user FROM User AS user, GroupRelation AS gr WHERE gr.group.id IN (:groupsIds) " +
			" AND (" +
				"((gr.status = '" + GroupRelation.STATUS_ACTIVE + "' OR gr.status = '" + GroupRelation.STATUS_ACTIVE_PENDING + "') " +
				"AND gr.initiationDate IS NOT NULL AND gr.initiationDate <= :dateTo) " +
				"OR ((gr.status = '" + GroupRelation.STATUS_PASSIVE + "' OR gr.status = '" + GroupRelation.STATUS_PASSIVE_PENDING + "') " +
				"AND gr.initiationDate IS NOT NULL AND gr.initiationDate <= :dateTo AND gr.terminationModificationDate IS NOT NULL AND gr.terminationModificationDate >= :dateTo) " +
			") " +
			"AND user.id = gr.relatedGroup.id AND gr.relatedGroupType.groupType = '" + UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE + "'"
	),
	@NamedQuery(
			name = User.QUERY_FIND_BY_GROUPS_IDS_AND_DELETED_AT_GIVEN_TIMEFRAME,
			query = "SELECT DISTINCT user FROM User AS user, GroupRelation AS gr WHERE gr.group.id in (:groupsIds) AND (" +
				"(gr.status = '" + GroupRelation.STATUS_ACTIVE_PENDING + "') " +
				"OR ((gr.status = '" + GroupRelation.STATUS_PASSIVE + "' or gr.status = '" + GroupRelation.STATUS_PASSIVE_PENDING + "') " +
				"AND gr.terminationDate IS NOT NULL AND gr.terminationDate >= :dateFrom AND gr.terminationDate < :dateTo) " +
			") " +
			"AND user.id = gr.relatedGroup.id AND gr.relatedGroupType.groupType = '" + UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE + "'"
	),
	@NamedQuery(
			name = User.QUERY_FIND_BY_GROUPS_IDS_AND_ACTIVE_ONLY_AT_GIVEN_TIMEFRAME,
			query = "SELECT DISTINCT user FROM User AS user, GroupRelation AS gr WHERE gr.group.id IN (:groupsIds) " +
			" AND (" +
				"((gr.status = '" + GroupRelation.STATUS_ACTIVE + "' OR gr.status = '" + GroupRelation.STATUS_ACTIVE_PENDING + "') " +
					"AND gr.initiationDate IS NOT NULL AND gr.initiationDate <= :dateTo) " +
				"OR ((gr.status = '" + GroupRelation.STATUS_PASSIVE + "' OR gr.status = '" + GroupRelation.STATUS_PASSIVE_PENDING + "') " +
					"AND gr.initiationDate IS NOT NULL AND gr.initiationDate <= :dateTo " +
					"AND gr.terminationModificationDate IS NOT NULL AND gr.terminationModificationDate >= :dateFrom) " +
			") " +
			"AND user.id = gr.relatedGroup.id AND gr.relatedGroupType.groupType = '" + UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE + "'"
	),
	@NamedQuery(
			name = User.QUERY_FIND_BY_GROUPS_IDS_DELETED,
			query = "SELECT DISTINCT user FROM User AS user, GroupRelation AS gr WHERE gr.group.id in (:groupsIds) AND (" +
				"gr.status IN ('" + GroupRelation.STATUS_ACTIVE_PENDING + "', '" + GroupRelation.STATUS_PASSIVE + "', '" + GroupRelation.STATUS_PASSIVE_PENDING + "') " +
			") " +
			"AND user.id = gr.relatedGroup.id AND gr.relatedGroupType.groupType = '" + UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE + "'"
	),
	@NamedQuery(
			name = User.QUERY_FIND_ACTIVE_OR_PASSIVE_BY_GROUPS_IDS,
			query = "SELECT DISTINCT user FROM User AS user, GroupRelation AS gr WHERE gr.group.id IN (:groupsIds) " +
			" AND (" +
				"((gr.status = '" + GroupRelation.STATUS_ACTIVE + "' OR gr.status = '" + GroupRelation.STATUS_ACTIVE_PENDING + "') " + "AND gr.initiationDate IS NOT NULL) " +
				"OR ((gr.status = '" + GroupRelation.STATUS_PASSIVE + "' OR gr.status = '" + GroupRelation.STATUS_PASSIVE_PENDING + "') " +
				"AND gr.initiationDate IS NOT NULL AND gr.terminationModificationDate IS NOT NULL) " +
			") " +
			"AND user.id = gr.relatedGroup.id AND gr.relatedGroupType.groupType = '" + UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE + "'"
	),
	@NamedQuery(
			name = User.QUERY_FIND_ALL_USERS,
			query = "select u from User u where u.deleted != 'Y'"),
	@NamedQuery(
			name = User.QUERY_COUNT_ALL,
			query = "select count(u) from User u where u.deleted != 'Y'"),
})
@XmlTransient
@Cacheable
public class User implements Serializable, UniqueIDCapable, MetaDataCapable {

	private static final long serialVersionUID = 3393646538663696610L;

	public static final String	ENTITY_NAME = "ic_user",
								COLUMN_USER_ID = "ic_user_id",
								COLUMN_DISPLAY_NAME = "display_name";

	private static final String COLUMN_UNIQUE_ID = "unique_id";
	private static final String COLUMN_FIRST_NAME = "first_name";
	private static final String COLUMN_MIDDLE_NAME = "middle_name";
	private static final String COLUMN_LAST_NAME = "last_name";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_DATE_OF_BIRTH = "date_of_birth";
	private static final String COLUMN_GENDER = "ic_gender_id";
	private static final String COLUMN_SYSTEM_IMAGE = "system_image_id";
	private static final String COLUMN_PRIMARY_GROUP = "primary_group";
	private static final String COLUMN_PERSONAL_ID = "personal_id";
	private static final String COLUMN_HOME_PAGE = "home_page_id";
	private static final String COLUMN_DELETED = "deleted";
	private static final String COLUMN_DELETED_BY = "deleted_by";
	private static final String COLUMN_DELETED_WHEN = "deleted_when";
	private static final String COLUMN_NATIVE_LANGUAGE = "ic_language_id";
	private static final String COLUMN_JURIDICAL_PERSON = "juridical_person";
	private static final String COLUMN_PREFERRED_LOCALE = "preferred_locale";
	private static final String COLUMN_PREFERRED_ROLE = "preferred_role";
	private static final String COLUMN_USER_PROPERTIES_FILE = "user_properties_file_id";

	public static final String SQL_RELATION_EMAIL = "ic_user_email";
	public static final String SQL_RELATION_ADDRESS = "ic_user_address";
	public static final String SQL_RELATION_PHONE = "ic_user_phone";
	public static final String SQL_RELATION_METADATA = "ic_metadata_ic_user";

	public static final String ADMINISTRATOR_DEFAULT_NAME = "Administrator";

	public static final String	QUERY_FIND_BY_PRIMARY_KEYS = "user.findAllByPrimaryKeys",
								QUERY_FIND_BY_PHONE_NUMBER = "user.findByPhoneNumber",
								QUERY_FIND_BY_METADATA = "user.findByMetadata",
								QUERY_FIND_BY_PERSONAL_IDS = "user.findByPersonalIDs",
								QUERY_FIND_BY_GROUPS_IDS_AND_ACTIVE_AT_GIVEN_TIMEFRAME = "user.findByGroupsIdsAndActiveAtGivenTimeframe",
								QUERY_FIND_BY_GROUPS_IDS_AND_ACTIVE_ONLY_AT_GIVEN_TIMEFRAME = "user.findByGroupsIdsAndActiveOnlyAtGivenTimeframe",
								QUERY_FIND_BY_GROUPS_IDS_AND_DELETED_AT_GIVEN_TIMEFRAME = "user.findByGroupsIdsAndDeletedAtGivenTimeframe",
								QUERY_FIND_BY_GROUPS_IDS_AND_ACTIVE_BEFORE_GIVEN_TIMEFRAME = "user.findByGroupsIdsAndBeforeGivenTimeframe",
								QUERY_FIND_BY_GROUPS_IDS_DELETED = "user.findByGroupsIdsAndDeleted",
								QUERY_FIND_ACTIVE_OR_PASSIVE_BY_GROUPS_IDS = "user.findActiveOrPassiveByGroupIds",
								QUERY_FIND_ALL_USERS = "user.findAllUsers",
								QUERY_COUNT_ALL = "user.countAll";

	public static final String PROP_ID = ENTITY_NAME + "_" + COLUMN_USER_ID;

	public static final String PROPERTY_ID = "userID";

	@Id
	@Column(name = User.COLUMN_USER_ID)
	private Integer userID;

	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@PrimaryKeyJoinColumn(name = COLUMN_USER_ID, referencedColumnName = Group.COLUMN_GROUP_ID)
	private UserGroupRepresentative group;

	public static final String PROPERTY_UUID = "uniqueId";
	public static final String PROP_UNIQUE_ID = ENTITY_NAME + "_" + COLUMN_UNIQUE_ID;
	@Column(name = COLUMN_UNIQUE_ID, length = 36, nullable = false, unique = true)
	private String uniqueId;

	@Column(name = COLUMN_FIRST_NAME, length = 45)
	private String firstName;

	@Column(name = COLUMN_MIDDLE_NAME, length = 90)
	private String middleName;

	@Column(name = COLUMN_LAST_NAME, length = 45)
	private String lastName;

	@Column(name = COLUMN_DISPLAY_NAME, length = 180)
	private String displayName;

	@Column(name = COLUMN_DESCRIPTION)
	private String description;

	@Temporal(TemporalType.DATE)
	@Column(name = COLUMN_DATE_OF_BIRTH)
	private Date dateOfBirth;

	public static final String PROPERTY_PERSONAL_ID = "personalID";
	@Column(name = COLUMN_PERSONAL_ID, length = 20)
	private String personalID;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_GENDER)
	private Gender gender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_SYSTEM_IMAGE)
	private ICFile systemImage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_PRIMARY_GROUP, referencedColumnName = Group.COLUMN_GROUP_ID, insertable = false, updatable = false)
	private Group primaryGroup;

	@Column(name = COLUMN_PRIMARY_GROUP, nullable = true)
	private Integer primaryGroupId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_HOME_PAGE)
	private ICPage homePage;

	@Column(name = COLUMN_DELETED, length = 1)
	private Character deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_DELETED_BY)
	private User deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_DELETED_WHEN)
	private Date deletedWhen;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_NATIVE_LANGUAGE)
	private ICLanguage nativeLanguage;

	@Column(name = COLUMN_JURIDICAL_PERSON, length = 1)
	private Character juridicalPerson;

	@Column(name = COLUMN_PREFERRED_LOCALE, length = 20)
	private String preferredLocale;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_PREFERRED_ROLE)
	private ICRole preferredRole;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_USER_PROPERTIES_FILE)
	private ICFile userProperties;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Phone.class)
	@JoinTable(name = SQL_RELATION_PHONE, joinColumns = { @JoinColumn(name = COLUMN_USER_ID) }, inverseJoinColumns = { @JoinColumn(name = Phone.COLUMN_PHONE_ID) })
	private List<Phone> phones;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = UserStatus.class)
	@JoinTable(name = UserStatus.ENTITY_NAME, joinColumns = { @JoinColumn(name = COLUMN_USER_ID) }, inverseJoinColumns = { @JoinColumn(name = UserStatus.COLUMN_USER_STATUS_ID) })
	private List<UserStatus> statuses;

	@ManyToMany(
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE},
			targetEntity = Email.class)
	@JoinTable(
			name = SQL_RELATION_EMAIL,
			joinColumns = {@JoinColumn(name = COLUMN_USER_ID)},
			inverseJoinColumns = {@JoinColumn(name = Email.COLUMN_EMAIL_ID)})
	private List<Email> emails;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Address.class)
	@JoinTable(name = SQL_RELATION_ADDRESS, joinColumns = { @JoinColumn(name = COLUMN_USER_ID) }, inverseJoinColumns = { @JoinColumn(name = Address.COLUMN_ADDRESS_ID) })
	private List<Address> addresses;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Metadata.class)
	@JoinTable(name = SQL_RELATION_METADATA, joinColumns = { @JoinColumn(name = COLUMN_USER_ID) }, inverseJoinColumns = { @JoinColumn(name = Metadata.COLUMN_METADATA_ID) })
	private Set<Metadata> metadata;

	@OneToOne(fetch = FetchType.LAZY, targetEntity = Group.class)
	@JoinColumn(name = COLUMN_USER_ID, referencedColumnName = Group.COLUMN_GROUP_ID)
	private Group userRepresentative;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.user")
    private List<TopNodeGroup> topNodeGroups = new ArrayList<>();

    @Column(name = com.idega.user.data.User.FIELD_SHA1, length = 40)
    private String sha1;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="user", cascade = CascadeType.ALL)
    private List<UserLogin> logins;

    @Column(name = com.idega.user.data.UserBMPBean.COLUMN_RESUME, length = 2048)
    private String resume;

    @Column(name = com.idega.user.data.UserBMPBean.COLUMN_LAST_READ_FROM_IMPORT)
    private Timestamp lastReadFromImport;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = ICLanguage.class)
	@JoinTable(name = com.idega.user.data.UserBMPBean.COLUMN_LANGUAGES, joinColumns = { @JoinColumn(name = COLUMN_USER_ID) }, inverseJoinColumns = { @JoinColumn(name = ICLanguage.COLUMN_LANGUAGE_ID) })
    private List<ICLanguage> languages;

	public Timestamp getLastReadFromImport() {
		return lastReadFromImport;
	}

	public void setLastReadFromImport(Timestamp lastReadFromImport) {
		this.lastReadFromImport = lastReadFromImport;
	}

	public List<ICLanguage> getLanguages() {
		languages = getInitialized(languages);
		return languages;
	}

	public void setLanguages(List<ICLanguage> languages) {
		this.languages = languages;
	}

	@PrePersist
	public void setDefaultValues() {
		if (getUniqueId() == null) {
			IdGenerator uidGenerator = IdGeneratorFactory.getUUIDGenerator();
			setUniqueId(uidGenerator.generateId());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.userID == null) ? 0 : this.userID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User)) {
			return false;
		}

		User other = (User) obj;
		if (this.userID == null) {
			if (other.userID != null) {
				return false;
			}
		} else if (!this.userID.equals(other.userID)) {
			return false;
		}

		return true;
	}

	public Integer getId() {
		return this.userID;
	}

	public void setId(Integer userID) {
		this.userID = userID;
	}

	public UserGroupRepresentative getGroup() {
		return this.group;
	}

	public void setGroup(UserGroupRepresentative group) {
		this.group = group;
		this.userID = group.getID();
	}

	@Override
	public String getUniqueId() {
		return uniqueId;
	}

	@Override
	public void setUniqueId(String uniqueID) {
		this.uniqueId = uniqueID;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return this.middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getPersonalID() {
		return this.personalID;
	}

	public void setPersonalID(String personalID) {
		this.personalID = personalID;
	}

	public Gender getGender() {
		gender = getInitialized(gender);
		return this.gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public ICFile getSystemImage() {
		systemImage = getInitialized(systemImage);
		return this.systemImage;
	}

	public void setSystemImage(ICFile systemImage) {
		this.systemImage = systemImage;
	}

	public Group getPrimaryGroup() {
		primaryGroup = getInitialized(primaryGroup);
		return this.primaryGroup;
	}

	public Integer getPrimaryGroupId() {
		return primaryGroupId;
	}

	public void setPrimaryGroup(Group primaryGroup) {
		this.primaryGroup = primaryGroup;
		if (primaryGroup != null) {
			this.primaryGroupId = primaryGroup.getID();
		}
	}

	public ICPage getHomePage() {
		homePage = getInitialized(homePage);
		return this.homePage;
	}

	public void setHomePage(ICPage homePage) {
		this.homePage = homePage;
	}

	public boolean isDeleted() {
		if (this.deleted == null) {
			return false;
		}
		return this.deleted == 'Y';
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted ? 'Y' : 'N';
	}

	public User getDeletedBy() {
		deletedBy = getInitialized(deletedBy);
		return this.deletedBy;
	}

	public void setDeletedBy(User deletedBy) {
		this.deletedBy = deletedBy;
	}

	public Date getDeletedWhen() {
		return this.deletedWhen;
	}

	public void setDeletedWhen(Date deletedWhen) {
		this.deletedWhen = deletedWhen;
	}

	public ICLanguage getNativeLanguage() {
		nativeLanguage = getInitialized(nativeLanguage);
		return this.nativeLanguage;
	}

	public void setNativeLanguage(ICLanguage nativeLanguage) {
		this.nativeLanguage = nativeLanguage;
	}

	public boolean isJuridicalPerson() {
		if (this.juridicalPerson == null) {
			return false;
		}
		return this.juridicalPerson == 'Y';
	}

	public void setJuridicalPerson(boolean juridicalPerson) {
		this.juridicalPerson = juridicalPerson ? 'Y' : 'N';
	}

	public String getPreferredLocale() {
		return this.preferredLocale;
	}

	public void setPreferredLocale(String preferredLocale) {
		this.preferredLocale = preferredLocale;
	}

	public ICRole getPreferredRole() {
		preferredRole = getInitialized(preferredRole);
		return this.preferredRole;
	}

	public void setPreferredRole(ICRole preferredRole) {
		this.preferredRole = preferredRole;
	}

	public ICFile getUserProperties() {
		userProperties = getInitialized(userProperties);
		return this.userProperties;
	}

	public void setUserProperties(ICFile userProperties) {
		this.userProperties = userProperties;
	}

	public List<Phone> getPhones() {
		phones = getInitialized(phones);
		return this.phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	public List<Email> getEmails() {
		emails = getInitialized(emails);
		return this.emails;
	}

	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}

	public List<Address> getAddresses() {
		addresses = getInitialized(addresses);
		return this.addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public Set<Metadata> getMetadata() {
		metadata = getInitialized(metadata);
		return this.metadata;
	}

	public void setMetadata(Set<Metadata> metadata) {
		this.metadata = metadata;
	}

	public Group getUserRepresentative() {
		userRepresentative = getInitialized(userRepresentative);
		return this.userRepresentative;
	}

	public Integer getUserRepresentativeId() {
		return getId();
	}

	private <T> T getInitialized(T notInitialized) {
		T initialized = DBUtil.getInstance().lazyLoad(notInitialized);
		return initialized;
	}

	public List<TopNodeGroup> getTopNodeGroups() {
		topNodeGroups = getInitialized(topNodeGroups);
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
		Map<String, String> map = new HashMap<>();

		Set<Metadata> list = getMetadata();
		for (Metadata metaData : list) {
			map.put(metaData.getKey(), metaData.getValue());
		}

		return map;
	}

	@Override
	public Map<String, String> getMetaDataTypes() {
		Map<String, String> map = new HashMap<>();

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
	public void setMetaData(String key, String value, String type) {
		setMetadata(key, value, type);
	}

	public Metadata setMetadata(String key, String value, String type) {
		Metadata metadata = getMetadata(key);
		if (metadata == null) {
			metadata = new Metadata();
			metadata.setKey(key);
		}
		metadata.setValue(value);
		if (type != null) {
			metadata.setType(type);
		}

		getMetadata().add(metadata);

		return metadata;
	}

	@Override
	public void setMetaData(String metaDataKey, String value) {
		setMetaData(metaDataKey, value, null);
	}

	@Override
	public void setMetaDataAttributes(Map<String, String> map) {
		for (String key: map.keySet()) {
			setMetaData(key, map.get(key), null);
		}
	}

	@Override
	public void updateMetaData() throws SQLException {
		// Does nothing...
	}

	public String getName() {
		String firstName = getFirstName();
		String middleName = getMiddleName();
		String lastName = getLastName();

		if (firstName == null) {
			firstName = CoreConstants.EMPTY;
		}

		if (middleName == null) {
			middleName = CoreConstants.EMPTY;
		} else if (!StringUtil.isEmpty(middleName)) {
			middleName = CoreConstants.SPACE.concat(middleName);
		}

		if (lastName == null) {
			lastName = CoreConstants.EMPTY;
		} else if (!StringUtil.isEmpty(lastName)){
			lastName = CoreConstants.SPACE.concat(lastName);
		}
		return firstName.concat(middleName).concat(lastName);
	}

	public String getSHA1() {
		return sha1;
	}

	public void setSHA1(String sha1) {
		this.sha1 = sha1;
	}

	public String getEmailAddress() {
		List<Email> emails = getEmails();
		if (ListUtil.isEmpty(emails)) {
			return null;
		}

		for (Email email: emails) {
			EmailType emailType = email.getEmailType();
			if (emailType == null) {
				continue;
			}

			if (EmailType.MAIN_EMAIL.equals(emailType.getUniqueName())) {
				return email.getAddress();
			}
		}

		return null;
	}

	public Address getMainAddress() {
		List<Address> addresses = getAddresses();
		if (ListUtil.isEmpty(addresses)) {
			return null;
		}

		for (Address address: addresses) {
			AddressType addressType = address.getAddressType();
			if (addressType == null) {
				continue;
			}

			if (AddressType.MAIN_ADDRESS_TYPE.equals(addressType.getUniqueName())) {
				return address;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return getName();
	}

	public List<UserLogin> getLogins() {
		logins = getInitialized(logins);
		return logins;
	}

	public void setLogins(List<UserLogin> logins) {
		this.logins = logins;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public Long getAge() {
		return CoreUtil.getAge(getDateOfBirth());
	}

	public List<UserStatus> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<UserStatus> statuses) {
		this.statuses = statuses;
	}

	public boolean isDeceased()  {
		try {
			UserBusiness userBusiness = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), UserBusiness.class);
			return userBusiness.isDeceased(getId());
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error while checking if user is deceased. Personal ID: " + getPersonalID(), e);
		}
		return false;
	}

}