package com.idega.core.localisation.data.dao.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.localisation.data.bean.ICLocale;
import com.idega.core.localisation.data.dao.ICLocaleDAO;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

@Repository(ICLocaleDAO.BEAN_NAME)
@Transactional(readOnly = true)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ICLocaleDAOImpl extends GenericDaoImpl implements ICLocaleDAO {

	@Override
	public ICLocale getLocale(Locale locale) {
		if (locale == null)
			return null;

		return getLocale(locale.toString());
	}

	@Override
	public ICLocale getLocale(String locale) {
		if (StringUtil.isEmpty(locale))
			return null;

		List<ICLocale> locales = null;
		try {
			locales = getResultListByInlineQuery(
					"from " + ICLocale.class.getName() + " l where l." + ICLocale.COLUMN_LOCALE + " = :localeParam",
					ICLocale.class,
					new Param("localeParam", locale)
			);
		} catch (Exception e) {}
		if (ListUtil.isEmpty(locales))
			return doCreateLocale(locale);

		return locales.get(0);
	}

	@Override
	@Transactional(readOnly = false)
	public ICLocale doCreateLocale(String locale) {
		if (StringUtil.isEmpty(locale))
			return null;

		ICLocale icLocale = new ICLocale();
		icLocale.setLocale(locale);
		icLocale.setInUse(locale.equals(Locale.ENGLISH.toString()));
		persist(icLocale);
		return icLocale.getId() == null ? null : icLocale;
	}
	
	@Override
	public List<ICLocale> getByLocales(List<String> locales){
		return getResultList(
				ICLocale.QUERY_GET_BY_LOCALES, 
				ICLocale.class,
				new Param("locales", locales)
		);
	}

	@Override
	public List<ICLocale> getAllLocales() {
		return getResultListByInlineQuery("from " + ICLocale.class.getName() + " l", ICLocale.class);
	}

	@Override
	public List<ICLocale> doFindLocalesByLanguage(String language) {
		if (StringUtil.isEmpty(language))
			return null;

		return getResultListByInlineQuery("from " + ICLocale.class.getName() + " l where l.locale like :language", ICLocale.class,
				new Param("language", language));
	}

}