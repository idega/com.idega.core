package com.idega.business.file;

import java.io.Serializable;
import java.util.List;

public class FileDownloadNotificationProperties implements Serializable {

	private static final long serialVersionUID = 4222083116747517517L;

	private String file;
	private String url;
	private String server;
	
	private List<String> users;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}
}
