/*

 * $Id: DateInput.java,v 1.16 2002/08/12 12:15:24 palli Exp $

 *

 * Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 * This software is the proprietary information of Idega hf.

 * Use is subject to license terms.

 *

 */

package com.idega.presentation.ui;



import java.io.*;

import java.util.*;



import java.text.DateFormatSymbols;



import com.idega.presentation.*;

import com.idega.util.*;

import com.idega.idegaweb.IWBundle;

import com.idega.idegaweb.IWResourceBundle;





/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/

public class DateInput extends InterfaceObject{





private Script _script;

private DropdownMenu _theDay;

private DropdownMenu _theMonth;

private DropdownMenu _theYear;

//private Parameter _theYearHidden;

private Parameter _theWholeDate;

private boolean _setCheck = false;

private boolean _isShowDay = true;

private int _fromYear;

private int _toYear;

private int _selectedYear=-1;

private String _styleAttribute = "font-size: 8pt";

private boolean _inShort=false;

protected boolean _justConstructed = false;

private boolean _showYear=true;

private boolean _displayDayLast=false;

private boolean _isDisabled= false;



private String _setDay;

private String _setMonth;



final static String DAY_KEY = "dateinput.day_long";

final static String MONTH_KEY = "dateinput.month_long";

final static String YEAR_KEY = "dateinput.year_long";



final static String DAY_KEY_S = "dateinput.day_short";

final static String MONTH_KEY_S = "dateinput.month_short";

final static String YEAR_KEY_S = "dateinput.year_short";



public DateInput(){

	this("dateinput");

}



public DateInput(String name){

  super();

  super.setName(name);

  _justConstructed = true;

  constructInputs();

}



public DateInput(String name, boolean _inShort){

  super();

  super.setName(name);

  _justConstructed = true;

  /*

  script = new Script();

  _theDay = new DropdownMenu(name+"_day");

  _theMonth = new DropdownMenu(name+"_month");

  //if(_showYear){

    _theYear = new DropdownMenu(name+"_year");

  //}

  //else{

    _theYearHidden = new Parameter(name+"_year");

  //}

  _theWholeDate = new Parameter(name,"");



  this._inShort=_inShort;

  //doSomeShit("D", "M", "Y");

  doSomeShit();

  */

  this._inShort=_inShort;

  constructInputs();

}



 //public Object _clone(IWContext iwc, boolean askForPermission) {

 // return clone();

