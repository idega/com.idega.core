package com.idega.core.localisation.data.dao.impl;

import java.util.Locale;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.localisation.data.bean.ICLocale;
import com.idega.core.localisation.data.dao.ICLocaleDAO;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
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

		return getSingleResultByInlineQuery("from " + ICLocale.class.getName() + " l where l." + ICLocale.COLUMN_LOCALE + " = :localeParam",
											ICLocale.class,
											new Param("localeParam", locale)
		);
	}

}
