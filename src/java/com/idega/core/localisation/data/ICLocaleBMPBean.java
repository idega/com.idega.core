package com.idega.core.localisation.data;

import java.util.Collection;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.core.location.data.Country;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;

/**
 * <p>
 * Entity bean for the IC_LOCALE table for storing all existing Locales.
 * </p>
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */
public class ICLocaleBMPBean extends com.idega.data.GenericEntity  implements ICLocale{

	private static final long serialVersionUID = 2393090935124729715L;

	public ICLocaleBMPBean() {
	}

  public ICLocaleBMPBean(int id)throws java.sql.SQLException {
    super(id);
  }

  @Override
public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameLocale(),"Locale",true,true,String.class,20);
    this.addAttribute(getColumnNameLanguageId(),"Language",true,true,Integer.class,"many-to-one",ICLanguage.class);
    this.addAttribute(getColumnNameCountryId(),"Country",true,true,Integer.class,"many-to-one",Country.class);
    this.addAttribute(getColumnNameInUse(),"In use",true,true,Boolean.class);
    this.getEntityDefinition().setBeanCachingActiveByDefault(true,true);
    this.getEntityDefinition().setUseFinderCollectionPrefetch(true);
  }

  @Override
public void insertStartData() throws Exception{

    java.util.Locale[] JavaLocales = java.util.Locale.getAvailableLocales();
    ICLocale il;
    String sLocale;
    for (int i = 0; i < JavaLocales.length; i++) {
      il = ((ICLocaleHome)com.idega.data.IDOLookup.getHome(ICLocale.class)).create();
      sLocale = JavaLocales[i].toString();
      il.setLocale(sLocale);
      if(sLocale.equals("en")) {
				il.setInUse(true);
      //else if(sLocale.equals("is_IS"))
      //  il.setInUse(true);
			}
			else {
				il.setInUse(false);
			}
      il.store();
    }
  }


  @Override
public String getEntityName() {
    return getEntityTableName();
  }
  public static String getEntityTableName(){return "IC_LOCALE";}
  public static String getColumnNameLocale(){return "LOCALE";}
  public static String getColumnNameLanguageId(){return "IC_LANGUAGE_ID";}
  public static String getColumnNameCountryId(){return "IC_COUNTRY_ID";}
  public static String getColumnNameInUse(){return "IN_USE";}

  @Override
public String getName(){
    return getLocale();
  }

  @Override
public void setLocale(String sLocaleName){
    setColumn(getColumnNameLocale(),sLocaleName);
  }
  @Override
public String getLocale(){
    return getStringColumnValue(getColumnNameLocale());
  }
  @Override
public void setLanguageId(int iLanguageId){
    setColumn(getColumnNameLanguageId(),iLanguageId);
  }
  @Override
public void setLanguageId(Integer iLanguageId){
    setColumn(getColumnNameLanguageId(),iLanguageId);
  }
  @Override
public int getLanguageId(){
    return getIntColumnValue(getColumnNameLanguageId());
  }
  @Override
public void setCountryId(int iCountryId){
    setColumn(getColumnNameCountryId(),iCountryId);
  }
  @Override
public void setCountryId(Integer iCountryId){
    setColumn(getColumnNameCountryId(),iCountryId);
  }
  @Override
public int getCountryId(){
    return getIntColumnValue(getColumnNameCountryId());
  }
  @Override
public void setInUse(boolean inUse){
    setColumn(getColumnNameInUse() ,inUse);
  }
  @Override
public boolean getInUse(){
    return getBooleanColumnValue(getColumnNameInUse());
  }

  @Override
public int getLocaleID(){
      return ((Integer) getPrimaryKey()).intValue();
  }

  @Override
public Locale getLocaleObject(){
    String localeString = this.getLocale();
    if(localeString!=null){
      return com.idega.core.localisation.business.ICLocaleBusiness.getLocaleFromLocaleString(localeString);
    }
    return null;
  }

  public Collection ejbFindAll() throws FinderException{
      Table table = new Table(this);
      SelectQuery query = new SelectQuery(table);
      query.addColumn(new WildCardColumn());
      //return idoFindPKsByQuery(query);
      return idoFindPKsByQuery(query);
  }

  public Collection ejbFindAllInUse()throws FinderException{
      return ejbFindByUsage(true);
  }

  public Collection ejbFindByUsage(boolean usage) throws FinderException{
      Table table = new Table(this);
      SelectQuery query = new SelectQuery(table);
      query.addColumn(new WildCardColumn());
      query.addCriteria(new MatchCriteria(table,getColumnNameInUse(),MatchCriteria.EQUALS,usage));
      return idoFindPKsByQuery(query);
  }

  public Object ejbFindByLocaleName(String locale) throws FinderException {
	  Table table = new Table(this);
	  SelectQuery query = new SelectQuery(table);
	  query.addColumn(new WildCardColumn());
      query.addCriteria(new MatchCriteria(table,getColumnNameLocale(),MatchCriteria.EQUALS,locale));
	  return idoFindOnePKByQuery(query);
  }

}
