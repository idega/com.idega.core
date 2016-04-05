package com.idega.core.business;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.builder.business.AdvancedPropertyComparator;
import com.idega.business.IBOLookup;
import com.idega.business.IBOService;
import com.idega.business.IBOSession;
import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.core.cache.IWCacheManager2;
import com.idega.data.IDOEntity;
import com.idega.data.IDOHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.DefaultIWBundle;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.repository.RepositoryService;
import com.idega.servlet.filter.RequestResponseProvider;
import com.idega.user.dao.UserDAO;
import com.idega.user.data.UserHome;
import com.idega.user.data.bean.User;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.expression.ELUtil;

/**
 * Common methods most often needed in Spring beans
 *
 * @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
 * @version $Revision: 1.0 $
 *
 * Last modified: $Date: 2009.10.04 19:26:46 $ by: $Author: valdas $
 */
public abstract class DefaultSpringBean {

	private static final Logger LOGGER = Logger.getLogger(DefaultSpringBean.class.getName());
	private static Logger LOGGER_;

	private RepositoryService repositoryService;

	protected Logger getLogger() {
		return getLogger(getClass());
	}

	protected static Logger getLogger(Class<?> theClass) {
		if (LOGGER_ == null) {
			LOGGER_ = Logger.getLogger(theClass.getName());
		}
		return LOGGER_;
	}

	protected <B extends IBOSession> B getSessionInstance(IWUserContext iwuc, Class<B> sessionBeanClass) {
		try {
			return IBOLookup.getSessionInstance(iwuc, sessionBeanClass);	//	Casting is needed to avoid stupid compilation error in Maven 2
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting session instance: " + sessionBeanClass);
		}
		return null;
	}

