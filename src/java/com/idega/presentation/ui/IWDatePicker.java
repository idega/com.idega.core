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
import com.idega.block.web2.business.Web2Business;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
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

	@Autowired
	private Web2Business web2Business;

	private Date date = null;
	private Date dateTo = null;

	private Date maxDate = null;
	private Date minDate = null;

	private boolean dateRange = false;
	private String yearRange = null;
	private boolean useCurrentDateIfNotSet = true;
	private boolean showCalendarImage = true;

	private String onSelectAction = null;
	private String inputName = null;
	private String imageURI = null;

	private String dateRangeSeparator = " - ";
	private boolean showYearChange = false;
	private boolean showMonthChange = false;
	private boolean showTime;

	private boolean addJQueryUIFiles = true;

	private String alternateFieldId = null;

	private static final String INITIAL_DATE_PROPERTY = "initialDate";
	private static final String INITIAL_DATE_TO_PROPERTY = "initialDateTo";
	private static final String MAX_DATE_PROPERTY = "maxDate";
	private static final String MIN_DATE_PROPERTY = "minDate";
	private static final String INPUT_NAME_PROPERTY = "inputName";
	private static final String DATE_RANGE_SEPARATOR_PROPERTY = "dateRangeSeparator";
	private static final String SHOW_DATE_RANGE_PROPERTY = "showDateRange";
	private static final String SHOW_CALENDAR_IMAGE_PROPERTY = "showCalendarImage";
	private static final String SHOW_IMAGE_URI_PROPERTY = "imageURI";
	private static final String SHOW_MONTH_PROPERTY = "showMonth";
	private static final String SHOW_YEAR_PROPERTY = "showYear";
	private static final String SHOW_TIME_PROPERTY = "showTime";
	private static final String USE_CURRENT_PROPERTY = "useCurrent";
	private static final String YEAR_RANGE_PROPERTY = "yearRange";
	private static final String DATEPICKER_VERSION_PROPERTY = "version";
	private static final String ALTERNATE_FIELD_PROPERTY = "alternateFieldId";

	private String version;

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

		ve = getValueExpression(SHOW_IMAGE_URI_PROPERTY);
    	if (ve != null) {
	    	String imageURI = (String) ve.getValue(context.getELContext());
	    	setImageURI(imageURI);
    	}

		ve = getValueExpression(USE_CURRENT_PROPERTY);
    	if (ve != null) {
	    	boolean useCurrent = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setUseCurrentDateIfNotSet(useCurrent);
    	}

    	ve = getValueExpression(DATEPICKER_VERSION_PROPERTY);
    	if (ve != null) {
	    	String version = (String) ve.getValue(context.getELContext());
	    	if (!StringUtil.isEmpty(version)) {
	    		setVersion(version);
	    	}
    	}

    	ve = getValueExpression(ALTERNATE_FIELD_PROPERTY);
    	if (ve != null) {
	    	String version = (String) ve.getValue(context.getELContext());
	    	if (!StringUtil.isEmpty(version)) {
	    		setAlternateFieldId(version);
	    	}
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

//		if (date != null) {
//			iwDate = new IWTimestamp(date);
//
//			if (isDateRange() && setManualDate) {
//				iwDate.setDay(1);
//			}
//		}
//		if (iwDate != null) {
//			StringBuilder value = new StringBuilder(WebUtil.getLocalizedDate(iwDate, iwc.getCurrentLocale(), isShowTime()));
//
//			if (isDateRange()) {
//				iwDateTo = dateTo == null ? new IWTimestamp(System.currentTimeMillis()) : new IWTimestamp(dateTo);
//				value.append(dateRangeSeparator);
//				if (isShowTime())
//					value.append(iwDateTo.getLocaleDateAndTime(locale, IWTimestamp.SHORT, IWTimestamp.SHORT));
//				else
//					value.append(iwDateTo.getLocaleDate(locale, IWTimestamp.SHORT));
//			}
//
//			setValue(value.toString());
//		}

		boolean canUseLocalizedText = language != null && !CoreConstants.EMPTY.equals(language) && !Locale.ENGLISH.getLanguage().equals(language);

		String version = getVersion();
		if (StringUtil.isEmpty(version)) {
			version = Web2Business.JQUERY_UI_LATEST_VERSION;
		}
		String function =
//			"datepicker";
			"1.8.17".equals(version) ? isShowTime() ? "datetimepicker" : "datepicker" : "iwDatePicker";
//			isShowTime() ? "datetimepicker" : "datepicker";
		StringBuffer initAction = new StringBuffer("jQuery('#").append(this.getId()).append("').").append(function).append("({");

		initAction.append("iwdp:{id:'").append(getId()).append("',lang:'").append(locale.getLanguage()).append("'},");
		// Is date range?
		initAction.append("rangeSelect: ").append(isDateRange()).append(", rangeSeparator: '").append(dateRangeSeparator).append("', ");

		// Default date
		initAction.append("defaultDate: ").append(iwDate == null ? "null" : new StringBuilder("new Date(").append(iwDate.getYear()).append(", ")
				.append(iwDate.getMonth() - 1).append(", ").append(iwDate.getDay()).append(")").toString());

		// Show month/year select
		initAction.append(", changeMonth: ").append(isChangeMonth()).append(", changeYear: ").append(isChangeYear());

		if (getYearRange() != null) {
			initAction.append(", yearRange: ").append("'" + getYearRange() + "'");
		}

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
			String imageURI = getImageURI();
			if (imageURI == null) {
				imageURI = getBundle(iwc).getVirtualPathWithFileNameString("calendar.gif");
			}

			initAction.append(", showOn: 'button', buttonImage: '").append(imageURI).append("', buttonImageOnly: true");
		}

		if (isShowTime()) {
			initAction.append(", showTime: true");
		}

		if (!StringUtil.isEmpty(getAlternateFieldId())) {
			initAction.append(", altField: '" + getAlternateFieldId() + "'");
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
		if(date != null){
			StringBuilder dateSet = new StringBuilder("jQuery(document).ready(function(){");
			if (isDateRange()) {
				long t1 = date.getTime();
				Long t2;
				if(dateTo == null){
					dateTo = new Date();
					t2 = dateTo.getTime();
					t2 = t2 - (t2 % (1000 * 60));
				}else{
					t2 = dateTo.getTime();
				}
				dateSet.append("jQuery('#").append(this.getId()).append("').").append(function).append("('setDate',new Date(")
				.append(t1).append("));");
				dateSet.append("jQuery('#").append(this.getId()).append("').").append(function).append("('setDate',new Date(")
					.append(t2).append("));");
			}else{
				dateSet.append("jQuery('#").append(this.getId()).append("').").append(function).append("('setDate',new Date(")
					.append(date.getTime()).append("));");
			}
			dateSet.append("});");
			initAction.append(dateSet);
		}

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
		String version = getVersion();
		if (StringUtil.isEmpty(version)) {
			version = Web2Business.JQUERY_UI_LATEST_VERSION;
		}

		if (isAddJQueryUIFiles()) {
			scripts.add(jQuery.getBundleURIToJQueryLib());
			scripts.add(jQuery.getBundleURIToJQueryUILib(JQueryUIType.UI_CORE));
			if (StringUtil.isEmpty(getVersion())) {
				scripts.add(jQuery.getBundleURIToJQueryUILib(JQueryUIType.UI_DATEPICKER));
			} else {
				scripts.add(jQuery.getBundleURIToJQueryUILib(version, JQueryUIType.UI_DATEPICKER.getFileName()));
			}

			PresentationUtil.addStyleSheetToHeader(iwc, jQuery.getBundleURIToJQueryUILib(version + "/themes/base", "ui.core.css"));
			PresentationUtil.addStyleSheetToHeader(iwc, jQuery.getBundleURIToJQueryUILib(version + "/themes/base", "ui.theme.css"));
			PresentationUtil.addStyleSheetToHeader(iwc, jQuery.getBundleURIToJQueryUILib(version + "/themes/base", "ui.datepicker.css"));
		}
		if (language != null) {
			scripts.add(jQuery.getBundleURIToJQueryUILib("1.8.17/i18n", "ui.datepicker-" + language + ".js"));
		}
		if (isShowTime() || (isDateRange() && !version.equals("1.8.17"))) {
			Web2Business web2Business = getWeb2Business();
			scripts.addAll(web2Business
					.getBundleUrisToTimePickerScript(language == null ? null : iwc.getCurrentLocale()));
			PresentationUtil.addStyleSheetToHeader(iwc,web2Business.getBundleUriToTimePickerStyle());
		}
		IWBundle iwb = CoreUtil.getCoreBundle();
		if (!"1.8.17".equals(version)) {
			scripts.add(iwb.getVirtualPathWithFileNameString("javascript/datepicker.js"));
		}
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/datepicker.css"));
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, scripts);
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

	public String getImageURI() {
		return imageURI;
	}

	public void setImageURI(String imageURI) {
		this.imageURI = imageURI;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isAddJQueryUIFiles() {
		return addJQueryUIFiles;
	}

	public void setAddJQueryUIFiles(boolean addJQueryUIFiles) {
		this.addJQueryUIFiles = addJQueryUIFiles;
	}

	public Web2Business getWeb2Business() {
		if (web2Business == null) {
			ELUtil.getInstance().autowire(this);
		}
		return web2Business;
	}

	public String getAlternateFieldId() {
		return alternateFieldId;
	}

	public void setAlternateFieldId(String alternateFieldId) {
		this.alternateFieldId = alternateFieldId;
	}

}