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
import java.util.Locale;
import java.io.FileInputStream;
import java.util.HashMap;

public class ImageFactory {
  private static IWMainApplication iwma;
  private static ImageFactory factory;
  private static IWBundle coreBundle;
  private static String fontPath;
  private static Font defaultFont;
  private static HashMap images;

  private static String GENERATED_IMAGES_FOLDER = "iw_generated";


  private ImageFactory(IWMainApplication iwma) {
    this.iwma = iwma;
  }

  public static ImageFactory getStaticInstance(IWMainApplication iwma){
   if(factory == null){
    factory = new ImageFactory(iwma);
    coreBundle = iwma.getCoreBundle();
    images = new HashMap();
    String folderPath = coreBundle.getResourcesRealPath()+FileUtil.getFileSeparator()+iwma.CORE_BUNDLE_FONT_FOLDER_NAME+FileUtil.getFileSeparator();
    try {
      Font fontbase = Font.createFont(Font.TRUETYPE_FONT,new FileInputStream(folderPath+iwma.CORE_DEFAULT_FONT));
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
    String filePath;
    String fileVirtualPath;
    Image image;

    if( local!=null ){
      image = (Image) images.get(textOnButton+local.toString());
      if( image != null ) return image;
      filePath = iwb.getResourcesRealPath(local);
      fileVirtualPath = iwb.getResourcesURL(local)+"/"+GENERATED_IMAGES_FOLDER+"/";
    }
    else{
      image = (Image) images.get(textOnButton);
      if( image != null ) return image;
      filePath = iwb.getResourcesRealPath();
      fileVirtualPath = iwb.getResourcesURL()+"/"+GENERATED_IMAGES_FOLDER+"/";
    }

    filePath = filePath+FileUtil.getFileSeparator()+GENERATED_IMAGES_FOLDER+FileUtil.getFileSeparator();

    FileUtil.createFolder(filePath);

    Button button = new Button(textOnButton,defaultFont);
    button.generate(filePath);

    image = new Image("iw_generated_"+Integer.toString(button.hashCode()),fileVirtualPath+button.getUpName(),fileVirtualPath+button.getOverName(),fileVirtualPath+button.getDownName());
    image.setWidth(button.getWidth());
    image.setHeight(button.getHeight());

    if( local!=null){
      images.put(textOnButton+local.toString(),image);
    }
    else{
      images.put(textOnButton,image);
    }
    return image;
  }

  public Image createTab(String textOnTab, IWBundle iwb, boolean flip){
    return createTab(textOnTab,iwb,null,flip);
  }

  public Image createTab(String textOnTab, IWBundle iwb, Locale local, boolean flip){
    String filePath;
    String fileVirtualPath;
    Image image;

    if( local!=null ){
      image = (Image) images.get(textOnTab+local.toString());
      if( image != null ) return image;
      filePath = iwb.getResourcesRealPath(local);
      fileVirtualPath = iwb.getResourcesURL(local)+"/"+GENERATED_IMAGES_FOLDER+"/";
    }
    else{
      image = (Image) images.get(textOnTab);
      if( image != null ) return image;
      filePath = iwb.getResourcesRealPath();
      fileVirtualPath = iwb.getResourcesURL()+"/"+GENERATED_IMAGES_FOLDER+"/";
    }

    filePath = filePath+FileUtil.getFileSeparator()+GENERATED_IMAGES_FOLDER+FileUtil.getFileSeparator();

    FileUtil.createFolder(filePath);

    Tab tab = new Tab(textOnTab,defaultFont);
    tab.flip(flip);
    tab.generate(filePath);

    image = new Image("iw_generated_"+Integer.toString(tab.hashCode()),fileVirtualPath+tab.getUpName(),fileVirtualPath+tab.getOverName(),fileVirtualPath+tab.getDownName());
    image.setWidth(tab.getWidth());
    image.setHeight(tab.getHeight());

    if( local!=null){
      images.put(textOnTab+local.toString(),image);
    }
    else{
      images.put(textOnTab,image);
    }
    return image;
  }

}