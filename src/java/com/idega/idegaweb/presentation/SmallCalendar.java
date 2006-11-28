package com.idega.idegaweb.presentation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.idega.core.builder.data.ICPage;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.util.IWCalendar;
import com.idega.util.IWTimestamp;
import com.idega.util.text.TextSoap;

public class SmallCalendar extends Block {

	private IWTimestamp today;
	private IWTimestamp stamp;
	private IWCalendar cal;
	private ICPage _page;
	private Class _class;
	private Image iNextImage;
	private Image iPreviousImage;

	private boolean useNextAndPreviousLinks = true;
	private boolean daysAreLinks = false;
	private boolean showNameOfDays = true;
	private boolean _highlight = false;
	private boolean LINE_VIEW = false;

	private String textColor = "#000000";
	private String highlightedText = "#660000";
	private String headerTextColor = "#000000";
	private String dayTextColor = this.headerTextColor;
	private String headerColor = null;
	private String dayCellColor = null;
	private String bodyColor = null;
	private String inactiveCellColor = null;
	private String backgroundColor = null;
	private String todayColor = this.headerColor;
	private String selectedColor = "#CCCCCC";
	private String textStyleClass;
	private String inactiveTextStyleClass;
	private String headerTextStyleClass;
	private String dayCellStyle;
	private String linkStyle;
	private String inactiveBackgroundCellStyleClass;
	private String backgroundStyleClass;
	private String todayBackgroundStyleClass;
	private String selectedBackgroundStyleClass;
	private String width = "110";
	private String height = "60";
	private Link _link;
	private String _target;

	private Vector parameterName = new Vector();
	private Vector parameterValue = new Vector();

	private Hashtable dayColors = null;
	private Hashtable dayFontColors = null;
	private Hashtable dayStyleClass = null;
	private Hashtable dayBackgroundStyleClass = null;
	
	private boolean useTarget = false;
	private String onClickMessageFormat = null;
	private int displayFormat = DateFormat.MEDIUM;
	private String dateParameterName = null;
	
	public static final String PRM_SETTINGS = "settings";

	public Table T;
	
	private int iCellpadding = 2;
	
	public SmallCalendar() {
		initialize();
	}

	public SmallCalendar(IWTimestamp timestamp) {
		initialize();
		this.stamp = timestamp;
	}

	public SmallCalendar(int year, int month) {
		initialize();
		this.stamp = new IWTimestamp();
		this.stamp.setMonth(month);
		this.stamp.setYear(year);
	}
	
	public void main(IWContext iwc) {
		this.cal = new IWCalendar(iwc.getCurrentLocale());
		
		if (this.stamp == null) {
			String day = iwc.getParameter(CalendarParameters.PARAMETER_DAY);
			String month = iwc.getParameter(CalendarParameters.PARAMETER_MONTH);
			String year = iwc.getParameter(CalendarParameters.PARAMETER_YEAR);
			if (isTarget() || !this.useTarget) {
				this.stamp = getTimestamp(day, month, year);
			}
			else if (iwc.getSessionAttribute("smcal" + getICObjectInstanceID()) != null) {
				this.stamp = (IWTimestamp) iwc.getSessionAttribute("smcal" + getICObjectInstanceID());
			}
			else {
				this.stamp = IWTimestamp.RightNow();
			}
		}
		if(iwc.isParameterSet(PRM_SETTINGS)){
			setInitializingString(iwc.getParameter(PRM_SETTINGS));
		}
		make(iwc);
	}

