//idega 2000 - Eiki

package com.idega.core.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.GenericEntity;

public class Country extends GenericEntity{

	public Country(){
		super();
	}

	public Country(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("name", "Nafn", true, true, String.class);
		addAttribute("internet_suffix", "internet skammstöfun", true, true, String.class);
                addAttribute("iso_abbreviation","ISO Skammstöfum",true,true, String.class,2);
	}


        public void insertStartData()throws Exception{
            Country country = new Country();
              country.setISOAbbreviation("IS");
              country.setInternetSuffix("is");
              country.setName("Iceland");
            country.insert();
        }

        public static Country getStaticInstance() {
            return (Country) getStaticInstance("com.idega.core.data.Country");
        }

	public String getEntityName(){
		return "ic_country";
	}

	public String getName(){
		return getStringColumnValue("name");
	}

	public void setISOAbbreviation(String abbreviation){
		setColumn("iso_abbreviation", abbreviation);
	}

	public String getISOAbbreviation(){
		return getStringColumnValue("iso_abbreviation");
	}

	public void setInternetSuffix(String internetSuffix){
		setColumn("internet_suffix", internetSuffix);
	}

	public String getInternetSuffix(){
		return getStringColumnValue("internet_suffix");
	}

	public void setName(String name){
		setColumn("name", name);
	}

}
