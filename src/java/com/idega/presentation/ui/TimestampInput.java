/*

 * $Id: TimestampInput.java,v 1.8 2004/09/04 19:34:32 eiki Exp $

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

	theDay = new DropdownMenu(name+"_day");

	theMonth = new DropdownMenu(name+"_month");

	theYear = new DropdownMenu(name+"_year");



	theHour = new DropdownMenu(name+"_hour");

	theMinute = new DropdownMenu(name+"_minute");

	//theSecond = new DropdownMenu(name+"_second");

	theWholeTimestamp = new Parameter(name,"");



        script = new Script();



	super.add(theDay);

	super.add(theMonth);

	super.add(theYear);



        super.add(textInFront);

	//super.add(new Text("klukkan"));



	super.add(theHour);

	super.add(theMinute);

	super.add(theWholeTimestamp);

	super.add(script);

	//super.add(theSecond);



	theYear.setOnChange("populateDays(this,this.form."+theMonth.getName()+",this.form."+theDay.getName()+");setValueOfHiddenTimestamp(this.form."+theYear.getName()+",this.form."+theMonth.getName()+",this.form."+theDay.getName()+",this.form."+theHour.getName()+",this.form."+theMinute.getName()+",this.form."+theWholeTimestamp.getName()+");");

	theMonth.setOnChange("populateDays(this.form."+theYear.getName()+",this,this.form."+theDay.getName()+");setValueOfHiddenTimestamp(this.form."+theYear.getName()+",this.form."+theMonth.getName()+",this.form."+theDay.getName()+",this.form."+theHour.getName()+",this.form."+theMinute.getName()+",this.form."+theWholeTimestamp.getName()+");");

	theDay.setOnChange("setValueOfHiddenTimestamp(this.form."+theYear.getName()+",this.form."+theMonth.getName()+",this.form."+theDay.getName()+",this.form."+theHour.getName()+",this.form."+theMinute.getName()+",this.form."+theWholeTimestamp.getName()+")");





	theHour.setOnChange("setValueOfHiddenTimestamp(this.form."+theYear.getName()+",this.form."+theMonth.getName()+",this.form."+theDay.getName()+",this.form."+theHour.getName()+",this.form."+theMinute.getName()+",this.form."+theWholeTimestamp.getName()+")");

	theMinute.setOnChange("setValueOfHiddenTimestamp(this.form."+theYear.getName()+",this.form."+theMonth.getName()+",this.form."+theDay.getName()+",this.form."+theHour.getName()+",this.form."+theMinute.getName()+",this.form."+theWholeTimestamp.getName()+")");







	theDay.setParentObject(this.getParentObject());

	theMonth.setParentObject(this.getParentObject());

	theYear.setParentObject(this.getParentObject());





	theHour.setParentObject(this.getParentObject());

	theMinute.setParentObject(this.getParentObject());

	//theSecond.setParentObject(this.getParentObject());





	//theYear.addMenuElement("","Ár");



        IWTimestamp stamp = IWTimestamp.RightNow();

        int currentYear = stamp.getYear();

        this.setYearRange(currentYear,currentYear+5);



	//theYear.addMenuElement("2000","2000");

	//theYear.addMenuElement("2001","2001");

	//theYear.addMenuElement("2002","2002");



	//theMonth.addMenuElement("","Mánuður");



        /*

	theMonth.addMenuElement("01","janúar");

	theMonth.addMenuElement("02","febrúar");

	theMonth.addMenuElement("03","mars");

	theMonth.addMenuElement("04","apríl");

	theMonth.addMenuElement("05","maí");

	theMonth.addMenuElement("06","júní");

	theMonth.addMenuElement("07","júlí");

	theMonth.addMenuElement("08","ágúst");

	theMonth.addMenuElement("09","september");

	theMonth.addMenuElement("10","október");

	theMonth.addMenuElement("11","nóvember");

	theMonth.addMenuElement("12","desember");

        */



	theMonth.addMenuElement("01");

	theMonth.addMenuElement("02");

	theMonth.addMenuElement("03");

	theMonth.addMenuElement("04");

	theMonth.addMenuElement("05");

	theMonth.addMenuElement("06");

	theMonth.addMenuElement("07");

	theMonth.addMenuElement("08");

	theMonth.addMenuElement("09");

	theMonth.addMenuElement("10");

	theMonth.addMenuElement("11");

	theMonth.addMenuElement("12");



	//theDay.addMenuElement("","Dagur");



	theDay.addMenuElement("01","1");

	theDay.addMenuElement("02","2");

	theDay.addMenuElement("03","3");

	theDay.addMenuElement("04","4");

	theDay.addMenuElement("05","5");

	theDay.addMenuElement("06","6");

	theDay.addMenuElement("07","7");

	theDay.addMenuElement("08","8");

	theDay.addMenuElement("09","9");



	theDay.addMenuElement("10","10");

	theDay.addMenuElement("11","11");

	theDay.addMenuElement("12","12");

	theDay.addMenuElement("13","13");

	theDay.addMenuElement("14","14");

	theDay.addMenuElement("15","15");

	theDay.addMenuElement("16","16");

	theDay.addMenuElement("17","17");

	theDay.addMenuElement("18","18");

	theDay.addMenuElement("19","19");



	theDay.addMenuElement("20","20");

	theDay.addMenuElement("21","21");

	theDay.addMenuElement("22","22");

	theDay.addMenuElement("23","23");

	theDay.addMenuElement("24","24");

	theDay.addMenuElement("25","25");

	theDay.addMenuElement("26","26");

	theDay.addMenuElement("27","27");

	theDay.addMenuElement("28","28");

	theDay.addMenuElement("29","29");



	theDay.addMenuElement("30","30");

	theDay.addMenuElement("31","31");





	//theHour.addMenuElement("","Klst");



	theHour.addMenuElement("00","00");

	theHour.addMenuElement("01","01");

	theHour.addMenuElement("02","02");

	theHour.addMenuElement("03","03");

	theHour.addMenuElement("04","04");

	theHour.addMenuElement("05","05");

	theHour.addMenuElement("06","06");

	theHour.addMenuElement("07","07");

	theHour.addMenuElement("08","08");

	theHour.addMenuElement("09","09");

	theHour.addMenuElement("10","10");

	theHour.addMenuElement("11","11");

	theHour.addMenuElement("12","12");

	theHour.addMenuElement("13","13");

	theHour.addMenuElement("14","14");

	theHour.addMenuElement("15","15");

	theHour.addMenuElement("16","16");

	theHour.addMenuElement("17","17");

	theHour.addMenuElement("18","18");

	theHour.addMenuElement("19","19");



	theHour.addMenuElement("20","20");

	theHour.addMenuElement("21","21");

	theHour.addMenuElement("22","22");

	theHour.addMenuElement("23","23");





	//theMinute.addMenuElement("","Mínútur");



	theMinute.addMenuElement("00","00");

	theMinute.addMenuElement("01","01");

	theMinute.addMenuElement("02","02");

	theMinute.addMenuElement("03","03");

	theMinute.addMenuElement("04","04");

	theMinute.addMenuElement("05","05");

	theMinute.addMenuElement("06","06");

	theMinute.addMenuElement("07","07");

	theMinute.addMenuElement("08","08");

	theMinute.addMenuElement("09","09");



	theMinute.addMenuElement("10","10");

	theMinute.addMenuElement("11","11");

	theMinute.addMenuElement("12","12");

	theMinute.addMenuElement("13","13");

	theMinute.addMenuElement("14","14");

	theMinute.addMenuElement("15","15");

	theMinute.addMenuElement("16","16");

	theMinute.addMenuElement("17","17");

	theMinute.addMenuElement("18","18");

	theMinute.addMenuElement("19","19");



	theMinute.addMenuElement("20","20");

	theMinute.addMenuElement("21","21");

	theMinute.addMenuElement("22","22");

	theMinute.addMenuElement("23","23");

	theMinute.addMenuElement("24","24");

	theMinute.addMenuElement("25","25");

	theMinute.addMenuElement("26","26");

	theMinute.addMenuElement("27","27");

	theMinute.addMenuElement("28","28");

	theMinute.addMenuElement("29","29");



	theMinute.addMenuElement("30","30");

	theMinute.addMenuElement("31","31");

	theMinute.addMenuElement("32","32");

	theMinute.addMenuElement("33","33");

	theMinute.addMenuElement("34","34");

	theMinute.addMenuElement("35","35");

	theMinute.addMenuElement("36","36");

	theMinute.addMenuElement("37","37");

	theMinute.addMenuElement("38","38");

	theMinute.addMenuElement("39","39");



	theMinute.addMenuElement("40","40");

	theMinute.addMenuElement("41","41");

	theMinute.addMenuElement("42","42");

	theMinute.addMenuElement("43","43");

	theMinute.addMenuElement("44","44");

	theMinute.addMenuElement("45","45");

	theMinute.addMenuElement("46","46");

	theMinute.addMenuElement("47","47");

	theMinute.addMenuElement("48","48");

	theMinute.addMenuElement("49","49");



	theMinute.addMenuElement("50","50");

	theMinute.addMenuElement("51","51");

	theMinute.addMenuElement("52","52");

	theMinute.addMenuElement("53","53");

	theMinute.addMenuElement("54","54");

	theMinute.addMenuElement("55","55");

	theMinute.addMenuElement("56","56");

	theMinute.addMenuElement("57","57");

	theMinute.addMenuElement("58","58");

	theMinute.addMenuElement("59","59");







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



        textInFront.setText(iwrb.getLocalizedString(THETIME_KEY," at "));



        String emptyString = "";

        if(inShort){

          theDay.addMenuElementFirst(emptyString,iwrb.getLocalizedString(DateInput.DAY_KEY_S,"D"));

          theMonth.addMenuElementFirst(emptyString,iwrb.getLocalizedString(DateInput.MONTH_KEY_S,"M"));

          theYear.addMenuElementFirst(emptyString,iwrb.getLocalizedString(DateInput.YEAR_KEY_S,"Y"));

          theHour.addMenuElementFirst(emptyString,iwrb.getLocalizedString(TimeInput.HOUR_KEY_S,"H"));

          theMinute.addMenuElementFirst(emptyString,iwrb.getLocalizedString(TimeInput.MINUTE_KEY_S,"M"));

        }

        else{

          theDay.addMenuElementFirst(emptyString,iwrb.getLocalizedString(DateInput.DAY_KEY,"Day"));

          theMonth.addMenuElementFirst(emptyString,iwrb.getLocalizedString(DateInput.MONTH_KEY,"Month"));

          theYear.addMenuElementFirst(emptyString,iwrb.getLocalizedString(DateInput.YEAR_KEY,"Year"));

          theHour.addMenuElementFirst(emptyString,iwrb.getLocalizedString(TimeInput.HOUR_KEY,"Hour"));

          theMinute.addMenuElementFirst(emptyString,iwrb.getLocalizedString(TimeInput.MINUTE_KEY,"Minute"));

        }



        String[] monthStrings = symbols.getMonths();



        for(int i=1;i<=12;i++){

          String value=Integer.toString(i);

          if(i<10){

            value="0"+value;

          }

          theMonth.setMenuElementDisplayString(value,monthStrings[i-1]);

        }



    }



