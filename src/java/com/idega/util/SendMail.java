package com.idega.util;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import com.idega.core.file.util.MimeTypeUtil;
import com.idega.core.messaging.MessagingSettings;
import com.idega.core.messaging.SMTPAuthenticator;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;

/**
 * <p>
 * Utility class to send Emails with the Java Mail API.
 * </p>
 * Last modified: $Date: 2009/06/18 15:57:43 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.20 $
 */
public class SendMail {

	private static final Logger LOGGER = Logger.getLogger(SendMail.class.getName());
	
	public SendMail() {
	}

	/**
	 * <p>
	 * Method that uses the Java Mail API to send an email message.<br/> It is
	 * recommended to use the <type>com.idega.core.messaging.EmailMessage</type>
	 * class rather than calling this method directly.
	 * </p>
	 * 
	 * @param from
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param replyTo
	 * @param host
	 * @param subject
	 * @param text
	 * @param mailType: plain text, HTML etc.
	 * @param attachedFiles
	 * @throws MessagingException
	 */	
	public static void send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text, String mailType,
			File... attachedFiles) throws MessagingException {

		// Charset usually either "UTF-8" or "ISO-8859-1". If not set the system default set is taken
		IWMainApplicationSettings settings = IWMainApplication.getDefaultIWApplicationContext().getApplicationSettings();
		String charset = settings.getCharSetForSendMail();
		boolean useSmtpAuthentication = settings.getBoolean(MessagingSettings.PROP_SYSTEM_SMTP_USE_AUTHENTICATION, Boolean.FALSE);
		String username = settings.getProperty(MessagingSettings.PROP_SYSTEM_SMTP_USER_NAME, CoreConstants.EMPTY);
		String password = settings.getProperty(MessagingSettings.PROP_SYSTEM_SMTP_PASSWORD, CoreConstants.EMPTY);
		if (StringUtil.isEmpty(host)) {
			host = settings.getProperty(MessagingSettings.PROP_SYSTEM_SMTP_MAILSERVER);
			if(StringUtil.isEmpty(host)){
				throw new MessagingException("Mail server is not configured.");
			}
		}
		
		if (StringUtil.isEmpty(username)) {
			useSmtpAuthentication = false;
		}
		
		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", host);

		// Start a session
		Session session;

		if (useSmtpAuthentication) {
			props.put("mail.smtp.auth", Boolean.TRUE.toString());
			Authenticator auth = new SMTPAuthenticator(username, password);
			session = Session.getInstance(props, auth);
		}
		else {
			session = Session.getInstance(props, null);
		}

		// Set debug if needed
		session.setDebug(settings.isDebugActive());

		// Construct a message
		if(StringUtil.isEmpty(from)){
			throw new MessagingException("From address is null.");
		}
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		
		// Process to, cc and bcc
		addRecipients(message, Message.RecipientType.TO, to);
		addRecipients(message, Message.RecipientType.CC, cc);
		addRecipients(message, Message.RecipientType.BCC, bcc);
		
		if (!StringUtil.isEmpty(replyTo)) {
			message.setReplyTo(InternetAddress.parse(replyTo));
		}

		message.setSubject(subject, charset);

		if (ArrayUtil.isEmpty(attachedFiles)) {
			setMessageContent(message, text, mailType, charset);
		}
		else {
			MimeBodyPart body = new MimeBodyPart();
			setMessageContent(body, text, mailType, charset);
			
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(body);
			
			for (File attachedFile: attachedFiles) {
				if (attachedFile == null) {
					continue;
				}
				
				BodyPart attachment = new MimeBodyPart();
				DataSource attachmentSource = new FileDataSource(attachedFile);
				DataHandler attachmentHandler = new DataHandler(attachmentSource);
				attachment.setDataHandler(attachmentHandler);
				attachment.setFileName(attachedFile.getName());
				attachment.setDescription("Attached file: " + attachment.getFileName());
				LOGGER.info("Adding attachment " + attachment);
				multipart.addBodyPart(attachment);
			}
			
			message.setContent(multipart);
		}

		// Send the message and close the connection
		Transport.send(message);
	}
		
	private static void setMessageContent(MimePart message, String content, String mailType, String charset) throws MessagingException {
		boolean htmlMail = MimeTypeUtil.MIME_TYPE_HTML.equals(mailType);
		if (htmlMail) {
			message.setText(content, charset, "html");
		} else {
			message.setText(content, charset);
		}
	}
	
	private static void addRecipients(MimeMessage message, RecipientType recipientType, String addresses) throws MessagingException {
		if (StringUtil.isEmpty(addresses)) {
			return;
		}
		
		addresses = addresses.replace(CoreConstants.SEMICOLON, CoreConstants.COMMA);
		message.addRecipients(recipientType, InternetAddress.parse(addresses));
	}

	public static void send(String from, String to, String cc, String bcc, String host, String subject, String text, File attachedFile) throws MessagingException {
		send(from, to, cc, bcc, null, host, subject, text, attachedFile);
	}
	
	public static void send(SendMailMessageValue mv) throws MessagingException {
		send(mv.getFrom(), mv.getTo(), mv.getCc(), mv.getBcc(), mv.getReplyTo(), mv.getHost(), mv.getSubject(), mv.getText(),
				mv.getAttachedFile() == null ? null : mv.getAttachedFile());
	}

	public static void send(String from, String to, String cc, String bcc, String host, String subject, String text) throws MessagingException {
		send(from, to, cc, bcc, null, host, subject, text);
	}

	public static void send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text) throws MessagingException {
		send(from, to, cc, bcc, replyTo, host, subject, text, new File[] {});
	}

	public static void send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text, File... attachedFiles)
		throws MessagingException {
		send(from, to, cc, bcc, replyTo, host, subject, text, MimeTypeUtil.MIME_TYPE_TEXT_PLAIN, attachedFiles);
	}
}