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
		addAttribute(getStreetColumnName(), "Heimilisfang", true, true, "java.lang.String",255);
		addAttribute(getStreetNumberColumnName(), "Númer", true, true, "java.lang.String",20);
		addAttribute(getCityColumnName(), "Staður", true, true, "java.lang.String",255);
                addAttribute(getProvinceColumnName(), "Hérað", true, true, "java.lang.String",255);
                addAttribute(getPOBoxColumnName(),"",true,true,"java.lang.String",20);
                addAttribute(getPostalCodeIdColumnName(), "Póstnúmer", true, true, "java.lang.Integer","one_to_one","com.idega.core.localisation.data.PostalCode");
		addAttribute(getCountryIdColumnName(), "Land", true, true, "java.lang.Integer","one_to_one","com.idega.core.localisation.data.Country");
		addAttribute(getAddressTypeId(), "Tegund heimisfangs", true, true, "java.lang.Integer","one_to_one","com.idega.localisation.data.AddressType");

	}

	public String getEntityName(){
		return "ic_address";
	}

	public void setDefaulValues() {
        }



        /* ColumnNames begin */

        public static String getStreetColumnName(){
          return "street_name";
        }

        public static String getStreetNumberColumnName(){
          return "street_number";
        }

        public static String getCityColumnName(){
          return "city";
        }

        public static String getProvinceColumnName(){
          return "province";
        }

        public static String getPOBoxColumnName(){
          return "p_o_box";
        }

        public static String getCountryIdColumnName(){
          return "ic_country_id";
        }

        public static String getPostalCodeIdColumnName(){
          return "ic_postal_code_id";
        }

        public static String getAddressTypeId(){
          return "ic_address_type_id";
        }

        /* ColumnNames end */



        public String getName(){
		return getStreet() + " "+ getStreetNumber();
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





	public int getPostalCodeId(){
          return getIntColumnValue("zipcode_id");
	}


/*	public void setZipcode(ZipCode zipcode){
		setColumn("zipcode_id",zipcode);
	}

	public ZipCode getZipCode()throws SQLException{
		return (ZipCode) getColumnValue("zipcode_id");
	}
*/


	public void setPostalCodeId(Integer postalcode_id){
		setColumn("zipcode_id",postalcode_id);
	}

	public void setPostalCodeId(int postalcode_id){
		setColumn("zipcode_id",postalcode_id);
	}


	public String getAddressType(){
		return (String) getColumnValue("address_type");
	}

	public void setAddressType(String address_type){
		setColumn("address_type",address_type);
	}

	public int getCountryId(){
		return getIntColumnValue("country_id");
	}

	public void setCountryId(Integer country_id){
		setColumn("country_id",country_id);
	}

	public void setCountryId(int country_id){
		setColumn("country_id",country_id);
	}

}
