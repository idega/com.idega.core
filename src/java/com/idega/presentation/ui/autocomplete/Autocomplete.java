package com.idega.presentation.ui.autocomplete;

import com.idega.core.bean.AutocompleteBean;
import com.idega.presentation.IWContext;
import com.idega.presentation.IWUIBase;
import com.idega.util.PresentationUtil;

/**
 * Main class for autocomplete components.
 * @author alex
 *
 */
public abstract class Autocomplete extends IWUIBase{

	private String selectionFunction = null;
	private AutocompleteBean autocompleteBean = null;
	
	/**
	 * Set an instance of {@link AutocompleteBean} to manage queries of autocomplete.
	 * @param autocompleteBean
	 */
	public void setAutocompleteBean(AutocompleteBean autocompleteBean) {
		this.autocompleteBean = autocompleteBean;
	}

	public AutocompleteBean AutocompleteBeangetAutocompleteBean() {
		return autocompleteBean;
	}

	/**
	 * Set javascript function that will take care of 
	 * selected item of autocomplete
	 * @return
	 */
	public String getSelectionFunction() {
		return selectionFunction;
	}

	public void setSelectionFunction(String selectionFunction) {
		this.selectionFunction = selectionFunction;
	}

	@Override
	protected void addFiles(IWContext iwc){
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, getScripts());
		PresentationUtil.addStyleSheetsToHeader(iwc, getStyleSheets());
	}
	
}
