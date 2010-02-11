package com.idega.idegaweb.presentation;

import javax.faces.component.UIComponent;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.help.presentation.Help;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.Window;
import com.idega.user.util.ICUserConstants;

public class IWAdminWindow extends Window {

	public static final String MENU_COLOR = "#EFEFEF";
	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.core";
	public final static String STYLE = "font-family:arial; font-size:8pt; color:#000000; text-align: justify; border: 1 solid #000000;";
	public final static String STYLE_2 = "font-family:arial; font-size:8pt; color:#000000; text-align: justify;";

	private IWBundle iwb;
	public IWBundle iwbCore;
	private Form adminForm;
	private Table adminTable;
	private Table headerTable;
	private Table leftTable;
	private Table rightTable;
	private boolean merged = true;
	private boolean displayEmpty = false;
	private String styleScript = "DefaultStyle.css";
	private boolean useStyleSheetFromCoreBundle = true;

	private String rightWidth = "160";
	private String method = "post";
	private int _cellPadding = 0;
	
	public static String HEADER_COLOR = "#38485C";

	public IWAdminWindow() {
		super();
	}

	public IWAdminWindow(String name) {
		super(name);
	}

	public IWAdminWindow(int width, int heigth) {
		super(width, heigth);
	}

	public IWAdminWindow(String name, int width, int height) {
		super(name, width, height);
	}

	public IWAdminWindow(String name, String url) {
		super(name, url);
	}

	public IWAdminWindow(String name, int width, int height, String url) {
		super(name, width, height, url);
	}

	public IWAdminWindow(String name, String classToInstanciate, String template) {
		super(name, classToInstanciate, template);
	}

	/*
	 * public IWAdminWindow(String name,Class classToInstanciate,Class template){ super(name,classToInstanciate,template); }
	 */
	public IWAdminWindow(String name, Class classToInstanciate) {
		super(name, classToInstanciate);
	}

	public Form getUnderlyingForm() {
		return this.adminForm;
	}

	public void _main(IWContext iwc) throws Exception {
		this.iwb = getBundle(iwc);
		this.iwbCore = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
		if (!this.displayEmpty) {
			makeTables();
			setAllMargins(0);
			String prop = this.iwbCore.getProperty("adminHeaderColor");
			if(prop!=null){
				HEADER_COLOR=prop;
			}

			if (this.merged) {
				super.add(this.adminTable);
			}
			else {
				super.add(this.adminForm);
			}
		}

		super._main(iwc);
		//add the stylesheet path and filename of the chosen stylesheet to the page
		this.setStyleSheetURL(getStyleSheetPath(iwc) + this.styleScript);
	}

	public void main(IWContext iwc) throws Exception {
	}

	private void makeTables() {
		this.adminForm = new Form();
		this.adminForm.setMethod(this.method);

		this.adminTable = new Table(2, 2);
		this.adminTable.mergeCells(1, 1, 2, 1);
		this.adminTable.setCellpadding(0);
		this.adminTable.setCellspacing(0);
		this.adminTable.setWidth("100%");
		this.adminTable.setHeight("100%");
		this.adminTable.setHeight(2, "100%");
		this.adminTable.setColor(1, 1, HEADER_COLOR);
		//adminTable.setColor(1, 2, "#FFFFFF");
		if (!this.merged) {
			this.adminTable.setColor(2, 2, MENU_COLOR);
			this.adminTable.setWidth(2, 2, this.rightWidth);
		}
		else {
			this.adminTable.mergeCells(1, 2, 2, 2);
		}
		this.adminTable.setRowVerticalAlignment(2, "top");
		this.adminForm.add(this.adminTable);

		this.headerTable = new Table();
		this.headerTable.setStyleClass("top");
		this.headerTable.setCellpadding(0);
		this.headerTable.setCellspacing(0);
		this.headerTable.setWidth("100%");
		this.headerTable.setAlignment(2, 1, "right");
		//      Image idegaweb = iwbCore.getImage("/editorwindow/idegaweb.gif","idegaWeb");
		//      headerTable.add(idegaweb,1,1);
		this.headerTable.setStyleClass(1, 1, "top_left");
		this.adminTable.add(this.headerTable, 1, 1);

		this.leftTable = new Table();
		this.leftTable.setCellpadding(this._cellPadding);
		this.leftTable.setWidth("100%");
		if (this.merged) {
			this.leftTable.setHeight("100%");
			this.leftTable.setCellspacing(0);
			this.leftTable.setVerticalAlignment(1, 1, "top");
		}
		this.adminTable.setAlignment(1, 2, "center");
		this.adminTable.add(this.leftTable, 1, 2);

		this.rightTable = new Table();
		this.rightTable.setCellpadding(8);
		this.rightTable.setWidth("100%");
		if (!this.merged) {
			this.adminTable.setAlignment(2, 2, "center");
			this.adminTable.add(this.rightTable, 2, 2);
		}
	}

