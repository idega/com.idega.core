package com.idega.core.location.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import com.idega.data.IDOQuery;
import com.idega.data.query.Column;
import com.idega.data.query.InCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.util.CoreUtil;
import com.idega.util.LocaleUtil;
import com.idega.util.StringUtil;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class CountryBMPBean extends GenericEntity implements Country{

  public CountryBMPBean(){
    super();
  }

  public CountryBMPBean(int id)throws SQLException{
    super(id);
  }

  @Override
public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameName(),"Nafn",true,true,String.class,255);
    this.addAttribute(getColumnNameDescription(),"Lýsing",true,true,String.class,500);
    this.addAttribute(getColumnNameIsoAbbreviation(),"ISO skammstöfun",true,true,String.class,10);
    this.getEntityDefinition().setBeanCachingActiveByDefault(true);
  }

  @Override
public String getEntityName() {
    return "ic_country";
  }

  public static String getColumnNameName(){return "country_name";}
  public static String getColumnNameDescription(){return "country_description";}
  public static String getColumnNameIsoAbbreviation(){return "iso_abbreviation";}


  @Override
public void insertStartData()throws Exception{
    String[] JavaLocales = java.util.Locale.getISOCountries();
    Country country;
    Locale locale = Locale.ENGLISH;
    Locale l = null;
    String lang = Locale.ENGLISH.getISO3Language();
    for (int i = 0; i < JavaLocales.length; i++) {
      country = IDOLookup.create(Country.class);
      l = new Locale(lang,JavaLocales[i]);
      country.setName(l.getDisplayCountry(locale));
      country.setIsoAbbreviation(JavaLocales[i]);
      country.store();
    }
  }

  	@Override
	public String getName(Locale locale) {
		return getName(locale, this.getStringColumnValue(getColumnNameName()));
  	}

	private String getName(Locale locale, String defaultName) {
		return LocaleUtil.getLocalizedCountryName(locale, defaultName, getIsoAbbreviation());
	}

  @Override
public String getName(){
    return getName(CoreUtil.getCurrentLocale());
  }

  @Override
public String getDescription(){
    return this.getStringColumnValue(getColumnNameDescription());
  }

 @Override
public String getIsoAbbreviation(){
    return this.getStringColumnValue(getColumnNameIsoAbbreviation());
  }



  @Override
public void setName(String Name){
    this.setColumn(getColumnNameName(),Name);
  }

  @Override
public void setDescription(String Description){
    this.setColumn(getColumnNameDescription(),Description);
  }


  @Override
public void setIsoAbbreviation(String IsoAbbreviation){
    this.setColumn(getColumnNameIsoAbbreviation(),IsoAbbreviation);
  }


  public static Country getStaticInstance(){
    return(Country)getStaticInstance(Country.class);
  }

 public Integer ejbFindByIsoAbbreviation(String abbreviation)throws FinderException{
    Collection countries = idoFindAllIDsByColumnBySQL(getColumnNameIsoAbbreviation(),abbreviation);
    if(!countries.isEmpty()){
      return (Integer)countries.iterator().next();
    }
		else {
			throw new FinderException("Country was not found");
		}
  }

 public Integer ejbFindByCountryName(String name)throws FinderException{
    Collection countries = idoFindAllIDsByColumnBySQL(CountryBMPBean.getColumnNameName(),name);
    if(!countries.isEmpty()){
      return (Integer)countries.iterator().next();
    }
		else {
			throw new FinderException("Country was not found");
		}
  }
  public Collection ejbFindAll() throws FinderException {
  		IDOQuery query = idoQuery();
  		query.appendSelectAllFrom(this).appendOrderBy(getColumnNameName());
		  return super.idoFindPKsByQuery(query);
  }

  public Collection ejbFindAllFromPostalCodes(Collection postalCodes) throws FinderException {
  	Table table = new Table(PostalCode.class);
  	Column postalCol = new Column(table, PostalCodeBMPBean.COLUMN_POSTAL_CODE_ID);

  	SelectQuery query = new SelectQuery(table);
  	query.addColumn(table, PostalCodeBMPBean.COLUMN_COUNTRY_ID, true);
  	if (postalCodes != null && !postalCodes.isEmpty()) {
  		query.addCriteria(new InCriteria(postalCol, postalCodes));
  	}

  	return this.idoFindPKsByQuery(query);
  }

	@Override
	public String getLocalizedName() {
		String abbreviation = getIsoAbbreviation();
		if (!StringUtil.isEmpty(abbreviation)) {
			Locale locale = LocaleUtil.getLocaleByCountry(abbreviation);
			if (locale != null) {
				return locale.getDisplayCountry(locale);
			}
		}

		return getName();
	}
}
