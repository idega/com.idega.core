package com.idega.core.user.data;

import java.sql.*;
import com.idega.data.*;


public class Address extends GenericEntity{

	public Address(){
		super();
	}

	public Address(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("street", "Heimilisfang", true, true, "java.lang.String");
		addAttribute("street_number", "Númer", true, true, "java.lang.String");
		addAttribute("postal_code_id", "Póstnúmer", true, true, "java.lang.Integer");
		addAttribute("country_id", "Land id", true, true, "java.lang.Integer");

		addAttribute("address_attribute", "Tegund heimisfangs", true, true, "java.lang.String");
		addAttribute("address_value", "Auka uppl.", true, true, "java.lang.String");
	}

	public String getEntityName(){
		return "ic_address";
	}

	public void setDefaulValues() {
        }



        /* ColumnNames begin */

        public static String getStreetColumnName(){
          return "street";
        }

        public static String getStreetNumberColumnName(){
          return "street_number";
        }

        public static String getProvinceColumnName(){
          return "province";
        }

        public static String getCityColumnName(){
          return "city";
        }

        public static String getCountryIdColumnName(){
          return "country_id";
        }

        public static String getPostalCodeIdColumnName(){
          return "postal_code_id";
        }

        public static String getPOBoxColumnName(){
          return "p_o_box";
        }

        /* ColumnNames end */



        public String getName(){
		return getStreet();
	}


	public void setEmail(String email) {
          setColumn("email",email);
	}


	public String getStreet(){
          return (String) getColumnValue("street");
	}

	public void setStreet(String street){
          setColumn("street",street);
        }


	public String getStreetNumber(){
          return getStringColumnValue("street_number");
	}

	public void setStreetNumber(String street_number){
          setColumn("street_number",street_number);
	}

	public void setStreetNumber(int street_number){
          setColumn("street_number",street_number);
	}


	public int getZipcodeId(){
          return getIntColumnValue("zipcode_id");
	}


/*	public void setZipcode(ZipCode zipcode){
		setColumn("zipcode_id",zipcode);
	}

	public ZipCode getZipCode()throws SQLException{
		return (ZipCode) getColumnValue("zipcode_id");
	}
*/


	public void setZipcodeId(Integer zipcode_id){
		setColumn("zipcode_id",zipcode_id);
	}

	public void setZipcodeId(int zipcode_id){
		setColumn("zipcode_id",zipcode_id);
	}


	public String getAddressType(){
		return (String) getColumnValue("address_type");
	}

	public void setAddressType(String address_type){
		setColumn("address_type",address_type);
	}


/*
	public Country getCountry(){
		return (Country) getColumnValue("country_id");
	}
*/

	public int getCountryId(){
		return getIntColumnValue("country_id");
	}

	public void setCountryId(Integer country_id){
		setColumn("country_id",country_id);
	}

	public void setCountryId(int country_id){
		setColumn("country_id",country_id);
	}


	//Many to many relations
	public void setUser(User type){
		setColumn("address_id",new Integer(type.getID()));
	}

	public User getUser()throws SQLException{
		return new User(getIntColumnValue("user_id"));
	}


	//public void addTo(Union union,String MemberShip type){
	//}

}
