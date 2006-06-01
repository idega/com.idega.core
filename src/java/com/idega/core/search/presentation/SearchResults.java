/*
 * $Id: SearchResults.java,v 1.13 2006/06/01 17:07:13 eiki Exp $ Created on Jan
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
import java.util.List;
import java.util.Map;
import com.idega.core.search.business.Search;
import com.idega.core.search.business.SearchPlugin;
import com.idega.core.search.business.SearchPluginManager;
import com.idega.core.search.business.SearchQuery;
import com.idega.core.search.business.SearchResult;
import com.idega.core.search.data.AdvancedSearchQuery;
import com.idega.core.search.data.SimpleSearchQuery;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.CSSSpacer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;

/**
 * Last modified: $Date: 2006/06/01 17:07:13 $ by $Author: eiki $
 * 
 * This block can use all SearchPlugin objects registered in bundles and sets up
 * the search results (simple by default or advanced) <br>
 * Use setInputParameterName if you want to have different searches on the same
 * page, remember to set the same parameter for the Searcher block.<br>
 * The look of the results are set via stylesheet classes defined in
 * iw_core.css.<br>
 * Do not change core_iw.css, rather add your own custom stylesheet after the
 * iw_core.css is added and override the styles.
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson </a>
 * @version $Revision: 1.13 $
 */
public class SearchResults extends Block {

	public static final String DEFAULT_STYLE_CLASS = "iw_search_results";
	public static final String DEFAULT_ROW_EVEN_STYLE_CLASS = "iw_search_results_row_even";
	public static final String DEFAULT_ROW_ODD_STYLE_CLASS = "iw_search_results_row_odd";
	public static final String DEFAULT_LINK_STYLE_CLASS = "iw_search_result_link";
	public static final String DEFAULT_ICON_STYLE_CLASS = "iw_search_result_icon";
	public static final String DEFAULT_ABSTRACT_TEXT_STYLE_CLASS = "iw_search_result_abstract";
	public static final String DEFAULT_EXTRA_INFO_TEXT_STYLE_CLASS = "iw_search_result_extra_info";
	public static final String DEFAULT_EXTRA_ATTRIBUTE_ODD_STYLE_CLASS = "iw_search_result_extra_attribute_odd";
	public static final String DEFAULT_EXTRA_ATTRIBUTE_EVEN_STYLE_CLASS = "iw_search_result_extra_attribute_even";
	private static final String DEFAULT_SEARCH_NAME_STYLE_CLASS = "iw_search_name";
	
	
	private String linkStyleClass = DEFAULT_LINK_STYLE_CLASS;
	private String abstractTextStyleClass = DEFAULT_ABSTRACT_TEXT_STYLE_CLASS;
	private String extraInformationTextStyleClass = DEFAULT_EXTRA_INFO_TEXT_STYLE_CLASS;
	private String searchNameStyleClass = DEFAULT_SEARCH_NAME_STYLE_CLASS;
	private String styleClass = DEFAULT_STYLE_CLASS;
	private String iconStyleClass = DEFAULT_ICON_STYLE_CLASS;
	private String rowEvenStyleClass = DEFAULT_ROW_EVEN_STYLE_CLASS;
	private String rowOddStyleClass = DEFAULT_ROW_ODD_STYLE_CLASS;
	private String extraAttributeTextOddStyleClass = DEFAULT_EXTRA_ATTRIBUTE_ODD_STYLE_CLASS;
	private String extraAttributeTextEvenStyleClass = DEFAULT_EXTRA_ATTRIBUTE_EVEN_STYLE_CLASS;
	private String searchParameterName = Searcher.DEFAULT_SEARCH_PARAMETER_NAME;
	private String advancedSearchParameterName = Searcher.DEFAULT_ADVANCED_SEARCH_PARAMETER_NAME;
	// todo create handler
	private String searchPluginsToUse;

	public SearchResults() {
		super();
	}

	/**
	 * @return Returns the searchParameterName.
	 */
	public String getSimpleSearchParameterName() {
		return this.searchParameterName;
	}

	/**
	 * @param searchParameterName
	 *            The searchParameterName to set.
	 */
	public void setSimpleSearchParameterName(String searchParameterName) {
		this.searchParameterName = searchParameterName;
	}

