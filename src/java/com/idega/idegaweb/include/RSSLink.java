/*
 * $Id: RSSLink.java,v 1.2 2009/04/24 08:39:08 valdas Exp $
 * Created on 12.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.include;


/**
 * <p>
 *  Class to represent an external link to an RSS feed.
 * </p>
 *  Last modified: $Date: 2009/04/24 08:39:08 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class RSSLink extends ExternalLink{

	private static final long serialVersionUID = -5443508768481787898L;
	
	private String title;
	
	/**
	 * By default the relation to alternate
	 */
	public RSSLink(){
		setType(PageResourceConstants.TYPE_RSS);
		setRelationship(PageResourceConstants.RELATIONSHIP_ALTERNATE);
	}
	/**
	 * By default the relation to alternate
	 */
	public RSSLink(String url){
		this();
		setUrl(url);
	}
	
	public RSSLink(String url,String relationship){
		this(url);
		setRelationship(relationship);
	}
	
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return this.title;
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}