	public void addBottom(String text) {
		this.adminTable.add(text, 1, 2);
	}

	public void add(PresentationObject pObject) {
		this.add((UIComponent)pObject);
	}	
	
	public void add(UIComponent obj) {
		if (!this.displayEmpty) {
			if (this.adminTable == null) {
				makeTables();
				super.add(this.adminTable);
			}
			this.leftTable.add(obj, 1, 1);
		}
		else {
			super.add(obj);
		}

	}

	public void addBottom(PresentationObject obj) {
		this.adminTable.add(obj, 1, 2);
	}

	public void addLeft(String text) {
		int rows = this.leftTable.getRows();
		if (!this.leftTable.isEmpty(1, rows)) {
			rows++;
		}

		this.leftTable.add(formatText(text), 1, rows);
	}

	public void addLeft(PresentationObject obj) {
		addLeft(obj, true);
	}

	public void addLeft(PresentationObject obj, boolean useStyle) {
		int rows = this.leftTable.getRows();
		if (!this.leftTable.isEmpty(1, rows)) {
			rows++;
		}

		if (useStyle) {
			setStyle(obj);
		}

		this.leftTable.add(obj, 1, rows);
	}

	public void addLeft(String text, PresentationObject obj, boolean hasBreak) {
		addLeft(text, obj, hasBreak, true);
	}

	public void addLeft(String text, PresentationObject obj, boolean hasBreak, boolean useStyle) {
		int rows = this.leftTable.getRows();
		if (!this.leftTable.isEmpty(1, rows)) {
			rows++;
		}

		if (useStyle) {
			setStyle(obj);
		}

		this.leftTable.add(formatText(text), 1, rows);
		if (hasBreak) {
			this.leftTable.add(Text.getBreak(), 1, rows);
		}
		this.leftTable.add(obj, 1, rows);
	}
	
	public void addInverseLeft(String text, PresentationObject obj, boolean hasBreak) {
		addInverseLeft(text, obj, hasBreak, false);
	}

	public void addInverseLeft(String text, PresentationObject obj, boolean hasBreak, boolean useStyle) {
		int rows = this.leftTable.getRows();
		if (!this.leftTable.isEmpty(1, rows)) {
			rows++;
		}

		if (useStyle) {
			setStyle(obj);
		}

		this.leftTable.add(obj, 1, rows);
		if (hasBreak) {
			this.leftTable.add(Text.getBreak(), 1, rows);
		}
		this.leftTable.add(text, 1, rows);
	}

	public void addLeft(String headline, String text) {
		int rows = this.leftTable.getRows();
		if (!this.leftTable.isEmpty(1, rows)) {
			rows++;
		}

		this.leftTable.add(formatHeadline(headline), 1, rows);
		this.leftTable.add(Text.getBreak(), 1, rows);
		this.leftTable.add(Text.getBreak(), 1, rows);
		this.leftTable.add(formatText(text, false), 1, rows);
	}

	public void addRight(String text) {
		int rows = this.rightTable.getRows();
		if (!this.rightTable.isEmpty(1, rows)) {
			rows++;
		}

		this.rightTable.add(formatText(text), 1, rows);
	}

	public void addRight(String text, PresentationObject obj, boolean hasBreak) {
		addRight(text, obj, hasBreak, true);
	}

	public void addRight(String text, PresentationObject obj, boolean hasBreak, boolean useStyle) {
		int rows = this.rightTable.getRows();
		if (!this.rightTable.isEmpty(1, rows)) {
			rows++;
		}

		if (useStyle) {
			setStyle(obj);
		}

		this.rightTable.add(formatText(text), 1, rows);
		if (hasBreak) {
			this.rightTable.add(Text.getBreak(), 1, rows);
		}
		this.rightTable.add(obj, 1, rows);
	}

	public void addSubmitButton(InterfaceObject obj) {
		int rows = this.rightTable.getRows();
		String height = this.rightTable.getHeight();

		if (height == null) {
			rows++;
			this.rightTable.setHeight("100%");
			this.rightTable.setHeight(1, rows, "100%");
			this.rightTable.setVerticalAlignment(1, rows, "bottom");
			this.rightTable.setAlignment(1, rows, "center");
		}

		if (!this.rightTable.isEmpty(1, rows)) {
			this.rightTable.add(Text.getNonBrakingSpace(), 1, rows);
		}
		this.rightTable.add(obj, 1, rows);
	}

	public void addHiddenInput(HiddenInput obj) {
		this.adminForm.add(obj);
	}

