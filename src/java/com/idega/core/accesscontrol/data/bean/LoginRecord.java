/**
 *
 */
package com.idega.core.accesscontrol.data.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.idega.user.data.bean.User;

@Entity
@Table(name = LoginRecord.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "loginRecord.findAllByLogin", query = "select l from LoginRecord l where l.login = :login"),
	@NamedQuery(name = "loginRecord.findLastByUser", query = "select l from LoginRecord l where l.user = :user order by l.inStamp desc"),
	@NamedQuery(name = "loginRecord.findLastByLogin", query = "select l from LoginRecord l where l.login = :login order by l.inStamp desc"),
	@NamedQuery(name = LoginRecord.GET_NUMBER_OF_LOGINS_FOR_USER, query = "select count(l) from LoginRecord l where l.user = :user")
})
public class LoginRecord implements Serializable {

	private static final long serialVersionUID = 874430366534007413L;

	public static final String GET_NUMBER_OF_LOGINS_FOR_USER = "loginRecord.getNumberOfLoginsForUser";

	public static final String ENTITY_NAME = "ic_login_rec";
	public static final String COLUMN_LOGIN_RECORD_ID = "ic_login_rec_id";
	public static final String COLUMN_LOGIN = "ic_login_id";

	private static final String COLUMN_IN = "in_stamp";
	private static final String COLUMN_OUT = "out_stamp";
	private static final String COLUMN_IP_ADDRESS = "ip";
	private static final String COLUMN_USER = "user_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_LOGIN_RECORD_ID)
	private Integer loginRecordID;

	@ManyToOne
	@JoinColumn(name = COLUMN_LOGIN)
	private UserLogin login;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_IN)
	private Date inStamp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_OUT)
	private Date outStamp;

	@Column(name = COLUMN_IP_ADDRESS, length = 41)
	private String ipAddress;

	@ManyToOne
	@JoinColumn(name = COLUMN_USER)
	private User user;

	public Integer getId() {
		return this.loginRecordID;
	}

	public void setId(Integer loginRecordID) {
		this.loginRecordID = loginRecordID;
	}

	public UserLogin getLogin() {
		return this.login;
	}

	public void setLogin(UserLogin login) {
		this.login = login;
	}

	public Date getInStamp() {
		return this.inStamp;
	}

	public void setInStamp(Date inStamp) {
		this.inStamp = inStamp;
	}

	public Date getOutStamp() {
		return this.outStamp;
	}

	public void setOutStamp(Date outStamp) {
		this.outStamp = outStamp;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}