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
public class SendMail {
	public SendMail() {
	}
	public static void send(String from, String to, String cc, String bcc, String host, String subject, String text, File attachedFile)
		throws MessagingException {
		// Start a session
		java.util.Properties properties = System.getProperties();
		Session session = Session.getInstance(properties, null);
		// Construct a message
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		to = to.replace(';', ',');
		message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		//process cc and bcc
		//this Address[] ccAddressess = InternetAddress.parse(cc); or similar
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
			message.setSubject(parseCharacters(subject));
		
			message.setText(parseCharacters(text));
		
		*/
		message.setSubject((subject));
		if (attachedFile == null) {
			message.setText((text));
		}
		else {
			BodyPart body = new MimeBodyPart();
			body.setText(text);
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
	
	/*private static String parseCharacters(String input){
	
	  StringBuffer returnBuffer = new StringBuffer();
	
	  char[] charArray = input.toCharArray();
	
	  for (int i = 0; i < charArray.length; i++) {
	
	    char ch = charArray[i];
	
	    char newChar = ch;
	
	    switch (ch) {
	
	      case 'a':
	
	        returnBuffer.append('a');
	
	      case 'b':
	
	        returnBuffer.append('b');
	
	      default:
	
	        returnBuffer.append(ch);
	
	    }
	
	  }
	
	  return returnBuffer.toString();
	
	}*/
	private static String parseCharacters(String input) {
		return convertStringBody(input);
	}
	/**
	 * From 2M Business Applications DK
	 */
	private static String convertStringBody(String tekst) {
		while (tekst.indexOf("�") > -1) {
			tekst = replace(tekst, "�", "&AElig;");
		}
		while (tekst.indexOf("�") > -1) {
			tekst = replace(tekst, "�", "&aelig;");
		}
		while (tekst.indexOf(System.getProperty("line.separator")) > -1) {
			tekst = replace(tekst, System.getProperty("line.separator"), "<br>");
		}
		return tekst;
	}
	private static String replace(String tekst, String changeFrom, String changeTo) {
		String tekst1 = tekst.substring(0, tekst.indexOf(changeFrom));
		String tekst2 = tekst.substring(tekst.indexOf(changeFrom) + 1);
		return tekst1 + changeTo + tekst2;
	}
}