 //}



public Object clone(){

  //System.out.println("DateInput.clone()");

  DateInput newObject = (DateInput)super.clone();

  if(_theWholeDate!=null){

    newObject._theWholeDate = (Parameter)this._theWholeDate.clone();

  }

  if(_theDay!=null){

    newObject._theDay = (DropdownMenu)this._theDay.clone();

  }

  if(_theMonth!=null){

    newObject._theMonth = (DropdownMenu)this._theMonth.clone();

  }

  if(_theYear!=null){

    newObject._theYear = (DropdownMenu)this._theYear.clone();

  }

  //if(_theYearHidden!=null){

  //  newObject._theYearHidden = (Parameter)this._theYearHidden.clone();

  //}

  if(_script!=null){

    newObject._script = (Script) this._script.clone();

  }

  return newObject;

}



public void setName(String name){

  super.setName(name);

  if(_theWholeDate!=null){

    _theWholeDate.setName(name);

  }

  if(_theDay!=null){

    _theDay.setName(name+"_day");

  }

  if(_theMonth!=null){

    _theMonth.setName(name+"_month");

  }

  if(_showYear){

    if(_theYear!=null){

      _theYear.setName(name+"_year");

    }

  }

  else{

    //if(_theYearHidden!=null){

    //  _theYearHidden.setName(name+"_year");

    //}

  }



}



private void addMenuElementsToDropdowns(){

//private void doSomeShit(String strDay, String strMonth, String strYear) {







	//_theDay.setParentObject(this.getParentObject());

	//_theMonth.setParentObject(this.getParentObject());

	//if(_showYear){

    //  _theYear.setParentObject(this.getParentObject());

    //}

    //else{

      //_theYearHidden.setParentObject(this.getParentObject());

    //}





	//_theYear.addMenuElement("",strYear);



        IWTimeStamp stamp = IWTimeStamp.RightNow();

        int currentYear = stamp.getYear();

        if(_showYear){

          this.setYearRange(currentYear,currentYear+5);

        }

        else{

          //this._theYearHidden.setValue(currentYear);

        }



	//_theYear.addMenuElement("2000","2000");

	//_theYear.addMenuElement("2001","2001");

	//_theYear.addMenuElement("2002","2002");

        /*

	_theMonth.addMenuElement("",strMonth);

	_theMonth.addMenuElement("01","janúar");

	_theMonth.addMenuElement("02","febrúar");

	_theMonth.addMenuElement("03","mars");

	_theMonth.addMenuElement("04","apríl");

	_theMonth.addMenuElement("05","maí");

	_theMonth.addMenuElement("06","júní");

	_theMonth.addMenuElement("07","júlí");

	_theMonth.addMenuElement("08","ágúst");

	_theMonth.addMenuElement("09","september");

	_theMonth.addMenuElement("10","október");

	_theMonth.addMenuElement("11","nóvember");

	_theMonth.addMenuElement("12","desember");

        */



        //_theMonth.addMenuElement("",strMonth);



        /*

	_theMonth.addMenuElement("01","jan");

	_theMonth.addMenuElement("02","feb");

	_theMonth.addMenuElement("03","mar");

	_theMonth.addMenuElement("04","apr");

	_theMonth.addMenuElement("05","maí");

	_theMonth.addMenuElement("06","jún");

	_theMonth.addMenuElement("07","júl");

	_theMonth.addMenuElement("08","ágú");

	_theMonth.addMenuElement("09","sep");

	_theMonth.addMenuElement("10","okt");

	_theMonth.addMenuElement("11","nóv");

	_theMonth.addMenuElement("12","des");

        */



        _theMonth.addMenuElement("00");

	_theMonth.addMenuElement("01");

	_theMonth.addMenuElement("02");

	_theMonth.addMenuElement("03");

	_theMonth.addMenuElement("04");

	_theMonth.addMenuElement("05");

	_theMonth.addMenuElement("06");

	_theMonth.addMenuElement("07");

	_theMonth.addMenuElement("08");

	_theMonth.addMenuElement("09");

	_theMonth.addMenuElement("10");

	_theMonth.addMenuElement("11");

	_theMonth.addMenuElement("12");



	_theDay.addMenuElement("00","D");

	_theDay.addMenuElement("01","1");

	_theDay.addMenuElement("02","2");

	_theDay.addMenuElement("03","3");

	_theDay.addMenuElement("04","4");

	_theDay.addMenuElement("05","5");

	_theDay.addMenuElement("06","6");

	_theDay.addMenuElement("07","7");

	_theDay.addMenuElement("08","8");

	_theDay.addMenuElement("09","9");



	_theDay.addMenuElement("10","10");

	_theDay.addMenuElement("11","11");

	_theDay.addMenuElement("12","12");

	_theDay.addMenuElement("13","13");

	_theDay.addMenuElement("14","14");

	_theDay.addMenuElement("15","15");

	_theDay.addMenuElement("16","16");

	_theDay.addMenuElement("17","17");

	_theDay.addMenuElement("18","18");

	_theDay.addMenuElement("19","19");



	_theDay.addMenuElement("20","20");

	_theDay.addMenuElement("21","21");

	_theDay.addMenuElement("22","22");

	_theDay.addMenuElement("23","23");

	_theDay.addMenuElement("24","24");

	_theDay.addMenuElement("25","25");

	_theDay.addMenuElement("26","26");

	_theDay.addMenuElement("27","27");

	_theDay.addMenuElement("28","28");

	_theDay.addMenuElement("29","29");



	_theDay.addMenuElement("30","30");

	_theDay.addMenuElement("31","31");



    if(this._showYear){

      _theYear.addMenuElement("YY");

    }



    //getScript().addFunction("populateDays","function populateDays(yearInput,monthInput,dayInput) {\r	timeA = new Date(yearInput.options[yearInput.selectedIndex].text, monthInput.options[monthInput.selectedIndex].value,1);\r	timeDifference = timeA - 86400000;\r	timeB = new Date(timeDifference);\r	\r	var oldSelectedDay = dayInput.selectedIndex;\r	\r	var daysInMonth = timeB.getDate();\r	for (var i = 0; i < dayInput.length; i++) {\r		dayInput.options[0] = null;\r		dayInput.options[0] = new Option('Dagur','');\r	}\r	for (var i = 1; i <= daysInMonth; i++) {\r		if (i<10){\r			dayInput.options[i] = new Option(i,'0'+i);\r		}\r		else{\r			dayInput.options[i] = new Option(i,i);\r		}\r	}\r	\r	dayInput.options[oldSelectedDay].selected = true;\r	\r}");

    //getScript().addFunction("setValueOfHiddenDate","function setValueOfHiddenDate(yearInput,monthInput,dayInput,hiddenInput){\r\r	var yearValue='2000';\r	var monthValue='01';\r	var dayValue='01';\r\r	if(yearInput.selectedIndex != 0){\r		yearValue=yearInput.options[yearInput.selectedIndex].value;\r	}\r	if(monthInput.selectedIndex != 0){\r		monthValue=monthInput.options[monthInput.selectedIndex].value;\r	}\r	if(dayInput.selectedIndex != 0){\r		dayValue=dayInput.options[dayInput.selectedIndex].value;\r	}\r\r	if ((yearInput.selectedIndex == 0) || (monthInput.selectedIndex == 0) || (dayInput.selectedIndex == 0)){\r		hiddenInput.value = '';\r	}\r	else{\r		hiddenInput.value = yearValue+'-'+monthValue+'-'+dayValue+'';\r	}\r}");



	//getScript().addFunction("populateDays","function populateDays(yearInput,monthInput,dayInput) {\r	timeA = new Date(yearInput.options[yearInput.selectedIndex].text, monthInput.options[monthInput.selectedIndex].value,1);\r	timeDifference = timeA - 86400000;\r	timeB = new Date(timeDifference);\r	\r	var oldSelectedDay = dayInput.selectedIndex;\r	\r	var daysInMonth = timeB.getDate();\r	for (var i = 0; i < dayInput.length; i++) {\r		dayInput.options[0] = null;\r		dayInput.options[0] = new Option('Dagur','');\r	}\r	for (var i = 1; i <= daysInMonth; i++) {\r		dayInput.options[i] = new Option(i,i);\r	}\r	\r	dayInput.options[oldSelectedDay].selected = true;\r	\r}");

	//getScript().addFunction("setValueOfHiddenDate","function setValueOfHiddenDate(yearInput,monthInput,dayInput,hiddenInput){\r\r	var yearValue='2000';\r	var monthValue='01';\r	var dayValue='01';\r\r	if(yearInput.selectedIndex != 0){\r		yearValue=yearInput.options[yearInput.selectedIndex].value;\r	}\r	if(monthInput.selectedIndex != 0){\r		monthValue=monthInput.options[monthInput.selectedIndex].value;\r	}\r	if(dayInput.selectedIndex != 0){\r		dayValue=dayInput.options[dayInput.selectedIndex].value;\r	}\r\r	if ((yearInput.selectedIndex == 0) || (monthInput.selectedIndex == 0) || (dayInput.selectedIndex == 0)){\r		hiddenInput.value = '';\r	}\r	else{\r		hiddenInput.value = yearValue+'-'+monthValue+'-'+dayValue+'';\r	}\r}");







}



/*private void setScript(Script _script){

	this._script = _script;

	setAssociatedScript(_script);

}*/



/*private Script getScript(){

	if (getAssociatedScript() == null){



		setScript(new Script());

	}

	else

	{

		_script = getAssociatedScript();

	}

	return _script;

}*/


public void setDisabled(boolean disabled) {
  this._isDisabled = disabled;
}



private Script getScript(){

	return this._script;

}



public void setStyle(String styleAttribute){

  if(this._isShowDay){

    _theDay.setStyle(styleAttribute);

  }

  _theMonth.setStyle(styleAttribute);

  if(this._showYear){

    _theYear.setStyle(styleAttribute);

  }

}




public void setStyleAttribute(String attributeName,String attributeValue){



  _theDay.setAttribute(attributeName,attributeValue);

  _theMonth.setAttribute(attributeName,attributeValue);

  _theYear.setAttribute(attributeName,attributeValue);



}





public void keepStatusOnAction(){



	_theDay.keepStatusOnAction();

	_theMonth.keepStatusOnAction();

	_theYear.keepStatusOnAction();



}



/*

public void setToSubmit(){

	_theDay.setToSubmit();

	_theMonth.setToSubmit();

	_theYear.setToSubmit();

}*/





public void setYear(int year){

	_setCheck=true;

        if(_fromYear > _toYear){

          if(_fromYear < year){

            _fromYear = year;

          }

          if(_toYear > year){

            _toYear = year;

          }

        } else{

          if(_fromYear > year){

            _fromYear = year;

          }

          if(_toYear < year){

            _toYear = year;

          }

        }

        /*

        if(!(year>_fromYear && year<_toYear) || !(year<_fromYear && year>_toYear)){

          if(year<_fromYear){

            _fromYear=year;

          }

          if(year>_toYear){

            _toYear=year;

          }

        }

        */

	//_theYear.setSelectedElement(Integer.toString(year));

        _selectedYear=year;

}



public void setYear(String year){

  setYear(Integer.parseInt(year));

}



public void setMonth(int month){

	setMonth(Integer.toString(month));

}



public void setMonth(String month){

	_setCheck=true;

	if (month.length() > 1){

        if(_theMonth!=null){

		  _theMonth.setSelectedElement(month);

        }

        _setMonth=month;

	}

	else{

      if(_theMonth!=null){

        _theMonth.setSelectedElement("0"+month);

      }

	  _setMonth="0"+month;

    }



}



public void setDay(int day){

	setDay(Integer.toString(day));

}



public void setDay(String day){



	_setCheck=true;

	if (day.length() > 1 ){

		//_theDay.setSelectedElement(day);

      _setDay=day;

    }

	else{

		//_theDay.setSelectedElement("0"+day);

	    _setDay="0"+day;

    }



}



public void setDate(java.sql.Date date){

  GregorianCalendar greg = new GregorianCalendar();

  greg.setTime(date);

//	setYear(date.getYear()+1900);

//	setMonth(date.getMonth()+1);

//	setDay(date.getDate());

	setYear(greg.get(Calendar.YEAR));

	setMonth(greg.get(Calendar.MONTH)+1);

	setDay(greg.get(Calendar.DAY_OF_MONTH));

	//System.out.println("Date gefur year: "+date.getYear()+" fyrir "+this.getName());

	//System.out.println("Date gefur month: "+date.getMonth()+" fyrir "+this.getName());

	//System.out.println("Date gefur day: "+date.getDay()+" fyrir "+this.getName());

}



public void setToCurrentDate(){

  GregorianCalendar greg = new GregorianCalendar();

	setYear(greg.get(Calendar.YEAR));

	setMonth(greg.get(Calendar.MONTH)+1);

	setDay(greg.get(Calendar.DAY_OF_MONTH));

}





/**

**Does nothing - overrides function in superclass - does nothing

**/

/*public void add(PresentationObject mo){

	//does nothing

}

*/



/**

 *

 */

public void setYearRange(int _fromYear,int _toYear){

  this._fromYear=_fromYear;

  this._toYear=_toYear;

}





