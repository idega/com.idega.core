package com.idega.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.idega.builder.bean.AdvancedProperty;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 * 
 *          Last modified: $Date: 2008/10/13 08:39:37 $ by $Author: civilis $
 */
public class SendMailMessageValue {

	private String from;
	private String to;
	private String cc;
	private String bcc;
	private String host;
	private String subject;
	private String text;
	private String replyTo;
	private File attachedFile;
	private List<AdvancedProperty> headers;

	public SendMailMessageValue(File attachedFile, String bcc, String cc, String from, String host, String subject, String text, String to,	String replyTo) {
		this();
		
		this.attachedFile = attachedFile;
		this.bcc = bcc;
		this.cc = cc;
		this.from = from;
		this.host = host;
		this.subject = subject;
		this.text = text;
		this.to = to;
	}

	public SendMailMessageValue() {
		super();
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public File getAttachedFile() {
		return attachedFile;
	}

	public void setAttachedFile(File attachedFile) {
		this.attachedFile = attachedFile;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public List<AdvancedProperty> getHeaders() {
		return headers;
	}

	public void setHeaders(List<AdvancedProperty> headers) {
		this.headers = headers;
	}
	
	public void addHeader(String name, String value) {
		if (headers == null)
			headers = new ArrayList<AdvancedProperty>();
		
		headers.add(new AdvancedProperty(name, value));
	}
	
	@Override
	public String toString() {
		return "From: " + getFrom() + ", to: " + getTo() + ", subject: " + getSubject() + ", message: " + getText() + ", attachment(s): " + getAttachedFile();
	}
}