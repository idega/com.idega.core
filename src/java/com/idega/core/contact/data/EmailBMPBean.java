package com.idega.core.contact.data;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.core.user.data.User;
import com.idega.data.IDOQuery;
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
	extends com.idega.data.GenericEntity
	implements com.idega.core.contact.data.Email, com.idega.core.contact.data.EmailDataView
{
	public final static String SQL_TABLE_NAME = "IC_EMAIL";
	public final static String SQL_COLUMN_EMAIL = "ADDRESS";
	public final static String SQL_COLUMN_TYPE = "IC_EMAIL_TYPE_ID";
	public EmailBMPBean()
	{
		super();
	}
	public EmailBMPBean(int id) throws SQLException
	{
		super(id);
	}
	public void initializeAttributes()
	{
		this.addAttribute(this.getIDColumnName());
		this.addAttribute(getColumnNameAddress(), "Email address", true, true, String.class, 255);
		addManyToOneRelationship(getColumnNameEmailTypeId(), "Type", EmailType.class);
		this.addManyToManyRelationShip(User.class, "ic_user_email");
	}
	public String getEntityName()
	{
		return SQL_TABLE_NAME;
	}
	public static String getColumnNameAddress()
	{
		return SQL_COLUMN_EMAIL;
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
	public void setEmailTypeId(int id)
	{
		setColumn(getColumnNameEmailTypeId(), id);
	}
	public int getEmailTypeId()
	{
		return getIntColumnValue(getColumnNameEmailTypeId());
	}
	public Collection ejbFindEmailsForUser(com.idega.user.data.User user) throws FinderException, RemoteException
	{
		int userId = ((Integer) user.getPrimaryKey()).intValue();
		return ejbFindEmailsForUser(userId);
	}
	public Collection ejbFindEmailsForUser(int iUserId) throws FinderException
	{
		StringBuffer sql = new StringBuffer("select ie.* ");
		sql.append(" from ").append(getTableName()).append(" ie,").append(UserBMPBean.SQL_RELATION_EMAIL).append(" iue ");
		sql.append(" where ie.").append(getIDColumnName()).append(" =iue.").append(getIDColumnName());
		sql.append(" and iue.").append(UserBMPBean.SQL_TABLE_NAME).append("_ID = ");
		sql.append(iUserId);
		return super.idoFindIDsBySQL(sql.toString());
	}
	public Integer ejbFindEmailByAddress(String address) throws FinderException
	{
		IDOQuery query = idoQueryGetSelect().appendWhereEqualsQuoted(getColumnNameAddress(),address );
		Collection coll = super.idoFindPKsByQuery(query);
		if (!coll.isEmpty())
			return (Integer) coll.iterator().next();
		else
			throw new FinderException("No email found");
	}
}
