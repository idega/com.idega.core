package com.idega.core.data;

import java.util.Collection;
import java.rmi.RemoteException;
import javax.ejb.FinderException;
import java.sql.*;
import com.idega.data.*;


public class CommuneBMPBean extends GenericEntity {//implements Commune {



  public CommuneBMPBean(){
    super();
  }

  public CommuneBMPBean(int id)throws SQLException{
    super(id);
  }

  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(getColumnNameCommuneName(), "Commune", true, true, String.class,50);
    addManyToOneRelationship("ic_province_id", "Province", Province.class);
  }

  public String getColumnNameCommuneName(){ return "commune_name"; }

  public String getEntityName(){
    return "ic_commune";
  }

  /**
   * All names are stored in uppercase, uses String.toUpperCase();
   */
  public void setCommuneName(String name){
    setColumn(getColumnNameCommuneName(), name.toUpperCase());
  }

  public String getCommuneName(){
    return getStringColumnValue(getColumnNameCommuneName());
  }

  public void setProvince(Province province){
    setColumn("ic_province_id",province);
  }

  public Province getProvince(){
    return (Province)getColumnValue("ic_province_id");
  }

  public void setProvinceID(int province_id){
    setColumn("ic_province_id",province_id);
  }

  public int getProvinceID(){
    return getIntColumnValue("ic_province_id");
  }

  public Integer ejbFindByCommuneNameAndProvinceId(String name,int provinceId)throws FinderException,RemoteException{
    Collection communes = idoFindAllIDsByColumnsBySQL(getColumnNameCommuneName(),name, "ic_province_id", Integer.toString(provinceId));
    if(!communes.isEmpty()){
      return (Integer)communes.iterator().next();
    }
    else throw new FinderException("Commune was not found");
  }

}