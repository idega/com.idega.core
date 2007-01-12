/*
 * $Id: TimeInput.java,v 1.8.2.1 2007/01/12 19:32:08 idegaweb Exp $
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
import com.idega.util.IWTimestamp;
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
	private boolean isDisabled = false;
	
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
		this.theHour = new DropdownMenu(name + "_hour");
		this.theMinute = new DropdownMenu(name + "_minute");
		
		
		//theSecond = new DropdownMenu(name+"_second");
		this.theWholeTime = new Parameter(name, "");
		this.script = new Script();
		super.add(this.theHour);
		super.add(this.theMinute);
		super.add(this.theWholeTime);
		super.add(this.script);
		//super.add(theSecond);
		this.theHour.setOnChange(
			"setValueOfHiddenTime(this.form."
				+ this.theHour.getName()
				+ ",this.form."
				+ this.theMinute.getName()
				+ ",this.form."
				+ this.theWholeTime.getName()
				+ ")");
		this.theMinute.setOnChange(
			"setValueOfHiddenTime(this.form."
				+ this.theHour.getName()
				+ ",this.form."
				+ this.theMinute.getName()
				+ ",this.form."
				+ this.theWholeTime.getName()
				+ ")");
		this.theHour.setParentObject(this.getParentObject());
		this.theMinute.setParentObject(this.getParentObject());
		//theSecond.setParentObject(this.getParentObject());
		//theHour.addMenuElement("","Klst");
		this.theHour.addMenuElement("00", "00");
		this.theHour.addMenuElement("01", "01");
		this.theHour.addMenuElement("02", "02");
		this.theHour.addMenuElement("03", "03");
		this.theHour.addMenuElement("04", "04");
		this.theHour.addMenuElement("05", "05");
		this.theHour.addMenuElement("06", "06");
		this.theHour.addMenuElement("07", "07");
		this.theHour.addMenuElement("08", "08");
		this.theHour.addMenuElement("09", "09");
		this.theHour.addMenuElement("10", "10");
		this.theHour.addMenuElement("11", "11");
		this.theHour.addMenuElement("12", "12");
		this.theHour.addMenuElement("13", "13");
		this.theHour.addMenuElement("14", "14");
		this.theHour.addMenuElement("15", "15");
		this.theHour.addMenuElement("16", "16");
		this.theHour.addMenuElement("17", "17");
		this.theHour.addMenuElement("18", "18");
		this.theHour.addMenuElement("19", "19");
		this.theHour.addMenuElement("20", "20");
		this.theHour.addMenuElement("21", "21");
		this.theHour.addMenuElement("22", "22");
		this.theHour.addMenuElement("23", "23");
		//theMinute.addMenuElement("","M�n�tur");
		this.theMinute.addMenuElement("00", "00");
		this.theMinute.addMenuElement("01", "01");
		this.theMinute.addMenuElement("02", "02");
		this.theMinute.addMenuElement("03", "03");
		this.theMinute.addMenuElement("04", "04");
		this.theMinute.addMenuElement("05", "05");
		this.theMinute.addMenuElement("06", "06");
		this.theMinute.addMenuElement("07", "07");
		this.theMinute.addMenuElement("08", "08");
		this.theMinute.addMenuElement("09", "09");
		this.theMinute.addMenuElement("10", "10");
		this.theMinute.addMenuElement("11", "11");
		this.theMinute.addMenuElement("12", "12");
		this.theMinute.addMenuElement("13", "13");
		this.theMinute.addMenuElement("14", "14");
		this.theMinute.addMenuElement("15", "15");
		this.theMinute.addMenuElement("16", "16");
		this.theMinute.addMenuElement("17", "17");
		this.theMinute.addMenuElement("18", "18");
		this.theMinute.addMenuElement("19", "19");
		this.theMinute.addMenuElement("20", "20");
		this.theMinute.addMenuElement("21", "21");
		this.theMinute.addMenuElement("22", "22");
		this.theMinute.addMenuElement("23", "23");
		this.theMinute.addMenuElement("24", "24");
		this.theMinute.addMenuElement("25", "25");
		this.theMinute.addMenuElement("26", "26");
		this.theMinute.addMenuElement("27", "27");
		this.theMinute.addMenuElement("28", "28");
		this.theMinute.addMenuElement("29", "29");
		this.theMinute.addMenuElement("30", "30");
		this.theMinute.addMenuElement("31", "31");
		this.theMinute.addMenuElement("32", "32");
		this.theMinute.addMenuElement("33", "33");
		this.theMinute.addMenuElement("34", "34");
		this.theMinute.addMenuElement("35", "35");
		this.theMinute.addMenuElement("36", "36");
		this.theMinute.addMenuElement("37", "37");
		this.theMinute.addMenuElement("38", "38");
		this.theMinute.addMenuElement("39", "39");
		this.theMinute.addMenuElement("40", "40");
		this.theMinute.addMenuElement("41", "41");
		this.theMinute.addMenuElement("42", "42");
		this.theMinute.addMenuElement("43", "43");
		this.theMinute.addMenuElement("44", "44");
		this.theMinute.addMenuElement("45", "45");
		this.theMinute.addMenuElement("46", "46");
		this.theMinute.addMenuElement("47", "47");
		this.theMinute.addMenuElement("48", "48");
		this.theMinute.addMenuElement("49", "49");
		this.theMinute.addMenuElement("50", "50");
		this.theMinute.addMenuElement("51", "51");
		this.theMinute.addMenuElement("52", "52");
		this.theMinute.addMenuElement("53", "53");
		this.theMinute.addMenuElement("54", "54");
		this.theMinute.addMenuElement("55", "55");
		this.theMinute.addMenuElement("56", "56");
		this.theMinute.addMenuElement("57", "57");
		this.theMinute.addMenuElement("58", "58");
		this.theMinute.addMenuElement("59", "59");
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
			this.theMinute.setSelectedElement(minute);
		}
		else
		{
			this.theMinute.setSelectedElement("0" + minute);
		}
	}
	public void setHour(int hour)
	{
		setHour(Integer.toString(hour));
	}
	public void setHour(String hour)
	{
		this.setCheck = true;
		if (hour.length() > 1)
		{
			this.theHour.setSelectedElement(hour);
		}
		else
		{
			this.theHour.setSelectedElement("0" + hour);
		}
	}
	public void main(IWContext iwc)
	{
		IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
		String emptyString = "";
		this.theHour.addMenuElementFirst(emptyString, iwrb.getLocalizedString(TimeInput.HOUR_KEY));
		this.theMinute.addMenuElementFirst(emptyString, iwrb.getLocalizedString(TimeInput.MINUTE_KEY));
		
		if(this.isDisabled){
			this.theHour.setDisabled(true);
			this.theMinute.setDisabled(true);
		}
		
		if(this.keepStatus){
			handleKeepStatus(iwc);
		}
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
		return this.theHour.getName();
	}
	public String getMinuteName()
	{
		return this.theMinute.getName();
	}
	private Script getJavaScript()
	{
		return this.script;
	}
	public void keepStatusOnAction()
	{
		this.keepStatus = true;
		//theSecond.keepStatusOnAction();
	}
	public void setStyleAttribute(String style)
	{
		this.theHour.setStyleAttribute(style);
		this.theMinute.setStyleAttribute(style);
	}
	public void setStyleClass(String styleName) 
	{
		this.theHour.setStyleClass(styleName);
		this.theMinute.setStyleClass(styleName);
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
		if (this.setCheck == true)
		{
			this.theWholeTime.setValue(
				this.theHour.getSelectedElementValue() + ":" + this.theMinute.getSelectedElementValue() + ":00.000000");
		}
		super.print(iwc);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(com.idega.presentation.IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		String lastValue = iwc.getParameter(this.theWholeTime.getName());
		if(lastValue!=null){
			setContent(lastValue);
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.ui.InterfaceObject#setContent(java.lang.String)
	 */
	public void setContent(String content) {
		if(!"".equals(content)){
			String dummyDate = "2005-01-01 ";
			dummyDate+=content;
			IWTimestamp stamp = new IWTimestamp(dummyDate);
			if (stamp != null) {
				setHour(stamp.getHour());
				setMinute(stamp.getMinute());
			}
		}
	}
	
	public void setDisabled(boolean disabled) {
		this.isDisabled = disabled;
	}
	
}