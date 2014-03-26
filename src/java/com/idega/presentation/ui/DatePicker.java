/*
 * Created on Dec 19, 2003
 * 
 */
package com.idega.presentation.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import com.idega.business.InputHandler;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;
import com.idega.util.IWCalendar;
import com.idega.util.IWTimestamp;
import com.idega.util.text.TextSoap;

/**
 * DatePicker is a chooser object used to choose a date from a calendar. The
 * chooser leaves a hidden input with the name of the chooser in a form with the
 * value of the chosen date represented in the timestamp format yyyy-mm-dd
 * hh:mm:ss:fffffffff where fffffffff are nanoseconds The default dateformat
 * style is DateFormat.SHORT A IWTimestamp object can be constructed from the
 * timestamp format value.
 * 
 * @author <a href="aron@idega.is">Aron Birkir </a>
 * @version 1.0
 */
public class DatePicker extends AbstractChooser implements InputHandler {

	private static Boolean jsExists = null;
	private Locale locale = null;
	private boolean useJSCalendar = true;
	private boolean showEmpty = false;

	private int dateFormatStyle = DateFormat.SHORT;
	private String dateFormatPattern = "yyyy-MM-dd";
	private Date date = new Date();
	private String styleClass = null;
	private int length = -1;
	private boolean keepStatus = false;

	private String textInputId = null;
	private String textInputOnchange = null;

	/**
	 * Creates a new DateInput object.
	 */
	public DatePicker() {
		this(null);
	}

	public DatePicker(String pickerName) {
		this(pickerName, null, null, null);
	}

	public DatePicker(String pickerName, Locale locale) {
		this(pickerName, null, locale, null);
	}

	public DatePicker(String pickerName, String style) {
		this(pickerName, style, null);
	}

	public DatePicker(String pickerName, String style, Locale locale) {
		this(pickerName, style, locale, null);
	}

	public DatePicker(String name, String style, Locale locale, Date date) {
		addForm(false);
		if (name != null) {
			setChooserParameter(name);
		}
		if (style != null) {
			setInputStyle(style);
		}
		if (locale != null) {
			this.locale = locale;
		}
		this.date = date;
	}

	public void main(IWContext iwc) {
		empty();
		IWBundle iwb = getBundle(iwc);
		IWResourceBundle iwrb = this.getResourceBundle(iwc);
		this.dateFormatPattern = iwc.getApplicationSettings().getProperty("DatePicker.date_format_string", "yyyy-MM-dd");
		setChooseButtonImage(iwb.getImage("calendar.gif", iwrb.getLocalizedString("datepicker.pick_date", "Pick date")));
		if (this.locale == null) {
			this.locale = iwc.getCurrentLocale();
		}
		if (this.date != null) {
			setDate(this.date);
		}
		else if (!showEmpty) {
			setDate(new Date());
		}
		// setParameterValue(SmallCalendar.PRM_SETTINGS,SmallCalendar.getInitializingString(true,null,"#0000FF","#00FF00","#00FFFF","#FFFF00","#FFFFFF","#FFF000"));
	}

	private TextInput realInput = null;
	
