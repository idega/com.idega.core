/*

 * $Id: TimestampInput.java,v 1.8.2.1 2007/01/12 19:32:06 idegaweb Exp $

 *

 * Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 * This software is the proprietary information of Idega hf.

 * Use is subject to license terms.

 *

 */

package com.idega.presentation.ui;



import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;
import com.idega.presentation.text.Text;
import com.idega.util.IWTimestamp;



/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/

public class TimestampInput extends InterfaceObject {



private Script script;



private DropdownMenu theDay;

private DropdownMenu theMonth;

private DropdownMenu theYear;



private DropdownMenu theHour;

private DropdownMenu theMinute;

//private DropdownMenu theSecond;

private Parameter theWholeTimestamp;

private boolean setCheck=false;

private boolean inShort=false;

private int fromYear;

private int toYear;

private int selectedYear=-1;

Text textInFront=new Text();



final static String THETIME_KEY = "timestampinput.thetime";







public TimestampInput(){

	this("timestampinput");

}



public TimestampInput(String name){

  init(name);

}



public TimestampInput(String name,boolean inShort){

  this.inShort = inShort;

  init(name);

}



private void init(String name){

	this.theDay = new DropdownMenu(name+"_day");

	this.theMonth = new DropdownMenu(name+"_month");

	this.theYear = new DropdownMenu(name+"_year");



	this.theHour = new DropdownMenu(name+"_hour");

	this.theMinute = new DropdownMenu(name+"_minute");

	//theSecond = new DropdownMenu(name+"_second");

	this.theWholeTimestamp = new Parameter(name,"");



        this.script = new Script();



	super.add(this.theDay);

	super.add(this.theMonth);

	super.add(this.theYear);



        super.add(this.textInFront);

	//super.add(new Text("klukkan"));



	super.add(this.theHour);

	super.add(this.theMinute);

	super.add(this.theWholeTimestamp);

	super.add(this.script);

	//super.add(theSecond);



	this.theYear.setOnChange("populateDays(this,this.form."+this.theMonth.getName()+",this.form."+this.theDay.getName()+");setValueOfHiddenTimestamp(this.form."+this.theYear.getName()+",this.form."+this.theMonth.getName()+",this.form."+this.theDay.getName()+",this.form."+this.theHour.getName()+",this.form."+this.theMinute.getName()+",this.form."+this.theWholeTimestamp.getName()+");");

	this.theMonth.setOnChange("populateDays(this.form."+this.theYear.getName()+",this,this.form."+this.theDay.getName()+");setValueOfHiddenTimestamp(this.form."+this.theYear.getName()+",this.form."+this.theMonth.getName()+",this.form."+this.theDay.getName()+",this.form."+this.theHour.getName()+",this.form."+this.theMinute.getName()+",this.form."+this.theWholeTimestamp.getName()+");");

	this.theDay.setOnChange("setValueOfHiddenTimestamp(this.form."+this.theYear.getName()+",this.form."+this.theMonth.getName()+",this.form."+this.theDay.getName()+",this.form."+this.theHour.getName()+",this.form."+this.theMinute.getName()+",this.form."+this.theWholeTimestamp.getName()+")");





	this.theHour.setOnChange("setValueOfHiddenTimestamp(this.form."+this.theYear.getName()+",this.form."+this.theMonth.getName()+",this.form."+this.theDay.getName()+",this.form."+this.theHour.getName()+",this.form."+this.theMinute.getName()+",this.form."+this.theWholeTimestamp.getName()+")");

	this.theMinute.setOnChange("setValueOfHiddenTimestamp(this.form."+this.theYear.getName()+",this.form."+this.theMonth.getName()+",this.form."+this.theDay.getName()+",this.form."+this.theHour.getName()+",this.form."+this.theMinute.getName()+",this.form."+this.theWholeTimestamp.getName()+")");







	this.theDay.setParentObject(this.getParentObject());

	this.theMonth.setParentObject(this.getParentObject());

	this.theYear.setParentObject(this.getParentObject());





	this.theHour.setParentObject(this.getParentObject());

	this.theMinute.setParentObject(this.getParentObject());

	//theSecond.setParentObject(this.getParentObject());





	//theYear.addMenuElement("","�r");



        IWTimestamp stamp = IWTimestamp.RightNow();

        int currentYear = stamp.getYear();

        this.setYearRange(currentYear,currentYear+5);



	//theYear.addMenuElement("2000","2000");

	//theYear.addMenuElement("2001","2001");

	//theYear.addMenuElement("2002","2002");



	//theMonth.addMenuElement("","M�nu�ur");



        /*

	theMonth.addMenuElement("01","jan�ar");

	theMonth.addMenuElement("02","febr�ar");

	theMonth.addMenuElement("03","mars");

	theMonth.addMenuElement("04","apr�l");

	theMonth.addMenuElement("05","ma�");

	theMonth.addMenuElement("06","j�n�");

	theMonth.addMenuElement("07","j�l�");

	theMonth.addMenuElement("08","�g�st");

	theMonth.addMenuElement("09","september");

	theMonth.addMenuElement("10","okt�ber");

	theMonth.addMenuElement("11","n�vember");

	theMonth.addMenuElement("12","desember");

        */



	this.theMonth.addMenuElement("01");

	this.theMonth.addMenuElement("02");

	this.theMonth.addMenuElement("03");

	this.theMonth.addMenuElement("04");

	this.theMonth.addMenuElement("05");

	this.theMonth.addMenuElement("06");

	this.theMonth.addMenuElement("07");

	this.theMonth.addMenuElement("08");

	this.theMonth.addMenuElement("09");

	this.theMonth.addMenuElement("10");

	this.theMonth.addMenuElement("11");

	this.theMonth.addMenuElement("12");



	//theDay.addMenuElement("","Dagur");



	this.theDay.addMenuElement("01","1");

	this.theDay.addMenuElement("02","2");

	this.theDay.addMenuElement("03","3");

	this.theDay.addMenuElement("04","4");

	this.theDay.addMenuElement("05","5");

	this.theDay.addMenuElement("06","6");

	this.theDay.addMenuElement("07","7");

	this.theDay.addMenuElement("08","8");

	this.theDay.addMenuElement("09","9");



	this.theDay.addMenuElement("10","10");

	this.theDay.addMenuElement("11","11");

	this.theDay.addMenuElement("12","12");

	this.theDay.addMenuElement("13","13");

	this.theDay.addMenuElement("14","14");

	this.theDay.addMenuElement("15","15");

	this.theDay.addMenuElement("16","16");

	this.theDay.addMenuElement("17","17");

	this.theDay.addMenuElement("18","18");

	this.theDay.addMenuElement("19","19");



	this.theDay.addMenuElement("20","20");

	this.theDay.addMenuElement("21","21");

	this.theDay.addMenuElement("22","22");

	this.theDay.addMenuElement("23","23");

	this.theDay.addMenuElement("24","24");

	this.theDay.addMenuElement("25","25");

	this.theDay.addMenuElement("26","26");

	this.theDay.addMenuElement("27","27");

	this.theDay.addMenuElement("28","28");

	this.theDay.addMenuElement("29","29");



	this.theDay.addMenuElement("30","30");

	this.theDay.addMenuElement("31","31");





	//theHour.addMenuElement("","Klst");



	this.theHour.addMenuElement("00","00");

	this.theHour.addMenuElement("01","01");

	this.theHour.addMenuElement("02","02");

	this.theHour.addMenuElement("03","03");

	this.theHour.addMenuElement("04","04");

	this.theHour.addMenuElement("05","05");

	this.theHour.addMenuElement("06","06");

	this.theHour.addMenuElement("07","07");

	this.theHour.addMenuElement("08","08");

	this.theHour.addMenuElement("09","09");

	this.theHour.addMenuElement("10","10");

	this.theHour.addMenuElement("11","11");

	this.theHour.addMenuElement("12","12");

	this.theHour.addMenuElement("13","13");

	this.theHour.addMenuElement("14","14");

	this.theHour.addMenuElement("15","15");

	this.theHour.addMenuElement("16","16");

	this.theHour.addMenuElement("17","17");

	this.theHour.addMenuElement("18","18");

	this.theHour.addMenuElement("19","19");



	this.theHour.addMenuElement("20","20");

	this.theHour.addMenuElement("21","21");

	this.theHour.addMenuElement("22","22");

	this.theHour.addMenuElement("23","23");





	//theMinute.addMenuElement("","M�n�tur");



	this.theMinute.addMenuElement("00","00");

	this.theMinute.addMenuElement("01","01");

	this.theMinute.addMenuElement("02","02");

	this.theMinute.addMenuElement("03","03");

	this.theMinute.addMenuElement("04","04");

	this.theMinute.addMenuElement("05","05");

	this.theMinute.addMenuElement("06","06");

	this.theMinute.addMenuElement("07","07");

	this.theMinute.addMenuElement("08","08");

	this.theMinute.addMenuElement("09","09");



	this.theMinute.addMenuElement("10","10");

	this.theMinute.addMenuElement("11","11");

	this.theMinute.addMenuElement("12","12");

	this.theMinute.addMenuElement("13","13");

	this.theMinute.addMenuElement("14","14");

	this.theMinute.addMenuElement("15","15");

	this.theMinute.addMenuElement("16","16");

	this.theMinute.addMenuElement("17","17");

	this.theMinute.addMenuElement("18","18");

	this.theMinute.addMenuElement("19","19");



	this.theMinute.addMenuElement("20","20");

	this.theMinute.addMenuElement("21","21");

	this.theMinute.addMenuElement("22","22");

	this.theMinute.addMenuElement("23","23");

	this.theMinute.addMenuElement("24","24");

	this.theMinute.addMenuElement("25","25");

	this.theMinute.addMenuElement("26","26");

	this.theMinute.addMenuElement("27","27");

	this.theMinute.addMenuElement("28","28");

	this.theMinute.addMenuElement("29","29");



	this.theMinute.addMenuElement("30","30");

	this.theMinute.addMenuElement("31","31");

	this.theMinute.addMenuElement("32","32");

	this.theMinute.addMenuElement("33","33");

	this.theMinute.addMenuElement("34","34");

	this.theMinute.addMenuElement("35","35");

	this.theMinute.addMenuElement("36","36");

	this.theMinute.addMenuElement("37","37");

	this.theMinute.addMenuElement("38","38");

	this.theMinute.addMenuElement("39","39");



	this.theMinute.addMenuElement("40","40");

	this.theMinute.addMenuElement("41","41");

	this.theMinute.addMenuElement("42","42");

	this.theMinute.addMenuElement("43","43");

	this.theMinute.addMenuElement("44","44");

	this.theMinute.addMenuElement("45","45");

	this.theMinute.addMenuElement("46","46");

	this.theMinute.addMenuElement("47","47");

	this.theMinute.addMenuElement("48","48");

	this.theMinute.addMenuElement("49","49");



	this.theMinute.addMenuElement("50","50");

	this.theMinute.addMenuElement("51","51");

	this.theMinute.addMenuElement("52","52");

	this.theMinute.addMenuElement("53","53");

	this.theMinute.addMenuElement("54","54");

	this.theMinute.addMenuElement("55","55");

	this.theMinute.addMenuElement("56","56");

	this.theMinute.addMenuElement("57","57");

	this.theMinute.addMenuElement("58","58");

	this.theMinute.addMenuElement("59","59");







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



//	getScript().addFunction("populateDays","function populateDays(yearInput,monthInput,dayInput) {\r	timeA = new Date(yearInput.options[yearInput.selectedIndex].text, monthInput.options[monthInput.selectedIndex].value,1);\r	timeDifference = timeA - 86400000;\r	timeB = new Date(timeDifference);\r	\r	var oldSelectedDay = dayInput.selectedIndex;\r	\r	var daysInMonth = timeB.getDate();\r	for (var i = 0; i < dayInput.length; i++) {\r		dayInput.options[0] = null;\r		dayInput.options[0] = new Option('Dagur','');\r	}\r	for (var i = 1; i <= daysInMonth; i++) {\r		dayInput.options[i] = new Option(i,i);\r	}\r	\r	dayInput.options[oldSelectedDay].selected = true;\r	\r}");



        getJavaScript().addFunction("populateDays","function populateDays(yearInput,monthInput,dayInput) {\r	timeA = new Date(yearInput.options[yearInput.selectedIndex].text, monthInput.options[monthInput.selectedIndex].value,1);\r	timeDifference = timeA - 86400000;\r	timeB = new Date(timeDifference);\r	\r	var oldSelectedDay = dayInput.selectedIndex;\r	\r	var daysInMonth = timeB.getDate();\r	for (var i = 0; i < dayInput.length; i++) {\r		dayInput.options[0] = null;\r		dayInput.options[0] = new Option('Dagur','');\r	}\r	for (var i = 1; i <= daysInMonth; i++) {\r		if(i<10){\r			dayInput.options[i] = new Option(i,'0'+i);\r		}\r		else{\r			dayInput.options[i] = new Option(i,i);\r		}\r	}\r	\r	dayInput.options[oldSelectedDay].selected = true;\r	\r}");

	getJavaScript().addFunction("setValueOfHiddenTimestamp","function setValueOfHiddenTimestamp(yearInput,monthInput,dayInput,hourInput,minuteInput,hiddenInput){\r\r\r	var yearValue = '2000';\r	var monthValue = '01';\r	var dayValue = '01';\r	\r	var hourValue='00';\r	var minuteValue='00';\r	var secondValue='00';\r	var millisecondValue='000000';\r	\r	\r	if(hourInput.selectedIndex != 0){\r		hourValue=hourInput.options[hourInput.selectedIndex].value;\r	}\r	if(minuteInput.selectedIndex != 0){\r		minuteValue=minuteInput.options[minuteInput.selectedIndex].value;\r	}\r	if(yearInput.selectedIndex != 0){\r		yearValue=yearInput.options[yearInput.selectedIndex].value;\r	}\r	if(monthInput.selectedIndex != 0){\r		monthValue=monthInput.options[monthInput.selectedIndex].value;\r	}\r	if(dayInput.selectedIndex != 0){\r		dayValue=dayInput.options[dayInput.selectedIndex].value;\r	}\r\r\r	if ((yearInput.selectedIndex == 0) || (monthInput.selectedIndex == 0) || (dayInput.selectedIndex == 0) || (hourInput.selectedIndex == 0) || (minuteInput.selectedIndex == 0) ){\r		hiddenInput.value = '';\r	}\r	else{\r		hiddenInput.value = yearValue+'-'+monthValue+'-'+dayValue+' '+hourValue+':'+minuteValue+':'+secondValue+'.'+millisecondValue;\r	}\r}\r");



}













