/*
 * $Id: SearchEngine.java,v 1.2 2005/01/18 12:43:12 tryggvil Exp $
 * Created on Jan 17, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.search.business;

import java.util.List;
import java.util.Map;
import com.idega.idegaweb.IWMainApplication;


/**
 * 
 *  Last modified: $Date: 2005/01/18 12:43:12 $ by $Author: tryggvil $
 * 
 * This interface defines methods that have to be implemented to make a resource searchable.<br>
 * Objects implementing this interface should be registered to their bundle as "Searchable" <br>
 * and will then become a search option in the Search block that they can add to a page and <br>
 * the results will be shown in the corresponding SearchResults block that they should also add to a page.
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.2 $
 */
public interface SearchEngine {
	
	/**
	 * This method is called once in the lifetime of the context for each searchable object before any search is done.
	 * @param iwc
	 * @return true if initialized correctly otherwise false.
	 */
	public boolean initialize(IWMainApplication iwma);
	
	/**
	 * This method is called when the server shuts down and the implementing class should use it to clean up and shutdown if needed.
	 * @param iwc
	 */
	public void destroy(IWMainApplication iwma);
	
	/**
	 * Creates a new search for a user with the key=value parameters in the Map.
	 * @param searchParameters
	 * @return
	 */
	public Search createSearch(SearchQuery searchQuery);
	
	/**
	 * @return a list of parameters the advanced search form should set up and retrieve after the form is submitted to use as input in getAdvancedSearchResults
	 */
	public List getAdvancedSearchSupportedParameters();
	
	/**
	 * @return true if the object implements getSimpleSearchResults.
	 */
	public boolean getSupportsSimpleSearch();
	
	/**
	 * 
	 * @return true if the object implements getAdvancedSearchResults.
	 */
	public boolean getSupportsAdvancedSearch();
	
}
