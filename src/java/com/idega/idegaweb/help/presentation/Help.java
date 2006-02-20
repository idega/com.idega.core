/*
 * $Id: Help.java,v 1.5 2006/02/20 11:04:35 laddi Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb.help.presentation;

import java.util.Locale;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.text.Link;

/**
 * A class to create help for any part of the idegaWeb system. Maintains a set
 * of helptext for each locale.......
 * 
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class Help extends Block {
	private static final String BUNDLE_IDENTIFIER = "com.idega.block.help";
	
	private static final String DEFAULT_HELP_TEXT = "hlp_default_text";
	private static final String DEFAULT_HELP_IMAGE = "/help/help.gif";
	private static final String DEFAULT_HELP_KEY = "hlp_";

	private static final String CORE_BUNDLE = "com.idega.core";
	public static final String HELP_KEY = "hlp_key";
	public static final String HELP_BUNDLE = "hlp_bundle";

	protected String _helpTextKey = null;
	protected String _helpTextBundle = null;

	protected boolean _showAsText = false;
	protected boolean _showInNewWindow = true;
	protected Link _helpLink = null;

	private IWBundle _iwb = null;
	private IWBundle _iwbCore = null;
	private IWResourceBundle _iwrbCore = null;
	
	private Image image = null;

	public Help() {
		_helpLink = new Link();
	}
	
	public void main(IWContext iwc) throws Exception {
		this.empty();
		if (_iwbCore == null) {
			_iwbCore = iwc.getIWMainApplication().getBundle(CORE_BUNDLE);
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
				if (!_helpLink.isLabelSet()) {
					image = _iwrbCore.getImage(DEFAULT_HELP_IMAGE);
					image.setAlignment(Image.ALIGNMENT_ABSOLUTE_MIDDLE);
					_helpLink.setImage(image);
				}
			}
			else {
				if(image != null) {
					image.setAlignment(Image.ALIGNMENT_ABSOLUTE_MIDDLE);
					_helpLink.setImage(image);
				}
				else {
					image = _iwrbCore.getImage(DEFAULT_HELP_IMAGE);
					image.setAlignment(Image.ALIGNMENT_ABSOLUTE_MIDDLE);
					_helpLink.setImage(image);
				}
			}			
		}

		if (_showInNewWindow) {
			_helpLink.setWindowToOpen(HelpWindow.class);
		}

		if (_helpTextKey != null)
			_helpLink.addParameter(HELP_KEY,_helpTextKey);
			
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
		this.image = image;
		_helpLink.setImage(image);
	}
	
	public void setImage(String url){
		setImage(new Image(url));
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

	/**
	 * @see com.idega.presentation.PresentationObject#getBundleIdentifier()
	 */
	public String getBundleIdentifier() {
		if (_helpTextBundle != null && !_helpTextBundle.equals(""))
			return _helpTextBundle;
			
		return BUNDLE_IDENTIFIER;
	}
}