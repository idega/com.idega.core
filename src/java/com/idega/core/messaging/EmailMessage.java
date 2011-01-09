package com.idega.core.messaging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.mail.MessagingException;

import com.idega.core.file.util.MimeTypeUtil;
import com.idega.util.ArrayUtil;
import com.idega.util.FileUtil;
import com.idega.util.IOUtil;
import com.idega.util.SendMail;

/**
 * <p>
 * Convenient and simple holder object to send an E-mail Message.
 * </p>
 *  Last modified: $Date: 2009/04/22 12:51:54 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class EmailMessage extends SimpleMessage {
	
	private String senderName;
	
	private String toAddress;
	private String fromAddress;
	private String replyToAddress;
	private String forcedToAddress;
	private String ccAddress;
	private String bccAddress;
	private String mailServer;
	
	private Collection<File> attachedFiles;
	
	private boolean autoDeletedAttachments = true,
					parsed;
	
	private String mailType = MimeTypeUtil.MIME_TYPE_TEXT_PLAIN;
	
	public EmailMessage() {
		super();
		
		MessagingSettings settings = getMessagingSettings();
		setMailServer(settings.getSMTPMailServer());
		setFromAddress(settings.getFromMailAddress());
		setForcedToAddress(settings.getForcedReceiver());
	}

	public EmailMessage(String subject, String body) {
		super(subject, body);
	}

	public EmailMessage(String subject, String body, String toAddress) {
		this(subject, body);
		setToAddress(toAddress);
	}
	
	protected EmailMessage(EmailMessage message) {
		this(message.getSubject(), message.getBody(), message.getToAddress());
		
		setSenderName(message.getSenderName());
		setFromAddress(message.getFromAddress());
		setReplyToAddress(message.getReplyToAddress());
		setForcedToAddress(message.getForcedToAddress());
		setCcAddress(message.getCcAddress());
		setBccAddress(message.getBccAddress());
		setMailServer(message.getMailServer());
		
		Collection<File> attachedFiles = getAttachedFiles();
		if (attachedFiles != null) {
			setAttachedFiles(new ArrayList<File>(attachedFiles));
		}
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

	public void addAttachment(File attachment) {
		if (attachedFiles == null) {
			attachedFiles = new ArrayList<File>();
		}
		attachedFiles.add(attachment);
	}
	
	public Collection<File> getAttachedFiles() {
		return attachedFiles;
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
	
	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	/**
	 * <p>
	 * Sends the email to the receiver.
	 * This checks if emailing is globally switched on or off and throws an exception if that's the case.
	 * </p>
	 * @throws MessagingException
	 */
	@Override
	public void send() throws MessagingException{
		MessagingSettings settings = getMessagingSettings();
		if (!settings.isEmailingEnabled()) {
			throw new MessagingException("E-mailing functionality is disabled globally");
		}
		
		File[] attachments = ArrayUtil.convertListToArray(getAttachedFiles());
		try {
			SendMail.send(getFromAddress(), getToAddress(), getCcAddress(), getBccAddress(), getReplyToAddress(), getMailServer(), getSubject(), getBody(),
					getMailType(), attachments);
		} catch (MessagingException e){
			throw e; //fix
		}
		finally {
			if (isAutoDeletedAttachments() & !ArrayUtil.isEmpty(attachments)) {
				for (File attachment: attachments) {
					if (attachment == null) {
						continue;
					}
					FileUtil.delete(attachment);
				}
			}
		}
	}

	public void setAttachments(Map<String, InputStream> attachments) {
		if (attachments == null || attachments.isEmpty()) {
			return;
		}
		
		for (String name: attachments.keySet()) {
			File attachment = null;
			InputStream stream = attachments.get(name);
			try {
				attachment = FileUtil.getFileAndCreateIfNotExists(name);
				FileUtil.streamToFile(stream, attachment);
				addAttachment(attachment);
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				IOUtil.closeInputStream(stream);
			}
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new EmailMessage(this);
	}

	@Override
	public String toString() {
		return new StringBuilder(getSubject()).append(": ").append(getBody()).append("; to: ").append(getToAddress()).append("; from: ").append(getFromAddress())
		.toString();
	}

	public void setAttachedFiles(Collection<File> attachedFiles) {
		this.attachedFiles = attachedFiles;
	}

	public boolean isAutoDeletedAttachments() {
		return autoDeletedAttachments;
	}

	public void setAutoDeletedAttachments(boolean autoDeletedAttachments) {
		this.autoDeletedAttachments = autoDeletedAttachments;
	}

	public String getMailType() {
		return mailType;
	}

	public void setMailType(String mailType) {
		this.mailType = mailType;
	}

	public boolean isParsed() {
		return parsed;
	}

	public void setParsed(boolean parsed) {
		this.parsed = parsed;
	}
}