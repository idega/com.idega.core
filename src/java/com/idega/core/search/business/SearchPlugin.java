/*
 * $Id: SearchPlugin.java,v 1.1 2005/01/19 01:48:30 eiki Exp $
 * Created on Jan 17, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.search.business;

import java.util.List;
import com.idega.idegaweb.IWMainApplication;


/**
 * 
 *  Last modified: $Date: 2005/01/19 01:48:30 $ by $Author: eiki $
 * 
 * This interface defines methods that have to be implemented to make "collection" searchable e.g. users/files/websites etc.<br>
 * Objects implementing this interface should be registered to their bundle as "iw.searchplugin" if you want to use the default<br>
 * presentation implementation from com.idega.core.search.presentation. The plugin will then become a search option in the Search block<br>
 * that they can add to a page and the results will be shown in the corresponding SearchResults block <br>
 * that they should also add to a page.<br>
 * Alternatively you can implement your own presentation layer for your own SearchPlugin using the interfaces and basic implementations<br>
 * in the package com.idega.search.
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>,<a href="mailto:tryggvi@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.1 $
 */
public interface SearchPlugin {
	
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
	 * @return a Search object that you use to get some results from
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
	
	/**
	 * @return A displayable, preferably localized name, can be null
	 */
	public String getSearchName();
	
	/**
	 * @return A displayable, preferably localized name, can be null
	 */
	public String getSearchDescription();
	
}
