/*
 * $Id: RSSLink.java,v 1.1 2005/03/06 13:17:37 tryggvil Exp $
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
 *  Last modified: $Date: 2005/03/06 13:17:37 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class RSSLink extends ExternalLink{
	
	/**
	 * By default the relation to alternate
	 */
	public RSSLink(){
		setType(TYPE_RSS);
		setRelationship(RELATIONSHIP_ALTERNATE);
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
}