    private void addLocalized(IWContext iwc){

        Locale locale = iwc.getCurrentLocale();

        DateFormatSymbols symbols = new DateFormatSymbols(locale);

        IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);



        this.textInFront.setText(iwrb.getLocalizedString(THETIME_KEY," at "));



        String emptyString = "";

        if(this.inShort){

          this.theDay.addMenuElementFirst(emptyString,iwrb.getLocalizedString(DateInput.DAY_KEY_S,"D"));

          this.theMonth.addMenuElementFirst(emptyString,iwrb.getLocalizedString(DateInput.MONTH_KEY_S,"M"));

          this.theYear.addMenuElementFirst(emptyString,iwrb.getLocalizedString(DateInput.YEAR_KEY_S,"Y"));

          this.theHour.addMenuElementFirst(emptyString,iwrb.getLocalizedString(TimeInput.HOUR_KEY_S,"H"));

          this.theMinute.addMenuElementFirst(emptyString,iwrb.getLocalizedString(TimeInput.MINUTE_KEY_S,"M"));

        }

        else{

          this.theDay.addMenuElementFirst(emptyString,iwrb.getLocalizedString(DateInput.DAY_KEY,"Day"));

          this.theMonth.addMenuElementFirst(emptyString,iwrb.getLocalizedString(DateInput.MONTH_KEY,"Month"));

          this.theYear.addMenuElementFirst(emptyString,iwrb.getLocalizedString(DateInput.YEAR_KEY,"Year"));

          this.theHour.addMenuElementFirst(emptyString,iwrb.getLocalizedString(TimeInput.HOUR_KEY,"Hour"));

          this.theMinute.addMenuElementFirst(emptyString,iwrb.getLocalizedString(TimeInput.MINUTE_KEY,"Minute"));

        }



