//idega 2000 - Eiki



package com.idega.core.data;



//import java.util.*;

import java.util.Collection;
import java.rmi.RemoteException;
import javax.ejb.FinderException;
import java.sql.*;

import com.idega.data.*;



public class PostalCodeBMPBean extends GenericEntity implements com.idega.core.data.PostalCode {



  public PostalCodeBMPBean(){
    super();
  }



  public PostalCodeBMPBean(int id)throws SQLException{
    super(id);
  }



  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute("postal_code", "Postalcode", true, true, String.class,50);
    addAttribute("name", "Name", true, true, String.class,50);
    addManyToOneRelationship("ic_country_id", "Country", Country.class);
  }


  public void insertStartData()throws Exception{
/*            java.util.List countries = EntityFinder.findAllByColumn(com.idega.core.data.CountryBMPBean.getStaticInstance(),"ISO_Abbreviation","IS");
      if (countries != null) {
          Country country = (Country) countries.get(0);
          PostalCode pCode;
          pCode = ((com.idega.core.data.PostalCodeHome)com.idega.data.IDOLookup.getHomeLegacy(PostalCode.class)).createLegacy();
            pCode.setPostalCode("101");
            pCode.setName("Reykjavik");
            pCode.setCountryID(country.getID());
            pCode.insert();
          pCode = ((com.idega.core.data.PostalCodeHome)com.idega.data.IDOLookup.getHomeLegacy(PostalCode.class)).createLegacy();
            pCode.setPostalCode("200");
            pCode.setName("Kópavogur");
            pCode.setCountryID(country.getID());
            pCode.insert();
          pCode = ((com.idega.core.data.PostalCodeHome)com.idega.data.IDOLookup.getHomeLegacy(PostalCode.class)).createLegacy();
            pCode.setPostalCode("201");<
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

  public Integer ejbFindByPostalCodeAndCountryId(String code,int countryId)throws FinderException,RemoteException{
    Collection codes = idoFindAllIDsByColumnsBySQL("postal_code",code, "ic_country_id", Integer.toString(countryId));
    if(!codes.isEmpty()){
      return (Integer)codes.iterator().next();
    }
    else throw new FinderException("PostalCode not found");
  }

}