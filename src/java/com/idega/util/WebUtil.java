package com.idega.util;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import javax.ejb.FinderException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.business.DefaultSpringBean;
import com.idega.core.idgenerator.business.IdGeneratorFactory;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;

@Service("webUtil")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class WebUtil extends DefaultSpringBean {

	public String getLocalizedString(String bundleIdentifier, String key, String returnValueIfNotFound) {
		return getMultipleLocalizedStrings(bundleIdentifier, Arrays.asList(
				new AdvancedProperty(key, returnValueIfNotFound)
		)).get(0);
	}

	public List<String> getMultipleLocalizedStrings(String bundleIdentifier, List<AdvancedProperty> multipleRequiredLocalizations) {
		List<String> defaultValues = getDefaultValues(multipleRequiredLocalizations);
		if (ListUtil.isEmpty(multipleRequiredLocalizations)) {
			return defaultValues;
		}

		IWBundle bundle = getBundle(bundleIdentifier);
		if (bundle == null) {
			getLogger().warning("Bundle was not found by identifier: ".concat(bundleIdentifier));
			return defaultValues;
		}

		IWResourceBundle iwrb = getResourceBundle(bundle);
		if (iwrb == null) {
			getLogger().warning("Unable to resolve resource bundle from bundle: " + bundle);
			return defaultValues;
		}

		List<String> localizations = new ArrayList<String>(multipleRequiredLocalizations.size());
		for (AdvancedProperty localizationRequest: multipleRequiredLocalizations) {
			localizations.add(iwrb.getLocalizedString(localizationRequest.getId(), localizationRequest.getValue()));
		}
		return localizations;
	}

	private List<String> getDefaultValues(List<AdvancedProperty> parameters) {
		if (ListUtil.isEmpty(parameters)) {
			return null;
		}

		List<String> defaultValues = new ArrayList<String>(parameters.size());
		for (AdvancedProperty parameter: parameters) {
			defaultValues.add(parameter.getValue());
		}
		return defaultValues;
	}

    public boolean sendEmail(String from, String to, String subject, String message) {
    	if (StringUtil.isEmpty(subject) || StringUtil.isEmpty(message)) {
    		getLogger().warning("Subject or/and message not provided, unable to send a message:\n" + message);
    		return false;
    	}

    	// Printing errors to console
    	if (isDevelopementState()) {
    		getLogger().log(Level.INFO,
        			"To: " + to + "\n" +
        			"From: " + from + "\n" +
        			"Subject: " + subject + "\n" +
        			"Message: " + message);

    		return Boolean.TRUE;
    	}

    	from = StringUtil.isEmpty(from) ? "idegaweb@idega.com" : from;

    	to = StringUtil.isEmpty(to) ? IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty("js_error_mail_to", "abuse@idega.com") : to;
    	if (StringUtil.isEmpty(to)) {
    		getLogger().warning("Receiver is unknown! Unable to send a message:\n" + message);
    		return false;
    	}

    	String host = IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty(CoreConstants.PROP_SYSTEM_SMTP_MAILSERVER);
    	if (StringUtil.isEmpty(host)) {
    		getLogger().warning("Mail server host is unknown, unable to send a message:\n" + message);
    		return false;
    	}

    	String userName = "Not logged in";
    	IWContext iwc = CoreUtil.getIWContext();
    	if (iwc != null && iwc.isLoggedOn()) {
    		userName = iwc.getCurrentUser().getName();
    	}
    	message.concat("\nUser: ").concat(userName);

    	final String fromAddress = from;
    	final String toAddress = to;
    	final String hostName = host;
    	final String sbjct = subject;
    	final String msg = message;
    	Thread sender = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		    		SendMail.send(fromAddress, toAddress, null, null, hostName, sbjct, msg);
		    	} catch(Exception e) {
					getLogger().log(Level.WARNING, "Error while sending email (".concat(msg).concat(") to: ").concat(toAddress), e);
				}
			}
		});
    	sender.start();

    	return true;
    }

    public boolean logOut() {
    	IWContext iwc = CoreUtil.getIWContext();
    	if (iwc == null) {
    		getLogger().warning(IWContext.class.getName() + " is not available!");
    		return false;
    	}
    	if (!iwc.isLoggedOn()) {
    		getLogger().warning("User is not logged in!");
    		return false;
    	}
    	LoginBusinessBean loginBusiness = null;
    	try {
    		loginBusiness = LoginBusinessBean.getLoginBusinessBean(iwc.getRequest().getSession(false));
    	} catch (Exception e) {
    		getLogger().log(Level.WARNING, "Error getting LoginBusiness", e);
    	}
    	if (loginBusiness == null)
    		return false;

    	return loginBusiness.logOutUser(iwc);
    }

    private Boolean latestNavigationUsed = Boolean.TRUE;

    public Boolean isLatestNavigationUsed() {
    	latestNavigationUsed = getApplication().getSettings().getBoolean("html5_navigation", Boolean.FALSE);
    	return latestNavigationUsed;
    }
    public Boolean isLoggedIn() {
    	IWContext iwc = CoreUtil.getIWContext();
    	if (iwc == null)
    		return Boolean.FALSE;
    	try {
    		return iwc.isLoggedOn();
    	} catch (Exception e) {
    		getLogger().log(Level.WARNING, "Error while checking if user is logged in", e);
    	}

    	return Boolean.FALSE;
    }

    public String getApplicationProperty(String name) {
    	if (StringUtil.isEmpty(name))
    		return null;

    	return getApplication().getSettings().getProperty(name);
    }

    public Boolean getBooleanApplicationProperty(String name, boolean defaultValue) {
    	if (StringUtil.isEmpty(name))
    		return false;

    	return getApplication().getSettings().getBoolean(name, defaultValue);
    }

    public String getAutoLoginUri(String personalId, String uri) {
    	if (!getApplication().getSettings().getBoolean("provide_auto_login", Boolean.FALSE)) {
    		getLogger().warning("Auto login URI can not be provided for a user by personal ID: " + personalId);
    		return null;
    	}

    	if (StringUtil.isEmpty(personalId)) {
    		getLogger().warning("Personal ID is not provided");
    		return null;
    	}
    	if (StringUtil.isEmpty(uri)) {
    		getLogger().warning("URI is not provided - can not construct auto login URI");
    		return null;
    	}

    	logOut();

    	User user = null;
    	UserBusiness userBusiness = getServiceInstance(UserBusiness.class);
    	try {
			user = userBusiness.getUser(personalId);
		} catch (RemoteException e) {
			getLogger().log(Level.WARNING, "Error getting user by personal ID: " + personalId, e);
		} catch (FinderException e) {}
    	if (user == null) {
    		getLogger().warning("User was not found by provided personal ID: " + personalId);
    		return null;
    	}

    	URIUtil uriUtil = new URIUtil(uri);
    	String uniqueId = user.getUniqueId();
    	if (StringUtil.isEmpty(uniqueId)) {
    		uniqueId = IdGeneratorFactory.getUUIDGenerator().generateId();
    		user.setUniqueId(uniqueId);
    		user.store();
    	}
    	uriUtil.setParameter(LoginBusinessBean.PARAM_LOGIN_BY_UNIQUE_ID, uniqueId);
    	uriUtil.setParameter(LoginBusinessBean.LoginStateParameter, LoginBusinessBean.LOGIN_EVENT_LOGIN);

		return uriUtil.getUri();
    }

    public String getFirstDayOfCurrentMonth(boolean showTime) {
    	return getLocalizedDate(getFirstDay(showTime), getCurrentLocale(), showTime);
    }

    public static final String getLocalizedDate(IWTimestamp date, Locale locale, boolean showTime) {
    	return showTime ?
    			date.getLocaleDateAndTime(locale, IWTimestamp.SHORT, IWTimestamp.SHORT) :
    			date.getLocaleDate(locale, IWTimestamp.SHORT);
    }

    public static final IWTimestamp getFirstDay(boolean showTime) {
    	IWTimestamp currentTime = IWTimestamp.RightNow();
    	currentTime.setDay(1);
    	if (showTime) {
    		currentTime.setHour(0);
    		currentTime.setMinute(0);
    		currentTime.setSecond(0);
    	}
    	return currentTime;
    }

    public static final IWTimestamp getLastDay(boolean showTime) {
    	IWTimestamp date = getFirstDay(showTime);
    	date.setMonth(date.getMonth() + 1);
    	date.setDay(date.getDay() - 1);
    	if (showTime) {
    		date.setHour(23);
    		date.setMinute(59);
    		date.setSecond(59);
    	}
    	return date;
    }

    public String getLastDayOfCurrentMonth(boolean showTime) {
    	return getLocalizedDate(getLastDay(showTime), getCurrentLocale(), showTime);
    }
}