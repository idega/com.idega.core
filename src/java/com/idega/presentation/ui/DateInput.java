/*
 * $Id: DateInput.java,v 1.22 2002/12/11 13:59:51 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;

import com.idega.presentation.IWContext;
import com.idega.presentation.Script;

import com.idega.util.IWTimestamp;

import java.text.DateFormatSymbols;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class DateInput extends InterfaceObject {
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
  private boolean _showYear = true;
  private boolean _displayDayLast = false;
  private boolean _isDisabled = false;
  private String _setDay;
  private String _setMonth;
  final static String DAY_KEY = "dateinput.day_long";
  final static String MONTH_KEY = "dateinput.month_long";
  final static String YEAR_KEY = "dateinput.year_long";
  final static String DAY_KEY_S = "dateinput.day_short";
  final static String MONTH_KEY_S = "dateinput.month_short";
  final static String YEAR_KEY_S = "dateinput.year_short";

  /**
   * Creates a new DateInput object.
   */
  public DateInput() {
    this("dateinput");
  }

  /**
   * Creates a new DateInput object.
   *
   * @param name Name of the parameter string
   */
  public DateInput(String name) {
    super();
    super.setName(name);
    _justConstructed = true;
    constructInputs();
  }

  /**
   * Creates a new DateInput object.
   *
   * @param name Name of the parameter string
   * @param _inShort True to display dates in shorthand names.
   */
  public DateInput(String name, boolean inShort) {
    super();
    super.setName(name);
    _justConstructed = true;
    this._inShort = inShort;
    constructInputs();
  }

  public Object clone() {
    DateInput newObject = (DateInput) super.clone();

    if (_theWholeDate != null) {
      newObject._theWholeDate = (Parameter) this._theWholeDate.clone();
    }

    if (_theDay != null) {
      newObject._theDay = (DropdownMenu) this._theDay.clone();
    }

    if (_theMonth != null) {
      newObject._theMonth = (DropdownMenu) this._theMonth.clone();
    }

    if (_theYear != null) {
      newObject._theYear = (DropdownMenu) this._theYear.clone();
    }

    if (_script != null) {
      newObject._script = (Script) this._script.clone();
    }

    return newObject;
  }

  public void setName(String name) {
    super.setName(name);

    if (_theWholeDate != null) {
      _theWholeDate.setName(name);
    }

    if (_theDay != null) {
      _theDay.setName(name + "_day");
    }

    if (_theMonth != null) {
      _theMonth.setName(name + "_month");
    }

    if (_showYear) {
      if (_theYear != null) {
        _theYear.setName(name + "_year");
      }
    }
  }

  private void addMenuElementsToDropdowns() {
    IWTimestamp stamp = IWTimestamp.RightNow();
    int currentYear = stamp.getYear();

    if (_showYear) {
      this.setYearRange(currentYear, currentYear + 5);
    }

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

    _theDay.addMenuElement("00", "D");
    _theDay.addMenuElement("01", "1");
    _theDay.addMenuElement("02", "2");
    _theDay.addMenuElement("03", "3");
    _theDay.addMenuElement("04", "4");
    _theDay.addMenuElement("05", "5");
    _theDay.addMenuElement("06", "6");
    _theDay.addMenuElement("07", "7");
    _theDay.addMenuElement("08", "8");
    _theDay.addMenuElement("09", "9");
    _theDay.addMenuElement("10", "10");
    _theDay.addMenuElement("11", "11");
    _theDay.addMenuElement("12", "12");
    _theDay.addMenuElement("13", "13");
    _theDay.addMenuElement("14", "14");
    _theDay.addMenuElement("15", "15");
    _theDay.addMenuElement("16", "16");
    _theDay.addMenuElement("17", "17");
    _theDay.addMenuElement("18", "18");
    _theDay.addMenuElement("19", "19");
    _theDay.addMenuElement("20", "20");
    _theDay.addMenuElement("21", "21");
    _theDay.addMenuElement("22", "22");
    _theDay.addMenuElement("23", "23");
    _theDay.addMenuElement("24", "24");
    _theDay.addMenuElement("25", "25");
    _theDay.addMenuElement("26", "26");
    _theDay.addMenuElement("27", "27");
    _theDay.addMenuElement("28", "28");
    _theDay.addMenuElement("29", "29");
    _theDay.addMenuElement("30", "30");
    _theDay.addMenuElement("31", "31");

    if (this._showYear) {
      _theYear.addMenuElement("YY");
    }
  }

  public void setDisabled(boolean disabled) {
    this._isDisabled = disabled;
  }

  private Script getDateScript() {
    return this._script;
  }

  public void setStyle(String styleAttribute) {
    if (this._isShowDay) {
      _theDay.setStyleClass(styleAttribute);
    }

    _theMonth.setStyleClass(styleAttribute);

    if (this._showYear) {
      _theYear.setStyleClass(styleAttribute);
    }
  }

  public void setStyleAttribute(String attributeName, String attributeValue) {
    _theDay.setAttribute(attributeName, attributeValue);
    _theMonth.setAttribute(attributeName, attributeValue);
    _theYear.setAttribute(attributeName, attributeValue);
  }

  public void setStyleAttribute(String attributeName) {
    _theDay.setStyleAttribute(attributeName);
    _theMonth.setStyleAttribute(attributeName);
    _theYear.setStyleAttribute(attributeName);
  }

  public void setYear(int year) {
    _setCheck = true;

    // Gimmi 13.10.2002
    _selectedYear = year;

    if (_fromYear > _toYear) {
      if (_fromYear < year) {
        _fromYear = year;
      }

      if (_toYear > year) {
        _toYear = year;
      }
    } else {
      if (_fromYear > year) {
        _fromYear = year;
      }

      if (_toYear < year) {
        _toYear = year;
      }
    }
  }

  public void setYear(String year) {
    setYear(Integer.parseInt(year));
  }

  public void setMonth(int month) {
    setMonth(Integer.toString(month));
  }

	/**
	 * Method setMonth.
	 * @param month
	 */
  public void setMonth(String month) {
    _setCheck = true;

    if (month.length() > 1) {
      if (_theMonth != null) {
        _theMonth.setSelectedElement(month);
      }

      _setMonth = month;
    } else {
      if (_theMonth != null) {
        _theMonth.setSelectedElement("0" + month);
      }

      _setMonth = "0" + month;
    }
  }

  public void setDay(int day) {
    setDay(Integer.toString(day));
  }

  public void setDay(String day) {
    _setCheck = true;

    if (day.length() > 1) {
      _setDay = day;
    } else {
      _setDay = "0" + day;
    }
  }

  public void setDate(java.sql.Date date) {
    GregorianCalendar greg = new GregorianCalendar();
    greg.setTime(date);

    setYear(greg.get(Calendar.YEAR));
    setMonth(greg.get(Calendar.MONTH) + 1);
    setDay(greg.get(Calendar.DAY_OF_MONTH));
  }

  public void setToCurrentDate() {
    GregorianCalendar greg = new GregorianCalendar();
    setYear(greg.get(Calendar.YEAR));
    setMonth(greg.get(Calendar.MONTH) + 1);
    setDay(greg.get(Calendar.DAY_OF_MONTH));
  }

  public void setYearRange(int _fromYear, int _toYear) {
    this._fromYear = _fromYear;
    this._toYear = _toYear;
  }

  public void setNoDayView() {
    setToShowDay(false);
  }

  public void setToShowDay(boolean ifShow) {
    this._isShowDay = ifShow;

    if (ifShow) {
    } else {
      this.setDay(1);
    }
  }

  public void setToShowYear(boolean ifShow) {
    this._showYear = ifShow;
  }

  public void setToDisplayDayLast(boolean dayLast) {
    _displayDayLast = dayLast;
  }

  private void constructInputs() {
    if (_justConstructed) {
      String name = getName();
      _script = new Script();

      if (this._isShowDay) {
        _theDay = new DropdownMenu(name + "_day");
      }

      _theMonth = new DropdownMenu(name + "_month");

      if (_showYear) {
        _theYear = new DropdownMenu(name + "_year");
      }

      _theWholeDate = new Parameter(name, "");
      addMenuElementsToDropdowns();
    }
  }

  private void setSetValues() {
    if (_setMonth != null) {
      this.setMonth(_setMonth);
    }

    if (_setDay != null) {
      if (_isShowDay) {
        this._theDay.setSelectedElement(_setDay);
      }
    }

    if (this._justConstructed) {
      if (this._showYear) {
        if (_fromYear < _toYear) {
          for (int i = _fromYear; i <= _toYear; i++) {
            _theYear.addMenuElement(Integer.toString(i));
          }
        } else {
          for (int i = _fromYear; i >= _toYear; i--) {
            _theYear.addMenuElement(Integer.toString(i));
          }
        }
      }
    }

    if (_selectedYear != -1) {
      if (this._showYear) {
        _theYear.setSelectedElement(Integer.toString(_selectedYear));
      }
    }

    if (_setCheck == true) {
      String year = getCurrentYear();
      String month = _theMonth.getSelectedElementValue();
      String day = getCurrentDay();
      _theWholeDate.setValue(year + "-" + month + "-" + day);
    }
  }

  public void main(IWContext iwc) throws Exception {
    setSetValues();
    addLocalized(iwc);
    addScriptElements(iwc);
    _justConstructed = false;
  }

  public void addScriptElements(IWContext iwc) {
    if (_isShowDay) {
      if (_showYear) {
        _theYear.setOnChange("iwPopulateDaysWithYear(this,this.form." + _theMonth.getName() + ",this.form." + _theDay.getName() + ");iwSetValueOfHiddenDateWithAllInputs(this.form." + _theYear.getName() + ",this.form." + _theMonth.getName() + ",this.form." + _theDay.getName() + ",this.form." + _theWholeDate.getName() + ");");
        _theMonth.setOnChange("iwPopulateDaysWithYear(this.form." + _theYear.getName() + ",this,this.form." + _theDay.getName() + ");iwSetValueOfHiddenDateWithAllInputs(this.form." + _theYear.getName() + ",this.form." + _theMonth.getName() + ",this.form." + _theDay.getName() + ",this.form." + _theWholeDate.getName() + ");");
        _theDay.setOnChange("iwSetValueOfHiddenDateWithAllInputs(this.form." + _theYear.getName() + ",this.form." + _theMonth.getName() + ",this.form." + _theDay.getName() + ",this.form." + _theWholeDate.getName() + ");");
      } else {
        _theMonth.setOnChange("iwPopulateDaysWithMonth('" + this._selectedYear + "',this,this.form." + _theDay.getName() + ");iwSetValueOfHiddenDateWithDay('" + this._selectedYear + "',this.form." + _theMonth.getName() + ",this.form." + _theDay.getName() + ",this.form." + _theWholeDate.getName() + ");");
        _theDay.setOnChange("iwSetValueOfHiddenDateWithDay('" + this._selectedYear + "',this.form." + _theMonth.getName() + ",this.form." + _theDay.getName() + ",this.form." + _theWholeDate.getName() + ");");
      }
    } else {
      if (_showYear) {
        _theYear.setOnChange("iwSetValueOfHiddenDateWithYear(this.form." + _theYear.getName() + ",this.form." + _theMonth.getName() + ",this.form." + _theWholeDate.getName() + ");");
        _theMonth.setOnChange("iwSetValueOfHiddenDateWithYear(this.form." + _theYear.getName() + ",this.form." + _theMonth.getName() + ",this.form." + _theWholeDate.getName() + ");");
      } else {
        _theMonth.setOnChange("iwSetValueOfHiddenDateWithMonth('" + this._selectedYear + "',this.form." + _theMonth.getName() + ",'01',this.form." + _theWholeDate.getName() + ");");
      }
    }
  }

  private void addLocalized(IWContext iwc) {
    Locale locale = iwc.getCurrentLocale();
    DateFormatSymbols symbols = new DateFormatSymbols(locale);
    IWBundle iwb = this.getBundle(iwc);
    IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
    String[] monthStrings;
    String dayString;
    String monthString;
    String yearString;

    if (_inShort) {
      dayString = iwrb.getLocalizedString(DAY_KEY_S, "D");
      monthString = iwrb.getLocalizedString(MONTH_KEY_S, "M");
      yearString = iwrb.getLocalizedString(YEAR_KEY_S, "Y");
      monthStrings = symbols.getShortMonths();
    } else {
      dayString = iwrb.getLocalizedString(DAY_KEY, "Day");
      monthString = iwrb.getLocalizedString(MONTH_KEY, "Month");
      yearString = iwrb.getLocalizedString(YEAR_KEY, "Year");
      monthStrings = symbols.getMonths();
    }

    _theDay.setMenuElementDisplayString("00", dayString);
    _theMonth.setMenuElementDisplayString("00", monthString);

    if (this._showYear) {
      _theYear.setMenuElementDisplayString("YY", yearString);
    }

    if (!this._inShort) {
      monthStrings = symbols.getMonths();
    }

    for (int i = 1; i <= 12; i++) {
      String value = Integer.toString(i);

      if (i < 10) {
        value = "0" + value;
      }

      _theMonth.setMenuElementDisplayString(value, monthStrings[i - 1]);
    }
  }

  public String getCurrentYear() {
    if (this._showYear) {
      return _theYear.getSelectedElementValue();
    } else {
      return Integer.toString(_selectedYear);
    }
  }

  private String getCurrentDay() {
    if (this._isShowDay) {
      return _theDay.getSelectedElementValue();
    } else {
      return "01";
    }
  }

  public void print(IWContext iwc) throws Exception {
    super.print(iwc);

    if (_isDisabled) {
      _theYear.setDisabled(_isDisabled);
      _theMonth.setDisabled(_isDisabled);
      _theDay.setDisabled(_isDisabled);
    }

    if (_displayDayLast) {
      if (this._showYear) {
        _theYear._print(iwc);
      }

      _theMonth._print(iwc);

      if (_isShowDay) {
        _theDay._print(iwc);
      }
    } else {
      if (_isShowDay) {
        _theDay._print(iwc);
      }

      _theMonth._print(iwc);

      if (this._showYear) {
        _theYear._print(iwc);
      }
    }

    _theWholeDate._print(iwc);
    _script._print(iwc);
  }

  /**
   * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
   */
  public void handleKeepStatus(IWContext iwc) {
    if (_theDay != null)
      _theDay.handleKeepStatus(iwc);

    _theMonth.handleKeepStatus(iwc);

    if (_theYear != null)
      _theYear.handleKeepStatus(iwc);

    _setCheck = true;
    setSetValues();
  }
}
