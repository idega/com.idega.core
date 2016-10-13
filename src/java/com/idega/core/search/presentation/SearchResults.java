/*
 * $Id: SearchResults.java,v 1.24 2008/12/03 09:38:35 laddi Exp $ Created on Jan
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

import javax.faces.component.UIComponent;

import com.idega.core.idgenerator.business.UUIDGenerator;
import com.idega.core.search.business.Search;
import com.idega.core.search.business.SearchPlugin;
import com.idega.core.search.business.SearchPluginManager;
import com.idega.core.search.business.SearchQuery;
import com.idega.core.search.business.SearchResult;
import com.idega.core.search.data.AdvancedSearchQuery;
import com.idega.core.search.data.SimpleSearchQuery;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.CSSSpacer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.util.PresentationUtil;

/**
 * Last modified: $Date: 2008/12/03 09:38:35 $ by $Author: laddi $
 *
 * This block can use all SearchPlugin objects registered in bundles and sets up
 * the search results (simple by default or advanced) <br>
 * Use setInputParameterName if you want to have different searches on the same
 * page, remember to set the same parameter for the Searcher block.<br>
 * The look of the results are set via stylesheet classes defined in
 * iw_core.css.<br>
 * Do not change core_iw.css, rather add your own custom stylesheet after the<br>
 * iw_core.css is added and override the styles.<br>
 * This class can also be EXTENDED like e.g. WhatIsNew block does by overriding some of the methods of this class<br>
 *
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson </a>
 * @version $Revision: 1.24 $
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

	private String id;
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
	protected String searchPluginsToUse;
	protected String searchQueryString;
	protected boolean showAllResultProperties = false;
	protected boolean openLinksInAnotherWindow = false;
	protected boolean hideResultsLayer = false;
	protected boolean showDateColumn = false;

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
	 *          The searchParameterName to set.
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
	 *          The advancedSearchParameterName to set.
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
	 *          The rowOddStyleClass to set.
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
	 *          The rowEvenStyleClass to set.
	 */
	public void setRowEvenStyleClass(String inputStyleClass) {
		this.rowEvenStyleClass = inputStyleClass;
	}

	/**
	 * @return Returns the styleClass.
	 */
	@Override
	public String getStyleClass() {
		return this.styleClass;
	}

	public String getContainerID() {
		return this.id;
	}

	/**
	 * @param styleClass
	 *          The styleClass to set.
	 */
	@Override
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public void setContainerID(String id) {
		this.id = id;
	}

	/**
	 * You need a very special reason to use this class since it makes the result list disappear with the style attribute display:none;
	 * For example if you want to use the file list in javascript but don't want to display it.
	 * @param hideResultsLayer
	 */
	public void setToHideResultsLayer(boolean hideResultsLayer){
		this.hideResultsLayer = hideResultsLayer;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	@Override
	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		IWResourceBundle iwrb = IWContext.getInstance().getIWMainApplication().getCoreBundle().getResourceBundle(iwc);
		IWBundle iwb = IWContext.getInstance().getIWMainApplication().getCoreBundle();
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/search.css"));

		if (isSimpleSearch(iwc) || isAdvancedSearch(iwc)) {
			CSSSpacer spacer = new CSSSpacer();
			Layer container = new Layer();
			container.setStyleClass(getStyleClass());
			if (getContainerID() != null) {
				container.setID(getContainerID());
			}

			if(hideResultsLayer){
				container.setStyleAttribute("display","none");
			}

			beforeAddingResultRows(container);
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
			if (isAdvancedSearch(iwc)) {
				isAdvancedSearch = true;
				query = new AdvancedSearchQuery();
			}
			else {
				String queryString = getSearchQueryString(iwc);
				Map<String, String> queryMap = new HashMap<String, String>();
				queryMap.put(getSimpleSearchParameterName(), queryString);
				query = new SimpleSearchQuery(queryMap);
			}
			// /
			boolean noResult = true;
			Collection<SearchPlugin> plugins = SearchPluginManager.getInstance().getAllSearchPluginsInitialized(iwc.getIWMainApplication());
			if (!plugins.isEmpty()) {
				Iterator<SearchPlugin> iter = plugins.iterator();
				while (iter.hasNext()) {
					SearchPlugin searchPlugin = iter.next();
					searchPlugin = configureSearchPlugin(searchPlugin);

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


					//
					if ((isAdvancedSearch && searchPlugin.getSupportsAdvancedSearch())
							|| (!isAdvancedSearch && searchPlugin.getSupportsSimpleSearch())) {
						if (isAdvancedSearch && searchPlugin.getSupportsAdvancedSearch()) {
							fillAdvancedSearch(iwc, query, searchPlugin);
						}
						Search search = searchPlugin.createSearch(query);
						Collection<SearchResult> results = search == null ? null : search.getSearchResults();
						if (results != null && !results.isEmpty()) {
							noResult = false;
							if(!isSetToShowDateColumn()) {
								Text searchName = new Text(searchPlugin.getSearchName());
								searchName.setStyleClass(getSearchNameStyleClass());
								container.add(searchName);
							} else {
								Layer tableHeader = new Layer();
								tableHeader.setStyleClass(getSearchNameStyleClass());
								
								Text searchName = new Text(searchPlugin.getSearchName());
								tableHeader.add(searchName);
								
								Text dateColumnName = new Text(searchPlugin.getSearchDateColumnName());
								dateColumnName.setStyleClass(getSearchNameStyleClass() + "_date");
								tableHeader.add(dateColumnName);
								
								container.add(tableHeader);
							}
							int row = 1;
							Iterator<SearchResult> iterator = results.iterator();
							UUIDGenerator generator = UUIDGenerator.getInstance();
							while (iterator.hasNext()) {
								SearchResult result = iterator.next();
								String textOnLink = result.getSearchResultName();
								String uri = result.getSearchResultURI();
								String abstractText = result.getSearchResultAbstract();
								String extraInfo = result.getSearchResultExtraInformation();
								Map<String, Object> extraParameters = result.getSearchResultAttributes();
								// todo group by type
								String type = result.getSearchResultType();
								if (row % 2 == 0) {
									rowContainer = (Layer) evenRowProtoType.clone();
								}
								else {
									rowContainer = (Layer) oddRowProtoType.clone();
								}
								rowContainer.setId(generator.generateId());

								// add an extra optional style with the search
								// type suffix
								addSearchResultTypeStyleClass(rowContainer, type);
								// adding spacer to force the row container
								// around all floating elements, another one is
								// added at the end
								CSSSpacer space = (CSSSpacer) spacer.clone();
								space.setId(generator.generateId());
								rowContainer.add(space);
								// add an optional icon, use a background image
								// in the style
								Layer icon = (Layer) iconPrototype.clone();
								icon.setId(generator.generateId());

								addSearchResultTypeStyleClass(icon, type);
								rowContainer.add(icon);
								if (textOnLink != null) {
									Link link = (Link) linkProtoType.clone();
									link.setText(textOnLink);
									link.setId(generator.generateId());
									// add an extra optional style with the
									// search type suffix
									addSearchResultTypeStyleClass(link, type);
									if (uri != null) {
										link.setURL(uri);
									}
									if (openLinksInAnotherWindow) {
										link.setTarget(Link.TARGET_BLANK_WINDOW);
									}
									rowContainer.add(link);
								}
								if (extraInfo != null) {
									Text extraInfoText = (Text) extraInfoTextProtoType.clone();
									extraInfoText.setText(extraInfo);
									extraInfoText.setId(generator.generateId());
									// add an extra optional style with the
									// search type suffix
									addSearchResultTypeStyleClass(extraInfoText, type);
									rowContainer.add(extraInfoText);
								}
								if (abstractText != null) {
									Text abstractT = (Text) abstractTextProtoType.clone();
									abstractT.setText(abstractText);
									abstractT.setId(generator.generateId());
									// add an extra optional style with the
									// search type suffix
									addSearchResultTypeStyleClass(abstractT, type);
									rowContainer.add(abstractT);
								}

								if (extraParameters != null && !extraParameters.isEmpty() && isSetToShowAllResultProperties()) {
									int counter = 0;
									for (Iterator<String> keys = extraParameters.keySet().iterator(); keys.hasNext();) {
										counter++;
										String key = keys.next();
										String value = extraParameters.get(key).toString();
										Text extraParams;

										if (counter % 2 == 0) {
											extraParams = (Text)extraAttributeEvenProtoType.clone();
										}
										else {
											extraParams = (Text)extraAttributeOddProtoType.clone();
										}

										extraParams.setText(value);
										extraParams.setId(generator.generateId());
										addSearchResultTypeAndAttributeKeyStyleClass(extraParams, type, key);

										rowContainer.add(extraParams);
									}
								}

								// extraRowElements
								Collection<UIComponent> rowElements = searchPlugin.getExtraRowElements(result, iwrb);
								if (rowElements != null && !rowElements.isEmpty()) {
									Iterator<UIComponent> reiter = rowElements.iterator();
									while (reiter.hasNext()) {
										rowContainer.add(reiter.next());
									}
								}

								// adding date column
								Collection<UIComponent> dates = searchPlugin.getFileCreationDates(result, iwrb);
								if (dates != null && !dates.isEmpty()) {
									Iterator<UIComponent> reiter = dates.iterator();
									while (reiter.hasNext()) {
										rowContainer.add(reiter.next());
									}
								}
								
								// adding spacer to force the row container
								// around all floating elements
								CSSSpacer space2 = (CSSSpacer) spacer.clone();
								space2.setId(generator.generateId());
								rowContainer.add(space2);
								addResultRow(container, rowContainer, extraInfo);
								row++;
							}
						} else {
							getLogger().info("There were found no results by query: " + query + ". Search plugin: " + searchPlugin.getClass().getName());
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
			afterAddingResultRows(container);
			add(container);
		}
	}

	protected void beforeAddingResultRows(Layer container) {
	}

	protected void addResultRow(Layer container, Layer rowContainer, String rowKey) {
		container.add(rowContainer);
	}

	protected void afterAddingResultRows(Layer container) {
	}

	/**
	 * Allows subclasses to cast the search plugin to its true class and manipulate it.
	 * Remember this is a global plugin so clone it if you don't want to mess with other searches.
	 * @param searchPlugin
	 */
	protected SearchPlugin configureSearchPlugin(SearchPlugin searchPlugin) {
		return searchPlugin;
	}

	/**
	 * @param iwc
	 * @return
	 */
	protected String getSearchQueryString(IWContext iwc) {
		String query = iwc.getParameter(getSimpleSearchParameterName());
		if(query==null){
			query = searchQueryString;
		}
		return query;
	}

	/**
	 * @param iwc
	 * @return
	 */
	protected boolean isAdvancedSearch(IWContext iwc) {
		return iwc.isParameterSet(getAdvancedSearchParameterName());
	}

	/**
	 * @param iwc
	 * @return
	 */
	protected boolean isSimpleSearch(IWContext iwc) {
		return iwc.isParameterSet(getSimpleSearchParameterName());
	}

	/**
	 * @param iwc
	 * @param query
	 * @param searchPlugin
	 */
	protected void fillAdvancedSearch(IWContext iwc, SearchQuery query, SearchPlugin searchPlugin) {
		String queryString = iwc.getParameter(getSimpleSearchParameterName());
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put(getSimpleSearchParameterName(), queryString);
		List<String> params = searchPlugin.getAdvancedSearchSupportedParameters();
		if (params != null && !params.isEmpty()) {
			Iterator<String> iterator = params.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
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


	/**
	 * @param searchQueryString the searchQueryString to set
	 */
	public void setSearchQueryString(String searchQueryString) {
		this.searchQueryString = searchQueryString;
	}

	/**
	 * @return the showAllResultProperties
	 */
	public boolean isSetToShowAllResultProperties() {
		return showAllResultProperties;
	}

	/**
	 * If true then the result properties map of a result is added, each key and value pair gets its own layer with style class
	 * @param showAllResultProperties
	 */
	public void setToShowAllResultProperties(boolean showAllResultProperties) {
		this.showAllResultProperties = showAllResultProperties;
	}

	public boolean getOpenLinksInAnotherWindow() {
		return openLinksInAnotherWindow;
	}

	public void setOpenLinksInAnotherWindow(boolean openLinksInAnotherWindow) {
		this.openLinksInAnotherWindow = openLinksInAnotherWindow;
	}

	public boolean isSetToShowDateColumn() {
		return showDateColumn;
	}

	public void setToShowDateColumn(boolean showDateColumn) {
		this.showDateColumn = showDateColumn;
	}
	
}