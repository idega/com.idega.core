package com.idega.core.accesscontrol.data;

/*
*Copyright 2000 idega.is All Rights Reserved.
*/

import java.sql.*;
import com.idega.data.*;
import com.idega.core.user.data.User;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.data.PermissionGroup;
import com.idega.core.data.GenericGroup;
import java.util.List;
import com.idega.util.EncryptionType;

public class LoginTable extends GenericEntity implements EncryptionType{

        public static String className = LoginTable.class.getName();

	public LoginTable(){
		super();
	}

	public LoginTable(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
          addAttribute(this.getIDColumnName());
          addAttribute(User.getColumnNameUserID(),"Notandi",true,true,Integer.class,"many-to-one",User.class);
          addAttribute(getUserLoginColumnName(),"Notandanafn",true,true,String.class,32);
          addAttribute(getUserPasswordColumnName(),"Lykilorð",true,true,String.class,255);
	}

	public String getEntityName(){
		return "ic_login";
	}

/*        public void insertStartData() throws SQLException {
          LoginTable login = new LoginTable();
          LoginInfo li = new LoginInfo();
          List user = EntityFinder.findAllByColumn(User.getStaticInstance(), User.getColumnNameFirstName(),User.getAdminDefaultName());
          User adminUser = null;
          if(user != null){
            adminUser = ((User)user.get(0));
          }else{
            adminUser = new User();
            adminUser.setFirstName(User.getAdminDefaultName());
            adminUser.insert();
          }

          try {
          /*
            login.setUserId(adminUser.getID());
            login.setUserLogin(User.getAdminDefaultName());
            login.setUserPassword("idega");
            login.insert();
            li.setID(login.getID());
            li.insert();
            */
/*            LoginDBHandler.createLogin(adminUser.getID(), User.getAdminDefaultName(), "idega", Boolean.TRUE, null, -1, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, EncryptionType.MD5);
          }
          catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            throw new SQLException("Login Not created");
          }

          AccessControl control = new AccessControl();
          GenericGroup group = PermissionGroup.getStaticPermissionGroupInstance().findGroup(AccessControl.getAdministratorGroupName());
          if(group != null){
            control.addUserToPermissionGroup((PermissionGroup)group,adminUser.getID());
          }else{
            int[] userId = new int[1];
            userId[0] = adminUser.getID();
            control.createPermissionGroup(AccessControl.getAdministratorGroupName(),null,null,userId,null);
          }
        }
*/
        public static LoginTable getStaticInstance(){
          return (LoginTable)LoginTable.getStaticInstance(LoginTable.class);
        }

        public static String getUserLoginColumnName(){
          return "user_login";
        }

        public static String getUserPasswordColumnName(){
          return "user_password";
        }

	public String getUserPassword(){
          String str = getStringColumnValue(getUserPasswordColumnName());
          char[] pass = new char[str.length()/2];

          try {

            for (int i = 0; i < pass.length; i++) {
              pass[i] = (char)Integer.decode("0x"+str.charAt(i*2)+str.charAt((i*2)+1)).intValue();
            }

            return String.valueOf(pass);
          }
          catch (Exception ex) {
            ex.printStackTrace();
            return str;
          }

	}


	public void setUserPassword(String userPassword){
          try {
            String str = "";
            char[] pass = userPassword.toCharArray();
            for (int i = 0; i < pass.length; i++) {
              String hex = Integer.toHexString((int)pass[i]);
              while (hex.length() < 2) {
                String s = "0";
                s += hex;
                hex = s;
              }
              str += hex;
            }

            if(str.equals("") && !userPassword.equals("")){
              str = null;
            }

            setColumn(getUserPasswordColumnName(), str);
          }
          catch (Exception ex) {
            ex.printStackTrace();
            setColumn(getUserPasswordColumnName(), userPassword);
          }


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
          return User.getColumnNameUserID();
        }

}
