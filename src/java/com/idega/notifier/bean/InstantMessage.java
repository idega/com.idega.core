package com.idega.notifier.bean;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InstantMessage implements Serializable {

	private static final long serialVersionUID = 4921583334016009981L;

	private String title;
	private String message;

	private Map<String, Boolean> sentToSessions = new ConcurrentHashMap<String, Boolean>();

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

	public void addSentToSession(String id) {
		sentToSessions.put(id, Boolean.TRUE);
	}

	public void addSentToSession(Collection<String> ids) {
		for (String id: ids) {
			addSentToSession(id);
		}
	}

	public boolean canSendToSession(String id) {
		return !sentToSessions.containsKey(id);
	}

}
