package com.idega.idegaweb;

import com.idega.presentation.ui.DropdownMenu;
import java.io.*;
import java.util.*;

import com.idega.idegaweb.IWMainApplication;
import com.idega.util.FileUtil;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class IWStyleManager {

public static HashMap map;
public static File file;
public static final String[] defaultStyles = { "A","body","table" };

  public static void getStyleSheet(IWMainApplication app) {
    boolean createNew = false;
    String URL = app.getApplicationRealPath()+"style"+FileUtil.getFileSeparator()+"style.css";
    Vector vector = null;

    try {
      file = FileUtil.getFileAndCreateRecursiveIfNotExists(URL);
    }
    catch (IOException e) {
      e.printStackTrace(System.err);
    }

    if ( file != null ) {
      try {
	vector = FileUtil.getLinesFromFile(file);
      }
      catch (IOException e) {
	vector = null;
      }

      if ( vector != null ) {
	if ( vector.size() == 0 ) {
	  addDefaultValues();
	}
	else {
	  getStylesFromFile(vector);
	}
      }
    }
  }

  public static void addDefaultValues() {
    addStyleParameter(defaultStyles[0],IWConstants.LINK_STYLE);
    addStyleParameter(defaultStyles[0]+":hover",IWConstants.LINK_HOVER_STYLE);
    addStyleParameter(defaultStyles[1],IWConstants.BODY_STYLE);
    addStyleParameter(defaultStyles[2],IWConstants.BODY_STYLE);
    writeStyleSheet();
  }

  public static void addStyleParameter(String name,String style) {
    if ( map == null )
      map = new HashMap();

    map.put(name,style);
  }

  public static void getStylesFromFile(Vector vector) {
    Iterator iter = vector.iterator();
    StringTokenizer tokenizer;
    while (iter.hasNext()) {
      tokenizer = new StringTokenizer((String) iter.next(),"{");
      while ( tokenizer.hasMoreTokens() ) {
        /** @todo  not working in jdk 1.3.1 : */
	//addStyleParameter(tokenizer.nextToken().replaceAll(" ",""),tokenizer.nextToken().replaceAll("}","").replaceAll(" ",""));
      }
    }
  }

  public static void writeStyleSheet() {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      Iterator iter = map.keySet().iterator();
      while (iter.hasNext()) {
	String name = (String) iter.next();
	String style = (String) map.get(name);
	int length = name.length() + style.length() + 5;

	writer.write(name+" { "+style+" }",0,length);
	writer.newLine();
      }
      writer.flush();
      writer.close();
    }
    catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }

  public static HashMap getStyleMap() {
    return map;
  }

  public static DropdownMenu getStyleDropdown(String parameterName) {
    DropdownMenu menu = new DropdownMenu(parameterName);

    if ( map != null ) {
      Iterator iter = map.keySet().iterator();
      while (iter.hasNext()) {
	String name = (String) iter.next();
	String value = name;

	if ( name.length() > 0 && name.indexOf(":") == -1 ) {
	  if ( name.indexOf(".") != -1 ) {
	    name = name.substring(name.indexOf(".")+1);
	  }
	  if ( value.indexOf(".") != -1 && value.indexOf(".") == 0 ) {
	    value = value.substring(value.indexOf(".")+1);
	  }

	  if ( !isDefaultStyle(name) )
	    menu.addMenuElement(name,value);
	}
      }
    }

    return menu;
  }

  public static boolean isDefaultStyle(String styleName) {
    for ( int a = 0; a < defaultStyles.length; a++ ) {
      if ( defaultStyles[a].equalsIgnoreCase(styleName) )
	return true;
    }
    return false;
  }
}