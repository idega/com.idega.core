//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.file.business.ICFileSystem;
import com.idega.idegaweb.IWConstants;
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
	private int _parentPageID = -1;
	
	private Class classToInstanciate;
	private Class templatePageClass;
	private String templateForObjectInstanciation;

	private String _URL;

	public GenericButton() {
		this("untitled", "");
	}

	public GenericButton(String name, String value) {
		super();
		setName(name);
		setValue(value);
		setInputType(INPUT_TYPE_BUTTON);
	}
	
	public GenericButton(String content) {
		this();
		setContent(content);
	}


	public void setAsImageButton(boolean asImageButton) {
		this.asImageButton = asImageButton;
	}

	public void setButtonImage(Image image) {
		this.defaultImage = image;
	}

	private void setSource(String source) {
		setMarkupAttribute("src",source);
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
				buffer.append("window.location='"+getURLString(iwc, _pageID, false)+"';").append("\n");
				addFunction = true;
			}
			if (_parentPageID != -1) {
				buffer.append("window.location='"+getURLString(iwc, _parentPageID, false)+"';").append("\n");
				addFunction = true;
			}
			if (_fileID != -1) {
				ICFileSystem fsystem = getICFileSystem(iwc);
				buffer.append(Window.getCallingScript(fsystem.getFileURI(_fileID))).append(";\n");
				addFunction = true;
			}
			if(classToInstanciate!=null){
				buffer.append("location='"+getURIToClassToInstanciate(iwc)+"';").append("\n");
				addFunction = true;
			}
			if(_URL != null){
				buffer.append(Window.getWindowCallingScript(_URL, "_blank", true, true, true, true, true, true, true, true, false, 640, 480)).append("\n");
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
		if (getMarkupLanguage().equals("HTML")) {
			if (asImageButton) {
				defaultImage = iwc.getIWMainApplication().getCoreBundle().getImageButton(getValueAsString());
			}
			if (!_onClickConfirm) {
				if (_windowClassToOpen != null) {
					String URL = Window.getWindowURL(_windowClassToOpen, iwc) + getParameters();
					setOnClick("javascript:" + Window.getCallingScriptString(_windowClassToOpen, URL, true, iwc));
				}
				if (_pageID != -1) {
					setOnClick("javascript:window.location='"+getURLString(iwc, _pageID, true)+"';");
				}
				if (_parentPageID != -1) {
					setOnClick("javascript:window.opener.location='"+getURLString(iwc, _parentPageID, true)+"';window.close();");
				}
				if (_fileID != -1) {
					ICFileSystem fsystem = getICFileSystem(iwc);
					setOnClick("javascript:"+Window.getCallingScript(fsystem.getFileURI(_fileID)));	
				}
				if (classToInstanciate != null) {
					setOnClick("javascript:location='"+getURIToClassToInstanciate(iwc)+"';");
				}
				if (_URL != null) {
					setOnClick("javascript:"+Window.getWindowCallingScript(_URL, "_blank", true, true, true, true, true, true, true, true, false, 640, 480));
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

				ICDomain d = iwc.getDomain();
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
				defaultImage.addMarkupAttributes(getMarkupAttributes());
				defaultImage.setStyleAttribute(buttonImageStyle);
				defaultImage.setName(getName());

				if (getInputType().equals(INPUT_TYPE_IMAGE)) {
					setSource(URL);
					super.print(iwc);
				}
				else
					print("<img " + defaultImage.getMarkupAttributesString() + " >");
			}
		} 
		else if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_WML)) {
			if(normalPrintSequence()) {
				printWML(iwc);
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
	
	public void setPageToOpen(ICPage page) {
		if (page != null && page.getID() != -1)
			setPageToOpen(page.getID());
	}
	
	public void setParentPageToOpen(int pageID) {
		_parentPageID = pageID;
	}
	
	public void setParentPageToOpen(ICPage page) {
		if (page != null && page.getID() != -1)
			setParentPageToOpen(page.getID());
	}
	
	public void setURLToOpen(String URL) {
		_URL = URL;
	}
	
	public void addParameter(String name, String value) {
		if (parameterMap == null)
			parameterMap = new HashMap();
		parameterMap.put(name, value);
	}
	
	/**
	 * Adds a whole map of parameters to the button
	 * @param parameterMap 
	 */
	public void addParameters(Map prmMap){
	    if (parameterMap == null)
			parameterMap = new HashMap();
		parameterMap.putAll(prmMap);
	}
	
	public void addParameter(String name, int value) {
		addParameter(name, String.valueOf(value));
	}
	/**
	 * @soon_deprecated replaced by addParameter(...)
	 * @param name
	 * @param value
	 */
	public void addParameterToWindow(String name, String value) {
		addParameter(name,value);
	}
	/**
	 * @soon_deprecated replaced by addParameter(...)
	 * @param name
	 * @param value
	 */
	public void addParameterToWindow(String name, int value) {
		addParameter(name,value);
	}
	/**
	 * @soon_deprecated replaced by addParameter(...)
	 * @param name
	 * @param value
	 */
	public void addParameterToPage(String name, String value) {
		addParameter(name,value);
	}
	/**
	 * @soon_deprecated replaced by addParameter(...)
	 * @param name
	 * @param value
	 */
	public void addParameterToPage(String name, int value) {
		addParameter(name,value);
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
	
	private String getURLString(IWContext iwc, int pageID, boolean convert) throws Exception{
		BuilderService bservice = getBuilderService(iwc);
		URLUtil url = new URLUtil(bservice.getPageURI(pageID), convert);
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
	
	private String getURIToClassToInstanciate(IWContext iwc) {
		if (this.classToInstanciate != null) {
			if (this.templatePageClass != null) {
				//return (iwc.getIWMainApplication().getObjectInstanciatorURI(classToInstanciate, templatePageClass))+getParameters();
				return(iwc.getIWMainApplication().getObjectInstanciatorURI(classToInstanciate))+getParameters();
			}
			else if (this.templateForObjectInstanciation != null) {
				return (iwc.getIWMainApplication().getObjectInstanciatorURI(classToInstanciate, templateForObjectInstanciation))+getParameters();
			}
			else {
				return (iwc.getIWMainApplication().getObjectInstanciatorURI(classToInstanciate))+getParameters();
			}
		}
		return "";
	}
	
	public void setClassToInstanciate(Class presentationObjectClass) {
		this.classToInstanciate = presentationObjectClass;
	}

	public void setClassToInstanciate(Class presentationObjectClass, Class pageTemplateClass) {
		setClassToInstanciate(presentationObjectClass);
		this.templatePageClass = pageTemplateClass;
	}

	public void setClassToInstanciate(Class presentationObjectClass, String template) {
		setClassToInstanciate(presentationObjectClass);
		this.templateForObjectInstanciation = template;
	}


}