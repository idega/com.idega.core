package com.idega.presentation.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.block.web2.business.JQuery;
import com.idega.block.web2.business.JQueryUIType;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.PresentationUtil;
import com.idega.util.expression.ELUtil;

/**
 * @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
 * @version $Revision: 1.14 $
 * 
 *          Date (range) picker
 * 
 *          Last modified: $Date: 2009/03/17 13:14:59 $ by $Author: laddi $
 */
public class IWDatePicker extends TextInput {

	@Autowired
	private JQuery jQuery;
	
	private Date date = null;
	private Date dateTo = null;

	private Date maxDate = null;
	private Date minDate = null;
	
	private boolean dateRange = false;
	private String yearRange = "c-10:c+10";
	private boolean useCurrentDateIfNotSet = true;
	private boolean showCalendarImage = true;

	private String onSelectAction = null;
	private String inputName = null;

	private String dateRangeSeparator = " - ";
	private boolean showYearChange = false;
	private boolean showMonthChange = false;
	
	private boolean showTime;
	
	private static final String INITIAL_DATE_PROPERTY = "initialDate";
	private static final String INITIAL_DATE_TO_PROPERTY = "initialDateTo";
	private static final String MAX_DATE_PROPERTY = "maxDate";
	private static final String MIN_DATE_PROPERTY = "minDate";
	private static final String INPUT_NAME_PROPERTY = "inputName";
	private static final String DATE_RANGE_SEPARATOR_PROPERTY = "dateRangeSeparator";
	private static final String SHOW_DATE_RANGE_PROPERTY = "showDateRange";
	private static final String SHOW_CALENDAR_IMAGE_PROPERTY = "showCalendarImage";
	private static final String SHOW_MONTH_PROPERTY = "showMonth";
	private static final String SHOW_YEAR_PROPERTY = "showYear";
	private static final String SHOW_TIME_PROPERTY = "showTime";
	private static final String USE_CURRENT_PROPERTY = "useCurrent";
	private static final String YEAR_RANGE_PROPERTY = "yearRange";

	/**
	 * Constructs a new <code>TextInput</code> with the name "untitled".
	 */
	public IWDatePicker() {
		this("untitled");
	}

	/**
	 * Constructs a new <code>TextInput</code> with the given name.
	 */
	public IWDatePicker(String name) {
		super();
		setInputName(name);
	}

    @Override
	public void encodeBegin(FacesContext context) throws IOException { 
    	ValueExpression ve = getValueExpression(INITIAL_DATE_PROPERTY);
    	if (ve != null) {
	    	Date date = (Date) ve.getValue(context.getELContext());
	    	setDate(date);
    	}

    	ve = getValueExpression(INITIAL_DATE_TO_PROPERTY);
    	if (ve != null) {
	    	Date date = (Date) ve.getValue(context.getELContext());
	    	setDateTo(date);
    	}

    	ve = getValueExpression(MAX_DATE_PROPERTY);
    	if (ve != null) {
	    	Date date = (Date) ve.getValue(context.getELContext());
	    	setMaxDate(date);
    	}

    	ve = getValueExpression(MIN_DATE_PROPERTY);
    	if (ve != null) {
	    	Date date = (Date) ve.getValue(context.getELContext());
	    	setMinDate(date);
    	}

		ve = getValueExpression(INPUT_NAME_PROPERTY);
    	if (ve != null) {
	    	String name = (String) ve.getValue(context.getELContext());
	    	setInputName(name);
    	}    
    	
		ve = getValueExpression(YEAR_RANGE_PROPERTY);
    	if (ve != null) {
	    	String yearRange = (String) ve.getValue(context.getELContext());
	    	setYearRange(yearRange);
    	}    
    	
		ve = getValueExpression(DATE_RANGE_SEPARATOR_PROPERTY);
    	if (ve != null) {
	    	String dateRangeSeperator = (String) ve.getValue(context.getELContext());
	    	setDateRangeSeparator(dateRangeSeperator);
    	}    
    	
		ve = getValueExpression(SHOW_DATE_RANGE_PROPERTY);
    	if (ve != null) {
	    	boolean showDateRange = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setDateRange(showDateRange);
    	}
    	
		ve = getValueExpression(SHOW_MONTH_PROPERTY);
    	if (ve != null) {
	    	boolean showMonth = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setShowMonthChange(showMonth);
    	}
    	
		ve = getValueExpression(SHOW_YEAR_PROPERTY);
    	if (ve != null) {
	    	boolean showYear = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setShowYearChange(showYear);
    	}
    	
		ve = getValueExpression(SHOW_TIME_PROPERTY);
    	if (ve != null) {
	    	boolean showTime = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setShowTime(showTime);
    	}
    	
		ve = getValueExpression(SHOW_CALENDAR_IMAGE_PROPERTY);
    	if (ve != null) {
	    	boolean showCalendarImage = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setShowCalendarImage(showCalendarImage);
    	}
    	
		ve = getValueExpression(USE_CURRENT_PROPERTY);
    	if (ve != null) {
	    	boolean useCurrent = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setUseCurrentDateIfNotSet(useCurrent);
    	}
    	
    	super.encodeBegin(context);
    }
    
