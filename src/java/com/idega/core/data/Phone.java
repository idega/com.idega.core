package com.idega.core.data;

import java.sql.*;
import com.idega.data.*;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */


public class Phone extends GenericEntity{

    public Phone(){
      super();
    }

    public Phone(int id)throws SQLException{
      super(id);
    }

    public void initializeAttributes(){
      addAttribute(getIDColumnName());
      addAttribute(getColumnNamePhoneNumber(),"Númer",true,true,"java.lang.String");
//      addAttribute(getColumnNameCountryCodeId(),"Landsnúmer",true,true,Integer.class,"many-to-one",CountryCode.class);
      addAttribute(getColumnNameAreaCodeId(),"Svæðisnúmer",true,true,Integer.class,"many-to-one",AreaCode.class);
      addAttribute(getColumnNamePhoneTypeId(),"Tegund",true,true,Integer.class,"many-to-one",PhoneType.class);
//      this.addManyToManyRelationShip(PhoneType.class,"ic_phone_phone_type");
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
          PhoneType[] pt = (PhoneType[]) (new PhoneType()).findAllByColumn(GenericType.getColumnNameUniqueName(),PhoneType.UNIQUE_NAME_HOME_PHONE);
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
          PhoneType[] pt = (PhoneType[]) (new PhoneType()).findAllByColumn(GenericType.getColumnNameUniqueName(),PhoneType.UNIQUE_NAME_WORK_PHONE);
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
          PhoneType[] pt = (PhoneType[]) (new PhoneType()).findAllByColumn(GenericType.getColumnNameUniqueName(),PhoneType.UNIQUE_NAME_FAX_NUMBER);
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
          PhoneType[] pt = (PhoneType[]) (new PhoneType()).findAllByColumn(GenericType.getColumnNameUniqueName(),PhoneType.UNIQUE_NAME_MOBILE_PHONE);
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
