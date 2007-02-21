package com.idega.core.accesscontrol.data;



/*

*Copyright 2000 idega.is All Rights Reserved.

*/



import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.user.data.UserBMPBean;



public class LoginTableBMPBean extends com.idega.data.GenericEntity implements com.idega.core.accesscontrol.data.LoginTable,com.idega.util.EncryptionType {



        public static String className = LoginTable.class.getName();
        public static String _COLUMN_PASSWORD = "USR_PASSWORD";
	    private static final String ENTITY_NAME = "IC_LOGIN";
	    private static final String COLUMN_USER_LOGIN = "USER_LOGIN";
	    private static final String COLUMN_USER_PASSWORD = "USER_PASSWORD";
    	private static final String COLUMN_LAST_CHANGED = "LAST_CHANGED";
	    private static final String COLUMN_LOGIN_TYPE = "LOGIN_TYPE";
	    private static final String COLUMN_CHANGED_BY_USER = "CHANGED_BY_USER_ID";
		private static final String COLUMN_CHANGED_BY_GROUP = "CHANGED_BY_GROUP_ID";
	

        private transient String unEncryptedUserPassword;

	public LoginTableBMPBean(){
		super();
	}


	public LoginTableBMPBean(int id)throws SQLException{
		super(id);
	}



	public void initializeAttributes(){
  		addAttribute(this.getIDColumnName());
		addAttribute(getColumnNameUserID(), "User id", true, true, Integer.class, "many-to-one", User.class);
		addAttribute(getUserLoginColumnName(), "User name", true, true, String.class, 32);
		addAttribute(getNewUserPasswordColumnName(), "Password (encrypted)", true, true, String.class, 255);
		// deprecated column
		addAttribute(getOldUserPasswordColumnName(), "Password (deprecated)", true, true, String.class, 20);
		addAttribute(getLastChangedColumnName(), "Last changed", true, true, Timestamp.class);
		addAttribute(getLoginTypeColumnName(), "Login type", true, true, String.class, 32);
		addAttribute(COLUMN_CHANGED_BY_USER, "Last changed by user id", true, true, Integer.class, "many-to-one", User.class);
		addAttribute(COLUMN_CHANGED_BY_GROUP, "Last changed by group id", true, true, Integer.class, "many-to-one", Group.class);
		
		setNullable(getUserLoginColumnName(), false);
		setUnique(getUserLoginColumnName(), true);
		
		addIndex("IDX_LOGIN_REC_2", getUserIDColumnName());
		addIndex("IDX_LOGIN_REC_3", new String[]{getUserIDColumnName(), getLoginTypeColumnName()});
		addIndex("IDX_LOGIN_REC_4", new String[]{getUserIDColumnName(), getUserLoginColumnName()});
		addIndex("IDX_LOGIN_REC_5", getUserLoginColumnName());
	}



        public void setDefaultValues(){
          setColumn(getOldUserPasswordColumnName(),"rugl");
        }



	public String getEntityName(){
		return ENTITY_NAME;
	}



        public static String getUserLoginColumnName(){

          return COLUMN_USER_LOGIN;

        }



        public static String getOldUserPasswordColumnName(){

          return COLUMN_USER_PASSWORD;

        }



        public static String getNewUserPasswordColumnName(){

          return _COLUMN_PASSWORD;

        }



        public static String getLastChangedColumnName() {

          return COLUMN_LAST_CHANGED;

        }
        
			public static String getLoginTypeColumnName() {
	
			  return COLUMN_LOGIN_TYPE;
	
			}



        public static String getUserPasswordColumnName(){

          System.out.println("LoginTable - getUserPassordColumnName()");

          System.out.println("caution: not save because of changes in entity");

          Exception e = new Exception();

          e.printStackTrace();

          return _COLUMN_PASSWORD;

        }



