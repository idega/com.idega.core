package com.idega.core.messaging;

import javax.mail.PasswordAuthentication;

/**
* SimpleAuthenticator is used to do simple authentication
* when the SMTP server requires it.
*/
public class SMTPAuthenticator extends javax.mail.Authenticator{
	private String user;
	private String passw;

	public SMTPAuthenticator(String userName, String password){
		this.user = userName;
		this.passw = password;
	}
	
	public PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication(user, passw);
	}
}