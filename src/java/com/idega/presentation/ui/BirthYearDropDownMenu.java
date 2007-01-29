/*
 * $Id: BirthYearDropDownMenu.java,v 1.3 2007/01/29 23:47:31 idegaweb Exp $
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
import com.idega.util.IWTimestamp;
/**
 * A presentation object for dynamic reports. Select an age (1-123).
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 */
public class BirthYearDropDownMenu extends DropDownMenuInputHandler {

	protected static String IW_BUNDLE_IDENTIFIER = "com.idega.user";
	
	public BirthYearDropDownMenu() {
		super();
	}

	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getResourceBundle(iwc);
	
		IWTimestamp stamp = IWTimestamp.RightNow();		
		int currentYear = stamp.getYear();
		int beginningYear = 1900;				

		addMenuElement(" ",iwrb.getLocalizedString("BirthYearDropdownmenu.all_ages", "All birthYears"));
		for (int i = currentYear; i >= beginningYear; i--) {
			addMenuElement(i, Integer.toString(i));
		}
		String selectedElement = getSelectedElementValue();
		if (selectedElement == null || selectedElement.length() == 0) {
			setSelectedElement(" ");
		}
	}
	
	/**
	 * @return the year, Integer
	 *  
	 */
	public Object getResultingObject(String[] values, IWContext iwc) throws Exception {
		if (values != null && values.length > 0) {
			String birthYear = values[0];
			if (" ".equals(birthYear)) {
				return null;
			}
			else {
				return new Integer(birthYear);
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
			return value.toString();
		}
		else {
			return iwrb.getLocalizedString("BirthYearDropdownmenu.all_ages", "All birthYears");
		}
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#getBundleIdentifier()
	 */
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}


}
