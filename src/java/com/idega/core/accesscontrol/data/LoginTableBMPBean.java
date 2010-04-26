package com.idega.core.accesscontrol.data;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOException;
import com.idega.data.query.CountColumn;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.user.data.UserBMPBean;
import com.idega.util.EncryptionType;

public class LoginTableBMPBean extends GenericEntity implements LoginTable, EncryptionType {

	public static String className = LoginTable.class.getName();
	public static String _COLUMN_PASSWORD = "USR_PASSWORD";
	private static final String ENTITY_NAME = "IC_LOGIN";
	private static final String COLUMN_USER_LOGIN = "USER_LOGIN";
	private static final String COLUMN_USER_PASSWORD = "USER_PASSWORD";
	private static final String COLUMN_LAST_CHANGED = "LAST_CHANGED";
	private static final String COLUMN_CHANGED_BY_USER = "CHANGED_BY_USER_ID";
	private static final String COLUMN_CHANGED_BY_GROUP = "CHANGED_BY_GROUP_ID";
	
	private static final String COLUMN_COUNT_SENT_TO_BANK = "bank_count";
	
	private static final String COLUMN_LOGIN_TYPE = "LOGIN_TYPE";
	private transient String unEncryptedUserPassword;

	@Override
	public void initializeAttributes() {
		addAttribute(this.getIDColumnName());
		addAttribute(getColumnNameUserID(), "User id", true, true, Integer.class, "many-to-one", User.class);
		addAttribute(getUserLoginColumnName(), "User name", true, true, String.class, 32);
		addAttribute(getNewUserPasswordColumnName(), "Password (encrypted)", true, true, String.class, 255);
		// deprecated column
		addAttribute(getOldUserPasswordColumnName(), "Password (deprecated)", true, true, String.class, 20);
		addAttribute(getLastChangedColumnName(), "Last changed", true, true, Timestamp.class);
		addAttribute(getLoginTypeColumnName(), "Login type", true, true, String.class, 32);
		addAttribute(COLUMN_CHANGED_BY_USER, "Last changed by user id", true, true, Integer.class, "many-to-one", User.class);
		addAttribute(COLUMN_CHANGED_BY_GROUP, "Last changed by group id", true, true, Integer.class, "many-to-one", Group.class);
		
		addAttribute(COLUMN_COUNT_SENT_TO_BANK, "bank count", Integer.class);
		
		setNullable(getUserLoginColumnName(), false);
		setUnique(getUserLoginColumnName(), true);
		
		addIndex("IDX_LOGIN_REC_2", getUserIDColumnName());
		addIndex("IDX_LOGIN_REC_3", new String[]{getUserIDColumnName(), getLoginTypeColumnName()});
		addIndex("IDX_LOGIN_REC_4", new String[]{getUserIDColumnName(), getUserLoginColumnName()});
		addIndex("IDX_LOGIN_REC_5", getUserLoginColumnName());
	    
		getEntityDefinition().setBeanCachingActiveByDefault(true);
		
	}

	@Override
	public void setDefaultValues() {
		setColumn(getOldUserPasswordColumnName(), "rugl");
	}

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public static String getUserLoginColumnName() {
		return COLUMN_USER_LOGIN;
	}

	public static String getOldUserPasswordColumnName() {
		return COLUMN_USER_PASSWORD;
	}

	public static String getNewUserPasswordColumnName() {
		return _COLUMN_PASSWORD;
	}

	public static String getLastChangedColumnName() {
		return COLUMN_LAST_CHANGED;
	}

	public static String getLoginTypeColumnName() {
		return COLUMN_LOGIN_TYPE;
	}

	public static String getUserPasswordColumnName() {
		System.out.println("LoginTable - getUserPassordColumnName()");
		System.out.println("caution: not save because of changes in entity");
		Exception e = new Exception();
		e.printStackTrace();
		return _COLUMN_PASSWORD;
	}

	public static String getColumnNameUserID() {
		return UserBMPBean.getColumnNameUserID();
	}

	/**
	 * just sets the password column value as this string without encoding.
	 */
	public void setUserPasswordInClearText(String password) {
		setColumn(getNewUserPasswordColumnName(), password);
	}

	/**
	 * just returns the password column value as is.
	 */
	public String getUserPasswordInClearText() {
		return getStringColumnValue(getNewUserPasswordColumnName());
	}

