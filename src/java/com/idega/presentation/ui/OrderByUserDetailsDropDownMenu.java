/*
 * $Id: OrderByUserDetailsDropDownMenu.java,v 1.2 2007/01/26 06:36:37 idegaweb Exp $
 * Created on 26.1.2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * <p>
 * TODO sigtryggur Describe Type OrderByUserDetailsDropDownMenu
 * </p>
 *  Last modified: $Date: 2007/01/26 06:36:37 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:sigtryggur@idega.com">sigtryggur</a>
 * @version $Revision: 1.2 $
 */
public class OrderByUserDetailsDropDownMenu extends DropDownMenuInputHandler {

	private static final String ORDER_BY_GROUP_PATH = "group_path_order";
	private static final String ORDER_BY_NAME = "name_order";
	private static final String ORDER_BY_USER_STATUS = "user_status_order";
	private static final String ORDER_BY_ADDRESS = "address_order";
	private static final String ORDER_BY_POSTAL_ADDRESS = "postal_address_order";
	
	protected static String IW_BUNDLE_IDENTIFIER = "com.idega.user";
	private static final String CLASS_NAME_PREFIX = "OrderByUserDetailsDropDownMenu.";

	public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		this.setName(name);
		this.addMenuElement(ORDER_BY_GROUP_PATH, iwrb.getLocalizedString(CLASS_NAME_PREFIX + ORDER_BY_GROUP_PATH, "Group path"));
		this.addMenuElement(ORDER_BY_NAME, iwrb.getLocalizedString(CLASS_NAME_PREFIX + ORDER_BY_NAME, "Name"));
		this.addMenuElement(ORDER_BY_USER_STATUS, iwrb.getLocalizedString(CLASS_NAME_PREFIX + ORDER_BY_USER_STATUS, "User status"));
		this.addMenuElement(ORDER_BY_ADDRESS, iwrb.getLocalizedString(CLASS_NAME_PREFIX + ORDER_BY_ADDRESS, "Address"));
		this.addMenuElement(ORDER_BY_POSTAL_ADDRESS, iwrb.getLocalizedString(CLASS_NAME_PREFIX + ORDER_BY_POSTAL_ADDRESS, "Postal address"));
		return this;
	}

	public String getDisplayForResultingObject(Object value, IWContext iwc) {
		if (value != null) {
			IWResourceBundle iwrb = getResourceBundle(iwc);
			return iwrb.getLocalizedString(CLASS_NAME_PREFIX + value);
		}
		else {
			return "";
		}
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
}