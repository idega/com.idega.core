package com.idega.core.accesscontrol.data;

/*
*Copyright 2000 idega.is All Rights Reserved.
*/

import java.sql.*;
import com.idega.data.*;
import com.idega.core.user.data.User;
import java.util.List;

public class LoginTable extends GenericEntity{

        public static String className = "com.idega.core.accesscontrol.data.LoginTable";

	public LoginTable(){
		super();
	}

	public LoginTable(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
          addAttribute(this.getIDColumnName());
          addAttribute(User.getUserIDColumnName(),"Notandi",true,true,"java.lang.Integer","many-to-one","com.idega.core.user.data.User");
          addAttribute(getUserLoginColumnName(),"Notandanafn",true,true,"java.lang.String");
          addAttribute(getUserPasswordColumnName(),"Lykilorð",true,true,"java.lang.String");
	}

	public String getEntityName(){
		return "ic_login";
	}

        public void insertStartData() throws SQLException {
          LoginTable login = new LoginTable();
          List user = EntityFinder.findAllByColumn(User.getStaticInstance(), User.getUserIDColumnName(),User.getAdminDefaultName());
          User adminUser = null;
          if(user != null){
            adminUser = ((User)user.get(0));
          }else{
            adminUser = new User();
            adminUser.setFirstName(User.getAdminDefaultName());
            adminUser.insert();
          }

          login.setUserId(adminUser.getID());
            login.setUserLogin(User.getAdminDefaultName());
            login.setUserPassword("");
            login.insert();

          LoginInfo info = new LoginInfo();
            info.setID(login.getID());
            info.setAccountEnabled(true);
            info.setAllowedToChange(true);
            info.setChangeNextTime(false);
            info.setPasswordExpires(false);
            info.insert();

        }

        public static LoginTable getStaticInstance(){
          return (LoginTable)LoginTable.getStaticInstance(className);
        }

        public static String getUserLoginColumnName(){
          return "user_login";
        }

        public static String getUserPasswordColumnName(){
          return "user_password";
        }

	public String getUserPassword(){
          return getStringColumnValue(getUserPasswordColumnName());
	}

	public void setUserPassword(String userPassword){
          setColumn(getUserPasswordColumnName(), userPassword);
	}
	public void setUserLogin(String userLogin) {
          setColumn(getUserLoginColumnName(), userLogin);
	}
	public String getUserLogin() {
          return getStringColumnValue(getUserLoginColumnName());
	}

	public int getUserId(){
          return getIntColumnValue(getUserIDColumnName());
	}

	public void setUserId(Integer userId){
          setColumn(getUserIDColumnName(), userId);
	}
	public void setUserId(int userId) {
          setColumn(getUserIDColumnName(),userId);
	}

        public static String getUserIDColumnName(){
          return User.getUserIDColumnName();
        }

}
