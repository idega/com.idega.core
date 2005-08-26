/*
 * $Id: DateInput.java,v 1.55 2005/08/26 16:04:40 gimmi Exp $
 * 
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.presentation.ui;

import java.text.DateFormatSymbols;
import java.text.ParseException;
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
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.2
 */
public class DateInput extends InterfaceObject implements InputHandler {

	private Script script;

	private DropdownMenu theDay;

	private DropdownMenu theMonth;

	private DropdownMenu theYear;

	private Parameter theWholeDate;

	private boolean setCheck = false;

	private boolean isShowDay = true;

	private int fromYear;

	private int toYear;

	private int selectedYear = -1;

	private boolean inShort = false;

	protected boolean justConstructed = false;

	private boolean showYear = true;

	private boolean displayDayLast = false;

	private boolean isDisabled = false;

	private String setDay;

	private String setMonth;

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
	// Flag that indicates if the dropdown menu should contain the null value
	// (no date) or not.
	// Caution: If this flag is set to true it is not possible to choose not a
	// date.
	private boolean showNullValue = true;

	private boolean keepStatusOnAction = false;

	private boolean isSetAsNotEmpty;

	private String notEmptyErrorMessage;

	private Date earliestDate;

	private String earliestDateErrorMessage;

	private Date latestDate;

	private String latestDateErrorMessage;

	private String styleClass;

	/**
	 * Creates a new DateInput object.
	 */
	public DateInput() {
		this("dateinput");
	}

	/**
	 * Creates a new DateInput object.
	 * 
	 * @param name
	 *          Name of the parameter string
	 */
	public DateInput(String name) {
		this(name, false);
	}

	/**
	 * Creates a new DateInput object.
	 * 
	 * @param name
	 *          Name of the parameter string
	 * @param _inShort
	 *          True to display dates in shorthand names.
	 */
	public DateInput(String name, boolean inShort) {
		this(name, inShort, true);
	}

	/**
	 * Creates a new Dateinput object.
	 * 
	 * @param name
	 *          Name of the parameter string
	 * @param _inShort
	 *          True to display dates in shorthand names.
	 * @param showNullValue
	 *          True if the null value should be shown (that is, it should be
	 *          possible to choose no date)
	 */
	public DateInput(String name, boolean inShort, boolean showNullValue) {
		super.setName(name);
		justConstructed = true;
		this.showNullValue = showNullValue;
		this.inShort = inShort;
		constructInputs();
	}

	public Object clone() {
		DateInput newObject = (DateInput) super.clone();

		if (theWholeDate != null) {
			newObject.theWholeDate = (Parameter) theWholeDate.clone();
		}

		if (theDay != null) {
			newObject.theDay = (DropdownMenu) this.theDay.clone();
		}

		if (theMonth != null) {
			newObject.theMonth = (DropdownMenu) this.theMonth.clone();
		}

		if (theYear != null) {
			newObject.theYear = (DropdownMenu) this.theYear.clone();
		}

		if (script != null) {
			newObject.script = (Script) this.script.clone();
		}

		return newObject;
	}

	public void setName(String name) {
		super.setName(name);

		if (theWholeDate != null) {
			theWholeDate.setName(name);
		}

		if (theDay != null) {
			theDay.setName(name + "_day");
		}

		if (theMonth != null) {
			theMonth.setName(name + "_month");
		}

		if (showYear) {
			if (theYear != null) {
				theYear.setName(name + "_year");
			}
		}
	}

	/**
	 * Sets the content (value) of the date input.
	 * 
	 * @param value
	 *          The content to set.
	 */
	public void setContent(String content) {
		IWTimestamp stamp = new IWTimestamp(content);
		if (stamp != null) {
			setDate(stamp.getDate());
		}
	}

