/*
 * $Id: DateInput.java,v 1.6 2002/01/23 14:48:46 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.ui;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Script;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.util.idegaTimestamp;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.sql.Date;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class DateInput extends InterfaceObjectContainer {
  private Script _script;
  private DropdownMenu _theDay;
  private DropdownMenu _theMonth;
  private DropdownMenu _theYear;
  private Parameter _theWholeDate;
  private boolean _setCheck = false;
  private boolean _isShowDay = true;
  private int _fromYear;
  private int _toYear;
  private int _selectedYear = -1;
  private String _styleAttribute = "font-size: 8pt";
  private boolean _inShort = false;
  protected boolean _justConstructed = false;

  final static String DAY_KEY = "dateinput.day_long";
  final static String MONTH_KEY = "dateinput.month_long";
  final static String YEAR_KEY = "dateinput.year_long";
  final static String DAY_KEY_S = "dateinput.day_short";
  final static String MONTH_KEY_S = "dateinput.month_short";
  final static String YEAR_KEY_S = "dateinput.year_short";

  /**
   *
   */
  public DateInput() {
    this("dateinput");
  }

  /**
   *
   */
  public DateInput(String name) {
    super();
    super.setName(name);
    _justConstructed = true;
    _script = new Script();
    _theDay = new DropdownMenu(name+"_day");
    _theMonth = new DropdownMenu(name+"_month");
    _theYear = new DropdownMenu(name+"_year");
    _theWholeDate = new Parameter(name,"");

    initialize();
  }

  /**
   *
   */
  public DateInput(String name, boolean inShort) {
    super();
    super.setName(name);
    _justConstructed = true;
    _script = new Script();
    _theDay = new DropdownMenu(name+"_day");
    _theMonth = new DropdownMenu(name+"_month");
    _theYear = new DropdownMenu(name+"_year");
    _theWholeDate = new Parameter(name,"");

    _inShort = inShort;
    initialize();
  }

  /**
   *
   */
  public Object clone() {
    DateInput newObject = (DateInput)super.clone();
    if (_theWholeDate != null) {
      newObject._theWholeDate = (Parameter)_theWholeDate.clone();
    }
    if (_theDay != null) {
      newObject._theDay = (DropdownMenu)_theDay.clone();
    }
    if (_theMonth != null) {
      newObject._theMonth = (DropdownMenu)_theMonth.clone();
    }
    if (_theYear != null) {
      newObject._theYear = (DropdownMenu)_theYear.clone();
    }
    if (_script != null) {
      newObject._script = (Script)_script.clone();
    }

    return(newObject);
  }

  /**
   *
   */
  public void setName(String name) {
    super.setName(name);
    _theWholeDate.setName(name);
    _theDay.setName(name+"_day");
    _theMonth.setName(name+"_month");
    _theYear.setName(name+"_year");
  }

  /**
   *
   */
  private void initialize() {
    _theDay.setParentObject(getParentObject());
    _theMonth.setParentObject(getParentObject());
    _theYear.setParentObject(getParentObject());

    idegaTimestamp stamp = idegaTimestamp.RightNow();
    int currentYear = stamp.getYear();
    setYearRange(currentYear,currentYear+5);

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

    _theYear.addMenuElement("YY");

    getScript().addFunction("populateDays","function populateDays(yearInput,monthInput,dayInput) {\r	timeA = new Date(yearInput.options[yearInput.selectedIndex].text, monthInput.options[monthInput.selectedIndex].value,1);\r	timeDifference = timeA - 86400000;\r	timeB = new Date(timeDifference);\r	\r	var oldSelectedDay = dayInput.selectedIndex;\r	\r	var daysInMonth = timeB.getDate();\r	for (var i = 0; i < dayInput.length; i++) {\r		dayInput.options[0] = null;\r		dayInput.options[0] = new Option('Dagur','');\r	}\r	for (var i = 1; i <= daysInMonth; i++) {\r		if (i<10){\r			dayInput.options[i] = new Option(i,'0'+i);\r		}\r		else{\r			dayInput.options[i] = new Option(i,i);\r		}\r	}\r	\r	dayInput.options[oldSelectedDay].selected = true;\r	\r}");
    getScript().addFunction("setValueOfHiddenDate","function setValueOfHiddenDate(yearInput,monthInput,dayInput,hiddenInput){\r\r	var yearValue='2000';\r	var monthValue='01';\r	var dayValue='01';\r\r	if(yearInput.selectedIndex != 0){\r		yearValue=yearInput.options[yearInput.selectedIndex].value;\r	}\r	if(monthInput.selectedIndex != 0){\r		monthValue=monthInput.options[monthInput.selectedIndex].value;\r	}\r	if(dayInput.selectedIndex != 0){\r		dayValue=dayInput.options[dayInput.selectedIndex].value;\r	}\r\r	if ((yearInput.selectedIndex == 0) || (monthInput.selectedIndex == 0) || (dayInput.selectedIndex == 0)){\r		hiddenInput.value = '';\r	}\r	else{\r		hiddenInput.value = yearValue+'-'+monthValue+'-'+dayValue+'';\r	}\r}");
  }

  /**
   *
   */
  private Script getScript() {
    return(_script);
  }

  /**
   *
   */
  public void setStyle(String styleAttribute) {
    _theDay.setStyle(styleAttribute);
    _theMonth.setStyle(styleAttribute);
    _theYear.setStyle(styleAttribute);
  }

  /**
   *
   */
  public void setStyleAttribute(String attributeName, String attributeValue) {
    _theDay.setAttribute(attributeName,attributeValue);
    _theMonth.setAttribute(attributeName,attributeValue);
    _theYear.setAttribute(attributeName,attributeValue);
  }

  /**
   *
   */
  public void keepStatusOnAction() {
    _theDay.keepStatusOnAction();
    _theMonth.keepStatusOnAction();
    _theYear.keepStatusOnAction();
  }

  /**
   *
   */
  public void setYear(int year) {
    _setCheck = true;
    if (_fromYear > _toYear) {
      if (_fromYear < year) {
        _fromYear = year;
      }
      if (_toYear > year) {
        _toYear = year;
      }
    }
    else {
      if (_fromYear > year) {
        _fromYear = year;
      }
      if (_toYear < year) {
        _toYear = year;
      }
    }

    _selectedYear=year;
  }

  /**
   *
   */
  public void setYear(String year) {
    setYear(Integer.parseInt(year));
  }

  /**
   *
   */
  public void setMonth(int month) {
    setMonth(Integer.toString(month));
  }

  /**
   *
   */
  public void setMonth(String month) {
    _setCheck = true;
    if (month.length() > 1) {
      _theMonth.setSelectedElement(month);
    }
    else {
      _theMonth.setSelectedElement("0"+month);
    }
  }

  /**
   *
   */
  public void setDay(int day) {
    setDay(Integer.toString(day));
  }

  /**
   *
   */
  public void setDay(String day) {
    _setCheck = true;
    if (day.length() > 1) {
      _theDay.setSelectedElement(day);
    }
    else {
      _theDay.setSelectedElement("0"+day);
    }
  }

  /**
   *
   */
  public void setDate(Date date) {
    GregorianCalendar greg = new GregorianCalendar();
    greg.setTime(date);
    setYear(greg.get(Calendar.YEAR));
    setMonth(greg.get(Calendar.MONTH)+1);
    setDay(greg.get(Calendar.DAY_OF_MONTH));
  }

  /**
   *
   */
  public void setToCurrentDate() {
    GregorianCalendar greg = new GregorianCalendar();
    setYear(greg.get(Calendar.YEAR));
    setMonth(greg.get(Calendar.MONTH)+1);
    setDay(greg.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * Does nothing - overrides function in superclass
   */
  public void add(PresentationObject mo) {
  }

  /**
   *
   */
  public void setYearRange(int fromYear, int toYear) {
    _fromYear = fromYear;
    _toYear = toYear;
  }

  /**
   *
   */
  public void setNoDayView() {
    _isShowDay = false;
    setDay(1);
  }

  /**
   *
   */
  public void main(IWContext iwc) throws Exception {
    if (_justConstructed) {
      if (_isShowDay) {
        super.add(_theDay);
      }
      super.add(_theMonth);
      super.add(_theYear);
      super.add(_theWholeDate);
      super.add(_script);

      addLocalized(iwc);
      _justConstructed = false;
    }

    addScriptElements(iwc);
  }

  /**
   *
   */
  public void addScriptElements(IWContext iwc){
    _theYear.setOnChange("populateDays(this,this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+");setValueOfHiddenDate(this.form."+_theYear.getName()+",this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+",this.form."+_theWholeDate.getName()+");");
    _theMonth.setOnChange("populateDays(this.form."+_theYear.getName()+",this,this.form."+_theDay.getName()+");setValueOfHiddenDate(this.form."+_theYear.getName()+",this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+",this.form."+_theWholeDate.getName()+");");
    _theDay.setOnChange("setValueOfHiddenDate(this.form."+_theYear.getName()+",this.form."+_theMonth.getName()+",this.form."+_theDay.getName()+",this.form."+_theWholeDate.getName()+");");
  }

  /**
   *
   */
  private void addLocalized(IWContext iwc) {
    Locale locale = iwc.getCurrentLocale();
    DateFormatSymbols symbols = new DateFormatSymbols(locale);
    IWBundle iwb = getBundle(iwc);
    IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
    String monthStrings[];

    String dayString;
    String monthString;
    String yearString;

    if (_inShort) {
      dayString = iwrb.getLocalizedString(DAY_KEY_S,"D");
      monthString = iwrb.getLocalizedString(MONTH_KEY_S,"M");
      yearString = iwrb.getLocalizedString(YEAR_KEY_S,"Y");
      monthStrings = symbols.getShortMonths();
    }
    else {
      dayString = iwrb.getLocalizedString(DAY_KEY,"Day");
      monthString = iwrb.getLocalizedString(MONTH_KEY,"Month");
      yearString = iwrb.getLocalizedString(YEAR_KEY,"Year");
      monthStrings = symbols.getMonths();
    }

    _theDay.setMenuElementDisplayString("00",dayString);
    _theMonth.setMenuElementDisplayString("00",monthString);
    _theYear.setMenuElementDisplayString("YY",yearString);

    if (!_inShort) {
      monthStrings = symbols.getMonths();
    }

    for (int i = 1; i <= 12; i++) {
      String value=Integer.toString(i);
      if (i < 10) {
        value = "0" + value;
      }
      _theMonth.setMenuElementDisplayString(value,monthStrings[i-1]);
    }
  }

  /**
   *
   */
  public void print(IWContext iwc) throws Exception {
    if (_fromYear < _toYear) {
      for (int i = _fromYear; i <= _toYear; i++) {
        _theYear.addMenuElement(Integer.toString(i));
      }
    }
    else {
      for (int i = _fromYear; i >= _toYear; i--) {
        _theYear.addMenuElement(Integer.toString(i));
      }
    }

    if (_selectedYear != -1) {
      _theYear.setSelectedElement(Integer.toString(_selectedYear));
    }

    if (_setCheck == true) {
      _theWholeDate.setValue(_theYear.getSelectedElementValue()+"-"+_theMonth.getSelectedElementValue()+"-"+_theDay.getSelectedElementValue());
    }
    super.print(iwc);
  }
}