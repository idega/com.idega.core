package com.idega.graphics.generator;

/**
 * Title: ImageFactory
 * Description: A factory object for creating dynamic and useful com.idega.presentation.Image object
 * for example a generated rollover image (uses three generated gif's) for buttons. The object
 * is instanciated by the IWMainApplication and keeps a record of created Image objects.
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */


import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Image;
import com.idega.util.FileUtil;

import java.util.Locale;

public class ImageFactory {
  private static IWMainApplication iwma;
  private static ImageFactory factory;

  private static String GENERATED_IMAGES_FOLDER = "iw_generated";

  private ImageFactory(IWMainApplication iwma) {
    this.iwma = iwma;
  }

  public static ImageFactory getStaticInstance(IWMainApplication iwma){
   if(factory == null) factory = new ImageFactory(iwma);
   return factory;
  }

  public Image createButton(String textOnButton, IWBundle iwb){
    return createButton(textOnButton,iwb,null);
  }

  public Image createButton(String textOnButton, IWBundle iwb, Locale local){
    Image image = null;
    String filePath;
    if( local!=null ) filePath = iwb.getResourcesRealPath(local);
    else filePath = iwb.getResourcesRealPath();

    filePath = filePath+FileUtil.getFileSeparator()+GENERATED_IMAGES_FOLDER+FileUtil.getFileSeparator();

    FileUtil.createFolder(filePath);

    Button button = new Button(textOnButton);
    button.generate(filePath);

    image = new Image("test",button.getButtonUpURL(),button.getButtonOverURL());

    return image;
  }

}