  public void setNoDayView(){

    setToShowDay(false);

  }



  public void setToShowDay(boolean ifShow){

    this._isShowDay=ifShow;

    if(ifShow){

    }

    else{

      this.setDay(1);

    }

  }



  public void setToShowYear(boolean ifShow){

    this._showYear=ifShow;

  }



  public void setToDisplayDayLast(boolean dayLast){

    _displayDayLast=dayLast;

  }



//public void main(IWContext iwc)throws Exception{







  //	_theYear.addMenuElement("2000","2000");

  //	_theYear.addMenuElement("2001","2001");

  //	_theYear.addMenuElement("2002","2002");



//}



private void constructInputs(){

  if(_justConstructed){

    String name = getName();

    _script = new Script();

    if(this._isShowDay){

      _theDay = new DropdownMenu(name+"_day");

    }



    _theMonth = new DropdownMenu(name+"_month");

    if(_showYear){

      _theYear = new DropdownMenu(name+"_year");

    }

    //else{

      //_theYearHidden = new Parameter(name+"_year");

    //}

    _theWholeDate = new Parameter(name,"");



    addMenuElementsToDropdowns();

  }

}



private void setSetValues(){

  if(_setMonth!=null){

    this.setMonth(_setMonth);

  }

  if(_setDay!=null){

    if(_isShowDay){

      this._theDay.setSelectedElement(_setDay);

    }

  }

    if(this._justConstructed){

      if(this._showYear){

        if(_fromYear < _toYear){

          for (int i=_fromYear;i<=_toYear;i++){

            _theYear.addMenuElement(Integer.toString(i));

          }

        }else{

          for (int i=_fromYear;i>=_toYear;i--){

            _theYear.addMenuElement(Integer.toString(i));

          }

        }

      }

    }



    if (_selectedYear!=-1){

      if(this._showYear){

        _theYear.setSelectedElement(Integer.toString(_selectedYear));

      }

      else{

        //_theYearHidden.setValue(_selectedYear);

      }

    }

    else{

      if(this._showYear){

      }

      else{

        //_theYearHidden.setValue(_fromYear);

      }

    }



    if (_setCheck == true){

        String year = getCurrentYear();

        String month = _theMonth.getSelectedElementValue();

        String day = getCurrentDay();

        _theWholeDate.setValue(year+"-"+month+"-"+day);

    }

}



private void addDropDowns(){

    if(_justConstructed){

      /*if(_displayDayLast){

        if(this._showYear){

          super.add(_theYear);

        }

        else{

          //super.add(_theYearHidden);

        }

        super.add(_theMonth);

        if (_isShowDay) {

          super.add(_theDay);

        }

      }

      else{

        if (_isShowDay) {

          super.add(_theDay);

        }

        super.add(_theMonth);

        if(this._showYear){

          super.add(_theYear);

        }

        else{

          //super.add(_theYearHidden);

        }

      }

      super.add(_theWholeDate);

      super.add(_script);

    */

  }

}



public void main(IWContext iwc)throws Exception{

  //constructInputs();

  setSetValues();

  addDropDowns();



  addLocalized(iwc);

  addScriptElements(iwc);



  _justConstructed = false;

}





