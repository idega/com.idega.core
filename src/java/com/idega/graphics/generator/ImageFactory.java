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
import com.idega.util.text.TextSoap;
import java.awt.Font;
import java.util.Locale;
import java.io.FileInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

public class ImageFactory {
  private static IWMainApplication iwma;
  private static ImageFactory factory;
  private static IWBundle coreBundle;
  private static String fontPath;
  private static Font defaultFont;
  private static HashMap images;
  private static Font fontbase;
  private static String BUTTON_SUFFIX = "_button";
  private static String TAB_SUFFIX = "_tab";

  private static String GENERATED_IMAGES_FOLDER = "iw_generated";


  ImageFactory(IWMainApplication iwma) {
    this.iwma = iwma;
  }

  public static ImageFactory getStaticInstance(IWMainApplication iwma){
   if(factory == null){
    factory = new ImageFactory(iwma);
    coreBundle = iwma.getCoreBundle();
    images = new HashMap();
    String folderPath = coreBundle.getResourcesRealPath()+FileUtil.getFileSeparator()+iwma.CORE_BUNDLE_FONT_FOLDER_NAME+FileUtil.getFileSeparator();
    try {
    //System.out.println(folderPath+iwma.CORE_DEFAULT_FONT);
      File file = new File(folderPath+iwma.CORE_DEFAULT_FONT);
      FileInputStream fis = new FileInputStream(file);


      fontbase = Font.createFont(Font.TRUETYPE_FONT,fis);

      String OS = System.getProperty("os.name","Windows");
      /**
       * Special check for point sizing on MacOS
       */
      if(OS.startsWith("Mac")){
        defaultFont = fontbase.deriveFont(Font.PLAIN,10);
      }
      else{
        defaultFont = fontbase.deriveFont(Font.PLAIN,8.5f);
      }
    }
    catch (Exception ex) {
      System.err.println("ImageFactory : default font is missing using default java font instead");
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
      image = (Image) images.get(textOnButton+BUTTON_SUFFIX+local.toString());
      if( image != null ) return image;
      filePath = iwb.getResourcesRealPath(local);
      fileVirtualPath = iwb.getResourcesURL(local)+"/"+GENERATED_IMAGES_FOLDER+"/";
    }
    else{
      image = (Image) images.get(textOnButton+BUTTON_SUFFIX);
      if( image != null ) return image;
      filePath = iwb.getResourcesRealPath();
      fileVirtualPath = iwb.getResourcesURL()+"/"+GENERATED_IMAGES_FOLDER+"/";
    }

    filePath = filePath+FileUtil.getFileSeparator()+GENERATED_IMAGES_FOLDER+FileUtil.getFileSeparator();

    FileUtil.createFolder(filePath);

    Button button = new Button(textOnButton,defaultFont);
    button.generate(filePath);


    System.out.println("fileVirtualPath :"+fileVirtualPath);

    String upName = fileVirtualPath+button.getUpName();
    String downName = fileVirtualPath+button.getDownName();
    String overName = fileVirtualPath+button.getOverName();

    image = new Image(textOnButton,upName,overName,downName);
    image.setWidth(button.getWidth());
    image.setHeight(button.getHeight());

    addToStoredImages(textOnButton+BUTTON_SUFFIX,image,local);

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
      image = (Image) images.get(textOnTab+TAB_SUFFIX+flip+local.toString());
      if( image != null ) return image;
      filePath = iwb.getResourcesRealPath(local);
      fileVirtualPath = iwb.getResourcesURL(local)+"/"+GENERATED_IMAGES_FOLDER+"/";
    }
    else{
      image = (Image) images.get(textOnTab+TAB_SUFFIX+flip);
      if( image != null ) return image;
      filePath = iwb.getResourcesRealPath();
      fileVirtualPath = iwb.getResourcesURL()+"/"+GENERATED_IMAGES_FOLDER+"/";
    }

    filePath = filePath+FileUtil.getFileSeparator()+GENERATED_IMAGES_FOLDER+FileUtil.getFileSeparator();

    FileUtil.createFolder(filePath);
    Font tabFont = null;

    String OS = System.getProperty("os.name","Windows");
    /**
     * Special check for point sizing on MacOS
     */
    if(OS.startsWith("Mac")){
      tabFont = fontbase.deriveFont(Font.PLAIN,10);
    }
    else{
      tabFont = fontbase.deriveFont(Font.PLAIN,8.5f);
    }

    Tab tab = new Tab(textOnTab,tabFont);
    tab.flip(flip);
    tab.generate(filePath);

    String upName = fileVirtualPath+flip+tab.getUpName();
    String downName = fileVirtualPath+flip+tab.getDownName();
    String overName = fileVirtualPath+flip+tab.getOverName();

    image = new Image(textOnTab,upName,overName,downName);
    image.setWidth(tab.getWidth());
    image.setHeight(tab.getHeight());

    addToStoredImages(textOnTab+TAB_SUFFIX+flip,image,local);

    return image;
  }

  private void addToStoredImages(String key , Image image, Locale local){
    if( local!=null){
      images.put(key+local.toString(),image);
    }
    else{
      images.put(key,image);
    }
  }

  /** delete all generated images in bundles and the (webroot)/iw_generated folder*/
  public static void deleteGeneratedImages(IWMainApplication iwma){

    FileUtil.deleteAllFilesInDirectory(iwma.getApplicationRealPath()+FileUtil.getFileSeparator()+GENERATED_IMAGES_FOLDER+FileUtil.getFileSeparator());

    List bundles = iwma.getRegisteredBundles();
    List locales = iwma.getAvailableLocales();

    Iterator iter = bundles.iterator();

    while (iter.hasNext()) {
      IWBundle bundle = (IWBundle ) iter.next();
      String resourcePath = bundle.getResourcesRealPath();
      FileUtil.deleteAllFilesInDirectory(resourcePath+FileUtil.getFileSeparator()+GENERATED_IMAGES_FOLDER+FileUtil.getFileSeparator());

      Iterator iter2 = locales.iterator();
      while (iter2.hasNext()) {
        Locale item = (Locale)iter2.next();
        resourcePath = bundle.getResourcesRealPath(item);
        FileUtil.deleteAllFilesInDirectory(resourcePath+FileUtil.getFileSeparator()+GENERATED_IMAGES_FOLDER+FileUtil.getFileSeparator());
      }
    }
  }


}