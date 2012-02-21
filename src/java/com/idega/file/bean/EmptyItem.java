package com.idega.file.bean;

import java.io.IOException;
import java.io.InputStream;

import com.idega.repository.bean.RepositoryItem;
import com.idega.util.StringHandler;

public class EmptyItem implements RepositoryItem {

	private String explanation = "This directory has no files";
	
	public InputStream getInputStream() throws IOException {
		try {
			return StringHandler.getStreamFromString(explanation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getName() {
		return "README.txt";
	}

	public long getLength() {
		return explanation.length();
	}

	public boolean delete() throws IOException {
		return true;
	}
}