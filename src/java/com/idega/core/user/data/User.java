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

    private static String sClassName = "com.idega.core.user.data.User";

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

      addAttribute(getFirstNameColumnName(),"Fornafn",true,true,"java.lang.String");
      addAttribute(getMiddleNameColumnName(),"Miðnafn",true,true,"java.lang.String");
      addAttribute(getLastNameColumnName(),"Eftirnafn",true,true,"java.lang.String");
      addAttribute(getDisplayNameColumnName(),"Kenninafn",true,true,"java.lang.String");
      addAttribute(getDescriptionColumnName(),"Lýsing",true,true,"java.lang.String");
      addAttribute(getDateOfBirthColumnName(),"Fæðingardagur",true,true,"java.sql.Date");
      addAttribute(getGenderColumnName(),"Kyn",true,true,"java.lang.Integer","many_to_one","com.idega.core.user.data.Gender");
      addAttribute(getSystemImageColumnName(),"Kerfismynd",true,true,"java.lang.Integer","one_to_one","com.idega.core.data.Image");

    }

    public void setDefaultValues(){
    }

    public void insertStartData() throws SQLException {
      User firstUser = new User();
      firstUser.setFirstName(getAdminDefaultName());
      firstUser.insert();
    }

    public String getIDColumnName(){
      return getUserIDColumnName();
    }

    public static User getStaticInstance(){
      return (User)User.getStaticInstance(sClassName);
    }

    public static String getAdminDefaultName(){
      return "Administrator";
    }




    /*  ColumNames begin   */

    public static String getUserIDColumnName(){
      return "ic_user_id";
    }

    public static String getFirstNameColumnName(){
      return "first_name";
    }

    public static String getMiddleNameColumnName(){
      return "middle_name";
    }

    public static String getLastNameColumnName(){
      return "last_name";
    }

    public static String getDisplayNameColumnName(){
      return "display_name";
    }

    public static String getDescriptionColumnName(){
      return "description";
    }

    public static String getDateOfBirthColumnName(){
      return "date_of_birth";
    }

    public static String getGenderColumnName(){
      return "ic_gender_id";
    }

    public static String getSystemImageColumnName(){
      return "system_image_id";
    }

    /*  ColumNames end   */


    /*  Getters begin   */

    public String getFirstName() {
      return (String) getColumnValue(getFirstNameColumnName());
    }

    public String getMiddleName() {
      return (String) getColumnValue(getMiddleNameColumnName());
    }

    public String getLastName() {
      return (String) getColumnValue(getLastNameColumnName());
    }

    public String getDisplayName() {
      return (String) getColumnValue(getLastNameColumnName());
    }

    public String getDescription() {
      return (String) getColumnValue(getLastNameColumnName());
    }

    public Date getDateOfBirth(){
      return (Date) getColumnValue(getDateOfBirthColumnName());
    }

    public int getGenderID(){
      return getIntColumnValue(getGenderColumnName());
    }

    public int getSystemImageID(){
      return getIntColumnValue(getSystemImageColumnName());
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
      setColumn(getFirstNameColumnName(),fName);
    }

    public void setMiddleName(String mName) {
      setColumn(getMiddleNameColumnName(),mName);
    }

    public void setLastName(String lName) {
      setColumn(getLastNameColumnName(),lName);
    }

    public void setDisplayName(String dName) {
      setColumn(getLastNameColumnName(),dName);
    }

    public void setDescription(String description) {
      setColumn(getLastNameColumnName(),description);
    }

    public void setDateOfBirth(Date dateOfBirth){
      setColumn(getDateOfBirthColumnName(),dateOfBirth);
    }

    public void setGender(Integer gender){
      setColumn(getGenderColumnName(),gender);
    }

    public void setGender(int gender){
      setColumn(getGenderColumnName(),gender);
    }

    public void setSystemImageID(Integer imageID){
      setColumn(getSystemImageColumnName(),imageID);
    }

    public void setSystemImageID(int imageID){
      setColumn(getSystemImageColumnName(),imageID);
    }

    /*  Setters end   */


    /*  relationship begin  */

    public List getAllGroups() throws SQLException{
      return EntityFinder.findRelated(this,GenericGroup.getStaticInstance());
    }
/*
    public List getPermissionGroups() throws SQLException{
      return EntityFinder.findRelated(this,PermissionGroup.getStaticPermissionGroupInstance());
    }
*/
    /* relationship end */





    /*  OLD   */


    public GenericGroup[] getGenericGroups()throws SQLException{
      GenericGroup group = new GenericGroup();
      return (GenericGroup[]) findRelated(group);
    }




}
