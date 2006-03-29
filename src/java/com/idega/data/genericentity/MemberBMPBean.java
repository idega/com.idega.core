//idega 2000 - Grímur Jónsson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.data.genericentity;



//import java.util.*;

import java.sql.*;

import com.idega.data.*;

import java.util.List;



/**

 *@deprecated Replaced with com.idega.core.user.data.User

*@author <a href="mailto:gimmi@idega.is">Grímur Jónsson</a>

*@version 1.2

*/



public class MemberBMPBean extends com.idega.data.GenericEntity implements com.idega.data.genericentity.Member,java.lang.Comparable {



  private static String sClassName = "com.idega.data.genericentity.Member";



	public MemberBMPBean(){

		super();

	}



	public MemberBMPBean(int id)throws SQLException{

		super(id);

	}





	public String getEntityName(){

		return "member";

	}



	public void initializeAttributes(){

          addAttribute(getIDColumnName());



          addAttribute("first_name","Fornafn",true,true,"java.lang.String");

          addAttribute("middle_name","Miðnafn",true,true,"java.lang.String");

          addAttribute("last_name","Eftirnafn",true,true,"java.lang.String");

          addAttribute("date_of_birth","Fæðingardagur",true,true,"java.sql.Date");

          addAttribute("gender","Kyn",true,true,"java.lang.String");

          addAttribute("social_security_number","Kennitala",true,true,"java.lang.String");



          /* Tillaga um viðbætur one to many tengs */

//        addAttribute("email_id", "e-mail_id", false,false, "java.lang.Integer");

//        addAttribute("phone_id", "Phone_id", false, false, "java.lang.Integer");

//        addAttribute("address_id", "Address_id", false, false, "java.lang.Integer");



          addAttribute("image_id","MyndNúmer",false,false,"java.lang.Integer");



	}



	public void setDefaultValues(){

		setColumn("image_id",1);

	}

/*

        public com.idega.jmodule.login.data.LoginType[] getLoginType() {

          com.idega.jmodule.login.data.LoginType[] log_type = null;



          try {

            log_type = (com.idega.jmodule.login.data.LoginType[])this.findRelated(new com.idega.jmodule.login.data.LoginType());

          }

          catch (SQLException s) {

          }

          return log_type;



        }

   */



	public int getImageId(){

		return getIntColumnValue("image_id");

	}



	public void setImageId(int image_id){

		setColumn("image_id",image_id);

	}



	public void setimage_id(Integer image_id){

		setColumn("image_id",image_id);

	}





	public Date getDateOfBirth(){

		return (Date) getColumnValue("date_of_birth");

	}



	public void setDateOfBirth(Date dateOfBirth){

		setColumn("date_of_birth",dateOfBirth);

	}



	public String getGender(){

		return getStringColumnValue("gender");

	}



	public void setGender(String gender){

		setColumn("gender",gender);

	}





	public String getSocialSecurityNumber(){

		return getStringColumnValue("social_security_number");

	}



	public void setSocialSecurityNumber(String social_security_number){

		setColumn("social_security_number",social_security_number);

	}







	public String getName() {

		String first = getFirstName();

		String middle = getMiddleName();

		String last = getLastName();



		String name = "";

		if (first == null) { first = ""; }

		if (middle == null) { middle = ""; }

		if (last == null) { last = ""; }



/*		if (first.equals(""))

			name = name + first;

		if (middle.equals(""))

			name = name + " " + middle;

		if (last.equals(""))

			name = name + " " + last;

*/

		name = first + " " + middle + " " + last;



		return name;

	}



	public String getFirstName() {

		return (String) getColumnValue("first_name");

	}



	public String getMiddleName() {

		return (String) getColumnValue("middle_name");

	}



	public String getLastName() {

		return (String) getColumnValue("last_name");

	}



	public void setFirstName(String fName) {

		setColumn("first_name",fName);

	}



	public void setMiddleName(String mName) {

		setColumn("middle_name",mName);

	}



	public void setLastName(String lName) {

		setColumn("last_name",lName);

	}





  /**

   * cuted from com.idega.projects.golf.entity.Member

   */



	public Group[] getGenericGroups()throws SQLException{

		Group group = ((com.idega.data.genericentity.GroupHome)com.idega.data.IDOLookup.getHomeLegacy(Group.class)).createLegacy();

		return (Group[]) findRelated(group);

		//return (Union[])union.findAll("select * from "+union.getEntityName()+" where "+this.getIDColumnName()+"='"+this.getID()+"' ");

	}



        public List getAllGroups() throws SQLException{

          return EntityFinder.findRelated(this,com.idega.data.genericentity.GroupBMPBean.getStaticInstance());

        }



    public static Member getStaticInstance(){

      return (Member)GenericEntity.getStaticInstance(sClassName);

    }



public int getAge() {

    int currentYear = com.idega.util.IWTimestamp.RightNow().getYear();

    int memberYear = 0;



    java.sql.Date date = this.getDateOfBirth();

    if (date != null) {

        com.idega.util.IWTimestamp stamp = new com.idega.util.IWTimestamp(date);

        memberYear = stamp.getYear();

    }

    else {

        String socialSecurityNumber = this.getSocialSecurityNumber();

        if ( socialSecurityNumber != null) {

          if (socialSecurityNumber.length() >= 6) {

              try {

                  memberYear = Integer.parseInt(socialSecurityNumber.substring(4,6));

              }

              catch (NumberFormatException n) {

              }

          }

        }

    }





    return currentYear - memberYear;



}

    /** @todo  */

    /** Implementing Comparable

     *

     */

    public int compareTo(Object M){

      return this.getName().compareTo(((Member) M).getName());

    }



    public static Member getMember(String socialSecurityNumber) {

        Member returner = null;

        try {

            List members = EntityFinder.findAllByColumn(((com.idega.data.genericentity.MemberHome)com.idega.data.IDOLookup.getHomeLegacy(Member.class)).createLegacy(),"SOCIAL_SECURITY_NUMBER",socialSecurityNumber);

            if (members != null) {

                if (members.size()  > 0) {

                    returner = (Member) members.get((members.size()-1));

                }

            }

        }

        catch (SQLException sq) {

            sq.printStackTrace(System.err);

        }



        return returner;

    }





}

