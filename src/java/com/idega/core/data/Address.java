//idega 2000 - eiki

package com.idega.core.data;
//import java.util.*;
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
                addAttribute("ic_address_type_id","Gerð heimilisfangs",true,true, Integer.class,"many-to-one",AddressType.class);
		addAttribute("street_name", "Heimilisfang", true, true, String.class);
		addAttribute("street_number", "Númer", true, true, String.class);
                addAttribute("city","Borg", true, true, String.class);
                addAttribute("providence","Hérað", true ,true, String.class);
                addAttribute("p_o_box","Pósthólf",true, true, String.class);
		addAttribute("postal_code_id", "Póstnúmer", true, true, Integer.class, "many-to-one",PostalCode.class);
		addAttribute("ic_country_id", "Land id", true, true,Integer.class, "many-to-one",Country.class);
        }

	public String getEntityName(){
		return "ic_address";
	}

	public void setDefaulValues() {
	}

	public String getName(){
		return getStreetName();
	}

	public String getStreetName(){
		return (String) getColumnValue("street_name");
	}

	public void setStreetName(String street_name){
			setColumn("street_name",street_name);	}

	public String getStreetNumber(){
			return getStringColumnValue("street_number");
	}

	public void setStreetNumber(String street_number){
			setColumn("street_number",street_number);
	}

	public void setStreetNumber(int street_number){
			setColumn("street_number",street_number);
	}

	public String getCity(){
		return (String) getColumnValue("city");
	}

	public void setCity(String city){
		setColumn("city",city);
	}


	public String getProvidence(){
		return (String) getColumnValue("providence");
	}

	public void setProvidence(String providence){
		setColumn("providence",providence);
	}
	public String getPOBox(){
		return (String) getColumnValue("p_o_box");
	}

	public void setPOBox(String p_o_box){
		setColumn("p_o_box",p_o_box);
	}

        public PostalCode getPostalCode() throws SQLException {
            return (PostalCode) getColumnValue("postal_code_id");
        }

	public int getPostalCodeID(){
		return getIntColumnValue("postal_code_id");
	}

	public void setPostalCodeID(int postal_code_id){
		setColumn("postal_code_id",postal_code_id);
	}

	public AddressType getAddressType(){
            return (AddressType) getColumnValue("address_type_id");
	}

	public void setAddressTypeID(int address_type_id){
		setColumn("address_type_id",address_type_id);
	}
        public int getAddressTypeID() {
            return getIntColumnValue("address_type_id");
        }


	public Country getCountry(){
		return (Country) getColumnValue("ic_country_id");
	}

	public int getCountryId(){
		return getIntColumnValue("ic_country_id");
	}

	public void setCountryId(int country_id){
		setColumn("ic_country_id",country_id);
	}
}
