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
		_size = collection.size();
	}

	/**
	 * 
	 */
	public CollectionNavigator(int size) {
		_size = size;
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
		navigationTable.setCellpadding(_padding);
		navigationTable.setCellspacing(0);
		navigationTable.setWidth(_width);
		navigationTable.setWidth(1, "33%");
		navigationTable.setWidth(2, "33%");
		navigationTable.setWidth(3, "33%");
		navigationTable.setAlignment(2, 1, Table.HORIZONTAL_ALIGN_CENTER);
		navigationTable.setAlignment(3, 1, Table.HORIZONTAL_ALIGN_RIGHT);

		Text prev = getText(localize("previous", "Previous"));
		Text next = getText(localize("next", "Next"));
		Text info = null;
		if (useShortText) {
			info = getText((_currentPage + 1) + "/" + (_maxPage + 1));
		}
		else {
			info = getText(localize("page", "Page") + " " + (_currentPage + 1) + " " + localize("of", "of") + " " + (_maxPage + 1));
		}
		if (_currentPage > 0) {
			Link lPrev = getLink(localize("previous", "Previous"));
			lPrev.addParameter(PARAMETER_CURRENT_PAGE, Integer.toString(_currentPage - 1));
			navigationTable.add(lPrev, 1, 1);
			lPrev.setToMaintainParameters(this.maintainedPrms);
		}
		else {
			navigationTable.add(prev, 1, 1);
		}
		navigationTable.add(info, 2, 1);

		if (_currentPage < _maxPage) {
			Link lNext = getLink(localize("next", "Next"));
			lNext.addParameter(PARAMETER_CURRENT_PAGE, Integer.toString(_currentPage + 1));
			navigationTable.add(lNext, 3, 1);
			lNext.setToMaintainParameters(maintainedPrms);
		}
		else {
			navigationTable.add(next, 3, 1);
		}
		
		add(navigationTable);
	}
	
	private Text getText(String string) {
		Text text = new Text(string);
		if (_textStyle != null) {
			text.setStyleClass(_textStyle);
		}
		return text;
	}
	
	private Link getLink(String string) {
		Link link = new Link(string);
		if (_linkStyle != null) {
			link.setStyleClass(_linkStyle);
		}
		if (_eventListener != null)
			link.setEventListener(_eventListener);
		return link;
	}
	
	private String localize(String key, String defaultValue) {
		return _iwrb.getLocalizedString(key, defaultValue);
	}
	
	private void parse(IWContext iwc) {
		if (iwc.isParameterSet(PARAMETER_CURRENT_PAGE))
			_currentPage = Integer.parseInt(iwc.getParameter(PARAMETER_CURRENT_PAGE));
	}
	
	private void initialize(IWContext iwc) {
		_iwrb = getResourceBundle(iwc);
		_maxPage = (int) Math.ceil(_size / _numberOfEntriesPerPage);
		if (_currentPage > _maxPage)
			_currentPage = 0;
		_start = _currentPage * _numberOfEntriesPerPage;
		
	}
	
	public void setEventListener(Class eventListener) {
		_eventListener = eventListener;
	}

	/**
	 * @return int
	 */
	public int getStart(IWContext iwc) {
		parse(iwc);
		initialize(iwc);
		return _start;
	}

	/**
	 * Sets the numberOfEntriesPerPage.
	 * @param numberOfEntriesPerPage The numberOfEntriesPerPage to set
	 */
	public void setNumberOfEntriesPerPage(int numberOfEntriesPerPage) {
		_numberOfEntriesPerPage = numberOfEntriesPerPage;
	}

	/**
	 * Sets the size.
	 * @param size The size to set
	 */
	public void setSize(int size) {
		_size = size;
	}
	/**
	 * Sets the linkStyle.
	 * @param linkStyle The linkStyle to set
	 */
	public void setLinkStyle(String linkStyle) {
		_linkStyle = linkStyle;
	}

	/**
	 * Sets the textStyle.
	 * @param textStyle The textStyle to set
	 */
	public void setTextStyle(String textStyle) {
		_textStyle = textStyle;
	}

	/**
	 * Sets the width.
	 * @param width The width to set
	 */
	public void setWidth(String width) {
		_width = width;
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
		_padding = padding;
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
}