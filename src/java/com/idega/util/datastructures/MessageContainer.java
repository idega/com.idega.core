package com.idega.util.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Apr 20, 2004
 */
public class MessageContainer {
	
	private String mainMessage;
	private List messages;
	
	/**
	 * @return Returns the mainMessage.
	 */
	public String getMainMessage() {
		return this.mainMessage;
	}
	
	/**
	 * @param mainMessage The mainMessage to set.
	 */
	public void setMainMessage(String mainMessage) {
		this.mainMessage = mainMessage;
	}
	
	/**
	 * @return Returns the messages.
	 */
	public List getMessages() {
		return this.messages;
	}
	
	public boolean addMessage(String message) {
		if (this.messages == null) {
			this.messages = new ArrayList();
		}
		return this.messages.add(message);
	}
		
}