	public void make(IWContext iwc) {
		int thismonth = this.today.getMonth();
		int stampmonth = this.stamp.getMonth();

		boolean shadow = (thismonth == stampmonth) ? true : false;
		if (shadow) {
			shadow = (this.today.getYear() == this.stamp.getYear()) ? true : false;
		}
		int daycount = this.cal.getLengthOfMonth(this.stamp.getMonth(), this.stamp.getYear());
		int daynr = this.cal.getDayOfWeek(this.stamp.getYear(), this.stamp.getMonth(), 1);
		String sMonth = this.cal.getMonthName(this.stamp.getMonth(), iwc.getCurrentLocale(), IWCalendar.SHORT);
		try {
			sMonth = sMonth.substring(0, 1).toUpperCase() + sMonth.substring(1);
		}
		catch (Exception e) {
			sMonth = this.cal.getMonthName(this.stamp.getMonth(), iwc.getCurrentLocale(), IWCalendar.SHORT);
		}
		String sYear = String.valueOf(this.stamp.getYear());
		Text tMonth = getHeaderText(sMonth + " " + sYear);
		Link right = null;
		if (this.iNextImage != null) {
			right = new Link(this.iNextImage);
		}
		else {
			right = new Link(getLinkText("&gt;"));
		}

		for (int i = 0; i < this.parameterName.size(); i++) {
			right.addParameter((String) this.parameterName.get(i), (String) this.parameterValue.get(i));
		}

		this.addNextMonthPrm(right, this.stamp);
		right.setTargetObjectInstance(this.getTargetObjectInstance());

		Link left = null;
		if (this.iPreviousImage != null) {
			left = new Link(this.iPreviousImage);
		}
		else {
			left = new Link(getLinkText("&lt;"));
		}
		for (int i = 0; i < this.parameterName.size(); i++) {
			left.addParameter((String) this.parameterName.get(i), (String) this.parameterValue.get(i));
		}

		this.addLastMonthPrm(left, this.stamp);
		setAsObjectInstanceTarget(left);

		Table T2 = new Table(3, 2);
		this.T.setCellpadding(this.iCellpadding);
		T2.setCellpadding(0);
		T2.mergeCells(1, 2, 3, 2);
		T2.setCellspacing(0);
		T2.setWidth(this.width);
		this.T.setWidth(this.width);
		T2.setHeight(this.height);
		this.T.setHeight(this.height);
		T2.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_CENTER);
		T2.setAlignment(2, 1, Table.HORIZONTAL_ALIGN_CENTER);
		T2.setAlignment(3, 1, Table.HORIZONTAL_ALIGN_CENTER);
		
		if (this.backgroundStyleClass != null) {
			T2.setStyleClass(getStyleName(this.backgroundStyleClass));
		} else if (this.backgroundColor != null) {
			T2.setColor(this.backgroundColor);
		}

		//T2.setAlignment(1, "center");
		//T2.setColumnVerticalAlignment(1, "middle");

		//T.setColor(inactiveCellColor);

		if (this.useNextAndPreviousLinks) {
			T2.add(left, 1, 1);
		}
		T2.add(tMonth, 2, 1);
		if (this.useNextAndPreviousLinks) {
			T2.add(right, 3, 1);
		}

		Text t;
		if (this.showNameOfDays) {
			int weekday = this.cal.getCalendar().getFirstDayOfWeek();
			int weekdays = (this.LINE_VIEW ? daycount + daynr : weekday + 7);
			int a = 1;
			int b = weekday;
			while(b < weekdays) {
				if (weekday > 7) {
					weekday = weekday - 7;
				}
				t = getHeaderText(this.cal.getDayName(weekday++, iwc.getCurrentLocale(), IWCalendar.LONG).substring(0, 1).toUpperCase());
				this.T.setAlignment(a, 1, "center");
				this.T.add(t, a++, 1);
				b++;
			}
			if (this.dayCellStyle != null) {
				this.T.setRowStyleClass(1, this.dayCellStyle);
			} else 	if (this.dayCellColor != null) {
				this.T.setRowColor(1, this.dayCellColor);
			}
		}

		int n = 1;
		int xpos = daynr;
		int ypos = 1;
		if (this.showNameOfDays) {
			ypos++;
		}

		int month = this.stamp.getMonth();
		int year = this.stamp.getYear();

