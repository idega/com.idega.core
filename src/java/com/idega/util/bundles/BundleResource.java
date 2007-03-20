package com.idega.util.bundles;

import java.io.IOException;
import java.io.InputStream;

import com.idega.idegaweb.IWBundle;

public class BundleResource implements Resource {

	private IWBundle bundle;
	private String pathWithinBundle;
	
	public BundleResource(IWBundle bundle, String pathWithinBundle) {
		this.bundle = bundle;
		this.pathWithinBundle = pathWithinBundle;
	}

	public InputStream getInputStream() throws IOException {
		return bundle.getResourceInputStream(pathWithinBundle);
	}

	public long lastModified() {
		return bundle.getResourceTime(pathWithinBundle);
	}
}