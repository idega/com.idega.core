/*
 * $Id: ExternalLink.java,v 1.1 2005/03/06 13:17:37 tryggvil Exp $
 * Created on 12.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.include;


/**
 * 	<p>
 *  Class to represent an External Link to a Resource that is represented in html as 
 *  a <code>&lt;link&gt;</code> tag used to define resources such ass CSS links.
 *  This class is used by the GlobalIncludeManager for managing resources included in all pages.
 *  </p>
 *  Last modified: $Date: 2005/03/06 13:17:37 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class ExternalLink {
	
	//static contants
	protected final static String MEDIA_SCREEN="screen";
	protected final static String MEDIA_TTY="tty";
	protected final static String MEDIA_TV="tv";
	protected final static String MEDIA_PROJECTION="projection";
	protected final static String MEDIA_HANDHELD="handheld";
	protected final static String MEDIA_PRINT="print";
	protected final static String MEDIA_BRAILLE="braille";
	protected final static String MEDIA_AURAL="aural";
	protected final static String MEDIA_ALL="all";
	
	protected final static String TYPE_CSS="text/css";
	protected final static String TYPE_RSS="application/rss+xml";
	
	protected final static String RELATIONSHIP_STYLESHEET="stylesheet";
	protected final static String RELATIONSHIP_ALTERNATE="alternate";
	
	//instance variables
	//href attribute
	private String url;
	//charset attribute
	private String characterset;
	//hreflang attribute
	private String urllanguage;
	private String type;
	//rel attribute
	private String relationship;
	//rev attribute
	private String reverseRelationship;
	private String media;
	//title attributes (maybe only used for rss)
	private String title;

	
	/**
	 * @return Returns the characterset.
	 */
	public String getCharacterset() {
		return characterset;
	}
	/**
	 * @param characterset The characterset to set.
	 */
	public void setCharacterset(String characterset) {
		this.characterset = characterset;
	}
	/**
	 * @return Returns the media.
	 */
	public String getMedia() {
		return media;
	}
	/**
	 * @param media The media to set.
	 */
	public void setMedia(String media) {
		this.media = media;
	}
	/**
	 * @return Returns the relationship.
	 */
	public String getRelationship() {
		return relationship;
	}
	/**
	 * @param relationship The relationship to set.
	 */
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	/**
	 * @return Returns the reverseRelationship.
	 */
	public String getReverseRelationship() {
		return reverseRelationship;
	}
	/**
	 * @param reverseRelationship The reverseRelationship to set.
	 */
	public void setReverseRelationship(String reverseRelationship) {
		this.reverseRelationship = reverseRelationship;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return Returns the url.
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url The url to set.
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return Returns the urllanguage.
	 */
	public String getUrllanguage() {
		return urllanguage;
	}
	/**
	 * @param urllanguage The urllanguage to set.
	 */
	public void setUrllanguage(String urllanguage) {
		this.urllanguage = urllanguage;
	}
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}
