/*
 * $Id: Searcher.java,v 1.2 2005/01/19 10:06:13 eiki Exp $
 * Created on Jan 17, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.search.presentation;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;



/**
 *  Last modified: $Date: 2005/01/19 10:06:13 $ by $Author: eiki $
 * 
 * This block can use all SearchPlugin objects registered in bundles and sets up the search form (simple by default or advanced).<br>
 * To view the actual search results you must have a SearchResults block on the page you want to display the results.<br>
 * Use setInputParameterName if you want to have different searches on the same page, remember to set the same parameter<br>
 * for the SearchResults object.
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.2 $
 */
public class Searcher extends Block {

	public static final String DEFAULT_STYLE_CLASS = "iw_searcher";
	public static final String DEFAULT_INPUT_STYLE_CLASS = "iw_searcher_input";
	public static final String DEFAULT_BUTTON_STYLE_CLASS = "iw_searcher_button";
	public static final String DEFAULT_SEARCH_PARAMETER_NAME = "iw_search";
	public static final String DEFAULT_ADVANCED_SEARCH_PARAMETER_NAME = "iw_search_adv";
	public static final String BUTTON_LOCALIZATION_KEY = "searcher.button";
	
	private String styleClass = DEFAULT_STYLE_CLASS;
	private String inputStyleClass = DEFAULT_INPUT_STYLE_CLASS;
	private String buttonStyleClass = DEFAULT_BUTTON_STYLE_CLASS;
	private String searchParameterName = DEFAULT_SEARCH_PARAMETER_NAME;
	private String resultsPageURI = null;
	private String targetFrame = null;
	
	private boolean isAdvancedSearch = false;
	
	
	public Searcher() {
		super();
	}
	
	/**
	 * @return Returns the targetFrame.
	 */
	public String getTargetFrame() {
		return targetFrame;
	}
	/**
	 * @param targetFrame The targetFrame to set.
	 */
	public void setTargetFrame(String targetFrame) {
		this.targetFrame = targetFrame;
	}

	/**
	 * @return Returns the searchParameterName.
	 */
	public String getSearchParameterName() {
		return searchParameterName;
	}
	/**
	 * @param searchParameterName The searchParameterName to set.
	 */
	public void setSearchParameterName(String searchParameterName) {
		this.searchParameterName = searchParameterName;
	}
	/**
	 * @return Returns the buttonStyleClass.
	 */
	public String getButtonStyleClass() {
		return buttonStyleClass;
	}
	/**
	 * @param buttonStyleClass The buttonStyleClass to set.
	 */
	public void setButtonStyleClass(String buttonStyleClass) {
		this.buttonStyleClass = buttonStyleClass;
	}
	/**
	 * @return Returns the inputStyleClass.
	 */
	public String getInputStyleClass() {
		return inputStyleClass;
	}
	/**
	 * @param inputStyleClass The inputStyleClass to set.
	 */
	public void setInputStyleClass(String inputStyleClass) {
		this.inputStyleClass = inputStyleClass;
	}
	/**
	 * @return Returns the styleClass.
	 */
	public String getStyleClass() {
		return styleClass;
	}
	/**
	 * @param styleClass The styleClass to set.
	 */
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		IWResourceBundle iwrb = iwc.getIWMainApplication().getCoreBundle().getResourceBundle(iwc);
		String oldSimpleQuery = iwc.getParameter(getSearchParameterName());
		
		Layer container = new Layer();
		container.setStyleClass(getStyleClass());
		
		Form searchForm = null;
		if(resultsPageURI!=null){
			searchForm = new Form(resultsPageURI);
		}
		else{
			searchForm = new Form();
		}
		
		if(targetFrame!=null){
			searchForm.setTarget(targetFrame);
		}
		
		container.add(searchForm);
		
		//simple search
		if(!isAdvancedSearch()){
			TextInput input = new TextInput(searchParameterName);
			input.setStyleClass(inputStyleClass);
			
			if(oldSimpleQuery!=null && !oldSimpleQuery.equals("advanced")){
				input.setContent(oldSimpleQuery);
			}
			searchForm.add(input);
			
			SubmitButton button = new SubmitButton(iwrb.getLocalizedString(BUTTON_LOCALIZATION_KEY,"Search"));
			button.setStyleClass(buttonStyleClass);
			searchForm.add(button);
		}
		else{
			//TODO get registered advanced search interfaces through Searchable implementors
			searchForm.add(new HiddenInput(getSearchParameterName(),"advanced"));
			searchForm.add(new HiddenInput(DEFAULT_ADVANCED_SEARCH_PARAMETER_NAME,"true"));
			SubmitButton button = new SubmitButton(iwrb.getLocalizedString(BUTTON_LOCALIZATION_KEY,"Search"));
			button.setStyleClass(buttonStyleClass);
			searchForm.add(button);
		}
		
		add(container);
		
	}
	/**
	 * @return Returns the resultsPageURI.
	 */
	public String getResultsPageURI() {
		return resultsPageURI;
	}
	/**
	 * @param resultsPageURI The resultsPageURI to set.
	 */
	public void setResultsPageURI(String resultsPageURI) {
		this.resultsPageURI = resultsPageURI;
	}
	
	/**
	 * @return Returns the isAdvancedSearch.
	 */
	public boolean isAdvancedSearch() {
		return isAdvancedSearch;
	}
	/**
	 * @param isAdvancedSearch The isAdvancedSearch to set.
	 */
	public void setAsAdvancedSearch(boolean isAdvancedSearch) {
		this.isAdvancedSearch = isAdvancedSearch;
	}

}
