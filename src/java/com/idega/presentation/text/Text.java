// idega 2000 - Tryggvi Larusson
/*
 * Copyright 2000 idega.is All Rights Reserved.
 */

package com.idega.presentation.text;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.idegaweb.IWConstants;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.util.text.StyleConstants;

/**
 * A wrapper class for presenting plain (formatted) text in idegaWeb
 * Presentaiton Objects.
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.2
 */
public class Text extends PresentationObject {

	private static Text emptyText;
	private static Text HTMLbreak;
	private static Text HTMLnbsp;

	protected String text;
	protected Map localizationMap;
	protected boolean attributeSet;
	protected boolean teletype;

	private boolean addHTMLFontTag = true;

	public static String FONT_FACE_ARIAL = "Arial, Helvetica, Sans-serif";
	public static String FONT_FACE_TIMES = "Times New Roman, Times, serif";
	public static String FONT_FACE_COURIER = "Courier New, Courier, mono";
	public static String FONT_FACE_GEORGIA = "Georgia, Times New Roman, Times, serif";
	public static String FONT_FACE_VERDANA = "Verdana, Arial, Helvetica, sans-serif";
	public static String FONT_FACE_GENEVA = "Geneva, Arial, Helvetica, san-serif";

	public static String FONT_FACE_STYLE_NORMAL = "normal";
	public static String FONT_FACE_STYLE_BOLD = "bold";
	public static String FONT_FACE_STYLE_ITALIC = "italic";
	public static String FONT_SIZE_7_HTML_1 = "1";
	public static String FONT_SIZE_7_STYLE_TAG = "7pt";
	public static String FONT_SIZE_10_HTML_2 = "2";
	public static String FONT_SIZE_10_STYLE_TAG = "10pt";
	public static String FONT_SIZE_12_HTML_3 = "3";
	public static String FONT_SIZE_12_STYLE_TAG = "12pt";
	public static String FONT_SIZE_14_HTML_4 = "4";
	public static String FONT_SIZE_14_STYLE_TAG = "14pt";
	public static String FONT_SIZE_16_STYLE_TAG = "16pt";
	public static String FONT_SIZE_18_HTML_5 = "5";
	public static String FONT_SIZE_18_STYLE_TAG = "18pt";
	public static String FONT_SIZE_24_HTML_6 = "6";
	public static String FONT_SIZE_24_STYLE_TAG = "24pt";
	public static String FONT_SIZE_34_HTML_7 = "7";
	public static String FONT_SIZE_34_STYLE_TAG = "34pt";

	public static String NON_BREAKING_SPACE = "&nbsp;";
	public static String BREAK = "<br/>";

	public static final String EMPTY_TEXT_STRING = "No text";

	/**
	 * *Constructor that creates the object with an empty string
	 */

	public Text() {
		this(EMPTY_TEXT_STRING);
	}

	public Text(String text) {
		super();
		setText(text);
		attributeSet = false;
		teletype = false;
	}

	public Text(String text, boolean bold, boolean italic, boolean underline) {
		this(text);
		if (bold) {
			this.setBold();
		}
		if (italic) {
			this.setItalic();
		}
		if (underline) {
			this.setUnderline();
		}
	}

