/*
 * $Id: SearchResults.java,v 1.2 2005/01/19 01:48:30 eiki Exp $ Created on Jan
 * 17, 2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.search.presentation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.idega.core.search.business.Search;
import com.idega.core.search.business.SearchPlugin;
import com.idega.core.search.business.SearchPluginManager;
import com.idega.core.search.business.SearchQuery;
import com.idega.core.search.business.SearchResult;
import com.idega.core.search.data.AdvancedSearchQuery;
import com.idega.core.search.data.SimpleSearchQuery;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;

/**
 * Last modified: $Date: 2005/01/19 01:48:30 $ by $Author: eiki $
 * 
 * This block can use all SearchPlugin objects registered in bundles and sets up
 * the search results (simple by default or advanced) <br>
 * Use setInputParameterName if you want to have different searches on the same
 * page, remember to set the same parameter <br>
 * for the Searcher block.
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson </a>
 * @version $Revision: 1.2 $
 */
public class SearchResults extends Block {

	public static final String DEFAULT_STYLE_CLASS = "iw_search_results";

	public static final String DEFAULT_ROW_EVEN_STYLE_CLASS = "iw_search_results_row_even";

	public static final String DEFAULT_ROW_ODD_STYLE_CLASS = "iw_search_results_row_odd";

	public static final String DEFAULT_SEARCH_PARAMETER_NAME = Searcher.DEFAULT_SEARCH_PARAMETER_NAME;
	
	public static final String DEFAULT_LINK_STYLE_CLASS = "iw_search_result_link";
	public static final String DEFAULT_ABSTRACT_TEXT_STYLE_CLASS = "iw_search_result_abstract";
	public static final String DEFAULT_EXTRA_INFO_TEXT_STYLE_CLASS = "iw_search_result_extra_info";
	private static final String DEFAULT_SEARCH_NAME_STYLE_CLASS = "iw_search_name";
	
	private String linkStyleClass = DEFAULT_LINK_STYLE_CLASS;
	private String abstractTextStyleClass = DEFAULT_ABSTRACT_TEXT_STYLE_CLASS;
	private String extraInformationTextStyleClass = DEFAULT_EXTRA_INFO_TEXT_STYLE_CLASS;


	private String searchNameStyleClass = DEFAULT_SEARCH_NAME_STYLE_CLASS;
	
	private String styleClass = DEFAULT_STYLE_CLASS;

	private String rowEvenStyleClass = DEFAULT_ROW_EVEN_STYLE_CLASS;

	private String rowOddStyleClass = DEFAULT_ROW_ODD_STYLE_CLASS;

	private String searchParameterName = DEFAULT_SEARCH_PARAMETER_NAME;
	


	public SearchResults() {
		super();
	}

	/**
	 * @return Returns the searchParameterName.
	 */
	public String getSearchParameterName() {
		return searchParameterName;
	}

	/**
	 * @param searchParameterName
	 *            The searchParameterName to set.
	 */
	public void setSearchParameterName(String searchParameterName) {
		this.searchParameterName = searchParameterName;
	}

	/**
	 * @return Returns the rowOddStyleClass.
	 */
	public String getRowOddStyleClass() {
		return rowOddStyleClass;
	}

	/**
	 * @param rowOddStyleClass
	 *            The rowOddStyleClass to set.
	 */
	public void setRowOddStyleClass(String buttonStyleClass) {
		this.rowOddStyleClass = buttonStyleClass;
	}

	/**
	 * @return Returns the rowEvenStyleClass.
	 */
	public String getRowEvenStyleClass() {
		return rowEvenStyleClass;
	}

	/**
	 * @param rowEvenStyleClass
	 *            The rowEvenStyleClass to set.
	 */
	public void setRowEvenStyleClass(String inputStyleClass) {
		this.rowEvenStyleClass = inputStyleClass;
	}

	/**
	 * @return Returns the styleClass.
	 */
	public String getStyleClass() {
		return styleClass;
	}

