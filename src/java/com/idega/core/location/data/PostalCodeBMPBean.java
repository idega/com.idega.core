//idega 2000 - Eiki



package com.idega.core.location.data;


import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;



public class PostalCodeBMPBean extends GenericEntity implements PostalCode {

	static final String TABLE_NAME="IC_POSTAL_CODE";
	 
	 static final String COLUMN_POSTAL_CODE_ID="IC_POSTAL_CODE_ID";
	 static final String COLUMN_POSTAL_CODE="POSTAL_CODE";
	 static final String COLUMN_NAME="NAME";
	 static final String COLUMN_COUNTRY_ID="IC_COUNTRY_ID";

	 
	 


  public PostalCodeBMPBean(){
    super();
  }



  public PostalCodeBMPBean(int id)throws SQLException{
    super(id);
  }



  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(COLUMN_POSTAL_CODE, "Postalcode", true, true, String.class,50);
    addAttribute(COLUMN_NAME, "Name", true, true, String.class,50);
    addManyToOneRelationship(COLUMN_COUNTRY_ID, "Country", Country.class);
    
    addIndex("IDX_IC_POSTALCODE", COLUMN_POSTAL_CODE);
    addIndex("IDX_IC_POSTALCODE2", new String[]{COLUMN_POSTAL_CODE, COLUMN_NAME});
    addIndex("IDX_IC_POSTALCODE3", new String[]{COLUMN_POSTAL_CODE, COLUMN_COUNTRY_ID});
    addIndex("IDX_IC_POSTALCODE4", COLUMN_COUNTRY_ID);
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
    return TABLE_NAME;
  }
  
  private String stripWhitespace(String strip){
  		String s = strip;
  		return s.replaceAll("\\s","");
  }

  public void setPostalCode(String code){
  	// remove all whitespace from postal code
    setColumn(COLUMN_POSTAL_CODE, stripWhitespace(code));
  }

  public String getPostalCode(){
    return getStringColumnValue(COLUMN_POSTAL_CODE);
  }

  /**
   * All names are stored in uppercase, uses String.toUpperCase();
   */
  public void setName(String name){
    setColumn(COLUMN_NAME, name.toUpperCase());
  }

  public String getName(){
    return getStringColumnValue(COLUMN_NAME);
  }

  public void setCountry(Country country){
    setColumn(COLUMN_COUNTRY_ID,country);
  }

  public Country getCountry(){
    return (Country)getColumnValue(COLUMN_COUNTRY_ID);
  }

  public void setCountryID(int country_id){
    setColumn(COLUMN_COUNTRY_ID,country_id);
  }

  public int getCountryID(){
    return getIntColumnValue(COLUMN_COUNTRY_ID);
  }
  
 

  public Integer ejbFindByPostalCodeAndCountryId(String code,int countryId)throws FinderException,RemoteException{
  	IDOQuery query = idoQueryGetSelect().appendWhereEquals(COLUMN_COUNTRY_ID,countryId).appendAndEqualsQuoted(COLUMN_POSTAL_CODE,stripWhitespace(code));
	Collection codes = idoFindPKsByQuery(query);
    if(!codes.isEmpty()){
      return (Integer)codes.iterator().next();
    }
    else throw new FinderException("PostalCode not found");
  }

  public Collection ejbFindAllByCountryIdOrderedByPostalCode(int countryId)throws FinderException,RemoteException{
    return this.idoFindAllIDsByColumnOrderedBySQL(COLUMN_COUNTRY_ID, Integer.toString(countryId),COLUMN_POSTAL_CODE);
  }
  
  public Collection ejbHomeFindByName(String name) throws FinderException {
  	return this.idoFindAllIDsByColumnBySQL(COLUMN_NAME, name);
  }
  
  public Collection ejbHomeFindAllUniqueNames() throws RemoteException, FinderException {
  	Collection all = ejbFindAll();
  	Collection names = new Vector();
  	Collection pks = new Vector();
  	if (all != null && !all.isEmpty()) {
  		Iterator iter = all.iterator();
  		PostalCode pc;
  		while (iter.hasNext()) {
  			pc = ((PostalCodeHome) this.getEJBLocalHome()).findByPrimaryKey(iter.next());
  			if (!names.contains(pc.getName())) {
					names.add(pc.getName());
					pks.add(pc.getPrimaryKey());
  			} 
  		}

		}
  	
  	return pks;
  }


  public Collection ejbFindAll()throws FinderException,RemoteException{
    return idoFindAllIDsBySQL();
  }
  
  public Collection ejbFindAllOrdererByCode()throws FinderException,RemoteException{
    return super.idoFindAllIDsOrderedBySQL(COLUMN_POSTAL_CODE);
  }
  
   public String getPostalAddress(){
    	StringBuffer addr = new StringBuffer();
    	String code = getPostalCode();
    	if(code!=null)
    		addr.append(code).append(" ");
    	String name = this.getName();
    	if(name !=null)
    		addr.append(name);
    	return addr.toString();
    }

	public Collection ejbHomeFindByPostalCodeFromTo(String codeFrom, String
	codeTo) throws FinderException {
	IDOQuery query = this.idoQuery();
	query.append("Select * from ").append(getEntityName())
	.append(" where ").append(COLUMN_POSTAL_CODE).append(" >=").append(codeFrom)
	.append(" and ").append(COLUMN_POSTAL_CODE).append(" <= ").append(codeTo)
	.append(" order by ").append(COLUMN_POSTAL_CODE);
	return this.idoFindPKsByQuery(query);
	}


	/* (non-Javadoc)
	 * @see com.idega.core.location.data.PostalCode#isEqualTo(com.idega.core.location.data.PostalCode)
	 */
	public boolean isEqualTo(PostalCode postal) {
		if(postal!=null){
			return (this.getPostalCode().equalsIgnoreCase(postal.getPostalCode()));
			//&& this.getName().equalsIgnoreCase(postal.getName()));
		}
		return false;
	}

}