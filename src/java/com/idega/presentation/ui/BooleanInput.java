//idega 2000 - Tryggvi Larusson
/*

*Copyright 2000 idega.is All Rights Reserved.

*/
package com.idega.presentation.ui;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class BooleanInput extends DropdownMenu {
	private static final String NO_KEY = "booleaninput.no";
	private static final String YES_KEY = "booleaninput.yes";
	private static final String SELECT_KEY = "booleaninput.select";
	private boolean _showSelectOption = false;
	
	public BooleanInput() {
		this("booleaninput");
	}
	public BooleanInput(String name) {
		super(name);
		addMenuElement("N");
		addMenuElement("Y");
	}
	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		IWBundle iwb = this.getBundle(iwc);
		IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
		if (_showSelectOption) {
			this.addMenuElementFirst("", iwrb.getLocalizedString(SELECT_KEY, "Select:"));
		}
		setMenuElementDisplayString("N", iwrb.getLocalizedString(NO_KEY,"No"));
		setMenuElementDisplayString("Y", iwrb.getLocalizedString(YES_KEY,"Yes"));
	}
	public void setSelected(boolean selected) {
		if (selected) {
			this.setSelectedElement("Y");
			//System.out.println("Setting selected=true for BooleanInput");
		}
		else {
			this.setSelectedElement("N");
			//System.out.println("Setting selected=false for BooleanInput");
		}
	}
	public void displayOnlyBooleanOptions() {
		_showSelectOption = false;
	}
	public void displaySelectOption() {
		this._showSelectOption = true;
	}
	
	public static boolean getBooleanReturnValue(String value) {
		if (value.equalsIgnoreCase("Y")) {
			return true;
		}
		else {
			return false;
		}
	}
}