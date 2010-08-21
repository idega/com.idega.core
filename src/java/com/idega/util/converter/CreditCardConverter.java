package com.idega.util.converter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

public class CreditCardConverter implements Converter {

	public final static String CONVERTER_ID = "iw.CreditCard";
	
	private String pattern;
	private String seperator;
	private int digits = -1;

	public Object getAsObject(FacesContext context, UIComponent component, String newValue) throws ConverterException {
		Long convertedValue = null;
		if (newValue == null) {
			return newValue;
		}
		
		if (getPattern() == null) {
			setPattern("0000,0000,0000,0000");
		}
		if (getSeperator() == null) {
			setSeperator("-");
		}
		if (getDigits() < 0) {
			setDigits(16);
		}
		
    	try {
			convertedValue = new Long(newValue.trim());
			
			DecimalFormat format = new DecimalFormat(getPattern());
			format.setMaximumFractionDigits(0);
			format.setMinimumIntegerDigits(getDigits());
			format.setMaximumIntegerDigits(getDigits());

			DecimalFormatSymbols dfs = format.getDecimalFormatSymbols();
			dfs.setGroupingSeparator(getSeperator().charAt(0));
			format.setDecimalFormatSymbols(dfs);
			
			return format.format(convertedValue);
		}
		catch (NumberFormatException nfe) {
			throw new ConverterException(nfe);
		}
	}

	public String getAsString(FacesContext context, UIComponent component, Object newValue) throws ConverterException {
		if (newValue == null) {
			return "null";
		}
		
		return getAsObject(context, component, newValue.toString()).toString();
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public String getSeperator() {
		return seperator;
	}

	public void setSeperator(String seperator) {
		this.seperator = seperator;
	}

	public int getDigits() {
		return digits;
	}

	public void setDigits(int digits) {
		this.digits = digits;
	}
	
	public static void main(String[] args) {
		CreditCardConverter c = new CreditCardConverter();
		c.setPattern("****-****-****-0000");
		c.setDigits(4);
		
		System.out.println(c.getAsString(null, null, "01234567891010"));
	}
}