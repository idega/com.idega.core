/*
 * $Id: ListNavigator.java,v 1.6 2009/02/02 13:42:35 donatas Exp $
 * Created on Oct 12, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import javax.faces.component.UIComponent;

import com.idega.event.IWPageEventListener;
import com.idega.idegaweb.IWException;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;


/**
 * Last modified: $Date: 2009/02/02 13:42:35 $ by $Author: donatas $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.6 $
 */
public class ListNavigator extends Block implements IWPageEventListener {

	public static final String PARAMETER_CURRENT_PAGE = "ln_curr_page";
	public static final String PARAMETER_NUMBER_OF_ENTRIES = "ln_num_entries";
	public static final String PARAMETER_UNIQUE_IDENTIFIER = "ln_unique_id";
	
	private String firstItemText;
	private String dropdownEntryName = "";

	private String iUniqueIdentifier;
	
	private int iSize = 0;
	private int iNumberOfEntriesPerPage = 10;
	
	private String navigationFunction;
	private String dropdownFunction;
	
	private int currentPage = 1;
	private int pageSize = -1;
	
	public ListNavigator() {
		this("default", 0);
	}
	
	public ListNavigator(String uniqueIdentifier, int size) {
		this.iUniqueIdentifier = uniqueIdentifier;
		this.iSize = size;
	}
	
	@Override
	public void main(IWContext iwc) throws Exception {
		UIComponent container = null;
		if (navigationFunction == null) {
			container = new Form();
			((Form)container).setEventListener(ListNavigator.class);
			((Form)container).addParameter(PARAMETER_UNIQUE_IDENTIFIER, this.iUniqueIdentifier);
		} else {
			container = new Layer();
		}
		
		Lists list = new Lists();
		container.getChildren().add(list);
		
		if (this.firstItemText != null) {
			ListItem item = new ListItem();
			item.add(new Text(this.firstItemText));
			list.add(item);
		}
		
		int currentPage = getCurrentPage(iwc);
		int numberOfPages = this.iSize / getNumberOfEntriesPerPage(iwc);
		if (this.iSize % getNumberOfEntriesPerPage(iwc) != 0) {
			numberOfPages++;
		}
		if (numberOfPages == 0) {
			numberOfPages++;
		}
		int start = getStartFromCurrentPage(currentPage, numberOfPages);
		int end = start + 9;
		if (end > numberOfPages) {
			end = numberOfPages;
		}
		if (currentPage > 1) {
			ListItem item = new ListItem();
			Link link = new Link("&lt;");
			link.setStyleClass("listNavigatorPager");
			if (navigationFunction != null) {
				link.getId();
				link.setURL("javascript:void(0);");
				link.setOnClick(getNavigationFunction(currentPage - 1));
			} else {
				link.addParameter(getCurrentPageParameter(), (currentPage - 1));
				link.addParameter(PARAMETER_UNIQUE_IDENTIFIER, this.iUniqueIdentifier);
				link.setEventListener(ListNavigator.class);
			}
			item.add(link);
			list.add(item);
		}
		for (int i = start; i <= end; i++) {
			ListItem item = new ListItem();
			Link link = new Link(String.valueOf(i));
			link.setStyleClass("listNavigatorPager");
			if (i == currentPage) {
				link.setStyleClass("currentPage");
			}
			
			if (navigationFunction != null) {
				link.getId();
				link.setURL("javascript:void(0);");
				link.setOnClick(getNavigationFunction(i));
			} else {
				link.addParameter(getCurrentPageParameter(), i);
				link.addParameter(PARAMETER_UNIQUE_IDENTIFIER, this.iUniqueIdentifier);
				link.setEventListener(ListNavigator.class);
			}
			item.add(link);
			list.add(item);
		}
		if (currentPage < numberOfPages) {
			ListItem item = new ListItem();
			Link link = new Link("&gt;");
			link.setStyleClass("listNavigatorPager");
			if (navigationFunction != null) {
				link.getId();
				link.setURL("javascript:void(0);");
				link.setOnClick(getNavigationFunction(currentPage + 1));
			} else {
				link.addParameter(getCurrentPageParameter(), (currentPage + 1));
				link.addParameter(PARAMETER_UNIQUE_IDENTIFIER, this.iUniqueIdentifier);
				link.setEventListener(ListNavigator.class);
			}
			
			item.add(link);
			list.add(item);
		}

		DropdownMenu menu = new DropdownMenu(getNumberOfEntriesParameter());
		menu.setStyleClass("listPagerSize");
		menu.addMenuElement(5, "5 " + this.dropdownEntryName);
		menu.addMenuElement(10, "10 " + this.dropdownEntryName);
		menu.addMenuElement(20, "20 " + this.dropdownEntryName);
		menu.addMenuElement(50, "50 " + this.dropdownEntryName);
		menu.setSelectedElement(getNumberOfEntriesPerPage(iwc));
		if (dropdownFunction != null) {
			menu.getId();
			menu.setOnChange(dropdownFunction);
		} else {
			menu.setToSubmit();
		}
		container.getChildren().add(menu);
		
		add(container);
	}
	
