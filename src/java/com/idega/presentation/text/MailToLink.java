/*
 * $Id: MailToLink.java,v 1.1.2.1 2006/12/04 20:17:27 idegaweb Exp $
 * Created on Dec 1, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.text;


public class MailToLink extends Link {
	
	private String PARAM_SUBJECT = "subject";
	private String PARAM_BODY = "body";
	private String PARAM_CC = "cc";
	private String PARAM_BCC = "bcc";
	
	private String recipients;
	private String subject;
	private String body;
	private String cc;
	private String bcc;

	public MailToLink (String text) {
		this(text, "");
	}
	
	public MailToLink (String text, String recipients) {
		this(text, recipients, "");
	}

	public MailToLink (String text, String recipients, String subject) {
		this(text, recipients, subject, "");
	}
	
	public MailToLink (String text, String recipients, String subject, String body) {
		this(text, recipients, subject, body, "");
	}
	
	public MailToLink (String text, String recipients, String subject, String body, String cc) {
		this(text, recipients, subject, body, cc, "");
	}
	
	public MailToLink (String text, String recipients, String subject, String body, String cc, String bcc) {
		super(text);
		setRecipients(recipients);
		setSubject(subject);
		setBody(body);
		setCC(cc);
		setBCC(bcc);
	}
	
	public String getBCC() {
		return bcc;
	}
	
	public void setBCC(String bcc) {
		if (isParameterSet(PARAM_BCC)) {
			removeParameter(PARAM_BCC);
		}
		this.bcc = bcc;
		if (this.bcc != null && !this.bcc.equals("")) {
			addParameter(PARAM_BCC, this.bcc);
		}
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		if (isParameterSet(PARAM_BODY)) {
			removeParameter(PARAM_BODY);
		}
		this.body = body;
		if (this.body != null && !this.body.equals("")) {
			addParameter(PARAM_BODY, this.body);
		}
	}
	
	public String getCC() {
		return cc;
	}
	
	public void setCC(String cc) {
		if (isParameterSet(PARAM_CC)) {
			removeParameter(PARAM_CC);
		}
		this.cc = cc;
		if (this.cc != null && !this.cc.equals("")) {
			addParameter(PARAM_CC, this.cc);
		}
	}
	
	public String getRecipients() {
		return recipients;
	}
	
	public void setRecipients(String recipients) {
		this.recipients = recipients;
		super.setURL("mailto:" + recipients);
	}
	
	public void setURL(String url) {
		setRecipients(url);
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		if (isParameterSet(PARAM_SUBJECT)) {
			removeParameter(PARAM_SUBJECT);
		}
		this.subject = subject;
		if (this.subject != null && !this.subject.equals("")) {
			addParameter(PARAM_SUBJECT, this.subject);
		}
	}
	
	public void addParameter(String parameterName, String parameterValue) {
		if ((parameterName != null) && (parameterValue != null)) {
			if (_parameterString == null) {
				_parameterString = new StringBuffer();
				_parameterString.append("?");
			}
			else {
				_parameterString.append("&");
			}

			_parameterString.append(parameterName);
			_parameterString.append("=");
			_parameterString.append(parameterValue);
		}
	}
}