	public String getUserPassword() {
		String str = null;
		try {
			str = getStringColumnValue(getNewUserPasswordColumnName());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			// str = null;
		}
		if (str == null) {
			try {
				String oldPass = getStringColumnValue(getOldUserPasswordColumnName());
				if (oldPass != null) {
					char[] pass = new char[oldPass.length() / 2];
					try {
						for (int i = 0; i < pass.length; i++) {
							pass[i] = (char) Integer.decode("0x" + oldPass.charAt(i * 2) + oldPass.charAt((i * 2) + 1)).intValue();
						}
						oldPass = String.valueOf(pass);
					}
					catch (Exception ex) {
						ex.printStackTrace();
						// oldPass = oldPass;
					}
					LoginTable table = ((LoginTableHome) getIDOHome(LoginTable.class)).findByPrimaryKey(this.getPrimaryKey());
					table.setUserPassword(oldPass);
					table.store();
					this.setUserPassword(oldPass);
					return oldPass;
					// this.setColumnAsNull(getOldUserPasswordColumnName());
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
				return getStringColumnValue(getOldUserPasswordColumnName());
			}
		}
		if (str != null) {
			char[] pass = new char[str.length() / 2];
			try {
				for (int i = 0; i < pass.length; i++) {
					pass[i] = (char) Integer.decode("0x" + str.charAt(i * 2) + str.charAt((i * 2) + 1)).intValue();
				}
				return String.valueOf(pass);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				return str;
			}
		}
		return str;
	}

	public void setUserPassword(String userPassword) {
		try {
			String str = "";
			char[] pass = userPassword.toCharArray();
			for (int i = 0; i < pass.length; i++) {
				String hex = Integer.toHexString(pass[i]);
				while (hex.length() < 2) {
					String s = "0";
					s += hex;
					hex = s;
				}
				str += hex;
			}
			if (str.equals("") && !userPassword.equals("")) {
				str = null;
			}
			setColumn(getNewUserPasswordColumnName(), str);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			setColumn(getNewUserPasswordColumnName(), userPassword);
		}
	}

	public void setUserLogin(String userLogin) {
		setColumn(getUserLoginColumnName(), userLogin);
	}

	public String getUserLogin() {
		return getStringColumnValue(getUserLoginColumnName());
	}

	public int getUserId() {
		return getIntColumnValue(getUserIDColumnName());
	}

	public User getUser() {
		return (User) getColumnValue(getUserIDColumnName());
	}

	public void setUserId(Integer userId) {
		setColumn(getUserIDColumnName(), userId);
	}

	public void setUserId(int userId) {
		setColumn(getUserIDColumnName(), userId);
	}

	public void setUser(User user){
		Integer userId = (Integer)user.getPrimaryKey();
		setUserId(userId);
	}
	
	public static String getUserIDColumnName() {
		return UserBMPBean.getColumnNameUserID();
	}

	public void setChangedByGroup(Group group){
		setColumn(COLUMN_CHANGED_BY_GROUP, group);
	}
	
	public void setChangedByGroupId(int changedByGroupId){
		setColumn(COLUMN_CHANGED_BY_GROUP, changedByGroupId);
	}
	
	public int getChangedByGroupId(){
		return getIntColumnValue(COLUMN_CHANGED_BY_GROUP);
	}
	
	public Group getChangedByGroup(){
		return (Group) getColumnValue(COLUMN_CHANGED_BY_GROUP);
	}
	
	public void setChangedByUser(User changedByUser){
		setColumn(COLUMN_CHANGED_BY_USER, changedByUser);
	}
	
	public void setChangedByUserId(int changedByUserId){
		setColumn(COLUMN_CHANGED_BY_USER, changedByUserId);
	}
	
	public int getChangedByUserId(){
		return getIntColumnValue(COLUMN_CHANGED_BY_USER);
	}
	
	public User getChangedByUser(){
		return (User) getColumnValue(COLUMN_CHANGED_BY_USER);
	}
	
	public void setLastChanged(Timestamp when) {
		setColumn(getLastChangedColumnName(), when);
	}

	public Timestamp getLastChanged() {
		return ((Timestamp) getColumnValue(getLastChangedColumnName()));
	}

	public void setBankCount(int bankCount) {
		setColumn(COLUMN_COUNT_SENT_TO_BANK, bankCount);
	}
	
	public int getBankCount() {
		return getIntColumnValue(COLUMN_COUNT_SENT_TO_BANK, 2);
	}
	
	/**
	 * Sets both the intented encrypted password and the original unencrypted
	 * password for temporary retrieval
	 */
	public void setUserPassword(String encryptedPassword, String unEncryptedPassword) {
		this.unEncryptedUserPassword = unEncryptedPassword;
		this.setUserPassword(encryptedPassword);
	}

	/**
	 * Gets the original password if the record is newly created, therefore it can
	 * be retrieved , if this is not a newly created record the exception
	 * PasswordNotKnown is thrown
	 */
	public String getUnencryptedUserPassword() throws PasswordNotKnown {
		if (this.unEncryptedUserPassword == null) {
			throw new PasswordNotKnown(this.getUserLogin());
		}
		else {
			return this.unEncryptedUserPassword;
		}
	}

	public void setLoginType(String loginType) {
		setColumn(getLoginTypeColumnName(), loginType);
	}

	public String getLoginType() {
		return getStringColumnValue(getLoginTypeColumnName());
	}
	
	public Object ejbFindLoginForUser(User user) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(getColumnNameUserID()), MatchCriteria.EQUALS, user));
		
		return idoFindOnePKByQuery(query);
	}

	public Collection ejbFindLoginsForUser(User user) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(getColumnNameUserID()), MatchCriteria.EQUALS, user));
		
		return idoFindPKsByQuery(query);
	}

	public Object ejbFindLoginForUser(int userID) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(getColumnNameUserID()), MatchCriteria.EQUALS, userID));
		
		return idoFindOnePKByQuery(query);
	}

	public Collection ejbFindLoginsForUser(int userID) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(getColumnNameUserID()), MatchCriteria.EQUALS, userID));
		
		return idoFindPKsByQuery(query);
	}
	
	public int ejbHomeGetNumberOfLogins(String userName) throws IDOException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new CountColumn(table, getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(getUserLoginColumnName()), MatchCriteria.EQUALS, userName));

		return idoGetNumberOfRecords(query);
	}

	/**
	 * Gets the <code>LoginTable</code> object for the given login.
	 * 
	 * @param login
	 * @return
	 * @throws FinderException
	 */
	public Object ejbFindByLogin(String login) throws FinderException {
		
		//try to find it cached:
		Collection cachedEntities = this.getCachedEntities();
		for (Iterator iter = cachedEntities.iterator(); iter.hasNext();) {
			LoginTable loginEntity = (LoginTable) iter.next();
			if(loginEntity!=null){
				String userLogin = loginEntity.getUserLogin();
				if(userLogin!=null){
					if(userLogin.equals(login)){
						return loginEntity.getPrimaryKey();
					}
				}
			}
		}
		
		//if it is not found in the cache query the database:
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(getUserLoginColumnName()), MatchCriteria.EQUALS, login));

		return idoFindOnePKByQuery(query);
	}
	
	public Object ejbFindByUserAndLogin(User user, String login) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));	
		query.addCriteria(new MatchCriteria(table.getColumn(getUserLoginColumnName()), MatchCriteria.EQUALS, login));
		query.addCriteria(new MatchCriteria(table.getColumn(getColumnNameUserID()), MatchCriteria.EQUALS, user));

		return idoFindOnePKByQuery(query);
	}

	public Object ejbFindByUserAndType(User user, String loginType) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(getLoginTypeColumnName()), MatchCriteria.EQUALS, loginType));
		query.addCriteria(new MatchCriteria(table.getColumn(getColumnNameUserID()), MatchCriteria.EQUALS, user));

		return idoFindOnePKByQuery(query);
	}

	/**
	 * <p>
	 * Finds the default (i.e. a login with no login type) for the User with id userId
	 * </p>
	 * @param userID
	 * @return
	 * @throws FinderException 
	 */
	public Object ejbFindDefaultLoginForUser(int userID) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(getColumnNameUserID()), MatchCriteria.EQUALS, userID));
		query.addCriteria(new MatchCriteria(table.getColumn(getLoginTypeColumnName()), MatchCriteria.IS,(String)null));		
		return idoFindOnePKByQuery(query);
	}

	public Object ejbFindDefaultLoginForUser(User user) throws FinderException {
		Integer iUserId = (Integer)user.getPrimaryKey();
		int userId = iUserId.intValue();
		return ejbFindDefaultLoginForUser(userId);
	}
}