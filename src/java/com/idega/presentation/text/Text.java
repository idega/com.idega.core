//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.text;


import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import com.idega.presentation.*;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.data.ICLocale;


/**
*A wrapper class for presenting plain (formatted) text in idegaWeb Presentaiton Objects.
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class Text extends PresentationObject{

private static Text emptyText;
private static Text HTMLbreak;
private static Text HTMLnbsp;

protected String text;
protected Map localizationMap;
protected boolean attributeSet;
protected boolean teletype;
protected boolean bold;
protected boolean italic;
protected boolean underline;

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
public static String FONT_SIZE_10_STYLE_TAG= "10pt";
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
**Constructor that creates the object with an empty string
**/

public Text(){
	this(EMPTY_TEXT_STRING);
}

public Text(String text){
	super();
	setText(text);
	attributeSet=false;
	teletype=false;
	bold=false;
	italic=false;
	underline=false;
}

public Text(String text,boolean bold,boolean italic,boolean underline){
	this(text);
	if(bold){
		this.setBold();
	}
	if(italic){
		this.setItalic();
	}
	if(underline){
		this.setUnderline();
	}
}



public void setAttribute(String name,String value){
	attributeSet=true;
	super.setAttribute(name,value);
}

protected boolean isEnclosedByParagraph(){
	boolean returnBool=false;
	PresentationObject obj = getParentObject();
	while(obj != null){
		if(obj.getClassName().equals("com.idega.presentation.text.Paragraph")){
			returnBool = true;
		}
		obj=obj.getParentObject();
	}
	return returnBool;
}

public void setFontSize(String s){
	setAttribute("size",s);
}

public void setFontSize(int i){
	setFontSize(Integer.toString(i));
}

public void setFontFace(String s){
	setAttribute("face",s);
}

public void setFontColor(String color){
	setAttribute("color",color);
}

public void setFontStyle(String style){
	setAttribute("style",style);
}

public void setFontClass(String styleClass){
	setStyle(styleClass);
}

public void setStyle(String style) {
  setStyleClass(style);
}

public void addToText(String s){
  if(text==EMPTY_TEXT_STRING){
    text=s;
  }
  else{
    text=text+s;
  }
}

public void setText(String s){
	text=s;
}

public void setLocalizedText(String localeString,String text){
    setLocalizedText(ICLocaleBusiness.getLocaleFromLocaleString(localeString),text);
}

public void setLocalizedText(int icLocaleID,String text){
    setLocalizedText(ICLocaleBusiness.getLocale(icLocaleID),text);
}

public void setLocalizedText(Locale locale,String text){
    getLocalizationMap().put(locale,text);
}

private Map getLocalizationMap(){
  if(localizationMap==null){
    localizationMap=new HashMap();
  }
  return localizationMap;
}


public void addBreak(){
	addToText(BREAK);
}

public void setTeleType(){
	teletype=true;
}

public void setBold(){
	bold=true;
}

public void setBold(boolean bold){
  this.bold=bold;
}

public void setItalic(){
	italic=true;
}

public void setItalic(boolean italic) {
  this.italic=italic;
}

public void setUnderline(){
	underline=true;
}

public void setUnderline(boolean underline) {
  this.underline=underline;
}

public String getText(){
	return this.text;
}

public String toString(){
	return this.text;
}

public void setCSSClass(String cStyleSheetReferanceClass){
  setAttribute("class",cStyleSheetReferanceClass);
}

/**
 * returns empty string with fontsize = 1
 */
public static Text emptyString(){
  if (emptyText == null){
    emptyText = new Text("");
    emptyText.setFontSize("1");
  }
  return emptyText;
}

public static Text getBreak(){
  if (HTMLbreak == null){
    HTMLbreak = new Text(BREAK);
    HTMLbreak.addHTMLFontTag(false);
    HTMLbreak.setUseBuilderObjectControl(false);
   // HTMLbreak.setFontSize("1");
  }
  return HTMLbreak;
}

public static Text getNonBrakingSpace(){
  if (HTMLnbsp == null){
    HTMLnbsp = new Text(NON_BREAKING_SPACE);
    HTMLnbsp.addHTMLFontTag(false);
    //HTMLnbsp.setFontSize(1);
  }
  return HTMLnbsp;
}

public static Text getNonBrakingSpace(int fontSize){
  Text nbsp = getNonBrakingSpace();
    nbsp.setFontSize(fontSize);
  return HTMLnbsp;
}

public void addHTMLFontTag(boolean addHTMLFontTag){
  this.addHTMLFontTag = addHTMLFontTag;
}

private void setDefaultAttributes(IWContext iwc){

	if ( (! isAttributeSet("size")) && addHTMLFontTag ){
		setFontSize(iwc.getDefaultFontSize());
	}
	if ( ! isAttributeSet("face") && addHTMLFontTag  ){
		setFontFace(iwc.getDefaultFontFace() );
	}
	if ( ! isAttributeSet("color") && addHTMLFontTag  ){
		setFontColor(iwc.getDefaultFontColor());
	}

	if ( ! isAttributeSet("style") && addHTMLFontTag ){
		setFontStyle(iwc.getDefaultFontStyle() );
	}

}

  public Object clone() {
    Text obj = null;
    try {
      obj = (Text)super.clone();

      obj.text = this.text;
      obj.attributeSet = this.attributeSet;
      obj.teletype = this.teletype;
      obj.bold = this.bold;
      obj.italic = this.italic;
      obj.underline = this.underline;
      obj.addHTMLFontTag = this.addHTMLFontTag;
      if(this.localizationMap!=null){
	obj.localizationMap=(Map)((HashMap)this.localizationMap).clone();
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }



public String getLocalizedText(IWContext iwc){

  if(this.localizationMap!=null){
    Locale currLocale = iwc.getCurrentLocale();

    String localizedString = (String)this.getLocalizationMap().get(currLocale);
    if(localizedString!=null){
      return localizedString;
    }
    else{
      String defLocalizedString = (String)this.getLocalizationMap().get(iwc.getApplication().getSettings().getDefaultLocale());
      if(defLocalizedString!=null){
	return defLocalizedString;
      }
    }
  }
  return getText();
}

public void print(IWContext iwc)throws Exception{
	//setDefaultAttributes(iwc);
	//if ( doPrint(iwc) ){
		if (getLanguage().equals("HTML")){
		  boolean alignSet = isAttributeSet(HORIZONTAL_ALIGNMENT);
		  if(alignSet){
		    print("<div align=\""+getHorizontalAlignment()+"\">");
		    removeAttribute(HORIZONTAL_ALIGNMENT);//does this slow things down?
		  }
			//if (getInterfaceStyle().equals("something")){
			//}
			//else{
			if (bold)
				{print("<b>");}
			if (italic)
				{print("<i>");}
				if (attributeSet){

					print("<font "+getAttributeString()+" >");
					print(getLocalizedText(iwc));
					print("</font>");
				}
				else{
					print(getLocalizedText(iwc));
				}
			if (bold)
				{print("</b>");}
			if (italic)
				{print("</i>");}

		  if(alignSet){
		    print("</div>");
		  }

			//}
		}
		else if (getLanguage().equals("WML")){
			/*if (isEnclosedByParagraph()){
				println(text);
			}
			else{
				Paragraph myParagraph = new Paragraph();
				myParagraph.setParentObject(this.getParentObject());
				myParagraph.add(this);
				myParagraph._print(iwc);
			}*/
				print(getLocalizedText(iwc));
		}
	//}
}


}

