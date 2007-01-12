/*
 * $Id: HelpWindow.java,v 1.5.2.1 2007/01/12 19:31:57 idegaweb Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb.help.presentation;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.presentation.ICLocalePresentation;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.StyledIWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.texteditor.TextEditor;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.util.FileUtil;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLCDATA;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLOutput;
import com.idega.xml.XMLParser;

/**
 * This class does something very clever.....
 * 
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class HelpWindow extends StyledIWAdminWindow {
	private static final String BUNDLE_IDENTIFIER = "com.idega.block.help";
	public final static String HELP_KEY = Help.HELP_KEY;
	public final static String HELP_BUNDLE = Help.HELP_BUNDLE;
	private final static String EDIT_IMAGE = "edit.gif";
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
	private final static String CLOSE_WINDOW = "hlp_close_window";
	
	private final static String ERROR_NO_HELP_KEY = "hlp_err_no_key";
	private final static String ERROR_NO_BUNDLE = "hlp_err_no_bundle";
	
	//the style for a white table with gray border
	private String mainTableStyle = "borderAllWhite";
	
	private boolean _hasEdit = false;
	private IWBundle _iwb = null;
	private IWResourceBundle _iwrb = null;
	private String _helpKey = null;
	private String _helpBundle = null;

	private String _localizedTitle = null;
	private String _localizedHelpText = null;
	
	private String _previousLocale = null;

	private XMLDocument _doc = null;

	protected String _titleStyleAttribute = null;
	protected String _titleStyleClass = null;
	protected String _bodyStyleAttribute = null;
	protected String _bodyStyleClass = null;
	protected String _linkStyleAttribute = null;
	protected String _linkStyleClass = null;
	protected String _seeAlsoStyleAttribute = null;
	protected String _seeAlsoStyleClass = null;

	public HelpWindow() {
		super();
		setResizable(true);
		setScrollbar(true);
		setHeight(500);
		setWidth(500);
	}

	private void edit(IWContext iwc) {
	
		Form form = new Form();
		Table t = new Table(1, 10);
		t.setStyleClass(this.mainTableStyle);
		t.setCellpadding(4);
		t.setCellspacing(0);
		t.setWidth("97%");
		t.setHeight("97%");
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

		getHelpText(iwc, this._helpKey, this._helpBundle, loc);

		TextInput title = new TextInput(TITLE);
		title.setLength(40);
		title.setMaxlength(255);
		if (this._localizedTitle != null) {
			title.setContent(this._localizedTitle);
		}

		TextEditor editor = new TextEditor();
		editor.setInputName(BODY);
		if (this._localizedHelpText != null) {
			editor.setContent(this._localizedHelpText);
		}

		SubmitButton save = new SubmitButton(this._iwrb.getLocalizedImageButton(SAVE, "Save"), SAVE);
		SubmitButton close = new SubmitButton(this._iwrb.getLocalizedImageButton(CLOSE, "Close"), CLOSE);

		Text titleLabel = new Text(this._iwrb.getLocalizedString(TITLE, "Title"));
		Text localeLabel = new Text(this._iwrb.getLocalizedString(LOCALE, "Locale"));
		Text bodyLabel = new Text(this._iwrb.getLocalizedString(BODY, "Help text"));
		
		if (this._titleStyleAttribute != null) {
			titleLabel.setStyleAttribute(this._titleStyleAttribute);
			localeLabel.setStyleAttribute(this._titleStyleAttribute);
			bodyLabel.setStyleAttribute(this._titleStyleAttribute);
		}		
		else if (this._titleStyleClass != null) {
			titleLabel.setStyleClass(this._titleStyleClass);
			localeLabel.setStyleClass(this._titleStyleClass);
			bodyLabel.setStyleClass(this._titleStyleClass);
		}

		int row = 1;
		t.add(titleLabel, 1, row++);
		t.add(title, 1, row++);
		row++;
		t.add(localeLabel, 1, row++);
		t.add(localeDrop, 1, row++);
		row++;
		t.add(bodyLabel, 1, row++);
		t.add(editor, 1, row++);
		row++; 
		t.setAlignment(1,row,"right");
		t.add(save, 1, row);
		t.add(Text.NON_BREAKING_SPACE, 1, row);
		t.add(close, 1, row);

		form.add(t);
		form.add(new HiddenInput(EDIT, "true"));
		form.add(new HiddenInput(HELP_KEY, this._helpKey));
		form.add(new HiddenInput(HELP_BUNDLE, this._helpBundle));
		add(form,iwc);
	}

	private void view(IWContext iwc) {

		Table t = new Table();
		t.setStyleClass(this.mainTableStyle);
		t.setWidth("97%");
//		t.setHeight(400);
		CloseButton close = new CloseButton(this._iwrb.getLocalizedImageButton(CLOSE_WINDOW, "Close"));
		int row = 1;
		if (this._hasEdit) {
			t.resize(1, 5);

			Link change = new Link();
			change.setImage(this._iwb.getImage(EDIT_IMAGE));
			change.setParameter(EDIT, "true");
			change.setParameter(HELP_KEY, this._helpKey);
			change.setParameter(HELP_BUNDLE,this._helpBundle);
			t.add(change, 1, row);
			row = 3;
		}
		else {
			t.resize(1, 3);
		}

    String localeIdString = iwc.getParameter(LOCALE);
    Locale loc = null;
    if (localeIdString != null) {
			loc = ICLocaleBusiness.getLocale(Integer.parseInt(localeIdString));
    } 
    else {    	
    	loc = iwc.getCurrentLocale();
    }

		getHelpText(iwc, this._helpKey, this._helpBundle, loc);

		Text title = null;
		if (this._localizedTitle != null) {
			title = new Text(this._localizedTitle);
			
			if (this._titleStyleAttribute != null) {
				title.setStyleAttribute(this._titleStyleAttribute);
			}
			else if (this._titleStyleClass != null) {
				title.setStyleClass(this._titleStyleClass);
			}
		}

		t.add(title, 1, row++);
		row++;
		
		Text body = null;
		if (this._localizedHelpText != null) {
			body = new Text(this._localizedHelpText);
			
			if (this._bodyStyleAttribute != null) {
				body.setStyleAttribute(this._bodyStyleAttribute);
			}
			else if (this._bodyStyleClass != null) {
				body.setStyleClass(this._bodyStyleClass);
			}
		}
						
		t.add(this._localizedHelpText, 1, row);
		t.add(close,1,++row);
		t.setAlignment(1,row,"right");

		add(t,iwc);
	}

	private void putHelpText(IWContext iwc, String helpKey, String bundle, Locale loc, String title, String body) { 
		if (this._doc == null) {
			loadHelpText(iwc,helpKey,bundle,loc);
		}
			
		XMLElement root = this._doc.getRootElement();
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

		XMLElement titleElement = help.getChild(XML_TITLE);
		if (titleElement == null) {
			titleElement = new XMLElement(XML_TITLE);
			help.addContent(titleElement);
		}

		if (title != null) {
			titleElement.setText(title);
		}
			
		XMLCDATA cdata = help.getXMLCDATAContent();
		if (cdata != null) {
			cdata.setText(body);
		}
		else {
			cdata = new XMLCDATA(body);
			help.addContent(cdata);
		}
	}

	private void saveHelpText(IWContext iwc, String helpKey, String bundle, Locale loc) {
		try {
			IWBundle iwb = null;
			if (bundle == null) {
				iwb = iwc.getIWMainApplication().getBundle(HELP_BUNDLE);
			}
			else {
				iwb = iwc.getIWMainApplication().getBundle(bundle);
			}

			StringBuffer fileName = new StringBuffer(iwb.getResourcesRealPath(loc));
			if (!fileName.toString().endsWith(FileUtil.getFileSeparator())) {
				fileName.append(FileUtil.getFileSeparator());
			}

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
		
			if (this._doc == null) {
				this._doc = new XMLDocument(new XMLElement(XML_ROOT));
			}

			XMLOutput output = new XMLOutput("  ", true);
			output.setLineSeparator(System.getProperty("line.separator"));
			output.setTextNormalize(true);
			output.setEncoding("UTF-16");
			output.output(this._doc, out);

			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getHelpText(IWContext iwc, String helpKey, String bundle, Locale loc) {
		this._localizedTitle = null;
		this._localizedHelpText = null;

		if (this._doc == null) {
			loadHelpText(iwc,helpKey,bundle,loc);
		}

		XMLElement root = this._doc.getRootElement();
		if (root == null) {
			return;
		}
			
		XMLElement help = root.getChild(XML_HELP);
		if (help == null) {
			return;
		}
			
		XMLAttribute id = help.getAttribute(XML_ID);
		if (id == null || !id.getValue().equals(helpKey)) {
			return;
		}

		XMLElement title = help.getChild(XML_TITLE);
		if (title != null) {
			this._localizedTitle = title.getTextTrim();
		}
			
		XMLCDATA cdata = help.getXMLCDATAContent();
		if (cdata != null) {
			this._localizedHelpText = cdata.getText();
		}		
	}

	private void loadHelpText(IWContext iwc, String helpKey, String bundle, Locale loc) {
		try {
			IWBundle iwb = null;
			if (bundle == null) {
				iwb = iwc.getIWMainApplication().getBundle(HELP_BUNDLE);
			}
			else {
				iwb = iwc.getIWMainApplication().getBundle(bundle);
			}
				
			XMLParser parser = new XMLParser(false);
			StringBuffer fileName = new StringBuffer(iwb.getResourcesRealPath(loc));
			if (!fileName.toString().endsWith(FileUtil.getFileSeparator())) {
				fileName.append(FileUtil.getFileSeparator());
			}

			fileName.append(XML_FOLDER);
			File file = new File(fileName.toString());
			file.mkdir();

			fileName.append(FileUtil.getFileSeparator());
			fileName.append(HELP_FILE_PREFIX);
			fileName.append(helpKey);
			fileName.append(XML_EXTENSION);
			
			file = new File(fileName.toString());
			file.createNewFile();

			this._doc = parser.parse(file);
		}
		catch (Exception e) {
			this._doc = new XMLDocument(new XMLElement(XML_ROOT));
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
			putHelpText(iwc,this._helpKey,this._helpBundle,loc,title,body);
			saveHelpText(iwc,this._helpKey,this._helpBundle,loc);
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
		this._hasEdit = iwc.hasEditPermission(this);
		this._iwb = iwc.getIWMainApplication().getBundle(BUNDLE_IDENTIFIER);
		this._iwrb = this._iwb.getResourceBundle(iwc);

		this._helpKey = iwc.getParameter(HELP_KEY);
		this._helpBundle = iwc.getParameter(HELP_BUNDLE);
		
		if (this._helpKey == null) {
			add(this._iwrb.getLocalizedString(ERROR_NO_HELP_KEY,"No help key specified"));
		}
		else if (this._helpBundle == null) {
			add(this._iwrb.getLocalizedString(ERROR_NO_BUNDLE,"No bundle for help text specified"));
		}
		else {
			control(iwc);
		}
	}

	public void setTitleStyleAttribute(String styleAttribute) {
		this._titleStyleAttribute = styleAttribute;
	}
	
	public void setTitleStyleClass(String styleClass) {
		this._titleStyleClass = styleClass;	
	}
	
	public void setBodyStyleAttribute(String styleAttribute) {
		this._bodyStyleAttribute = styleAttribute;		
	}
	
	public void setBodyStyleClass(String styleClass) {
		this._bodyStyleClass = styleClass;			
	}
	
	public void setLinkStyleAttribute(String styleAttribute) {
		this._linkStyleAttribute = styleAttribute;
	}
	
	public void setLinkStyleClass(String styleClass) {
		this._linkStyleClass = styleClass;			
	}
	
	public void setSeeAlsoStyleAttribute(String styleAttribute) {
		this._seeAlsoStyleAttribute = styleAttribute;		
	}
	
	public void setSeeAlsoStyleClass(String styleClass) {
		this._seeAlsoStyleClass = styleClass;			
	}
}