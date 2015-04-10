package com.idega.core.contact.data;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.core.user.data.User;
import com.idega.data.EntityControl;
import com.idega.data.IDOFinderException;
import com.idega.data.IDOQuery;
import com.idega.util.StringUtil;
/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */
public class PhoneBMPBean extends ContactBmpBean implements com.idega.core.contact.data.Phone
{
	private static final long serialVersionUID = -4420896834850155007L;
	private static String userRelationshipTableName=null;

	public PhoneBMPBean()
	{
		super();
	}
	public PhoneBMPBean(int id) throws SQLException
	{
		super(id);
	}

	@Override
	public void initializeAttributes()
	{
		addAttribute(getIDColumnName());
		addAttribute(getColumnNamePhoneNumber(), "Number", true, true, "java.lang.String");
		//      addAttribute(getColumnNameCountryCodeId(),"Landsn�mer",true,true,Integer.class,"many-to-one",CountryCode.class);
		addManyToOneRelationship(getColumnNameAreaCodeId(), "Area code", AreaCode.class);
		addManyToOneRelationship(getColumnNamePhoneTypeId(), "Type", PhoneType.class);
		//      this.addManyToManyRelationShip(PhoneType.class,"ic_phone_phone_type");
		this.addManyToManyRelationShip(User.class, "ic_user_phone");
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
		return "ic_phone";
	}
	public static String getColumnNamePhoneNumber()
	{
		return "phone_number";
	}
	//    public static String getColumnNameCountryCodeId(){return"ic_country_code_id";}
	public static String getColumnNameAreaCodeId()
	{
		return "ic_area_code_id";
	}
	public static String getColumnNamePhoneTypeId()
	{
		return "ic_phone_type_id";
	}
	@Override
	public void setDefaultValues()
	{
		//      setColumn(getColumnNameCountryCodeId(),-1);
		//      setColumn(getColumnNameAreaCodeId(),-1);
	}
	@Override
	public String getNumber()
	{
		return (String) getColumnValue(getColumnNamePhoneNumber());
	}
	@Override
	public void setNumber(String number)
	{
		setColumn(getColumnNamePhoneNumber(), number);
	}
	@Override
	public int getPhoneTypeId()
	{
		return getIntColumnValue(getColumnNamePhoneTypeId());
	}

	@Override
	public void setPhoneTypeId(int phone_type_id)
	{
		setColumn(getColumnNamePhoneTypeId(), phone_type_id);
	}

	@Override
	public PhoneType getPhoneType() {
		return (PhoneType)getColumnValue(getColumnNamePhoneTypeId());
	}

