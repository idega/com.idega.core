//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import java.io.*;
import java.util.*;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.data.IBDomain;
import com.idega.presentation.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class GenericButton extends GenericInput {

	private boolean asImageButton = false;
	private Image defaultImage;
	private final String buttonImageStyle = "cursor:hand;";
	private Class _windowClassToOpen;

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
				setOnClick("javascript:" + Window.getCallingScriptString(_windowClassToOpen, iwc));
			}

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

	public synchronized Object clone() {
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
}