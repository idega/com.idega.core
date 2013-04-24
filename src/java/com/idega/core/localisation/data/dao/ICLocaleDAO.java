package com.idega.core.localisation.data.dao;

import java.util.List;
import java.util.Locale;

import com.idega.business.SpringBeanName;
import com.idega.core.localisation.data.bean.ICLocale;
import com.idega.core.persistence.GenericDao;

@SpringBeanName(ICLocaleDAO.BEAN_NAME)
public interface ICLocaleDAO extends GenericDao {

	public static final String BEAN_NAME = "icLocaleDAO";

	public ICLocale getLocale(Locale locale);

	public ICLocale getLocale(String locale);

	public ICLocale doCreateLocale(String locale);

	public List<ICLocale> getAllLocales();

	public List<ICLocale> doFindLocalesByLanguage(String language);

}