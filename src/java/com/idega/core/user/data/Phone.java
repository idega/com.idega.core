//idega 2000 - eiki

package com.idega.core.user.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;

public class Phone extends GenericEntity{

	public Phone(){
		super();
	}

	public Phone(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("phone_number","Númer",true,true,"java.lang.String");
		addAttribute("country_id","Land",true,true,"java.lang.Integer");
		addAttribute("phone_attribute","Tegund",true,true,"java.lang.String");
                addAttribute("phone_attribute_value","Tegund",true,true,"java.lang.String");
	}

	public String getEntityName(){
		return "phone";
	}

	public void setDefaultValues() {
		setColumn("country_id",1);
	}

	public String getNumber(){
		return (String)getColumnValue("phone_number");
	}

	public void setNumber(String number){
		setColumn("phone_number", number);
	}

	public int getCountryId(){
		return getIntColumnValue("country_id");
	}
/*
	public Country getCountry()throws SQLException{
		return (Country) getColumnValue("country_id");
	}

	public void setCountry(Country country){
		setColumn("country_id",new Integer(country.getID()));
	}
*/
	public void setCountryId(Integer country_id){
		setColumn("country_id", country_id);
	}

	public String getPhoneType(){

		return (String) getColumnValue("phone_type");

	}

		public void setPhoneType(String phone_type){
			setColumn("phone_type", phone_type);
	}




}