	public void addTitle(String title) {
		Text adminTitle = new Text(title + "&nbsp;&nbsp;");
		adminTitle.setBold();
		adminTitle.setFontColor("#FFFFFF");
		adminTitle.setFontSize("3");
		adminTitle.setFontFace(Text.FONT_FACE_ARIAL);

		super.setTitle(title);

		this.headerTable.add(adminTitle, 2, 1);
	}

	public void addTitle(String title, String style) {
		Text adminTitle = new Text(title + "&nbsp;&nbsp;");
		adminTitle.setFontStyle(style);

		super.setTitle(title);

		this.headerTable.add(adminTitle, 2, 1);
	}

	public void addHeaderObject(PresentationObject obj) {
		int rows = this.headerTable.getRows() + 1;
		this.headerTable.mergeCells(1, rows, 2, rows);
		this.headerTable.setAlignment(1, rows, "center");

		this.headerTable.add(obj, 1, rows);
	}

	public Text formatText(String s, boolean bold) {
		Text T = new Text();
		if (s != null) {
			T = new Text(s);
			if (bold) {
				T.setBold();
			}
			T.setFontColor("#000000");
			T.setFontSize(Text.FONT_SIZE_7_HTML_1);
			T.setFontFace(Text.FONT_FACE_VERDANA);
		}
		return T;
	}

	public void formatText(Text text, boolean bold) {
		if (bold) {
			text.setBold();
		}
		text.setFontColor("#000000");
		text.setFontSize(Text.FONT_SIZE_7_HTML_1);
		text.setFontFace(Text.FONT_FACE_VERDANA);
	}

	public Text formatText(String s) {
		Text T = formatText(s, true);
		return T;
	}

	public Text formatHeadline(String s) {
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

	public void setStyle(PresentationObject obj) {
		if (obj instanceof Text) {
			this.setStyle((Text) obj);
		}
		else {
			obj.setMarkupAttribute("style", STYLE);
		}
	}

	public void setStyle(Text obj) {
		obj.setMarkupAttribute("style", STYLE_2);
	}

	public void setEmpty() {
		this.displayEmpty = true;
	}

	public void setStyle(PresentationObject obj, String style) {
		obj.setMarkupAttribute("style", style);
	}

	public void setUnMerged() {
		this.merged = false;
		this._cellPadding = 8;
	}

	public void setRightWidth(int rightWidth) {
		this.rightWidth = Integer.toString(rightWidth);
	}

	public void setRightWidth(String rightWidth) {
		this.rightWidth = rightWidth;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setCellpadding(int padding) {
		this._cellPadding = padding;
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	/**
	 * This method depends on iwbCore and iwb to be initialized
	 * 
	 * @return the path of the chosen stylesheet
	 */
	private String getStyleSheetPath(IWContext iwc) {
		IWProperty styleSheet = null;
		String styleSrc = null;
		if (this.useStyleSheetFromCoreBundle) {
			styleSheet = iwc.getIWMainApplication().getSystemProperties().getIWProperty(IW_BUNDLE_IDENTIFIER + ".editorwindow_styleSheet_path");
			if (styleSheet == null) {
				styleSrc = this.iwbCore.getVirtualPath() + "/editorwindow/";
				iwc.getIWMainApplication().getSystemProperties().getNewProperty().setProperty(IW_BUNDLE_IDENTIFIER + ".editorwindow_styleSheet_path", styleSrc, false, false);
			}
			else {
				styleSrc = styleSheet.getValue();
			}
		}
		else {
			styleSheet = iwc.getIWMainApplication().getSystemProperties().getIWProperty(getBundleIdentifier() + ".editorwindow_styleSheet_name");
			if (styleSheet == null) {
				styleSrc = this.iwb.getVirtualPath() + "/editorwindow/";
				iwc.getIWMainApplication().getSystemProperties().getNewProperty().setProperty(getBundleIdentifier() + ".editorwindow_styleSheet_name", styleSrc, false, false);
			}
			else {
				styleSrc = styleSheet.getValue();
			}
		}
		return styleSrc;
	}

	public void setStyleScript(String styleScriptName) {
		this.styleScript = styleScriptName;
	}

	/**
	 * @param value
	 *          if false it uses the current bundle of the extended class. Default value is true.
	 */
	public void setToUseStyleSheetFromCoreBundle(boolean value) {
		this.useStyleSheetFromCoreBundle = value;
	}
	public static Help getHelp(String helpTextKey) {
		IWContext iwc = IWContext.getInstance();
		IWBundle iwb = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
	 	Help help = new Help();
	 	Image helpImage = iwb.getImage("help.gif");//.setSrc("/idegaweb/bundles/com.idega.user.bundle/resources/help.gif");
 	  help.setHelpTextBundle( ICUserConstants.HELP_BUNDLE_IDENTFIER);
	  help.setHelpTextKey(helpTextKey);
	  help.setImage(helpImage);
	  return help;
	}

}