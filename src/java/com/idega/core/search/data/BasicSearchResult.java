/*
 * $Id: BasicSearchResult.java,v 1.1 2005/01/19 01:48:30 eiki Exp $
 * Created on Jan 18, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.search.data;

import java.util.Map;
import com.idega.core.search.business.SearchResult;


/**
 *  Last modified: $Date: 2005/01/19 01:48:30 $ by $Author: eiki $
 * 
 * A general implementation of SearchResult with simple get and set methods.
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.1 $
 */
public class BasicSearchResult implements SearchResult {

	private String searchResultType;
	private String searchResultName;
	private String searchResultAbstract;
	private String searchResultExtraInformation;
	private String searchResultURI;
	private Map searchResultAttributes;
	
	public BasicSearchResult() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchResult#getSearchResultType()
	 */
	public String getSearchResultType() {
		return searchResultType;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchResult#getSearchResultName()
	 */
	public String getSearchResultName() {
		return searchResultName;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchResult#getSearchResultExtraInformation()
	 */
	public String getSearchResultExtraInformation() {
		return searchResultExtraInformation;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchResult#getSearchResultAbstract()
	 */
	public String getSearchResultAbstract() {
		return searchResultAbstract;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchResult#getSearchResultURI()
	 */
	public String getSearchResultURI() {
		return searchResultURI;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchResult#getSearchResultAttributes()
	 */
	public Map getSearchResultAttributes() {
		return searchResultAttributes;
	}
	
	/**
	 * @param searchResultAbstract The searchResultAbstract to set.
	 */
	public void setSearchResultAbstract(String searchResultAbstract) {
		this.searchResultAbstract = searchResultAbstract;
	}
	
	/**
	 * @param searchResultAttributes The searchResultAttributes to set.
	 */
	public void setSearchResultAttributes(Map searchResultAttributes) {
		this.searchResultAttributes = searchResultAttributes;
	}
	
	/**
	 * @param searchResultExtraInformation The searchResultExtraInformation to set.
	 */
	public void setSearchResultExtraInformation(String searchResultExtraInformation) {
		this.searchResultExtraInformation = searchResultExtraInformation;
	}
	
	/**
	 * @param searchResultName The searchResultName to set.
	 */
	public void setSearchResultName(String searchResultName) {
		this.searchResultName = searchResultName;
	}
	
	/**
	 * @param searchResultType The searchResultType to set.
	 */
	public void setSearchResultType(String searchResultType) {
		this.searchResultType = searchResultType;
	}
	
	/**
	 * @param searchResultURI The searchResultURI to set.
	 */
	public void setSearchResultURI(String searchResultURI) {
		this.searchResultURI = searchResultURI;
	}
}
