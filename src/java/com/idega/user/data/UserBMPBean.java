package com.idega.user.data;

import com.idega.data.*;
import com.idega.core.data.*;
import com.idega.core.accesscontrol.data.PermissionGroup;
import java.util.List;
import java.sql.*;

import java.util.Collection;
import javax.ejb.*;
import java.rmi.RemoteException;

/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserBMPBean extends com.idega.data.GenericEntity implements User {

    private static String sClassName = User.class.getName();

    public UserBMPBean(){
      super();
    }

    public UserBMPBean(int id)throws SQLException{
      super(id);
    }


    public String getEntityName(){
            return "ic_user";
    }

    public void initializeAttributes(){
      addAttribute(getIDColumnName());

      addAttribute(getColumnNameFirstName(),"First name",true,true,java.lang.String.class);
      addAttribute(getColumnNameMiddleName(),"Middle name",true,true,java.lang.String.class);
      addAttribute(getColumnNameLastName(),"Last name",true,true,java.lang.String.class);
      addAttribute(getColumnNameDisplayName(),"Display name",true,true,java.lang.String.class);
      addAttribute(getColumnNameDescription(),"Description",true,true,java.lang.String.class);
      addAttribute(getColumnNameDateOfBirth(),"Birth date",true,true,java.sql.Date.class);
      addManyToOneRelationship(getColumnNameGender(),"Gender",com.idega.user.data.Gender.class);
      addOneToOneRelationship(getColumnNameSystemImage(),"Image",com.idega.core.data.ICFile.class);
      addOneToOneRelationship(_COLUMNNAME_USER_GROUP_ID,"User",Group.class);
      addOneToOneRelationship(_COLUMNNAME_PRIMARY_GROUP_ID,"Primary group",Group.class);
      this.addManyToManyRelationShip(Address.class,"ic_user_address");
      this.addManyToManyRelationShip(Phone.class,"ic_user_phone");
      this.addManyToManyRelationShip(Email.class,"ic_user_email");
      this.setNullable(getColumnNameSystemImage(),true);
      this.setNullable(_COLUMNNAME_PRIMARY_GROUP_ID,true);
      //temp
      this.addManyToManyRelationShip(Group.class,"ic_group_user");
    }

    public void setDefaultValues(){
    }

    public void insertStartData() throws SQLException {

    }

    public String getIDColumnName(){
      return getColumnNameUserID();
    }

    public static User getStaticInstance(){
      return (User)com.idega.user.data.UserBMPBean.getStaticInstance(sClassName);
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

    public Group getGroup(){
      return (Group)getColumnValue(_COLUMNNAME_USER_GROUP_ID);
    }

    public int getGroupID(){
      return getIntColumnValue(_COLUMNNAME_USER_GROUP_ID);
    }

    public int getPrimaryGroupID(){
      return getIntColumnValue(_COLUMNNAME_PRIMARY_GROUP_ID);
    }

    public Group getPrimaryGroup(){
      return (Group)getColumnValue(_COLUMNNAME_PRIMARY_GROUP_ID);
    }

    public Group getUserGroup(){
      return (Group)getColumnValue(this._COLUMNNAME_USER_GROUP_ID);
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

    public void setGroup(Group group){
      setColumn(_COLUMNNAME_USER_GROUP_ID,group);
    }

    public void setPrimaryGroupID(int icGroupId){
      setColumn(_COLUMNNAME_PRIMARY_GROUP_ID,icGroupId);
    }

    public void setPrimaryGroupID(Integer icGroupId){
      setColumn(_COLUMNNAME_PRIMARY_GROUP_ID,icGroupId);
    }


    /*  Setters end   */


    /*  Business methods begin   */

    public void removeAllAddresses()throws IDORemoveRelationshipException{
       super.idoRemoveFrom(Address.class);
    }

    public void removeAllEmails()throws IDORemoveRelationshipException{
        super.idoRemoveFrom(Email.class);
    }

    public void removeAllPhones()throws IDORemoveRelationshipException{
       super.idoRemoveFrom(Phone.class);
    }

    public Collection getAddresses(){
      try{
       return super.idoGetRelatedEntities(Address.class);
      }
      catch(Exception e){
        e.printStackTrace();
        throw new RuntimeException("Error in getAddresses() : "+e.getMessage());
      }
    }

    public Collection getEmails() {
      try{
        return super.idoGetRelatedEntities(Email.class);
      }
      catch(Exception e){
        e.printStackTrace();
        throw new RuntimeException("Error in getEmails() : "+e.getMessage());
      }
    }

    public Collection getPhones() {
      try{
       return super.idoGetRelatedEntities(Phone.class);
      }
      catch(Exception e){
        e.printStackTrace();
        throw new RuntimeException("Error in getPhones() : "+e.getMessage());
      }
    }

    public void addAddress(Address address)throws IDOAddRelationshipException {
       this.idoAddTo(address);
    }

    public void addEmail(Email email)throws IDOAddRelationshipException {
       this.idoAddTo(email);
    }

    public void addPhone(Phone phone)throws IDOAddRelationshipException {
       this.idoAddTo(phone);
    }

    /*  Business methods end   */



    /*  Finders begin   */

    public Collection ejbFindUsersForUserRepresentativeGroups(Collection groupList)throws FinderException{
      String sGroupList = IDOUtil.getInstance().convertListToCommaseparatedString(groupList);
      return this.idoFindIDsBySQL("select * from "+getEntityName()+" where "+_COLUMNNAME_USER_GROUP_ID+" in ("+sGroupList+")");
    }

    public Collection ejbFindAllUsers()throws FinderException{
      return super.idoFindAllIDsBySQL();
    }

    public Collection ejbFindUsersInPrimaryGroup(Group group)throws FinderException,RemoteException{
      return super.idoFindAllIDsByColumnBySQL(_COLUMNNAME_PRIMARY_GROUP_ID,group.getPrimaryKey().toString());
    }

    public Collection ejbFindAllUsersOrderedByFirstName()throws FinderException,RemoteException{
      return super.idoFindAllIDsOrderedBySQL(this.getColumnNameFirstName());
    }

    /*  Finders end   */
}
