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
        public static String _COLUMN_PASSWORD = "usr_password";

	public LoginTable(){
		super();
	}

	public LoginTable(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
          addAttribute(this.getIDColumnName());
          addAttribute(getColumnNameUserID(),"Notandi",true,true,Integer.class,"many-to-one",User.class);
          addAttribute(getUserLoginColumnName(),"Notandanafn",true,true,String.class,32);
          addAttribute(getNewUserPasswordColumnName(),"Lykilorð",true,true,String.class,255);
          //deprecated column
          addAttribute(getOldUserPasswordColumnName(),"Lykilorð",true,true,String.class,20);
          addAttribute(getLastChangedColumnName(),"Síðast breytt",true,true,Timestamp.class);
          setUnique(getUserLoginColumnName(),true);

	}

        public void setDefaultValues(){
          setColumn(getOldUserPasswordColumnName(),"rugl");
        }

	public String getEntityName(){
		return "ic_login";
	}

        public static String getUserLoginColumnName(){
          return "user_login";
        }

        public static String getOldUserPasswordColumnName(){
          return "user_password";
        }

        public static String getNewUserPasswordColumnName(){
          return _COLUMN_PASSWORD;
        }

        public static String getLastChangedColumnName() {
          return("last_changed");
        }

        public static String getUserPasswordColumnName(){
          System.out.println("LoginTable - getUserPassordColumnName()");
          System.out.println("caution: not save because of changes in entity");
          Exception e = new Exception();
          e.printStackTrace();
          return _COLUMN_PASSWORD;
        }

        public static String getColumnNameUserID(){
          return User.getColumnNameUserID();
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



	public String getUserPassword(){
          String str = null;
          try {
            str = getStringColumnValue(getNewUserPasswordColumnName());
          }
          catch (Exception ex) {
            ex.printStackTrace();
            // str = null;
          }


          if(str == null){
            try {
              String oldPass = getStringColumnValue(getOldUserPasswordColumnName());
              if(oldPass != null){

                char[] pass = new char[oldPass.length()/2];

                try {

                  for (int i = 0; i < pass.length; i++) {
                    pass[i] = (char)Integer.decode("0x"+oldPass.charAt(i*2)+oldPass.charAt((i*2)+1)).intValue();
                  }

                  oldPass = String.valueOf(pass);
                }
                catch (Exception ex) {
                  ex.printStackTrace();
                  // oldPass = oldPass;
                }

                LoginTable table = new LoginTable(this.getID());
                table.setUserPassword(oldPass);
                table.update();
                this.setUserPassword(oldPass);
                return oldPass;
                //this.setColumnAsNull(getOldUserPasswordColumnName());
              }
            }
            catch (Exception ex) {
              ex.printStackTrace();
              return getStringColumnValue(getOldUserPasswordColumnName());
            }
          }
          if(str != null){
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

          return str;

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

            setColumn(getNewUserPasswordColumnName(), str);
          }
          catch (Exception ex) {
            ex.printStackTrace();
            setColumn(getNewUserPasswordColumnName(), userPassword);
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

  public void setLastChanged(Timestamp when) {
    setColumn(getLastChangedColumnName(),when);
  }

  public Timestamp getLastChanged() {
    return((Timestamp)getColumnValue(getLastChangedColumnName()));
  }
}