	protected <B extends IBOService> B getServiceInstance(Class<B> serviceBeanClass) {
		return getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), serviceBeanClass);
	}

	protected <B extends IBOService> B getServiceInstance(IWApplicationContext iwac, Class<B> serviceBeanClass) {
		try {
			return IBOLookup.getServiceInstance(iwac == null ? IWMainApplication.getDefaultIWApplicationContext(): iwac, serviceBeanClass);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting service instance: " + serviceBeanClass);
		}
		return null;
	}

	protected Locale getCurrentLocale() {
		IWContext iwc = CoreUtil.getIWContext();
		Locale locale = iwc == null ? null : iwc.getCurrentLocale();

		if (locale == null) {
			locale = IWMainApplication.getDefaultIWMainApplication().getDefaultLocale();
		}

		return locale == null ? Locale.ENGLISH : locale;
	}

	protected User getCurrentUser() {
		User user = null;
		try {
			LoginSession loginSession = ELUtil.getInstance().getBean(LoginSession.class);
			user = loginSession.getUserEntity();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting current user");
		}

		if (user == null) {
			IWContext iwc = CoreUtil.getIWContext();
			user = iwc == null ? null : iwc.isLoggedOn() ? iwc.getLoggedInUser() : null;
		}

		return user;
	}

	protected User getUser(com.idega.user.data.User legacyUser) {
		if (legacyUser == null) {
			return null;
		}

		UserDAO userDAO = ELUtil.getInstance().getBean(UserDAO.class);
		return userDAO.getUser(Integer.valueOf(legacyUser.getId()));
	}
	protected com.idega.user.data.User getLegacyUser() {
		return getLegacyUser(getCurrentUser());
	}
	protected com.idega.user.data.User getLegacyUser(User newUser) {
		if (newUser == null) {
			return null;
		}

		UserHome userHome = getHomeForEntity(com.idega.user.data.User.class);
		try {
			return userHome.findByPrimaryKey(newUser.getId());
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected <H extends IDOHome> H getHomeForEntity(Class<? extends IDOEntity> entityClass) {
		try {
			@SuppressWarnings("unchecked")
			H home = (H) IDOLookup.getHome(entityClass);
			return home;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Som error occurred getting home interface for entity: " + entityClass, e);
		}
		return null;
	}

	/**
	 * 
	 * <p>Application properties are defined 
	 * at ~/workspace/developer/applicationproperties/</p>
	 * @param propertyName is 'property key name',
	 * not <code>null</code>;
	 * @return 'property key value' or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected String getApplicationProperty(String propertyName) {
		IWMainApplicationSettings settings = getSettings();
		if (settings != null) {
			return settings.getProperty(propertyName);
		}

		return null;
	}

	/**
	 * 
	 * <p>Application properties are defined 
	 * at ~/workspace/developer/applicationproperties/</p>
	 * @param propertyName is 'property key name',
	 * not <code>null</code>;
	 * @param defaultPropertyValue is initial value of 'property key value',
	 * not <code>null</code>;
	 * @return 'property key value' or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected String getApplicationProperty(
			String propertyName, 
			String defaultPropertyValue) {
		IWMainApplicationSettings settings = getSettings();
		if (settings != null) {
			return settings.getProperty(propertyName, defaultPropertyValue);
		}

		return null;
	}
	
	protected IWMainApplicationSettings getSettings() {
		IWMainApplication application = getApplication();
		if (application != null) {
			return application.getSettings();
		}

		return null;
	}

	protected IWMainApplication getApplication() {
		return IWMainApplication.getDefaultIWMainApplication();
	}

	protected IWApplicationContext getIWApplicationContext() {
		return IWMainApplication.getDefaultIWApplicationContext();
	}

	protected <K extends Serializable, V> Map<K, V> getCache(String cacheName) {
		return getCache(cacheName, IWCacheManager2.DEFAULT_CACHE_TTL_SECONDS);
	}

	/**
	 * Returns cache ({@link Map})
	 *
	 * @param <K>
	 * @param <V>
	 * @param cacheName
	 * @param timeToLive - time in seconds
	 * @return
	 */
	protected <K extends Serializable, V> Map<K, V> getCache(String cacheName, long timeToLive) {
		return getCache(cacheName, timeToLive, IWCacheManager2.DEFAULT_CACHE_SIZE);
	}

	protected <K extends Serializable, V> Map<K, V> getCache(String cacheName, long timeToLive, int size) {
		return getCache(cacheName, IWCacheManager2.DEFAULT_CACHE_TTL_IDLE_SECONDS, timeToLive, size, true);
	}

	protected <K extends Serializable, V> Map<K, V> getCache(String cacheName, long timeToIdle, long timeToLive, int size, boolean resetable) {
		try {
			return IWCacheManager2.getInstance(getApplication()).getCache(cacheName, size, IWCacheManager2.DEFAULT_OVERFLOW_TO_DISK, IWCacheManager2.DEFAULT_ETERNAL,
					timeToIdle, timeToLive,	resetable);
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error getting cache!", e);
		}
		return null;
	}

	protected IWBundle getBundle(String bundleIdentifier) {
		try {
			return getApplication().getBundle(bundleIdentifier);
		} catch (Exception e) {}
		return null;
	}

	protected IWResourceBundle getResourceBundle(IWBundle bundle) {
		if (bundle != null) {
			Locale locale = getCurrentLocale();
			IWContext iwc = null;
			if (locale == null) {
				iwc = CoreUtil.getIWContext();
			}

			if (locale == null && iwc == null) {
				locale = Locale.ENGLISH;
				LOGGER.warning("Will use default locale (" + locale + ") for resource bundle, because was unable to resolve both - IWContext and current locale");
			}

			return locale == null ? bundle.getResourceBundle(iwc) : bundle.getResourceBundle(locale);
		}

		return null;
	}

	protected HttpSession getSession() {
		RequestResponseProvider provider = null;
		try {
			provider = ELUtil.getInstance().getBean(RequestResponseProvider.class);
		} catch (Exception e) {}

		HttpSession session = null;
		if (provider != null && provider.getRequest() != null)
			session = provider.getRequest().getSession(Boolean.TRUE);

		if (session == null) {
			IWContext iwc = CoreUtil.getIWContext();
			if (iwc != null)
				session = iwc.getSession();
		}

		return session;
	}

	protected RepositoryService getRepositoryService() {
		if (repositoryService == null)
			repositoryService = ELUtil.getInstance().getBean(RepositoryService.class);
		return repositoryService;
	}

	protected String getHost() {
    	return CoreUtil.getHost();
    }

	protected void doSortValues(List<AdvancedProperty> values, Map<String, String> container, Locale locale) {
		doSortValues(values, container, locale, Boolean.FALSE);
	}

	protected void doSortValues(List<AdvancedProperty> values, Map<String, String> container, Locale locale, boolean descending) {
		Collections.sort(values, new AdvancedPropertyComparator(locale, descending));

		for (AdvancedProperty value: values)
			container.put(value.getId(), value.getValue());
	}

	/**
	 * <p>Takes property from /workspace/developer/applicationproperties named
	 * "is_developement_mode". Check this property, when you need to add code
	 * necessary for development, but useless to production environment.</p>
	 * @return <code>true</code> if it is developing environment,
	 * <code>false</code> otherwise.
	 * @see CoreConstants#DEVELOPEMENT_STATE_PROPERTY
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	protected boolean isDevelopementState() {
		return !DefaultIWBundle.isProductionEnvironment();
	}

	protected <T> Map<String, T> getBeansOfType(Class<T> type) {
		Map<String, T> beans = WebApplicationContextUtils.getWebApplicationContext(getApplication().getServletContext()).getBeansOfType(type);
		return beans;
	}
	protected <T> Collection<T> getBeans(Class<T> type) {
		Map<String, T> beans = getBeansOfType(type);
		if (MapUtil.isEmpty(beans))
			return Collections.emptyList();
		return beans.values();
	}
}