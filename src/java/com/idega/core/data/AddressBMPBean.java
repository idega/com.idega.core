//idega 2000 - eiki



package com.idega.core.data;

//import java.util.*;

import java.sql.*;
import java.rmi.RemoteException;
import com.idega.data.*;

import javax.ejb.*;
import com.idega.core.user.data.User;



public class AddressBMPBean extends com.idega.data.GenericEntity implements com.idega.core.data.Address {

        private transient AddressTypeHome addressTypeHome;


	public AddressBMPBean(){

		super();

	}



	public AddressBMPBean(int id)throws SQLException{

		super(id);

	}



	public void initializeAttributes(){

		addAttribute(getIDColumnName());

                addManyToOneRelationship(getColumnNameAddressTypeId(),"Address type",AddressType.class);

		addAttribute("street_name", "Street Name", true, true, String.class);

		addAttribute("street_number", "Street numbeer", true, true, String.class);

                addAttribute("city","City", true, true, String.class);

                addAttribute("providence","Providence", true ,true, String.class);

                addAttribute("p_o_box","PostBox",true, true, String.class);

		addManyToOneRelationship("postal_code_id", "PostalCode",PostalCode.class);

		addManyToOneRelationship("ic_country_id", "Country",Country.class);

                this.addManyToManyRelationShip(User.class,"ic_user_address");

        }



        public static String getColumnNameAddressTypeId() {return "ic_address_type_id";}



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


        public void setPostalCode(PostalCode postalCode){
		setColumn("postal_code_id",postalCode);
	}

	public void setPostalCodeID(int postal_code_id){
		setColumn("postal_code_id",postal_code_id);
	}



	public AddressType getAddressType(){

            return (AddressType) getColumnValue("ic_address_type_id");

	}



	public void setAddressTypeID(int address_type_id){
          setColumn("ic_address_type_id",address_type_id);
	}


	public void setAddressType(AddressType type){
          setColumn("ic_address_type_id",type);
	}

        public int getAddressTypeID() {

            return getIntColumnValue("ic_address_type_id");

        }



	public Country getCountry(){

		return (Country) getColumnValue("ic_country_id");

	}



	public int getCountryId(){

		return getIntColumnValue("ic_country_id");

	}


	public void setCountry(Country country){
		setColumn("ic_country_id",country);
	}

	public void setCountryId(int country_id){
		setColumn("ic_country_id",country_id);
	}

        public AddressTypeHome getAddressTypeHome()throws RemoteException{
          if(addressTypeHome==null){
            addressTypeHome = (AddressTypeHome)IDOLookup.getHome(AddressType.class);
          }
          return addressTypeHome;
        }

        public AddressType ejbHomeGetAddressType1() throws RemoteException{
          try{
            return getAddressTypeHome().findAddressType1();
          }
          catch(FinderException e){
            throw new RemoteException(e.getMessage());
          }
        }


        public AddressType ejbHomeGetAddressType2() throws RemoteException{
          try{
            return getAddressTypeHome().findAddressType2();
          }
          catch(FinderException e){
            throw new RemoteException(e.getMessage());
          }
        }
}

