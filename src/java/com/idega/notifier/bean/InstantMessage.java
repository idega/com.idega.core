package com.idega.notifier.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InstantMessage implements Serializable {

	private static final long serialVersionUID = 4921583334016009981L;

	private String title;
	private String message;
	
	private List<String> sentToSessions = new ArrayList<String>();
	
	public InstantMessage() {
		super();
	}
	
	public InstantMessage(String title, String message) {
		this();
		
		this.title = title;
		this.message = message;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getSentToSessions() {
		return sentToSessions;
	}

	public void setSentToSessions(List<String> sentToSessions) {
		this.sentToSessions = sentToSessions;
	}
	
	public void addSentToSession(String id) {
		synchronized (sentToSessions) {
			sentToSessions.add(id);
		}
	}
	
	public void addSentToSession(Collection<String> ids) {
		synchronized (sentToSessions) {
			sentToSessions.addAll(ids);
		}
	}
	
	public boolean canSendToSession(String id) {
		synchronized (sentToSessions) {
			return !sentToSessions.contains(id);
		}
	}

}
