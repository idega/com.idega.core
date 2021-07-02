package com.idega.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
import com.idega.util.mail.DummyMessage;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.sun.mail.smtp.SMTPTransport;

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
	public static Message send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text, String mailType,
			File... attachedFiles) throws MessagingException {
		return send(from, to, cc, bcc, replyTo, host, subject, text, mailType, false, false, attachedFiles);
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
	public static Message send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text, String mailType,
			boolean deleteFiles, boolean simpleMessage, File... attachedFiles) throws MessagingException {
		return send(from, to, cc, bcc, replyTo, host, subject, text, mailType, null, false, deleteFiles, simpleMessage, attachedFiles);
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
			if (!StringUtil.isEmpty(portValue)) {
				port = Integer.valueOf(portValue);
			}
			email.setSmtpPort(port);
			email.setAuthenticator(new DefaultAuthenticator(getUsername(), getPassword()));
			email.setSSLOnConnect(true);
			email.setFrom(from);
			email.setSubject(subject);
			email.setMsg(message);
			email.addTo(to);
			if (!StringUtil.isEmpty(replyTo)) {
				email.addReplyTo(replyTo);
			}
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

	private static boolean sendViaSendGridAPI(
			String key,
			String from,
			String to,
			String cc,
			String bcc,
			String replyTo,
			String subject,
			String text,
			List<AdvancedProperty> headers,
			final boolean deleteFiles,
			final File... attachedFiles
	) throws MessagingException {
		//	From, to, content
		com.sendgrid.helpers.mail.objects.Email fromEmail = new com.sendgrid.helpers.mail.objects.Email(from);
		com.sendgrid.helpers.mail.objects.Email toEmail = new com.sendgrid.helpers.mail.objects.Email(to);
	    text = StringUtil.isEmpty(text) ? subject : text;
		Content content = new Content(MimeTypeUtil.MIME_TYPE_HTML, text);
	    Mail mail = new Mail(fromEmail, subject, toEmail, content);

	    EmailValidator validator = EmailValidator.getInstance();

	    List<Personalization> personalizations = mail.getPersonalization();
	    Personalization personalization = ListUtil.isEmpty(personalizations) ? null : personalizations.iterator().next();
	    boolean newPersonalization = personalization == null;

	    //	CC and BCC
	    if (validator.isValid(cc) && !cc.equalsIgnoreCase(to)) {
	    	personalization = newPersonalization ? new Personalization() : personalization;
	    	personalization.addCc(new com.sendgrid.helpers.mail.objects.Email(cc));
	    	if (newPersonalization) {
	    		mail.addPersonalization(personalization);
	    	}
		}
		if (validator.isValid(bcc) && !bcc.equalsIgnoreCase(cc)) {
			personalization = newPersonalization ? new Personalization() : personalization;
	    	personalization.addBcc(new com.sendgrid.helpers.mail.objects.Email(bcc));
	    	if (newPersonalization) {
	    		mail.addPersonalization(personalization);
	    	}
		}

		//	Reply to
		if (!StringUtil.isEmpty(replyTo)) {
			com.sendgrid.helpers.mail.objects.Email replyToEmail = new com.sendgrid.helpers.mail.objects.Email(replyTo);
			mail.setReplyTo(replyToEmail);
		}

		//	Headers
		if (!ListUtil.isEmpty(headers)) {
			for (AdvancedProperty header: headers) {
				mail.addHeader(header.getId(), header.getValue());
			}
		}

		//	Attachments
		if (!ArrayUtil.isEmpty(attachedFiles)) {
			for (File attachment: attachedFiles) {
				if (attachment == null || !attachment.exists() || !attachment.canRead()) {
					LOGGER.warning("File '" + attachment + "' does not exist!");
					continue;
				}

				InputStream contentStream = null;
				try {
					contentStream = new FileInputStream(attachment);
					String type = Files.probeContentType(attachment.toPath());
					Attachments attachments = new Attachments.Builder(attachment.getName(), contentStream)
			                .withType(type)
			                .build();
					mail.addAttachments(attachments);
				} catch (IOException e) {
					LOGGER.log(Level.WARNING, "Error adding attachment " + attachment + ". Can not send email to " + to + " with subject " + subject, e);
					return false;
				} finally {
					IOUtil.close(contentStream);
				}
			}
		}

		boolean success = false;
	    SendGrid sg = new SendGrid(key);
	    Request request = new Request();
	    try {
	    	request.setMethod(Method.POST);
	    	request.setEndpoint("mail/send");
	    	request.setBody(mail.build());
	    	Response response = sg.api(request);
	    	int statusCode = response.getStatusCode();
	    	success = statusCode == 200 || statusCode == 201 || statusCode == 202;
	    	if (!success) {
	    		LOGGER.warning("Error sending email to " + to + " with subject " + subject + ". Status code: " + statusCode + ", response: " + response.getBody() + ", response headers: " + response.getHeaders());
	    	}
	    	return success;
	    } catch (Exception e) {
	    	String error = "Error sending email to " + to + " with subject " + subject;
	    	LOGGER.log(Level.WARNING, error, e);
	    	throw new MessagingException(error, e);
	    } finally {
	    	if (success && deleteFiles && !ArrayUtil.isEmpty(attachedFiles)) {
	    		for (File attachment: attachedFiles) {
					if (attachment != null && attachment.exists()) {
						attachment.delete();
					}
	    		}
	    	}
	    }
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
	public synchronized static Message send(
			String from,
			String to,
			String cc,
			String bcc,
			String replyTo,
			String host,
			String subject,
			String text,
			String mailType,
			List<AdvancedProperty> headers,
			final boolean useThread,
			final boolean deleteFiles,
			boolean simpleMessage,
			final File... attachedFiles
	) throws MessagingException {

		IWMainApplicationSettings settings = IWMainApplication.getDefaultIWMainApplication().getSettings();
		String customTo = settings.getProperty("custom_mail_receiver");
		if (EmailValidator.getInstance().isValid(customTo)) {
			LOGGER.info("Using " + customTo + " instead of " + to);
			to = customTo;
		}

		String alwaysBCC = settings.getProperty("custom_mail_bcc_receiver");
		if (EmailValidator.getInstance().isValid(alwaysBCC) && to != null && !to.equals(settings.getProperty("exception_report_receiver", "abuse@idega.com"))) {
			bcc = EmailValidator.getInstance().isValid(bcc) ? bcc.concat(CoreConstants.COMMA).concat(alwaysBCC) : alwaysBCC;
		}

		if (StringUtil.isEmpty(to)) {
			LOGGER.warning("Unknown receiver. Can not send message with subject " + subject);
			return null;
		}

		if (settings.getBoolean("always_use_html_mail", false) || StringHandler.isHTML(text)) {
			mailType = MimeTypeUtil.MIME_TYPE_HTML;
		}

		if (simpleMessage) {
			sendSimpleMail(from, to, subject, text);
			return null;
		}

		boolean viaSendGrindAPI = settings.getBoolean("mail.send_via_api", false);
		String sendGridKey = viaSendGrindAPI ? settings.getProperty("mail.send_grid_key") : null;
		if (viaSendGrindAPI && !StringUtil.isEmpty(sendGridKey)) {
			boolean success = sendViaSendGridAPI(sendGridKey, from, to, cc, bcc, replyTo, subject, text, headers, deleteFiles, attachedFiles);
			return success ? new DummyMessage() : null;
		}

		if (!DefaultIWBundle.isProductionEnvironment()) {
			LOGGER.log(Level.INFO, "to: " + to + " mail: " + text);
			return null;
		}

		// Charset usually either "UTF-8" or "ISO-8859-1". If not set the system default set is taken
		String charset = settings.getCharSetForSendMail();
		boolean useSmtpAuthentication = settings.getBoolean(MessagingSettings.PROP_SYSTEM_SMTP_USE_AUTHENTICATION, Boolean.TRUE);
		boolean useSSL = settings.getBoolean(MessagingSettings.PROP_SYSTEM_SMTP_USE_SSL, Boolean.TRUE);
		String username = getUsername();
		String password = getPassword();
		String port = getPort();
		if (StringUtil.isEmpty(host)) {
			host = getHost();
			if (StringUtil.isEmpty(host)) {
				throw new MessagingException("Mail server is not configured!");
			}
		}

		if (StringUtil.isEmpty(username)) {
			useSmtpAuthentication = false;
		}

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", host);

		// Set the smtp server port
		if (!StringUtil.isEmpty(port)) {
			props.put("mail.smtp.port", port);
		}

		boolean tlsOn = settings.getBoolean(MessagingSettings.PROP_SYSTEM_SMTP_USE_TLS, Boolean.FALSE);
		if (tlsOn) {
			props.put("mail.smtp.starttls.enable", true);
		}

		// Start a session
		Session session;
		if (useSmtpAuthentication) {
			props.put("mail.smtp.auth", Boolean.TRUE.toString());
			Authenticator auth = new SMTPAuthenticator(username, password);

			if (useSSL) {
				props.put("mail.smtp.ssl.enable", Boolean.TRUE.toString());
			}

			session = Session.getInstance(props, auth);
		} else {
			session = Session.getInstance(props, null);
		}

		// Set debug if needed
		session.setDebug(settings.isDebugActive());

		// Construct a message
		//	Sender
		if (StringUtil.isEmpty(from)) {
			from = settings.getProperty(MessagingSettings.PROP_MESSAGEBOX_FROM_ADDRESS);
		}
		if (StringUtil.isEmpty(from)) {
			throw new MessagingException("From address is null.");
		}
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
		if (!StringUtil.isEmpty(replyTo)) {
			message.setReplyTo(InternetAddress.parse(replyTo));
		}

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
				if (attachedFile == null) {
					continue;
				}
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

				try {
					String contentType = Files.probeContentType(attachedFile.toPath());
					if (!StringUtil.isEmpty(contentType)) {
						attachment.setHeader("Content-Type", contentType);
					}
				} catch (Exception e) {}

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
					SMTPTransport.send(mail);
				} catch (Exception e) {
					StringBuilder filesNames = new StringBuilder();
					if (!ArrayUtil.isEmpty(attachedFiles)) {
						for (File attachment: attachedFiles) {
							if (attachment == null) {
								continue;
							}

							filesNames.append(attachment.getName()).append(CoreConstants.COMMA).append(CoreConstants.SPACE);
						}
					}
					LOGGER.log(Level.WARNING, "Error sending mail " + mail + " - Attachments: '" + filesNames.toString() + "': " + e.getMessage());
				} finally {
					if (deleteFiles && !ArrayUtil.isEmpty(attachedFiles)) {
						for (File attachment: attachedFiles) {
							if (attachment != null && attachment.exists()) {
								attachment.delete();
							}
						}
					}
				}
			}
		});
		if (useThread) {
			transporter.start();
		} else {
			transporter.run();
		}

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
		if (StringUtil.isEmpty(addresses)) {
			return false;
		}

		String[] emails = addresses.split(CoreConstants.COMMA);
		if (ArrayUtil.isEmpty(emails)) {
			return false;
		}

		for (String email: emails) {
			if (StringUtil.isEmpty(email)) {
				continue;
			}

			email = email.trim();
			if (!EmailValidator.getInstance().validateEmail(email)) {
				LOGGER.warning("Email '" + email + "' is invalid");
				return false;
			}
		}

		return true;
	}

	private static boolean addRecipients(MimeMessage message, RecipientType recipientType, String addresses) throws MessagingException {
		if (StringUtil.isEmpty(addresses)) {
			return false;
		}

		addresses = StringHandler.replace(addresses, CoreConstants.SEMICOLON, CoreConstants.COMMA);
		if (!doValidateAddresses(addresses)) {
			LOGGER.warning("Adrress(es) " + addresses + " is/are invalid!");
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
		send(mv.getFrom(), mv.getTo(), mv.getCc(), mv.getBcc(), mv.getReplyTo(), mv.getHost(), mv.getSubject(), mv.getText(), mv.getHeaders(), false, false, attachment);
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
	public static Message send(
			String from,
			String to,
			String cc,
			String bcc,
			String replyTo,
			String host,
			String subject,
			String text,
			List<AdvancedProperty> headers,
			boolean useThread,
			boolean deleteFiles,
			File... attachedFiles
	) throws MessagingException {
		return send(from, to, cc, bcc, replyTo, host, subject, text, MimeTypeUtil.MIME_TYPE_TEXT_PLAIN, headers, useThread, deleteFiles, false, attachedFiles);
	}
}