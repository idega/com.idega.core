//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import java.io.*;
import java.util.*;

import com.idega.block.media.business.MediaBusiness;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.data.IBDomain;
import com.idega.builder.data.IBPage;
import com.idega.presentation.*;
import com.idega.util.URLUtil;
import com.idega.util.text.TextSoap;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class GenericButton extends GenericInput {

	private int _pageID = -1;
	private int _fileID = -1;
	private boolean asImageButton = false;
	private Image defaultImage;
	private final String buttonImageStyle = "cursor:hand;";
	private Class _windowClassToOpen;
	private Map parameterMap;

	public GenericButton() {
		this("untitled", "");
	}

	public GenericButton(String name, String value) {
		super();
		setName(name);
		setValue(value);
		setInputType(INPUT_TYPE_BUTTON);
	}

	public void setAsImageButton(boolean asImageButton) {
		this.asImageButton = asImageButton;
	}

	public void setButtonImage(Image image) {
		this.defaultImage = image;
	}

	private void setSource(String source) {
		setAttribute("src",source);
	}

	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals("HTML")) {
			if (asImageButton) {
				defaultImage = iwc.getApplication().getCoreBundle().getImageButton(getValue());
			}
			if (_windowClassToOpen != null) {
				String URL = Window.getWindowURL(_windowClassToOpen, iwc) + getParameters();
				setOnClick("javascript:" + Window.getCallingScriptString(_windowClassToOpen, URL, true, iwc));
			}
			if (_pageID != -1) {
				setOnClick("javascript:window.location='"+getURLString(iwc)+"';");
			}
			if (_fileID != -1) {
				setOnClick("javascript:"+Window.getCallingScript(MediaBusiness.getMediaURL(_fileID, iwc.getApplication())));	
			}
			
			getParentPage();

			if (defaultImage == null) {
				super.print(iwc);
			}
			else {
				String URL = defaultImage.getURL();
				if (URL == null) {
					URL = defaultImage.getMediaURL(iwc);
				}

				IBDomain d = BuilderLogic.getInstance().getCurrentDomain(iwc);
				if (d.getURL() != null) {
					if (URL.startsWith("/")) {
						String protocol;
						if (iwc.getRequest().isSecure()) {
							protocol = "https://";
						}
						else {
							protocol = "http://";
						}
						URL = protocol + d.getURL() + URL;
					}
				}

				defaultImage.setURL(URL);
				defaultImage.setAttributes(getAttributes());
				defaultImage.setStyleAttribute(buttonImageStyle);

				if (getInputType().equals(INPUT_TYPE_IMAGE)) {
					setSource(URL);
					super.print(iwc);
				}
				else
					print("<img " + defaultImage.getAttributeString() + " >");
			}
		}
	}

	public Object clone() {
		GenericButton obj = (GenericButton) super.clone();
		if (this.defaultImage != null) {
			obj.defaultImage = (Image) this.defaultImage.clone();
		}
		return obj;
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		//does nothing...
	}
	
	public void setWindowToOpen(Class windowClassToOpen) {
		_windowClassToOpen = windowClassToOpen;
	}
	
	public void setPageToOpen(int pageID) {
		_pageID = pageID;
	}
	
	public void setPageToOpen(IBPage page) {
		if (page != null && page.getID() != -1)
			setPageToOpen(page.getID());
	}
	
	public void addParameterToWindow(String name, String value) {
		if (parameterMap == null)
			parameterMap = new HashMap();
		parameterMap.put(name, value);
	}
	
	public void addParameterToWindow(String name, int value) {
		addParameterToWindow(name, String.valueOf(value));
	}
	
	public void addParameterToPage(String name, String value) {
		if (parameterMap == null)
			parameterMap = new HashMap();
		parameterMap.put(name, value);
	}
	
	public void addParameterToPage(String name, int value) {
		addParameterToPage(name, String.valueOf(value));
	}
	
	private String getParameters() {
		StringBuffer returnString = new StringBuffer();
		if (parameterMap != null) {
			Iterator iter = parameterMap.keySet().iterator();
			while (iter.hasNext()) {
				String name = (String) iter.next();
				String value = (String) parameterMap.get(name);
				if (name != null && value != null) {
					returnString.append("&");
					returnString.append(name);
					returnString.append("=");
					returnString.append(value);
				}
			}
		}
		return TextSoap.convertSpecialCharacters(returnString.toString());
	}
	
	private String getURLString(IWContext iwc) {
		URLUtil url = new URLUtil(BuilderLogic.getInstance().getIBPageURL(iwc, _pageID));
		if (parameterMap != null) {
			Iterator iter = parameterMap.keySet().iterator();
			while (iter.hasNext()) {
				String name = (String) iter.next();
				String value = (String) parameterMap.get(name);
				if (name != null && value != null) {
					url.addParameter(name, value);
				}
			}
		}
				
		return url.toString();
	}

	/**
	 * Sets the fileID for the ICFile to open on click.
	 * @param fileID 	The fileID to set
	 */
	public void setFileToOpen(int fileID) {
		_fileID = fileID;
	}

}