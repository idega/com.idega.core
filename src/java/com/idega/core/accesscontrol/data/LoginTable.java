package com.idega.core.accesscontrol.data;

//idega 2000 - Gimmi
/*
*Copyright 2000 idega.is All Rights Reserved.
*/



//import java.util.*;
import java.sql.*;
import com.idega.data.*;
import com.idega.core.user.data.User;

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
          addAttribute("user_password","Lykilorð",true,true,"java.lang.String");
	}

	public String getEntityName(){
		return "ic_login";
	}

        public static LoginTable getStaticInstance(){
          return (LoginTable)LoginTable.getStaticInstance(className);
        }

        public static String getUserLoginColumnName(){
          return "user_login";
        }



	public String getUserPassword(){
		return (String) getColumnValue("user_password");
	}

	public void setUserPassword(String userPassword){
		setColumn("user_password", userPassword);
	}
	public void setUserLogin(String userLogin) {
		setColumn(getUserLoginColumnName(), userLogin);
	}
	public String getUserLogin() {
		return (String) getColumnValue(getUserLoginColumnName());
	}


	public int getUserId(){
		return getIntColumnValue(getUserIDColumnName());
	}

	public void setUserId(Integer memberId){
		setColumn(getUserIDColumnName(), memberId);
	}
	public void setUserId(int memberId) {
		setUserId((new Integer(memberId)));
	}

        public static String getUserIDColumnName(){
          return User.getUserIDColumnName();
        }

}
