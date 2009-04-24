package com.idega.util.resources;

import com.idega.idegaweb.include.ExternalLink;

public interface AbstractMinifier {
	
	public abstract String getMinifiedResource(ExternalLink resource);

}