	private void addMenuElementsToDropdowns() {
		IWTimestamp stamp = IWTimestamp.RightNow();
		int currentYear = stamp.getYear();

		if (showYear) {
			this.setYearRange(currentYear, currentYear + 5);
		}

		if (showNullValue) {
			theMonth.addMenuElement("00");
		}
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

		if (showNullValue) {
			theDay.addMenuElement("00", "D");
		}
		theDay.addMenuElement("01", "1");
		theDay.addMenuElement("02", "2");
		theDay.addMenuElement("03", "3");
		theDay.addMenuElement("04", "4");
		theDay.addMenuElement("05", "5");
		theDay.addMenuElement("06", "6");
		theDay.addMenuElement("07", "7");
		theDay.addMenuElement("08", "8");
		theDay.addMenuElement("09", "9");
		theDay.addMenuElement("10", "10");
		theDay.addMenuElement("11", "11");
		theDay.addMenuElement("12", "12");
		theDay.addMenuElement("13", "13");
		theDay.addMenuElement("14", "14");
		theDay.addMenuElement("15", "15");
		theDay.addMenuElement("16", "16");
		theDay.addMenuElement("17", "17");
		theDay.addMenuElement("18", "18");
		theDay.addMenuElement("19", "19");
		theDay.addMenuElement("20", "20");
		theDay.addMenuElement("21", "21");
		theDay.addMenuElement("22", "22");
		theDay.addMenuElement("23", "23");
		theDay.addMenuElement("24", "24");
		theDay.addMenuElement("25", "25");
		theDay.addMenuElement("26", "26");
		theDay.addMenuElement("27", "27");
		theDay.addMenuElement("28", "28");
		theDay.addMenuElement("29", "29");
		theDay.addMenuElement("30", "30");
		theDay.addMenuElement("31", "31");

		if (showYear && showNullValue) {
			theYear.addMenuElement("YY");
		}
	}

	public void setDisabled(boolean disabled) {
		isDisabled = disabled;
	}

	public void setStyle(String styleAttribute) {
		if (isShowDay) {
			theDay.setStyleClass(styleAttribute);
		}

		theMonth.setStyleClass(styleAttribute);

		if (showYear) {
			theYear.setStyleClass(styleAttribute);
		}
	}

	public void setStyleAttribute(String attributeName, String attributeValue) {
		theDay.setMarkupAttribute(attributeName, attributeValue);
		theMonth.setMarkupAttribute(attributeName, attributeValue);
		theYear.setMarkupAttribute(attributeName, attributeValue);
	}

	public void setStyleAttribute(String attributeName) {
		theDay.setStyleAttribute(attributeName);
		theMonth.setStyleAttribute(attributeName);
		theYear.setStyleAttribute(attributeName);
	}

