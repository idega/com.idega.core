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
import java.awt.Font;
import java.io.FileInputStream;

import java.util.Locale;

public class ImageFactory {
  private static IWMainApplication iwma;
  private static ImageFactory factory;
  private static IWBundle coreBundle;
  private static String fontPath;
  private static Font defaultFont;

  private static String GENERATED_IMAGES_FOLDER = "iw_generated";

  private ImageFactory(IWMainApplication iwma) {
    this.iwma = iwma;
  }

  public static ImageFactory getStaticInstance(IWMainApplication iwma){
   if(factory == null){
    factory = new ImageFactory(iwma);
    coreBundle = iwma.getCoreBundle();
    String folderPath = coreBundle.getResourcesRealPath()+FileUtil.getFileSeparator()+iwma.CORE_BUNDLE_FONT_FOLDER_NAME+FileUtil.getFileSeparator();
    try {
      Font fontbase = Font.createFont(Font.TRUETYPE_FONT,new FileInputStream(folderPath+"Spliffy.ttf"));
      defaultFont = fontbase.deriveFont(Font.PLAIN,10.f);
    }
    catch (Exception ex) {
      ex.printStackTrace(System.err);
    }

   }
   return factory;
  }

  public Image createButton(String textOnButton, IWBundle iwb){
    return createButton(textOnButton,iwb,null);
  }

  public Image createButton(String textOnButton, IWBundle iwb, Locale local){
    Image image = null;
    String filePath;
    String fileVirtualPath;

    if( local!=null ){
      filePath = iwb.getResourcesRealPath(local);
      fileVirtualPath = iwb.getResourcesURL(local)+"/"+GENERATED_IMAGES_FOLDER+"/";
    }
    else{
      filePath = iwb.getResourcesRealPath();
      fileVirtualPath = iwb.getResourcesURL()+"/"+GENERATED_IMAGES_FOLDER+"/";
    }

    filePath = filePath+FileUtil.getFileSeparator()+GENERATED_IMAGES_FOLDER+FileUtil.getFileSeparator();

    FileUtil.createFolder(filePath);

    Button button = new Button(textOnButton,defaultFont);
    button.generate(filePath);

    image = new Image("iw_generated_"+Integer.toString(button.hashCode()),fileVirtualPath+button.getButtonUpName(),fileVirtualPath+button.getButtonOverName(),fileVirtualPath+button.getButtonDownName());
    image.setWidth(button.getWidth());
    image.setHeight(button.getHeight());

    return image;
  }

}