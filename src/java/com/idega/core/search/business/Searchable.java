/*
 * $Id: Searchable.java,v 1.1 2005/01/17 19:16:25 eiki Exp $
 * Created on Jan 17, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.search.business;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;


/**
 * 
 *  Last modified: $Date: 2005/01/17 19:16:25 $ by $Author: eiki $
 * 
 * This interface defines methods that have to be implemented to make a resource searchable.<br>
 * Objects implementing this interface should be registered to their bundle as "Searchable" <br>
 * and will then become a search option in the Search block that they can add to a page and <br>
 * the results will be shown in the corresponding SearchResults block that they should also add to a page.
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.1 $
 */
public interface Searchable {
	
	/**
	 * This method is called once in the lifetime of the context for each searchable object before any search is done.
	 * @param iwc
	 * @return true if initialized correctly otherwise false.
	 */
	public boolean initialize(IWContext iwc);
	
	/**
	 * This method is called when the server shuts down and the implementing class should use it to clean up and shutdown if needed.
	 * @param iwc
	 */
	public void destroy(IWContext iwc);
	
	/**
	 * Processes the query and returns a collection of result objects.
	 * @param queryString The query string the user entered in a simple text input<br>
	 * @param iwc The context so the search method can get the paremeters it needs if any.<br>
	 * @return a collection of Objects (any type) the Search block will in turn call the getResultPresentation method for each object in the collection.
	 */
	public Collection getSimpleSearchResults(String queryString, IWContext iwc);
	
	/**
	 * The method that is called for each object you return by either getSimpleSearchResults or getAdvancedSearchResults.
	 * @param resultObject an object of the type you put in the result collection
	 * @param iwc
	 * @return a presentationobject representing the search result
	 */
	public PresentationObject getResultPresentation(Object resultObject, IWContext iwc);
	
	/**
	 * Processes the query and returns a collection of result objects
	 * @param queryMap A map of parameter - queryString mappings.
	 * @param iwc The context so the search method can get other parameters.<br>
	 * @return a collection of Objects (any type) the Search block will in turn call the getResultRow method for each object in the collection.
	 */
	public Collection getAdvancedSearchResults(Map queryMap, IWContext iwc);
	
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
	
	
	/**
	 * @param iwc
	 * @return a localized String name of the search to be displayed in the search results and possibly as a title for the search.
	 */
	public String getSearchName(IWContext iwc);
	
}
