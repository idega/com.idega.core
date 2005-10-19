/*
 * $Id: ListNavigator.java,v 1.2 2005/10/19 12:52:11 laddi Exp $
 * Created on Oct 12, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import com.idega.event.IWPageEventListener;
import com.idega.idegaweb.IWException;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;


/**
 * Last modified: $Date: 2005/10/19 12:52:11 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class ListNavigator extends Block implements IWPageEventListener {

	public static final String PARAMETER_CURRENT_PAGE = "ln_curr_page";
	public static final String PARAMETER_NUMBER_OF_ENTRIES = "ln_num_entries";
	public static final String PARAMETER_UNIQUE_IDENTIFIER = "ln_unique_id";

	private String iUniqueIdentifier;
	
	private int iSize = 0;
	private int iNumberOfEntriesPerPage = 10;
	
	public ListNavigator() {
		this("default", 0);
	}
	
	public ListNavigator(String uniqueIdentifier, int size) {
		iUniqueIdentifier = uniqueIdentifier;
		iSize = size;
	}
	
	public void main(IWContext iwc) throws Exception {
		Form form = new Form();
		form.setEventListener(ListNavigator.class);
		form.addParameter(PARAMETER_UNIQUE_IDENTIFIER, iUniqueIdentifier);
		
		Lists list = new Lists();
		form.add(list);
		
		int currentPage = getCurrentPage(iwc);
		int numberOfPages = iSize / getNumberOfEntriesPerPage(iwc);
		if (iSize % getNumberOfEntriesPerPage(iwc) != 0) {
			numberOfPages++;
		}
		if (numberOfPages == 0) {
			numberOfPages++;
		}
		int start = getStartFromCurrentPage(currentPage);
		int end = start + 9;
		if (end > numberOfPages) {
			end = numberOfPages;
		}
		if (currentPage > 1) {
			ListItem item = new ListItem();
			Link link = new Link("&lt;");
			link.addParameter(getCurrentPageParameter(), (currentPage - 1));
			link.addParameter(PARAMETER_UNIQUE_IDENTIFIER, iUniqueIdentifier);
			link.setEventListener(ListNavigator.class);
			
			item.add(link);
			list.add(item);
		}
		for (int i = start; i <= end; i++) {
			ListItem item = new ListItem();
			Link link = new Link(String.valueOf(i));
			if (i == currentPage) {
				link.setStyleClass("currentPage");
			}
			link.addParameter(getCurrentPageParameter(), i);
			link.addParameter(PARAMETER_UNIQUE_IDENTIFIER, iUniqueIdentifier);
			link.setEventListener(ListNavigator.class);
			
			item.add(link);
			list.add(item);
		}
		if (currentPage < end) {
			ListItem item = new ListItem();
			Link link = new Link("&gt;");
			link.addParameter(getCurrentPageParameter(), (currentPage + 1));
			link.addParameter(PARAMETER_UNIQUE_IDENTIFIER, iUniqueIdentifier);
			link.setEventListener(ListNavigator.class);
			
			item.add(link);
			list.add(item);
		}

		DropdownMenu menu = new DropdownMenu(getNumberOfEntriesParameter());
		menu.addMenuElement(5, "5 " + "");
		menu.addMenuElement(10, "10 " + "");
		menu.addMenuElement(20, "20 " + "");
		menu.addMenuElement(50, "50 " + "");
		menu.setSelectedElement(getNumberOfEntriesPerPage(iwc));
		menu.setToSubmit();
		form.add(menu);
		
		add(form);
	}
	
	private int getStartFromCurrentPage(int currentPage) {
		int start = currentPage - 9;
		if (start < 1) {
			start = 1;
		}
		return start;
	}
	
	private int getCurrentPage(IWContext iwc) {
		Integer currentPage = (Integer) iwc.getSessionAttribute(getCurrentPageParameter());
		if (currentPage != null) {
			return currentPage.intValue();
		}
		return 1;
	}
	
	public int getStartingEntry(IWContext iwc) {
		return (getCurrentPage(iwc) - 1) * getNumberOfEntriesPerPage(iwc);
	}
	
	public int getNumberOfEntriesPerPage(IWContext iwc) {
		Integer numberOfEntries = (Integer) iwc.getSessionAttribute(getNumberOfEntriesParameter());
		if (numberOfEntries != null) {
			return numberOfEntries.intValue();
		}
		return iNumberOfEntriesPerPage;
	}
	
	private String getCurrentPageParameter() {
		return PARAMETER_CURRENT_PAGE + "_" + iUniqueIdentifier;
	}

	private String getNumberOfEntriesParameter() {
		return PARAMETER_NUMBER_OF_ENTRIES + "_" + iUniqueIdentifier;
	}

	public boolean actionPerformed(IWContext iwc) throws IWException {
		iUniqueIdentifier = iwc.getParameter(PARAMETER_UNIQUE_IDENTIFIER);
		if (iwc.isParameterSet(getCurrentPageParameter())) {
			iwc.setSessionAttribute(getCurrentPageParameter(), new Integer(iwc.getParameter(getCurrentPageParameter())));
		}
		if (iwc.isParameterSet(getNumberOfEntriesParameter())) {
			iwc.setSessionAttribute(getNumberOfEntriesParameter(), new Integer(iwc.getParameter(getNumberOfEntriesParameter())));
			iwc.removeSessionAttribute(getCurrentPageParameter());
		}
		return true;
	}
	
	public void setSize(int size) {
		iSize = size;
	}
	
	public void setUniqueIdentifier(String identifier) {
		iUniqueIdentifier = identifier;
	}
}