	private int getStartFromCurrentPage(int currentPage, int pageCount) {
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
		return this.currentPage > 0 ? this.currentPage : 1;
	}
	
	public int getStartingEntry(IWContext iwc) {
		return (getCurrentPage(iwc) - 1) * getNumberOfEntriesPerPage(iwc);
	}
	
	public int getNumberOfEntriesPerPage(IWContext iwc) {
		if (pageSize > 0) {
			return pageSize;
		}
		Integer numberOfEntries = (Integer) iwc.getSessionAttribute(getNumberOfEntriesParameter());
		if (numberOfEntries != null) {
			return numberOfEntries.intValue();
		}
		return this.iNumberOfEntriesPerPage;
	}
	
	private String getCurrentPageParameter() {
		return PARAMETER_CURRENT_PAGE + "_" + this.iUniqueIdentifier;
	}

	private String getNumberOfEntriesParameter() {
		return PARAMETER_NUMBER_OF_ENTRIES + "_" + this.iUniqueIdentifier;
	}

	public boolean actionPerformed(IWContext iwc) throws IWException {
		this.iUniqueIdentifier = iwc.getParameter(PARAMETER_UNIQUE_IDENTIFIER);
		if (iwc.isParameterSet(getCurrentPageParameter())) {
			iwc.setSessionAttribute(getCurrentPageParameter(), new Integer(iwc.getParameter(getCurrentPageParameter())));
		}
		if (iwc.isParameterSet(getNumberOfEntriesParameter())) {
			iwc.setSessionAttribute(getNumberOfEntriesParameter(), new Integer(iwc.getParameter(getNumberOfEntriesParameter())));
			iwc.removeSessionAttribute(getCurrentPageParameter());
		}
		return true;
	}
	
	private String getNavigationFunction(int page) {
		return navigationFunction.replace("#PAGE#", String.valueOf(page));
	}
	
	public void setSize(int size) {
		this.iSize = size;
	}
	
	public void setUniqueIdentifier(String identifier) {
		this.iUniqueIdentifier = identifier;
	}

	public void setDropdownEntryName(String dropdownEntryName) {
		this.dropdownEntryName = dropdownEntryName;
	}
	
	public void setFirstItemText(String firstItemText) {
		this.firstItemText = firstItemText;
	}
	
	public void setNumberOfEntriesPerPage(int numberOfEntriesPerPage) {
		this.iNumberOfEntriesPerPage = numberOfEntriesPerPage;
	}

	public String getNavigationFunction() {
		return navigationFunction;
	}

	public void setNavigationFunction(String navigationFunction) {
		this.navigationFunction = navigationFunction;
	}

	public String getDropdownFunction() {
		return dropdownFunction;
	}

	public void setDropdownFunction(String dropdownFunction) {
		this.dropdownFunction = dropdownFunction;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}