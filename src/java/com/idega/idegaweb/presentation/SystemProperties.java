package com.idega.idegaweb.presentation;

import java.util.Iterator;
import java.util.Locale;

import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWSystemProperties;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

/**
 * @author Laddi
 */
public class SystemProperties extends Block {

	private final String SYSTEM_PROPERTY = "iw_s_p";
	private final String PROPERTY = "iw_p";
	private final String PROPERTY_VALUE = "iw_p_v";
	private final String PROPERTY_TYPE = "iw_p_t";
	private final String PROPERTY_TYPE_MAP = "map";
	private final String PARAMETER_ACTION = "iw_action";

	private final int NO_ACTION = 1;
	private final int ACTION_SAVE = 2;
	private final int ACTION_DELETE = 3;
	private final int TYPE_PROPERTY = 1;
	private final int TYPE_MAP = 2;

	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.core";
	protected IWResourceBundle _iwrb;
	protected IWSystemProperties properties;
	protected IWPropertyList propertyList;
	protected IWProperty _property;
	private Locale _locale;
	private String systemProperty;
	private String propertyName;
	private String propertyValue;
	private int action = this.NO_ACTION;

	private String _width = Table.HUNDRED_PERCENT;

	public SystemProperties() {
	}

	public void main(IWContext iwc) throws Exception {
		this._iwrb = getResourceBundle(iwc);
		this._locale = iwc.getCurrentLocale();
		this.properties = iwc.getSystemProperties();
		this.propertyList = this.properties;
		this.systemProperty = iwc.getParameter(this.SYSTEM_PROPERTY);
		if (this.systemProperty != null) {
			if (this.systemProperty.length() == 0) {
				this.systemProperty = null;
			}
			else {
				this.propertyList = this.properties.getProperties(this.systemProperty);
			}
		}

		doBusiness(iwc);
		initialize();
	}

	private void initialize() {
		Table table = new Table(1, 4);
		table.setCellpaddingAndCellspacing(0);
		table.setHeight(3, 8);
		table.setWidth(getWidth());

		table.add(getForm(), 1, 1);
		table.add(getPropertyForm(), 1, 2);
		table.add(getPropertiesForm(), 1, 4);
		add(table);
	}

	private Form getForm() {
		Form form = new Form();

		Table table = new Table(2, 1);
		table.setWidth(1,"100");
		form.add(table);

		table.add(getTitleText(this._iwrb.getLocalizedString("category", "Category") + ":"), 1, 1);
		table.add(getInterfaceObject(getPropertiesMenu()), 2, 1);

		return form;
	}

	private Form getPropertyForm() {
		Form form = new Form();
		form.add(new HiddenInput(this.PARAMETER_ACTION, String.valueOf(this.ACTION_SAVE)));
		if (this.systemProperty != null) {
			form.add(new HiddenInput(this.SYSTEM_PROPERTY, this.systemProperty));
		}

		Table table = new Table(2, 4);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setWidth(1,"100");
		form.add(table);

		TextInput nameInput = new TextInput(this.PROPERTY);
		TextInput valueInput = new TextInput(this.PROPERTY_VALUE);
		if (this._property != null && this.action != this.ACTION_DELETE) {
			nameInput.setValue(this._property.getName());
			valueInput.setValue(this._property.getValue());
		}

		table.add(getTitleText(this._iwrb.getLocalizedString("property_name", "Name") + ":"), 1, 1);
		table.add(getTitleText(this._iwrb.getLocalizedString("property_value", "Value") + ":"), 1, 2);
		table.add(getTitleText(this._iwrb.getLocalizedString("property_type", "Type") + ":"), 1, 3);
		table.add(getInterfaceObject(nameInput), 2, 1);
		table.add(getInterfaceObject(valueInput), 2, 2);
		table.add(getInterfaceObject(getPropertyTypeMenu()), 2, 3);

		SubmitButton button = new SubmitButton(this._iwrb.getLocalizedString("save", "Save"));
		button.setAsImageButton(true);
		table.mergeCells(1, 4, 2, 4);
		table.add(button, 1, 4);

		return form;
	}

	private Form getPropertiesForm() {
		Form form = new Form();
		form.add(new HiddenInput(this.PARAMETER_ACTION, String.valueOf(this.ACTION_DELETE)));
		if (this.systemProperty != null) {
			form.add(new HiddenInput(this.SYSTEM_PROPERTY, this.systemProperty));
		}

		Table table = new Table();
		table.add(getTitleText(this._iwrb.getLocalizedString("property", "Property")), 2, 1);
		table.add(getTitleText(this._iwrb.getLocalizedString("value", "Value")), 3, 1);
		table.setRows(3);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setWidth(2, "50%");
		table.setWidth(3, "50%");
		form.add(table);

		Iterator iter = this.propertyList.iterator();
		int row = 2;
		CheckBox checkBox;
		IWProperty iwProperty;
		while (iter.hasNext()) {
			iwProperty = (IWProperty) iter.next();
			checkBox = new CheckBox(this.PROPERTY, iwProperty.getName());
			table.add(checkBox, 1, row);
			table.add(getPropertyLink(iwProperty), 2, row);
			table.add(getText(iwProperty.getValue()), 3, row++);
		}

		SubmitButton button = new SubmitButton(this._iwrb.getLocalizedString("delete", "Delete"));
		button.setAsImageButton(true);
		table.mergeCells(1, row, 3, row);
		table.add(button, 1, row);

		return form;
	}

