//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idega.builder.data.IBDomain;
import com.idega.builder.data.IBPage;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.file.business.ICFileSystem;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Script;
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
	
	private boolean _onClickConfirm = false;
	private String _confirmMessage;

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

	/**
	 * @see com.idega.presentation.PresentationObject#main(IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		if (_onClickConfirm) {
			Script script = getParentPage().getAssociatedScript();
			if (script != null)
				script = new Script();
					
			boolean addFunction = false;
					
			StringBuffer buffer = new StringBuffer();
			buffer.append("function onClickConfirm(message) {").append("\n\t");
			buffer.append("var submit = confirm(message);").append("\n\t");
			buffer.append("if (submit)").append("\n\t\t");
					
			if (_windowClassToOpen != null) {
				String URL = Window.getWindowURL(_windowClassToOpen, iwc) + getParameters();
				buffer.append(Window.getCallingScriptString(_windowClassToOpen, URL, true, iwc)).append(";\n");
				addFunction = true;
			}
			if (_pageID != -1) {
				buffer.append("window.location='"+getURLString(iwc, false)+"';").append("\n");
				addFunction = true;
			}
			if (_fileID != -1) {
				ICFileSystem fsystem = getICFileSystem(iwc);
				buffer.append(Window.getCallingScript(fsystem.getFileURI(_fileID))).append(";\n");
				addFunction = true;
			}

			buffer.append("}");
			if (addFunction) {
				setOnClick("javascript:onClickConfirm('"+_confirmMessage+"')");
				script.addFunction("onClickConfirm", buffer.toString());
				getParentPage().setAssociatedScript(script);
			}
		}
	}
	
	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals("HTML")) {
			if (asImageButton) {
				defaultImage = iwc.getApplication().getCoreBundle().getImageButton(getValue());
			}
			if (!_onClickConfirm) {
				if (_windowClassToOpen != null) {
					String URL = Window.getWindowURL(_windowClassToOpen, iwc) + getParameters();
					setOnClick("javascript:" + Window.getCallingScriptString(_windowClassToOpen, URL, true, iwc));
				}
				if (_pageID != -1) {
					setOnClick("javascript:window.location='"+getURLString(iwc, true)+"';");
				}
				if (_fileID != -1) {
					ICFileSystem fsystem = getICFileSystem(iwc);
					setOnClick("javascript:"+Window.getCallingScript(fsystem.getFileURI(_fileID)));	
				}
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

				IBDomain d = iwc.getDomain();
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
	
	private String getURLString(IWContext iwc, boolean convert) throws Exception{
		BuilderService bservice = getBuilderService(iwc);
		URLUtil url = new URLUtil(bservice.getPageURI(_pageID), convert);
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
	
	public void setOnClickConfirm(String confirmMessage) {
		_onClickConfirm = true;
		_confirmMessage = confirmMessage;
	}
}