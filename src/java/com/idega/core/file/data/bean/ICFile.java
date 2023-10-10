/**
 *
 */
package com.idega.core.file.data.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.idega.core.file.data.ICFileBMPBean;
import com.idega.core.idgenerator.business.IdGenerator;
import com.idega.core.idgenerator.business.IdGeneratorFactory;
import com.idega.core.localisation.data.bean.ICLocale;
import com.idega.core.version.data.bean.ICItem;
import com.idega.core.version.data.bean.ICVersion;
import com.idega.data.GenericEntity;
import com.idega.data.MetaDataCapable;
import com.idega.data.bean.Metadata;
import com.idega.user.data.bean.User;
import com.idega.util.CoreConstants;
import com.idega.util.DBUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

@Entity
@Table(name = ICFile.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(
			name = ICFile.QUERY_FIND_BY_ID,
			query = "FROM ICFile f WHERE f.fileId = :id"),
	@NamedQuery(name = ICFile.QUERY_FIND_ALL, query = "select f from ICFile f")
})
public class ICFile implements Serializable, MetaDataCapable {

	private static final long serialVersionUID = 3313808078154307237L;

	public static final String ENTITY_NAME = "ic_file";
	public static final String COLUMN_FILE_ID = "ic_file_id";
	private static final String COLUMN_MIME_TYPE = "mime_type";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_CREATION_DATE = "creation_date";
	private static final String COLUMN_MODIFICATION_DATE = "modification_date";
	private static final String COLUMN_FILE_SIZE = "file_size";
	private static final String COLUMN_LOCALE = "ic_locale_id";
	private static final String COLUMN_LOCALIZATION_KEY = "ic_localization_key";
	private static final String COLUMN_DELETED = "deleted";
	private static final String COLUMN_DELETED_BY = "deleted_by";
	private static final String COLUMN_DELETED_WHEN = "deleted_when";
	private static final String COLUMN_HASH = "hash_value";
	private static final String COLUMN_VALUE = "file_value";

