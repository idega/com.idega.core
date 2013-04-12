package com.idega.util;

import java.io.File;
import java.util.Collections;
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
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailConstants;
import org.apache.commons.mail.SimpleEmail;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.core.file.util.MimeTypeUtil;
import com.idega.core.messaging.MessagingSettings;
import com.idega.core.messaging.SMTPAuthenticator;
import com.idega.idegaweb.DefaultIWBundle;
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

	public static final String	HEADER_AUTO_SUBMITTED = "Auto-Submitted",
								HEADER_PRECEDENCE = "Precedence";

	private static final Logger LOGGER = Logger.getLogger(SendMail.class.getName());

	private SendMail() {}

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
		send(from, to, cc, bcc, replyTo, host, subject, text, mailType, false, false, attachedFiles);
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
	 * @param mailType
	 * @param deleteFiles
	 * @param attachedFiles
	 * @throws MessagingException
	 */
	public static void send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text, String mailType,
			boolean deleteFiles, boolean simpleMessage, File... attachedFiles) throws MessagingException {
		send(from, to, cc, bcc, replyTo, host, subject, text, mailType, null, false, deleteFiles, simpleMessage, attachedFiles);
	}

	public static boolean sendSimpleMail(String from, String to, String subject, String message) {
		return sendSimpleMail(from, to, null, subject, message);
	}

	public static boolean sendSimpleMail(String from, String to, String replyTo, String subject, String message) {
		try {
			Email email = new SimpleEmail();
			email.setCharset(EmailConstants.UTF_8);
			email.setHostName(getHost());
			int port = 465;
			String portValue = getPort();
			if (!StringUtil.isEmpty(portValue))
				port = Integer.valueOf(portValue);
			email.setSmtpPort(port);
			email.setAuthenticator(new DefaultAuthenticator(getUsername(), getPassword()));
			email.setSSLOnConnect(true);
			email.setFrom(from);
			email.setSubject(subject);
			email.setMsg(message);
			email.addTo(to);
			if (!StringUtil.isEmpty(replyTo))
				email.addReplyTo(replyTo);
			email.send();

			return true;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error sending mail to " + to + " from " + from + ". Subject: " + subject + ", message: " + message, e);
		}
		return false;
	}

	private static String getUsername() {
		return IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty(MessagingSettings.PROP_SYSTEM_SMTP_USER_NAME, "idega");
	}

	private static String getPassword() {
		return IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty(MessagingSettings.PROP_SYSTEM_SMTP_PASSWORD, "d2B]kc3CVpdmgp");
	}

	private static String getPort() {
		return IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty(MessagingSettings.PROP_SYSTEM_SMTP_PORT, CoreConstants.EMPTY);
	}

	private static String getHost() {
		return IWMainApplication.getDefaultIWMainApplication().getSettings()
				.getProperty(MessagingSettings.PROP_SYSTEM_SMTP_MAILSERVER, "smtp.sendgrid.net");
	}

	public static Message send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text, String mailType,
			List<AdvancedProperty> headers, final boolean useThread, final boolean deleteFiles, final File... attachedFiles)
		throws MessagingException {
		return send(from, to, cc, bcc, replyTo, host, subject, text, mailType, headers, useThread, deleteFiles, false, attachedFiles);
	}

	/**
	 * <p>
	 * Method that uses the Java Mail API to send an email message.<br/> It is
	 * recommended to use the <type>com.idega.core.messaging.EmailMessage</type>
	 * class rather than calling this method directly.
	 * </p>
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
			List<AdvancedProperty> headers, final boolean useThread, final boolean deleteFiles, boolean simpleMessage, final File... attachedFiles)
		throws MessagingException {

		if (simpleMessage) {
			sendSimpleMail(from, to, subject, text);
			return null;
		}

		if (!DefaultIWBundle.isProductionEnvironment()) {
			LOGGER.log(Level.INFO, "to: " + to + " mail: " + text);
			return null;
		}

		// Charset usually either "UTF-8" or "ISO-8859-1". If not set the system default set is taken
		IWMainApplicationSettings settings = IWMainApplication.getDefaultIWApplicationContext().getApplicationSettings();
		String charset = settings.getCharSetForSendMail();
		boolean useSmtpAuthentication = settings.getBoolean(MessagingSettings.PROP_SYSTEM_SMTP_USE_AUTHENTICATION, Boolean.TRUE);
		boolean useSSL = settings.getBoolean(MessagingSettings.PROP_SYSTEM_SMTP_USE_SSL, Boolean.TRUE);
		String username = getUsername();
		String password = getPassword();
		String port = getPort();
		if (StringUtil.isEmpty(host)) {
			host = getHost();
			if (StringUtil.isEmpty(host))
				throw new MessagingException("Mail server is not configured!");
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
		if (!addRecipients(message, Message.RecipientType.TO, to)) {
			LOGGER.warning("Unable to send email to " + to);
			return null;
		}
		addRecipients(message, Message.RecipientType.CC, cc);
		addRecipients(message, Message.RecipientType.BCC, bcc);

		//	Reply-to
		if (!StringUtil.isEmpty(replyTo))
			message.setReplyTo(InternetAddress.parse(replyTo));

		//	Subject
		message.setSubject(subject, charset);

		//	Attachments
		if (ArrayUtil.isEmpty(attachedFiles)) {
			setMessageContent(message, text, mailType, charset);
		} else {
			MimeBodyPart body = new MimeBodyPart();
			setMessageContent(body, text, mailType, charset);

			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(body);

			for (File attachedFile: attachedFiles) {
				if (attachedFile == null)
					continue;
				if (!attachedFile.exists()) {
					LOGGER.warning("File '" + attachedFile + "' does not exist!");
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
			for (AdvancedProperty header: headers) {
				message.addHeader(header.getId(), header.getValue());
			}
		}

		// Send the message and close the connection
		final Message mail = message;
		Thread transporter = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Transport.send(mail);
				} catch (Exception e) {
					StringBuilder filesNames = new StringBuilder();
					if (!ArrayUtil.isEmpty(attachedFiles)) {
						for (File attachment: attachedFiles) {
							if (attachment == null)
								continue;

							filesNames.append(attachment.getName()).append(CoreConstants.COMMA).append(CoreConstants.SPACE);
						}
					}
					LOGGER.log(Level.WARNING, "Error sending mail " + mail + " - Attachments: '" + filesNames.toString() + "': " + e.getMessage());
				} finally {
					if (deleteFiles && !ArrayUtil.isEmpty(attachedFiles)) {
						for (File attachment: attachedFiles) {
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

	private static boolean doValidateAddresses(String addresses) {
		if (StringUtil.isEmpty(addresses))
			return false;

		String[] emails = addresses.split(CoreConstants.COMMA);
		if (ArrayUtil.isEmpty(emails))
			return false;

		for (String email: emails) {
			if (!EmailValidator.getInstance().validateEmail(email))
				return false;
		}

		return true;
	}

	private static boolean addRecipients(MimeMessage message, RecipientType recipientType, String addresses) throws MessagingException {
		if (StringUtil.isEmpty(addresses))
			return false;

		addresses = StringHandler.replace(addresses, CoreConstants.SEMICOLON, CoreConstants.COMMA);
		if (!doValidateAddresses(addresses)) {
			LOGGER.warning("Adrresses " + addresses + " are invalid!");
			return false;
		}

		message.addRecipients(recipientType, InternetAddress.parse(addresses));
		return true;
	}

	public static void send(String from, String to, String cc, String bcc, String host, String subject, String text, File attachedFile)
			throws MessagingException {
		send(from, to, cc, bcc, host, subject, text, false, false, attachedFile);
	}

	public static void send(String from, String to, String cc, String bcc, String host, String subject, String text, boolean useThread,
			boolean deleteFile, File attachedFile) throws MessagingException {
		send(from, to, cc, bcc, null, host, subject, text, useThread, deleteFile, attachedFile);
	}

	public static void send(SendMailMessageValue mv) throws MessagingException {
		File attachment = mv.getAttachedFile();
		send(mv.getFrom(), mv.getTo(), mv.getCc(), mv.getBcc(), mv.getReplyTo(), mv.getHost(), mv.getSubject(), mv.getText(), mv.getHeaders(), false,
				true, attachment);
	}

	public static Message send(String from, String to, String cc, String bcc, String host, String subject, String text) throws MessagingException {
		return send(from, to, cc, bcc, null, host, subject, text);
	}

	public static Message send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text)
			throws MessagingException {
		return send(from, to, cc, bcc, replyTo, host, subject, text, true, false, new File[] {});
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
	public static Message send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text,
			boolean useThread, boolean deleteFiles, File... attachedFiles) throws MessagingException {
		List<AdvancedProperty> headers = Collections.emptyList();
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
	public static Message send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text,
			List<AdvancedProperty> headers, boolean useThread, boolean deleteFiles, File... attachedFiles) throws MessagingException {
		return send(from, to, cc, bcc, replyTo, host, subject, text, MimeTypeUtil.MIME_TYPE_TEXT_PLAIN, headers, useThread, deleteFiles, false,
				attachedFiles);
	}
}