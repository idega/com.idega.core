/*
 * $Id:$
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.help;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.text.Link;
import com.idega.util.FileUtil;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLCDATA;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLOutput;
import com.idega.xml.XMLParser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * A class to create help for any part of the idegaWeb system. Maintains a set
 * of helptext for each locale.......
 * 
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class Help extends PresentationObjectContainer {
	private static final String DEFAULT_HELP_TEXT = "hlp_default_text";
	private static final String DEFAULT_HELP_IMAGE = "/help/help.gif";
	private static final String DEFAULT_HELP_KEY = "hlp_";
	private static final String CORE_BUNDLE = "com.idega.core";
	public static final String HELP_KEY = "hlp_key";
	public static final String HELP_BUNDLE = "hlp_bundle";

	protected String _helpTextKey = null;
	protected String _helpTextBundle = null;

	protected boolean _showAsText = true; //false;
	protected boolean _showInNewWindow = true;
	protected Link _helpLink = null;

	private IWBundle _iwb = null;
	private IWBundle _iwbCore = null;
	private IWResourceBundle _iwrbCore = null;

	public Help() {
		_helpLink = new Link();
	}
	
	public void main(IWContext iwc) throws Exception {
		if (_iwbCore == null) {
			_iwbCore = iwc.getApplication().getBundle(CORE_BUNDLE);
			_iwrbCore = _iwbCore.getResourceBundle(iwc);
		}

		if (_showAsText) {
			if (_helpLink.isText()) {
				if (!_helpLink.isLabelSet()) {
					_helpLink.setText(_iwrbCore.getLocalizedString(DEFAULT_HELP_TEXT, "Help"));
				}
			}
			else {
				_helpLink.setText(_iwrbCore.getLocalizedString(DEFAULT_HELP_TEXT, "Help"));
			}
		}
		else {
			if (_helpLink.isImage()) {
				if (!_helpLink.isLabelSet())
					_helpLink.setImage(_iwrbCore.getImage(DEFAULT_HELP_IMAGE));				
			}
			else {
				_helpLink.setImage(_iwrbCore.getImage(DEFAULT_HELP_IMAGE));
			}			
		}

		if (_showInNewWindow) {
			_helpLink.setWindowToOpen(HelpWindow.class);
		}

		if (_helpTextKey != null)
			_helpLink.addParameter(HELP_KEY,_helpTextKey);
//		else
//			_helpLink.addParameter(HELP_KEY,DEFAULT_HELP_KEY);
			
		if (_helpTextBundle != null)
			_helpLink.addParameter(HELP_BUNDLE,_helpTextBundle);
			
		add(_helpLink);	
	}

	public void setShowAsText(boolean showAsText) {
		_showAsText = showAsText;
	}

	public boolean getShowAsText() {
		return _showAsText;
	}

	public void setLocalizedLinkText(String localeString, String text) {
		_helpLink.setLocalizedText(localeString, text);
	}

	public void setLocalizedLinkText(int icLocaleID, String text) {
		_helpLink.setLocalizedText(icLocaleID, text);
	}

	public void setLocalizedLinkText(Locale locale, String text) {
		_helpLink.setLocalizedText(locale, text);
	}

	public String getLocalizedLinkText(IWContext iwc) {
		return _helpLink.getLocalizedText(iwc);
	}

	public void setLinkText(String linkText) {
		_helpLink.setText(linkText);
	}

	public String getLinkText() {
		return _helpLink.getText();
	}

	public void setLocalizedImage(String localeString, int imageID) {
		_helpLink.setLocalizedImage(localeString, imageID);
	}

	public void setLocalizedImage(Locale locale, int imageID) {
		_helpLink.setLocalizedImage(locale, imageID);
	}
	
	public void setImage(Image image) {
		_helpLink.setImage(image);
	}
	
	public void setImageId(int imageId) {
		_helpLink.setImageId(imageId);
	}
	
//	public void setShowTextInNewWindow(boolean showInNewWindow) {
//		_showInNewWindow = showInNewWindow;
//	}
//
//	public boolean getShowTextInNewWindow() {
//		return _showInNewWindow;
//	}
	
	public Object clone(IWUserContext iwc, boolean askForPermission) {
		Help obj = null;
		try {
			obj = (Help) super.clone(iwc, askForPermission);

			obj._helpTextKey = _helpTextKey;

			obj._showAsText = _showAsText;
			obj._showInNewWindow = _showInNewWindow;
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}	
	
	public String getHelpTextKey() {
		return _helpTextKey;	
	}
	
	public void setHelpTextKey(String key) {
		_helpTextKey = key;
	}	
	
	public String getHelpTextBundle() {
		return _helpTextBundle;	
	}
	
	public void setHelpTextBundle(String bundleString) {
		_helpTextBundle = bundleString;	
	}
}