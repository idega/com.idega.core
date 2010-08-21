package com.idega.util.converter;

import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.idega.util.LocaleUtil;
import com.idega.util.PersonalIDFormatter;

public class PersonalIDConverter implements Converter {

	public final static String CONVERTER_ID = "iw.PersonalID";

	private Locale locale;
	
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		if (value == null) {
			return value;
		}
		
		if (getLocale() == null) {
			setLocale(LocaleUtil.getIcelandicLocale());
		}
		
		return PersonalIDFormatter.format(value, locale);
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		if (value == null) {
			return "null";
		}
		
		return getAsObject(context, component, value.toString()).toString();
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public static void main(String[] args) {
		PersonalIDConverter c = new PersonalIDConverter();
		
		System.out.println(c.getAsString(null, null, "0202774919"));
	}
}