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
	public static final String[] defaultStyles = { "A", "A:hover", "body", "table", "form" };

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
			Vector vector = null;
	
			try {
				file = FileUtil.getFileAndCreateRecursiveIfNotExists(URL);
			}
			catch (IOException e) {
				e.printStackTrace(System.err);
			}
	
			if (file != null) {
				try {
					vector = FileUtil.getLinesFromFile(file);
				}
				catch (IOException e) {
					vector = null;
				}
	
				if (vector != null) {
					if (vector.size() == 0) {
						addDefaultValues();
					}
					else {
						getStylesFromFile(vector);
					}
				}
			}
		}
	}

	private void addDefaultValues() {
		setStyle(defaultStyles[0], IWConstants.LINK_STYLE);
		setStyle(defaultStyles[1], IWConstants.LINK_HOVER_STYLE);
		setStyle(defaultStyles[2], IWConstants.BODY_STYLE);
		setStyle(defaultStyles[3], IWConstants.BODY_STYLE);
		setStyle(defaultStyles[4], IWConstants.FORM_STYLE);
		writeStyleSheet();
	}

	public void setStyle(String name, String style) {
		if ( name.length() > 0 ) {
			getStyleMap().put(name, style);
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

	private void getStylesFromFile(Vector vector) {
		Iterator iter = vector.iterator();
		StringTokenizer tokenizer;
		while (iter.hasNext()) {
			tokenizer = new StringTokenizer((String) iter.next(), "{");
			while (tokenizer.hasMoreTokens()) {
				/*String styleName = TextSoap.findAndReplace(tokenizer.nextToken(), " ", "");
				styleName = TextSoap.findAndReplace(styleName, ".", "");
				String styleParameter = TextSoap.findAndReplace(tokenizer.nextToken(), "}", "");
				styleParameter = TextSoap.findAndReplace(styleParameter, " ", "");
				setStyle(styleName, styleParameter);*/

				String styleName = TextSoap.findAndReplace(tokenizer.nextToken(), ".", "");
				String styleParameter = TextSoap.findAndReplace(tokenizer.nextToken(), "}", "");
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
				String style = getStyle(name);
				int length = name.length() + style.length() + 2;
				String writeString = name + "{" + style + "}";
				if ( !isDefaultStyle(name) && name.indexOf(".") == -1 ) {
					writeString = "." + writeString; 
					length++;
				}

				writer.write(writeString, 0, length);
				writer.newLine();
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