package com.idega.presentation.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.idega.block.web2.business.Web2Business;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.PresentationUtil;
import com.idega.util.expression.ELUtil;

/**
 * @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
 * @version $Revision: 1.9 $
 *
 * Date (range) picker
 *
 * Last modified: $Date: 2009/01/29 07:36:55 $ by $Author: valdas $
 */
public class IWDatePicker extends TextInput {
	
	private Date date = null;
	private Date dateTo = null;
	
	private boolean dateRange = false;
	private boolean useCurrentDateIfNotSet = true;
	private boolean showCalendarImage = true;
	
	private String onSelectAction = null;
	private String inputName = null;
	
	private String dateRangeSeparator = " - ";

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
		if (date == null || useCurrentDateIfNotSet) {
			date = new Date(System.currentTimeMillis());
		}
		else {
			useCurrentDateIfNotSet = false;
		}
		if (date != null) {
			iwDate = new IWTimestamp(date);
			
			if (isDateRange() && useCurrentDateIfNotSet) {
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
		
		boolean canUseLocalizedText = language != null  && !CoreConstants.EMPTY.equals(language) && !Locale.ENGLISH.getLanguage().equals(language);
		StringBuffer initAction = new StringBuffer("jQuery('#").append(this.getId()).append("').datepicker({");
			//	Is date range?
			initAction.append("rangeSelect: ").append(isDateRange()).append(", rangeSeparator: '").append(dateRangeSeparator).append("', ");
			
			//	Custom date
			if (iwDate != null) {
				initAction.append("defaultDate: new Date(").append(iwDate.getYear()).append(", ").append(iwDate.getMonth() - 1).append(", ")
							.append(iwDate.getDay()).append(")");
			}
			
			//	Calendar image
			if (isShowCalendarImage()) {
				initAction.append(", showOn: 'button', buttonImage: '").append(getBundle(iwc).getVirtualPathWithFileNameString("calendar.gif"))
							.append("', buttonImageOnly: true");
			}
			
			//	onSelect action
			if (onSelectAction != null) {
				initAction.append(", onSelect: function() {").append(onSelectAction).append("}");
			}
			
			initAction.append(", buttonText: '").append(getResourceBundle(iwc).getLocalizedString("select_date", "Select date")).append("'");
			
			//	Localization
			if (canUseLocalizedText) {
				initAction.append(", regional: ['").append(language).append("']");
			}
	
		initAction.append("});");
		
		//	Initialization action
		if (!CoreUtil.isSingleComponentRenderingProcess(iwc)) {
			initAction = new StringBuffer("jQuery(window).load(function() {").append(initAction.toString()).append("});");
		}
		PresentationUtil.addJavaScriptActionToBody(iwc, initAction.toString());
		
		//	Resources
		addRequiredLibraries(iwc, canUseLocalizedText ? language : null);
	}
	
	private void addRequiredLibraries(IWContext iwc, String language) {
		List<String> scripts = new ArrayList<String>();
		
		Web2Business web2 = ELUtil.getInstance().getBean(Web2Business.SPRING_BEAN_IDENTIFIER);
		scripts.add(web2.getBundleURIToJQueryLib());
		scripts.add(web2.getBundleURIToJQueryUILib("1.6rc5", "ui.datepicker.js"));

		if (language != null) {
			scripts.add(web2.getBundleURIToJQueryUILib("1.6rc5/datepicker/i18n", "ui.datepicker-" + language + ".js"));
		}
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, scripts);
		
		PresentationUtil.addStyleSheetToHeader(iwc, web2.getBundleURIToJQueryUILib("1.6rc5/themes/base", "ui.all.css"));
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
	
}
