/**
 * $Id: SimpleMessage.java,v 1.2 2007/06/27 22:19:08 tryggvil Exp $
 * Created in 2007 by tryggvil
 *
 * Copyright (C) 2000-2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.messaging;

import javax.mail.MessagingException;
import com.idega.idegaweb.IWMainApplication;


/**
 * <p>
 * Basic datastructure for the Messaging System.
 * </p>
 *  Last modified: $Date: 2007/06/27 22:19:08 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class SimpleMessage implements Cloneable {

	private String subject;
	private String body;
	
	public SimpleMessage() {
		super();
	}
	
	public SimpleMessage(String subject, String body) {
		this();
		
		setSubject(subject);
		setBody(body);
	}

	protected SimpleMessage(SimpleMessage message) {
		this(message.getSubject(), message.getBody());
	}
	
	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	protected MessagingSettings getMessagingSettings(){
		return getIWMainApplication().getMessagingSettings();
	}
	
	private IWMainApplication getIWMainApplication() {
		return IWMainApplication.getDefaultIWMainApplication();
	}
	
	/**
	 * <p>
	 * Sends the email to the receiver.
	 * </p>
	 * @throws MessagingException
	 */
	public void send() throws MessagingException {}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new SimpleMessage(this);
	}
}