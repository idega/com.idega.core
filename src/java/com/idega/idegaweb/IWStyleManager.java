package com.idega.idegaweb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.idega.util.FileUtil;
import com.idega.util.text.TextSoap;

/**
 * <p>Title: IdegaWeb Style Manager</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega</p>
 * @author <a href="mailto:laddi@idega.is">Þórhallur "Laddi" Helgason</a>
 * @version 1.1
 */

public class IWStyleManager {

	private static IWStyleManager _instance = null;
	
	public IWStyleManager(IWMainApplication application) {
		this.application = application;
	}
	
	public IWStyleManager() {
		this(null);
	}
	
	public IWMainApplication application;
	public static Map map;
	public static File file;
	public static final String[] defaultStyles = { "A", "A:hover", "body", "table", "form", "img" };

	/**
	 * A method to get an instance of this class.
	 * 
	 * @return An instance of the IWStyleManager class.
	 */	
	public static IWStyleManager getInstance() {
		if (_instance == null)
			_instance = new IWStyleManager();
			
		return _instance;
	}

	public void getStyleSheet() {
		if ( application != null ) {
			String URL = application.getApplicationRealPath() + FileUtil.getFileSeparator() + "idegaweb" + FileUtil.getFileSeparator() + "style" + FileUtil.getFileSeparator() + "style.css";
			String lines = null;
	
			try {
				file = FileUtil.getFileAndCreateIfNotExists(URL);
			}
			catch (IOException e) {
				file = null;
			}
			
			try {
				lines = FileUtil.getStringFromFile(URL);
			}
			catch (IOException e) {
				lines = null;
			}

			if (lines != null) {
				getStylesFromFile(lines);
				addDefaultValues();
			}
		}
	}

	private void addDefaultValues() {
		if (!isStyleSet(defaultStyles[0])) {
			setStyle(defaultStyles[0], IWConstants.LINK_STYLE);
		}
		if (!isStyleSet(defaultStyles[1])) {
			setStyle(defaultStyles[1], IWConstants.LINK_HOVER_STYLE);
		}
		if (!isStyleSet(defaultStyles[2])) {
			setStyle(defaultStyles[2], IWConstants.BODY_STYLE);
		}
		if (!isStyleSet(defaultStyles[3])) {
			setStyle(defaultStyles[3], IWConstants.BODY_STYLE);
		}
		if (!isStyleSet(defaultStyles[4])) {
			setStyle(defaultStyles[4], IWConstants.FORM_STYLE);
		}
		if (!isStyleSet(defaultStyles[5])) {
			setStyle(defaultStyles[5], IWConstants.IMAGE_STYLE);
		}
		writeStyleSheet();
	}

	public void setStyle(String name, String style) {
		if ( name.length() > 0 ) {
			getStyleMap().put(name, style != null ? style : "");
			writeStyleSheet();
		}
	}
	
	public String getStyle(String name) {
		return (String) getStyleMap().get(name);
	}
	
	public void removeStyle(String name) {
		getStyleMap().remove(name);
		writeStyleSheet();	
	}
	
	public boolean isStyleSet(String name) {
		return getStyleMap().containsKey(name);	
	}

	private void getStylesFromFile(String lines) {
		StringTokenizer tokenizer = new StringTokenizer(lines, "}");
		while (tokenizer.hasMoreTokens()) {
			String style = tokenizer.nextToken();
			int index = style.indexOf("{");
			
			if (index != -1) {
				String styleName = style.substring(0, index).trim();
				styleName = TextSoap.findAndCut(styleName, ".");
				String styleParameter = "";
				
				if (style.indexOf(";") != -1) {
					StringTokenizer tokens = new StringTokenizer(style.substring(index + 1), ";");
					while (tokens.hasMoreTokens()) {
						String parameter = tokens.nextToken();
						index = parameter.indexOf(":");
						
						if (index != -1) {
							String parameterName = parameter.substring(0, index).trim();
							parameterName = TextSoap.findAndReplace(parameterName, "\n", "");
							parameterName = TextSoap.findAndReplace(parameterName, "\t", "");
							
							String parameterValue = parameter.substring(index + 1);
							parameterValue = TextSoap.findAndReplace(parameterValue, "\n", "");
							parameterValue = TextSoap.findAndReplace(parameterValue, "\t", "");
							
							String paramValue = "";
							StringTokenizer params = new StringTokenizer(parameterValue, " ");
							while (params.hasMoreTokens()) {
								String value = params.nextToken().trim();
								paramValue += value;
								if (params.hasMoreTokens()) {
									paramValue += " ";
								}
							}
							
							styleParameter += parameterName + ":" + paramValue + ";";
						}
					}
				}
				setStyle(styleName, styleParameter);
			}
		}
	}

	public void writeStyleSheet() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			Iterator iter = getStyleMap().keySet().iterator();
			while (iter.hasNext()) {
				String name = (String) iter.next();
				if ( !isDefaultStyle(name) && name.indexOf(".") == -1 ) {
					name = "." + name;
				}
				
				String style = getStyle(name);
				String writeString = name + " {";
				writer.write(writeString, 0, writeString.length());
				writer.newLine();
				
				if (style != null && style.length() > 0 ) {
					StringTokenizer tokens = new StringTokenizer(style, ";");
					while (tokens.hasMoreTokens()) {
						writeString = "\t" + tokens.nextToken() + ";";
						writer.write(writeString, 0, writeString.length());
						writer.newLine();
					}
				}

				writeString = "}";
				writer.write(writeString, 0, writeString.length());
				writer.newLine();
				if (iter.hasNext()) {
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private Map getStyleMap() {
		if ( map == null )
			map = new LinkedHashMap();	
		return map;
	}

	public List getStyleList() {
		List list = new Vector();
		if (map != null) {
			Iterator iter = getStyleMap().keySet().iterator();
			while (iter.hasNext()) {
				String name = (String) iter.next();
				if (name.length() > 0) {
					if (name.indexOf(".") != -1) {
						name = name.substring(name.indexOf(".") + 1);
					}

					list.add(name);
				}
			}
		}
		return list;
	}

	private boolean isDefaultStyle(String styleName) {
		for (int a = 0; a < defaultStyles.length; a++) {
			if (defaultStyles[a].equalsIgnoreCase(styleName))
				return true;
		}
		return false;
	}
}