	public static int getHomeNumberID()
	{
		/*int returner = -1;
		try
		{
			PhoneType[] pt =
				(PhoneType[])
					(((com.idega.core.contact.data.PhoneTypeHome) com.idega.data.IDOLookup.getHomeLegacy(PhoneType.class)).createLegacy()).findAllByColumn(
					com.idega.core.data.GenericTypeBMPBean.getColumnNameUniqueName(),
					com.idega.core.contact.data.PhoneTypeBMPBean.UNIQUE_NAME_HOME_PHONE);
			if (pt.length > 0)
			{
				returner = pt[0].getID();
			}
		} catch (SQLException sql)
		{
			sql.printStackTrace(System.err);
		}
		return returner;*/
		return PhoneTypeBMPBean.HOME_PHONE_ID;
	}
	public static int getWorkNumberID()
	{
		/*int returner = -1;
		try
		{
			PhoneType[] pt =
				(PhoneType[])
					(((com.idega.core.contact.data.PhoneTypeHome) com.idega.data.IDOLookup.getHomeLegacy(PhoneType.class)).createLegacy()).findAllByColumn(
					com.idega.core.data.GenericTypeBMPBean.getColumnNameUniqueName(),
					com.idega.core.contact.data.PhoneTypeBMPBean.UNIQUE_NAME_WORK_PHONE);
			if (pt.length > 0)
			{
				returner = pt[0].getID();
			}
		} catch (SQLException sql)
		{
			sql.printStackTrace(System.err);
		}
		return returner;*/
		return PhoneTypeBMPBean.WORK_PHONE_ID;
	}
	public static int getFaxNumberID()
	{
		/*int returner = -1;
		try
		{
			PhoneType[] pt =
				(PhoneType[])
					(((com.idega.core.contact.data.PhoneTypeHome) com.idega.data.IDOLookup.getHomeLegacy(PhoneType.class)).createLegacy()).findAllByColumn(
					com.idega.core.data.GenericTypeBMPBean.getColumnNameUniqueName(),
					com.idega.core.contact.data.PhoneTypeBMPBean.UNIQUE_NAME_FAX_NUMBER);
			if (pt.length > 0)
			{
				returner = pt[0].getID();
			}
		} catch (SQLException sql)
		{
			sql.printStackTrace(System.err);
		}
		return returner;*/
		return PhoneTypeBMPBean.FAX_NUMBER_ID;
	}
	public static int getMobileNumberID()
	{
		/*int returner = -1;
		try
		{
			PhoneType[] pt =
				(PhoneType[])
					(((com.idega.core.contact.data.PhoneTypeHome) com.idega.data.IDOLookup.getHomeLegacy(PhoneType.class)).createLegacy()).findAllByColumn(
					com.idega.core.data.GenericTypeBMPBean.getColumnNameUniqueName(),
					com.idega.core.contact.data.PhoneTypeBMPBean.UNIQUE_NAME_MOBILE_PHONE);
			if (pt.length > 0)
			{
				returner = pt[0].getID();
			}
		} catch (SQLException sql)
		{
			sql.printStackTrace(System.err);
		}
		return returner;*/
		return PhoneTypeBMPBean.MOBILE_PHONE_ID;
	}
	public Integer ejbFindUsersHomePhone(com.idega.user.data.User user) throws FinderException
	{
		try{
			int userID = ((Integer)user.getPrimaryKey()).intValue();
			String sql = getSelectWithPhoneType(userID,getHomeNumberID());
			return (Integer) this.idoFindOnePKBySQL(sql);
		}
		catch(Exception e){
			throw new IDOFinderException(e);
		}
	}
	public Integer ejbFindUsersWorkPhone(com.idega.user.data.User user) throws FinderException
	{
		try{
			int userID = ((Integer)user.getPrimaryKey()).intValue();
			String sql = getSelectWithPhoneType(userID,getWorkNumberID());
			return (Integer) this.idoFindOnePKBySQL(sql);
		}
		catch(Exception e){
			throw new IDOFinderException(e);
		}
	}
	public Integer ejbFindUsersMobilePhone(com.idega.user.data.User user) throws FinderException
	{
		try{
			int userID = ((Integer)user.getPrimaryKey()).intValue();
			String sql = getSelectWithPhoneType(userID,getMobileNumberID());
			return (Integer) this.idoFindOnePKBySQL(sql);
		}
		catch(Exception e){
			throw new IDOFinderException(e);
		}
	}
	public Integer ejbFindUsersFaxPhone(com.idega.user.data.User user) throws FinderException
	{
		try{
			int userID = ((Integer)user.getPrimaryKey()).intValue();
			String sql = getSelectWithPhoneType(userID,getFaxNumberID());
			return (Integer) this.idoFindOnePKBySQL(sql);
		}
		catch(Exception e){
			throw new IDOFinderException(e);
		}
	}



	protected String getSelectWithPhoneType(int userID,int phoneTypeID){

			StringBuffer buf = new StringBuffer();
			buf.append("select p.* from ");
			buf.append(this.getTableName());
			buf.append(" p,");
			buf.append(getUserRelationshipTableName());
			buf.append(" rel where rel.");
			buf.append(getIDColumnName());
			buf.append("=p.");
			buf.append(getIDColumnName());
			buf.append(" and rel.ic_user_id=");
			buf.append(userID);
			buf.append(" and p.");
			buf.append(getColumnNamePhoneTypeId());
			buf.append("=");
			buf.append(phoneTypeID);

			//System.out.println(buf.toString());
			return buf.toString();
	}

	/**
	 * Method getUserRelationshipTableName.
	 * @return Object
	 */
	private String getUserRelationshipTableName()
	{
		if(userRelationshipTableName==null){
			userRelationshipTableName=EntityControl.getManyToManyRelationShipTableName(this.getClass(), User.class);
		}
		return userRelationshipTableName;
	}

	/**
	 *
	 * <p>Finds primary keys by criteria.</p>
	 * @param phoneNumber is {@link Phone#getNumber()}, not <code>null</code>;
	 * @return {@link Phone}s by number or {@link Collections#emptyList()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Collection<Object> ejbFindByPhoneNumber(String phoneNumber) {
		if (StringUtil.isEmpty(phoneNumber)) {
			return Collections.emptyList();
		}

		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(this);
		query.appendWhereEquals(getColumnNamePhoneNumber(), phoneNumber);

		try {
			return idoFindPKsByQuery(query);
		} catch (FinderException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING,
					"Failed to find primary keys for " + this.getClass().getName() +
					" by query: '" + query.toString() + "'");
		}

		return Collections.emptyList();
	}

	public Collection ejbFindUsersPhones(int userId,int type) throws FinderException {
		try{
			String sql = getSelectWithPhoneType(userId,type);
			return idoFindPKsBySQL(sql);
		}
		catch(Exception e){
			throw new IDOFinderException(e);
		}
	}
}
