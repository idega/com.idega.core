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

import java.util.Random;

import com.idega.util.StringUtil;


/**
 * <p>
 *  Class to represent an external link to an RSS feed.
 * </p>
 *  Last modified: $Date: 2009/04/24 08:39:08 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class RSSLink extends ExternalLink {

	private static final long serialVersionUID = -5443508768481787898L;
	
	private int hashCode;
	
	private String title;
	
	/**
	 * By default the relation to alternate
	 */
	public RSSLink() {
		hashCode = new Random().nextInt();
		
		setType(PageResourceConstants.TYPE_RSS);
		setRelationship(PageResourceConstants.RELATIONSHIP_ALTERNATE);
	}
	
	/**
	 * By default the relation to alternate
	 */
	public RSSLink(String url) {
		this();
		setUrl(url);
	}
	
	public RSSLink(String url, String relationship) {
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

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RSSLink) {
			return StringUtil.isEmpty(getUrl()) ? false : getUrl().equals(((RSSLink) obj).getUrl());
		}
		return false;
	}
}