/*
 * Created on Dec 19, 2003
 *
 */
package com.idega.presentation.ui;

import java.util.Collection;
import java.util.Iterator;

import com.idega.idegaweb.presentation.SmallCalendar;
import com.idega.presentation.IWContext;

/**
 * DatePickerWindow
 * @author aron 
 * @version 1.0
 */
public class DatePickerWindow extends AbstractChooserWindow {
	
	
	public DatePickerWindow() {
		//setAutoResize(true);
		setEmpty();
		setScrollbar(false);
		setResizable(false);
		setTitlebar(false);
		setHeight(140);
		setWidth(140);
		setMousePositionOffsets(0,0);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.ui.AbstractChooserWindow#displaySelection(com.idega.presentation.IWContext)
	 */
	public void displaySelection(IWContext iwc) {
		
		String selPrm = getSelectionParameter(iwc);
		String selVal = iwc.getParameter(selPrm);
		if(selVal==null)
			selVal = new java.util.Date().toString();
		
		SmallCalendar cal = new SmallCalendar();
		cal.setDaysAsLink(true);
		cal.setOnClickMessageFormat(getOnSelectionCode("{1}","{0}"));
		//"document.forms[0]."+selPrm+".value= {0} ;"+
		
		Collection parameters = getHiddenParameters(iwc);
		for (Iterator iter = parameters.iterator(); iter.hasNext();) {
			Parameter prm = (Parameter) iter.next();
			cal.addParameterToLink(prm.getName(),prm.getValueAsString());
		}
		
		add(cal);
		add(new HiddenInput(selPrm,selVal));
		
	}
	
	
	
	
	
	
}