  public void addScriptElements(IWContext iwc){

    /*if(_showYear){

	  _theYear.setOnChange("populateDays(this,this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+");setValueOfHiddenDate(this.form."+_theYear.getName()+",this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+",this.form."+_theWholeDate.getName()+");");

	}

    _theMonth.setOnChange("populateDays(this.form."+_theYear.getName()+",this,this.form."+_theDay.getName()+");setValueOfHiddenDate(this.form."+_theYear.getName()+",this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+",this.form."+_theWholeDate.getName()+");");

	_theDay.setOnChange("setValueOfHiddenDate(this.form."+_theYear.getName()+",this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+",this.form."+_theWholeDate.getName()+");");

    */

    if(_isShowDay){

      if(_showYear){

        _theYear.setOnChange("iwPopulateDaysWithYear(this,this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+");iwSetValueOfHiddenDateWithAllInputs(this.form."+_theYear.getName()+",this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+",this.form."+_theWholeDate.getName()+");");

        _theMonth.setOnChange("iwPopulateDaysWithYear(this.form."+_theYear.getName()+",this,this.form."+_theDay.getName()+");iwSetValueOfHiddenDateWithAllInputs(this.form."+_theYear.getName()+",this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+",this.form."+_theWholeDate.getName()+");");

        _theDay.setOnChange("iwSetValueOfHiddenDateWithAllInputs(this.form."+_theYear.getName()+",this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+",this.form."+_theWholeDate.getName()+");");

      }

      else{

        _theMonth.setOnChange("iwPopulateDaysWithMonth('"+this._selectedYear+"',this,this.form."+_theDay.getName()+");iwSetValueOfHiddenDateWithDay('"+this._selectedYear+"',this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+",this.form."+_theWholeDate.getName()+");");

        _theDay.setOnChange("iwSetValueOfHiddenDateWithDay('"+this._selectedYear+"',this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+",this.form."+_theWholeDate.getName()+");");

      }

    }

    else{

      if(_showYear){

        _theYear.setOnChange("iwSetValueOfHiddenDateWithYear(this.form."+_theYear.getName()+",this.form."+_theMonth.getName()+",this.form."+_theWholeDate.getName()+");");

        _theMonth.setOnChange("iwSetValueOfHiddenDateWithYear(this.form."+_theYear.getName()+",this.form."+_theMonth.getName()+",this.form."+_theWholeDate.getName()+");");

      }

      else{

        _theMonth.setOnChange("iwSetValueOfHiddenDateWithMonth('"+this._selectedYear+"',this.form."+_theMonth.getName()+",'01',this.form."+_theWholeDate.getName()+");");

      }

    }

  }