	@Override
	public void main(IWContext iwc) {
		if (inputName == null) {
			inputName = this.getId();
		}
		setName(inputName);

		Locale locale = iwc.getCurrentLocale();
		if (locale == null) {
			locale = Locale.ENGLISH;
		}

		String language = locale.getLanguage();

		IWTimestamp iwDate = null;
		IWTimestamp iwDateTo = null;

		boolean setManualDate = false;
		if (date == null && useCurrentDateIfNotSet) {
			date = new Date(System.currentTimeMillis());
			setManualDate = true;
		}

		if (date != null) {
			iwDate = new IWTimestamp(date);

			if (isDateRange() && setManualDate) {
				iwDate.setDay(1);
			}
		}
		if (iwDate != null) {
			StringBuilder value = new StringBuilder(iwDate.getLocaleDate(locale, IWTimestamp.SHORT));

			if (isDateRange()) {
				iwDateTo = dateTo == null ? new IWTimestamp(System.currentTimeMillis()) : new IWTimestamp(dateTo);
				value.append(dateRangeSeparator).append(iwDateTo.getLocaleDate(locale, IWTimestamp.SHORT));
			}

			setValue(value.toString());
		}

		boolean canUseLocalizedText = language != null && !CoreConstants.EMPTY.equals(language) && !Locale.ENGLISH.getLanguage().equals(language);
		StringBuffer initAction = new StringBuffer("jQuery('#").append(this.getId()).append("').datepicker({");
		// Is date range?
		initAction.append("rangeSelect: ").append(isDateRange()).append(", rangeSeparator: '").append(dateRangeSeparator).append("', ");

		// Default date
		initAction.append("defaultDate: ").append(iwDate == null ? "null" : new StringBuilder("new Date(").append(iwDate.getYear()).append(", ")
				.append(iwDate.getMonth() - 1).append(", ").append(iwDate.getDay()).append(")").toString());

		// Show month/year select
		initAction.append(", changeMonth: ").append(isChangeMonth()).append(", changeYear: ").append(isChangeYear()).append(", yearRange: ").append("'" + getYearRange() + "'");

		// Max date
		if (getMaxDate() != null) {
			IWTimestamp maxDate = new IWTimestamp(getMaxDate());
			initAction.append(", maxDate: ").append(new StringBuilder("new Date(").append(maxDate.getYear()).append(", ").append(maxDate.getMonth() - 1).append(", ")
					.append(maxDate.getDay()).append(")").toString());
		}
		
		// Min date
		if (getMinDate() != null) {
			IWTimestamp maxDate = new IWTimestamp(getMinDate());
			initAction.append(", minDate: ").append(new StringBuilder("new Date(").append(maxDate.getYear()).append(", ").append(maxDate.getMonth() - 1).append(", ")
					.append(maxDate.getDay()).append(")").toString());
		}
		
		// Calendar image
		if (isShowCalendarImage()) {
			initAction.append(", showOn: 'button', buttonImage: '").append(getBundle(iwc).getVirtualPathWithFileNameString("calendar.gif")).append("', buttonImageOnly: true");
		}

		if (isShowTime()) {
			initAction.append(", showTime: true");
		}
				
		// onSelect action
		if (onSelectAction != null) {
			initAction.append(", onSelect: function() {").append(onSelectAction).append("}");
		}

		initAction.append(", buttonText: '").append(getResourceBundle(iwc).getLocalizedString("select_date", "Select date")).append("'");

		// Localization
		if (canUseLocalizedText) {
			initAction.append(", regional: ['").append(language).append("']");
		}
		
		initAction.append("});");

		// Initialization action
		if (!CoreUtil.isSingleComponentRenderingProcess(iwc)) {
			initAction = new StringBuffer("jQuery(window).load(function() {").append(initAction.toString()).append("});");
		}
		PresentationUtil.addJavaScriptActionToBody(iwc, initAction.toString());

		// Resources
		addRequiredLibraries(iwc, canUseLocalizedText ? language : null);
	}