	public void setMarkupAttribute(String name, String value) {
		attributeSet = true;
		super.setMarkupAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#setStyleAttribute(java.lang.String, java.lang.String)
	 */
	public void setStyleAttribute(String attribute, String value) {
		attributeSet = true;
		super.setStyleAttribute(attribute, value);
	}

	protected boolean isEnclosedByParagraph() {
		boolean returnBool = false;
		PresentationObject obj = getParentObject();
		while (obj != null) {
			if (obj.getClassName().equals("com.idega.presentation.text.Paragraph")) {
				returnBool = true;
			}
			obj = obj.getParentObject();
		}
		return returnBool;
	}

	public void setFontSize(String size) {
		this.setStyleAttribute("font-size", getFontSize(size));
	}

	public void setFontSize(int size) {
		setFontSize(Integer.toString(size));
	}

	public void setFontFace(String fontFace) {
		setStyleAttribute("font-family", fontFace);
	}

	public void setFontColor(String color) {
		setStyleAttribute("color", color);
	}

	public void setFontStyle(String style) {
		setStyleAttribute(style);
	}

	public void setFontClass(String styleClass) {
		attributeSet = true;
		setStyle(styleClass);
	}

	public void setStyle(String style) {
		attributeSet = true;
		setStyleClass(style);
	}

	public void addToText(String text) {
		if (this.text == EMPTY_TEXT_STRING) {
			this.text = text;
		}
		else {
			this.text = this.text + text;
		}
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setLocalizedText(String localeString, String text) {
		setLocalizedText(ICLocaleBusiness.getLocaleFromLocaleString(localeString), text);
	}

	public void setLocalizedText(int icLocaleID, String text) {
		setLocalizedText(ICLocaleBusiness.getLocale(icLocaleID), text);
	}

	public void setLocalizedText(Locale locale, String text) {
		getLocalizationMap().put(locale, text);
	}

	private Map getLocalizationMap() {
		if (localizationMap == null) {
			localizationMap = new HashMap();
		}
		return localizationMap;
	}

	public void addBreak() {
		addToText(BREAK);
	}

	public void setTeleType() {
		teletype = true;
	}

	public void setBold() {
		setBold(true);
	}

	public void setBold(boolean bold) {
		if (bold) {
			setStyleAttribute(StyleConstants.ATTRIBUTE_FONT_WEIGHT, StyleConstants.FONT_WEIGHT_BOLD);
		}
		else {
			setStyleAttribute(StyleConstants.ATTRIBUTE_FONT_WEIGHT, StyleConstants.FONT_WEIGHT_NORMAL);
		}
	}

	public void setItalic() {
		setItalic(true);
	}

	public void setItalic(boolean italic) {
		if (italic) {
			setStyleAttribute(StyleConstants.ATTRIBUTE_FONT_STYLE, StyleConstants.FONT_STYLE_ITALIC);
		}
		else {
			setStyleAttribute(StyleConstants.ATTRIBUTE_FONT_STYLE, StyleConstants.FONT_STYLE_NORMAL);
		}
	}

	public void setUnderline() {
		setUnderline(true);
	}

	public void setUnderline(boolean underline) {
		if (underline) {
			setStyleAttribute(StyleConstants.ATTRIBUTE_TEXT_DECORATION, StyleConstants.TEXT_DECORATION_UNDERLINE);
		}
		else {
			setStyleAttribute(StyleConstants.ATTRIBUTE_TEXT_DECORATION, StyleConstants.TEXT_DECORATION_NONE);
		}
	}

	public String getText() {
		return this.text;
	}

	public String toString() {
		return this.text;
	}

	public void setCSSClass(String cStyleSheetReferanceClass) {
		setMarkupAttribute("class", cStyleSheetReferanceClass);
	}

	/**
	 * returns empty string with fontsize = 1
	 */
	public static Text emptyString() {
		if (emptyText == null) {
			emptyText = new Text("");
			emptyText.setFontSize("1");
		}
		return emptyText;
	}

	public static Text getBreak() {
		if (HTMLbreak == null) {
			HTMLbreak = new Text(BREAK);
			HTMLbreak.addHTMLFontTag(false);
			HTMLbreak.setUseBuilderObjectControl(false);
			// HTMLbreak.setFontSize("1");
		}
		return HTMLbreak;
	}

	public static Text getNonBrakingSpace() {
		if (HTMLnbsp == null) {
			HTMLnbsp = new Text(NON_BREAKING_SPACE);
			HTMLnbsp.addHTMLFontTag(false);
			//HTMLnbsp.setFontSize(1);
		}
		return HTMLnbsp;
	}

	public static Text getNonBrakingSpace(int fontSize) {
		Text nbsp = getNonBrakingSpace();
		nbsp.setFontSize(fontSize);
		return HTMLnbsp;
	}

	public void addHTMLFontTag(boolean addHTMLFontTag) {
		this.addHTMLFontTag = addHTMLFontTag;
	}

	public Object clone() {
		Text obj = null;
		try {
			obj = (Text) super.clone();

			obj.text = this.text;
			obj.attributeSet = this.attributeSet;
			obj.addHTMLFontTag = this.addHTMLFontTag;
			if (this.localizationMap != null) {
				obj.localizationMap = (Map) ((HashMap) this.localizationMap).clone();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}

		return obj;
	}

	public String getLocalizedText(IWContext iwc) {
		if (this.localizationMap != null) {
			Locale currLocale = iwc.getCurrentLocale();

			String localizedString = (String) this.getLocalizationMap().get(currLocale);
			if (localizedString != null) {
				return localizedString;
			}
			else {
				String defLocalizedString = (String) this.getLocalizationMap().get(iwc.getIWMainApplication().getSettings().getDefaultLocale());
				if (defLocalizedString != null) {
					return defLocalizedString;
				}
			}
		}
		return getText();
	}

	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
			if (attributeSet) {
				print("<span " + getMarkupAttributesString() + " >");
				print(getLocalizedText(iwc));
				print("</span>");
			}
			else {
				print(getLocalizedText(iwc));
			}
		}
		else if (getLanguage().equals(IWConstants.MARKUP_LANGUAGE_PDF_XML)) {
			String attributes = getMarkupAttributesString();

			print("<paragraph " + attributes + ">");
			print(getLocalizedText(iwc));
			print("</paragraph>");
		}
		else if (getLanguage().equals(IWConstants.MARKUP_LANGUAGE_WML)) {
			String text = getLocalizedText(iwc);
			print(text);
		}
	}

	private String getFontSize(String size) {
		if (size.equals(FONT_SIZE_7_HTML_1)) {
			return FONT_SIZE_7_STYLE_TAG;
		}
		else if (size.equals(FONT_SIZE_10_HTML_2)) {
			return FONT_SIZE_10_STYLE_TAG;
		}
		else if (size.equals(FONT_SIZE_12_HTML_3)) {
			return FONT_SIZE_12_STYLE_TAG;
		}
		else if (size.equals(FONT_SIZE_14_HTML_4)) {
			return FONT_SIZE_14_STYLE_TAG;
		}
		else if (size.equals(FONT_SIZE_18_HTML_5)) {
			return FONT_SIZE_18_STYLE_TAG;
		}
		else if (size.equals(FONT_SIZE_24_HTML_6)) {
			return FONT_SIZE_24_STYLE_TAG;
		}
		else if (size.equals(FONT_SIZE_34_HTML_7)) {
			return FONT_SIZE_34_STYLE_TAG;
		}
		return size + "px";
	}
}