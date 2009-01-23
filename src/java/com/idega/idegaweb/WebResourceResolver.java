package com.idega.idegaweb;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class WebResourceResolver implements Map<String, String> {
	
	private String bundleIdentifier;
	
	public WebResourceResolver(String bundleIdentifier) {
		super();
		
		this.bundleIdentifier = bundleIdentifier;
	}
	
	public void clear() {
		throw new UnsupportedOperationException(this.getClass().getName() + ": UnsupportedOperationException");
	}

	public boolean containsKey(Object arg0) {
		throw new UnsupportedOperationException(this.getClass().getName() + ": UnsupportedOperationException");
	}

	public boolean containsValue(Object arg0) {
		throw new UnsupportedOperationException(this.getClass().getName() + ": UnsupportedOperationException");
	}

	public Set<java.util.Map.Entry<String, String>> entrySet() {
		throw new UnsupportedOperationException(this.getClass().getName() + ": UnsupportedOperationException");
	}

	public String get(Object arg0) {
		return IWMainApplication.getDefaultIWMainApplication().getBundle(bundleIdentifier).getVirtualPathWithFileNameString(arg0.toString());
	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException(this.getClass().getName() + ": UnsupportedOperationException");
	}

	public Set<String> keySet() {
		throw new UnsupportedOperationException(this.getClass().getName() + ": UnsupportedOperationException");
	}

	public String put(String arg0, String arg1) {
		throw new UnsupportedOperationException(this.getClass().getName() + ": UnsupportedOperationException");
	}

	public void putAll(Map<? extends String, ? extends String> arg0) {
		throw new UnsupportedOperationException(this.getClass().getName() + ": UnsupportedOperationException");
	}

	public String remove(Object arg0) {
		throw new UnsupportedOperationException(this.getClass().getName() + ": UnsupportedOperationException");
	}

	public int size() {
		throw new UnsupportedOperationException(this.getClass().getName() + ": UnsupportedOperationException");
	}

	public Collection<String> values() {
		throw new UnsupportedOperationException(this.getClass().getName() + ": UnsupportedOperationException");
	}

}
