//idega 2000 - Gimmi



package com.idega.idegaweb.employment.data;

//import java.util.*;

import java.sql.SQLException;
import com.idega.core.user.data.User;
import com.idega.data.genericentity.Member;



public class EmploymentMemberInfoBMPBean extends com.idega.data.GenericEntity implements com.idega.idegaweb.employment.data.EmploymentMemberInfo {



	public EmploymentMemberInfoBMPBean(){

		super();

	}



	public EmploymentMemberInfoBMPBean(int id)throws SQLException{

		super(id);

	}



	public void initializeAttributes(){

		addAttribute(getIDColumnName());

		addAttribute("title","Title",true,true, "java.lang.String");

		addAttribute("education","Education",true,true, "java.lang.String",3500);

//		addAttribute("school","School",true,true, "java.lang.String",3500);

		addAttribute("cv","CV",true,true, "java.lang.String",3500);

		addAttribute("began_work","h�f st�rf", true, true, "java.lang.String");

                addAttribute("","Division",true,true,"java.lang.Integer");

              this.addManyToManyRelationShip(User.class);

	}



        public String getIDColumnName(){

          return "ep_member_info_id";

        }



	public String getEntityName(){

		return "i_ep_member_info";

	}





	public void setBeganWork(String began_work) {

		setColumn("began_work",began_work);

	}



	public String getBeganWork() {

		return getStringColumnValue("began_work");

	}





	public void setDateOfBirth(String date_of_birth) {

		setColumn("date_of_birth",date_of_birth);

	}



	public String getDateOfBirth() {

		return getStringColumnValue("date_of_birth");

	}







	public String getName(){

		String returner;

		if (getID() == -1) {

			returner = "�sk�r� / ur";

		}

		else {

			returner = getMember().getName();

		}



		return returner;

	}



        /**

         * @deprecated

         */

	public Member getMember() {

		return (((com.idega.data.genericentity.MemberHome)com.idega.data.IDOLookup.getHomeLegacy(Member.class)).createLegacy());

	}



	public String getTitle() {

		return getStringColumnValue("title");

	}



	public void setTitle(String title) {

		setColumn("title",title);

	}



	public String getEducation() {

		return getStringColumnValue("education");

	}



	public void setEducation(String education) {

		setColumn("education",education);

	}



	public String getSchool() {

		return getStringColumnValue("school");

	}



	public void setSchool(String school) {

		setColumn("school",school);

	}



	public String getCV() {

		return getStringColumnValue("cv");

	}



	public void setCV(String cv) {

		setColumn("cv",cv);

	}



}

