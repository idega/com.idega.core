/*
 * $Id: DateInput.java,v 1.49 2004/05/11 18:04:35 thomas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.idega.business.InputHandler;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;
import com.idega.util.IWTimestamp;
import com.idega.util.text.TextSoap;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class DateInput extends InterfaceObject implements InputHandler{
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
  private String dayString;
  private String monthString;
  private String yearString;
  
  final static int SYSTEM_LAUNCH_YEAR = 2004;
  
  // added by thomas
  // Flag that indicates if the dropdown menu should contain the null value (no date) or not.
  // Caution: If this flag is set to true it is not possible to choose not a date.
  private boolean showNullValue = true;
  private boolean _keepStatusOnAction = false;

	private boolean isSetAsNotEmpty;
	private String notEmptyErrorMessage;
	private Date _earliestDate;
	private String _earliestDateErrorMessage;
	private Date _latestDate;
	private String _latestDateErrorMessage;
	private String _styleClass;
	
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
    this(name, false);
  }

  /**
   * Creates a new DateInput object.
   *
   * @param name Name of the parameter string
   * @param _inShort True to display dates in shorthand names.
   */
  public DateInput(String name, boolean inShort) {
    this(name, inShort, true);
  }

  /** Creates a new Dateinput object.
   * @param name Name of the parameter string
   * @param _inShort True to display dates in shorthand names.
   * @param showNullValue True if the null value should be shown (that is, it should be possible to choose no date)
   */
  public DateInput(String name, boolean inShort, boolean showNullValue) {
    super.setName(name);
    _justConstructed = true;
    this.showNullValue = showNullValue;
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

	/**
	 * Sets the content (value) of the date input.
	 * @param value	The content to set.
	 */
	public void setContent(String content) {
		IWTimestamp stamp = new IWTimestamp(content);
		if (stamp != null)
			setDate(stamp.getDate());
	}

  private void addMenuElementsToDropdowns() {
    IWTimestamp stamp = IWTimestamp.RightNow();
    int currentYear = stamp.getYear();

    if (_showYear) {
      this.setYearRange(currentYear, currentYear + 5);
    }

    if (showNullValue)  {
      _theMonth.addMenuElement("00");
    }
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

    if (showNullValue) {
    _theDay.addMenuElement("00", "D");
    }
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

    if (this._showYear && showNullValue) {
      _theYear.addMenuElement("YY");
    }
  }

  public void setDisabled(boolean disabled) {
    this._isDisabled = disabled;
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
    _theDay.setMarkupAttribute(attributeName, attributeValue);
    _theMonth.setMarkupAttribute(attributeName, attributeValue);
    _theYear.setMarkupAttribute(attributeName, attributeValue);
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
      _setMonth = month;
    } else {
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

  private void initilizeValues() {
    if (_setMonth != null) {
      _theMonth.setSelectedElement(_setMonth);
    }

    if (_setDay != null) {
      if (_isShowDay) {
        this._theDay.setSelectedElement(_setDay);
      }
    }

    if (this._justConstructed) {
      if (this._showYear) {
      	//hack to avoid duplicate values
      	_theYear.removeElements();
				if (showNullValue) {
				 _theYear.addMenuElement("YY");
				}
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
  	this.empty();
    initilizeValues();
    addLocalized(iwc);
    addScriptElements(iwc);
    _justConstructed = false;

		if (_keepStatusOnAction)
			handleKeepStatus(iwc); 
      
		if (_isDisabled) {
			_theYear.setDisabled(_isDisabled);
			_theMonth.setDisabled(_isDisabled);
			_theDay.setDisabled(_isDisabled);
		}
		if (_styleClass != null) {
			_theYear.setStyleClass(_styleClass);
			_theMonth.setStyleClass(_styleClass);
			_theDay.setStyleClass(_styleClass);
		}

		if (_displayDayLast) {
			if (this._showYear) {
				add(_theYear);
			}

			add(_theMonth);

			if (_isShowDay) {
				add(_theDay);
			}
		} else {
			if (_isShowDay) {
				add(_theDay);
			}

			add(_theMonth);

			if (this._showYear) {
				add(_theYear);
			}
		}

		add(_theWholeDate);
		add(_script);
  }

  public void addScriptElements(IWContext iwc) {
    if (_isShowDay) {
      if (_showYear) {
        _theYear.setOnChange("iwPopulateDaysWithYear(this,this.form." + _theMonth.getName() + ",this.form." + _theDay.getName() + ",'"+dayString+"');iwSetValueOfHiddenDateWithAllInputs(this.form." + _theYear.getName() + ",this.form." + _theMonth.getName() + ",this.form." + _theDay.getName() + ",this.form." + _theWholeDate.getName() + ");");
        _theMonth.setOnChange("iwPopulateDaysWithYear(this.form." + _theYear.getName() + ",this,this.form." + _theDay.getName() + ",'"+dayString+"');iwSetValueOfHiddenDateWithAllInputs(this.form." + _theYear.getName() + ",this.form." + _theMonth.getName() + ",this.form." + _theDay.getName() + ",this.form." + _theWholeDate.getName() + ");");
        _theDay.setOnChange("iwSetValueOfHiddenDateWithAllInputs(this.form." + _theYear.getName() + ",this.form." + _theMonth.getName() + ",this.form." + _theDay.getName() + ",this.form." + _theWholeDate.getName() + ");");
      } else {
        _theMonth.setOnChange("iwPopulateDaysWithMonth('" + this._selectedYear + "',this,this.form." + _theDay.getName() + ",'"+dayString+"');iwSetValueOfHiddenDateWithDay('" + this._selectedYear + "',this.form." + _theMonth.getName() + ",this.form." + _theDay.getName() + ",this.form." + _theWholeDate.getName() + ");");
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
		if (isSetAsNotEmpty) {
			if (_showYear)
				_theYear.setAsNotEmpty(notEmptyErrorMessage, "YY");
			_theMonth.setAsNotEmpty(notEmptyErrorMessage, "00");
			if (_isShowDay)
				_theDay.setAsNotEmpty(notEmptyErrorMessage, "00");
		}
		if (_earliestDate != null) {
			if (getParentForm() != null) {
				Form form = getParentForm();
				form.setOnSubmit("return checkSubmit(this)");
				Script script = form.getAssociatedFormScript();
				if (script == null)
					script = new Script();

				if (script.getFunction("checkSubmit") == null) {
					script.addFunction("checkSubmit", "function checkSubmit(inputs){\n\n}");
				}
				script.addToBeginningOfFunction("checkSubmit", "if (checkEarliestDate (findObj('" + getName() + "'),"+_earliestDate.getTime()+", '"+_earliestDateErrorMessage+"') == false ){\nreturn false;\n}\n");
				
				StringBuffer buffer = new StringBuffer();
				buffer.append("function checkEarliestDate(input, date, warnMsg) {").append("\n\t");
				buffer.append("var returnBoolean = true;").append("\n\t");
				buffer.append("var dateString = input.value;").append("\n\t");
				buffer.append("if (dateString.length > 0) {").append("\n\t\t");
				buffer.append("var oldDate = new Date(date);").append("\n\t\t");
				buffer.append("var month = dateString.substring(5,7) - 1;").append("\n\t\t");
				buffer.append("var newDate = new Date(dateString.substring(0,4),month,dateString.substring(8,10));").append("\n\t\t");
				buffer.append("var difference = oldDate - newDate;").append("\n\t\t");
				buffer.append("if (difference >= 0)").append("\n\t\t\t");
				buffer.append("returnBoolean = false;").append("\n\t");
				buffer.append("}").append("\n\n\t");
				buffer.append("if (!returnBoolean)").append("\n\t\t");
				buffer.append("alert(warnMsg);").append("\n\t");
				buffer.append("return returnBoolean;").append("\n}");
				script.addFunction("checkEarliestDate", buffer.toString());
				
				form.setAssociatedFormScript(script);
			}
		}
		if (_latestDate != null) {
			if (getParentForm() != null) {
				Form form = getParentForm();
				form.setOnSubmit("return checkSubmit(this)");
				Script script = form.getAssociatedFormScript();
				if (script == null)
					script = new Script();

				if (script.getFunction("checkSubmit") == null) {
					script.addFunction("checkSubmit", "function checkSubmit(inputs){\n\n}");
				}
				script.addToFunction("checkSubmit", "if (checkLatestDate (findObj('" + getName() + "'),"+_latestDate.getTime()+", '"+_latestDateErrorMessage+"') == false ){\nreturn false;\n}\n");
				
				StringBuffer buffer = new StringBuffer();
				buffer.append("function checkLatestDate(input, date, warnMsg) {").append("\n\t");
				buffer.append("var returnBoolean = true;").append("\n\t");
				buffer.append("var dateString = input.value;").append("\n\t");
				buffer.append("if (dateString.length > 0) {").append("\n\t\t");
				buffer.append("var oldDate = new Date(date);").append("\n\t\t");
				buffer.append("var month = dateString.substring(5,7) - 1;").append("\n\t\t");
				buffer.append("var newDate = new Date(dateString.substring(0,4),month,dateString.substring(8,10));").append("\n\t\t");
				buffer.append("var difference = newDate - oldDate;").append("\n\t\t");
				buffer.append("if (difference > 0)").append("\n\t\t\t");
				buffer.append("returnBoolean = false;").append("\n\t");
				buffer.append("}").append("\n\n\t");
				buffer.append("if (!returnBoolean)").append("\n\t\t");
				buffer.append("alert(warnMsg);").append("\n\t");
				buffer.append("return returnBoolean;").append("\n}");
				script.addFunction("checkLatestDate", buffer.toString());
				
				form.setAssociatedFormScript(script);
			}
		}
  }

	public void setAsNotEmpty(String errorMessage) {
		isSetAsNotEmpty = true;
		notEmptyErrorMessage = TextSoap.removeLineBreaks(errorMessage);
	}
	
	public void setEarliestPossibleDate(Date date, String errorMessage) {
		_earliestDate = date;
		_earliestDateErrorMessage = errorMessage;
	}

	public void setLatestPossibleDate(Date date, String errorMessage) {
		_latestDate = date;
		_latestDateErrorMessage = errorMessage;
	}

  private void addLocalized(IWContext iwc) {
    Locale locale = iwc.getCurrentLocale();
    DateFormatSymbols symbols = new DateFormatSymbols(locale);
    IWBundle iwb = this.getBundle(iwc);
    IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
    String[] monthStrings;

    if (showNullValue) {
      if (_inShort) {
        dayString = iwrb.getLocalizedString(DAY_KEY_S, "D");
        monthString = iwrb.getLocalizedString(MONTH_KEY_S, "M");
        yearString = iwrb.getLocalizedString(YEAR_KEY_S, "Y");
      } else {
        dayString = iwrb.getLocalizedString(DAY_KEY, "Day");
        monthString = iwrb.getLocalizedString(MONTH_KEY, "Month");
        yearString = iwrb.getLocalizedString(YEAR_KEY, "Year");
      }
      _theDay.setMenuElementDisplayString("00", dayString);
      _theMonth.setMenuElementDisplayString("00", monthString);

      if (this._showYear) {
        _theYear.setMenuElementDisplayString("YY", yearString);
      }
    }
    if (this._inShort) {
      monthStrings = symbols.getShortMonths();
    } 
    else {
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

  /**
   * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
   */
  public void handleKeepStatus(IWContext iwc) {
    if (_theDay != null)
      _theDay.handleKeepStatus(iwc);

    _theMonth.handleKeepStatus(iwc);

    if (_theYear != null)
      _theYear.handleKeepStatus(iwc);
    
    _theWholeDate.handleKeepStatus(iwc);
    initilizeValues();
  }
  
  public void keepStatusOnAction(boolean keepStatus) {
  	_keepStatusOnAction = keepStatus;
  }

	public void keepStatusOnAction() {
		keepStatusOnAction(true);
	}

	/**
	 * @see com.idega.presentation.PresentationObject#setStyleClass(java.lang.String)
	 */
	public void setStyleClass(String styleName) {
		_styleClass = styleName;
	}
	
	public String getIDForDay(){
		return _theDay.getID();	
	}

	public String getIDForMonth(){
		return _theMonth.getID();	
	}

	public String getIDForYear(){
		return _theYear.getID();	
	}

  /**
   * Sets if the drop down menu should contain a null value 
   * (that is a choice corresponding to no date) or not.
   * The default value is true.
   * @param b
   */
  public void setShowNullValue(boolean b) {
    showNullValue = b;
  }

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}

	public String getDisplayForResultingObject(Object value, IWContext iwc) {
		Locale locale = iwc.getCurrentLocale();
		if (value != null) {
			return TextSoap.findAndCut((new IWTimestamp((java.sql.Date)value)).getLocaleDate(locale),"GMT");
		}
		else {
			return null;
		}
	}

	public PresentationObject getHandlerObject(String name,	String stringValue, IWContext iwc) {
		this.setName(name);
		IWTimestamp to = IWTimestamp.RightNow();
		this.setYearRange(SYSTEM_LAUNCH_YEAR, to.getYear());
		this.setDate(to.getDate());			
		return this;
	}

	public Object getResultingObject(String[] value, IWContext iwc)	throws Exception {
		if (value != null && value.length > 0) {
			String dateString = value[0];
			if (" ".equals(dateString) || "".equals(dateString)) {
				return null;
			}
			else {
				SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
				Date date = formatter.parse(dateString);
				java.sql.Date sqlDate = new java.sql.Date(date.getTime());
				return sqlDate;
			}		
		}
		else
			return null;
	}
	
	public PresentationObject getHandlerObject(String name, Collection values, IWContext iwc) {
		String value = (String) Collections.min(values);
		return getHandlerObject(name, value, iwc);
	}


	public Object convertSingleResultingObjectToType(Object value, String className) {
		return value;
	}
	
}

