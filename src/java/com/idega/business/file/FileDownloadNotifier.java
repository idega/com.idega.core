package com.idega.business.file;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.contact.data.Email;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.SendMail;
import com.idega.util.StringUtil;

public abstract class FileDownloadNotifier implements Serializable {

	private static final long serialVersionUID = -5759650675280042585L;
	
	private static final Logger LOGGER = Logger.getLogger(FileDownloadNotifier.class.getName());
	
	public AdvancedProperty sendNotifications(FileDownloadNotificationProperties properties) {
		AdvancedProperty result = new AdvancedProperty(Boolean.FALSE.toString(), "Unable to send notifications");
		
		if (properties == null || StringUtil.isEmpty(properties.getUrl())) {
			LOGGER.warning("Invalid properties!");
			return result;
		}
		
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			LOGGER.warning(IWContext.class + " is unavailable");
			return result;
		}
		
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);

		ICFile file = getFile(properties);
		
		List<String> usersIds = properties.getUsers();
		if (ListUtil.isEmpty(usersIds)) {
			LOGGER.warning("There are no recipients of this notification!");
			return result;
		}
		List<User> users = getUsers(usersIds);
		if (ListUtil.isEmpty(users)) {
			LOGGER.warning("There are no recipients of this notification!");
			return result;
		}
		
		Map<String, AdvancedProperty> emailsAndLinks = getEmails(file, properties, users);
		if (emailsAndLinks == null || emailsAndLinks.isEmpty()) {
			LOGGER.warning("Unable to resolve e-mail addresses for recipients: " + usersIds);
			return result;
		}
		
		Map<String, String> uris = getUriToDocument(properties, users);
		if (uris == null || uris.isEmpty()) {
			LOGGER.warning("Unable to resolve links to documents for recipients: " + usersIds);
			return result;
		}
		
		String subject = new StringBuilder(iwrb.getLocalizedString("file_download_notifier.reminder_to_download_document", "Reminder to download document"))
			.toString();
		if (file != null) {
			try {
				subject = new StringBuilder(subject).append(": ").append(URLDecoder.decode(file.getName(), CoreConstants.ENCODING_UTF8)).toString();
			} catch(UnsupportedEncodingException e) {
				LOGGER.log(Level.WARNING, "Error decoding: " + file.getName(), e);
			}
		}
		
		String pageMessagePart1 = new StringBuilder(iwrb.getLocalizedString("file_download_notifier.reminder_message_for_document_to_download",
			"Please download document. You can find it: ")).toString();
		String pageMessagePart2 = iwrb.getLocalizedString("file_download_notifier.or_directly_download", "Or directly download from: ");
		
		IWMainApplicationSettings settings = iwc.getApplicationSettings();
		String host = settings.getProperty(CoreConstants.PROP_SYSTEM_SMTP_MAILSERVER);
		String from = settings.getProperty(CoreConstants.PROP_SYSTEM_MAIL_FROM_ADDRESS);
		if (StringUtil.isEmpty(from)) {
			LOGGER.warning("Address 'from' is not defined, unable to send message: " + subject + " to: " + emailsAndLinks);
			return result;
		}
		
		for (String userId: emailsAndLinks.keySet()) {
			AdvancedProperty emailAndLink = emailsAndLinks.get(userId);
			if (emailAndLink == null) {
				LOGGER.warning("No email and/or link to attachment found for user: " + userId);
				continue;
			}
			
			String uriToDocument = uris.get(userId);
			if (StringUtil.isEmpty(uriToDocument)) {
				LOGGER.warning("No uri found to the document for user: " + userId);
				continue;
			}
			
			String message = new StringBuilder(pageMessagePart1).append(uriToDocument).append(CoreConstants.SPACE).append(pageMessagePart2)
								.append(emailAndLink.getValue()).toString();
			if (!sendNotification(emailAndLink.getId(), subject, message, host, from)) {
				return result;
			}
		}
		
		result.setId(Boolean.TRUE.toString());
		result.setValue(iwrb.getLocalizedString("file_download_notifier.reminders_sent_successfully", "Reminders were sent successfully"));
		return result;
	}
	
	private boolean sendNotification(String recipient, String subject, String message, String host, String from) {
		if (StringUtil.isEmpty(recipient) || StringUtil.isEmpty(subject) || StringUtil.isEmpty(message)) {
			LOGGER.warning("Some of fields are not provided: recipient: '"+recipient+"', subject: '"+subject+"', message: '"+message+"'");
			return false;
		}
		
		try {
			SendMail.send(from, recipient, null, null, host, subject, message);
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error sending message: " + subject + " to: " + recipient, e);
			return false;
		}
		
		return true;
	}
	
	private ICFile getFile(String id) {
		try {
			ICFileHome fileHome = (ICFileHome) IDOLookup.getHome(ICFile.class);
			return fileHome.findByPrimaryKey(id);
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "File was not found by id: " + id);
		}
		return null;
	}
	
	protected ICFile getFile(Integer hash) {
		try {
			ICFileHome fileHome = (ICFileHome) IDOLookup.getHome(ICFile.class);
			return fileHome.findByHash(hash);
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "File was not found by hash: " + hash);
		}
		return null;
	}
	
	private Map<String, AdvancedProperty> getEmails(ICFile attachment, FileDownloadNotificationProperties properties, List<User> users) {
		if (StringUtil.isEmpty(properties.getServer())) {
			return null;
		}
		
		UserBusiness userBusiness = null;
		try {
			userBusiness = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), UserBusiness.class);
		} catch (IBOLookupException e) {
			LOGGER.log(Level.WARNING, "Error getting UserBusiness", e);
		}
		if (userBusiness == null) {
			return null;
		}
		
		Map<String, AdvancedProperty> emailsAndLinks = new HashMap<String, AdvancedProperty>(users.size());
		for (User user: users) {	
			Email email = null;
			try {
				email = userBusiness.getEmailHome().findMainEmailForUser(user);
			} catch(Exception e) {
				LOGGER.log(Level.WARNING, "Error getting email for user: " + user, e);
			}
			
			if (email != null) {
				String uri = getUriToAttachment(properties, user);
				
				if (StringUtil.isEmpty(uri)) {
					LOGGER.warning("Unable to resolve file uri for user: " + user + " and email: " + email + attachment == null ?
							"" : " and file: " + attachment.getId());
				} else {
					emailsAndLinks.put(user.getId(), new AdvancedProperty(email.getEmailAddress(),
							new StringBuilder(properties.getServer()).append(uri).toString()));
				}
			}
		}
		
		return emailsAndLinks;
	}
	
	private List<User> getUsers(List<String> usersIds) {
		UserBusiness userBusiness = null;
		try {
			userBusiness = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), UserBusiness.class);
		} catch (IBOLookupException e) {
			LOGGER.log(Level.WARNING, "Error getting UserBusiness", e);
		}
		if (userBusiness == null) {
			return null;
		}
		
		List<User> users = new ArrayList<User>(usersIds.size());
		for (String userId: usersIds) {
			User user = null;
			try {
				user = userBusiness.getUser(Integer.valueOf(userId));
			} catch(Exception e) {
				LOGGER.log(Level.WARNING, "Error getting user by id: " + userId);
			}
			if (user != null) {
				users.add(user);
			}
		}
		
		return users;
	}
	
	public abstract String getUriToAttachment(FileDownloadNotificationProperties properties, User user);

	public abstract Map<String, String> getUriToDocument(FileDownloadNotificationProperties properties, List<User> users);
	
	protected ICFile getFile(FileDownloadNotificationProperties properties) {
		return getFile(properties.getFile());
	}
}
