package com.idega.core.accesscontrol.data;

import com.idega.data.IDOLegacyEntity;
import java.sql.Timestamp;
import java.sql.SQLException;

/**
 * Title:   idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author  <a href="mailto:aron@idega.is">aron@idega.is
 * @version 1.0
 */

public class LoginRecordBMPBean extends com.idega.data.GenericEntity implements com.idega.core.accesscontrol.data.LoginRecord {

    public static String getEntityTableName(){return "IC_LOGIN_REC";}
    public static String getColumnLoginId(){return "IC_LOGIN_ID";}
    public static String getColumnInStamp(){return "IN_STAMP";}
    public static String getColumnOutStamp(){return "OUT_STAMP";}
    public static String getColumnIPAddress(){return "IP";}

    public LoginRecordBMPBean(){
      super();
    }

    public LoginRecordBMPBean(int id)throws SQLException{
      super(id);
    }

    public void initializeAttributes(){
      addAttribute(this.getIDColumnName());
      addAttribute(getColumnLoginId(),"Login id",true,true,Integer.class,"many-to-one",LoginTable.class);
      addAttribute(getColumnInStamp(),"Login Stamp",true,true,Timestamp.class);
      addAttribute(getColumnOutStamp(),"Logout Stamp",true,true,Timestamp.class);
      addAttribute(getColumnIPAddress(),"IP address",true,true,String.class,16);
    }

    public String getEntityName(){
      return getEntityTableName();
    }
    public void setLoginId(int Id) {
      setColumn(getColumnLoginId(),Id);
    }
    public int getLoginId(){
      return getIntColumnValue(getColumnLoginId());
    }
    public Timestamp getLogInStamp(){
      return (Timestamp) getColumnValue(getColumnInStamp());
    }
    public void setLogInStamp(Timestamp stamp){
      setColumn(getColumnInStamp(),stamp);
    }
    public Timestamp getLogOutStamp(){
      return (Timestamp) getColumnValue(getColumnOutStamp());
    }
    public void setLogOutStamp(Timestamp stamp){
      setColumn(getColumnOutStamp(),stamp);
    }
    public String getIPAdress(){
      return getStringColumnValue(getColumnIPAddress());
    }
    public void setIPAdress(String ip){
      setColumn(getColumnIPAddress(),ip);
    }
}
