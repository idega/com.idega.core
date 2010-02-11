/*
 * Created on 22.1.2004 by  tryggvil in project com.project
 */
package com.idega.idegaweb.presentation;

/**
 * CalendarParameters //TODO: tryggvil Describe class
 * Copyright (C) idega software 2004
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class CalendarParameters {

	public static final int DAY = 1;
	public static final int MONTH = 2;
	public static final int YEAR = 3;
	public static final int AHEAD_VIEW = 4;
	public static final int WEEK = 5;
	public static final String PARAMETER_CALENDAR = "IWCalendar";
	public static final String PARAMETER_DAY = PARAMETER_CALENDAR + "_day";
	public static final String PARAMETER_MONTH = PARAMETER_CALENDAR + "_month";
	public static final String PARAMETER_YEAR = PARAMETER_CALENDAR + "_year";
	public static final String PARAMETER_DATE = PARAMETER_CALENDAR + "_date";
	public static final String PARAMETER_DAY_VIEW = Integer.toString(DAY);
	public static final String PARAMETER_MONTH_VIEW = Integer.toString(MONTH);
	public static final String PARAMETER_YEAR_VIEW = Integer.toString(YEAR);
	public static final String PARAMETER_CALENDAR_ID = PARAMETER_CALENDAR + "_ID";
	public static final String PARAMETER_ENTRY_ID = PARAMETER_CALENDAR + "_entryID";
	public static final String PARAMETER_TYPE_ID = PARAMETER_CALENDAR + "_typeID";
	public static final String PARAMETER_FILE_ID = "ic_file_id";
	public static final String PARAMETER_LOCALE_DROP = "locale_drop";
	public static final String PARAMETER_ENTRY_HEADLINE = "entry_headline";
	public static final String PARAMETER_ENTRY_BODY = "entry_body";
	public static final String PARAMETER_ENTRY_DATE = "entry_date";
	public static final String PARAMETER_ENTRY_END_DATE = "entry_end_date";
	public static final String PARAMETER_ENTRY_TIME = "entry_time";
	public static final String PARAMETER_IC_CAT = "ic_cat_id";
	public static final String PARAMETER_INSTANCE_ID = "ic_instance_id";
	public static final String PARAMETER_MODE = PARAMETER_CALENDAR + "_mode";
	public static final String PARAMETER_MODE_DELETE = PARAMETER_CALENDAR + "_delete";
	public static final String PARAMETER_MODE_EDIT = "edit";
	public static final String PARAMETER_MODE_CLOSE = "close";
	public static final String PARAMETER_MODE_SAVE = "save";
	public static final String PARAMETER_TRUE = "true";
	public static final String PARAMETER_FALSE = "false";
	public static final String PARAMETER_SHOW_CALENDAR = "show_calendar";
	public static final String PARAMETER_VIEW = PARAMETER_CALENDAR + "_view";

	public static final String PARAMETER_ALL_DAY_EVENT = "all_day_event";
	public static final String PARAMETER_REPEAT_EVERY_YEAR = "repeat_every_year";
}