	/**
	 * @param styleClass
	 *            The styleClass to set.
	 */
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		//	IWResourceBundle iwrb =
		// iwc.getIWMainApplication().getCoreBundle().getResourceBundle(iwc);
		if (iwc.isParameterSet(getSearchParameterName())) {
			Layer container = new Layer();
			container.setStyleClass(getStyleClass());
			
			//prototypes
			Layer evenRowProtoType = new Layer();
			evenRowProtoType.setStyleClass(getRowEvenStyleClass());
			
			Layer oddRowProtoType = new Layer();
			oddRowProtoType.setStyleClass(getRowOddStyleClass());
			
			Link linkProtoType = new Link();
			linkProtoType.setStyleClass(getLinkStyleClass());
			
			Text abstractTextProtoType = new Text();
			abstractTextProtoType.setStyleClass(getAbstractTextStyleClass());

			Text extraInfoTextProtoType = new Text();
			extraInfoTextProtoType.setStyleClass(getExtraInformationTextStyleClass());
			//////
			boolean isAdvancedSearch = false;
			SearchQuery query;
			if (iwc.isParameterSet(Searcher.DEFAULT_ADVANCED_SEARCH_PARAMETER_NAME)) {
				//TODO advanced search
				//container.add("Advanced search");
				String queryString = iwc.getParameter(getSearchParameterName());
				Map queryMap = new HashMap();
				queryMap.put(getSearchParameterName(), queryString);
				query = new AdvancedSearchQuery();
				isAdvancedSearch = true;
			}
			else {
				String queryString = iwc.getParameter(getSearchParameterName());
				Map queryMap = new HashMap();
				queryMap.put(getSearchParameterName(), queryString);
				query = new SimpleSearchQuery(queryMap);
				//container.add("Simple search");
				//container.addBreak();
			}
			
		
			Collection plugins = SearchPluginManager.getInstance().getAllSearchPluginsInitialized(iwc.getIWMainApplication());
			if (!plugins.isEmpty()) {
				Iterator iter = plugins.iterator();
				while (iter.hasNext()) {
					//odd or even row
					Layer rowContainer;
					
					
					SearchPlugin searchPlugin = (SearchPlugin) iter.next();
					if( (isAdvancedSearch && searchPlugin.getSupportsAdvancedSearch()) || (!isAdvancedSearch && searchPlugin.getSupportsSimpleSearch()) ){
						
						Search search = searchPlugin.createSearch(query);
						Collection results = search.getSearchResults();
						
						if (results != null && !results.isEmpty()) {
							Text searchName = new Text(searchPlugin.getSearchName());
							searchName.setStyleClass(getSearchNameStyleClass());
							container.add(searchName);
							
							int row = 1;
							Iterator iterator = results.iterator();
							while (iterator.hasNext()) {
								
								if(row%2==0){
									rowContainer = (Layer)evenRowProtoType.clone();
								}
								else{
									rowContainer = (Layer)oddRowProtoType.clone();
								}
								
								SearchResult result = (SearchResult) iterator.next();
								String textOnLink = result.getSearchResultName();
								String uri = result.getSearchResultURI();
								String abstractText = result.getSearchResultAbstract();
								String extraInfo = result.getSearchResultExtraInformation();
								//todo group by type
								//String type = result.getSearchResultType();
								
								if(textOnLink!=null){
									Link link = (Link) linkProtoType.clone();
									link.setText(textOnLink);
								
									if(uri!=null){
										link.setURL(uri);
									}
									rowContainer.add(link);
								}
								
								if(abstractText!=null){
									Text abstractT = (Text)abstractTextProtoType.clone();
									abstractT.setText(abstractText);
									rowContainer.add(abstractT);
								}
								
								if(extraInfo!=null){
									Text extraInfoText = (Text)extraInfoTextProtoType.clone();
									extraInfoText.setText(extraInfo);
									rowContainer.add(extraInfoText);
								}
								
								container.add(rowContainer);
								row++;
							}
						}
					}
				}
			}
			add(container);
		}
	}
	
	/**
	 * @return Returns the linkStyleClass.
	 */
	public String getLinkStyleClass() {
		return linkStyleClass;
	}
	/**
	 * @param linkStyleClass The linkStyleClass to set.
	 */
	public void setLinkStyleClass(String linkStyleClass) {
		this.linkStyleClass = linkStyleClass;
	}
	/**
	 * @return Returns the abstractTextStyleClass.
	 */
	public String getAbstractTextStyleClass() {
		return abstractTextStyleClass;
	}
	/**
	 * @param abstractTextStyleClass The abstractTextStyleClass to set.
	 */
	public void setAbstractTextStyleClass(String abstractTextStyleClass) {
		this.abstractTextStyleClass = abstractTextStyleClass;
	}
	/**
	 * @return Returns the extraInformationTextStyleClass.
	 */
	public String getExtraInformationTextStyleClass() {
		return extraInformationTextStyleClass;
	}
	/**
	 * @param extraInformationTextStyleClass The extraInformationTextStyleClass to set.
	 */
	public void setExtraInformationTextStyleClass(String extraInformationTextStyleClass) {
		this.extraInformationTextStyleClass = extraInformationTextStyleClass;
	}
	/**
	 * @return Returns the searchNameStyleClass.
	 */
	public String getSearchNameStyleClass() {
		return searchNameStyleClass;
	}
	/**
	 * @param searchNameStyleClass The searchNameStyleClass to set.
	 */
	public void setSearchNameStyleClass(String searchNameStyleClass) {
		this.searchNameStyleClass = searchNameStyleClass;
	}
}