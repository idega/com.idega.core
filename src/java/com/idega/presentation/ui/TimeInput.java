/*
 * $Id: TimeInput.java,v 1.12 2006/04/09 12:13:15 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 *
 */
package com.idega.presentation.ui;

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
		this.theHour = new DropdownMenu(name + "_hour");
		this.theMinute = new DropdownMenu(name + "_minute");

		this.theWholeTime = new Parameter(name, "");
		this.script = new Script();
		super.add(this.theHour);
		super.add(this.theMinute);
		super.add(this.theWholeTime);
		super.add(this.script);
	}

	public void setMinute(int minute) {
		setMinute(Integer.toString(minute));
	}

	public void setMinute(String minute) {
		if (minute.length() > 1) {
			this.theMinute.setSelectedElement(minute);
		}
		else {
			this.theMinute.setSelectedElement("0" + minute);
		}
	}

	public void setHour(int hour) {
		setHour(Integer.toString(hour));
	}

	public void setHour(String hour) {
		this.setCheck = true;
		if (hour.length() > 1) {
			this.theHour.setSelectedElement(hour);
		}
		else {
			this.theHour.setSelectedElement("0" + hour);
		}
	}

	@Override
	public void main(IWContext iwc) {
		this.theHour.setOnChange("setValueOfHiddenTime(this.form." + this.theHour.getName() + ",this.form." + this.theMinute.getName() + ",this.form." + this.theWholeTime.getName()+ ")");
		this.theMinute.setOnChange("setValueOfHiddenTime(this.form." + this.theHour.getName() + ",this.form." + this.theMinute.getName() + ",this.form." + this.theWholeTime.getName() + ")");
		this.theHour.setParentObject(this.getParentObject());
		this.theMinute.setParentObject(this.getParentObject());

		for (int a = this.iFromHour; a <= this.iToHour; a++) {
			this.theHour.addMenuElement(TextSoap.addZero(a), TextSoap.addZero(a));
		}

		for (int a = 0; a < 60; a = a + this.iMinuteInterval) {
			this.theMinute.addMenuElement(TextSoap.addZero(a), TextSoap.addZero(a));
		}

		getJavaScript().addFunction("setValueOfHiddenTime", "function setValueOfHiddenTime(hourInput,minuteInput,hiddenInput){\r\r	var hourValue='00';\r	var minuteValue='00';\r	var secondValue='00';\r	var millisecondValue='000000';\r	\r	\r	if(hourInput.selectedIndex != 0){\r		hourValue=hourInput.options[hourInput.selectedIndex].value;\r	}\r	if(minuteInput.selectedIndex != 0){\r		minuteValue=minuteInput.options[minuteInput.selectedIndex].value;\r	}\r\r\r	if ((hourInput.selectedIndex == 0) || (minuteInput.selectedIndex == 0) ){\r		hiddenInput.value = '';\r	}\r	else{\r		hiddenInput.value = hourValue+':'+minuteValue+':'+secondValue+'.'+millisecondValue;\r	}\r}");

		IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
		String emptyString = "";
		this.theHour.addMenuElementFirst(emptyString, iwrb.getLocalizedString(TimeInput.HOUR_KEY));
		this.theMinute.addMenuElementFirst(emptyString, iwrb.getLocalizedString(TimeInput.MINUTE_KEY));
		if (this.isDisabled) {
			this.theHour.setDisabled(true);
			this.theMinute.setDisabled(true);
		}
		if (this.keepStatus) {
			handleKeepStatus(iwc);
		}
	}

	public void setTime(java.sql.Time time) {
		IWTimestamp stamp = new IWTimestamp(time);

		setHour(TextSoap.addZero(stamp.getHour()));
		setMinute(TextSoap.addZero(stamp.getMinute()));
	}

	public String getHourName() {
		return this.theHour.getName();
	}

	public String getMinuteName() {
		return this.theMinute.getName();
	}

	private Script getJavaScript() {
		return this.script;
	}

	@Override
	public void setStyleAttribute(String style) {
		this.theHour.setStyleAttribute(style);
		this.theMinute.setStyleAttribute(style);
	}

	@Override
	public void setStyleClass(String styleName) {
		this.theHour.setStyleClass(styleName);
		this.theMinute.setStyleClass(styleName);
	}

	/**
	 * *Does nothing - overrides function in superclass
	 */
	@Override
	public void add(PresentationObject mo) {
		// does nothing
	}

	@Override
	public void print(IWContext iwc) throws Exception {
		if (this.setCheck == true) {
			this.theWholeTime.setValue(this.theHour.getSelectedElementValue() + ":" + this.theMinute.getSelectedElementValue() + ":00.000000");
		}
		super.print(iwc);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(com.idega.presentation.IWContext)
	 */
	@Override
	public void handleKeepStatus(IWContext iwc) {
		try {
			super.handleKeepStatus(iwc);
		} catch (AssertionError e) {
			return;
		}

		String lastValue = iwc.getParameter(this.theWholeTime.getName());
		if (lastValue != null) {
			setContent(lastValue);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	@Override
	public boolean isContainer() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.presentation.ui.InterfaceObject#setContent(java.lang.String)
	 */
	@Override
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

	@Override
	public void setDisabled(boolean disabled) {
		this.isDisabled = disabled;
	}

	public void setFromHour(int fromHour) {
		this.iFromHour = fromHour;
	}

	public void setMinuteInterval(int minuteInterval) {
		this.iMinuteInterval = minuteInterval;
	}

	public void setToHour(int toHour) {
		this.iToHour = toHour;
	}
}