package com.idega.util.text;



import com.idega.presentation.text.Text;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.idegaweb.IWMainApplication;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class TextFormat{

  private static TextFormat format;

  public static final int NORMAL = 1;
  public static final int HEADER = 2;
  public static final int TITLE = 3;

  public static Text[] TextTemplates;


  public static TextFormat getInstance(){
    if(format == null){
      format = new TextFormat();
      format.load();
    }
    return format;
  }

  public Text format(String text){
    return getText(text,NORMAL);
  }
  public Text format(String text,int type){
    return getText(text,type);
  }

  public Text format(int i){
    return format(String.valueOf(i),NORMAL);
  }
  public Text format(String text,String color){
    Text t = getText(text,NORMAL);
    t.setFontColor(color);
    return t;
  }

  private Text getText(String text,int type){
    Text t  = new Text();
    switch (type) {
      case NORMAL : t =  (Text)TextTemplates[0].clone(); break;
      case HEADER : t =  (Text)TextTemplates[1].clone(); break;
      case TITLE :  t =  (Text)TextTemplates[2].clone(); break;
      default : t =  (Text)TextTemplates[0].clone(); break;
    }
    t.setText(text);
    return t;
  }

  private void load(){
    TextTemplates = new Text[3];
    TextTemplates[0] = new Text();
    TextTemplates[1] = new Text();
    TextTemplates[2] = new Text();
  }

  private void store(){


  }

}
