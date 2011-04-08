package com.idega.repository.bean;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import com.idega.core.data.ICTreeNode;

public interface RepositoryItem extends ICTreeNode {

	public InputStream getInputStream() throws IOException;

	public String getName();

	public long getLength();
	public long getCreationDate();
	public long getLastModified();

	public boolean delete() throws IOException;

	public Collection<RepositoryItem> getChildResources();

	public boolean isCollection();

	public boolean exists();

	public String getPath();
	public String getParentPath();

	public URL getHttpURL();

	public String getMimeType();
}