package com.idega.core.messaging;

import com.idega.idegaweb.IWMainApplication;

/**
 * <p>
 * Class to fetch system wide settings for messaging (for emailing).
 * </p>
 *  Last modified: $Date: 2008/05/09 01:04:42 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class MessagingSettings {

	public static final String DEFAULT_SMTP_MAILSERVER = "mail.idega.com";
	public static final String PROP_SYSTEM_SMTP_MAILSERVER = "messagebox_smtp_mailserver";
	public static final String PROP_SYSTEM_SMTP_USER_NAME = "messagebox_smtp_username";
	public static final String PROP_SYSTEM_SMTP_PASSWORD = "messagebox_smtp_password";
	public static final String PROP_MESSAGEBOX_FROM_ADDRESS = "messagebox_from_mailaddress";
	public static final String PROP_SYSTEM_FORCED_RECEIVER = "messagebox_forced_receiver_address";
	public static final String PROP_SYSTEM_BCC_RECEIVER = "messagebox_bcc_receiver_address";
	public static final String DEFAULT_MESSAGEBOX_FROM_ADDRESS = "messagebox@idega.com";
	public static final String PROPERTY_EMAIL_ENABLED = "can_send_email";
	public static final String PROP_SYSTEM_SMTP_USE_AUTHENTICATION = "messagebox_smtp_authenticate";
	
	public static String PROPERTY_VALUE_NOTSET="notset";
	
	private IWMainApplication iwma;
	
	public MessagingSettings(IWMainApplication iwma){
		this.iwma=iwma;
	}
	
	private String getProperty(String propertyKey){
		return getProperty(propertyKey,null);
	}
	
	private String getProperty(String propertyKey,String defaultValue){
		if(defaultValue==null){
			return this.iwma.getSettings().getProperty(propertyKey);
		}
		else{
			return this.iwma.getSettings().getProperty(propertyKey,defaultValue);
		}
	}
	
	private void setProperty(String propertyKey,String propertyValue){
		this.iwma.getSettings().setProperty(propertyKey,propertyValue);
	}
	
	public String getBCCReceiver() {
		String theRet =  getProperty(PROP_SYSTEM_BCC_RECEIVER);
		if(theRet!=null){
			if(theRet.equals(PROPERTY_VALUE_NOTSET)){
				return null;
			}
		}
		return theRet;
	}
	
	public void setBCCReceiver(String receiver) {
		setProperty(PROP_SYSTEM_BCC_RECEIVER, receiver);
	}
	
	public String getForcedReceiver() {
		String theRet =  getProperty(PROP_SYSTEM_FORCED_RECEIVER);
		if(theRet!=null){
			if(theRet.equals(PROPERTY_VALUE_NOTSET)){
				return null;
			}
		}
		return theRet;
	}
	
	public void setForcedReceiver(String forcedReceiver) {
		setProperty(PROP_SYSTEM_FORCED_RECEIVER, forcedReceiver);
	}
	
	public String getFromMailAddress() {
		return getProperty(PROP_MESSAGEBOX_FROM_ADDRESS, DEFAULT_MESSAGEBOX_FROM_ADDRESS);
	}
	
	public void setFromMailAddress(String fromMailAddress) {
		setProperty(PROP_MESSAGEBOX_FROM_ADDRESS, fromMailAddress);
	}
	
	public String getSMTPMailServer() {
		return getProperty(PROP_SYSTEM_SMTP_MAILSERVER,DEFAULT_SMTP_MAILSERVER);
	}
	
	public void setSMTPMailServer(String mailServer) {
		setProperty(PROP_SYSTEM_SMTP_MAILSERVER, mailServer);
	}
	
	/**
	 * <p>
	 * Returns if emailing is switched on or off globally<br/>
	 * This uses the global "can_send_email" application property.
	 * </p>
	 * @return
	 */
	public boolean isEmailingEnabled(){
		String theRet = getProperty(PROPERTY_EMAIL_ENABLED);
		if(theRet!=null){
			try{
				Boolean b = Boolean.valueOf(theRet);
				return b.booleanValue();
			}
			catch(Exception e){}
		}
		return true;
	}
	
}
