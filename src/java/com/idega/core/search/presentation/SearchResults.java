/*
 * $Id: SearchResults.java,v 1.1 2005/01/17 19:15:24 eiki Exp $
 * Created on Jan 17, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.search.presentation;

import java.util.Collection;
import java.util.Iterator;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.core.component.data.ICObjectHome;
import com.idega.core.search.business.Searchable;
import com.idega.data.IDOLookup;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;



/**
 *  Last modified: $Date: 2005/01/17 19:15:24 $ by $Author: eiki $
 * 
 * This block looks up all Searchable objects registered in bundles and sets up the search form (simple or advanced).<br>
 * To view the actual search results you must have a SearchResults block in the page you want to display the results.<br>
 * Use setInputParameterName if you want to have different searches on the same page, remember to set the same parameter<br>
 * for the SearchResults object.
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.1 $
 */
public class SearchResults extends Block {

	public static final String DEFAULT_STYLE_CLASS = "iw_search_results";
	public static final String DEFAULT_ROW_EVEN_STYLE_CLASS = "iw_search_results_row_even";
	public static final String DEFAULT_ROW_ODD_STYLE_CLASS = "iw_search_results_row_odd";
	public static final String DEFAULT_SEARCH_PARAMETER_NAME = Search.DEFAULT_SEARCH_PARAMETER_NAME;
	
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
	 * @param searchParameterName The searchParameterName to set.
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
	 * @param rowOddStyleClass The rowOddStyleClass to set.
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
	 * @param rowEvenStyleClass The rowEvenStyleClass to set.
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
	//	IWResourceBundle iwrb = iwc.getIWMainApplication().getCoreBundle().getResourceBundle(iwc);
		
		if(iwc.isParameterSet(getSearchParameterName())){
			
			
			
			Layer container = new Layer();
			container.setStyleClass(getStyleClass());
			
			if(iwc.isParameterSet(Search.DEFAULT_ADVANCED_SEARCH_PARAMETER_NAME)){
				//TODO advanced search
				container.add("Advanced search");
			}
			else{
				String queryString = iwc.getParameter(getSearchParameterName());
				
				container.add("Simple search");
				//todo remove all addbreak and implement styles
				container.addBreak();
				
				ICObjectHome icoHome = (ICObjectHome) IDOLookup.getHome(ICObject.class);
				ICObject obj;
				Collection coll = icoHome.findAllByObjectType(ICObjectBMPBean.COMPONENT_TYPE_SEARCHABLE);
				if (coll != null && !coll.isEmpty()) {
					Iterator iter = coll.iterator();
					while (iter.hasNext()) {
						obj = (ICObject) iter.next();
						
						Searchable searchable =  (Searchable)Class.forName(obj.getClassName()).newInstance();
						
						searchable.initialize(iwc);
						container.addBreak();
						container.add(searchable.getSearchName(iwc));
						container.addBreak();
						
						Collection results = searchable.getSimpleSearchResults(queryString,iwc);
						
						if(results!=null && !results.isEmpty()){
							
							Iterator iterator = results.iterator();
							while (iterator.hasNext()) {
								Object result = (Object) iterator.next();
								container.add(searchable.getResultPresentation(result,iwc));
								container.addBreak();
							}
							
						}
						
					}
					
				} 	
			}

			add(container);
		
		}
		
	}
		
}
