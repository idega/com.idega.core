/*
 * $Id: TimeInput.java,v 1.10 2005/12/12 05:44:37 laddi Exp $
 * 
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 * 
 */
package com.idega.presentation.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;
import com.idega.util.IWTimestamp;
import com.idega.util.text.TextSoap;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class TimeInput extends InterfaceObject {

	private Script script;
	private DropdownMenu theHour;
	private DropdownMenu theMinute;
	
	private Parameter theWholeTime;
	private boolean setCheck = false;
	private boolean isDisabled = false;

	private int iMinuteInterval = 1;
	private int iFromHour = 0;
	private int iToHour = 23;
	
	final static String HOUR_KEY = "timeinput.hour";
	final static String MINUTE_KEY = "timeinput.minute";
	final static String HOUR_KEY_S = "timeinput.hour_short";
	final static String MINUTE_KEY_S = "timeinput.minute_short";

	public TimeInput() {
		this("timeinput");
	}

	public TimeInput(String name) {
		super();
		theHour = new DropdownMenu(name + "_hour");
		theMinute = new DropdownMenu(name + "_minute");
		
		theWholeTime = new Parameter(name, "");
		script = new Script();
		super.add(theHour);
		super.add(theMinute);
		super.add(theWholeTime);
		super.add(script);
	}

	public void setMinute(int minute) {
		setMinute(Integer.toString(minute));
	}

	public void setMinute(String minute) {
		if (minute.length() > 1) {
			theMinute.setSelectedElement(minute);
		}
		else {
			theMinute.setSelectedElement("0" + minute);
		}
	}

	public void setHour(int hour) {
		setHour(Integer.toString(hour));
	}

	public void setHour(String hour) {
		setCheck = true;
		if (hour.length() > 1) {
			theHour.setSelectedElement(hour);
		}
		else {
			theHour.setSelectedElement("0" + hour);
		}
	}

	public void main(IWContext iwc) {
		theHour.setOnChange("setValueOfHiddenTime(this.form." + theHour.getName() + ",this.form." + theMinute.getName() + ",this.form." + theWholeTime.getName()+ ")");
		theMinute.setOnChange("setValueOfHiddenTime(this.form." + theHour.getName() + ",this.form." + theMinute.getName() + ",this.form." + theWholeTime.getName() + ")");
		theHour.setParentObject(this.getParentObject());
		theMinute.setParentObject(this.getParentObject());

		for (int a = iFromHour; a <= iToHour; a++) {
			theHour.addMenuElement(TextSoap.addZero(a), TextSoap.addZero(a));
		}

		for (int a = 0; a < 60; a = a + iMinuteInterval) {
			theMinute.addMenuElement(TextSoap.addZero(a), TextSoap.addZero(a));
		}
		
		getJavaScript().addFunction("setValueOfHiddenTime", "function setValueOfHiddenTime(hourInput,minuteInput,hiddenInput){\r\r	var hourValue='00';\r	var minuteValue='00';\r	var secondValue='00';\r	var millisecondValue='000000';\r	\r	\r	if(hourInput.selectedIndex != 0){\r		hourValue=hourInput.options[hourInput.selectedIndex].value;\r	}\r	if(minuteInput.selectedIndex != 0){\r		minuteValue=minuteInput.options[minuteInput.selectedIndex].value;\r	}\r\r\r	if ((hourInput.selectedIndex == 0) || (minuteInput.selectedIndex == 0) ){\r		hiddenInput.value = '';\r	}\r	else{\r		hiddenInput.value = hourValue+':'+minuteValue+':'+secondValue+'.'+millisecondValue;\r	}\r}");

		IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
		String emptyString = "";
		theHour.addMenuElementFirst(emptyString, iwrb.getLocalizedString(TimeInput.HOUR_KEY));
		theMinute.addMenuElementFirst(emptyString, iwrb.getLocalizedString(TimeInput.MINUTE_KEY));
		if (isDisabled) {
			theHour.setDisabled(true);
			theMinute.setDisabled(true);
		}
		if (keepStatus) {
			handleKeepStatus(iwc);
		}
	}

	public void setTime(java.sql.Time time) {
		GregorianCalendar greg = new GregorianCalendar();
		greg.setTime(new Date(time.getTime()));

		setHour(greg.get(Calendar.HOUR_OF_DAY));
		setMinute(greg.get(Calendar.MINUTE));
	}

	public String getHourName() {
		return theHour.getName();
	}

	public String getMinuteName() {
		return theMinute.getName();
	}

	private Script getJavaScript() {
		return this.script;
	}

	public void setStyleAttribute(String style) {
		theHour.setStyleAttribute(style);
		theMinute.setStyleAttribute(style);
	}

	public void setStyleClass(String styleName) {
		theHour.setStyleClass(styleName);
		theMinute.setStyleClass(styleName);
	}

	/**
	 * *Does nothing - overrides function in superclass
	 */
	public void add(PresentationObject mo) {
		// does nothing
	}

	public void print(IWContext iwc) throws Exception {
		if (setCheck == true) {
			theWholeTime.setValue(theHour.getSelectedElementValue() + ":" + theMinute.getSelectedElementValue() + ":00.000000");
		}
		super.print(iwc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(com.idega.presentation.IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		String lastValue = iwc.getParameter(theWholeTime.getName());
		if (lastValue != null) {
			setContent(lastValue);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.presentation.ui.InterfaceObject#setContent(java.lang.String)
	 */
	public void setContent(String content) {
		if (!"".equals(content)) {
			String dummyDate = "2005-01-01 ";
			dummyDate += content;
			IWTimestamp stamp = new IWTimestamp(dummyDate);
			if (stamp != null) {
				setHour(stamp.getHour());
				setMinute(stamp.getMinute());
			}
		}
	}

	public void setDisabled(boolean disabled) {
		isDisabled = disabled;
	}

	public void setFromHour(int fromHour) {
		iFromHour = fromHour;
	}
	
	public void setMinuteInterval(int minuteInterval) {
		iMinuteInterval = minuteInterval;
	}
	
	public void setToHour(int toHour) {
		iToHour = toHour;
	}
}