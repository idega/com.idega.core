package com.idega.presentation.ui.handlers;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import com.idega.util.IWTimestamp;
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

	public static final DateFormat 	DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd"),
									DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private static final List<DateFormat> DEFAULT_FORMATTERS = Arrays.asList(DATE_TIME_FORMATTER, new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss"), DATE_FORMATTER, new SimpleDateFormat("MM/dd/yyyy"));

	private String method = null;
	private String instanceId = null;

	@Override
	public List<?> getDefaultHandlerTypes() {
		return null;
	}

	@Override
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc, boolean oldGenerationHandler, String instanceId, String method) {
		this.instanceId = instanceId;
		this.method = method;

		Layer layer = new Layer();
		IWDatePicker datePicker = new IWDatePicker();
		datePicker.setVersion(IWDatePicker.VERSION_1_8_17);
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

		for (DateFormat formatter: DEFAULT_FORMATTERS) {
			try {
				Date date = formatter.parse(source);
				if (date != null) {
					return date;
				}
			} catch (ParseException e) {}
		}

		return null;
	}

	public static final Date getParsedDateByCurrentLocale(String source) {
		return getParsedDateByCurrentLocale(source, null);
	}

	private static final Date getParsedDateByCurrentLocale(String source, Locale locale) {
		if (StringUtil.isEmpty(source)) {
			return null;
		}

		if (source.endsWith(".0")) {
			source = source.substring(0, source.lastIndexOf(".0"));
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

		IWTimestamp date = null;
		try {
			date = new IWTimestamp(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale).parse(source));
		} catch(Exception e) {}
		if (date == null) {
			try {
				date = new IWTimestamp(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(source));
			} catch (Exception e) {}
		}
		if (date == null) {
			try {
				date = new IWTimestamp(DATE_TIME_FORMATTER.parse(source));
			} catch (Exception e) {}
		}
		if (date == null) {
			try {
				date = new IWTimestamp(DateFormat.getDateInstance(DateFormat.SHORT, locale).parse(source));
			} catch(Exception e) {}
		}
		if (date == null) {
			try {
				date = new IWTimestamp(DateFormat.getDateInstance(DateFormat.SHORT).parse(source));
			} catch (Exception e) {}
		}
		if (date == null) {
			try {
				date = new IWTimestamp(DATE_FORMATTER.parse(source));
			} catch (Exception e) {
				Logger.getLogger(IWDatePickerHandler.class.getName()).log(Level.WARNING, "Error converting string to date: " + source + " by locale: " +
						locale.toString());
			}
		}
		return date == null ? null : date.getDate();
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

	public static final Date getParsedDateByFormat(String source, String format) {
		if (StringUtil.isEmpty(source) || StringUtil.isEmpty(format)) {
			return null;
		}
		DateFormat formatter = new SimpleDateFormat(format);
		try {
			return formatter.parse(source);
		} catch (ParseException e) {
			return null;
		}
	}

	public static final Date getParsedDate(String source, Locale locale) {
		Date date = getParsedDateByCurrentLocale(source, locale);
		return date == null ? getParsedDateByDefaultPattern(source) : date;
	}

	@Override
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
