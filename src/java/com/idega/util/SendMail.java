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

		message.setSubject(parseCharacters(subject));
		message.setText(parseCharacters(text));

		//Connect to the transport
		Transport transport = session.getTransport("smtp");
		transport.connect(host, "", "");

		//Send the message and close the connection
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();

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

    private static String parseCharacters(String input){
      return convertStringBody(input);
    }



    /**
     * From 2M Business Applications DK
     */
    private static String convertStringBody(String tekst) {

        while (tekst.indexOf("®") > -1) {
            tekst = replace(tekst, "®","&AElig;");
        }

        while (tekst.indexOf("¾") > -1) {
            tekst = replace(tekst, "¾","&aelig;");
        }
        while (tekst.indexOf(System.getProperty("line.separator")) > -1) {
            tekst = replace(tekst,System.getProperty("line.separator"),"<br>");
        }
        return tekst;
    }

    private static String replace(String tekst, String changeFrom, String changeTo) {
        String tekst1 = tekst.substring(0,tekst.indexOf(changeFrom));
        String tekst2 = tekst.substring(tekst.indexOf(changeFrom)+1);
        return tekst1 + changeTo + tekst2;
    }






}
