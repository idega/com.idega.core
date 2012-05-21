package com.idega.presentation.ui.handlers;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.IWDatePicker;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.StringUtil;

/**
 * @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
 * @version $Revision: 1.4 $
 *
 * Handler for date (range) picker: converts date to storable format and user friendly format.
 *
 * Last modified: $Date: 2009/01/29 07:37:19 $ by $Author: valdas $
 */
public class IWDatePickerHandler implements ICPropertyHandler {
	
	private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd"),
									TIME_FORMATTER = new SimpleDateFormat("HH:mm");
	
	private String method = null;
	private String instanceId = null;
	
	public List<?> getDefaultHandlerTypes() {
		return null;
	}

	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc, boolean oldGenerationHandler, String instanceId, String method) {
		this.instanceId = instanceId;
		this.method = method;
		
		Layer layer = new Layer();
		IWDatePicker datePicker = new IWDatePicker();
		layer.add(datePicker);
		
		Date date = getParsedDate(stringValue);
		if (date != null) {
			datePicker.setDate(date);
		}
		
		datePicker.setShowCalendarImage(true);
		datePicker.setOnSelectAction("saveModuleProperty(null, jQuery('#" + datePicker.getId() + "'));");
		
		return layer;
	}

	private static final Date getParsedDateByDefaultPattern(String source) {
		if (source == null || CoreConstants.EMPTY.equals(source)) {
			return null;
		}
		
		try {
			return DATE_FORMATTER.parse(source);
		} catch (ParseException e) {
		}
		
		return null;
	}
	
	public static final Date getParsedDateByCurrentLocale(String source) {
		return getParsedDateByCurrentLocale(source, null);
	}
	
	@SuppressWarnings("deprecation")
	private static final Date getParsedDateByCurrentLocale(String source, Locale locale) {
		if (StringUtil.isEmpty(source)) {
			return null;
		}
		
		if (locale == null) {
			IWContext iwc = CoreUtil.getIWContext();
			if (iwc != null) {
				locale = iwc.getCurrentLocale();
			}
			if (locale == null) {
				locale = Locale.ENGLISH;
			}
		}
		
		Date date = null;
		try {
			date = DateFormat.getDateInstance(DateFormat.SHORT, locale).parse(source);
		} catch(Exception e) {
			Logger.getLogger(IWDatePickerHandler.class.getName()).log(Level.WARNING, "Error converting string to date: " + source + " by locale: " +
					locale.toString());
			return null;
		}
		
		String[] dateAndTimeSource = source.split(CoreConstants.SPACE);
		if (dateAndTimeSource.length == 2) {
			try {
				Date time = TIME_FORMATTER.parse(dateAndTimeSource[1]);
				
				date.setHours(time.getHours());
				date.setMinutes(time.getMinutes());
			} catch (ParseException e) {
				Logger.getLogger(IWDatePickerHandler.class.getName()).log(Level.WARNING, "Error converting string to time: " + dateAndTimeSource[1] +
						" by locale: " + locale.toString());
			}
		}
		
		return date;
	}
	
	public static final Timestamp getParsedTimestampByCurrentLocale(String source) {
		Date date = getParsedDate(source);
		if (date == null) {
			return null;
		}
		return new Timestamp(date.getTime());
	}
	
	public static final Date getParsedDate(String source) {
		return getParsedDate(source, null);
	}
	
	public static final Date getParsedDate(String source, Locale locale) {
		Date date = getParsedDateByCurrentLocale(source, locale);
		return date == null ? getParsedDateByDefaultPattern(source) : date;	
	}
	
	public void onUpdate(String[] values, IWContext iwc) {
		if (values == null || values.length == 0) {
			return;
		}
		
		Date selectedDate = getParsedDateByCurrentLocale(values[0], iwc.getCurrentLocale());
		if (selectedDate == null) {
			return;
		}
		
		values = new String[] {DATE_FORMATTER.format(selectedDate)};
		
		BuilderService builderService = null;
		try {
			builderService = BuilderServiceFactory.getBuilderService(iwc);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (builderService == null) {
			return;
		}
		builderService.setProperty(String.valueOf(iwc.getCurrentIBPageID()), instanceId, method, values, iwc.getIWMainApplication());
	}

}
