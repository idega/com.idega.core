package com.idega.idegaweb.include;

public class AtomLink extends RSSLink {

	private static final long serialVersionUID = -2866966090139140751L;

	public AtomLink() {
		super();
		
		setType(PageResourceConstants.TYPE_ATOM);
	}
	
	public AtomLink(String url) {
		this();
		
		setUrl(url);
	}
	
	public AtomLink(String url, String relationship) {
		this(url);
		
		setRelationship(relationship);
	}
}