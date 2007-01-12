package com.idega.presentation.ui;

import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;

public class HTMLArea extends Block {

	public static final String DEFAULT_HIDDEN_TEXTEDITOR_INPUT_NAME = "IWHTMLArea";

	private String width = "100%";

	private String height = "100%";

	private String color = "white";

	private int cols = 50;

	private int rows = 50;

	private boolean menues = true;

	private String text = "";

	private String inputName = DEFAULT_HIDDEN_TEXTEDITOR_INPUT_NAME;

	boolean fullHTMLPageSupport = false;

	boolean fullScreen = false;

	public HTMLArea() {
	}

	public HTMLArea(String text) {
		this();
		this.text = text;
	}

	public HTMLArea(String inputName, String text) {
		this(text);
		this.inputName = inputName;
	}

	public HTMLArea(String text, String width, String height) {
		this(text);
		this.width = width;
		this.height = height;
	}

	public HTMLArea(String inputName, String text, String width, String height) {
		this(text, width, height);
		this.inputName = inputName;
	}

	public void main(IWContext iwc) {
		String rootFolder = this.getBundle(iwc).getResourcesVirtualPath() + "/htmlarea/";
		Page parent = getParentPage();
		//style
		parent.addStyleSheetURL(rootFolder + "htmlarea.css");
		//javascript
		parent.setOnLoad("initEditor();");
		//  parent.getAssociatedScript().addVariable("_editor_url","\""+rootFolder+"\"");
		// parent.getAssociatedScript().addVariable("_editor_lang","\"en\"");
		//      parent.getAssociatedScript().addScriptSource(rootFolder+"htmlarea.js");
		//      parent.getAssociatedScript().addScriptSource(rootFolder+"dialog.js");
		//      parent.getAssociatedScript().addScriptSource(rootFolder+"lang/en.js");
		StringBuffer variables = new StringBuffer();
		variables.append("_editor_url = \"").append(rootFolder).append("\"").append(";\n").append(
				"_editor_lang = \"en\";");
		StringBuffer loadPlugins = new StringBuffer();
		loadPlugins.append("HTMLArea.loadPlugin(\"TableOperations\");\n").append(
				"HTMLArea.loadPlugin(\"SpellChecker\");\n");
		if (hasFullHTMLPageSupport()) {
			loadPlugins.append("HTMLArea.loadPlugin(\"FullPage\");\n");
		}
		loadPlugins.append("HTMLArea.loadPlugin(\"CSS\");\n").append("HTMLArea.loadPlugin(\"ContextMenu\");\n");
		StringBuffer initEditorScript = new StringBuffer();
		initEditorScript.append(" var editor = null;\n").append("function initEditor() {\n").append(
				"// create an editor for the \"" + this.inputName + "\" textbox\n").append(
				"editor = new HTMLArea('" + this.inputName + "');\n");
		if (hasFullHTMLPageSupport()) {
			initEditorScript.append("// register the FullPage plugin\n").append("editor.registerPlugin(FullPage);\n");
		}
		initEditorScript.append("// register the TableOperations plugin\n").append(
				"editor.registerPlugin(TableOperations);\n").append("// register the SpellChecker plugin\n").append(
				"editor.registerPlugin(SpellChecker);\n").append("// add a contextual menu\n").append(
				"editor.registerPlugin(ContextMenu);\n").append("editor.generate();").append("}\n");
		parent.addJavaScriptBeforeJavaScriptURLs("htmlAreaInitialVariables", variables.toString());
		parent.addJavascriptURL(rootFolder + "htmlarea.js");
		parent.addJavascriptURL(rootFolder + "dialog.js");
		parent.addJavascriptURL(rootFolder + "lang/en.js");
		parent.addJavaScriptAfterJavaScriptURLs("htmlAreaLoadPlugins", loadPlugins.toString());
		parent.addJavaScriptAfterJavaScriptURLs("htmlAreainitEditorMethod", initEditorScript.toString());
		
		TextArea area = new TextArea(this.inputName);
		area.setRows(this.rows);
		area.setID(this.inputName);
		area.setContent(this.text);
		area.setStyleAttribute("width", this.width);
		area.setStyleAttribute("heigth", this.height);
		add(area);
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setBackgroundColor(String color) {
		this.color = color;
	}

	public void setShowMenues(boolean menues) {
		this.menues = menues;
	}

	public void setInputName(String name) {
		this.inputName = name;
	}

	public void setContent(String text) {
		this.text = text;
	}

	public String getMarkupLanguage(IWContext iwc) {
		if (iwc.isIE() && (!iwc.isMacOS()) && (!iwc.isOpera())) {//IE5.5,windows
																 // and not
																 // Opera
																 // (faking as
																 // IE)
			return "HTML";
		}
		else {
			return null;
		}
	}

	/**
	 * @return Returns the number rows of rows for textarea shown if client does
	 *         not handle editor.
	 */
	public int getRows() {
		return this.rows;
	}

	/**
	 * @param rows
	 *            The number of rows of textarea to show if client does not
	 *            handle editor.
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @return Returns the number of columns for textarea to show if client does
	 *         not handle editor.
	 */
	public int getColumns() {
		return this.cols;
	}

	/**
	 * @param cols
	 *            The number of textarea columns to show if client does not
	 *            handle editor .
	 */
	public void setColumns(int cols) {
		this.cols = cols;
	}

	/**
	 * @return Returns true if this editor can handle a full html page
	 */
	public boolean hasFullHTMLPageSupport() {
		return this.fullHTMLPageSupport;
	}

	/**
	 * Set to true if the editor should be able to edit page parameters
	 * 
	 * @param fullHTMLPageSupport
	 */
	public void setFullHTMLPageSupport(boolean fullHTMLPageSupport) {
		this.fullHTMLPageSupport = fullHTMLPageSupport;
	}

	/**
	 * @return Returns the fullScreen.
	 */
	public boolean isFullScreen() {
		return this.fullScreen;
	}

	/**
	 * Set to the page will only contain the editor (inserts javascript)
	 * 
	 * @param fullScreen
	 */
	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
	}
}