	public void setYear(int year) {
		setCheck = true;

		// Gimmi 13.10.2002
		selectedYear = year;

		if (fromYear > toYear) {
			if (fromYear < year) {
				fromYear = year;
			}

			if (toYear > year) {
				toYear = year;
			}
		}
		else {
			if (fromYear > year) {
				fromYear = year;
			}

			if (toYear < year) {
				toYear = year;
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
	 * 
	 * @param month
	 */
	public void setMonth(String month) {
		setCheck = true;

		if (month.length() > 1) {
			setMonth = month;
		}
		else {
			setMonth = "0" + month;
		}
	}

	public void setDay(int day) {
		setDay(Integer.toString(day));
	}

	public void setDay(String day) {
		setCheck = true;

		if (day.length() > 1) {
			setDay = day;
		}
		else {
			setDay = "0" + day;
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

	public void setYearRange(int fromYear, int toYear) {
		this.fromYear = fromYear;
		this.toYear = toYear;
	}

	public void setNoDayView() {
		setToShowDay(false);
	}

	public void setToShowDay(boolean ifShow) {
		isShowDay = ifShow;

		if (!ifShow) {
			setDay(1);
		}
	}

	public void setToShowYear(boolean ifShow) {
		showYear = ifShow;
	}

	public void setToDisplayDayLast(boolean dayLast) {
		displayDayLast = dayLast;
	}

	private void constructInputs() {
		if (justConstructed) {
			String name = getName();
			script = new Script();

			if (isShowDay) {
				theDay = new DropdownMenu(name + "_day");
			}

			theMonth = new DropdownMenu(name + "_month");

			if (showYear) {
				theYear = new DropdownMenu(name + "_year");
			}

			theWholeDate = new Parameter(name, "");
			addMenuElementsToDropdowns();
		}
	}

	private void initilizeValues() {
		if (setMonth != null) {
			theMonth.setSelectedElement(setMonth);
		}

		if (setDay != null) {
			if (isShowDay) {
				theDay.setSelectedElement(setDay);
			}
		}

		if (justConstructed) {
			if (showYear) {
				// hack to avoid duplicate values
				theYear.removeElements();
				if (showNullValue) {
					theYear.addMenuElement("YY");
				}
				if (fromYear < toYear) {
					for (int i = fromYear; i <= toYear; i++) {
						theYear.addMenuElement(Integer.toString(i));
					}
				}
				else {
					for (int i = fromYear; i >= toYear; i--) {
						theYear.addMenuElement(Integer.toString(i));
					}
				}
			}
		}

		if (selectedYear != -1) {
			if (showYear) {
				theYear.setSelectedElement(Integer.toString(selectedYear));
			}
		}

		if (setCheck == true) {
			String year = getCurrentYear();
			String month = theMonth.getSelectedElementValue();
			String day = getCurrentDay();
			theWholeDate.setValue(year + "-" + month + "-" + day);
		}
	}

	public void main(IWContext iwc) throws Exception {
		this.empty();
		initilizeValues();
		addLocalized(iwc);
		addScriptElements(iwc);
		justConstructed = false;

		if (keepStatusOnAction) {
			handleKeepStatus(iwc);
		}

		if (isDisabled) {
			theYear.setDisabled(isDisabled);
			theMonth.setDisabled(isDisabled);
			theDay.setDisabled(isDisabled);
		}
		if (styleClass != null) {
			theYear.setStyleClass(styleClass);
			theMonth.setStyleClass(styleClass);
			theDay.setStyleClass(styleClass);
		}

		if (displayDayLast) {
			if (showYear) {
				add(theYear);
			}

			add(theMonth);

			if (isShowDay) {
				add(theDay);
			}
		}
		else {
			if (isShowDay) {
				add(theDay);
			}

			add(theMonth);

			if (showYear) {
				add(theYear);
			}
		}

		add(theWholeDate);
		add(script);
	}

	public void addScriptElements(IWContext iwc) {
		if (isShowDay) {
			if (showYear) {
				theYear.setOnChange("iwPopulateDaysWithYear(this,this.form." + theMonth.getName() + ",this.form." + theDay.getName() + ",'" + dayString + "');iwSetValueOfHiddenDateWithAllInputs(this.form." + theYear.getName() + ",this.form." + theMonth.getName() + ",this.form." + theDay.getName() + ",this.form." + theWholeDate.getName() + ");");
				theMonth.setOnChange("iwPopulateDaysWithYear(this.form." + theYear.getName() + ",this,this.form." + theDay.getName() + ",'" + dayString + "');iwSetValueOfHiddenDateWithAllInputs(this.form." + theYear.getName() + ",this.form." + theMonth.getName() + ",this.form." + theDay.getName() + ",this.form." + theWholeDate.getName() + ");");
				theDay.setOnChange("iwSetValueOfHiddenDateWithAllInputs(this.form." + theYear.getName() + ",this.form." + theMonth.getName() + ",this.form." + theDay.getName() + ",this.form." + theWholeDate.getName() + ");");
			}
			else {
				theMonth.setOnChange("iwPopulateDaysWithMonth('" + selectedYear + "',this,this.form." + theDay.getName() + ",'" + dayString + "');iwSetValueOfHiddenDateWithDay('" + selectedYear + "',this.form." + theMonth.getName() + ",this.form." + theDay.getName() + ",this.form." + theWholeDate.getName() + ");");
				theDay.setOnChange("iwSetValueOfHiddenDateWithDay('" + selectedYear + "',this.form." + theMonth.getName() + ",this.form." + theDay.getName() + ",this.form." + theWholeDate.getName() + ");");
			}
		}
		else {
			if (showYear) {
				theYear.setOnChange("iwSetValueOfHiddenDateWithYear(this.form." + theYear.getName() + ",this.form." + theMonth.getName() + ",this.form." + theWholeDate.getName() + ");");
				theMonth.setOnChange("iwSetValueOfHiddenDateWithYear(this.form." + theYear.getName() + ",this.form." + theMonth.getName() + ",this.form." + theWholeDate.getName() + ");");
			}
			else {
				theMonth.setOnChange("iwSetValueOfHiddenDateWithMonth('" + selectedYear + "',this.form." + theMonth.getName() + ",'01',this.form." + theWholeDate.getName() + ");");
			}
		}
		if (isSetAsNotEmpty) {
			if (showYear) {
				theYear.setAsNotEmpty(notEmptyErrorMessage, "YY");
			}
			theMonth.setAsNotEmpty(notEmptyErrorMessage, "00");
			if (isShowDay) {
				theDay.setAsNotEmpty(notEmptyErrorMessage, "00");
			}
		}
		if (earliestDate != null) {
			if (getParentForm() != null) {
				Form form = getParentForm();
				form.setOnSubmit("return checkSubmit(this)");
				Script script = form.getAssociatedFormScript();
				if (script == null) {
					script = new Script();
				}

				if (script.getFunction("checkSubmit") == null) {
					script.addFunction("checkSubmit", "function checkSubmit(inputs){\n\n}");
				}

				IWTimestamp earlyDate = new IWTimestamp(earliestDate.getTime());
				earlyDate.setTime(0, 0, 0, 0);
				script.addToBeginningOfFunction("checkSubmit", "if (checkEarliestDate (findObj('" + getName() + "')," + earlyDate.getDate().getTime() + ", '" + earliestDateErrorMessage + "') == false ){\nreturn false;\n}\n");

				StringBuffer buffer = new StringBuffer();
				buffer.append("function checkEarliestDate(input, date, warnMsg) {").append("\n\t");
				buffer.append("var returnBoolean = true;").append("\n\t");
				buffer.append("var dateString = input.value;").append("\n\t");
				buffer.append("if (dateString.length > 0) {").append("\n\t\t");
				buffer.append("var oldDate = new Date(date);").append("\n\t\t");
				buffer.append("var month = dateString.substring(5,7) - 1;").append("\n\t\t");
				buffer.append("var newDate = new Date(dateString.substring(0,4),month,dateString.substring(8,10));").append("\n\t\t");
				buffer.append("var difference = oldDate - newDate;").append("\n\t\t");
				buffer.append("if (difference > 0)").append("\n\t\t\t");
				buffer.append("returnBoolean = false;").append("\n\t");
				buffer.append("}").append("\n\n\t");
				buffer.append("if (!returnBoolean)").append("\n\t\t");
				buffer.append("alert(warnMsg);").append("\n\t");
				buffer.append("return returnBoolean;").append("\n}");
				script.addFunction("checkEarliestDate", buffer.toString());

				form.setAssociatedFormScript(script);
			}
		}
		if (latestDate != null) {
			if (getParentForm() != null) {
				Form form = getParentForm();
				form.setOnSubmit("return checkSubmit(this)");
				Script script = form.getAssociatedFormScript();
				if (script == null) {
					script = new Script();
				}

				if (script.getFunction("checkSubmit") == null) {
					script.addFunction("checkSubmit", "function checkSubmit(inputs){\n\n}");
				}
				script.addToFunction("checkSubmit", "if (checkLatestDate (findObj('" + getName() + "')," + latestDate.getTime() + ", '" + latestDateErrorMessage + "') == false ){\nreturn false;\n}\n");

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
		earliestDate = date;
		earliestDateErrorMessage = errorMessage;
	}

	public void setLatestPossibleDate(Date date, String errorMessage) {
		latestDate = date;
		latestDateErrorMessage = errorMessage;
	}

	private void addLocalized(IWContext iwc) {
		Locale locale = iwc.getCurrentLocale();
		DateFormatSymbols symbols = new DateFormatSymbols(locale);
		IWBundle iwb = this.getBundle(iwc);
		IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
		String[] monthStrings;

		if (showNullValue) {
			if (inShort) {
				dayString = iwrb.getLocalizedString(DAY_KEY_S, "D");
				monthString = iwrb.getLocalizedString(MONTH_KEY_S, "M");
				yearString = iwrb.getLocalizedString(YEAR_KEY_S, "Y");
			}
			else {
				dayString = iwrb.getLocalizedString(DAY_KEY, "Day");
				monthString = iwrb.getLocalizedString(MONTH_KEY, "Month");
				yearString = iwrb.getLocalizedString(YEAR_KEY, "Year");
			}
			theDay.setMenuElementDisplayString("00", dayString);
			theMonth.setMenuElementDisplayString("00", monthString);

			if (showYear) {
				theYear.setMenuElementDisplayString("YY", yearString);
			}
		}
		if (inShort) {
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

			theMonth.setMenuElementDisplayString(value, monthStrings[i - 1]);
		}
	}

	public String getCurrentYear() {
		if (showYear) {
			return theYear.getSelectedElementValue();
		}
		else {
			return Integer.toString(selectedYear);
		}
	}

	private String getCurrentDay() {
		if (isShowDay) {
			return theDay.getSelectedElementValue();
		}
		else {
			return "01";
		}
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		initilizeValues();
		if (theDay != null)
			theDay.handleKeepStatus(iwc);

		theMonth.handleKeepStatus(iwc);

		if (theYear != null) {
			theYear.handleKeepStatus(iwc);
		}

		theWholeDate.handleKeepStatus(iwc);
	}

	public void keepStatusOnAction(boolean keepStatus) {
		keepStatusOnAction = keepStatus;
	}

	public void keepStatusOnAction() {
		keepStatusOnAction(true);
	}

	/**
	 * @see com.idega.presentation.PresentationObject#setStyleClass(java.lang.String)
	 */
	public void setStyleClass(String styleName) {
		styleClass = styleName;
	}

	public String getIDForDay() {
		return theDay.getID();
	}

	public String getIDForMonth() {
		return theMonth.getID();
	}

	public String getIDForYear() {
		return theYear.getID();
	}

	/**
	 * Sets if the drop down menu should contain a null value (that is a choice
	 * corresponding to no date) or not. The default value is true.
	 * 
	 * @param b
	 */
	public void setShowNullValue(boolean b) {
		showNullValue = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}

	public String getDisplayForResultingObject(Object value, IWContext iwc) {
		Locale locale = iwc.getCurrentLocale();
		if (value != null) {
			return TextSoap.findAndCut((new IWTimestamp((java.sql.Date) value)).getLocaleDate(locale), "GMT");
		}
		else {
			return null;
		}
	}

	public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
		this.setName(name);
		if (value != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date date = formatter.parse(value);
				java.sql.Date sqlDate = new java.sql.Date(date.getTime());
				setDate(sqlDate);
				return this;
			}
			catch (ParseException ex) {
				logError("[DateInput] The value " + value + " could not be parsed");
				// go further to the default setting
			}
		}
		IWTimestamp to = IWTimestamp.RightNow();
		this.setYearRange(SYSTEM_LAUNCH_YEAR, to.getYear());
		this.setDate(to.getDate());
		return this;
	}

	public Object getResultingObject(String[] value, IWContext iwc) throws Exception {
		if (value != null && value.length > 0) {
			String dateString = value[0];
			if (" ".equals(dateString) || "".equals(dateString)) {
				return null;
			}
			else {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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
		if (value != null) {
			IWTimestamp stamp = new IWTimestamp(value.toString());
			return stamp.getDate();
		}
		return value;
	}
}