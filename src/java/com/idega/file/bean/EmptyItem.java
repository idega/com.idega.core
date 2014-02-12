package com.idega.file.bean;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import javax.jcr.RepositoryException;
import java.util.List;

import com.idega.repository.bean.RepositoryItem;
import com.idega.util.StringHandler;

public class EmptyItem extends RepositoryItem {

	private static final long serialVersionUID = 913585203882550527L;

	private String explanation = "This directory has no files";

	@Override
	public InputStream getInputStream() throws IOException {
		try {
			return StringHandler.getStreamFromString(explanation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getName() {
		return "README.txt";
	}

	@Override
	public long getLength() {
		return explanation.length();
	}

	@Override
	public boolean delete() throws IOException {
		return true;
	}

	@Override
	public Collection<RepositoryItem> getChildResources() {
		return null;
	}

	@Override
	public boolean isCollection() {
		return false;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public URL getHttpURL() {
		return null;
	}

	@Override
	public String getMimeType() {
		return null;
	}

	@Override
	public long getCreationDate() {
		return 0;
	}

	@Override
	public long getLastModified() {
		return 0;
	}

	@Override
	public String getParentPath() {
		return null;
	}

	@Override
	public RepositoryItem getParenItem() {
		return null;
	}

	@Override
	public Collection<RepositoryItem> getSiblingResources() {
		return Collections.emptyList();
	}

	@Override
	public boolean createNewFile() throws IOException, RepositoryException {
		return false;
	}
	public List<EmptyItem> getChildren() {
		return null;
	}

}