/*
 * $Id: DateInput.java,v 1.60 2006/04/09 12:13:16 laddi Exp $
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
import com.idega.util.CoreConstants;
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
		this.justConstructed = true;
		this.showNullValue = showNullValue;
		this.inShort = inShort;
		constructInputs();
	}

	@Override
	public Object clone() {
		DateInput newObject = (DateInput) super.clone();

		if (this.theWholeDate != null) {
			newObject.theWholeDate = (Parameter) this.theWholeDate.clone();
		}

		if (this.theDay != null) {
			newObject.theDay = (DropdownMenu) this.theDay.clone();
		}

		if (this.theMonth != null) {
			newObject.theMonth = (DropdownMenu) this.theMonth.clone();
		}

		if (this.theYear != null) {
			newObject.theYear = (DropdownMenu) this.theYear.clone();
		}

		if (this.script != null) {
			newObject.script = (Script) this.script.clone();
		}

		return newObject;
	}

	@Override
	public void setName(String name) {
		super.setName(name);

		if (this.theWholeDate != null) {
			this.theWholeDate.setName(name);
		}

		if (this.theDay != null) {
			this.theDay.setName(name + "_day");
		}

		if (this.theMonth != null) {
			this.theMonth.setName(name + "_month");
		}

		if (this.showYear) {
			if (this.theYear != null) {
				this.theYear.setName(name + "_year");
			}
		}
	}

	/**
	 * Sets the content (value) of the date input.
	 *
	 * @param value
	 *          The content to set.
	 */
	@Override
	public void setContent(String content) {
		if(!"".equals(content)){
			IWTimestamp stamp = new IWTimestamp(content);
			if (stamp != null) {
				setDate(stamp.getDate());
			}
		}
	}

	private void addMenuElementsToDropdowns() {
		IWTimestamp stamp = IWTimestamp.RightNow();
		int currentYear = stamp.getYear();

		if (this.showYear) {
			this.setYearRange(currentYear, currentYear + 5);
		}

		if (this.showNullValue) {
			this.theMonth.addMenuElement("00");
		}
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

		if (this.showNullValue) {
			this.theDay.addMenuElement("00", "D");
		}
		this.theDay.addMenuElement("01", "1");
		this.theDay.addMenuElement("02", "2");
		this.theDay.addMenuElement("03", "3");
		this.theDay.addMenuElement("04", "4");
		this.theDay.addMenuElement("05", "5");
		this.theDay.addMenuElement("06", "6");
		this.theDay.addMenuElement("07", "7");
		this.theDay.addMenuElement("08", "8");
		this.theDay.addMenuElement("09", "9");
		this.theDay.addMenuElement("10", "10");
		this.theDay.addMenuElement("11", "11");
		this.theDay.addMenuElement("12", "12");
		this.theDay.addMenuElement("13", "13");
		this.theDay.addMenuElement("14", "14");
		this.theDay.addMenuElement("15", "15");
		this.theDay.addMenuElement("16", "16");
		this.theDay.addMenuElement("17", "17");
		this.theDay.addMenuElement("18", "18");
		this.theDay.addMenuElement("19", "19");
		this.theDay.addMenuElement("20", "20");
		this.theDay.addMenuElement("21", "21");
		this.theDay.addMenuElement("22", "22");
		this.theDay.addMenuElement("23", "23");
		this.theDay.addMenuElement("24", "24");
		this.theDay.addMenuElement("25", "25");
		this.theDay.addMenuElement("26", "26");
		this.theDay.addMenuElement("27", "27");
		this.theDay.addMenuElement("28", "28");
		this.theDay.addMenuElement("29", "29");
		this.theDay.addMenuElement("30", "30");
		this.theDay.addMenuElement("31", "31");

		if (this.showYear && this.showNullValue) {
			this.theYear.addMenuElement("YY");
		}
	}

	@Override
	public void setDisabled(boolean disabled) {
		this.isDisabled = disabled;
	}

	public void setStyle(String styleAttribute) {
		if (this.isShowDay) {
			this.theDay.setStyleClass(styleAttribute);
		}

		this.theMonth.setStyleClass(styleAttribute);

		if (this.showYear) {
			this.theYear.setStyleClass(styleAttribute);
		}
	}

	@Override
	public void setStyleAttribute(String attributeName, String attributeValue) {
		this.theDay.setMarkupAttribute(attributeName, attributeValue);
		this.theMonth.setMarkupAttribute(attributeName, attributeValue);
		this.theYear.setMarkupAttribute(attributeName, attributeValue);
	}

	@Override
	public void setStyleAttribute(String attributeName) {
		this.theDay.setStyleAttribute(attributeName);
		this.theMonth.setStyleAttribute(attributeName);
		this.theYear.setStyleAttribute(attributeName);
	}

	public void setYear(int year) {
		this.setCheck = true;

		// Gimmi 13.10.2002
		this.selectedYear = year;

		if (this.fromYear > this.toYear) {
			if (this.fromYear < year) {
				this.fromYear = year;
			}

			if (this.toYear > year) {
				this.toYear = year;
			}
		}
		else {
			if (this.fromYear > year) {
				this.fromYear = year;
			}

			if (this.toYear < year) {
				this.toYear = year;
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
		this.setCheck = true;

		if (month.length() > 1) {
			this.setMonth = month;
		}
		else {
			this.setMonth = "0" + month;
		}
	}

	public void setDay(int day) {
		setDay(Integer.toString(day));
	}

	public void setDay(String day) {
		this.setCheck = true;

		if (day.length() > 1) {
			this.setDay = day;
		}
		else {
			this.setDay = "0" + day;
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
		this.isShowDay = ifShow;

		if (!ifShow) {
			setDay(1);
		}
	}

	public void setToShowYear(boolean ifShow) {
		this.showYear = ifShow;
	}

	public void setToDisplayDayLast(boolean dayLast) {
		this.displayDayLast = dayLast;
	}

	private void constructInputs() {
		if (this.justConstructed) {
			String name = getName();
			this.script = new Script();

			if (this.isShowDay) {
				this.theDay = new DropdownMenu(name + "_day");
			}

			this.theMonth = new DropdownMenu(name + "_month");

			if (this.showYear) {
				this.theYear = new DropdownMenu(name + "_year");
			}

			this.theWholeDate = new Parameter(name, "");
			addMenuElementsToDropdowns();
		}
	}

	private void initilizeValues() {
		if (this.setMonth != null) {
			this.theMonth.setSelectedElement(this.setMonth);
		}

		if (this.setDay != null) {
			if (this.isShowDay) {
				this.theDay.setSelectedElement(this.setDay);
			}
		}

		if (this.justConstructed) {
			if (this.showYear) {
				// hack to avoid duplicate values
				this.theYear.removeElements();
				if (this.showNullValue) {
					this.theYear.addMenuElement("YY");
				}
				if (this.fromYear < this.toYear) {
					for (int i = this.fromYear; i <= this.toYear; i++) {
						this.theYear.addMenuElement(Integer.toString(i));
					}
				}
				else {
					for (int i = this.fromYear; i >= this.toYear; i--) {
						this.theYear.addMenuElement(Integer.toString(i));
					}
				}
			}
		}

		if (this.selectedYear != -1) {
			if (this.showYear) {
				this.theYear.setSelectedElement(Integer.toString(this.selectedYear));
			}
		}

		if (this.setCheck == true) {
			String year = getCurrentYear();
			String month = this.theMonth.getSelectedElementValue();
			String day = getCurrentDay();
			this.theWholeDate.setValue(year + "-" + month + "-" + day);
		}
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		this.empty();
		initilizeValues();
		addLocalized(iwc);
		addScriptElements(iwc);
		this.justConstructed = false;

		if (this.keepStatusOnAction) {
			handleKeepStatus(iwc);
		}

		if (this.isDisabled) {
			this.theYear.setDisabled(this.isDisabled);
			this.theMonth.setDisabled(this.isDisabled);
			this.theDay.setDisabled(this.isDisabled);
		}
		if (this.styleClass != null) {
			this.theYear.setStyleClass(this.styleClass);
			this.theMonth.setStyleClass(this.styleClass);
			this.theDay.setStyleClass(this.styleClass);
		}

		if (this.displayDayLast) {
			if (this.showYear) {
				add(this.theYear);
			}

			add(this.theMonth);

			if (this.isShowDay) {
				add(this.theDay);
			}
		}
		else {
			if (this.isShowDay) {
				add(this.theDay);
			}

			add(this.theMonth);

			if (this.showYear) {
				add(this.theYear);
			}
		}

		add(this.theWholeDate);
		add(this.script);
	}

	public void addScriptElements(IWContext iwc) {
		if (this.isShowDay) {
			if (this.showYear) {
				this.theYear.setOnChange("iwPopulateDaysWithYear(this,this.form." + this.theMonth.getName() + ",this.form." + this.theDay.getName() + ",'" + this.dayString + "');iwSetValueOfHiddenDateWithAllInputs(this.form." + this.theYear.getName() + ",this.form." + this.theMonth.getName() + ",this.form." + this.theDay.getName() + ",this.form." + this.theWholeDate.getName() + ");");
				this.theMonth.setOnChange("iwPopulateDaysWithYear(this.form." + this.theYear.getName() + ",this,this.form." + this.theDay.getName() + ",'" + this.dayString + "');iwSetValueOfHiddenDateWithAllInputs(this.form." + this.theYear.getName() + ",this.form." + this.theMonth.getName() + ",this.form." + this.theDay.getName() + ",this.form." + this.theWholeDate.getName() + ");");
				this.theDay.setOnChange("iwSetValueOfHiddenDateWithAllInputs(this.form." + this.theYear.getName() + ",this.form." + this.theMonth.getName() + ",this.form." + this.theDay.getName() + ",this.form." + this.theWholeDate.getName() + ");");
			}
			else {
				this.theMonth.setOnChange("iwPopulateDaysWithMonth('" + this.selectedYear + "',this,this.form." + this.theDay.getName() + ",'" + this.dayString + "');iwSetValueOfHiddenDateWithDay('" + this.selectedYear + "',this.form." + this.theMonth.getName() + ",this.form." + this.theDay.getName() + ",this.form." + this.theWholeDate.getName() + ");");
				this.theDay.setOnChange("iwSetValueOfHiddenDateWithDay('" + this.selectedYear + "',this.form." + this.theMonth.getName() + ",this.form." + this.theDay.getName() + ",this.form." + this.theWholeDate.getName() + ");");
			}
		}
		else {
			if (this.showYear) {
				this.theYear.setOnChange("iwSetValueOfHiddenDateWithYear(this.form." + this.theYear.getName() + ",this.form." + this.theMonth.getName() + ",this.form." + this.theWholeDate.getName() + ");");
				this.theMonth.setOnChange("iwSetValueOfHiddenDateWithYear(this.form." + this.theYear.getName() + ",this.form." + this.theMonth.getName() + ",this.form." + this.theWholeDate.getName() + ");");
			}
			else {
				this.theMonth.setOnChange("iwSetValueOfHiddenDateWithMonth('" + this.selectedYear + "',this.form." + this.theMonth.getName() + ",'01',this.form." + this.theWholeDate.getName() + ");");
			}
		}
		if (this.isSetAsNotEmpty) {
			if (this.showYear) {
				this.theYear.setAsNotEmpty(this.notEmptyErrorMessage, "YY");
			}
			this.theMonth.setAsNotEmpty(this.notEmptyErrorMessage, "00");
			if (this.isShowDay) {
				this.theDay.setAsNotEmpty(this.notEmptyErrorMessage, "00");
			}
		}
		if (this.earliestDate != null) {
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

				IWTimestamp earlyDate = new IWTimestamp(this.earliestDate.getTime());
				earlyDate.setTime(0, 0, 0, 0);
				script.addToBeginningOfFunction("checkSubmit", "if (checkEarliestDate (findObj('" + getName() + "')," + earlyDate.getDate().getTime() + ", '" + this.earliestDateErrorMessage + "') == false ){\nreturn false;\n}\n");

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
		if (this.latestDate != null) {
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
				script.addToFunction("checkSubmit", "if (checkLatestDate (findObj('" + getName() + "')," + this.latestDate.getTime() + ", '" + this.latestDateErrorMessage + "') == false ){\nreturn false;\n}\n");

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
		this.isSetAsNotEmpty = true;
		this.notEmptyErrorMessage = TextSoap.removeLineBreaks(errorMessage);
	}

	public void setEarliestPossibleDate(Date date, String errorMessage) {
		this.earliestDate = date;
		this.earliestDateErrorMessage = errorMessage;
	}

	public void setLatestPossibleDate(Date date, String errorMessage) {
		this.latestDate = date;
		this.latestDateErrorMessage = errorMessage;
	}

	private void addLocalized(IWContext iwc) {
		Locale locale = iwc.getCurrentLocale();
		DateFormatSymbols symbols = new DateFormatSymbols(locale);
		IWBundle iwb = this.getBundle(iwc);
		IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
		String[] monthStrings;

		if (this.showNullValue) {
			if (this.inShort) {
				this.dayString = iwrb.getLocalizedString(DAY_KEY_S, "D");
				this.monthString = iwrb.getLocalizedString(MONTH_KEY_S, "M");
				this.yearString = iwrb.getLocalizedString(YEAR_KEY_S, CoreConstants.Y);
			}
			else {
				this.dayString = iwrb.getLocalizedString(DAY_KEY, "Day");
				this.monthString = iwrb.getLocalizedString(MONTH_KEY, "Month");
				this.yearString = iwrb.getLocalizedString(YEAR_KEY, "Year");
			}
			this.theDay.setMenuElementDisplayString("00", this.dayString);
			this.theMonth.setMenuElementDisplayString("00", this.monthString);

			if (this.showYear) {
				this.theYear.setMenuElementDisplayString("YY", this.yearString);
			}
		}
		if (this.inShort) {
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

			this.theMonth.setMenuElementDisplayString(value, monthStrings[i - 1]);
		}
	}

	public String getCurrentYear() {
		if (this.showYear) {
			return this.theYear.getSelectedElementValue();
		}
		else {
			return Integer.toString(this.selectedYear);
		}
	}

	private String getCurrentDay() {
		if (this.isShowDay) {
			return this.theDay.getSelectedElementValue();
		}
		else {
			return "01";
		}
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	@Override
	public void handleKeepStatus(IWContext iwc) {
		try {
			super.handleKeepStatus(iwc);
		} catch (AssertionError e) {
			return;
		}

		initilizeValues();
		String name = iwc.getParameter(getName());

		String nameDay = null;
		String nameMonth = null;
		String nameYear = null;
		if (name != null && !"".equals(name)) {
			nameDay = name.substring(8, 10);
			nameMonth = name.substring(5, 7);
			nameYear = name.substring(0, 4);
		}


		if (this.theDay != null && nameDay != null) {
			this.theDay.setSelectedElement(nameDay);
		}

		if (nameMonth != null) {
			this.theMonth.setSelectedElement(nameMonth);
		}

		if (this.theYear != null && nameYear != null) {
			this.theYear.setSelectedElement(nameYear);
		}

		if (name != null) {
			this.theWholeDate.setValue(name);
		}
	}

	@Override
	public void keepStatusOnAction(boolean keepStatus) {
		this.keepStatusOnAction = keepStatus;
	}

	@Override
	public void keepStatusOnAction() {
		keepStatusOnAction(true);
	}

	/**
	 * @see com.idega.presentation.PresentationObject#setStyleClass(java.lang.String)
	 */
	@Override
	public void setStyleClass(String styleName) {
		this.styleClass = styleName;
	}

	public String getIDForDay() {
		return this.theDay.getID();
	}

	public String getIDForMonth() {
		return this.theMonth.getID();
	}

	public String getIDForYear() {
		return this.theYear.getID();
	}

	/**
	 * Sets if the drop down menu should contain a null value (that is a choice
	 * corresponding to no date) or not. The default value is true.
	 *
	 * @param b
	 */
	public void setShowNullValue(boolean b) {
		this.showNullValue = b;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public String getDisplayForResultingObject(Object value, IWContext iwc) {
		Locale locale = iwc.getCurrentLocale();
		if (value != null) {
			return TextSoap.findAndCut((new IWTimestamp((java.sql.Date) value)).getLocaleDate(locale), "GMT");
		}
		else {
			return null;
		}
	}

	@Override
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

	@Override
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
		else {
			return null;
		}
	}

	@Override
	public PresentationObject getHandlerObject(String name, Collection values, IWContext iwc) {
		String value = (String) Collections.min(values);
		return getHandlerObject(name, value, iwc);
	}

	@Override
	public Object convertSingleResultingObjectToType(Object value, String className) {
		if (value != null) {
			IWTimestamp stamp = new IWTimestamp(value.toString());
			return stamp.getDate();
		}
		return value;
	}
}