	private Link getPropertyLink(IWProperty property) {
		Link link = new Link(this.properties.getLocalizedName(this._locale, property));
		link.setFontStyle(IWConstants.BUILDER_FONT_STYLE_SMALL);

		if (property.getType().equalsIgnoreCase(this.PROPERTY_TYPE_MAP)) {
			link.addParameter(this.SYSTEM_PROPERTY, property.getName());
		}
		else {
			if (this.systemProperty != null) {
				link.addParameter(this.SYSTEM_PROPERTY, this.systemProperty);
			}
			link.addParameter(this.PROPERTY, property.getName());
		}

		return link;
	}

	private DropdownMenu getPropertiesMenu() {
		DropdownMenu menu = new DropdownMenu(this.SYSTEM_PROPERTY);
		menu.addMenuElementFirst("", this._iwrb.getLocalizedString("system_properties", "System properties"));
		menu.setToSubmit();

		Iterator iter = this.properties.iterator();
		while (iter.hasNext()) {
			IWProperty property = (IWProperty) iter.next();
			if (property.getType().equals(this.PROPERTY_TYPE_MAP)) {
				menu.addMenuElement(property.getName(), "- " + this.properties.getLocalizedName(this._locale, property));
			}
		}
		if (this.systemProperty != null) {
			menu.setSelectedElement(this.systemProperty);
		}

		return menu;
	}

	private DropdownMenu getPropertyTypeMenu() {
		DropdownMenu menu = new DropdownMenu(this.PROPERTY_TYPE);
		menu.addMenuElement(this.TYPE_PROPERTY, this._iwrb.getLocalizedString("type_property", "Property"));
		menu.addMenuElement(this.TYPE_MAP, this._iwrb.getLocalizedString("type_map", "Category"));
		return menu;
	}

	private void doBusiness(IWContext iwc) {
		this.action = getAction(iwc);
		this.propertyName = iwc.getParameter(this.PROPERTY);
		if (this.propertyName != null && this.propertyName.length() > 0) {
			this._property = this.propertyList.getIWProperty(this.propertyName);
		}
		this.propertyValue = iwc.getParameter(this.PROPERTY_VALUE);

		switch (this.action) {
			case ACTION_DELETE :
				deleteProperties(iwc);
				break;
			case ACTION_SAVE :
				saveProperty(iwc);
				break;
		}
	}

	private void deleteProperties(IWContext iwc) {
		String[] propertyNames = iwc.getParameterValues(this.PROPERTY);
		if (propertyNames != null) {
			for (int a = 0; a < propertyNames.length; a++) {
				this.propertyList.removeProperty(propertyNames[a]);
			}
		}
	}

	private void saveProperty(IWContext iwc) {
		int type = getPropertyType(iwc);
		if (this.propertyName != null && this.propertyValue != null) {
			if (this.propertyName.length() > 0) {
				switch (type) {
					case TYPE_PROPERTY :
						this.propertyList.setProperty(this.propertyName, this.propertyValue);
						break;
					case TYPE_MAP :
						if (this.systemProperty == null) {
							this.propertyList.getNewPropertyList(this.propertyName);
						}
						break;
				}
			}
		}
	}

	private int getAction(IWContext iwc) {
		try {
			return Integer.parseInt(iwc.getParameter(this.PARAMETER_ACTION));
		}
		catch (NumberFormatException nfe) {
			return this.NO_ACTION;
		}
	}

	private int getPropertyType(IWContext iwc) {
		try {
			return Integer.parseInt(iwc.getParameter(this.PROPERTY_TYPE));
		}
		catch (NumberFormatException nfe) {
			return this.TYPE_PROPERTY;
		}
	}

	private InterfaceObject getInterfaceObject(InterfaceObject obj) {
		obj.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
		return obj;
	}

	private Text getTitleText(String string) {
		Text text = new Text(string);
		text.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		return text;
	}

	private Text getText(String string) {
		Text text = new Text(string);
		text.setFontStyle(IWConstants.BUILDER_FONT_STYLE_SMALL);
		return text;
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	/**
	 * Returns the width.
	 * @return String
	 */
	public String getWidth() {
		return this._width;
	}

	/**
	 * Sets the width.
	 * @param width The width to set
	 */
	public void setWidth(String width) {
		this._width = width;
	}
}