//idega 2000 - eiki



package com.idega.idegaweb.employment.data;



//import java.util.*;

import java.sql.SQLException;

import com.idega.core.user.data.User;



public class DivisionBMPBean extends com.idega.data.GenericEntity implements com.idega.idegaweb.employment.data.Division {



	public DivisionBMPBean(){

		super();

	}



	public DivisionBMPBean(int id)throws SQLException{

		super(id);

	}



	public void initializeAttributes(){

		addAttribute(getIDColumnName());

		addAttribute("division_name","Nafn deildar",true,true,"java.lang.String");

                addAttribute("parent_id","pabba id",true,true,"java.lang.Integer");

            this.addManyToManyRelationShip(User.class);



	}



        public String getIDColumnName(){

          return "ep_division_id";

        }





	public String getEntityName(){

		return "i_ep_division";

	}



        public int getParentId() {

            return getIntColumnValue("parent_id");

        }



        public void setParentId(int parent_id) {

          setColumn("parent_id",(new Integer(parent_id)));

        }



	public String getName() {

		return getDivisionName();

	}



	public void setName(String name) {

		setDivisionName(name);

	}



	public String getDivisionName() {

		return getStringColumnValue("division_name");

	}



	public void setDivisionName(String name) {

		setColumn("division_name",name);

	}



}

