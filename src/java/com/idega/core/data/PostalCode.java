//idega 2000 - Eiki

package com.idega.core.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;

public class PostalCode extends GenericEntity{

	public PostalCode(){
		super();
	}

	public PostalCode(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("postal_code", "Postalcode", true, true, String.class);
		addAttribute("name", "Name", true, true, String.class);
		addManyToOneRelationship("ic_country_id", "Country", Country.class);
	}

        public void insertStartData()throws Exception{
/*            java.util.List countries = EntityFinder.findAllByColumn(Country.getStaticInstance(),"ISO_Abbreviation","IS");
            if (countries != null) {
                Country country = (Country) countries.get(0);
                PostalCode pCode;

                pCode = new PostalCode();
                  pCode.setPostalCode("101");
                  pCode.setName("Reykjavik");
                  pCode.setCountryID(country.getID());
                  pCode.insert();
                pCode = new PostalCode();
                  pCode.setPostalCode("200");
                  pCode.setName("Kópavogur");
                  pCode.setCountryID(country.getID());
                  pCode.insert();
                pCode = new PostalCode();
                  pCode.setPostalCode("201");
                  pCode.setName("Kópavogur");
                  pCode.setCountryID(country.getID());
                  pCode.insert();
            }
            */
        }

	public String getEntityName(){
		return "ic_postal_code";
	}

	public void setPostalCode(String code){
		setColumn("postal_code", code);
	}

	public String getPostalCode(){
		return getStringColumnValue("postal_code");
	}

	public void setName(String name){
		setColumn("name", name);
	}

	public String getName(){
		return getStringColumnValue("name");
	}

	public void setCountry(Country country){
		setColumn("ic_country_id",country);
	}

	public Country getCountry(){
		return (Country)getColumnValue("ic_country_id");
	}

	public void setCountryID(int country_id){
		setColumn("ic_country_id",country_id);
	}

	public int getCountryID(){
		return getIntColumnValue("ic_country_id");
	}

}
