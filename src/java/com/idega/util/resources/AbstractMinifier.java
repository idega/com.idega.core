package com.idega.util.resources;

import java.io.InputStream;

public interface AbstractMinifier {
	
	public abstract String getMinifiedResource(String content);
	
	public abstract String getMinifiedResource(InputStream stream);

}
