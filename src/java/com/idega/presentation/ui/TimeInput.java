/*
 * $Id: TimeInput.java,v 1.7 2004/09/23 15:53:28 gimmi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
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
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class TimeInput extends InterfaceObject
{
	private Script script;
	private DropdownMenu theHour;
	private DropdownMenu theMinute;
	//private DropdownMenu theSecond;
	private Parameter theWholeTime;
	private boolean setCheck = false;
	final static String HOUR_KEY = "timeinput.hour";
	final static String MINUTE_KEY = "timeinput.minute";
	final static String HOUR_KEY_S = "timeinput.hour_short";
	final static String MINUTE_KEY_S = "timeinput.minute_short";
	public TimeInput()
	{
		this("timeinput");
	}
	public TimeInput(String name)
	{
		super();
		theHour = new DropdownMenu(name + "_hour");
		theMinute = new DropdownMenu(name + "_minute");
		//theSecond = new DropdownMenu(name+"_second");
		theWholeTime = new Parameter(name, "");
		script = new Script();
		super.add(theHour);
		super.add(theMinute);
		super.add(theWholeTime);
		super.add(script);
		//super.add(theSecond);
		theHour.setOnChange(
			"setValueOfHiddenTime(this.form."
				+ theHour.getName()
				+ ",this.form."
				+ theMinute.getName()
				+ ",this.form."
				+ theWholeTime.getName()
				+ ")");
		theMinute.setOnChange(
			"setValueOfHiddenTime(this.form."
				+ theHour.getName()
				+ ",this.form."
				+ theMinute.getName()
				+ ",this.form."
				+ theWholeTime.getName()
				+ ")");
		theHour.setParentObject(this.getParentObject());
		theMinute.setParentObject(this.getParentObject());
		//theSecond.setParentObject(this.getParentObject());
		//theHour.addMenuElement("","Klst");
		theHour.addMenuElement("00", "00");
		theHour.addMenuElement("01", "01");
		theHour.addMenuElement("02", "02");
		theHour.addMenuElement("03", "03");
		theHour.addMenuElement("04", "04");
		theHour.addMenuElement("05", "05");
		theHour.addMenuElement("06", "06");
		theHour.addMenuElement("07", "07");
		theHour.addMenuElement("08", "08");
		theHour.addMenuElement("09", "09");
		theHour.addMenuElement("10", "10");
		theHour.addMenuElement("11", "11");
		theHour.addMenuElement("12", "12");
		theHour.addMenuElement("13", "13");
		theHour.addMenuElement("14", "14");
		theHour.addMenuElement("15", "15");
		theHour.addMenuElement("16", "16");
		theHour.addMenuElement("17", "17");
		theHour.addMenuElement("18", "18");
		theHour.addMenuElement("19", "19");
		theHour.addMenuElement("20", "20");
		theHour.addMenuElement("21", "21");
		theHour.addMenuElement("22", "22");
		theHour.addMenuElement("23", "23");
		//theMinute.addMenuElement("","Mínútur");
		theMinute.addMenuElement("00", "00");
		theMinute.addMenuElement("01", "01");
		theMinute.addMenuElement("02", "02");
		theMinute.addMenuElement("03", "03");
		theMinute.addMenuElement("04", "04");
		theMinute.addMenuElement("05", "05");
		theMinute.addMenuElement("06", "06");
		theMinute.addMenuElement("07", "07");
		theMinute.addMenuElement("08", "08");
		theMinute.addMenuElement("09", "09");
		theMinute.addMenuElement("10", "10");
		theMinute.addMenuElement("11", "11");
		theMinute.addMenuElement("12", "12");
		theMinute.addMenuElement("13", "13");
		theMinute.addMenuElement("14", "14");
		theMinute.addMenuElement("15", "15");
		theMinute.addMenuElement("16", "16");
		theMinute.addMenuElement("17", "17");
		theMinute.addMenuElement("18", "18");
		theMinute.addMenuElement("19", "19");
		theMinute.addMenuElement("20", "20");
		theMinute.addMenuElement("21", "21");
		theMinute.addMenuElement("22", "22");
		theMinute.addMenuElement("23", "23");
		theMinute.addMenuElement("24", "24");
		theMinute.addMenuElement("25", "25");
		theMinute.addMenuElement("26", "26");
		theMinute.addMenuElement("27", "27");
		theMinute.addMenuElement("28", "28");
		theMinute.addMenuElement("29", "29");
		theMinute.addMenuElement("30", "30");
		theMinute.addMenuElement("31", "31");
		theMinute.addMenuElement("32", "32");
		theMinute.addMenuElement("33", "33");
		theMinute.addMenuElement("34", "34");
		theMinute.addMenuElement("35", "35");
		theMinute.addMenuElement("36", "36");
		theMinute.addMenuElement("37", "37");
		theMinute.addMenuElement("38", "38");
		theMinute.addMenuElement("39", "39");
		theMinute.addMenuElement("40", "40");
		theMinute.addMenuElement("41", "41");
		theMinute.addMenuElement("42", "42");
		theMinute.addMenuElement("43", "43");
		theMinute.addMenuElement("44", "44");
		theMinute.addMenuElement("45", "45");
		theMinute.addMenuElement("46", "46");
		theMinute.addMenuElement("47", "47");
		theMinute.addMenuElement("48", "48");
		theMinute.addMenuElement("49", "49");
		theMinute.addMenuElement("50", "50");
		theMinute.addMenuElement("51", "51");
		theMinute.addMenuElement("52", "52");
		theMinute.addMenuElement("53", "53");
		theMinute.addMenuElement("54", "54");
		theMinute.addMenuElement("55", "55");
		theMinute.addMenuElement("56", "56");
		theMinute.addMenuElement("57", "57");
		theMinute.addMenuElement("58", "58");
		theMinute.addMenuElement("59", "59");
		/*
		
		theSecond.addMenuElement("00","00");
		
		theSecond.addMenuElement("01","01");
		
		theSecond.addMenuElement("02","02");
		
		theSecond.addMenuElement("03","03");
		
		theSecond.addMenuElement("04","04");
		
		theSecond.addMenuElement("05","05");
		
		theSecond.addMenuElement("06","06");
		
		theSecond.addMenuElement("07","07");
		
		theSecond.addMenuElement("08","08");
		
		theSecond.addMenuElement("09","09");
		
		
		
		theSecond.addMenuElement("10","10");
		
		theSecond.addMenuElement("11","11");
		
		theSecond.addMenuElement("12","12");
		
		theSecond.addMenuElement("13","13");
		
		theSecond.addMenuElement("14","14");
		
		theSecond.addMenuElement("15","15");
		
		theSecond.addMenuElement("16","16");
		
		theSecond.addMenuElement("17","17");
		
		theSecond.addMenuElement("18","18");
		
		theSecond.addMenuElement("19","19");
		
		
		
		theSecond.addMenuElement("20","20");
		
		theSecond.addMenuElement("21","21");
		
		theSecond.addMenuElement("22","22");
		
		theSecond.addMenuElement("23","23");
		
		theSecond.addMenuElement("24","24");
		
		theSecond.addMenuElement("25","25");
		
		theSecond.addMenuElement("26","26");
		
		theSecond.addMenuElement("27","27");
		
		theSecond.addMenuElement("28","28");
		
		theSecond.addMenuElement("29","29");
		
		
		
		theSecond.addMenuElement("30","30");
		
		theSecond.addMenuElement("31","31");
		
		theSecond.addMenuElement("32","32");
		
		theSecond.addMenuElement("33","33");
		
		theSecond.addMenuElement("34","34");
		
		theSecond.addMenuElement("35","35");
		
		theSecond.addMenuElement("36","36");
		
		theSecond.addMenuElement("37","37");
		
		theSecond.addMenuElement("38","38");
		
		theSecond.addMenuElement("39","39");
		
		
		
		theSecond.addMenuElement("40","40");
		
		theSecond.addMenuElement("41","41");
		
		theSecond.addMenuElement("42","42");
		
		theSecond.addMenuElement("43","43");
		
		theSecond.addMenuElement("44","44");
		
		theSecond.addMenuElement("45","45");
		
		theSecond.addMenuElement("46","46");
		
		theSecond.addMenuElement("47","47");
		
		theSecond.addMenuElement("48","48");
		
		theSecond.addMenuElement("49","49");
		
		
		
		theSecond.addMenuElement("50","50");
		
		theSecond.addMenuElement("51","51");
		
		theSecond.addMenuElement("52","52");
		
		theSecond.addMenuElement("53","53");
		
		theSecond.addMenuElement("54","54");
		
		theSecond.addMenuElement("55","55");
		
		theSecond.addMenuElement("56","56");
		
		theSecond.addMenuElement("57","57");
		
		theSecond.addMenuElement("58","58");
		
		theSecond.addMenuElement("59","59");
		
		*/
		getJavaScript().addFunction(
			"setValueOfHiddenTime",
			"function setValueOfHiddenTime(hourInput,minuteInput,hiddenInput){\r\r	var hourValue='00';\r	var minuteValue='00';\r	var secondValue='00';\r	var millisecondValue='000000';\r	\r	\r	if(hourInput.selectedIndex != 0){\r		hourValue=hourInput.options[hourInput.selectedIndex].value;\r	}\r	if(minuteInput.selectedIndex != 0){\r		minuteValue=minuteInput.options[minuteInput.selectedIndex].value;\r	}\r\r\r	if ((hourInput.selectedIndex == 0) || (minuteInput.selectedIndex == 0) ){\r		hiddenInput.value = '';\r	}\r	else{\r		hiddenInput.value = hourValue+':'+minuteValue+':'+secondValue+'.'+millisecondValue;\r	}\r}");
	}
	public void setMinute(int minute)
	{
		//theMinute.setSelectedElement(Integer.toString(minute));
		setMinute(Integer.toString(minute));
	}
	public void setMinute(String minute)
	{
		if (minute.length() > 1)
		{
			theMinute.setSelectedElement(minute);
		}
		else
		{
			theMinute.setSelectedElement("0" + minute);
		}
	}
	public void setHour(int hour)
	{
		setHour(Integer.toString(hour));
	}
	public void setHour(String hour)
	{
		setCheck = true;
		if (hour.length() > 1)
		{
			theHour.setSelectedElement(hour);
		}
		else
		{
			theHour.setSelectedElement("0" + hour);
		}
	}
	public void main(IWContext iwc)
	{
		IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
		String emptyString = "";
		theHour.addMenuElementFirst(emptyString, iwrb.getLocalizedString(TimeInput.HOUR_KEY));
		theMinute.addMenuElementFirst(emptyString, iwrb.getLocalizedString(TimeInput.MINUTE_KEY));
	}
	public void setTime(java.sql.Time time)
	{
		//	setHour(time.getHours());
		//	setMinute(time.getMinutes());
		GregorianCalendar greg = new GregorianCalendar();
		greg.setTime(new Date(time.getTime()));
		setHour(greg.get(Calendar.HOUR_OF_DAY));
		setMinute(greg.get(Calendar.MINUTE));
		//setSecond(time.getMinutes());
	}
	/*
	
	public void setSecond(int second){
	
		theSecond.setSelected(Integer.toString(second));
	
	}
	
	
	
	public void setSecond(String second){
	
		theSecond.setSelected(second);
	
	}
	
	*/
	public String getHourName()
	{
		return theHour.getName();
	}
	public String getMinuteName()
	{
		return theMinute.getName();
	}
	private Script getJavaScript()
	{
		return this.script;
	}
	public void keepStatusOnAction()
	{
		theHour.keepStatusOnAction();
		theMinute.keepStatusOnAction();
		//theSecond.keepStatusOnAction();
	}
	public void setStyleAttribute(String style)
	{
		theHour.setStyleAttribute(style);
		theMinute.setStyleAttribute(style);
	}
	public void setStyleClass(String styleName) 
	{
		theHour.setStyleClass(styleName);
		theMinute.setStyleClass(styleName);
	}
	
	/**
	**Does nothing - overrides function in superclass
	**/
	public void add(PresentationObject mo)
	{
		//does nothing
	}
	public void print(IWContext iwc) throws Exception
	{
		if (setCheck == true)
		{
			theWholeTime.setValue(
				theHour.getSelectedElementValue() + ":" + theMinute.getSelectedElementValue() + ":00.000000");
		}
		super.print(iwc);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(com.idega.presentation.IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
}