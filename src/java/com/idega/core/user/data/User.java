package com.idega.core.user.data;

import com.idega.data.*;
import com.idega.core.data.*;
import com.idega.core.accesscontrol.data.PermissionGroup;
import java.util.List;
import java.sql.*;


/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class User extends GenericEntity {

    private static String sClassName = User.class.getName();

    public User(){
      super();
    }

    public User(int id)throws SQLException{
      super(id);
    }


    public String getEntityName(){
            return "ic_user";
    }

    public void initializeAttributes(){
      addAttribute(getIDColumnName());

      addAttribute(getColumnNameFirstName(),"Fornafn",true,true,"java.lang.String");
      addAttribute(getColumnNameMiddleName(),"Miðnafn",true,true,"java.lang.String");
      addAttribute(getColumnNameLastName(),"Eftirnafn",true,true,"java.lang.String");
      addAttribute(getColumnNameDisplayName(),"Kenninafn",true,true,"java.lang.String");
      addAttribute(getColumnNameDescription(),"Lýsing",true,true,"java.lang.String");
      addAttribute(getColumnNameDateOfBirth(),"Fæðingardagur",true,true,"java.sql.Date");
      addAttribute(getColumnNameGender(),"Kyn",true,true,"java.lang.Integer","many_to_one","com.idega.core.user.data.Gender");
      addAttribute(getColumnNameSystemImage(),"Kerfismynd",true,true,"java.lang.Integer","one_to_one","com.idega.core.data.ICFile");
      addAttribute(_COLUMNNAME_USER_GROUP_ID,"Notandi",true,true,Integer.class,"one-to-one",GenericGroup.class);
      addAttribute(_COLUMNNAME_PRIMARY_GROUP_ID,"Aðal notendahópur",true,true,Integer.class,"one-to-one",GenericGroup.class);
      this.addManyToManyRelationShip(Address.class,"ic_user_address");
      this.addManyToManyRelationShip(Phone.class,"ic_user_phone");
      this.addManyToManyRelationShip(Email.class,"ic_user_email");
      this.setNullable(getColumnNameSystemImage(),true);
      this.setNullable(_COLUMNNAME_PRIMARY_GROUP_ID,true);
      //temp
      this.addManyToManyRelationShip(GenericGroup.class,"ic_group_user");
    }

    public void setDefaultValues(){
    }

    public void insertStartData() throws SQLException {

    }

    public String getIDColumnName(){
      return getColumnNameUserID();
    }

    public static User getStaticInstance(){
      return (User)User.getStaticInstance(sClassName);
    }

    public static String getAdminDefaultName(){
      return "Administrator";
    }




    /*  ColumNames begin   */

    public static String getColumnNameUserID(){return "ic_user_id";}
    public static String getColumnNameFirstName(){return "first_name";}
    public static String getColumnNameMiddleName(){return "middle_name";}
    public static String getColumnNameLastName(){return "last_name";}
    public static String getColumnNameDisplayName(){return "display_name";}
    public static String getColumnNameDescription(){return "description";}
    public static String getColumnNameDateOfBirth(){return "date_of_birth";}
    public static String getColumnNameGender(){return "ic_gender_id";}
    public static String getColumnNameSystemImage(){return "system_image_id";}
    public static final String _COLUMNNAME_USER_GROUP_ID = "user_representative";
    public static final String _COLUMNNAME_PRIMARY_GROUP_ID = "primary_group";
    /*  ColumNames end   */


    /*  Getters begin   */

    public String getFirstName() {
      return (String) getColumnValue(getColumnNameFirstName());
    }

    public String getMiddleName() {
      return (String) getColumnValue(getColumnNameMiddleName());
    }

    public String getLastName() {
      return (String) getColumnValue(getColumnNameLastName());
    }

    public String getDisplayName() {
      return (String) getColumnValue(getColumnNameDisplayName());
    }

    public String getDescription() {
      return (String) getColumnValue(getColumnNameDescription());
    }

    public Date getDateOfBirth(){
      return (Date) getColumnValue(getColumnNameDateOfBirth());
    }

    public int getGenderID(){
      return getIntColumnValue(getColumnNameGender());
    }

    public int getSystemImageID(){
      return getIntColumnValue(getColumnNameSystemImage());
    }

    public int getGroupID(){
      return getIntColumnValue(_COLUMNNAME_USER_GROUP_ID);
    }

    public int getPrimaryGroupID(){
      return getIntColumnValue(_COLUMNNAME_PRIMARY_GROUP_ID);
    }

    public String getName(){
	  String firstName=this.getFirstName();
	  String middleName=this.getMiddleName();
	  String lastName = this.getLastName();

	  if(firstName == null){
	    firstName="";
	  }

	  if(middleName == null){
	  	middleName="";
	  }else{
	    middleName = " "+middleName;
	  }

	  if(lastName == null){
	  	lastName="";
	  }else{
  		lastName = " " + lastName;
  	  }
      return firstName + middleName + lastName;
    }

    /*  Getters end   */


    /*  Setters begin   */

    public void setFirstName(String fName) {
      if(!com.idega.core.accesscontrol.business.AccessControl.isValidUsersFirstName(fName)){
        fName = "Invalid firstname";
      }
      if(com.idega.core.accesscontrol.business.AccessControl.isValidUsersFirstName(this.getFirstName())){ // if not Administrator
        setColumn(getColumnNameFirstName(),fName);
      }

    }

    public void setMiddleName(String mName) {
      setColumn(getColumnNameMiddleName(),mName);
    }

    public void setLastName(String lName) {
      setColumn(getColumnNameLastName(),lName);
    }

    public void setDisplayName(String dName) {
      setColumn(getColumnNameDisplayName(),dName);
    }

    public void setDescription(String description) {
      setColumn(getColumnNameDescription(),description);
    }

    public void setDateOfBirth(Date dateOfBirth){
      setColumn(getColumnNameDateOfBirth(),dateOfBirth);
    }

    public void setGender(Integer gender){
      setColumn(getColumnNameGender(),gender);
    }

    public void setGender(int gender){
      setColumn(getColumnNameGender(),gender);
    }

    public void setSystemImageID(Integer fileID){
      setColumn(getColumnNameSystemImage(),fileID);
    }

    public void setSystemImageID(int fileID){
      setColumn(getColumnNameSystemImage(),fileID);
    }

    public void setGroupID(int icGroupId){
      setColumn(_COLUMNNAME_USER_GROUP_ID,icGroupId);
    }

    public void setPrimaryGroupID(int icGroupId){
      setColumn(_COLUMNNAME_PRIMARY_GROUP_ID,icGroupId);
    }

    public void setPrimaryGroupID(Integer icGroupId){
      setColumn(_COLUMNNAME_PRIMARY_GROUP_ID,icGroupId);
    }


    /*  Setters end   */


}
