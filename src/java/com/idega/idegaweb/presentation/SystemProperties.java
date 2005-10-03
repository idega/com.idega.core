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
	private int action = NO_ACTION;

	private String _width = Table.HUNDRED_PERCENT;

	public SystemProperties() {
	}

	public void main(IWContext iwc) throws Exception {
		_iwrb = getResourceBundle(iwc);
		_locale = iwc.getCurrentLocale();
		properties = iwc.getSystemProperties();
		propertyList = properties;
		systemProperty = iwc.getParameter(SYSTEM_PROPERTY);
		if (systemProperty != null) {
			if (systemProperty.length() == 0)
				systemProperty = null;
			else
				propertyList = properties.getProperties(systemProperty);
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

		table.add(getTitleText(_iwrb.getLocalizedString("category", "Category") + ":"), 1, 1);
		table.add(getInterfaceObject(getPropertiesMenu()), 2, 1);

		return form;
	}

	private Form getPropertyForm() {
		Form form = new Form();
		form.add(new HiddenInput(PARAMETER_ACTION, String.valueOf(ACTION_SAVE)));
		if (systemProperty != null)
			form.add(new HiddenInput(SYSTEM_PROPERTY, systemProperty));

		Table table = new Table(2, 4);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setWidth(1,"100");
		form.add(table);

		TextInput nameInput = new TextInput(PROPERTY);
		TextInput valueInput = new TextInput(PROPERTY_VALUE);
		if (_property != null && action != ACTION_DELETE) {
			nameInput.setValue(_property.getName());
			valueInput.setValue(_property.getValue());
		}

		table.add(getTitleText(_iwrb.getLocalizedString("property_name", "Name") + ":"), 1, 1);
		table.add(getTitleText(_iwrb.getLocalizedString("property_value", "Value") + ":"), 1, 2);
		table.add(getTitleText(_iwrb.getLocalizedString("property_type", "Type") + ":"), 1, 3);
		table.add(getInterfaceObject(nameInput), 2, 1);
		table.add(getInterfaceObject(valueInput), 2, 2);
		table.add(getInterfaceObject(getPropertyTypeMenu()), 2, 3);

		SubmitButton button = new SubmitButton(_iwrb.getLocalizedString("save", "Save"));
		button.setAsImageButton(true);
		table.mergeCells(1, 4, 2, 4);
		table.add(button, 1, 4);

		return form;
	}

	private Form getPropertiesForm() {
		Form form = new Form();
		form.add(new HiddenInput(PARAMETER_ACTION, String.valueOf(ACTION_DELETE)));
		if (systemProperty != null)
			form.add(new HiddenInput(SYSTEM_PROPERTY, systemProperty));

		Table table = new Table();
		table.add(getTitleText(_iwrb.getLocalizedString("property", "Property")), 2, 1);
		table.add(getTitleText(_iwrb.getLocalizedString("value", "Value")), 3, 1);
		table.setRows(3);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setWidth(2, "50%");
		table.setWidth(3, "50%");
		form.add(table);

		Iterator iter = propertyList.iterator();
		int row = 2;
		CheckBox checkBox;
		IWProperty iwProperty;
		while (iter.hasNext()) {
			iwProperty = (IWProperty) iter.next();
			checkBox = new CheckBox(PROPERTY, iwProperty.getName());
			table.add(checkBox, 1, row);
			table.add(getPropertyLink(iwProperty), 2, row);
			table.add(getText(iwProperty.getValue()), 3, row++);
		}

		SubmitButton button = new SubmitButton(_iwrb.getLocalizedString("delete", "Delete"));
		button.setAsImageButton(true);
		table.mergeCells(1, row, 3, row);
		table.add(button, 1, row);

		return form;
	}

	private Link getPropertyLink(IWProperty property) {
		Link link = new Link(properties.getLocalizedName(_locale, property));
		link.setFontStyle(IWConstants.BUILDER_FONT_STYLE_SMALL);

		if (property.getType().equalsIgnoreCase(PROPERTY_TYPE_MAP)) {
			link.addParameter(SYSTEM_PROPERTY, property.getName());
		}
		else {
			if (systemProperty != null)
				link.addParameter(SYSTEM_PROPERTY, systemProperty);
			link.addParameter(PROPERTY, property.getName());
		}

		return link;
	}

	private DropdownMenu getPropertiesMenu() {
		DropdownMenu menu = new DropdownMenu(SYSTEM_PROPERTY);
		menu.addMenuElementFirst("", _iwrb.getLocalizedString("system_properties", "System properties"));
		menu.setToSubmit();

		Iterator iter = properties.iterator();
		while (iter.hasNext()) {
			IWProperty property = (IWProperty) iter.next();
			if (property.getType().equals(PROPERTY_TYPE_MAP)) {
				menu.addMenuElement(property.getName(), "- " + properties.getLocalizedName(_locale, property));
			}
		}
		if (systemProperty != null)
			menu.setSelectedElement(systemProperty);

		return menu;
	}

	private DropdownMenu getPropertyTypeMenu() {
		DropdownMenu menu = new DropdownMenu(this.PROPERTY_TYPE);
		menu.addMenuElement(TYPE_PROPERTY, _iwrb.getLocalizedString("type_property", "Property"));
		menu.addMenuElement(TYPE_MAP, _iwrb.getLocalizedString("type_map", "Category"));
		return menu;
	}

	private void doBusiness(IWContext iwc) {
		action = getAction(iwc);
		propertyName = iwc.getParameter(PROPERTY);
		if (propertyName != null && propertyName.length() > 0)
			_property = propertyList.getIWProperty(propertyName);
		propertyValue = iwc.getParameter(PROPERTY_VALUE);

		switch (action) {
			case ACTION_DELETE :
				deleteProperties(iwc);
				break;
			case ACTION_SAVE :
				saveProperty(iwc);
				break;
		}
	}

	private void deleteProperties(IWContext iwc) {
		String[] propertyNames = iwc.getParameterValues(PROPERTY);
		if (propertyNames != null) {
			for (int a = 0; a < propertyNames.length; a++) {
				propertyList.removeProperty(propertyNames[a]);
			}
		}
	}

	private void saveProperty(IWContext iwc) {
		int type = getPropertyType(iwc);
		if (propertyName != null && propertyValue != null) {
			if (propertyName.length() > 0) {
				switch (type) {
					case TYPE_PROPERTY :
						propertyList.setProperty(propertyName, propertyValue);
						break;
					case TYPE_MAP :
						if (systemProperty == null)
							propertyList.getNewPropertyList(propertyName);
						break;
				}
			}
		}
	}

	private int getAction(IWContext iwc) {
		try {
			return Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}
		catch (NumberFormatException nfe) {
			return NO_ACTION;
		}
	}

	private int getPropertyType(IWContext iwc) {
		try {
			return Integer.parseInt(iwc.getParameter(PROPERTY_TYPE));
		}
		catch (NumberFormatException nfe) {
			return TYPE_PROPERTY;
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
		return _width;
	}

	/**
	 * Sets the width.
	 * @param width The width to set
	 */
	public void setWidth(String width) {
		_width = width;
	}
}