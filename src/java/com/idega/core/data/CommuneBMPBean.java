package com.idega.core.data;

import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;


public class CommuneBMPBean extends GenericEntity implements Commune {

	private static String COMMUNE_ENTITY_NAME = "ic_commune";

	private static String COLUMN_COMMUNE_NAME = "commune_name";
	private static String COLUMN_COMMUNE_CODE = "commune_code";
	private static String COLUMN_PROVINCE_ID = "ic_province_id";

  public CommuneBMPBean(){
    super();
  }

  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(COLUMN_COMMUNE_NAME, "Commune", true, true, String.class,50);
    addAttribute(COLUMN_COMMUNE_CODE, "Commune code", true, true, String.class, 20);
    addManyToOneRelationship(COLUMN_PROVINCE_ID, "Province", Province.class);
  }

  public String getEntityName(){
    return COMMUNE_ENTITY_NAME;
  }

  /**
   * All names are stored in uppercase, uses String.toUpperCase();
   */
  public void setCommuneName(String name){
    setColumn(COLUMN_COMMUNE_NAME, name.toUpperCase());
  }

  public String getCommuneName(){
    return getStringColumnValue(COLUMN_COMMUNE_NAME);
  }

	public void setCommuneCode(String code) {
		setColumn(COLUMN_COMMUNE_CODE, code);
	}
	
	public String getCommuneCode() {
		return getStringColumnValue(COLUMN_COMMUNE_CODE);
	}

  public void setProvince(Province province){
    setColumn(COLUMN_PROVINCE_ID,province);
  }

  public Province getProvince(){
    return (Province)getColumnValue(COLUMN_PROVINCE_ID);
  }

  public void setProvinceID(int province_id){
    setColumn(COLUMN_PROVINCE_ID,province_id);
  }

  public int getProvinceID(){
    return getIntColumnValue(COLUMN_PROVINCE_ID);
  }

  public Collection ejbFindAllCommunes() throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(this);
		return idoFindPKsByQuery(query);
  }
  
  public Integer ejbFindByCommuneNameAndProvince(String name, Object provinceID) throws FinderException {
    IDOQuery query = idoQuery();
    query.appendSelectAllFrom(this).appendWhereEquals(COLUMN_PROVINCE_ID, provinceID).appendAndEqualsQuoted(COLUMN_COMMUNE_NAME, name);
    return (Integer) idoFindOnePKByQuery(query);
  }
}