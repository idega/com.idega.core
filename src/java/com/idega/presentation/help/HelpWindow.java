/*
 * $Id:$
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.help;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.presentation.ICLocalePresentation;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.texteditor.TextEditor;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.Window;
import com.idega.util.FileUtil;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLCDATA;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLOutput;
import com.idega.xml.XMLParser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * This class does something very clever.....
 * 
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class HelpWindow extends Window {
	public final static String HELP_KEY = Help.HELP_KEY;
	public final static String HELP_BUNDLE = Help.HELP_BUNDLE;
	private final static String IW_CORE_BUNDLE_IDENTIFIER = "com.idega.core";
	private final static String EDIT_IMAGE = "shared/edit.gif";
	private final static String HELP_FILE_PREFIX = "hlp_";

	private final static String XML_ROOT = "xml";
	private final static String XML_HELP = "help";
	private final static String XML_ID = "id";
	private final static String XML_TITLE = "title";
	private final static String XML_FOLDER = "help";
	private final static String XML_EXTENSION = ".xml";

	private final static String TITLE = "hlp_title";
	private final static String LOCALE = "hlp_locale";
	private final static String BODY = "hlp_body";

	private final static String SAVE = "hlp_save";
	private final static String CLOSE = "hlp_close";
	private final static String EDIT = "hlp_edit";

	private boolean _hasEdit = false;
	private IWBundle _iwb = null;
	private IWResourceBundle _iwrb = null;
	private String _helpKey = null;
	private String _helpBundle = null;

	private String _localizedTitle = null;
	private String _localizedHelpText = null;
	
	private String _previousLocale = null;

	private XMLDocument _doc = null;
//	private XMLElement _root = null;

	public HelpWindow() {
		super();
		setResizable(true);
		setScrollbar(true);
		setHeight(200);
		setWidth(160);
	}

	private void edit(IWContext iwc) {
		Form form = new Form();
		Table t = new Table(1, 10);
		DropdownMenu localeDrop = ICLocalePresentation.getLocaleDropdownIdKeyed(LOCALE);
		localeDrop.setToSubmit();
		
    String localeIdString = iwc.getParameter(LOCALE);
    Locale loc = null;
    if (localeIdString != null) {
			localeDrop.setSelectedElement(localeIdString);			
			loc = ICLocaleBusiness.getLocale(Integer.parseInt(localeIdString));
    } 
    else {
    	Locale currentLocale = iwc.getCurrentLocale();
    	int locId = ICLocaleBusiness.getLocaleId(currentLocale);
    	localeDrop.setSelectedElement(Integer.toString(locId));
    	loc = currentLocale;
    }

		getHelpText(iwc, _helpKey, _helpBundle, loc);

		TextInput title = new TextInput(TITLE);
		title.setLength(40);
		title.setMaxlength(255);
		if (_localizedTitle != null)
			title.setContent(_localizedTitle);

		TextEditor editor = new TextEditor();
		editor.setInputName(BODY);
		if (_localizedHelpText != null)
			editor.setContent(_localizedHelpText);

		SubmitButton save = new SubmitButton(_iwrb.getLocalizedImageButton(SAVE, "Save"), SAVE);
		SubmitButton close = new SubmitButton(_iwrb.getLocalizedImageButton(CLOSE, "Close"), CLOSE);

		int row = 1;
		t.add(formatLabel(_iwrb.getLocalizedString(TITLE, "Title")), 1, row++);
		t.add(title, 1, row++);
		row++;
		t.add(formatLabel(_iwrb.getLocalizedString(LOCALE, "Locale")), 1, row++);
		t.add(localeDrop, 1, row++);
		row++;
		t.add(formatLabel(_iwrb.getLocalizedString(BODY, "Help text")), 1, row++);
		t.add(editor, 1, row++);
		row++;
		t.add(save, 1, row);
		t.add(Text.NON_BREAKING_SPACE, 1, row);
		t.add(close, 1, row);

		form.add(t);
		form.add(new HiddenInput(EDIT, "true"));
		form.add(new HiddenInput(HELP_KEY, _helpKey));
		if (_helpBundle != null)
			form.add(new HiddenInput(HELP_BUNDLE, _helpBundle));
		add(form);
	}

	private void view(IWContext iwc) {
		Table t = null;
		int row = 1;
		if (_hasEdit) {
			t = new Table(1, 5);

			Link change = new Link();
			change.setImage(_iwb.getImage(EDIT_IMAGE));
			change.setParameter(EDIT, "true");
			change.setParameter(HELP_KEY, _helpKey);
			t.add(change, 1, row);
			row = 3;
		}
		else {
			t = new Table(1, 3);
		}

    String localeIdString = iwc.getParameter(LOCALE);
    Locale loc = null;
    if (localeIdString != null) {
			loc = ICLocaleBusiness.getLocale(Integer.parseInt(localeIdString));
    } 
    else {    	
    	loc = iwc.getCurrentLocale();
    }

		getHelpText(iwc, _helpKey, _helpBundle, loc);

		Text title = null;
		if (_localizedTitle != null)
			title = formatLabel(_localizedTitle);

		t.add(title, 1, row++);
		row++;
		if (_localizedHelpText != null)
			t.add(_localizedHelpText, 1, row);

		add(t);
	}

	private void putHelpText(IWContext iwc, String helpKey, String bundle, Locale loc, String title, String body) { 
		if (_doc == null) 
			loadHelpText(iwc,helpKey,bundle);
			
		XMLElement root = _doc.getRootElement();
		XMLElement help = root.getChild(XML_HELP);
		if (help == null) {
			help = new XMLElement(XML_HELP);
			help.setAttribute(XML_ID,helpKey);
			root.addContent(help);
		}
		
		XMLAttribute id = help.getAttribute(XML_ID);
		if (id == null || !id.getValue().equals(helpKey)) {
			help = new XMLElement(XML_HELP);
			help.setAttribute(XML_ID,helpKey);
			root.addContent(help);				
		}

		String lang = loc.getLanguage();
		String country = loc.getCountry();
		
		StringBuffer localeString = new StringBuffer(lang);
		if (!country.equals("")) {
			localeString.append("_");
			localeString.append(country);
		}
				
		XMLElement localeElement = help.getChild(localeString.toString());
		if (localeElement == null) {
			localeElement = new XMLElement(localeString.toString());
			help.addContent(localeElement);
		}

		XMLElement titleElement = localeElement.getChild(XML_TITLE);
		if (titleElement == null) {
			titleElement = new XMLElement(XML_TITLE);
			localeElement.addContent(titleElement);
		}

		if (title != null)
			titleElement.setText(title);
			
		XMLCDATA cdata = localeElement.getXMLCDATAContent();
		if (cdata != null) {
			cdata.setText(body);
		}
		else {
			cdata = new XMLCDATA(body);
			localeElement.addContent(cdata);
		}
	}

	private void saveHelpText(IWContext iwc, String helpKey, String bundle) {
		try {
			IWBundle iwb = null;
			if (bundle == null)
				iwb = iwc.getApplication().getBundle(IW_CORE_BUNDLE_IDENTIFIER);
			else
				iwb = iwc.getApplication().getBundle(bundle);

			XMLParser parser = new XMLParser(false);
			StringBuffer fileName = new StringBuffer(iwb.getResourcesRealPath());
			if (!fileName.toString().endsWith(FileUtil.getFileSeparator()))
				fileName.append(FileUtil.getFileSeparator());

			fileName.append(XML_FOLDER);
			File file = new File(fileName.toString());
			file.mkdir();

			fileName.append(FileUtil.getFileSeparator());
			fileName.append(HELP_FILE_PREFIX);
			fileName.append(helpKey);
			fileName.append(XML_EXTENSION);
			
			file = new File(fileName.toString());
			file.createNewFile();

			FileOutputStream out = new FileOutputStream(file);
		
			if (_doc == null)
				_doc = new XMLDocument(new XMLElement(XML_ROOT));

			XMLOutput output = new XMLOutput("  ", true);
			output.setLineSeparator(System.getProperty("line.separator"));
			output.setTextNormalize(true);
			output.setEncoding("UTF-16");
			output.output(_doc, out);

			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getHelpText(IWContext iwc, String helpKey, String bundle, Locale loc) {
		_localizedTitle = null;
		_localizedHelpText = null;

		if (_doc == null) 
			loadHelpText(iwc,helpKey,bundle);

		XMLElement root = _doc.getRootElement();
		if (root == null)
			return;
			
		XMLElement help = root.getChild(XML_HELP);
		if (help == null)
			return;
			
		XMLAttribute id = help.getAttribute(XML_ID);
		if (id == null || !id.getValue().equals(helpKey))
			return;

		String lang = loc.getLanguage();
		String country = loc.getCountry();
		
		StringBuffer localeString = new StringBuffer(lang);
		if (!country.equals("")) {			
			localeString.append("_");
			localeString.append(country);
		}
				
		XMLElement localeElement = help.getChild(localeString.toString());
		if (localeElement == null)
			return;
			
		XMLElement title = localeElement.getChild(XML_TITLE);
		if (title != null) {
			String tmp = title.getText();
			_localizedTitle = title.getTextTrim();
		}
			
		XMLCDATA cdata = localeElement.getXMLCDATAContent();
		if (cdata != null) 
			_localizedHelpText = cdata.getText();		
	}

	private void loadHelpText(IWContext iwc, String helpKey, String bundle) {
		try {
			IWBundle iwb = null;
			if (bundle == null)
				iwb = iwc.getApplication().getBundle(IW_CORE_BUNDLE_IDENTIFIER);
			else
				iwb = iwc.getApplication().getBundle(bundle);
				
			XMLParser parser = new XMLParser(false);
			StringBuffer fileName = new StringBuffer(iwb.getResourcesRealPath());
			if (!fileName.toString().endsWith(FileUtil.getFileSeparator()))
				fileName.append(FileUtil.getFileSeparator());

			fileName.append(XML_FOLDER);
			File file = new File(fileName.toString());
			file.mkdir();

			fileName.append(FileUtil.getFileSeparator());
			fileName.append(HELP_FILE_PREFIX);
			fileName.append(helpKey);
			fileName.append(XML_EXTENSION);
			
			file = new File(fileName.toString());
			file.createNewFile();

			_doc = parser.parse(file);
		}
		catch (Exception e) {
			_doc = new XMLDocument(new XMLElement(XML_ROOT));
		}
	}

	private void control(IWContext iwc) {
//		if (iwc.isParameterSet(LOCALE)) {
//			String locale = iwc.getParameter(LOCALE);
//			if (locale.equals(_previousLocale)) {
//			}
//			else {
//				_previousLocale = locale;
//			}
//		}

		if (iwc.isParameterSet(SAVE)) {
			String title = iwc.getParameter(TITLE);
			String body = iwc.getParameter(BODY);
			String locale = iwc.getParameter(LOCALE);
			Locale loc = ICLocaleBusiness.getLocale(Integer.parseInt(locale));
			putHelpText(iwc,_helpKey,_helpBundle,loc,title,body);
			saveHelpText(iwc,_helpKey,_helpBundle);
		}	
		
		if (iwc.isParameterSet(CLOSE)) {
			view(iwc);
		}
		else if (iwc.isParameterSet(EDIT)) {
			edit(iwc);
		}
		else {
			view(iwc);
		}
	}

	public void main(IWContext iwc) {
		_hasEdit = iwc.hasEditPermission(this);
		_iwb = iwc.getApplication().getBundle(IW_CORE_BUNDLE_IDENTIFIER);
		_iwrb = _iwb.getResourceBundle(iwc);

		_helpKey = iwc.getParameter(HELP_KEY);
		_helpBundle = iwc.getParameter(HELP_BUNDLE);
		
		if (_helpKey == null)
			add("No help key specified");
		else
			control(iwc);
	}

	public Text formatLabel(String s) {
		Text T = new Text();
		if (s != null) {
			T = new Text(s);
			T.setBold();
			T.setFontColor("#000000");
			T.setFontSize(Text.FONT_SIZE_10_HTML_2);
			T.setFontFace(Text.FONT_FACE_VERDANA);
		}
		return T;
	}
}