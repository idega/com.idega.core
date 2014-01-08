package com.idega.repository.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface RepositoryItem {

	public InputStream getInputStream() throws IOException;

	public String getName();

	public long getLength();

	public boolean delete() throws IOException;

	public boolean isDirectory();

	public List<? extends RepositoryItem> getChildren();

}