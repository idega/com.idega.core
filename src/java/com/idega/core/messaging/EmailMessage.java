package com.idega.core.messaging;

import java.io.File;
import javax.mail.MessagingException;
import com.idega.util.SendMail;

/**
 * <p>
 * Convenient and simple holder object to send an E-mail Message.
 * </p>
 *  Last modified: $Date: 2007/06/27 22:19:08 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class EmailMessage extends SimpleMessage {
	
	private String toAddress;
	private String fromAddress;
	private String replyToAddress;
	private String forcedToAddress;
	private String ccAddress;
	private String bccAddress;
	private File attachedFile;
	
	private String mailServer;
	
	public EmailMessage(){
		MessagingSettings settings = getMessagingSettings();
		setMailServer(settings.getSMTPMailServer());
		setFromAddress(settings.getFromMailAddress());
		setForcedToAddress(settings.getForcedReceiver());
	}

	public EmailMessage(String subject,String body){
		this();
		setSubject(subject);
		setBody(body);
	}

	public EmailMessage(String subject,String body,String toAddress){
		this(subject,body);
		setToAddress(toAddress);
	}
	
	public String getFromAddress() {
		return fromAddress;
	}
	
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	
	public String getToAddress() {
		String forcedTo = getForcedToAddress();
		if(forcedTo!=null){
			return forcedTo;
		}
		return toAddress;
	}
	
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	protected String getForcedToAddress() {
		return forcedToAddress;
	}

	
	protected void setForcedToAddress(String forcedToAddress) {
		this.forcedToAddress = forcedToAddress;
	}
	
	public File getAttachedFile() {
		return attachedFile;
	}

	
	public void setAttachedFile(File attachedFile) {
		this.attachedFile = attachedFile;
	}

	
	public String getBccAddress() {
		return bccAddress;
	}

	
	public void setBccAddress(String bccAddress) {
		this.bccAddress = bccAddress;
	}

	
	public String getMailServer() {
		return mailServer;
	}

	
	public void setMailServer(String mailServer) {
		this.mailServer = mailServer;
	}

	
	public String getReplyToAddress() {
		return replyToAddress;
	}

	
	public void setReplyToAddress(String replyToAddress) {
		this.replyToAddress = replyToAddress;
	}

	
	public String getCcAddress() {
		return ccAddress;
	}
	
	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}
	
	/**
	 * <p>
	 * Sends the email to the receiver.
	 * This checks if emailing is globally switched on or off and throws an exception if that's the case.
	 * </p>
	 * @throws MessagingException
	 */
	public void send() throws MessagingException{
		MessagingSettings settings = getMessagingSettings();
		if(settings.isEmailingEnabled()){
			SendMail.send(getFromAddress(), getToAddress(), getCcAddress(), getBccAddress(), getReplyToAddress(), getMailServer(), getSubject(), getBody(),getAttachedFile());
		}
		else{
			throw new MessagingException("E-mailing functionality is disabled globally");
		}
	}

}
