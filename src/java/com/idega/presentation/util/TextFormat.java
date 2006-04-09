package com.idega.presentation.util;



import com.idega.presentation.text.Text;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class TextFormat implements Singleton{

  private static Instantiator initiator = new Instantiator() { public Object getInstance() { return new TextFormat();}};

  public static final int NORMAL = 1;
  public static final int HEADER = 2;
  public static final int TITLE = 3;

  private Text[] textTemplates;

  public TextFormat()	{
  	load();
  }
  
  public static TextFormat getInstance()	{
  	return (TextFormat) SingletonRepository.getRepository().getInstance(TextFormat.class, initiator);
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
      case NORMAL : t =  (Text)this.textTemplates[0].clone(); break;
      case HEADER : t =  (Text)this.textTemplates[1].clone(); break;
      case TITLE :  t =  (Text)this.textTemplates[2].clone(); break;
      default : t =  (Text)this.textTemplates[0].clone(); break;
    }
    t.setText(text);
    return t;
  }

  private void load(){
    this.textTemplates = new Text[3];
    this.textTemplates[0] = new Text();
    this.textTemplates[1] = new Text();
    this.textTemplates[1].setBold(true);
    this.textTemplates[2] = new Text();
    this.textTemplates[2].setBold(true);
  }

}
