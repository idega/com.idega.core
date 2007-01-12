package com.idega.core.location.data;

import java.util.Collection;
import java.rmi.RemoteException;
import javax.ejb.FinderException;
import java.sql.*;
import com.idega.data.*;


public class ProvinceBMPBean extends GenericEntity implements Province {



  public ProvinceBMPBean(){
    super();
  }

  public ProvinceBMPBean(int id)throws SQLException{
    super(id);
  }

  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(getColumnNameProvinceName(), "Province", true, true, String.class,50);
    addManyToOneRelationship("ic_country_id", "Country", Country.class);
  }

  public String getColumnNameProvinceName(){ return "province_name"; }

  public String getEntityName(){
    return "ic_province";
  }

  /**
   * All names are stored in uppercase, uses String.toUpperCase();
   */
  public void setProvinceName(String name){
    setColumn(getColumnNameProvinceName(), name.toUpperCase());
  }

  public String getProvinceName(){
    return getStringColumnValue(getColumnNameProvinceName());
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

  public Integer ejbFindByProvinceNameAndCountryId(String name,int countryId)throws FinderException,RemoteException{
    Collection provinces = idoFindAllIDsByColumnsBySQL(getColumnNameProvinceName(),name, "ic_country_id", Integer.toString(countryId));
    if(!provinces.isEmpty()){
      return (Integer)provinces.iterator().next();
    }
	else {
		throw new FinderException("Province was not found");
	}
  }

}