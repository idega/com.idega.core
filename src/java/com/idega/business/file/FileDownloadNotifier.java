package com.idega.business.file;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
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
		
		String fileId = properties.getFile();
		if (StringUtil.isEmpty(fileId)) {
			LOGGER.warning("File ID is undefined (for ICFile)");
			return result;
		}

		ICFile file = getFile(fileId);
		if (file == null) {
			LOGGER.warning("File was not found by ID: " + fileId);
			return result;
		}
		
		List<String> usersIds = properties.getUsers();
		if (ListUtil.isEmpty(usersIds)) {
			LOGGER.warning("There are no recipients of this notification!");
			return result;
		}
		
		List<AdvancedProperty> emailsAndLinks = getEmails(file, properties);
		if (ListUtil.isEmpty(emailsAndLinks)) {
			LOGGER.warning("Unable to resolve e-mail addresses for recipients: " + usersIds);
			return result;
		}
		
		String subject = CoreConstants.EMPTY;
		try {
			subject = new StringBuilder(iwrb.getLocalizedString("file_download_notifier.reminder_to_download_document", "Reminder to download document"))
				.append(": ").append(URLDecoder.decode(file.getName(), CoreConstants.ENCODING_UTF8)).toString();
		} catch(UnsupportedEncodingException e) {
			LOGGER.log(Level.WARNING, "Error decoding: " + file.getName(), e);
		}
		
		String pageMessage = new StringBuilder(iwrb.getLocalizedString("file_download_notifier.reminder_message_for_document_to_download",
			"Please download document. You can find it: ")).append(properties.getUrl()).append(". ")
			.append(iwrb.getLocalizedString("file_download_notifier.or_directly_download", "Or directly download from: ")).toString();
		
		IWMainApplicationSettings settings = iwc.getApplicationSettings();
		String host = settings.getProperty(CoreConstants.PROP_SYSTEM_SMTP_MAILSERVER);
		String from = settings.getProperty(CoreConstants.PROP_SYSTEM_MAIL_FROM_ADDRESS);
		if (StringUtil.isEmpty(from)) {
			LOGGER.warning("Address 'from' is not defined, unable to send message: " + subject + " to: " + emailsAndLinks);
			return result;
		}
		
		for (AdvancedProperty emailAndLink: emailsAndLinks) {
			if (!sendNotification(emailAndLink.getId(), subject, new StringBuilder(pageMessage).append(emailAndLink.getValue()).toString(), host, from)) {
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
	
	private List<AdvancedProperty> getEmails(ICFile attachment, FileDownloadNotificationProperties properties) {
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
		
		List<AdvancedProperty> emailsAndLinks = new ArrayList<AdvancedProperty>(properties.getUsers().size());
		for (String userId: properties.getUsers()) {
			User user = null;
			Email email = null;
			try {
				user = userBusiness.getUser(Integer.valueOf(userId));
				email = userBusiness.getEmailHome().findMainEmailForUser(user);
			} catch(Exception e) {
				LOGGER.log(Level.WARNING, "Error getting email for user: " + userId, e);
			}
			
			if (user != null && email != null) {
				String uri = getUriToAttachment(properties, user);
				
				if (StringUtil.isEmpty(uri)) {
					LOGGER.warning("Unable to resolve file uri for user: " + user + " and email: " + email + " and file: " + attachment.getId());
				} else {
					emailsAndLinks.add(new AdvancedProperty(email.getEmailAddress(), new StringBuilder(properties.getServer()).append(uri).toString()));
				}
			}
		}
		
		return emailsAndLinks;
	}
	
	public abstract String getUriToAttachment(FileDownloadNotificationProperties properties, User user);

}
