/*
 * $Id: OrderByGroupDetailsDropDownMenu.java,v 1.2 2007/01/26 06:36:37 idegaweb Exp $
 * Created on 26.1.2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.util.Arrays;
import java.util.List;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * <p>
 * TODO sigtryggur Describe Type OrderByGroupDetailsDropDownMenu
 * </p>
 *  Last modified: $Date: 2007/01/26 06:36:37 $ by $Author: idegaweb $
 *
 * @author <a href="mailto:sigtryggur@idega.com">sigtryggur</a>
 * @version $Revision: 1.2 $
 */
public class OrderByGroupDetailsDropDownMenu extends DropDownMenuInputHandler {

	public static final String	ORDER_BY_GROUP_PATH = "group_path_order",
								ORDER_BY_NAME = "name_order",
								ORDER_BY_GROUP_TYPE = "group_type_order",
								ORDER_BY_ADDRESS = "address_order",
								ORDER_BY_POSTAL_ADDRESS = "postal_address_order";

	protected static String IW_BUNDLE_IDENTIFIER = "com.idega.user";
	private static final String CLASS_NAME_PREFIX = "OrderByGroupDetailsDropDownMenu.";

	@Override
	public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		this.setName(name);
		for (AdvancedProperty element: getOrderings(iwrb)) {
			this.addMenuElement(element.getId(), element.getValue());
		}
		return this;
	}

	public static List<AdvancedProperty> getOrderings(IWResourceBundle iwrb) {
		return Arrays.asList(
				new AdvancedProperty(ORDER_BY_GROUP_PATH, iwrb.getLocalizedString(CLASS_NAME_PREFIX + ORDER_BY_GROUP_PATH, "Group path")),
				new AdvancedProperty(ORDER_BY_NAME, iwrb.getLocalizedString(CLASS_NAME_PREFIX + ORDER_BY_NAME, "Name")),
				new AdvancedProperty(ORDER_BY_GROUP_TYPE, iwrb.getLocalizedString(CLASS_NAME_PREFIX + ORDER_BY_GROUP_TYPE, "Group type")),
				new AdvancedProperty(ORDER_BY_ADDRESS, iwrb.getLocalizedString(CLASS_NAME_PREFIX + ORDER_BY_ADDRESS, "Address")),
				new AdvancedProperty(ORDER_BY_POSTAL_ADDRESS, iwrb.getLocalizedString(CLASS_NAME_PREFIX + ORDER_BY_POSTAL_ADDRESS, "Postal address"))
		);
	}

	@Override
	public String getDisplayForResultingObject(Object value, IWContext iwc) {
		if (value != null) {
			IWResourceBundle iwrb = getResourceBundle(iwc);
			return iwrb.getLocalizedString(CLASS_NAME_PREFIX + value);
		}
		else {
			return "";
		}
	}

	@Override
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
}