package com.idega.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.core.business.DefaultSpringBean;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;

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
    	
    	from = StringUtil.isEmpty(from) ? "idegaweb@idega.com" : from;
    	
    	to = StringUtil.isEmpty(to) ? IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty("js_error_mail_to", "programmers@idega.com") : to;
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
}