	private void addRequiredLibraries(IWContext iwc, String language) {
		List<String> scripts = new ArrayList<String>();

		JQuery jQuery = getJQuery();
		scripts.add(jQuery.getBundleURIToJQueryLib());
		scripts.add(jQuery.getBundleURIToJQueryUILib(JQueryUIType.UI_CORE));
		scripts.add(jQuery.getBundleURIToJQueryUILib(JQueryUIType.UI_DATEPICKER));

		if (language != null) {
			scripts.add(jQuery.getBundleURIToJQueryUILib("1.8.17/i18n", "ui.datepicker-" + language + ".js"));
		}
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, scripts);

		PresentationUtil.addStyleSheetToHeader(iwc, jQuery.getBundleURIToJQueryUILib("1.8.17/themes/base", "ui.core.css"));
		PresentationUtil.addStyleSheetToHeader(iwc, jQuery.getBundleURIToJQueryUILib("1.8.17/themes/base", "ui.theme.css"));
		PresentationUtil.addStyleSheetToHeader(iwc, jQuery.getBundleURIToJQueryUILib("1.8.17/themes/base", "ui.datepicker.css"));
	}

	@Override
	public String getBundleIdentifier() {
		return CoreConstants.CORE_IW_BUNDLE_IDENTIFIER;
	}

	public boolean isDateRange() {
		return dateRange;
	}

	public void setDateRange(boolean dateRange) {
		this.dateRange = dateRange;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isUseCurrentDateIfNotSet() {
		return useCurrentDateIfNotSet;
	}

	public void setUseCurrentDateIfNotSet(boolean useCurrentDateIfNotSet) {
		this.useCurrentDateIfNotSet = useCurrentDateIfNotSet;
	}

	public boolean isShowCalendarImage() {
		return showCalendarImage;
	}

	public void setShowCalendarImage(boolean showCalendarImage) {
		this.showCalendarImage = showCalendarImage;
	}

	private boolean isChangeYear() {
		return this.showYearChange;
	}

	public void setShowYearChange(boolean showYearChange) {
		this.showYearChange = showYearChange;
	}

	public String getYearRange() {
		return yearRange;
	}

	public void setYearRange(String yearRange) {
		this.yearRange = yearRange;
	}

	private boolean isChangeMonth() {
		return this.showMonthChange;
	}

	public void setShowMonthChange(boolean showMonthChange) {
		this.showMonthChange = showMonthChange;
	}

	public String getOnSelectAction() {
		return onSelectAction;
	}

	public void setOnSelectAction(String onSelectAction) {
		this.onSelectAction = onSelectAction;
	}

	public String getInputName() {
		return inputName;
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public String getDateRangeSeparator() {
		return dateRangeSeparator;
	}

	public void setDateRangeSeparator(String dateRangeSeparator) {
		this.dateRangeSeparator = dateRangeSeparator;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}
	
	JQuery getJQuery() {
		if (jQuery == null) {
			ELUtil.getInstance().autowire(this);
		}
		return jQuery;
	}

	public boolean isShowTime() {
		return showTime;
	}

	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}
}