	/**
	 * @return Returns the advancedSearchParameterName.
	 */
	public String getAdvancedSearchParameterName() {
		return this.advancedSearchParameterName;
	}

	/**
	 * @param searchParameterName
	 *            The advancedSearchParameterName to set.
	 */
	public void setAdvancedSearchParameterName(String advancedSearchParameterName) {
		this.advancedSearchParameterName = advancedSearchParameterName;
	}

	/**
	 * @return Returns the rowOddStyleClass.
	 */
	public String getRowOddStyleClass() {
		return this.rowOddStyleClass;
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
		return this.rowEvenStyleClass;
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
		return this.styleClass;
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
		IWResourceBundle iwrb = IWContext.getInstance().getIWMainApplication().getCoreBundle().getResourceBundle(iwc);
		if (iwc.isParameterSet(getSimpleSearchParameterName()) || iwc.isParameterSet(getAdvancedSearchParameterName())) {
			CSSSpacer spacer = new CSSSpacer();
			Layer container = new Layer();
			container.setStyleClass(getStyleClass());
			// prototypes
			Layer evenRowProtoType = new Layer();
			evenRowProtoType.setStyleClass(getRowEvenStyleClass());
			
			Layer oddRowProtoType = new Layer();
			oddRowProtoType.setStyleClass(getRowOddStyleClass());
			
			Layer iconPrototype = new Layer();
			iconPrototype.setStyleClass(getIconStyleClass());
			
			Link linkProtoType = new Link();
			linkProtoType.setStyleClass(getLinkStyleClass());
			
			Text abstractTextProtoType = new Text();
			abstractTextProtoType.setStyleClass(getAbstractTextStyleClass());
			
			Text extraInfoTextProtoType = new Text();
			extraInfoTextProtoType.setStyleClass(getExtraInformationTextStyleClass());
			
			Text extraAttributeOddProtoType = new Text();
			extraAttributeOddProtoType.setStyleClass(getExtraAttributeTextOddStyleClass());
			
			Text extraAttributeEvenProtoType = new Text();
			extraAttributeEvenProtoType.setStyleClass(getExtraAttributeTextEvenStyleClass());
			
			// ////
			// If the query is advanced then the query object is created later
			boolean isAdvancedSearch = false;
			SearchQuery query;
			if (iwc.isParameterSet(Searcher.DEFAULT_ADVANCED_SEARCH_PARAMETER_NAME)) {
				isAdvancedSearch = true;
				query = new AdvancedSearchQuery();
			}
			else {
				String queryString = iwc.getParameter(getSimpleSearchParameterName());
				Map queryMap = new HashMap();
				queryMap.put(getSimpleSearchParameterName(), queryString);
				query = new SimpleSearchQuery(queryMap);
			}
			// /
			boolean noResult = true;
			Collection plugins = SearchPluginManager.getInstance().getAllSearchPluginsInitialized(
					iwc.getIWMainApplication());
			if (!plugins.isEmpty()) {
				Iterator iter = plugins.iterator();
				while (iter.hasNext()) {
					SearchPlugin searchPlugin = (SearchPlugin) iter.next();
					// odd or even row
					Layer rowContainer;
					// todo get from handler
					if (getSearchPluginsToUse() != null) {
						String searchClass = searchPlugin.getClass().getName();
						String className  = searchClass.substring(searchClass.lastIndexOf('.') + 1);
						String pluginNamesOrClasses = getSearchPluginsToUse();
						if ( (pluginNamesOrClasses.indexOf(className) < 0) && (pluginNamesOrClasses.indexOf(searchClass) < 0) ) {
							continue;
						}
					}
					//
					if ((isAdvancedSearch && searchPlugin.getSupportsAdvancedSearch())
							|| (!isAdvancedSearch && searchPlugin.getSupportsSimpleSearch())) {
						if (isAdvancedSearch && searchPlugin.getSupportsAdvancedSearch()) {
							fillAdvancedSearch(iwc, query, searchPlugin);
						}
						Search search = searchPlugin.createSearch(query);
						Collection results = search.getSearchResults();
						if (results != null && !results.isEmpty()) {
							noResult = false;
							Text searchName = new Text(searchPlugin.getSearchName());
							searchName.setStyleClass(getSearchNameStyleClass());
							container.add(searchName);
							int row = 1;
							Iterator iterator = results.iterator();
							while (iterator.hasNext()) {
								SearchResult result = (SearchResult) iterator.next();
								String textOnLink = result.getSearchResultName();
								String uri = result.getSearchResultURI();
								String abstractText = result.getSearchResultAbstract();
								String extraInfo = result.getSearchResultExtraInformation();
								Map extraParameters = result.getSearchResultAttributes();
								// todo group by type
								String type = result.getSearchResultType();
								if (row % 2 == 0) {
									rowContainer = (Layer) evenRowProtoType.clone();
								}
								else {
									rowContainer = (Layer) oddRowProtoType.clone();
								}
								// add an extra optional style with the search
								// type suffix
								addSearchResultTypeStyleClass(rowContainer, type);
								// adding spacer to force the row container
								// around all floating elements, another one is
								// added at the end
								rowContainer.add(spacer.clone());
								// add an optional icon, use a background image
								// in the style
								Layer icon = (Layer) iconPrototype.clone();
								addSearchResultTypeStyleClass(icon, type);
								rowContainer.add(icon);
								if (textOnLink != null) {
									Link link = (Link) linkProtoType.clone();
									link.setText(textOnLink);
									// add an extra optional style with the
									// search type suffix
									addSearchResultTypeStyleClass(link, type);
									if (uri != null) {
										link.setURL(uri);
									}
									rowContainer.add(link);
								}
								if (extraInfo != null) {
									Text extraInfoText = (Text) extraInfoTextProtoType.clone();
									extraInfoText.setText(extraInfo);
									// add an extra optional style with the
									// search type suffix
									addSearchResultTypeStyleClass(extraInfoText, type);
									rowContainer.add(extraInfoText);
								}
								if (abstractText != null) {
									Text abstractT = (Text) abstractTextProtoType.clone();
									abstractT.setText(abstractText);
									// add an extra optional style with the
									// search type suffix
									addSearchResultTypeStyleClass(abstractT, type);
									rowContainer.add(abstractT);
								}
								if (extraParameters != null && !extraParameters.isEmpty()) {
									Iterator keys = extraParameters.keySet().iterator();
									int counter = 0;
									while (keys.hasNext()) {
										counter++;
										String key = (String) keys.next();
										String value = (String) extraParameters.get(key);
										Text extraParams;
										
										if (counter % 2 == 0) {
											extraParams = (Text)extraAttributeEvenProtoType.clone();
										}
										else {
											extraParams = (Text)extraAttributeOddProtoType.clone();
										}
										
										extraParams.setText(value);
										addSearchResultTypeAndAttributeKeyStyleClass(extraParams, type, key);
								
										rowContainer.add(extraParams);
									}
								}
								// adding spacer to force the row container
								// around all floating elements
								rowContainer.add(spacer.clone());
								container.add(rowContainer);
								row++;
							}
						}
					}
				}
			}
			if (noResult) {
				Text noResults = new Text(iwrb.getLocalizedString("search_results.no_results",
						"The search found no results matching your query."));
				noResults.setStyleClass(getSearchNameStyleClass());
				container.add(noResults);
			}
			add(container);
		}
	}