        public static String getColumnNameUserID(){

          return UserBMPBean.getColumnNameUserID();

        }



/*        public void insertStartData() throws SQLException {

          LoginTable login = ((com.idega.core.accesscontrol.data.LoginTableHome)com.idega.data.IDOLookup.getHomeLegacy(LoginTable.class)).createLegacy();

          LoginInfo li = ((com.idega.core.accesscontrol.data.LoginInfoHome)com.idega.data.IDOLookup.getHomeLegacy(LoginInfo.class)).createLegacy();

          List user = EntityFinder.findAllByColumn(com.idega.core.user.data.UserBMPBean.getStaticInstance(), com.idega.core.user.data.UserBMPBean.getColumnNameFirstName(),com.idega.core.user.data.UserBMPBean.getAdminDefaultName());

          User adminUser = null;

          if(user != null){

            adminUser = ((User)user.get(0));

          }else{

            adminUser = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).createLegacy();

            adminUser.setFirstName(com.idega.core.user.data.UserBMPBean.getAdminDefaultName());

            adminUser.insert();

          }



          try {

          /*

            login.setUserId(adminUser.getID());

            login.setUserLogin(com.idega.core.user.data.UserBMPBean.getAdminDefaultName());

            login.setUserPassword("idega");

            login.insert();

            li.setID(login.getID());

            li.insert();

            */

/*            LoginDBHandler.createLogin(adminUser.getID(), com.idega.core.user.data.UserBMPBean.getAdminDefaultName(), "idega", Boolean.TRUE, null, -1, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, EncryptionType.MD5);

          }

          catch (Exception ex) {

            System.err.println(ex.getMessage());

            ex.printStackTrace();

            throw new SQLException("Login Not created");

          }



          AccessControl control = new AccessControl();

          GenericGroup group = com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getStaticPermissionGroupInstance().findGroup(AccessControl.getAdministratorGroupName());

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

          return (LoginTable)GenericEntity.getStaticInstance(LoginTable.class);

        }


/**
 * just sets the password column value as this string without encoding.
 */
public void setUserPasswordInClearText(String password){
	setColumn(getNewUserPasswordColumnName(), password);
}

/**
 * just returns the password column value as is.
 */
    public String getUserPasswordInClearText(){
    		return getStringColumnValue(getNewUserPasswordColumnName());
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



                LoginTable table = ((com.idega.core.accesscontrol.data.LoginTableHome)com.idega.data.IDOLookup.getHomeLegacy(LoginTable.class)).findByPrimaryKeyLegacy(this.getID());

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
              String hex = Integer.toHexString(pass[i]);
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
	
	public User getUser(){
		return (User)getColumnValue(getUserIDColumnName());
	}

	public void setUserId(Integer userId){
          setColumn(getUserIDColumnName(), userId);
	}

	public void setUserId(int userId) {
          setColumn(getUserIDColumnName(),userId);
	}

        public static String getUserIDColumnName(){
          return UserBMPBean.getColumnNameUserID();
        }

        public void setLastChanged(Timestamp when) {
          setColumn(getLastChangedColumnName(),when);
        }

        public Timestamp getLastChanged() {
          return((Timestamp)getColumnValue(getLastChangedColumnName()));
        }
        
        public void setChangedByGroup(Group group){
    		setColumn(COLUMN_CHANGED_BY_GROUP, group);
    	}
    	
    	public void setChangedByGroupId(int changedByGroupId){
    		setColumn(COLUMN_CHANGED_BY_GROUP, changedByGroupId);
    	}
    	
    	public int getChangedByGroupId(){
    		return getIntColumnValue(COLUMN_CHANGED_BY_GROUP);
    	}
    	
    	public Group getChangedByGroup(){
    		return (Group) getColumnValue(COLUMN_CHANGED_BY_GROUP);
    	}
    	
    	public void setChangedByUser(User changedByUser){
    		setColumn(COLUMN_CHANGED_BY_USER, changedByUser);
    	}
    	
    	public void setChangedByUserId(int changedByUserId){
    		setColumn(COLUMN_CHANGED_BY_USER, changedByUserId);
    	}
    	
    	public int getChangedByUserId(){
    		return getIntColumnValue(COLUMN_CHANGED_BY_USER);
    	}
    	
    	public User getChangedByUser(){
    		return (User) getColumnValue(COLUMN_CHANGED_BY_USER);
    	}
    	

        /**
         * Sets both the intented encrypted password and the original unencrypted password for temporary retrieval
         */
        public void setUserPassword(String encryptedPassword,String unEncryptedPassword){
          this.unEncryptedUserPassword=unEncryptedPassword;
          this.setUserPassword(encryptedPassword);
        }

        /**
         * Gets the original password if the record is newly created, therefore it can be retrieved , if this is not a newly created record the exception PasswordNotKnown is thrown
         */
        public String getUnencryptedUserPassword()throws PasswordNotKnown{
          if(this.unEncryptedUserPassword==null){
            throw new PasswordNotKnown(this.getUserLogin());
          }
          else{
            return this.unEncryptedUserPassword;
          }
        }
        
        
		public void setLoginType(String loginType) {
			  setColumn(getLoginTypeColumnName(), loginType);
		}
	
		public String getLoginType() {
			  return getStringColumnValue(getLoginTypeColumnName());
		}
		
		
		public Collection ejbFindLoginsForUser(com.idega.core.user.data.User user) throws FinderException{
			
			IDOQuery query = idoQuery();
			query.appendSelectAllFrom(this);
			query.appendWhereEquals(getColumnNameUserID(),user.getPrimaryKey());

			return idoFindPKsByQuery(query);	
		}
			
		
		
}