        String[] monthStrings = symbols.getMonths();



        for(int i=1;i<=12;i++){

          String value=Integer.toString(i);

          if(i<10){

            value="0"+value;

          }

          this.theMonth.setMenuElementDisplayString(value,monthStrings[i-1]);

        }



    }



public void setYear(int year){

	this.setCheck=true;

        if(year<this.fromYear){

          this.fromYear=year;

        }

        if(year>this.toYear){

          this.toYear=year;

        }

	//theYear.setSelectedElement(Integer.toString(year));

        this.selectedYear=year;

}



public void setYear(String year){

  setYear(Integer.parseInt(year));

}



public void setMonth(int month){

	setMonth(Integer.toString(month));

}



public void setMonth(String month){

	this.setCheck=true;

	if (month.length() > 1){

		this.theMonth.setSelectedElement(month);

	}

	else{

		this.theMonth.setSelectedElement("0"+month);

	}

}



public void setDay(int day){

	setDay(Integer.toString(day));

}



public void setDay(String day){

	this.setCheck=true;

	if (day.length() > 1 ){

		this.theDay.setSelectedElement(day);

	}

	else{

		this.theDay.setSelectedElement("0"+day);

	}

}



public void setDate(java.sql.Date date) {

  GregorianCalendar greg = new GregorianCalendar();

  greg.setTime(date);

//	setYear(date.getYear()+1900);

//	setMonth(date.getMonth()+1);

//	setDay(date.getDate());

	setYear(greg.get(Calendar.YEAR));

	setMonth(greg.get(Calendar.MONTH)+1);

	setDay(greg.get(Calendar.DAY_OF_MONTH));

}







