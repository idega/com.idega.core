/*
 * $Id: GenericButton.java,v 1.30 2005/03/09 02:07:20 tryggvil Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.faces.context.FacesContext;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.file.business.ICFileSystem;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Script;
import com.idega.util.URLUtil;
import com.idega.util.text.TextSoap;

/**
 * <p>
 * This component is for rendering out a input element of type button.
 * </p>
 *  Last modified: $Date: 2005/03/09 02:07:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.30 $
 */
public class GenericButton extends GenericInput {

	//constants:
	private static final String buttonImageStyle = "cursor:hand;";
	
	//Instance variables
	private int _pageID = -1;
	private int _fileID = -1;
	private boolean asImageButton = false;
	private Class _windowClassToOpen;
	private Map parameterMap;
	private boolean _onClickConfirm = false;
	private String _confirmMessage;
	private int _parentPageID = -1;
	private Class classToInstanciate;
	private Class templatePageClass;
	private String templateForObjectInstanciation;
	private String _URL;

	
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[13];
		values[0] = super.saveState(ctx);
		values[1] = new Integer(_pageID);
		values[2] = new Integer(_fileID);
		values[3] = Boolean.valueOf(asImageButton);
		values[4] = _windowClassToOpen;
		values[5] = parameterMap;
		values[6] = Boolean.valueOf(_onClickConfirm);
		values[7] = _confirmMessage;
		values[8] = new Integer(_parentPageID);
		values[9] = classToInstanciate;
		values[10] = templatePageClass;
		values[11] = templateForObjectInstanciation;
		values[12] = _URL;
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		_pageID = ((Integer)values[1]).intValue();
		_fileID = ((Integer)values[2]).intValue();
		asImageButton = ((Boolean)values[3]).booleanValue();
		_windowClassToOpen = (Class)values[4];
		parameterMap = (Map)values[5];
		_onClickConfirm = ((Boolean)values[6]).booleanValue();
		_confirmMessage = (String)values[7];
		_parentPageID = ((Integer)values[8]).intValue();
		classToInstanciate = (Class)values[9];
		templatePageClass = (Class)values[10];
		templateForObjectInstanciation = (String) values[11];
		_URL = (String)values[12];
	}
	
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

	//TODO remove this variable declaration and move totally to facets:
	//This variable is kept because of legacy reasons but should be replaced with a Facet
	private Image oldDefaultImage;
	public void setButtonImage(Image image) {
		if(IWMainApplication.useJSF){
			getFacets().put("buttonimage",image);
		}
		else{
			this.oldDefaultImage =image;
		}
	}
	
	protected Image getButtonImage(){
		if(IWMainApplication.useJSF){
			return (Image)getFacet("buttonimage");
		}
		else{
			return this.oldDefaultImage;
		}
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
				Image generatedImage = iwc.getIWMainApplication().getCoreBundle().getImageButton(getValueAsString());
				setButtonImage(generatedImage);
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
			Image buttonImage = getButtonImage();
			if (buttonImage == null) {
				//no image on button:
				super.print(iwc);
			}
			else {
				String URL = buttonImage.getURL();
				if (URL == null) {
					URL = buttonImage.getMediaURL(iwc);
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

				buttonImage.setURL(URL);
				buttonImage.addMarkupAttributes(getMarkupAttributes());
				buttonImage.setStyleAttribute(buttonImageStyle);
				buttonImage.setName(getName());

				if (getInputType().equals(INPUT_TYPE_IMAGE)) {
					setSource(URL);
					super.print(iwc);
				}
				else
					print("<img " + buttonImage.getMarkupAttributesString() + " >");
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
		if (this.oldDefaultImage != null) {
			obj.oldDefaultImage = (Image) this.oldDefaultImage.clone();
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