	public PresentationObject getPresentationObject(IWContext iwc) {
		if (realInput != null) {
			return realInput;
		}
		
		realInput = new TextInput(this.displayInputName);
		realInput.setDisabled(this.disabled);
		if (textInputId != null) {
			realInput.setId(textInputId);
		}
		if (textInputOnchange != null) {
			realInput.setOnChange(textInputOnchange);
		}

		int inputLength = 10;
		if (this.length < 0) {
			switch (this.dateFormatStyle) {
				case DateFormat.SHORT:
					inputLength = 10;
					break;
				case DateFormat.MEDIUM:
					inputLength = 12;
					break;
				case DateFormat.LONG:
					inputLength = 16;
					break;
				case DateFormat.FULL:
					inputLength = 20;
					break;
			}
		}
		else {
			inputLength = this.length;
		}
		realInput.setLength(inputLength);
		if (this._style != null) {
			realInput.setMarkupAttribute("style", this._style);
		}

		if (this.keepStatus && iwc.isParameterSet(getChooserParameter())) {
			String p = iwc.getParameter(getChooserParameter());
			setDate(new IWTimestamp(p).getDate());
		}
		if (this._stringDisplay != null) {
			realInput.setValue(this._stringDisplay);
		}
		if (this.styleClass != null) {
			realInput.setStyleClass(this.styleClass);
		}

		return realInput;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.ui.AbstractChooser#getChooserWindowClass()
	 */
	public Class getChooserWindowClass() {
		return DatePickerWindow.class;
	}

	/**
	 * Sets the chosen date andformats it according to the formatstyle set, and
	 * the locale set, or default locale if none is set
	 * 
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
		String display = date.toString();
		String value = new SimpleDateFormat("yyyy-MM-dd").format(date);
		if (this.locale != null) {
			display = new SimpleDateFormat(this.dateFormatPattern, this.locale).format(date);
			// DateFormat.getDateInstance(dateFormatStyle, locale).format(date);
		}
		else {
			display = new SimpleDateFormat(this.dateFormatPattern).format(date);

			// DateFormat.getDateInstance(dateFormatStyle).format(date);
		}
		setChooserValue(display, value);
	}

	/**
	 * Sets the chosen date and formats it according to the formatstyle set and
	 * locale.
	 * 
	 * @param date
	 * @param locale
	 */
	public void setDate(Date date, Locale locale) {
		this.locale = locale;
		setDate(date);
	}

	/**
	 * Sets the format style used by DateFormat to format the date chosen values
	 * can be one of SHORT,MEDIUM,LONG,FULL in class DateFormat
	 * 
	 * @param formatStyle
	 */
	public void setDateFormatStyle(int formatStyle) {
		this.dateFormatStyle = formatStyle;
	}

	/**
	 * Sets the format pattern for date display ( see java.text.SimpleDateFormat)
	 * 
	 * @param pattern
	 */
	public void setDateFormatPattern(String pattern) {
		this.dateFormatPattern = pattern;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.business.InputHandler#getHandlerObject(java.lang.String, java.lang.String, com.idega.presentation.IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
		// setName(name);
		setChooserParameter(name);
		if (value != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date date = formatter.parse(value);
				java.sql.Date sqlDate = new java.sql.Date(date.getTime());
				setDate(sqlDate, iwc.getCurrentLocale());
				return this;
			}
			catch (ParseException ex) {
				logError("[DateInput] The value " + value + " could not be parsed");
				// go further to the default setting
			}
		}
		else {
			setDate(new Date());
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.business.InputHandler#getHandlerObject(java.lang.String, java.util.Collection, com.idega.presentation.IWContext)
	 */
	public PresentationObject getHandlerObject(String name, Collection values, IWContext iwc) {
		String value = (String) Collections.min(values);
		return getHandlerObject(name, value, iwc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.business.InputHandler#getResultingObject(java.lang.String[], com.idega.presentation.IWContext)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.business.InputHandler#getDisplayForResultingObject(java.lang.Object, com.idega.presentation.IWContext)
	 */
	public String getDisplayForResultingObject(Object value, IWContext iwc) {
		Locale locale = iwc.getCurrentLocale();
		if (value != null) {
			return TextSoap.findAndCut((new IWTimestamp((java.sql.Date) value)).getLocaleDate(locale), "GMT");
		}
		else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.business.InputHandler#convertSingleResultingObjectToType(java.lang.Object, java.lang.String)
	 */
	public Object convertSingleResultingObjectToType(Object value, String className) {
		return value;
	}

	private boolean useJSCalendar(IWBundle bundle) {
		if (!this.useJSCalendar) {
			return false;
		}
		else if (jsExists != null) {
			return jsExists.booleanValue();
		}
		else {
			try {
				java.io.File jsFile = new java.io.File(IWMainApplication.getDefaultIWMainApplication().getRealPath(bundle.getImageURI("jscalendar/calendar.js")));
				jsExists = Boolean.valueOf(jsFile.exists());
				return jsExists.booleanValue();
			}
			catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.ui.AbstractChooser#getTable(com.idega.presentation.IWContext, com.idega.idegaweb.IWBundle)
	 */
	public PresentationObject getTable(IWContext iwc, IWBundle bundle) {
		IWResourceBundle iwrb = bundle.getResourceBundle(iwc);
		if (!useJSCalendar(bundle)) {
			return super.getTable(iwc, bundle);
		}

		Layer layer = new Layer();
		layer.setStyleClass("datePicker");

		PresentationObject object = getPresentationObject(iwc);
		Parameter value = new Parameter(getChooserParameter(), "");
		if (getChooserValue() != null) {
			value.setValue(getChooserValue());
		}

		Image button = (bundle.getImage("calendar.gif", iwrb.getLocalizedString("datepicker.pick_date", "Pick date")));
		button.setOnClick("return showCalendar('" + object.getID() + "', '" + this.dateFormatPattern + "','" + value.getID() + "');");


		// String langScriptURI = "jscalendar/calendar-"+iwc.getCurrentLocale().getLanguage()+".js";
		// String langScriptURI = "jscalendar/calendar_lang.js";
		// checkOrCreateLanguageScript(iwc,iwrb,langScriptURI);
		// parentPage.addJavascriptURL(iwrb.getImageURI(langScriptURI ));
//		parentPage.addJavaScriptAfterJavaScriptURLs("calendar_lang", createCalendarLangScript(iwrb));
//		Page parentPage = getParentPage();
		addRequiredJavascriptAndStylesheets(iwc, getParentPage());
//		Script s1 = new Script();
//		s1.addScriptSource(bundle.getImageURI("jscalendar/calendar.js"));
//		
//		Script s2 = new Script();
//		s2.addScriptSource(bundle.getImageURI("jscalendar/calendarHelper.js"));
//
//		Stylesheet sh = new Stylesheet();
//		sh.setPath(bundle.getImageURI("jscalendar/calendar-win2k-1.css"));
//		
//		add(s1);
//		add(s2);
//		add(sh);
		

		Script s =  new Script();
		s.addFunction("calendar_lang", createCalendarLangScript(iwrb));
		layer.add(s);

		
		layer.add(value);
		layer.add(new Parameter(VALUE_PARAMETER_NAME, value.getName()));
		layer.add(object);
		layer.add(button);

		return layer;
	}
	
	public static void addRequiredJavascriptAndStylesheets(IWContext iwc, Page parentPage) {
		IWMainApplication iwma = iwc.getApplicationContext().getIWMainApplication();
		IWBundle bundle = iwma.getBundle(PresentationObject.CORE_IW_BUNDLE_IDENTIFIER);

		parentPage.addJavascriptURL(bundle.getImageURI("jscalendar/calendar.js"));

		parentPage.addJavascriptURL(bundle.getImageURI("jscalendar/calendarHelper.js"));
		parentPage.addStyleSheetURL(bundle.getImageURI("jscalendar/calendar-win2k-1.css"));
	}

	/*
	 * public void checkOrCreateLanguageScript(IWContext iwc,IWResourceBundle iwrb, String scriptBundleURI){ if(langFiles==null) langFiles = new
	 * HashMap(2); if(!langFiles.containsKey(locale.getCountry())){ try { java.io.File jsFile = new
	 * java.io.File(iwc.getIWMainApplication().getRealPath(iwrb.getImageURI(scriptBundleURI))); if(!jsFile.exists()){
	 * 
	 * jsFile.createNewFile(); BufferedWriter writer = new BufferedWriter(new FileWriter(jsFile)); writer.write(createCalendarLangScript(iwrb));
	 * writer.flush(); writer.close(); } langFiles.put(locale.getCountry(),"true"); } catch (Exception e) { e.printStackTrace(); } } }
	 */

	public String createCalendarLangScript(IWResourceBundle iwrb) {
		Locale locale = iwrb.getLocale();
		IWCalendar cal = new IWCalendar();
		StringBuffer script = new StringBuffer();
		script.append("// ** I18N **\n");
		script.append("Calendar._DN = new Array(");
		script.append("\"").append(cal.getDayName(1, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getDayName(2, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getDayName(3, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getDayName(4, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getDayName(5, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getDayName(6, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getDayName(7, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getDayName(1, locale, IWCalendar.LONG)).append("\"");
		script.append(");\n");

		script.append("Calendar._MN = new Array(");
		script.append("\"").append(cal.getMonthName(1, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getMonthName(2, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getMonthName(3, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getMonthName(4, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getMonthName(5, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getMonthName(6, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getMonthName(7, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getMonthName(8, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getMonthName(9, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getMonthName(10, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getMonthName(11, locale, IWCalendar.LONG)).append("\",");
		script.append("\"").append(cal.getMonthName(12, locale, IWCalendar.LONG)).append("\"");
		script.append(");\n");

		// Calendar._DN = new Array("Sunnudagur","Manudagur","Tridjudagur","Midvikudagur","Fimmtudagur","Fostudagur","Laugardagur","Sunnudagur");
		// Calendar._MN = new Array("januar","februar","mars","april","mai","juni","juli","agust","september","oktober","november","desember");

		script.append("//		 tooltips \n");
		script.append("Calendar._TT = {};\n");
		script.append("Calendar._TT[\"TOGGLE\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.toggle_first_day_of_week", "Toggle first day of week") + "\";\n");
		script.append("Calendar._TT[\"PREV_YEAR\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.prev_year", "Prev. year (hold for menu)") + "\";\n");
		script.append("Calendar._TT[\"PREV_MONTH\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.prev_month", "Prev. month (hold for menu)") + "\";\n");
		script.append("Calendar._TT[\"GO_TODAY\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.go_today", "Go Today") + "\";\n");
		script.append("Calendar._TT[\"NEXT_MONTH\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.next_month", "Next month (hold for menu)") + "\";\n");
		script.append("Calendar._TT[\"NEXT_YEAR\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.next_year", "Next year (hold for menu)") + "\";\n");
		script.append("Calendar._TT[\"SEL_DATE\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.select_date", "Select date") + "\";\n");
		script.append("Calendar._TT[\"DRAG_TO_MOVE\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.drag_to_move", "Drag to move") + "\";\n");
		script.append("Calendar._TT[\"PART_TODAY\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.today_in_parentheses", "(today)") + "\";\n");
		script.append("Calendar._TT[\"MON_FIRST\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.display_monday_first", "Display Monday first") + "\";\n");
		script.append("Calendar._TT[\"SUN_FIRST\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.display_sunday_first", "Display Sunday first") + "\";\n");
		script.append("Calendar._TT[\"CLOSE\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.close", "Close") + "\";\n");
		script.append("Calendar._TT[\"TODAY\"] = \"" + iwrb.getLocalizedString("jscal.tooltip.today", "Today") + "\";\n");
		return script.toString();
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public void keepStatusOnAction(boolean keepStatus) {
		this.keepStatus = keepStatus;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setUseJSCalendar(boolean useJSCalendar) {
		this.useJSCalendar = useJSCalendar;
	}

	protected boolean getUsePublicWindowOpener() {
		return true;
	}

	public String getTextInputId() {
		return textInputId;
	}

	public void setTextInputId(String textInputId) {
		this.textInputId = textInputId;
	}

	public String getTextInputOnchange() {
		return textInputOnchange;
	}

	public void setTextInputOnchange(String textInputOnchange) {
		this.textInputOnchange = textInputOnchange;
	}

	public void setShowEmpty(boolean showEmpty) {
		this.showEmpty = showEmpty;
	}
}