/*
 * $Id: GenderDropDownMenu.java,v 1.1.2.1 2007/01/26 05:47:28 idegaweb Exp $
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
/**
 * A presentation object for dynamic reports to genders. Both,male or female. both is default.
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 */
public class GenderDropDownMenu extends DropDownMenuInputHandler {

	private static final String MALE = "m"; //same as in workreportmember
	private static final String FEMALE = "f"; //same as in workreportmember
	private static final String BOTH = "b";

	protected static String IW_BUNDLE_IDENTIFIER = "com.idega.user";

	public GenderDropDownMenu() {
		super();
	}

	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		this.addMenuElement(MALE, iwrb.getLocalizedString("GenderDropdownmenu.male", "Male"));
		this.addMenuElement(FEMALE, iwrb.getLocalizedString("GenderDropdownmenu.female", "Female"));
		this.addMenuElement(BOTH, iwrb.getLocalizedString("GenderDropdownmenu.both", "Both"));
		String selectedElement = getSelectedElementValue();
		if (selectedElement == null || selectedElement.length() == 0) {
			this.setSelectedElement(BOTH);
		}
	}
	
	/**
	 * @return the year, Integer
	 *  
	 */
	public Object getResultingObject(String[] values, IWContext iwc) throws Exception {
		if (values != null && values.length > 0) {
			String gender = values[0];
			if (BOTH.equals(gender)) {
				return null;
			}
			else {
				return gender;
			}
		}
		else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.business.InputHandler#getDisplayNameOfValue(java.lang.String, com.idega.presentation.IWContext)
	 */
	public String getDisplayForResultingObject(Object value, IWContext iwc) {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		if (value != null) {
			
			String displayName = "";
			if (BOTH.equals(value)) {
				displayName = iwrb.getLocalizedString("GenderDropdownmenu.both", "Both");
			}
			else if (MALE.equals(value)) {
					displayName = iwrb.getLocalizedString("GenderDropdownmenu.male", "Male");
			}
			else {
				displayName = iwrb.getLocalizedString("GenderDropdownmenu.female", "Female");
			}

			return displayName;
		}
		else {
			return iwrb.getLocalizedString("GenderDropdownmenu.both", "Both");
		}
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#getBundleIdentifier()
	 */
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

}