		if (this.dayBackgroundStyleClass != null) {
			Iterator iter = this.dayBackgroundStyleClass.keySet().iterator();
			while (iter.hasNext()) {
				String dayString = (String) iter.next();
				if (inThisMonth(dayString, year, month)) {
					IWTimestamp newStamp = new IWTimestamp(dayString);
					int[] XY = getXYPos(newStamp.getYear(), newStamp.getMonth(), newStamp.getDay());
					this.T.setStyleClass(XY[0], XY[1], getDayBackgroundStyleClass(dayString));
				}
			}
		} else if (this.dayColors != null) {
			Enumeration enumer = this.dayColors.keys();
			while (enumer.hasMoreElements()) {
				String dayString = (String) enumer.nextElement();
				if (inThisMonth(dayString, year, month)) {
					IWTimestamp newStamp = new IWTimestamp(dayString);
					int[] XY = getXYPos(newStamp.getYear(), newStamp.getMonth(), newStamp.getDay());
					this.T.setColor(XY[0], XY[1], getDayColor(dayString));
				}
			}
		}

		Link theLink;
		String dayColor = null;
		DateFormat df = DateFormat.getDateInstance(this.displayFormat,iwc.getCurrentLocale());
		SimpleDateFormat dateValueFormat = new SimpleDateFormat("yyyy-MM-dd");

		while (n <= daycount) {
			xpos = getXYPos(year, month, n)[0];
			ypos = getXYPos(year, month, n)[1];
			t = new Text(String.valueOf(n));
			dayColor = this.textColor;
			String styleClass = getDayStyleClass(getDateString(year, month, n));
			if (styleClass != null) {
				t.setStyleClass(styleClass);
			} else if (getDayFontColor(getDateString(year, month, n)) != null) {
				dayColor = getDayFontColor(getDateString(year, month, n));
				t.setFontStyle("font-family: Verdana,Arial, Helvetica, sans-serif; color: " + dayColor + "; font-size: 10px; font-weight: bold; text-decoration: none;");
			}
			else {
				if (this.today.getYear() == year && this.today.getMonth() == month && this.today.getDay() == n) {
					dayColor = this.dayTextColor;
					t.setFontStyle("font-family: Verdana,Arial, Helvetica, sans-serif; color: " + dayColor + "; font-size: 10px; font-weight: bold; text-decoration: none;");
				}
				else {
					t.setFontStyle("font-family: Arial, Helvetica, sans-serif; color: " + dayColor + "; font-size: 10px; text-decoration: none;");
				}
			}
			this.T.setAlignment(xpos, ypos, "center");

			if (this.todayBackgroundStyleClass != null && ((n == this.today.getDay()) && shadow)) {
				this.T.setStyleClass(xpos, ypos, this.todayBackgroundStyleClass);
			} else if ((this.todayColor != null) && ((n == this.today.getDay()) && shadow)) {
				this.T.setColor(xpos, ypos, this.todayColor);
			}

			if (this._highlight) {
				if (n == this.stamp.getDay() && month == this.stamp.getMonth() && year == this.stamp.getYear()) {
					if (this.selectedBackgroundStyleClass != null) {
						this.T.setStyleClass(xpos, ypos, this.selectedBackgroundStyleClass);
					} else {
						this.T.setColor(xpos, ypos, this.selectedColor);
					}
				}
			}

			if (this.daysAreLinks) {
				theLink = getLink();
				if (styleClass != null) {
					theLink = new Link(String.valueOf(n));
					theLink.setStyleClass(styleClass);
				}
				else {
					theLink.setPresentationObject(t);
				}
				if (this._page != null) {
					theLink.setPage(this._page);
				}
				if (this._class != null) {
					theLink.setClassToInstanciate(this._class);
				}
				if (dateParameterName != null) {
					theLink.addParameter(dateParameterName, new IWTimestamp(n, stamp.getMonth(), stamp.getYear()).toSQLDateString());
				}
				theLink.addParameter(CalendarParameters.PARAMETER_DAY, n);
				theLink.addParameter(CalendarParameters.PARAMETER_MONTH, this.stamp.getMonth());
				theLink.addParameter(CalendarParameters.PARAMETER_YEAR, this.stamp.getYear());
				/*if (textStyleClass == null) {
					theLink.setFontColor(textColor);
				} else {
					theLink.setStyleClass(textStyleClass);
				}*/
				//theLink.setFontSize(1);
				for (int i = 0; i < this.parameterName.size(); i++) {
					theLink.addParameter((String) this.parameterName.get(i), (String) this.parameterValue.get(i));
				}
				if(this.onClickMessageFormat!=null){
					Object[] s = new Object[2];
					IWTimestamp timeStamp = new IWTimestamp(n,this.stamp.getMonth(),this.stamp.getYear());
					
					s[0]="'"+dateValueFormat.format(timeStamp.getDate())+"'";
					//s[1]="'"+timeStamp.getTimestamp().toString()+"'";
					s[1] = "'"+df.format(timeStamp.getDate())+"'";
					String onClickString = java.text.MessageFormat.format(this.onClickMessageFormat, s);
					theLink.setOnClick(onClickString);
				}
				this.T.add(theLink, xpos, ypos);
			}
			else {
				this.T.add(t, xpos, ypos);
			}

			if (this.LINE_VIEW) {
				xpos++;
			}
			else {
				xpos = xpos % 7 + 1;
			}
			if (xpos == 1 && !this.LINE_VIEW) {
				ypos++;
			}
			n++;
		}

