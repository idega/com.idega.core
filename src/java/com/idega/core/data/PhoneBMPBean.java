package com.idega.core.data;

import java.sql.*;
import com.idega.data.*;
import com.idega.core.user.data.User;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */


public class PhoneBMPBean extends com.idega.data.GenericEntity implements com.idega.core.data.Phone {

    public PhoneBMPBean(){
      super();
    }

    public PhoneBMPBean(int id)throws SQLException{
      super(id);
    }

    public void initializeAttributes(){
      addAttribute(getIDColumnName());
      addAttribute(getColumnNamePhoneNumber(),"Number",true,true,"java.lang.String");
//      addAttribute(getColumnNameCountryCodeId(),"Landsnúmer",true,true,Integer.class,"many-to-one",CountryCode.class);
      addManyToOneRelationship(getColumnNameAreaCodeId(),"Area code",AreaCode.class);
      addManyToOneRelationship(getColumnNamePhoneTypeId(),"Type",PhoneType.class);
//      this.addManyToManyRelationShip(PhoneType.class,"ic_phone_phone_type");
      this.addManyToManyRelationShip(User.class,"ic_user_phone");
    }

    public String getEntityName(){
      return "ic_phone";
    }

    public static String getColumnNamePhoneNumber(){return"phone_number";}
//    public static String getColumnNameCountryCodeId(){return"ic_country_code_id";}
    public static String getColumnNameAreaCodeId(){return"ic_area_code_id";}
    public static String getColumnNamePhoneTypeId(){return"ic_phone_type_id";}

    public void setDefaultValues() {
//      setColumn(getColumnNameCountryCodeId(),-1);
//      setColumn(getColumnNameAreaCodeId(),-1);
    }

    public String getNumber(){
      return (String)getColumnValue(getColumnNamePhoneNumber());
    }

    public void setNumber(String number){
      setColumn(getColumnNamePhoneNumber(), number);
    }


    public int getPhoneTypeId(){
      return getIntColumnValue(getColumnNamePhoneTypeId());
    }

    public void setPhoneTypeId(int phone_type_id){
      setColumn(getColumnNamePhoneTypeId(), phone_type_id);
    }

    public static int getHomeNumberID() {
      int returner = -1;
      try {
          PhoneType[] pt = (PhoneType[]) (((com.idega.core.data.PhoneTypeHome)com.idega.data.IDOLookup.getHomeLegacy(PhoneType.class)).createLegacy()).findAllByColumn(com.idega.core.data.GenericTypeBMPBean.getColumnNameUniqueName(),com.idega.core.data.PhoneTypeBMPBean.UNIQUE_NAME_HOME_PHONE);
          if (pt.length > 0) {
            returner = pt[0].getID();
          }
      }
      catch (SQLException sql) {
          sql.printStackTrace(System.err);
      }
      return returner;
    }

    public static int getWorkNumberID() {
      int returner = -1;
      try {
          PhoneType[] pt = (PhoneType[]) (((com.idega.core.data.PhoneTypeHome)com.idega.data.IDOLookup.getHomeLegacy(PhoneType.class)).createLegacy()).findAllByColumn(com.idega.core.data.GenericTypeBMPBean.getColumnNameUniqueName(),com.idega.core.data.PhoneTypeBMPBean.UNIQUE_NAME_WORK_PHONE);
          if (pt.length > 0) {
            returner = pt[0].getID();
          }
      }
      catch (SQLException sql) {
          sql.printStackTrace(System.err);
      }
      return returner;
    }

    public static int getFaxNumberID() {
      int returner = -1;
      try {
          PhoneType[] pt = (PhoneType[]) (((com.idega.core.data.PhoneTypeHome)com.idega.data.IDOLookup.getHomeLegacy(PhoneType.class)).createLegacy()).findAllByColumn(com.idega.core.data.GenericTypeBMPBean.getColumnNameUniqueName(),com.idega.core.data.PhoneTypeBMPBean.UNIQUE_NAME_FAX_NUMBER);
          if (pt.length > 0) {
            returner = pt[0].getID();
          }
      }
      catch (SQLException sql) {
          sql.printStackTrace(System.err);
      }
      return returner;
    }

    public static int getMobileNumberID() {
      int returner = -1;
      try {
          PhoneType[] pt = (PhoneType[]) (((com.idega.core.data.PhoneTypeHome)com.idega.data.IDOLookup.getHomeLegacy(PhoneType.class)).createLegacy()).findAllByColumn(com.idega.core.data.GenericTypeBMPBean.getColumnNameUniqueName(),com.idega.core.data.PhoneTypeBMPBean.UNIQUE_NAME_MOBILE_PHONE);
          if (pt.length > 0) {
            returner = pt[0].getID();
          }
      }
      catch (SQLException sql) {
          sql.printStackTrace(System.err);
      }
      return returner;
    }

}