public void setTimestamp(java.sql.Timestamp timestamp){

  GregorianCalendar greg = new GregorianCalendar();

  greg.setTime(new Date(timestamp.getTime()));

//	setYear(date.getYear()+1900);

//	setMonth(date.getMonth()+1);

//	setDay(date.getDate());

//	setHour(timestamp.getHours());

//	setMinute(timestamp.getMinutes());

	setYear(greg.get(Calendar.YEAR));

	setMonth(greg.get(Calendar.MONTH)+1);

	setDay(greg.get(Calendar.DAY_OF_MONTH));



	setHour(greg.get(Calendar.HOUR_OF_DAY));

	setMinute(greg.get(Calendar.MINUTE));

	//setSecond(timestamp.getMinutes());

/*

	System.err.println("Timestamp gefur year: "+timestamp.getYear()+" fyrir "+this.getName());

	System.err.println("Timestamp gefur month: "+timestamp.getMonth()+" fyrir "+this.getName());

	System.err.println("Timestamp gefur day: "+timestamp.getDay()+" fyrir "+this.getName());

	System.err.println("Timestamp gefur hour: "+timestamp.getHours()+" fyrir "+this.getName());

	System.err.println("Timestamp gefur minute: "+timestamp.getMinutes()+" fyrir "+this.getName());

      */

}



