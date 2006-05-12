package com.idega.util;
import java.io.File;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import com.idega.idegaweb.IWMainApplication;
public class SendMail {
	public SendMail() {
	}
	public static void send(String from, String to, String cc, String bcc, String host, String subject, String text, File attachedFile)
		throws MessagingException {
		// charset usually either "UTF-8" or "ISO-8859-1"
		// if not set the system default set is taken 
		String charset = IWMainApplication.getDefaultIWApplicationContext().getApplicationSettings().getCharSetForSendMail();
		// Start a session
		java.util.Properties properties = System.getProperties();
		Session session = Session.getInstance(properties, null);
		// Construct a message
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		//process cc and bcc
		//this Address[] ccAddressess = InternetAddress.parse(cc); or similar
		if ((cc != null) && !("".equals(cc))) {
			message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
		}
		if ((bcc != null) && !("".equals(bcc))) {
			message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
		}
		/** @todo tryggvi laga */
		/*
			message.setSubject(parseCharacters(subject));
		
			message.setText(parseCharacters(text));
		
		*/
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
		//Connect to the transport
		Transport transport = session.getTransport("smtp");
		transport.connect(host, "", "");
		//Send the message and close the connection
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}
	
	public static void send(String from, String to, String cc, String bcc, String host, String subject, String text)
			throws MessagingException {
		send(from, to, cc, bcc, host, subject, text, null);
	}
}