	/**
	 * @param iwc
	 * @param query
	 * @param searchPlugin
	 */
	protected void fillAdvancedSearch(IWContext iwc, SearchQuery query, SearchPlugin searchPlugin) {
		String queryString = iwc.getParameter(getSimpleSearchParameterName());
		Map queryMap = new HashMap();
		queryMap.put(getSimpleSearchParameterName(), queryString);
		List params = searchPlugin.getAdvancedSearchSupportedParameters();
		if (params != null && !params.isEmpty()) {
			Iterator iterator = params.iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = iwc.getParameter(key);
				queryMap.put(key, value);
			}
		}
		query.setSearchParameters(queryMap);
	}

	/**
	 * Adds an extra optional style with the search type suffix
	 * 
	 * @param obj
	 * @param type
	 */
	protected void addSearchResultTypeStyleClass(PresentationObject obj, String type) {
		String style = obj.getStyleClass();
		obj.setStyleClass(style + " " + style + "_" + type);
	}
	

	/**
	 * Adds an extra optional style with the search type suffix
	 * 
	 * @param obj
	 * @param type
	 */
	protected void addSearchResultTypeAndAttributeKeyStyleClass(PresentationObject obj, String type, String attributeKey) {
		String style = obj.getStyleClass();
		StringBuffer newStyle = new StringBuffer();
		newStyle.append(style).append(" ").append(style).append("_").append(type).append(" ").append(type).append("_").append(attributeKey);
		obj.setStyleClass(newStyle.toString());
	}

	/**
	 * @return Returns the linkStyleClass.
	 */
	public String getLinkStyleClass() {
		return this.linkStyleClass;
	}

	/**
	 * @param linkStyleClass
	 *            The linkStyleClass to set.
	 */
	public void setLinkStyleClass(String linkStyleClass) {
		this.linkStyleClass = linkStyleClass;
	}

	/**
	 * @return Returns the abstractTextStyleClass.
	 */
	public String getAbstractTextStyleClass() {
		return this.abstractTextStyleClass;
	}

	/**
	 * @param abstractTextStyleClass
	 *            The abstractTextStyleClass to set.
	 */
	public void setAbstractTextStyleClass(String abstractTextStyleClass) {
		this.abstractTextStyleClass = abstractTextStyleClass;
	}

	/**
	 * @return Returns the extraInformationTextStyleClass.
	 */
	public String getExtraInformationTextStyleClass() {
		return this.extraInformationTextStyleClass;
	}

	/**
	 * @param extraInformationTextStyleClass
	 *            The extraInformationTextStyleClass to set.
	 */
	public void setExtraInformationTextStyleClass(String extraInformationTextStyleClass) {
		this.extraInformationTextStyleClass = extraInformationTextStyleClass;
	}

	/**
	 * @return Returns the searchNameStyleClass.
	 */
	public String getSearchNameStyleClass() {
		return this.searchNameStyleClass;
	}

	/**
	 * @param searchNameStyleClass
	 *            The searchNameStyleClass to set.
	 */
	public void setSearchNameStyleClass(String searchNameStyleClass) {
		this.searchNameStyleClass = searchNameStyleClass;
	}

	/**
	 * @return Returns the iconStyleClass.
	 */
	public String getIconStyleClass() {
		return this.iconStyleClass;
	}

	/**
	 * @param iconStyleClass
	 *            The iconStyleClass to set.
	 */
	public void setIconStyleClass(String iconStyleClass) {
		this.iconStyleClass = iconStyleClass;
	}

	/**
	 * @return Returns the searchPluginsToUse.
	 */
	public String getSearchPluginsToUse() {
		return this.searchPluginsToUse;
	}

	/**
	 * Set a CVS list of the classnames of search plugins, e.g. WebCrawlerSearchPlugin, ContentSearch, etc.
	 * @param searchPluginsToUse
	 */
	public void setSearchPluginsToUse(String searchPluginsToUse) {
		this.searchPluginsToUse = searchPluginsToUse;
	}

	/**
	 * @return Returns the extraAttributeTextEvenStyleClass.
	 */
	public String getExtraAttributeTextEvenStyleClass() {
		return this.extraAttributeTextEvenStyleClass;
	}

	/**
	 * @param extraAttributeTextEvenStyleClass
	 *            The extraAttributeTextEvenStyleClass to set.
	 */
	public void setExtraAttributeTextEvenStyleClass(String extraAttributeEvenStyleClass) {
		this.extraAttributeTextEvenStyleClass = extraAttributeEvenStyleClass;
	}

	/**
	 * @return Returns the extraAttributeTextOddStyleClass.
	 */
	public String getExtraAttributeTextOddStyleClass() {
		return this.extraAttributeTextOddStyleClass;
	}

	/**
	 * @param extraAttributeTextOddStyleClass
	 *            The extraAttributeTextOddStyleClass to set.
	 */
	public void setExtraAttributeTextOddStyleClass(String extraAttributeOddStyleClass) {
		this.extraAttributeTextOddStyleClass = extraAttributeOddStyleClass;
	}
}