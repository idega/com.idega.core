/*
 * $Id: AbstractChooser.java,v 1.24 2004/09/28 16:41:01 eiki Exp $
 * Copyright (C) 2001 Idega hf. All Rights Reserved. This software is the
 * proprietary information of Idega hf. Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.net.URLEncoder;

import javax.faces.component.UIComponent;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>,
 *         <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public abstract class AbstractChooser extends PresentationObjectContainer {
	static final String CHOOSER_SELECTION_PARAMETER = "iw_ch_p";
	static final String DISPLAYSTRING_PARAMETER = "iw_ch_d";
	static final String VALUE_PARAMETER = "iw_ch_v";
	static final String DISPLAYSTRING_PARAMETER_NAME = "iw_ch_d_n";
	static final String VALUE_PARAMETER_NAME = "iw_ch_v_n";
	static final String SCRIPT_PREFIX_PARAMETER = "iw_ch_ch_p";
	static final String SCRIPT_SUFFIX_PARAMETER = "iw_ch_s";
	public static final String FILTER_PARAMETER = "iw_filter";

	public String chooserParameter = VALUE_PARAMETER;
	public String displayInputName = DISPLAYSTRING_PARAMETER;
	private boolean _addForm = true;
	private boolean _addTextInput = true;
	private Form _form = null;
	private Image _buttonImage = null;
	protected String _style = IWConstants.BUILDER_FONT_STYLE_INTERFACE;
	protected String _stringValue;
	protected String _stringDisplay;
	private String _attributeValue;
	private String _attributeName;
	private String filter = "";
	private Link link = null;
	protected boolean disabled = true;
	private IWBundle _bundle;
	private IWResourceBundle _iwrb;
	private int _inputLength = -1;
	
	private String styleClassName;
	private boolean isStyleClassSet = false;

	/**
	 * @param aDisabled -
	 *          the new value for disabled
	 */
	public void setDisabled(boolean aDisabled) {
		disabled = aDisabled;
	}

	public void setInputLength(int length) {
		_inputLength = length;
	}
	
	/**
	 *
	 */
	public AbstractChooser() {}

	/**
	 *
	 */
	public abstract Class getChooserWindowClass();

	/**
	 *
	 */
	public String getChooserParameter() {
		return (chooserParameter);
	}

	/**
	 *
	 */
	public void setChooserParameter(String parameterName) {
		chooserParameter = parameterName;
		if (displayInputName == DISPLAYSTRING_PARAMETER) {
			displayInputName = parameterName + "_displaystring";
		}
	}

	public void setChooserValue(String displayString, String valueString) {
		this._stringValue = valueString;
		this._stringDisplay = displayString;
	}

	protected void setChooserValue(String displayString, int valueInt) {
		setChooserValue(displayString, Integer.toString(valueInt));
	}

	public void setValue(Object objectValue) {
		setValue(objectValue.toString());
	}

	public void setValue(String stringValue) {
		setChooserValue(stringValue, stringValue);
	}

	public String getChooserValue() {
		return _stringValue;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 *
	 */
	public void setName(String name) {
		displayInputName = name;
		if (chooserParameter == VALUE_PARAMETER) {
			chooserParameter = name + "_chooser";
		}
	}

	/**
	 *
	 */
	public String getName() {
		return (displayInputName);
	}

	/**
	 *
	 */
	public void _main(IWContext iwc) throws Exception {
		super._main(iwc);
		_bundle = getBundle(iwc);
		_iwrb = getResourceBundle(iwc);
		if (_addForm) {
			_form = new Form();
			_form.setWindowToOpen(getChooserWindowClass());
			add(_form);
			_form.add(getTable(iwc, _bundle));
		}
		else {
			add(getTable(iwc, _bundle));
			_form = getParentForm();
		}

	}

	/**
	 *
	 */
	public PresentationObject getTable(IWContext iwc, IWBundle bundle) {
		Table table = new Table(2, 1);
		table.setCellpadding(0);
		table.setCellspacing(0);

		Parameter value = new Parameter(getChooserParameter(), "");
		if (_stringValue != null) {
			value.setValue(_stringValue);
		}
		table.add(value);

		PresentationObject object = getPresentationObject(iwc);

		table.add(new Parameter(VALUE_PARAMETER_NAME, value.getName()));
		//GenericButton button = new
		// GenericButton("chooserbutton",bundle.getResourceBundle(iwc).getLocalizedString(chooserText,"Choose"));
		if (_addForm) {
			SubmitButton button = new SubmitButton(_iwrb.getLocalizedString("choose", "Choose"));
			table.add(button, 2, 1);
			_form.addParameter(CHOOSER_SELECTION_PARAMETER, getChooserParameter());
			_form.addParameter(SCRIPT_PREFIX_PARAMETER, "window.opener.document." + _form.getID());
			_form.addParameter(SCRIPT_SUFFIX_PARAMETER, "value");
			_form.addParameter(FILTER_PARAMETER, filter);
			addParametersToForm(_form);
		}
		else {
			getLink(_iwrb);

			link.setWindowToOpen(getChooserWindowClass());
			link.addParameter(CHOOSER_SELECTION_PARAMETER, getChooserParameter());

			link.addParameter(SCRIPT_PREFIX_PARAMETER, getParentFormJavascriptPath());

			//TODO Make the javascript work for other objects than form elements,
			// e.g. a Link
			/*
			 * if(object instanceof Layer){
			 * link.addParameter(SCRIPT_SUFFIX_PARAMETER,"title"); }
			 */
			link.addParameter(SCRIPT_SUFFIX_PARAMETER, "value");
			//}

			link.addParameter(DISPLAYSTRING_PARAMETER_NAME, object.getID());
			link.addParameter(VALUE_PARAMETER_NAME, value.getName());
			if (_attributeName != null && _attributeValue != null) {
				link.addParameter(_attributeName, _attributeValue);
			}
			link.addParameter(FILTER_PARAMETER, filter);
			
			addParametersToLink(link);
			
			table.add(link, 2, 1);
		}

		table.add(object, 1, 1);
		table.add(new Parameter(DISPLAYSTRING_PARAMETER_NAME, "151324213"));
		return (table);
	}

	/**
	 * Override this method to add extra parameters to the chooser link
	 * @param form
	 */
	protected void addParametersToLink(Link link) {
	}

	/**
	 * Override this method to add extra parameters to the chooser form
	 * @param form
	 */
	protected void addParametersToForm(Form form) {
	}

	public String getParentFormJavascriptPath() {
		return "window.opener.document." + getParentFormString(this);
	}

	public PresentationObject getPresentationObject(IWContext iwc) {
		if (_addTextInput) {
			TextInput input = new TextInput(displayInputName);
			input.setDisabled(disabled);
			if (_inputLength > 0)
				input.setLength(_inputLength);

			if (_style != null) {
				input.setMarkupAttribute("style", _style);
			}
			if(isStyleClassSet) {
				input.setStyleClass(styleClassName);
			}

			if (_stringDisplay != null) {
				input.setValue(_stringDisplay);
			}

			return input;
		}
		else {
			HiddenInput input = new HiddenInput(displayInputName);
			if (_stringDisplay != null) {
				input.setValue(_stringDisplay);
			}
			return input;
		}
	}

	/*
	 *
	 */
	private String getParentFormString(PresentationObject obj) {
		String returnString = "";
		UIComponent parent = obj.getParent();
		if (parent != null) {
			if(parent instanceof PresentationObject){
				if (!(parent instanceof Form)) {
					returnString = getParentFormString((PresentationObject) parent);
				}
				else {
					returnString = ((PresentationObject) parent).getID() + ".";
				}
			}
		}

		return (returnString);
	}

	public void setInputStyle(String style) {
		_style = style;
	}
	
	public void setStyleClassName(String styleClassName) {
		this.styleClassName = styleClassName;
		isStyleClassSet = true;
	}

	public void addForm(boolean addForm) {
		_addForm = addForm;
	}

	public void setChooseButtonImage(Image buttonImage) {
		_buttonImage = buttonImage;
	}

	public void setParameterValue(String attributeName, String attributeValue) {
		_attributeName = attributeName;
		_attributeValue = URLEncoder.encode(attributeValue);
	}

	public void addParameterToChooserLink(String param, String value) {
		getLink(null).addParameter(param, value);
	}

	private Link getLink(IWResourceBundle iwrb) {
		if (link == null) {
			if (_buttonImage == null) {
				link = new Link(iwrb.getLocalizedString("choose", "Choose"));
			}
			else {
				_buttonImage.setHorizontalSpacing(3);
				link = new Link(_buttonImage);
			}
		}

		return link;
	}

	public void addTextInput(boolean addTextInput) {
		this._addTextInput = addTextInput;
	}

	/**
	 * @return Returns the bundle.
	 */
	protected IWBundle getBundle() {
		return this._bundle;
	}
	/**
	 * @return Returns the resource bundle.
	 */
	protected IWResourceBundle getResourceBundle() {
		return this._iwrb;
	}
}
