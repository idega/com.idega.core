/**
 *
 */
package com.idega.core.accesscontrol.data.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.idega.core.accesscontrol.data.PasswordNotKnown;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;
import com.idega.util.Encrypter;
import com.idega.util.IWTimestamp;

@Entity
@Table(name = UserLogin.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "login.findAllByUser", query = "select l from UserLogin l where l.user = :user"),
	@NamedQuery(name = "login.findByLogin", query = "select l from UserLogin l where l.userLogin = :userLogin"),
	@NamedQuery(name = "login.findByUserAndLogin", query = "select l from UserLogin l where l.user = :user and l.userLogin = :userLogin"),
	@NamedQuery(name = "login.findByUserAndType", query = "select l from UserLogin l where l.user = :user and l.loginType = :loginType"),
	@NamedQuery(name = "login.findDefaultLoginForUser", query = "select l from UserLogin l where l.user = :user and l.loginType is null"),
	@NamedQuery(name = UserLogin.QUERY_FIND_DEFAULT_LOGIN_BY_UUID, query = "SELECT l FROM UserLogin l JOIN l.user u WHERE (u.uniqueId = :"
		+User.PROP_UNIQUE_ID+") AND l.loginType IS null")
})
@Cacheable
public class UserLogin implements Serializable {

	private static final long serialVersionUID = -2164709920312090204L;

	public static final String ENTITY_NAME = "ic_login";
	public static final String COLUMN_LOGIN_ID = "ic_login_id";
	private static final String COLUMN_USER = "ic_user_id";
	private static final String COLUMN_USER_LOGIN = "user_login";
	private static final String COLUMN_USER_PASSWORD = "usr_password";
	private static final String COLUMN_LAST_CHANGED = "last_changed";
	private static final String COLUMN_CHANGED_BY_USER = "changed_by_user_id";
	private static final String COLUMN_CHANGED_BY_GROUP = "changed_by_group_id";
	private static final String COLUMN_LOGIN_TYPE = "login_type";
	private static final String COLUMN_COUNT_SENT_TO_BANK = "bank_count";

	public static final String QUERY_FIND_DEFAULT_LOGIN_BY_UUID = "login.findDefaultLoginByUUId";
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_LOGIN_ID)
	private Integer loginID;

	@ManyToOne
	@JoinColumn(name = COLUMN_USER)
	private User user;

	@Column(name = COLUMN_USER_LOGIN, length = 32, nullable = false, unique = true)
	private String userLogin;

	@Column(name = COLUMN_USER_PASSWORD)
	private String userPassword;

	@Transient
	private String unencryptedUserPassword;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_LAST_CHANGED)
	private Date lastChanged;

	@ManyToOne
	@JoinColumn(name = COLUMN_CHANGED_BY_USER)
	private User changedByUser;

	@ManyToOne
	@JoinColumn(name = COLUMN_CHANGED_BY_GROUP)
	private Group changedByGroup;

	@Column(name = COLUMN_LOGIN_TYPE, length = 32)
	private String loginType;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "userLogin")
	private LoginInfo loginInfo;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = LoginRecord.class)
	@JoinColumn(name = COLUMN_LOGIN_ID)
	private List<LoginRecord> loginRecords;

	@SuppressWarnings("unused")
	@PrePersist
	@PreUpdate
	private void updateLastChanged() {
		setLastChanged(IWTimestamp.getTimestampRightNow());
	}

	@Column(name = COLUMN_COUNT_SENT_TO_BANK)
	private Integer bankCount;

	public Integer getId() {
		return this.loginID;
	}

	public void setId(Integer loginID) {
		this.loginID = loginID;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUserLogin() {
		return this.userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getUserPassword() {
		return Encrypter.hexToAscii(this.userPassword);
	}

	public void setUserPassword(String userPassword) {
		setUserPassword(Encrypter.asciiToHex(Encrypter.encryptOneWay(userPassword)), userPassword);
	}

	/**
	 * Sets both the intented encrypted password and the original unencrypted
	 * password for temporary retrieval
	 */
	public void setUserPassword(String encryptedPassword, String unencryptedPassword) {
		this.unencryptedUserPassword = unencryptedPassword;
		this.userPassword = encryptedPassword;
	}

	/**
	 * Gets the original password if the record is newly created, therefore it can
	 * be retrieved , if this is not a newly created record the exception
	 * PasswordNotKnown is thrown
	 */
	public String getUnencryptedUserPassword() throws PasswordNotKnown {
		if (this.unencryptedUserPassword == null) {
			throw new PasswordNotKnown(getUserLogin());
		}
		else {
			return this.unencryptedUserPassword;
		}
	}

	public Date getLastChanged() {
		return this.lastChanged;
	}

	public void setLastChanged(Date lastChanged) {
		this.lastChanged = lastChanged;
	}

	public User getChangedByUser() {
		return this.changedByUser;
	}

	public void setChangedByUser(User changedByUser) {
		this.changedByUser = changedByUser;
	}

	public Group getChangedByGroup() {
		return this.changedByGroup;
	}

	public void setChangedByGroup(Group changedByGroup) {
		this.changedByGroup = changedByGroup;
	}

	public String getLoginType() {
		return this.loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public LoginInfo getLoginInfo() {
		return this.loginInfo;
	}

	public List<LoginRecord> getLoginRecords() {
		return this.loginRecords;
	}

	public void setLoginRecords(List<LoginRecord> loginRecords) {
		this.loginRecords = loginRecords;
	}

	public Integer getBankCount() {
		return bankCount;
	}

	public void setBankCount(Integer bankCount) {
		this.bankCount = bankCount;
	}

}