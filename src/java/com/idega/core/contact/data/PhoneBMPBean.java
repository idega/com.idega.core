package com.idega.core.contact.data;
import java.sql.SQLException;

import javax.ejb.FinderException;

import com.idega.core.user.data.User;
import com.idega.data.EntityControl;
import com.idega.data.IDOFinderException;
/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */
public class PhoneBMPBean extends com.idega.data.GenericEntity implements com.idega.core.contact.data.Phone
{
	private static String userRelationshipTableName=null;
	
	public PhoneBMPBean()
	{
		super();
	}
	public PhoneBMPBean(int id) throws SQLException
	{
		super(id);
	}
	public void initializeAttributes()
	{
		addAttribute(getIDColumnName());
		addAttribute(getColumnNamePhoneNumber(), "Number", true, true, "java.lang.String");
		//      addAttribute(getColumnNameCountryCodeId(),"Landsnúmer",true,true,Integer.class,"many-to-one",CountryCode.class);
		addManyToOneRelationship(getColumnNameAreaCodeId(), "Area code", AreaCode.class);
		addManyToOneRelationship(getColumnNamePhoneTypeId(), "Type", PhoneType.class);
		//      this.addManyToManyRelationShip(PhoneType.class,"ic_phone_phone_type");
		this.addManyToManyRelationShip(User.class, "ic_user_phone");
	}
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
	public void setDefaultValues()
	{
		//      setColumn(getColumnNameCountryCodeId(),-1);
		//      setColumn(getColumnNameAreaCodeId(),-1);
	}
	public String getNumber()
	{
		return (String) getColumnValue(getColumnNamePhoneNumber());
	}
	public void setNumber(String number)
	{
		setColumn(getColumnNamePhoneNumber(), number);
	}
	public int getPhoneTypeId()
	{
		return getIntColumnValue(getColumnNamePhoneTypeId());
	}
	public void setPhoneTypeId(int phone_type_id)
	{
		setColumn(getColumnNamePhoneTypeId(), phone_type_id);
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
			buf.append(getHomeNumberID());
				
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


}
