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
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
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

    private int dateFormatStyle = DateFormat.SHORT;
    private String dateFormatPattern = "yyyy-MM-dd";
    private Date date = new Date();

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
        setChooserParameter(name);
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
        setChooseButtonImage(iwb.getImage("calendar.gif", "Pick date"));
        if (locale == null) {
            locale = iwc.getCurrentLocale();
        }
        if (date != null) {
            setDate(date);
        } else {
            setDate(new Date());
        }
        //setParameterValue(SmallCalendar.PRM_SETTINGS,SmallCalendar.getInitializingString(true,null,"#0000FF","#00FF00","#00FFFF","#FFFF00","#FFFFFF","#FFF000"));
    }

    public PresentationObject getPresentationObject(IWContext iwc) {

        TextInput input = new TextInput(displayInputName);
        input.setDisabled(disabled);
        int inputLength = 10;
        switch (dateFormatStyle) {
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
        input.setLength(inputLength);
        if (_style != null) {
            input.setMarkupAttribute("style", _style);
        }

        if (_stringDisplay != null) {
            input.setValue(_stringDisplay);
        }

        return input;
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
        String display = date.toString();
        String value = new IWTimestamp(date.getTime()).getTimestamp()
                .toString();
        if (locale != null) {
            display = new SimpleDateFormat(dateFormatPattern,locale).format(date);
            //DateFormat.getDateInstance(dateFormatStyle, locale).format(date);
        } else {
            display = new SimpleDateFormat(dateFormatPattern).format(date);
            //DateFormat.getDateInstance(dateFormatStyle).format(date);
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
        dateFormatStyle = formatStyle;
    }
    
    /**
     * Sets the format pattern for date display ( see java.text.SimpleDateFormat)
     * @param pattern
     */
    public void setDateFormatPattern(String pattern){
        this.dateFormatPattern = pattern;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.idega.business.InputHandler#getHandlerObject(java.lang.String,
     *      java.lang.String, com.idega.presentation.IWContext)
     */
    public PresentationObject getHandlerObject(String name, String value,
            IWContext iwc) {
        setName(name);
        if (value != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = formatter.parse(value);
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                setDate(sqlDate, iwc.getCurrentLocale());
                return this;
            } catch (ParseException ex) {
                logError("[DateInput] The value " + value
                        + " could not be parsed");
                // go further to the default setting
            }
        }
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.idega.business.InputHandler#getHandlerObject(java.lang.String,
     *      java.util.Collection, com.idega.presentation.IWContext)
     */
    public PresentationObject getHandlerObject(String name, Collection values,
            IWContext iwc) {
        String value = (String) Collections.min(values);
        return getHandlerObject(name, value, iwc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.idega.business.InputHandler#getResultingObject(java.lang.String[],
     *      com.idega.presentation.IWContext)
     */
    public Object getResultingObject(String[] value, IWContext iwc)
            throws Exception {
        if (value != null && value.length > 0) {
            String dateString = value[0];
            if (" ".equals(dateString) || "".equals(dateString)) {
                return null;
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = formatter.parse(dateString);
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                return sqlDate;
            }
        } else
            return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.idega.business.InputHandler#getDisplayForResultingObject(java.lang.Object,
     *      com.idega.presentation.IWContext)
     */
    public String getDisplayForResultingObject(Object value, IWContext iwc) {
        Locale locale = iwc.getCurrentLocale();
        if (value != null) {
            return TextSoap.findAndCut((new IWTimestamp((java.sql.Date) value))
                    .getLocaleDate(locale), "GMT");
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.idega.business.InputHandler#convertSingleResultingObjectToType(java.lang.Object,
     *      java.lang.String)
     */
    public Object convertSingleResultingObjectToType(Object value,
            String className) {
        return value;
    }
    
    private boolean useJSCalendar(IWBundle bundle){
        if(!useJSCalendar)
            return false;
        else if(jsExists!=null)
            return jsExists.booleanValue();
        else{
            try {
                java.io.File jsFile = new java.io.File(IWMainApplication.getDefaultIWMainApplication().getRealPath(bundle.getImageURI("jscalendar/calendar.js")));
                jsExists = Boolean.valueOf(jsFile.exists());
                return jsExists.booleanValue();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see com.idega.presentation.ui.AbstractChooser#getTable(com.idega.presentation.IWContext, com.idega.idegaweb.IWBundle)
     */
    public PresentationObject getTable(IWContext iwc, IWBundle bundle) {
        if(!useJSCalendar(bundle))
          return super.getTable(iwc, bundle);
        
        Table table = new Table(2, 2);
		table.setCellpadding(0);
		table.setCellspacing(0);
		
		PresentationObject object = getPresentationObject(iwc);
		Parameter value = new Parameter(getChooserParameter(), "");
		if (getChooserValue() != null) {
			value.setValue(getChooserValue());
		}
		
		Image button = (bundle.getImage("jscalendar/cal.gif"));
		button.setOnClick("return showCalendar('"+object.getID()+"', '"+dateFormatPattern+"','"+value.getID()+"');");
		
		Page parentPage = getParentPage();
		parentPage.addJavascriptURL(bundle.getImageURI("jscalendar/calendar.js"));
		parentPage.addJavascriptURL(bundle.getImageURI("jscalendar/calendar-"+iwc.getCurrentLocale().getLanguage()+".js" ));
		parentPage.addJavascriptURL(bundle.getImageURI("jscalendar/calendarHelper.js"));
		
		parentPage.addStyleSheetURL(bundle.getImageURI("jscalendar/calendar-win2k-1.css"));

		
		table.add(value);
		table.add(new Parameter(VALUE_PARAMETER_NAME, value.getName()));
		table.add(object, 1, 1);
		table.add(button,2,1);
		
		
		return table;
    }

}