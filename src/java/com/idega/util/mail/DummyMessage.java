package com.idega.util.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

public class DummyMessage extends Message {

	@Override
	public int getSize() throws MessagingException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLineCount() throws MessagingException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getContentType() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMimeType(String mimeType) throws MessagingException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDisposition() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDisposition(String disposition) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescription() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescription(String description) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFileName() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFileName(String filename) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream getInputStream() throws IOException, MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataHandler getDataHandler() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getContent() throws IOException, MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataHandler(DataHandler dh) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContent(Object obj, String type) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setText(String text) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContent(Multipart mp) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeTo(OutputStream os) throws IOException, MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] getHeader(String header_name) throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHeader(String header_name, String header_value) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addHeader(String header_name, String header_value) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeHeader(String header_name) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Enumeration<?> getAllHeaders() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<?> getMatchingHeaders(String[] header_names) throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<?> getNonMatchingHeaders(String[] header_names) throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Address[] getFrom() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFrom() throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFrom(Address address) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFrom(Address[] addresses) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Address[] getRecipients(RecipientType type) throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRecipients(RecipientType type, Address[] addresses) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRecipients(RecipientType type, Address[] addresses) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSubject() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSubject(String subject) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Date getSentDate() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSentDate(Date date) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Date getReceivedDate() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flags getFlags() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFlags(Flags flag, boolean set) throws MessagingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Message reply(boolean replyToAll) throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveChanges() throws MessagingException {
		// TODO Auto-generated method stub

	}

}