  private void addLocalized(IWContext iwc){

      Locale locale = iwc.getCurrentLocale();

      DateFormatSymbols symbols = new DateFormatSymbols(locale);

      IWBundle iwb = this.getBundle(iwc);

      IWResourceBundle iwrb = iwb.getResourceBundle(iwc);

      String[] monthStrings;



      String dayString;

      String monthString;

      String yearString;









      if(_inShort){

        dayString = iwrb.getLocalizedString(DAY_KEY_S,"D");

        monthString = iwrb.getLocalizedString(MONTH_KEY_S,"M");

        yearString = iwrb.getLocalizedString(YEAR_KEY_S,"Y");

        monthStrings = symbols.getShortMonths();

      }

      else{

        dayString = iwrb.getLocalizedString(DAY_KEY,"Day");

        monthString = iwrb.getLocalizedString(MONTH_KEY,"Month");

        yearString = iwrb.getLocalizedString(YEAR_KEY,"Year");

        monthStrings = symbols.getMonths();

      }





      _theDay.setMenuElementDisplayString("00",dayString);

      _theMonth.setMenuElementDisplayString("00",monthString);

      if(this._showYear){

        _theYear.setMenuElementDisplayString("YY",yearString);

      }

      /*

      _theDay.addMenuElementFirst("",dayString);

      _theMonth.addMenuElementFirst("",monthString);

      _theYear.addMenuElementFirst("",yearString);

      */



      if(this._inShort){



      }

      else{

        monthStrings = symbols.getMonths();

      }



      for(int i=1;i<=12;i++){

        String value=Integer.toString(i);

        if(i<10){

          value="0"+value;

        }

        _theMonth.setMenuElementDisplayString(value,monthStrings[i-1]);

      }



  }





  public String getCurrentYear(){

    if(this._showYear){

      return _theYear.getSelectedElementValue();

    }

    else{

      return Integer.toString(_selectedYear);

    }

  }



  private String getCurrentDay(){

    if(this._isShowDay){

      return _theDay.getSelectedElementValue();

    }

    else{

      return "01";

    }

  }



  public void print(IWContext iwc)throws Exception{

    super.print(iwc);

    if (_isDisabled) {
      _theYear.setDisabled(_isDisabled);
      _theMonth.setDisabled(_isDisabled);
      _theDay.setDisabled(_isDisabled);
    }

    if(_displayDayLast){

        if(this._showYear){

          _theYear._print(iwc);

        }

        else{

          //super.add(_theYearHidden);

        }

        _theMonth._print(iwc);

        if (_isShowDay) {

          _theDay._print(iwc);

        }

      }

      else{

        if (_isShowDay) {

          _theDay._print(iwc);

        }

        _theMonth._print(iwc);

        if(this._showYear){

          _theYear._print(iwc);

        }

        else{

          //super.add(_theYearHidden);

        }

      }

      _theWholeDate._print(iwc);

      _script._print(iwc);





  }



}

