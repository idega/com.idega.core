/*
 * $Id: GenericButton.java,v 1.38.2.1 2006/08/01 15:42:54 gimmi Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.util.Iterator;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICPage;
import com.idega.core.file.business.ICFileSystem;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Script;
import com.idega.util.StringHandler;
import com.idega.util.URLUtil;
import com.idega.util.datastructures.list.KeyValueList;
import com.idega.util.datastructures.list.KeyValuePair;
import com.idega.util.text.TextSoap;

/**
 * <p>
 * This component is for rendering out a input element of type button.
 * </p>
 *  Last modified: $Date: 2006/08/01 15:42:54 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.38.2.1 $
 */
public class GenericButton extends GenericInput {

	//constants:
	private static final String buttonImageStyle = "cursor:hand;";
	
	//Instance variables
	private int _pageID = -1;
	private int _fileID = -1;
	private boolean asImageButton = false;
	private Class _windowClassToOpen;
	private Class _publicWindowClassToOpen;
	private KeyValueList  parameterList;
	private boolean _onClickConfirm = false;
	private String _confirmMessage;
	private int _parentPageID = -1;
	private Class classToInstanciate;
	private Class templatePageClass;
	private String templateForObjectInstanciation;
	private String _URL;

	
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[14];
		values[0] = super.saveState(ctx);
		values[1] = new Integer(this._pageID);
		values[2] = new Integer(this._fileID);
		values[3] = Boolean.valueOf(this.asImageButton);
		values[4] = this._windowClassToOpen;
		values[5] = this.parameterList;
		values[6] = Boolean.valueOf(this._onClickConfirm);
		values[7] = this._confirmMessage;
		values[8] = new Integer(this._parentPageID);
		values[9] = this.classToInstanciate;
		values[10] = this.templatePageClass;
		values[11] = this.templateForObjectInstanciation;
		values[12] = this._URL;
		values[13] = this._publicWindowClassToOpen;
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this._pageID = ((Integer)values[1]).intValue();
		this._fileID = ((Integer)values[2]).intValue();
		this.asImageButton = ((Boolean)values[3]).booleanValue();
		this._windowClassToOpen = (Class)values[4];
		this.parameterList = (KeyValueList)values[5];
		this._onClickConfirm = ((Boolean)values[6]).booleanValue();
		this._confirmMessage = (String)values[7];
		this._parentPageID = ((Integer)values[8]).intValue();
		this.classToInstanciate = (Class)values[9];
		this.templatePageClass = (Class)values[10];
		this.templateForObjectInstanciation = (String) values[11];
		this._URL = (String)values[12];
		this._publicWindowClassToOpen = (Class)values[13];
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
		if (this._onClickConfirm) {
			Script script = getParentPage().getAssociatedScript();
			if (script != null) {
				script = new Script();
			}
					
			boolean addFunction = false;
					
			StringBuffer buffer = new StringBuffer();
			buffer.append("function onClickConfirm(message) {").append("\n\t");
			buffer.append("var submit = confirm(message);").append("\n\t");
			buffer.append("if (submit)").append("\n\t\t");
					
			if (this._windowClassToOpen != null) {
				String URL = Window.getWindowURLWithParameters(this._windowClassToOpen, iwc, getConvertedAndCheckedParameterList());
				buffer.append(Window.getCallingScriptString(this._windowClassToOpen, URL, true, iwc)).append(";\n");
				addFunction = true;
			}
			if (this._publicWindowClassToOpen != null) {
				String URL = Window.getWindowURLWithParameters(this._publicWindowClassToOpen, iwc, getConvertedAndCheckedParameterList());
				buffer.append(Window.getCallingScriptString(this._publicWindowClassToOpen, URL, true, iwc)).append(";\n");
				addFunction = true;
			}
			if (this._pageID != -1) {
				buffer.append("window.location='"+getURLString(iwc, this._pageID, false)+"';").append("\n");
				addFunction = true;
			}
			if (this._parentPageID != -1) {
				buffer.append("window.location='"+getURLString(iwc, this._parentPageID, false)+"';").append("\n");
				addFunction = true;
			}
			if (this._fileID != -1) {
				ICFileSystem fsystem = getICFileSystem(iwc);
				buffer.append(Window.getCallingScript(fsystem.getFileURI(this._fileID))).append(";\n");
				addFunction = true;
			}
			if(this.classToInstanciate!=null){
				buffer.append("location='"+getURIToClassToInstanciate(iwc)+"';").append("\n");
				addFunction = true;
			}
			if(this._URL != null){
				buffer.append(Window.getWindowCallingScript(this._URL, "_blank", true, true, true, true, true, true, true, true, false, 640, 480)).append("\n");
				addFunction = true;
			}

			buffer.append("}");
			if (addFunction) {
				setOnClick("javascript:onClickConfirm('"+this._confirmMessage+"')");
				script.addFunction("onClickConfirm", buffer.toString());
				getParentPage().setAssociatedScript(script);
			}
		}
	}
	
	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			if (this.asImageButton) {
				String normalizedName = StringHandler.stripNonRomanCharacters(getValueAsString(),
						new char[] { '0', '1', '2', '3',
								'4', '5', '6', '7', '8',
								'9', '-', '.' });
				Image generatedImage = iwc.getIWMainApplication().getCoreBundle().getImageButton(normalizedName);
				setButtonImage(generatedImage);
			}
			if (!this._onClickConfirm) {
				if (this._windowClassToOpen != null) {
					String URL =  Window.getWindowURLWithParameters(this._windowClassToOpen, iwc, getConvertedAndCheckedParameterList()); 
					setOnClick("javascript:" + Window.getCallingScriptString(this._windowClassToOpen, URL, true, iwc));
				}
				if (this._publicWindowClassToOpen != null) {
					String URL =  Window.getPublicWindowURLWithParameters(this._publicWindowClassToOpen, iwc, getConvertedAndCheckedParameterList()); 
					setOnClick("javascript:" + Window.getCallingScriptString(this._publicWindowClassToOpen, URL, true, iwc));
				}
				if (this._pageID != -1) {
					setOnClick("javascript:window.location='"+getURLString(iwc, this._pageID, true)+"';");
				}
				if (this._parentPageID != -1) {
					setOnClick("javascript:window.opener.location='"+getURLString(iwc, this._parentPageID, true)+"';window.close();");
				}
				if (this._fileID != -1) {
					ICFileSystem fsystem = getICFileSystem(iwc);
					setOnClick("javascript:"+Window.getCallingScript(fsystem.getFileURI(this._fileID)));	
				}
				if (this.classToInstanciate != null) {
					setOnClick("javascript:location='"+getURIToClassToInstanciate(iwc)+"';");
				}
				if (this._URL != null) {
					setOnClick("javascript:"+Window.getWindowCallingScript(this._URL, "_blank", true, true, true, true, true, true, true, true, false, 640, 480));
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

				/*ICDomain d = iwc.getDomain();
				String serverUrl = d.getURLWithoutLastSlash();
				if (serverUrl != null) {
					if (URL.startsWith("/")) {
//						String protocol;
//						if (iwc.getRequest().isSecure()) {
//							protocol = "https://";
//						}
//						else {
//							protocol = "http://";
//						}
//						URL = protocol + serverName + URL;
//						
						URL = serverUrl + URL;
					}
				}*/

				buttonImage.setURL(URL);
				buttonImage.addMarkupAttributes(getMarkupAttributes());
				buttonImage.setStyleAttribute(buttonImageStyle);
				buttonImage.setName(getName());

				if (getInputType().equals(INPUT_TYPE_IMAGE)) {
					setSource(URL);
					super.print(iwc);
				}
				else {
					print("<img " + buttonImage.getMarkupAttributesString() + " />");
				}
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
		this._windowClassToOpen = windowClassToOpen;
	}

	public void setPublicWindowToOpen(Class windowClassToOpen) {
		this._publicWindowClassToOpen = windowClassToOpen;
	}
	
	public void setPageToOpen(int pageID) {
		this._pageID = pageID;
	}
	
	public void setPageToOpen(ICPage page) {
		if (page != null && page.getID() != -1) {
			setPageToOpen(page.getID());
		}
	}
	
	public void setParentPageToOpen(int pageID) {
		this._parentPageID = pageID;
	}
	
	public void setParentPageToOpen(ICPage page) {
		if (page != null && page.getID() != -1) {
			setParentPageToOpen(page.getID());
		}
	}
	
	public void setURLToOpen(String URL) {
		this._URL = URL;
	}
	
	public void addParameter(String name, String value) {
		if (this.parameterList == null) {
			this.parameterList = new KeyValueList();
		}
		this.parameterList.put(name, value);
	}
	
	/**
	 * Adds a whole map of parameters to the button
	 * @param parameterMap 
	 */
	public void addParameters(Map prmMap){
		Iterator iterator = prmMap.keySet().iterator();
		while (iterator.hasNext()) {
			String name = (String) iterator.next();
			String value = (String) prmMap.get(name);
			addParameter(name,value);
		}
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
	
	
	private KeyValueList getConvertedAndCheckedParameterList() {
		if (this.parameterList == null) {
			return null;
		}
		KeyValueList convertedList = new KeyValueList(this.parameterList.size());
		Iterator iter = this.parameterList.iterator();
		while (iter.hasNext()) {
			KeyValuePair pair = (KeyValuePair) iter.next();
			String name = (String) pair.getKey();
			String value = (String) pair.getValue();
			if (name != null && value != null) {
				String convertedName = TextSoap.convertSpecialCharacters(name);
				String convertedValue = TextSoap.convertSpecialCharacters(value);
				convertedList.put(convertedName, convertedValue);
			}
		}
		return convertedList;		
	}
	
	
	private String getURLString(IWContext iwc, int pageID, boolean convert) throws Exception{
		BuilderService bservice = getBuilderService(iwc);
		URLUtil url = new URLUtil(bservice.getPageURI(pageID), convert);
		if (this.parameterList != null) {
			Iterator iter = this.parameterList.iterator();
			while (iter.hasNext()) {
				KeyValuePair pair = (KeyValuePair) iter.next();
				String name = (String) pair.getKey();
				String value = (String) pair.getValue();
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
		this._fileID = fileID;
	}
	
	public void setOnClickConfirm(String confirmMessage) {
		this._onClickConfirm = true;
		this._confirmMessage = confirmMessage;
	}
	
	private String getURIToClassToInstanciate(IWContext iwc) {
		if (this.classToInstanciate == null) {
			return "";
		}
		String uri = null;
		if (this.templatePageClass != null) {
				//return (iwc.getIWMainApplication().getObjectInstanciatorURI(classToInstanciate, templatePageClass))+getParameters();
			uri = iwc.getIWMainApplication().getObjectInstanciatorURI(this.classToInstanciate);
		}
		else if (this.templateForObjectInstanciation != null) {
			uri = iwc.getIWMainApplication().getObjectInstanciatorURI(this.classToInstanciate, this.templateForObjectInstanciation);
		}
		else {
			uri = iwc.getIWMainApplication().getObjectInstanciatorURI(this.classToInstanciate);
		}
		KeyValueList convertedList = getConvertedAndCheckedParameterList();
		if (convertedList.isEmpty()) {
			return uri;
		}
		StringBuffer buffer = new StringBuffer(uri);
		if (buffer.indexOf("?") < 0) {
			buffer.append('?');
		}
		else {
			buffer.append('=');
		}
		Iterator iterator = convertedList.iterator();
		boolean firstparameter = true;
		while (iterator.hasNext()) {
			if (firstparameter) {
				firstparameter = false;
			}
			else {
				buffer.append('&');
			}
			KeyValuePair pair = (KeyValuePair) iterator.next();
			String name = (String) pair.getKey();
			String value = (String) pair.getValue();
			buffer.append(name).append('=').append(value);
		}
		return buffer.toString();
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