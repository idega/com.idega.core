/**
 *
 */
package com.idega.core.accesscontrol.data.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.idega.data.MetaDataCapable;
import com.idega.data.bean.Metadata;
import com.idega.util.EncryptionType;
import com.idega.util.IWTimestamp;

@Entity
@Table(name = LoginInfo.ENTITY_NAME)
@Cacheable
public class LoginInfo implements Serializable, MetaDataCapable {

	private static final long serialVersionUID = 2994921102778269086L;

	public static final String ENTITY_NAME = "ic_login_info";
	public static final String COLUMN_LOGIN_INFO_ID = UserLogin.COLUMN_LOGIN_ID;
	private static final String COLUMN_ACCOUNT_ENABLED = "account_enabled";
	private static final String COLUMN_MODIFIED = "modified";
	private static final String COLUMN_DAYS_OF_VALIDITY = "days_of_vality";
	private static final String COLUMN_PASSWORD_EXPIRES = "passwd_expires";
	private static final String COLUMN_ALLOWED_TO_CHANGE = "allowed_to_change";
	private static final String COLUMN_CHANGE_NEXT_TIME = "change_next_time";
	private static final String COLUMN_ENCRYPTION_TYPE = "encryption_type";
	private static final String COLUMN_CREATION_TYPE = "creation_type";

	private static final String META_DATA_ACCESS_CLOSED = "ACCESS_CLOSED";
	private static final String META_DATA_FAILED_ATTEMPT_COUNT = "FAILED_ATTEMPT_COUNT";

	@Id
	@Column(name = COLUMN_LOGIN_INFO_ID)
	private Integer loginInfoID;

	@OneToOne
	@PrimaryKeyJoinColumn(name = COLUMN_LOGIN_INFO_ID, referencedColumnName = UserLogin.COLUMN_LOGIN_ID)
	private UserLogin userLogin;

	@Column(name = COLUMN_ACCOUNT_ENABLED, length = 1)
	private Character accountEnabled;

	@Temporal(TemporalType.DATE)
	@Column(name = COLUMN_MODIFIED)
	private Date modified;

	@Column(name = COLUMN_DAYS_OF_VALIDITY)
	private Integer daysOfValidity;

	@Column(name = COLUMN_PASSWORD_EXPIRES, length = 1)
	private Character passwordExpires;

	@Column(name = COLUMN_ALLOWED_TO_CHANGE, length = 1)
	private Character allowedToChange;

	@Column(name = COLUMN_CHANGE_NEXT_TIME, length = 1)
	private Character changeNextTime;

	@Column(name = COLUMN_ENCRYPTION_TYPE, length = 30)
	private String encryptionType;

	@Column(name = COLUMN_CREATION_TYPE, length = 30)
	private String creationType;

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, targetEntity = Metadata.class)
	@JoinTable(name = "ic_login_info_ic_metadata", joinColumns = { @JoinColumn(name = COLUMN_LOGIN_INFO_ID) }, inverseJoinColumns = { @JoinColumn(name = Metadata.COLUMN_METADATA_ID) })
	private Set<Metadata> metadata;

	public LoginInfo() {
		this.setAccountEnabled(Boolean.TRUE.booleanValue());
		this.setAllowedToChange(Boolean.TRUE.booleanValue());
		this.setChangeNextTime(Boolean.FALSE.booleanValue());
		this.setDaysOfValidity(10000);
		this.setPasswordExpires(Boolean.FALSE.booleanValue());
		this.setEncryptionType(EncryptionType.MD5);
	}

	@SuppressWarnings("unused")
	@PrePersist
	@PreUpdate
	private void setDefaultValues() {
		this.setModified(IWTimestamp.getTimestampRightNow());
	}

	public Integer getId() {
		return this.loginInfoID;
	}

	public void setId(Integer loginInfoID) {
		this.loginInfoID = loginInfoID;
	}

	public UserLogin getUserLogin() {
		return this.userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
		this.loginInfoID = userLogin.getId();
	}

	public boolean getAccountEnabled() {
		if (this.accountEnabled == null) {
			return true;
		}
		return this.accountEnabled.charValue() == 'Y';
	}

	public void setAccountEnabled(boolean accountEnabled) {
		this.accountEnabled = accountEnabled ? 'Y' : 'N';
	}

	public Date getModified() {
		return this.modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Integer getDaysOfValidity() {
		return this.daysOfValidity;
	}

	public void setDaysOfValidity(Integer daysOfValidity) {
		this.daysOfValidity = daysOfValidity;
	}

	public boolean getPasswordExpires() {
		if (this.passwordExpires == null) {
			return false;
		}
		return this.passwordExpires.charValue() == 'Y';
	}

	public void setPasswordExpires(boolean passwordExpires) {
		this.passwordExpires = passwordExpires ? 'Y' : 'N';
	}

	public boolean getAllowedToChange() {
		if (this.allowedToChange == null) {
			return true;
		}
		return this.allowedToChange.charValue() == 'Y';
	}

	public void setAllowedToChange(boolean allowedToChange) {
		this.allowedToChange = allowedToChange ? 'Y' : 'N';
	}

	public boolean getChangeNextTime() {
		if (this.changeNextTime == null) {
			return false;
		}
		return this.changeNextTime.charValue() == 'Y';
	}

	public void setChangeNextTime(boolean changeNextTime) {
		this.changeNextTime = changeNextTime ? 'Y' : 'N';
	}

	public String getEncryptionType() {
		return this.encryptionType;
	}

	public void setEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
	}

	public String getCreationType() {
		return this.creationType;
	}

	public void setCreationType(String creationType) {
		this.creationType = creationType;
	}

	public boolean isLoginExpired() {
		if (getPasswordExpires()) {
			IWTimestamp modified = new IWTimestamp(getModified());
			modified.addDays(getDaysOfValidity());
			return modified.isEarlierThan(IWTimestamp.RightNow());
		}
		return false;
	}

	public boolean getAccessClosed() {
		String value = getMetaData(META_DATA_ACCESS_CLOSED);
		return value != null && value.equals("true");
	}

	public int getFailedAttemptCount() {
		String value = getMetaData(META_DATA_FAILED_ATTEMPT_COUNT);
		return value == null ? 0 : Integer.parseInt(value);
	}

	public void setAccessClosed(boolean closed) {
		setMetaData(META_DATA_ACCESS_CLOSED, closed ? "true" : "false");
	}

	public void setFailedAttemptCount(int attempts) {
		setMetaData(META_DATA_FAILED_ATTEMPT_COUNT, Integer.toString(attempts));
	}

	public Set<Metadata> getMetadata() {
		return this.metadata;
	}

	public void setMetadata(Set<Metadata> metadata) {
		this.metadata = metadata;
	}

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
		// Does nothing...
	}
}