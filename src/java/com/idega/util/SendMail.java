package com.idega.util;

import javax.mail.*;
import javax.mail.internet.*;

public class SendMail {



        public SendMail(){

        }


	public static void send(String from, String to, String cc, String bcc,String host, String subject, String text) throws MessagingException {

		// Start a session
		java.util.Properties properties = System.getProperties();
		Session session = Session.getInstance(properties, null);


		// Construct a message
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		//process cc and bcc
		//this Address[] ccAddressess = InternetAddress.parse(cc); or similar

		if((cc!=null) || !(cc.equals(""))){
		message.addRecipients(Message.RecipientType.CC,InternetAddress.parse(cc));
		}
		if((bcc!=null) || !(bcc.equals(""))){
		message.addRecipients(Message.RecipientType.BCC,InternetAddress.parse(bcc));
		}

		message.setSubject(subject);
		message.setText(text);

		//Connect to the transport
		Transport transport = session.getTransport("smtp");
		transport.connect(host, "", "");

		//Send the message and close the connection
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();

	}

}
