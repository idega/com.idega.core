package com.idega.util.resources;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.List;

public interface ResourceScanner extends Serializable {

	public void scanFile();
	
	public StringBuffer getResultBuffer();

	public boolean isNeedToReplace();
	
	public void setReaderBuffer(BufferedReader readerBuffer);
	
	public void setLinkToTheme(String linkToTheme);
	
	public String getParsedContent(List<String> contentLines, String fileUri);
}
