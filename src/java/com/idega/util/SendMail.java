package com.idega.util;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
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
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMultipart;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.core.file.util.MimeTypeUtil;
import com.idega.core.messaging.MessagingSettings;
import com.idega.core.messaging.SMTPAuthenticator;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;

/**
 * <p>
 * Utility class to send Emails with the Java Mail API.
 * </p>
 * Last modified: $Date: 2008/06/07 15:47:57 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.10.2.10 $
 */
public class SendMail {

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
	 * @param mailType
	 * @param headers
	 * @param attachedFiles
	 * @throws MessagingException
	 */
	public static Message send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text, String mailType,
			List headers, final boolean useThread, final boolean deleteFiles, final File[] attachedFiles) throws MessagingException {

		// Charset usually either "UTF-8" or "ISO-8859-1". If not set the system default set is taken
		IWMainApplicationSettings settings = IWMainApplication.getDefaultIWApplicationContext().getApplicationSettings();
		String charset = settings.getCharSetForSendMail();
		boolean useSmtpAuthentication = settings.getBoolean(MessagingSettings.PROP_SYSTEM_SMTP_USE_AUTHENTICATION, true);
		boolean useSSL = settings.getBoolean(MessagingSettings.PROP_SYSTEM_SMTP_USE_SSL, false);
		String username = settings.getProperty(MessagingSettings.PROP_SYSTEM_SMTP_USER_NAME, "idegatest@idega.com");
		String password = settings.getProperty(MessagingSettings.PROP_SYSTEM_SMTP_PASSWORD, "pl4tf0rm");
		String port = settings.getProperty(MessagingSettings.PROP_SYSTEM_SMTP_PORT, CoreConstants.EMPTY);
		if (StringUtil.isEmpty(host)) {
			host = settings.getProperty(MessagingSettings.PROP_SYSTEM_SMTP_MAILSERVER, "smtp.emailsrvr.com");
			if (StringUtil.isEmpty(host))
				throw new MessagingException("Mail server is not configured.");
		}

		if (StringUtil.isEmpty(username))
			useSmtpAuthentication = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", host);

		// Set the smtp server port
		if (!StringUtil.isEmpty(port))
			props.put("mail.smtp.port", port);

		// Start a session
		Session session;
		if (useSmtpAuthentication) {
			props.put("mail.smtp.auth", Boolean.TRUE.toString());
			Authenticator auth = new SMTPAuthenticator(username, password);

			if (useSSL)
				props.put("mail.smtp.ssl.enable", Boolean.TRUE.toString());

			session = Session.getInstance(props, auth);
		} else {
			session = Session.getInstance(props, null);
		}

		// Set debug if needed
		session.setDebug(settings.isDebugActive());

		// Construct a message
		//	Sender
		if (StringUtil.isEmpty(from))
			throw new MessagingException("From address is null.");
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));

		// Process to, cc and bcc
		addRecipients(message, Message.RecipientType.TO, to);
		addRecipients(message, Message.RecipientType.CC, cc);
		addRecipients(message, Message.RecipientType.BCC, bcc);

		//	Reply-to
		if (!StringUtil.isEmpty(replyTo))
			message.setReplyTo(InternetAddress.parse(replyTo));

		//	Subject
		message.setSubject(subject, charset);

		//	Attachments
		if ((attachedFiles == null) || (attachedFiles.length == 0)) {
			setMessageContent(message, text, mailType, charset);
		} else {
			MimeBodyPart body = new MimeBodyPart();
			setMessageContent(body, text, mailType, charset);

			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(body);

			for(int i = 0;i < attachedFiles.length;i++){
				File attachedFile = attachedFiles[i];
				if (attachedFile == null)
					continue;
				if (!attachedFile.exists()) {
					Logger.getLogger(SendMail.class.getName()).warning("File '" + attachedFile + "' does not exist!");
					continue;
				}

				BodyPart attachment = new MimeBodyPart();
				DataSource attachmentSource = new FileDataSource(attachedFile);
				DataHandler attachmentHandler = new DataHandler(attachmentSource);
				attachment.setDataHandler(attachmentHandler);
				attachment.setFileName(attachedFile.getName());
				attachment.setDescription("Attached file: " + attachment.getFileName());
				multipart.addBodyPart(attachment);
			}

			message.setContent(multipart);
		}

		//	Headers
		if (!ListUtil.isEmpty(headers)) {
			for(Iterator iter = headers.iterator();iter.hasNext();){
				AdvancedProperty header = (AdvancedProperty) iter.next();
				message.addHeader(header.getId(), header.getValue());
			}
		}

		// Send the message and close the connection
		final Message mail = message;
		Thread transporter = new Thread(new Runnable() {
			public void run() {
				try {
					Transport.send(mail);
				} catch (Exception e) {
					StringBuilder filesNames = new StringBuilder();
					if (attachedFiles != null) {
						for(int i = 0;i < attachedFiles.length;i++){
							File attachment = attachedFiles[i];
							if (attachment == null)
								continue;
							filesNames.append(attachment.getName()).append(CoreConstants.COMMA).append(CoreConstants.SPACE);
						}
					}
					Logger.getLogger(SendMail.class.getName()).log(Level.WARNING,
							"Error sending mail " + mail + ". Attachments: '" + filesNames.toString() + "'", e);
				} finally {
					if (attachedFiles != null) {
						for(int i = 0;i < attachedFiles.length;i++){
							File attachment = attachedFiles[i];
							if (attachment != null && attachment.exists())
								attachment.delete();
						}
					}
				}
			}
		});
		if (useThread)
			transporter.start();
		else
			transporter.run();

		return message;
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
	 * @param attachedFile
	 * @throws MessagingException
	 */
	public static void send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text, File attachedFile) throws MessagingException {
		File[] files = {attachedFile};
		if(attachedFile == null){
			files = null;
		}
		send(from, to, cc, bcc, replyTo, host, subject, text, Collections.EMPTY_LIST, true, false,files);
	}

	/**
	 * Sends email
	 * @param from
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param replyTo
	 * @param host
	 * @param subject
	 * @param text
	 * @param useThread
	 * @param deleteFiles
	 * @param attachedFiles
	 * @return
	 * @throws MessagingException
	 */
	public static Message send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text, boolean useThread, boolean deleteFiles,
			File[] attachedFiles) throws MessagingException {
		List headers = Collections.emptyList();
		return send(from, to, cc, bcc, replyTo, host, subject, text, headers, useThread, deleteFiles, attachedFiles);
	}
	
	/**
	 * Sends email
	 * @param from
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param replyTo
	 * @param host
	 * @param subject
	 * @param text
	 * @param headers
	 * @param useThread
	 * @param deleteFiles
	 * @param attachedFiles
	 * @return
	 * @throws MessagingException
	 */
	public static Message send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text, List headers,
			boolean useThread, boolean deleteFiles, File[] attachedFiles) throws MessagingException {
		return send(from, to, cc, bcc, replyTo, host, subject, text, MimeTypeUtil.MIME_TYPE_TEXT_PLAIN, headers, useThread, deleteFiles, attachedFiles);
	}
	
	public static void send(String from, String to, String cc, String bcc, String host, String subject, String text, File attachedFile) throws MessagingException {
		send(from, to, cc, bcc, null, host, subject, text, attachedFile);
	}

	public static void send(String from, String to, String cc, String bcc, String host, String subject, String text) throws MessagingException {
		send(from, to, cc, bcc, null, host, subject, text, null);
	}

	public static void send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text) throws MessagingException {
		send(from, to, cc, bcc, replyTo, host, subject, text, null);
	}

}