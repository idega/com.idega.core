package com.idega.presentation;

import java.io.IOException;
public class Applet extends PresentationObject {
	private StringBuffer params = new StringBuffer();
	private boolean usePlugin = false;
	public Applet() {
		setName(this.getID());
		setWidth(0);
		setHeight(0);
	}
	public Applet(String appletClass) {
		setAppletClass(appletClass);
		setName(this.getID());
		setWidth(0);
		setHeight(0);
	}
	public Applet(String appletClass, String archive) {
		this(appletClass);
		setName(this.getID());
		setWidth(0);
		setHeight(0);
		setCodeArchive(archive);
	}
	public Applet(String appletClass, String codeBase, String archive) {
		this(appletClass);
		setName(this.getID());
		setWidth(0);
		setHeight(0);
		setCodebase(codeBase);
		setCodeArchive(archive);
	}
	public Applet(String appletClass, String archive, int width, int height) {
		this(appletClass);
		setAppletName(this.getID());
		setWidth(width);
		setHeight(height);
		setCodeArchive(archive);
	}
	public Applet(String appletClass, int width, int height) {
		this(appletClass);
		setAppletName(this.getID());
		setWidth(width);
		setHeight(height);
		setCodebase(".");
	}
	public Applet(String appletClass, String name, int width, int height, String codeBase) {
		this(appletClass);
		setAppletName(name);
		setWidth(width);
		setHeight(height);
		setCodebase(codeBase);
	}
	public Applet(String appletClass, int width, int height, String codeBase) {
		this(appletClass);
		setAppletName(this.getID());
		setWidth(width);
		setHeight(height);
		setCodebase(codeBase);
	}
	
	
	public void setParam(String name, String value) {
		params.append("<param name=\"");
		params.append(name);
		params.append("\" value=\"");
		params.append(value);
		params.append("\" >\n");
	}
	public StringBuffer getParams() {
		return params;
	}
	public void setWidth(int width) {
		setWidth(Integer.toString(width));
	}
	public void setWidth(String width) {
		setMarkupAttribute("width", width);
	}
	public String getWidth() {
		return getMarkupAttribute("width");
	}
	public void setHeight(int height) {
		setHeight(Integer.toString(height));
	}
	public void setHeight(String height) {
		setMarkupAttribute("height", height);
	}
	public String getHeight() {
		return getMarkupAttribute("height");
	}
	public void setBackgroundColor(String ColorStaticColorString) {
		setParam("BGCOLOR", ColorStaticColorString);
	}
	public void setCodebase(String CODEBASE) {
		setMarkupAttribute("CODEBASE", CODEBASE);
	}
	public String getCodebase() {
		return getMarkupAttribute("CODEBASE");
	}
	public void setCodeArchive(String ARCHIVE) {
		setMarkupAttribute("ARCHIVE", ARCHIVE);
	}
	public String getCodeArchive() {
		return getMarkupAttribute("ARCHIVE");
	}
	public void setAppletClass(String CODE) {
		setMarkupAttribute("CODE", CODE);
	}
	public String getAppletClass() {
		return getMarkupAttribute("CODE");
	}
	public void setAppletName(String NAME) {
		setMarkupAttribute("NAME", NAME);
	}
	public String getAppletName() {
		return getMarkupAttribute("NAME");
	}
	public void setAlignment(String ALIGN) {
		setMarkupAttribute("ALIGN", ALIGN);
	}
	public String getAlignment() {
		return getMarkupAttribute("ALIGN");
	}
	public void setHSpace(String HSPACE) {
		setMarkupAttribute("HSPACE", HSPACE);
	}
	public String getHSpace() {
		return getMarkupAttribute("HSPACE");
	}
	public void setVSpace(String VSPACE) {
		setMarkupAttribute("VSPACE", VSPACE);
	}
	public String getVSpace() {
		return getMarkupAttribute("VSPACE");
	}
	public void setAlt(String alt) {
		setMarkupAttribute("alt", alt);
	}
	public String getAlt() {
		return getMarkupAttribute("alt");
	}
	public void print(IWContext iwc) throws IOException {
		if (doPrint(iwc)) {
			if (getMarkupLanguage().equals("HTML")) {
				print("<APPLET");
				print(getMarkupAttributesString());
				print(" >\n");
				print(params.toString());
				if (getAlt() != null)
					print(getAlt());
				print("</APPLET>");
			}
		}
	}
}