	private static final String TREE_TABLE_NAME = ENTITY_NAME + "_tree";
	public static final String QUERY_FIND_BY_ID = "file.findById";
	public static final String QUERY_FIND_ALL = "file.findAll";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_FILE_ID)
	private Integer fileId;

	@ManyToOne
	@JoinColumn(name = COLUMN_MIME_TYPE)
	private ICMimeType mimetype;

	@Column(name = COLUMN_NAME)
	private String name;

	@Column(name = COLUMN_DESCRIPTION, length = 1000)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_CREATION_DATE)
	private Date creationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_MODIFICATION_DATE)
	private Date modificationDate;

	@Column(name = COLUMN_FILE_SIZE)
	private Integer fileSize;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_LOCALE)
	private ICLocale locale;

	@Column(name = COLUMN_LOCALIZATION_KEY)
	private String localizationKey;

	@Column(name = COLUMN_DELETED, length = 1)
	private Character deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_DELETED_BY)
	private User deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_DELETED_WHEN)
	private Date deletedWhen;

	@Column(name = COLUMN_HASH)
	private Integer hashValue;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = ICItem.class)
	@JoinTable(name = "ic_file_ic_item", joinColumns = { @JoinColumn(name = COLUMN_FILE_ID) }, inverseJoinColumns = { @JoinColumn(name = ICItem.COLUMN_ITEM_ID) })
	private List<ICItem> items;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = ICVersion.class)
	@JoinTable(name = "ic_file_ic_version", joinColumns = { @JoinColumn(name = COLUMN_FILE_ID) }, inverseJoinColumns = { @JoinColumn(name = ICVersion.COLUMN_VERSION_ID) })
	private List<ICVersion> versions;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = User.class)
	@JoinTable(name = "ic_file_downloaders", joinColumns = { @JoinColumn(name = COLUMN_FILE_ID) }, inverseJoinColumns = { @JoinColumn(name = User.COLUMN_USER_ID) })
	private List<User> downloaders;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, targetEntity = Metadata.class)
	@JoinTable(name = "ic_file_ic_metadata", joinColumns = { @JoinColumn(name = COLUMN_FILE_ID) }, inverseJoinColumns = { @JoinColumn(name = Metadata.COLUMN_METADATA_ID) })
	private Set<Metadata> metadata;

	@ManyToOne(optional = true)
	@JoinTable(name = TREE_TABLE_NAME, joinColumns = { @JoinColumn(name = "child_" + COLUMN_FILE_ID, referencedColumnName = COLUMN_FILE_ID) }, inverseJoinColumns = { @JoinColumn(name = COLUMN_FILE_ID) })
	private ICFile parent;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = ICFile.class)
	@JoinTable(name = TREE_TABLE_NAME, joinColumns = { @JoinColumn(name = COLUMN_FILE_ID) }, inverseJoinColumns = { @JoinColumn(name = "child_" + COLUMN_FILE_ID, referencedColumnName = COLUMN_FILE_ID) })
	private List<ICFile> children;

	@Column(name = ICFileBMPBean.FILE_URI_IN_REPO, length = 1000)
	private String uriInRepo;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = COLUMN_VALUE)
	private byte[] value;

	@Column(name = GenericEntity.UNIQUE_ID_COLUMN_NAME, unique = true, nullable = false)
	private String uniqueId;

	@Column(name = ICFileBMPBean.COLUMN_TOKEN, length = ICFileBMPBean.TOKEN_MAX_LENGTH, unique = true, nullable = false)
	private String token;

	@Column(name = ICFileBMPBean.COLUMN_PUBLIC, length = 1)
	private Character publiclyAvailable;

	public Integer getFileId() {
		return fileId;
	}

	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	/**
	 * @return the mimetype
	 */
	public ICMimeType getMimetype() {
		return this.mimetype;
	}

	/**
	 * @param mimetype
	 *          the mimetype to set
	 */
	public void setMimetype(ICMimeType mimetype) {
		this.mimetype = mimetype;
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
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return this.creationDate;
	}

	/**
	 * @param creationDate
	 *          the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the modificationDate
	 */
	public Date getModificationDate() {
		return this.modificationDate;
	}

	/**
	 * @param modificationDate
	 *          the modificationDate to set
	 */
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	/**
	 * @return the fileSize
	 */
	public int getFileSize() {
		return this.fileSize != null ? this.fileSize : -1;
	}

	/**
	 * @param fileSize
	 *          the fileSize to set
	 */
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the locale
	 */
	public ICLocale getLocale() {
		locale = DBUtil.getInstance().lazyLoad(locale);
		return this.locale;
	}

	/**
	 * @param locale
	 *          the locale to set
	 */
	public void setLocale(ICLocale locale) {
		this.locale = locale;
	}

	/**
	 * @return the localizationKey
	 */
	public String getLocalizationKey() {
		return this.localizationKey;
	}

	/**
	 * @param localizationKey
	 *          the localizationKey to set
	 */
	public void setLocalizationKey(String localizationKey) {
		this.localizationKey = localizationKey;
	}

	/**
	 * @return the deleted
	 */
	public boolean isDeleted() {
		if (this.deleted == null) {
			return false;
		}
		return this.deleted == CoreConstants.CHAR_Y;
	}

	/**
	 * @param deleted
	 *          the deleted to set
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted ? CoreConstants.CHAR_Y : CoreConstants.CHAR_N;
	}

	/**
	 * @return the deletedBy
	 */
	public User getDeletedBy() {
		deletedBy = DBUtil.getInstance().lazyLoad(deletedBy);
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

	public Integer getHash() {
		return this.hashValue;
	}

	public void setHash(Integer hashValue) {
		this.hashValue = hashValue;
	}

	/**
	 * @return the items
	 */
	public List<ICItem> getItems() {
		items = DBUtil.getInstance().lazyLoad(items);
		return this.items;
	}

	/**
	 * @param items
	 *          the items to set
	 */
	public void setItems(List<ICItem> items) {
		this.items = items;
	}

	/**
	 * @return the versions
	 */
	public List<ICVersion> getVersions() {
		versions = DBUtil.getInstance().lazyLoad(versions);
		return this.versions;
	}

	/**
	 * @param versions
	 *          the versions to set
	 */
	public void setVersions(List<ICVersion> versions) {
		this.versions = versions;
	}

	/**
	 * @return Returns the parent.
	 */
	public ICFile getParent() {
		return this.parent;
	}

	/**
	 * @param parent The parent to set.
	 */
	public void setParent(ICFile parent) {
		this.parent = parent;
	}

	/**
	 * @return Returns the children.
	 */
	public List<ICFile> getChildren() {
		children = DBUtil.getInstance().lazyLoad(children);
		return this.children;
	}

	/**
	 * @param children The children to set.
	 */
	public void setChildren(List<ICFile> children) {
		this.children = children;
	}

	/**
	 * @return the metadata
	 */
	public Set<Metadata> getMetadata() {
		metadata = DBUtil.getInstance().lazyLoad(metadata);
		return this.metadata;
	}

	/**
	 * @param metadata
	 *          the metadata to set
	 */
	public void setMetadata(Set<Metadata> metadata) {
		this.metadata = metadata;
	}

	public void addDownloadedBy(User downloader) {
		getDownloadedBy().add(downloader);
	}

	public Collection<User> getDownloadedBy() {
		if (!DBUtil.getInstance().isInitialized(downloaders)) {
			downloaders = DBUtil.getInstance().lazyLoad(downloaders);
			if (downloaders == null) {
				downloaders = new ArrayList<>();
			} else {
				downloaders = new ArrayList<>(downloaders);
			}
		}
		return downloaders;
	}

	public void removeDownloadedBy(User downloader) {
		getDownloadedBy().remove(downloader);
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

	@PrePersist
	@PreUpdate
	public void setDefaultValues() {
		if (StringUtil.isEmpty(uniqueId)) {
			IdGenerator uidGenerator = IdGeneratorFactory.getUUIDGenerator();
			setUniqueId(uidGenerator.generateId());
		}
		if (StringUtil.isEmpty(token)) {
			setToken(StringHandler.getRandomString(ICFileBMPBean.TOKEN_MAX_LENGTH));
		}
	}

	public String getUriInRepo() {
		return uriInRepo;
	}

	public void setUriInRepo(String uriInRepo) {
		this.uriInRepo = uriInRepo;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Boolean getPublicl() {
		if (this.publiclyAvailable == null) {
			return false;
		}
		return this.publiclyAvailable == CoreConstants.CHAR_Y;
	}

	public void setPubliclyAvailable(Boolean publiclyAvailable) {
		this.publiclyAvailable = publiclyAvailable != null && publiclyAvailable ? CoreConstants.CHAR_Y : CoreConstants.CHAR_N;
	}

}