		if (this.inactiveBackgroundCellStyleClass != null) {
			for (int a = 1; a <= this.T.getRows(); a++) {
				for (int b = 1; b <= this.T.getColumns(); b++) {
					if (this.T.getClass(b, a) == null) {
						this.T.setStyleClass(b, a, this.inactiveBackgroundCellStyleClass);
					}
				}
			}
		} else 	if (this.inactiveCellColor != null) {
			for (int a = 1; a <= this.T.getRows(); a++) {
				for (int b = 1; b <= this.T.getColumns(); b++) {
					if (this.T.getColor(b, a) == null) {
						this.T.setColor(b, a, this.inactiveCellColor);
					}
				}
			}
		}

		T2.add(this.T, 1, 2);
		add(T2);
		iwc.setSessionAttribute("smcal" + getICObjectInstanceID(), this.stamp);
	}

	public void initialize() {
		this.today = new IWTimestamp();
		this.T = new Table();
		this.T.setCellspacing(0);
		this.T.setWidth(this.width);
	}

	private Text getLinkText(String content) {
		Text text = new Text(content);
		if (this.linkStyle != null) {
			text.setStyleClass(this.linkStyle);
		} else {
			text.setFontColor(this.headerTextColor);
			text.setFontSize(2);
			text.setBold();
			text.setFontStyle("font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: " + this.headerTextColor + "; font-size: 8pt; text-decoration: none;");
		}
		return text;
	}
	
	private Text getHeaderText(String content) {
		Text text = new Text(content);
		if (this.headerTextStyleClass != null) {
			text.setStyleClass(this.headerTextStyleClass);
		} else {
			text.setFontColor(this.headerTextColor);
			text.setFontSize(2);
			text.setBold();
			text.setFontStyle("font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: " + this.headerTextColor + "; font-size: 8pt; text-decoration: none;");
		}
		return text;
	}
	
	public void addNextMonthPrm(Link L, IWTimestamp idts) {
		if (idts.getMonth() == 12) {
			L.addParameter(CalendarParameters.PARAMETER_MONTH, String.valueOf(1));
			L.addParameter(CalendarParameters.PARAMETER_YEAR, String.valueOf(idts.getYear() + 1));
		}
		else {
			L.addParameter(CalendarParameters.PARAMETER_MONTH, String.valueOf(idts.getMonth() + 1));
			L.addParameter(CalendarParameters.PARAMETER_YEAR, String.valueOf(idts.getYear()));
		}
		L.addParameter(CalendarParameters.PARAMETER_DAY,String.valueOf(idts.getDay()));
//		L.addParameter(CalendarBusiness.PARAMETER_DAY,String.valueOf(idts.getDay()));
	}

	public void addLastMonthPrm(Link L, IWTimestamp idts) {
		if (idts.getMonth() == 1) {
			L.addParameter(CalendarParameters.PARAMETER_MONTH, String.valueOf(12));
			L.addParameter(CalendarParameters.PARAMETER_YEAR, String.valueOf(idts.getYear() - 1));
		}
		else {
			L.addParameter(CalendarParameters.PARAMETER_MONTH, String.valueOf(idts.getMonth() - 1));
			L.addParameter(CalendarParameters.PARAMETER_YEAR, String.valueOf(idts.getYear()));
		}
		L.addParameter(CalendarParameters.PARAMETER_DAY,String.valueOf(idts.getDay()));
		//L.addParameter(CalendarBusiness.PARAMETER_DAY,String.valueOf(idts.getDay()));
	}

	public IWTimestamp nextMonth(IWTimestamp idts) {
		if (idts.getMonth() == 12) {
			return new IWTimestamp(1, 1, idts.getYear() + 1);
		}
		else {
			return new IWTimestamp(1, idts.getMonth() + 1, idts.getYear());
		}
	}

	public IWTimestamp lastMonth(IWTimestamp idts) {
		if (idts.getMonth() == 1) {
			return new IWTimestamp(1, 12, idts.getYear() - 1);
		}
		else {
			return new IWTimestamp(1, idts.getMonth() - 1, idts.getYear());
		}
	}

	public String getDateString(int year, int month, int day) {
		return Integer.toString(year) + "-" + TextSoap.addZero(month) + "-" + TextSoap.addZero(day);
	}

	public String getDayColor(String dateString) {
		if (this.dayColors != null) {
			if (this.dayColors.get(dateString) != null) {
				return (String) this.dayColors.get(dateString);
			}
			else {
				return null;
			}
		}
		return null;
	}

	private String getDayBackgroundStyleClass(String dateString) {
		if (this.dayBackgroundStyleClass != null) {
			if (this.dayBackgroundStyleClass.get(dateString) != null) {
				return (String) this.dayBackgroundStyleClass.get(dateString);
			}
		}
		return null;
	}
	
	private String getDayStyleClass(String dateString) {
		String dayStyle = null;
		if (this.dayStyleClass != null) {
			if (this.dayStyleClass.get(dateString) != null) {
				dayStyle = (String) this.dayStyleClass.get(dateString);
			}
		}
		if (dayStyle == null) {
			dayStyle = this.inactiveTextStyleClass;
		}
		return dayStyle;
	}
	
	public String getDayFontColor(String dateString) {
		if (this.dayFontColors != null) {
			if (this.dayFontColors.get(dateString) != null) {
				return (String) this.dayFontColors.get(dateString);
			}
			else {
				return null;
			}
		}
		return null;
	}

	private boolean inThisMonth(String dayString, int year, int month) {
		if (dayString != null) {
			if (dayString.substring(0, 7).equalsIgnoreCase(getDateString(year, month, 1).substring(0, 7))) {
				return true;
			}
			return false;
		}
		return false;
	}

	public void setTextColor(String color) {
		this.textColor = color;
	}

	public void setOnlySelectedHighlighted(boolean highlight) {
		this._highlight = highlight;
	}

	public void setSelectedHighlighted(boolean highlight) {
		this._highlight = highlight;
	}

	public void setSelectedHighlightColor(String color) {
		this.selectedColor = color;
	}

	public void setHighlightedTextColor(String color) {
		this.highlightedText = color;
	}

	public void setBackgroundColor(String color) {
		this.backgroundColor = color;
	}

	public void setDayCellStyle(String style) {
		this.dayCellStyle = style;
	}
	public void setDayCellColor(String color) {
		this.dayCellColor = color;
	}

	public void setHeaderColor(String color) {
		this.headerColor = color;
	}

	public void setHeaderTextColor(String color) {
		this.headerTextColor = color;
	}

	public void setDayTextColor(String color) {
		this.dayTextColor = color;
	}

	public void setBodyColor(String color) {
		this.bodyColor = color;
	}

	public void setInActiveCellColor(String color) {
		this.inactiveCellColor = color;
	}

	public void useNextAndPreviousLinks(boolean use) {
		this.useNextAndPreviousLinks = use;
	}

	public void setDaysAsLink(boolean use) {
		this.daysAreLinks = use;
	}

	public void setURL(String url) {
		setDaysAsLink(true);
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setWidth(int width) {
		setWidth(Integer.toString(width));
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public void setHeight(int height) {
		setHeight(Integer.toString(height));
	}

	public void showNameOfDays(boolean show) {
		this.showNameOfDays = show;
	}

	public void setColorToday(String color) {
		this.todayColor = color;
	}

	public void useColorToday(boolean useColorToday) {
		if (!useColorToday) {
			this.todayColor = "";
		}
	}

	public void setDayFontStyleClass(int year, int month, int day, String styleClass) {
		if (this.dayStyleClass == null) {
			this.dayStyleClass = new Hashtable();
		}
		this.dayStyleClass.put(getDateString(year, month, day), styleClass);
	}
	
	public void setDayFontStyleClass(IWTimestamp timestamp, String color) {
		setDayFontStyleClass(timestamp.getYear(), timestamp.getMonth(), timestamp.getDay(), color);
	}

	public void setDayFontColor(int year, int month, int day) {
		if (this.dayFontColors == null) {
			this.dayFontColors = new Hashtable();
		}
		this.dayFontColors.put(getDateString(year, month, day), this.highlightedText);
	}

	public void setDayFontColor(int year, int month, int day, String color) {
		if (this.dayFontColors == null) {
			this.dayFontColors = new Hashtable();
		}
		this.dayFontColors.put(getDateString(year, month, day), color);
	}

	public void setDayFontColor(IWTimestamp timestamp, String color) {
		setDayFontColor(timestamp.getYear(), timestamp.getMonth(), timestamp.getDay(), color);
	}

	public void setTodayFontColor(String color) {
		IWTimestamp timestamp = new IWTimestamp();
		setDayFontColor(timestamp.getYear(), timestamp.getMonth(), timestamp.getDay(), color);
	}

	public void setDayStyleClass(int year, int month, int day, String styleClass) {
		if (this.dayBackgroundStyleClass == null) {
			this.dayBackgroundStyleClass = new Hashtable();
		}
		this.dayBackgroundStyleClass.put(getDateString(year, month, day), styleClass);
	}

	public void setDayStyleClass(IWTimestamp timestamp, String color) {
		this.setDayStyleClass(timestamp.getYear(), timestamp.getMonth(), timestamp.getDay(), color);
	}
	
	public void setDayColor(int year, int month, int day, String color) {
		if (this.dayColors == null) {
			this.dayColors = new Hashtable();
		}
		this.dayColors.put(getDateString(year, month, day), color);
	}

	public void setDayColor(IWTimestamp timestamp, String color) {
		this.setDayColor(timestamp.getYear(), timestamp.getMonth(), timestamp.getDay(), color);
	}

	public void setDayOfWeekColor(int dayOfWeek, String color) {

		int startingY = 1;
		if (this.showNameOfDays) {
			++startingY;
		}
		int[] lastDay = getMaxPos();
		int maxX = lastDay[0];
		int maxY = lastDay[1];

		if (maxX < dayOfWeek) {
			--maxY;
		}

		for (int i = startingY; i <= maxY; i++) {
			this.T.setColor(dayOfWeek, i, color);
		}

	}
	
	public void setDayOfWeekStyleClass(int dayOfWeek, String styleClass) {
		int startingY = 1;
		if (this.showNameOfDays) {
			++startingY;
		}
		int[] lastDay = getMaxPos();
		int maxX = lastDay[0];
		int maxY = lastDay[1];

		if (maxX < dayOfWeek) {
			--maxY;
		}

		for (int i = startingY; i <= maxY; i++) {
			this.T.setStyleClass(dayOfWeek, i, styleClass);
		}		
	}

	/**
	 * returns the x and y pos of the last day of the month
	 */
	private int[] getMaxPos() {
		int day = this.cal.getLengthOfMonth(this.stamp.getMonth(), this.stamp.getYear());

		return this.getXYPos(this.stamp.getYear(), this.stamp.getMonth(), day);
	}

	private int[] getXYPos(int year, int month, int day) {
		int startingY = 1;
		if (this.showNameOfDays) {
			++startingY;
		}

		int dayOfWeek = this.cal.getDayOfWeek(year, month, 1);
		int firstDayOfWeek = this.cal.getCalendar().getFirstDayOfWeek();
		int daynr = dayOfWeek - firstDayOfWeek;
		
		int x = (daynr + day) % 7;
		int y = ((daynr + day) / 7) + 1;
		if (x == 0) {
			x = 7;
			--y;
		}
		if (dayOfWeek == Calendar.SUNDAY && firstDayOfWeek != Calendar.SUNDAY) {
			y++;
		}

		y += (startingY - 1);

		int[] returner = { x, y };
		return returner;
	}

	private String getTarget() {
		return this._target;
	}

	public void setTarget(String target) {
		this._target = target;
	}

	public void setAsLineView(boolean line) {
		this.LINE_VIEW = line;
	}

	private Link getLink() {
		if (this._link == null) {
			this._link = new Link();
			if (getTarget() != null) {
				this._link.setTarget(getTarget());
			}
			setAsObjectInstanceTarget(this._link);
		}

		return (Link) this._link.clone();
	}

	/**
	 * Set the proxy Link object to be used
	 * @param link
	 */
	public void setLink(Link link) {
		this._link = link;
	}

	/**
	 * Set the page to direct to
	 * @param page
	 */
	public void setPage(ICPage page) {
		this._page = page;
	}

	/**
	 * Set the current timestamp
	 * @param stamp
	 */
	public void setTimestamp(IWTimestamp stamp) {
		this.stamp = stamp;
	}

	/**
	 * Adds a parameter to be maintained in calendar
	 * @param name
	 * @param value
	 */
	public void addParameterToLink(String name, int value) {
		addParameterToLink(name, Integer.toString(value));
	}
	/**
	 * Adds a parameter to be maintained in calendar
	 * @param name
	 * @param value
	 */
	public void addParameterToLink(String name, String value) {
		this.parameterName.add(name);
		this.parameterValue.add(value);
	}
	
	/**
	 * Set the message format to be parsed with MessageFormat
	 * with the {0} parameter for the chosen dates long value
	 * and the {1} parameter for the chosed date's locale display format
	 * @param format
	 */
	public void setOnClickMessageFormat(String format){
		this.onClickMessageFormat = format;
	}
	
	/**
	 * Sets the display format of the date when calendar user to choose
	 * date, default set to DateFormat.MEDIUM, can be DateFormat.SHORT
	 * and DateFormat.LONG instead
	 * @param format
	 */
	public void setOnClickDisplayFormat(int format){
		this.displayFormat = format;
	}

	public synchronized Object clone() {
		SmallCalendar obj = null;
		try {
			obj = (SmallCalendar) super.clone();
			if (this.today != null) {
				obj.today = new IWTimestamp(this.today);
			}
			if (this.stamp != null) {
				obj.stamp = new IWTimestamp(this.stamp);
			}
			if (this.T != null) {
				obj.T = (Table) this.T.clone();
			}
			if (this.dayColors != null) {
				obj.dayColors = (Hashtable) this.dayColors.clone();
			}
			if (this.dayFontColors != null) {
				obj.dayFontColors = (Hashtable) this.dayFontColors.clone();
			}
			if (this.dayStyleClass != null) {
				obj.dayStyleClass = (Hashtable) this.dayStyleClass.clone();
			}
			if (this.dayBackgroundStyleClass != null) {
				obj.dayBackgroundStyleClass = (Hashtable) this.dayBackgroundStyleClass.clone();
			}
			if (this.parameterName != null) {
				obj.parameterName = (Vector) this.parameterName.clone();
			}
			if (this.parameterValue != null) {
				obj.parameterValue = (Vector) this.parameterValue.clone();
			}

			obj.cal = this.cal;

			obj.useNextAndPreviousLinks = this.useNextAndPreviousLinks;
			obj.daysAreLinks = this.daysAreLinks;
			obj.showNameOfDays = this.showNameOfDays;

			obj.textColor = this.textColor;
			obj.headerTextColor = this.headerTextColor;
			obj.headerColor = this.headerColor;
			obj.bodyColor = this.bodyColor;
			obj.inactiveCellColor = this.inactiveCellColor;
			obj.backgroundColor = this.backgroundColor;
			obj.todayColor = this.todayColor;
			
			obj.textStyleClass = this.textStyleClass;
			obj.headerTextStyleClass = this.headerTextStyleClass;
			obj.inactiveBackgroundCellStyleClass = this.inactiveBackgroundCellStyleClass;
			obj.backgroundStyleClass = this.backgroundStyleClass;
			obj.todayBackgroundStyleClass = this.backgroundStyleClass;
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}
	
	public void setInitializingString(String initializingString){
		StringTokenizer tok = new StringTokenizer(initializingString,",");
		if(tok.countTokens()==8){
			String t = tok.nextToken();
			if(t.equals("1")) {
				showNameOfDays(true);
			}
			t = tok.nextToken();
			if(t!=null) {
				setTextColor(t);
			}
			t = tok.nextToken();
			if(t!=null) {
				setHeaderTextColor(t);
			}
			t = tok.nextToken();
			if(t!=null) {
				setHeaderColor(t);
			}
			t = tok.nextToken();
			if(t!=null) {
				setBodyColor(t);
			}
			t = tok.nextToken();
			if(t!=null) {
				setInActiveCellColor(t);
			}
			t = tok.nextToken();
			if(t!=null) {
				setBackgroundColor(t);
			}
			t = tok.nextToken();
			if(t!=null) {
				setTodayFontColor(t);
			}
		
		}
	}
	
	public static String getInitializingString(boolean showNameOfDays,String textColor,String headerTextColor,String headerColor,String bodyColor,String inactiveCellColor,String backgroundColor,String todayColor){
		StringBuffer buf = new StringBuffer();
		String sep = ",";
		buf.append(showNameOfDays?"1":"0").append(sep);
		buf.append(textColor).append(sep);
		buf.append(headerTextColor).append(sep);
		buf.append(headerColor).append(sep);
		buf.append(bodyColor).append(sep);
		buf.append(inactiveCellColor).append(sep);
		buf.append(backgroundColor).append(sep);
		buf.append(todayColor);
		return buf.toString();
	}
	
	/*
	 * This method is not a presentation but was moved here because of compilation issues
	 */
	 //TODO: Move away to a more appropriate class
	public static IWTimestamp getTimestamp(String day, String month, String year) {
		IWTimestamp stamp = new IWTimestamp();

		if (day != null) {
			stamp.setDay(Integer.parseInt(day));
		}
		// removed dubius behavior A
		/*else {
		 stamp.setDay(1);
		 }
		 */
		if (month != null) {
			stamp.setMonth(Integer.parseInt(month));
		}
		if (year != null) {
			stamp.setYear(Integer.parseInt(year));
		}

		stamp.setHour(0);
		stamp.setMinute(0);
		stamp.setSecond(0);

		return stamp;
	}

	public void setBackgroundStyleName(String style) {
		this.backgroundStyleClass = style;
	}
	
	public void setHeaderFontStyleName(String style) {
		this.headerTextStyleClass = style;
	}
	
	public void setTextStyleName(String style) {
		this.textStyleClass = style;
	}
	
	public void setHighlightedTextStyleName(String style) {
	}
	
	public void setDayTextStyleName(String style) {
	}
	
	public void setLinkStyle(String style) {
		this.linkStyle = style;
	}
	
	public void setInactiveBackgroundCellStyleName(String style) {
		this.inactiveBackgroundCellStyleClass = style;
	}

	public void setTodayBackgroundStyleName(String style) {
		this.todayBackgroundStyleClass = style;
	}

	public void setSelectedBackgroundStyleName(String style) {
		this.selectedBackgroundStyleClass = style;
	}

	public void setNextImage(Image nextImage) {
		this.iNextImage = nextImage;
	}
	
	public void setPreviousImage(Image previousImage) {
		this.iPreviousImage = previousImage;
	}
	public void setInactiveTextStyleClass(String inactiveTextStyleClass) {
		this.inactiveTextStyleClass = inactiveTextStyleClass;
	}
	public void setCellpadding(int cellpadding) {
		this.iCellpadding = cellpadding;
	}
	public void setMonthTextStyleClass(String monthTextStyleClass) {
	}

	public void setClassToLinkTo(Class classToLinkTo) {
		this._class = classToLinkTo;
	}

	public void setDateParameterName(String dateParameterName) {
		this.dateParameterName = dateParameterName;
	}
}
