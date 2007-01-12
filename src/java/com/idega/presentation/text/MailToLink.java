/*
 * $Id: MailToLink.java,v 1.1.2.2 2007/01/12 19:32:01 idegaweb Exp $
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
		return this.bcc;
	}
	
	public void setBCC(String bcc) {
		if (isParameterSet(this.PARAM_BCC)) {
			removeParameter(this.PARAM_BCC);
		}
		this.bcc = bcc;
		if (this.bcc != null && !this.bcc.equals("")) {
			addParameter(this.PARAM_BCC, this.bcc);
		}
	}
	
	public String getBody() {
		return this.body;
	}
	
	public void setBody(String body) {
		if (isParameterSet(this.PARAM_BODY)) {
			removeParameter(this.PARAM_BODY);
		}
		this.body = body;
		if (this.body != null && !this.body.equals("")) {
			addParameter(this.PARAM_BODY, this.body);
		}
	}
	
	public String getCC() {
		return this.cc;
	}
	
	public void setCC(String cc) {
		if (isParameterSet(this.PARAM_CC)) {
			removeParameter(this.PARAM_CC);
		}
		this.cc = cc;
		if (this.cc != null && !this.cc.equals("")) {
			addParameter(this.PARAM_CC, this.cc);
		}
	}
	
	public String getRecipients() {
		return this.recipients;
	}
	
	public void setRecipients(String recipients) {
		this.recipients = recipients;
		super.setURL("mailto:" + recipients);
	}
	
	public void setURL(String url) {
		setRecipients(url);
	}
	
	public String getSubject() {
		return this.subject;
	}
	
	public void setSubject(String subject) {
		if (isParameterSet(this.PARAM_SUBJECT)) {
			removeParameter(this.PARAM_SUBJECT);
		}
		this.subject = subject;
		if (this.subject != null && !this.subject.equals("")) {
			addParameter(this.PARAM_SUBJECT, this.subject);
		}
	}
	
	public void addParameter(String parameterName, String parameterValue) {
		if ((parameterName != null) && (parameterValue != null)) {
			if (this._parameterString == null) {
				this._parameterString = new StringBuffer();
				this._parameterString.append("?");
			}
			else {
				this._parameterString.append("&");
			}

			this._parameterString.append(parameterName);
			this._parameterString.append("=");
			this._parameterString.append(parameterValue);
		}
	}
}