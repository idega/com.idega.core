/*
 * $Id: BooleanInput.java,v 1.13 2007/04/18 17:26:24 civilis Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;

/**
 * <p>
 * This component presents a selection of a boolean value as a dropdown menu and presents the user with values Yes and No.
 * </p>
 *  Last modified: $Date: 2007/04/18 17:26:24 $ by $Author: civilis $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.13 $
 */
public class BooleanInput extends DropdownMenu {
	private static final String NO_KEY = "booleaninput.no";
	private static final String YES_KEY = "booleaninput.yes";
	private static final String SELECT_KEY = "booleaninput.select";
	private boolean _showSelectOption = false;


	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = Boolean.valueOf(this._showSelectOption);
		return values;
	}
	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this._showSelectOption = ((Boolean) values[1]).booleanValue();
	}

	/**
	 * Dedicated to jsf restore phase
	 *
	 */
	public BooleanInput() {
		super();
	}

	public BooleanInput(String name) {

		super(name == null ? "booleaninput" : name);

		addMenuElement(CoreConstants.N);
		addMenuElement(CoreConstants.Y);
	}
	@Override
	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		IWBundle iwb = this.getBundle(iwc);
		IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
		if (this._showSelectOption) {
			this.addMenuElementFirst("", iwrb.getLocalizedString(SELECT_KEY, "Select:"));
		}
		setMenuElementDisplayString(CoreConstants.N, iwrb.getLocalizedString(NO_KEY,"No"));
		setMenuElementDisplayString(CoreConstants.Y, iwrb.getLocalizedString(YES_KEY,"Yes"));
	}
	public void setSelected(boolean selected) {

		if (selected) {
			this.setSelectedElement(CoreConstants.Y);
		}
		else {
			this.setSelectedElement(CoreConstants.N);
		}
	}
	public void displayOnlyBooleanOptions() {
		this._showSelectOption = false;
	}
	public void displaySelectOption() {
		this._showSelectOption = true;
	}

	public static boolean getBooleanReturnValue(String value) {
		if (value.equalsIgnoreCase(CoreConstants.Y)) {
			return true;
		}
		else {
			return false;
		}
	}
}