public void setYear(int year){

	setCheck=true;

        if(year<fromYear){

          fromYear=year;

        }

        if(year>toYear){

          toYear=year;

        }

	//theYear.setSelectedElement(Integer.toString(year));

        selectedYear=year;

}



public void setYear(String year){

  setYear(Integer.parseInt(year));

}



public void setMonth(int month){

	setMonth(Integer.toString(month));

}



public void setMonth(String month){

	setCheck=true;

	if (month.length() > 1){

		theMonth.setSelectedElement(month);

	}

	else{

		theMonth.setSelectedElement("0"+month);

	}

}



public void setDay(int day){

	setDay(Integer.toString(day));

}



public void setDay(String day){

	setCheck=true;

	if (day.length() > 1 ){

		theDay.setSelectedElement(day);

	}

	else{

		theDay.setSelectedElement("0"+day);

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

		theMinute.setSelectedElement(minute);

	}

	else{

		theMinute.setSelectedElement("0"+minute);

	}

}





public void setHour(int hour){

	setHour(Integer.toString(hour));

}



public void setHour(String hour){

	if (hour.length() > 1 ){

		theHour.setSelectedElement(hour);

	}

	else{

		theHour.setSelectedElement("0"+hour);

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

	theDay.keepStatusOnAction();

	theMonth.keepStatusOnAction();

	theYear.keepStatusOnAction();

	theHour.keepStatusOnAction();

	theMinute.keepStatusOnAction();

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



  for (int i=fromYear;i<=toYear;i++){

    theYear.addMenuElement(Integer.toString(i));

  }



  if (selectedYear!=-1){

    theYear.setSelectedElement(Integer.toString(selectedYear));

  }

	if (setCheck == true){

		theWholeTimestamp.setValue(theYear.getSelectedElementValue()+"-"+theMonth.getSelectedElementValue()+"-"+theDay.getSelectedElementValue()+" "+theHour.getSelectedElementValue()+":"+theMinute.getSelectedElementValue()+":00.000000");

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