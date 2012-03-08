package com.idega.repository.bean;

import java.io.IOException;
import java.io.InputStream;

public interface RepositoryItem {

	public InputStream getInputStream() throws IOException;
	
	public String getName();
	
	public long getLength();
	
	public boolean delete() throws IOException;
}