package com.idega.util.mail;

import java.io.File;

public class Attachment {

	private File attachment;
	
	private String cid;
	private String disposition;
	
	public Attachment(File attachment) {
		this.attachment = attachment;
	}
	
	public Attachment(File attachment, String cid, String disposition) {
		this(attachment);
		this.cid = cid;
		this.disposition = disposition;
	}

	public File getAttachment() {
		return attachment;
	}

	public void setAttachment(File attachment) {
		this.attachment = attachment;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
	
}