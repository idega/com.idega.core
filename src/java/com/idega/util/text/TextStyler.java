package com.idega.util.text;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Iterator;
import com.idega.util.text.StyleConstants;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;


public class TextStyler {

private HashMap _styleMap;
private String _styleString;
private String[] _styles = StyleConstants.ALL_STYLES;

public TextStyler() {
  setDefaultValues();
  setMapStyles();
}

public TextStyler(String styleString) {
  this();
  _styleString = styleString;
  setMapStyles();
}

  public String getStyleString() {
    Iterator iter = _styleMap.keySet().iterator();
    String attribute;
    String value;
    String styleString = "";
    while (iter.hasNext()) {
      attribute = (String) iter.next();
      value = (String) _styleMap.get(attribute);
      if ( value != null ) {
        styleString += attribute + StyleConstants.DELIMITER_COLON + value + StyleConstants.DELIMITER_SEMICOLON;
      }
    }
    return styleString;
  }

  private void setMapStyles() {
    if ( _styleString != null ) {
      StringTokenizer tokens = new StringTokenizer(_styleString,";");
      int a = -1;
      String attribute;
      String value;

      while (tokens.hasMoreTokens()) {
        StringTokenizer tokens2 = new StringTokenizer(tokens.nextToken(),":");

        a = 1;
        attribute = null;
        value = null;

        while (tokens2.hasMoreTokens()) {
          if ( a == 1 ) {
            attribute = tokens2.nextToken();
            a++;
          }
          else if ( a == 2 )
            value = tokens2.nextToken();
        }
        _styleMap.put(attribute,value);
      }
    }
  }

  private void setDefaultValues() {
    if ( _styleMap == null )
      _styleMap = new HashMap();

    if ( _styles != null ) {
      for ( int a = 0; a < _styles.length; a++ ) {
        _styleMap.put(_styles[a],null);
      }
    }
  }

  public void setStyleValue(String attribute,String value) {
    _styleMap.put(attribute,value);
  }

  public String getStyleValue(String attribute) {
    String value = (String) _styleMap.get(attribute);
    if ( value != null )
      return value;
    return "";
  }
  
  public boolean isStyleSet(String attribute) {
  	String value = (String) _styleMap.get(attribute);
  	if (value != null)
  		return true;
  	return false;
  }
}
