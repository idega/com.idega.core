package com.idega.presentation;

import java.util.ArrayList;
import java.util.Collection;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;

/**
 * @author laddi
 */
public class CollectionNavigator extends Block {

	public static final String PARAMETER_CURRENT_PAGE = "cn_current_page";
	
	private int _currentPage = 0;
	private int _maxPage = 0;
	private int _start = 0;
	private int _size = 0;
	private int _numberOfEntriesPerPage = 0;
	
	private int _padding = 0;
	
	private String uniqueIdentifier = "";
	
	private java.util.List maintainedPrms = new ArrayList();
	
	private IWResourceBundle _iwrb;

	private String _linkStyle;
	private String _textStyle;
	private String _width = Table.HUNDRED_PERCENT;
	
	private Class _eventListener;
	private boolean useShortText = false;

	/**
	 * 
	 */
	public CollectionNavigator(Collection collection) {
		this._size = collection.size();
	}

	/**
	 * 
	 */
	public CollectionNavigator(int size) {
		this._size = size;
	}

	/**
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		parse(iwc);
		initialize(iwc);
		drawNavigator(iwc);
	}
	
	private void drawNavigator(IWContext iwc) {
		Table navigationTable = new Table(3, 1);
		navigationTable.setCellpadding(this._padding);
		navigationTable.setCellspacing(0);
		navigationTable.setWidth(this._width);
		navigationTable.setWidth(1, "33%");
		navigationTable.setWidth(2, "33%");
		navigationTable.setWidth(3, "33%");
		navigationTable.setAlignment(2, 1, Table.HORIZONTAL_ALIGN_CENTER);
		navigationTable.setAlignment(3, 1, Table.HORIZONTAL_ALIGN_RIGHT);

		Text prev = getText(localize("previous", "Previous"));
		Text next = getText(localize("next", "Next"));
		Text info = null;
		if (this.useShortText) {
			info = getText((this._currentPage + 1) + "/" + (this._maxPage + 1));
		}
		else {
			info = getText(localize("page", "Page") + " " + (this._currentPage + 1) + " " + localize("of", "of") + " " + (this._maxPage + 1));
		}
		if (this._currentPage > 0) {
			Link lPrev = getLink(localize("previous", "Previous"));
			lPrev.addParameter(getUniqueParameterName(), Integer.toString(this._currentPage - 1));
			navigationTable.add(lPrev, 1, 1);
			lPrev.setToMaintainParameters(this.maintainedPrms);
		}
		else {
			navigationTable.add(prev, 1, 1);
		}
		navigationTable.add(info, 2, 1);

		if (this._currentPage < this._maxPage) {
			Link lNext = getLink(localize("next", "Next"));
			lNext.addParameter(getUniqueParameterName(), Integer.toString(this._currentPage + 1));
			navigationTable.add(lNext, 3, 1);
			lNext.setToMaintainParameters(this.maintainedPrms);
		}
		else {
			navigationTable.add(next, 3, 1);
		}
		
		add(navigationTable);
	}
	
	private Text getText(String string) {
		Text text = new Text(string);
		if (this._textStyle != null) {
			text.setStyleClass(this._textStyle);
		}
		return text;
	}
	
	private Link getLink(String string) {
		Link link = new Link(string);
		if (this._linkStyle != null) {
			link.setStyleClass(this._linkStyle);
		}
		if (this._eventListener != null) {
			link.setEventListener(this._eventListener);
		}
		return link;
	}
	
	private String localize(String key, String defaultValue) {
		return this._iwrb.getLocalizedString(key, defaultValue);
	}
	
	private void parse(IWContext iwc) {
		if (iwc.isParameterSet(getUniqueParameterName())) {
			this._currentPage = Integer.parseInt(iwc.getParameter(getUniqueParameterName()));
		}
	}
	
	private void initialize(IWContext iwc) {
		this._iwrb = getResourceBundle(iwc);
		this._maxPage = (int) Math.ceil(this._size / this._numberOfEntriesPerPage);
		if (this._currentPage > this._maxPage) {
			this._currentPage = 0;
		}
		this._start = this._currentPage * this._numberOfEntriesPerPage;
		
	}
	
	public void setEventListener(Class eventListener) {
		this._eventListener = eventListener;
	}

	/**
	 * @return int
	 */
	public int getStart(IWContext iwc) {
		parse(iwc);
		initialize(iwc);
		return this._start;
	}

	/**
	 * Sets the numberOfEntriesPerPage.
	 * @param numberOfEntriesPerPage The numberOfEntriesPerPage to set
	 */
	public void setNumberOfEntriesPerPage(int numberOfEntriesPerPage) {
		this._numberOfEntriesPerPage = numberOfEntriesPerPage;
	}

	/**
	 * Sets the size.
	 * @param size The size to set
	 */
	public void setSize(int size) {
		this._size = size;
	}
	/**
	 * Sets the linkStyle.
	 * @param linkStyle The linkStyle to set
	 */
	public void setLinkStyle(String linkStyle) {
		this._linkStyle = linkStyle;
	}

	/**
	 * Sets the textStyle.
	 * @param textStyle The textStyle to set
	 */
	public void setTextStyle(String textStyle) {
		this._textStyle = textStyle;
	}

	/**
	 * Sets the width.
	 * @param width The width to set
	 */
	public void setWidth(String width) {
		this._width = width;
	}

	/**
	 * Sets the width.
	 * @param width The width to set
	 */
	public void setWidth(int width) {
		setWidth(String.valueOf(width));
	}
	/**
	 * @return String
	 */
	public static String getParameterName() {
		return PARAMETER_CURRENT_PAGE;
	}

	/**
	 * Sets the padding.
	 * @param padding The padding to set
	 */
	public void setPadding(int padding) {
		this._padding = padding;
	}
	/**
	 * Adds a parameter name to maintain if exists in request
	 * @param prm
	 */
	public void addMaintainParameter(String prm){
		this.maintainedPrms.add(prm);
	}

	/**
	 * @param useShortText The useShortText to set.
	 */
	public void setUseShortText(boolean useShortText) {
		this.useShortText = useShortText;
	}
	
	/**
	 * To identify two different instances of the navigator in the same view
	 * @param identifier
	 */
	public void setIdentifier(String identifier){
	    	this.uniqueIdentifier = identifier;
	}
	
	private String getUniqueParameterName(){
	    return PARAMETER_CURRENT_PAGE + this.uniqueIdentifier;
	}
}