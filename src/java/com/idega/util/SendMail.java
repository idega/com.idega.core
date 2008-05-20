package com.idega.util;

import java.io.File;
import java.util.Properties;

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
import javax.mail.internet.MimeMultipart;

import com.idega.core.messaging.MessagingSettings;
import com.idega.core.messaging.SMTPAuthenticator;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;

/**
 * <p>
 * Utility class to send Emails with the Java Mail API.
 * </p>
 * Last modified: $Date: 2008/05/20 13:35:03 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.10.2.8 $
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
	 * @param attachedFile
	 * @throws MessagingException
	 */
	public static void send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text, File attachedFile) throws MessagingException {

		// charset usually either "UTF-8" or "ISO-8859-1"
		// if not set the system default set is taken
		IWMainApplicationSettings settings = IWMainApplication.getDefaultIWApplicationContext().getApplicationSettings();
		String charset = settings.getCharSetForSendMail();
		boolean useSmtpAuthentication = Boolean.valueOf(settings.getProperty(MessagingSettings.PROP_SYSTEM_SMTP_USE_AUTHENTICATION, "true")).booleanValue();
		String username = settings.getProperty(MessagingSettings.PROP_SYSTEM_SMTP_USER_NAME, "");
		String password = settings.getProperty(MessagingSettings.PROP_SYSTEM_SMTP_PASSWORD, "");
		//Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", host);

		// Start a session
		Session session;

		if (useSmtpAuthentication) {
			props.put("mail.smtp.auth", "true");
			Authenticator auth = new SMTPAuthenticator(username, password);
			session = Session.getInstance(props, auth);
		}
		else {
			session = Session.getInstance(props, null);
		}

		//set debug if needed
		session.setDebug(settings.isDebugActive());

		// Construct a message
		to = to.replace(';', ',');
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		// process cc and bcc
		// this Address[] ccAddressess = InternetAddress.parse(cc); or similar
		if ((cc != null) && !("".equals(cc))) {
			cc = cc.replace(';', ',');
			message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
		}
		if ((bcc != null) && !("".equals(bcc))) {
			bcc = bcc.replace(';', ',');
			message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
		}
		/** @todo tryggvi laga */
		/*
		 * message.setSubject(parseCharacters(subject));
		 * 
		 * message.setText(parseCharacters(text));
		 * 
		 */
		if (replyTo != null && !"".equals(replyTo)) {
			message.setReplyTo(InternetAddress.parse(replyTo));
		}

		//EIKI IS THIS CORRECT? in examples on the net the second parameter is usually "text/plain" a.k.a. a contenttype!?
		message.setSubject(subject, charset);

		if (attachedFile == null) {
			message.setText(text, charset);
		}
		else {
			MimeBodyPart body = new MimeBodyPart();
			body.setText(text, charset);
			BodyPart attachment = new MimeBodyPart();
			DataSource attachmentSource = new FileDataSource(attachedFile);
			DataHandler attachmentHandler = new DataHandler(attachmentSource);
			attachment.setDataHandler(attachmentHandler);
			attachment.setFileName(attachedFile.getName());
			attachment.setDescription("Attached file");
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(body);
			System.out.println("Adding attachment " + attachment);
			multipart.addBodyPart(attachment);
			message.setContent(multipart);
		}

		// Send the message and close the connection
		Transport.send(message);
		//Transport.sendMessage(message, message.getAllRecipients());
		//transport.close();
	}

	public static void send(String from, String to, String cc, String bcc, String host, String subject, String text, File attachedFile) throws MessagingException {
		send(from, to, cc, bcc, null, host, subject, text, attachedFile);
	}

	public static void send(String from, String to, String cc, String bcc, String host, String subject, String text) throws MessagingException {
		send(from, to, cc, bcc, null, host, subject, text, null);
	}

	public static void send(String from, String to, String cc, String bcc, String replyTo, String host, String subject, String text) throws MessagingException {
		send(from, to, cc, bcc, host, replyTo, subject, text, null);
	}

}