public void setMinute(int minute){

	//theMinute.setSelectedElement(Integer.toString(minute));

        setMinute(Integer.toString(minute));

}



public void setMinute(String minute){

	if (minute.length() > 1 ){

		this.theMinute.setSelectedElement(minute);

	}

	else{

		this.theMinute.setSelectedElement("0"+minute);

	}

}





public void setHour(int hour){

	setHour(Integer.toString(hour));

}



public void setHour(String hour){

	if (hour.length() > 1 ){

		this.theHour.setSelectedElement(hour);

	}

	else{

		this.theHour.setSelectedElement("0"+hour);

	}

}





public void setTime(java.sql.Time time){

  GregorianCalendar greg = new GregorianCalendar();

  greg.setTime(new Date(time.getTime()));



	setHour(greg.get(Calendar.HOUR_OF_DAY));

	setMinute(greg.get(Calendar.MINUTE));



//	setHour(time.getHours());

//	setMinute(time.getMinutes());

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



private Script getJavaScript(){

	return this.script;

}



public void keepStatusOnAction(){

	this.theDay.keepStatusOnAction();

	this.theMonth.keepStatusOnAction();

	this.theYear.keepStatusOnAction();

	this.theHour.keepStatusOnAction();

	this.theMinute.keepStatusOnAction();

	//theSecond.keepStatusOnAction();

}



/**

**Does nothing - overrides function in superclass

**/

public void add(PresentationObject mo){

	//does nothing

}



public void main(IWContext iwc){

  addLocalized(iwc);

}





public void print(IWContext iwc)throws Exception{



  for (int i=this.fromYear;i<=this.toYear;i++){

    this.theYear.addMenuElement(Integer.toString(i));

  }



  if (this.selectedYear!=-1){

    this.theYear.setSelectedElement(Integer.toString(this.selectedYear));

  }

	if (this.setCheck == true){

		this.theWholeTimestamp.setValue(this.theYear.getSelectedElementValue()+"-"+this.theMonth.getSelectedElementValue()+"-"+this.theDay.getSelectedElementValue()+" "+this.theHour.getSelectedElementValue()+":"+this.theMinute.getSelectedElementValue()+":00.000000");

	}

	super.print(iwc);

}





public void setYearRange(int fromYear,int toYear){

  this.fromYear=fromYear;

  this.toYear=toYear;

}



//public void main(IWContext iwc)throws Exception{







  //	theYear.addMenuElement("2000","2000");

  //	theYear.addMenuElement("2001","2001");

  //	theYear.addMenuElement("2002","2002");



//}



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