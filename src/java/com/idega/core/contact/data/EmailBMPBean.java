package com.idega.core.contact.data;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.business.IBORuntimeException;
import com.idega.core.data.GenericTypeBMPBean;
import com.idega.core.user.data.User;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.data.query.Column;
import com.idega.data.query.InCriteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.user.data.Group;
import com.idega.user.data.GroupBMPBean;
import com.idega.user.data.UserBMPBean;
/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu?mundur ?g?st S?mundsson</a>
 * @version 1.0
 */
public class EmailBMPBean
	extends ContactBmpBean
	implements com.idega.core.contact.data.Email, com.idega.core.contact.data.EmailDataView{
	private static final long serialVersionUID = -6649005980606226999L;
	public final static String SQL_TABLE_NAME = "IC_EMAIL";
	public final static String SQL_COLUMN_EMAIL = "ADDRESS";
	public final static String SQL_COLUMN_TYPE = "IC_EMAIL_TYPE_ID";
	public final static String SQL_COLUMN_EMAIL_ID = "IC_EMAIL_ID";
	public EmailBMPBean()
	{
		super();
	}
	public EmailBMPBean(int id) throws SQLException
	{
		super(id);
	}
	@Override
	public void initializeAttributes()
	{
		this.addAttribute(this.getIDColumnName());
		this.addAttribute(getColumnNameAddress(), "Email address", true, true, String.class, 255);
		addManyToOneRelationship(getColumnNameEmailTypeId(), "Type", EmailType.class);
		this.addManyToManyRelationShip(User.class, "ic_user_email");
		super.initializeAttributes();
	}
	
	@Override
	public void remove() throws RemoveException {
		try {
			idoRemoveFrom(User.class);
		} catch (Exception e) {
		}
		super.remove();
	}
	
	@Override
	public String getEntityName()
	{
		return SQL_TABLE_NAME;
	}
	public static String getColumnNameAddress()
	{
		return SQL_COLUMN_EMAIL;
	}
	public static String getColumnNameEmailId()
	{
		return SQL_COLUMN_EMAIL_ID;
	}
	public static String getColumnNameEmailTypeId()
	{
		return SQL_COLUMN_TYPE;
	}
	public void setEmailAddress(String address)
	{
		setColumn(getColumnNameAddress(), address);
	}
	public String getEmailAddress()
	{
		return getStringColumnValue(getColumnNameAddress());
	}

	public String getEmailAddressMailtoFormatted()
	{
		return getEmailAddressMailtoFormattedWithSubject(null);
	}

	public String getEmailAddressMailtoFormattedWithSubject(String subject) {
		String emailAddress = getStringColumnValue(getColumnNameAddress());
		if (emailAddress != null && !emailAddress.equals("")) {
			String subjectString = "";
			if (subject != null && !subject.equals("")) {
				subjectString = "?subject="+subject;
			}
			emailAddress = "<a href=\"mailto:"+emailAddress+ subjectString +"\">"+emailAddress+"</a>";
		}
		return emailAddress;
	}

	public void setEmailType(EmailType emailType) {
		setColumn(getColumnNameEmailTypeId(), emailType);
	}

	public EmailType getEmailType() {
		return (EmailType) getColumnValue(getColumnNameEmailTypeId());
	}

	public void setEmailTypeId(int id)
	{
		setColumn(getColumnNameEmailTypeId(), id);
	}
	public int getEmailTypeId()
	{
		return getIntColumnValue(getColumnNameEmailTypeId());
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws FinderException
	 * @throws RemoteException
	 *
	 */
	public Collection ejbFindEmailsForUser(com.idega.user.data.User user) throws FinderException
	{
		Object userId = user.getPrimaryKey();
		return executeQuery(com.idega.user.data.User.class, UserBMPBean.SQL_RELATION_EMAIL, userId);
	}

	/**
	 *
	 * @param iUserId
	 * @return
	 * @throws FinderException
	 *
	 */
	public Collection ejbFindEmailsForUser(int iUserId) throws FinderException
	{
		Object userId = new Integer(iUserId);
		return executeQuery(com.idega.user.data.User.class, UserBMPBean.SQL_RELATION_EMAIL, userId);
	}

	public Object ejbFindEmailForUser(com.idega.user.data.User user, EmailType emailType) throws FinderException {
		return findEmailForUser(user, emailType.getUniqueName());
	}

	public Object ejbFindEmailForGroup(Group group, EmailType emailType) throws FinderException {
		return findEmailForGroup(group, emailType.getUniqueName());
	}

	/**
	 * Just a shortcut for the main email type.
	 *
	 * @param user
	 * @return
	 * @throws FinderException
	 * @throws RemoteException
	 */
	public Object ejbFindMainEmailForUser(com.idega.user.data.User user) throws FinderException {
		return findEmailForUser(user,EmailTypeBMPBean.MAIN_EMAIL);
	}

	/**
	 * Just a shortcut for the main email type.
	 *
	 * @param user
	 * @return
	 * @throws FinderException
	 * @throws RemoteException
	 */
	public Object ejbFindMainEmailForGroup(Group group) throws FinderException {
		return findEmailForGroup(group, EmailTypeBMPBean.MAIN_EMAIL);
	}

	private Object findEmailForGroup(Group group, String uniqueNameOfEmailType) throws FinderException {
		return findEmailForUserOrGroup(Group.class, GroupBMPBean.SQL_RELATION_EMAIL, group, uniqueNameOfEmailType);
	}

	private Object findEmailForUser(com.idega.user.data.User user, String uniqueNameOfEmailType) throws FinderException  {
		return findEmailForUserOrGroup(com.idega.user.data.User.class, UserBMPBean.SQL_RELATION_EMAIL, user, uniqueNameOfEmailType);
	}


	private Object findEmailForUserOrGroup(Class userOrGroupClass, String relationTableName, IDOEntity userOrGroup, String uniqueNameOfEmailType) throws FinderException {
		Object userOrGroupId = userOrGroup.getPrimaryKey();
		Object pk = null;
		try {
			pk = executeQuery(userOrGroupClass, relationTableName, userOrGroupId, uniqueNameOfEmailType);
		}
		catch (FinderException ex) {
			pk  = null;
		}
		if (pk != null) {
			return pk;
		}
		// nothing found?
		// repairing email stuff - that usually done only one time per user
		// choose the latest email (that is the email with the greatest primary key) that has no email type
		// try to trepair data only for main email type
		if (! EmailTypeBMPBean.MAIN_EMAIL.equals(uniqueNameOfEmailType)) {
			throw new FinderException();
		}
		Collection coll = executeQuery(userOrGroupClass, relationTableName, userOrGroupId);
		// if coll is null there are not any emails
		if (coll == null || coll.isEmpty()) {
			throw new FinderException();
		}
		Email resultEmail = null;
		Integer resultEmailPrimaryKey = null;
		EmailHome home = (EmailHome) getEJBLocalHome();
		Iterator allEmailsIterator = coll.iterator();
		while (allEmailsIterator.hasNext()) {
			Integer anEmailPrimaryKey = (Integer) allEmailsIterator.next();
			Email anEmail = home.findByPrimaryKey(anEmailPrimaryKey);
			EmailType emailType = anEmail.getEmailType();
			// do not find emails with a set type
			if (emailType == null) {
				// choose the one with the greatest primary key
				if (resultEmailPrimaryKey == null || ( anEmailPrimaryKey.compareTo(resultEmailPrimaryKey)) > 0) {
					resultEmailPrimaryKey = anEmailPrimaryKey;
					resultEmail = anEmail;
				}
			}
		}
		// hopefully we found something - maybe not
		if (resultEmail == null) {
			// that means there are only emails with a different type than "main" - quite strange....
			throw new FinderException();
		}
		// the found email has no type yet
		// set the type to main email
		EmailTypeHome emailTypeHome;
		try {
			emailTypeHome = (EmailTypeHome) IDOLookup.getHome(EmailType.class);
			EmailType mainEmailType = emailTypeHome.findMainEmailType();
			resultEmail.setEmailType(mainEmailType);
			resultEmail.store();
			return resultEmailPrimaryKey;
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
			throw new IBORuntimeException();
		}
		catch (RemoteException e) {
			e.printStackTrace();
			throw new IBORuntimeException();
		}
		catch (FinderException e) {
			// ! main email type is always set !
			e.printStackTrace();
			throw new IBORuntimeException();
		}

	}


	private Collection executeQuery(Class userOrGroupClass, String relationTableName, Object userOrGroupId) throws FinderException {
		IDOQuery query = getFromQuery(relationTableName);
		appendWhere(query, userOrGroupClass, userOrGroupId);
		return idoFindPKsByQuery(query);
	}

	private Object executeQuery(Class userOrGroupClass, String relationTableName, Object userOrGroupId, String uniqueNameOfEmailType) throws FinderException {
		IDOQuery query = getFromQuery(relationTableName);

		query.append(",");
		Table emailTypeTable = new Table(EmailType.class);
		query.append(emailTypeTable).append(" type ");

		appendWhere(query, userOrGroupClass, userOrGroupId);

		String emailTypePrimaryKey = null;
		String emailTypeUniqueName = GenericTypeBMPBean.getColumnNameUniqueName();
		try {
			emailTypePrimaryKey = emailTypeTable.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
		}
		catch (IDOCompositePrimaryKeyException e) {
			throw new FinderException(e.getMessage());
		}
		query.appendAnd();
		query.append(" email.").append(getColumnNameEmailTypeId()).appendEqualSign().append("type.").append(emailTypePrimaryKey);
		query.appendAnd();
		query.append("type.").append(emailTypeUniqueName).appendEqualSign().appendQuoted(uniqueNameOfEmailType);

		return idoFindOnePKByQuery(query);
	}

	private IDOQuery getFromQuery(String relationTableName) {
		IDOQuery query = idoQuery();
		query.appendSelect();
		query.append("email.*");
		query.append(" from ");
		query.append(getTableName()).append(" email,");
		query.append(relationTableName).append(" iue ");
		return query;
	}

	private void appendWhere(IDOQuery query, Class userOrGroupClass, Object userOrGroupId) throws FinderException {
		Table userOrGroupTable = new Table(userOrGroupClass);
		String userOrGroupPrimaryKey = null;
		try {
			userOrGroupPrimaryKey = userOrGroupTable.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
		}
		catch (IDOCompositePrimaryKeyException e) {
			throw new FinderException(e.getMessage());
		}
		query.appendWhere();
		query.append(" email.").append(getIDColumnName()).appendEqualSign().append("iue.").append(getIDColumnName());
		query.appendAnd();
		query.append("iue.").append(userOrGroupPrimaryKey).appendEqualSign().append(userOrGroupId);
	}

	@SuppressWarnings("unchecked")
	public Collection<Integer> ejbFindMainEmailsForUsers(Collection<com.idega.user.data.User> users) throws FinderException {
		Table emails = new Table(this);
		SelectQuery query = new SelectQuery(emails);

		Table emailTypes = new Table(EmailType.class);

		query.addColumn(new Column(emails, getIDColumnName()));

		query.addJoin(emails, SQL_COLUMN_TYPE, emailTypes, SQL_COLUMN_TYPE);
		query.addCriteria(new MatchCriteria(emailTypes, GenericTypeBMPBean.getColumnNameUniqueName(), MatchCriteria.EQUALS, EmailTypeBMPBean.MAIN_EMAIL));

		Table usersEmails = new Table(UserBMPBean.SQL_RELATION_EMAIL);
		query.addJoin(emails, getIDColumnName(), usersEmails, getIDColumnName());

		Table usersTable = new Table(com.idega.user.data.User.class);
		query.addJoin(usersEmails, com.idega.user.data.User.FIELD_USER_ID, usersTable, com.idega.user.data.User.FIELD_USER_ID);
		query.addCriteria(new InCriteria(usersTable, com.idega.user.data.User.FIELD_USER_ID, users));

    	return idoFindPKsByQuery(query);
	}
	
}
