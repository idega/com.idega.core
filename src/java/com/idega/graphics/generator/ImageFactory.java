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

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.Image;
import com.idega.util.FileUtil;
import com.idega.util.IWColor;

public class ImageFactory {
	private static ImageFactory factory;
	
	private static final String BUTTON_SUFFIX = "_button";
	private static final String TAB_SUFFIX = "_tab";
	private static String GENERATED_IMAGES_FOLDER = "iw_generated";
	
	public final static String GENERATED_FILL_COLOR = "iw_generated_fill_color";
	public final static String GENERATED_BORDER_COLOR = "iw_generated_border_color";
	public final static String GENERATED_FONT = "iw_generated_font";
	public final static String GENERATED_FONT_COLOR = "iw_generated_font_color";
	public final static String GENERATED_OVER_COLOR = "iw_generated_over_color";
	public final static String GENERATED_UNDER_COLOR = "iw_generated_under_color";
	public final static String GENERATED_HIGHLIGHT_COLOR = "iw_generated_highligth_color";	
	
	//Instance variables:
	private IWMainApplication iwma;
	private IWBundle coreBundle;
	private String fontPath;
	private Font defaultFont;
	private HashMap images;
	private Font fontbase;


	ImageFactory(IWMainApplication iwma) {
		this.iwma = iwma;
	}

	public static ImageFactory getStaticInstance(IWMainApplication iwma) {
		if (factory == null) {
			factory = new ImageFactory(iwma);
			factory.coreBundle = iwma.getCoreBundle();
			factory.images = new HashMap();
			//if (!shutdown) {
				String folderPath = factory.coreBundle.getResourcesRealPath() + FileUtil.getFileSeparator() + iwma.CORE_BUNDLE_FONT_FOLDER_NAME + FileUtil.getFileSeparator();
				try {
					//System.out.println(folderPath+iwma.CORE_DEFAULT_FONT);
					File file = new File(folderPath + iwma.CORE_DEFAULT_FONT);
					FileInputStream fis = new FileInputStream(file);

					factory.fontbase = Font.createFont(Font.TRUETYPE_FONT, fis);
					factory.defaultFont = factory.fontbase.deriveFont(Font.PLAIN, getDefaultFontSize());

				}
				catch (Exception ex) {
					System.err.println("ImageFactory : default font is missing using default java font instead");
				}
			//}

		}

		return factory;
	}

	/**
	 * Unloads or shutdowns the factory
	 *
	 */
	public void unload(){
		factory=null;
	}


	public Image createButton(String textOnButton, IWBundle iwb) {
		return createButton(textOnButton, iwb, null);
	}

	public Image createButton(String textOnButton, IWBundle iwb, Locale local) {
		String filePath;
		String fileVirtualPath;
		Image image;

		if (local != null) {
			image = (Image) images.get(textOnButton + BUTTON_SUFFIX + local.toString());
			if (image != null)
				return image;
			filePath = iwb.getResourcesRealPath(local);
			fileVirtualPath = iwb.getResourcesURL(local) + "/" + GENERATED_IMAGES_FOLDER + "/";
		}
		else {
			image = (Image) images.get(textOnButton + BUTTON_SUFFIX);
			if (image != null)
				return image;
			filePath = iwb.getResourcesRealPath();
			fileVirtualPath = iwb.getResourcesURL() + "/" + GENERATED_IMAGES_FOLDER + "/";
		}

		filePath = filePath + FileUtil.getFileSeparator() + GENERATED_IMAGES_FOLDER + FileUtil.getFileSeparator();

		FileUtil.createFolder(filePath);

		Button button = new Button(textOnButton, defaultFont);
		setButtonAttributesFromBundleProperties(iwb,button);
		button.generate(filePath);

		if (iwb.getApplication().getSettings().getIfDebug())
			System.out.println("fileVirtualPath :" + fileVirtualPath);

		String upName = fileVirtualPath + button.getUpName();
		String downName = fileVirtualPath + button.getDownName();
		String overName = fileVirtualPath + button.getOverName();

		image = new Image(textOnButton, upName, overName, downName);
		image.setWidth(button.getWidth());
		image.setHeight(button.getHeight());

		addToStoredImages(textOnButton + BUTTON_SUFFIX, image, local);

		return image;
	}

	public Image createTab(String textOnTab, IWBundle iwb, boolean flip) {
		return createTab(textOnTab, iwb, null, flip);
	}

	public Image createTab(String textOnTab, IWBundle iwb, Locale local, boolean flip) {
		String filePath;
		String fileVirtualPath;
		Image image;

		if (local != null) {
			image = (Image) images.get(textOnTab + TAB_SUFFIX + flip + local.toString());
			if (image != null)
				return image;
			filePath = iwb.getResourcesRealPath(local);
			fileVirtualPath = iwb.getResourcesURL(local) + "/" + GENERATED_IMAGES_FOLDER + "/";
		}
		else {
			image = (Image) images.get(textOnTab + TAB_SUFFIX + flip);
			if (image != null)
				return image;
			filePath = iwb.getResourcesRealPath();
			fileVirtualPath = iwb.getResourcesURL() + "/" + GENERATED_IMAGES_FOLDER + "/";
		}

		filePath = filePath + FileUtil.getFileSeparator() + GENERATED_IMAGES_FOLDER + FileUtil.getFileSeparator();

		FileUtil.createFolder(filePath);
		Font tabFont = null;

		tabFont = fontbase.deriveFont(Font.PLAIN, getDefaultFontSize());

		Tab tab = new Tab(textOnTab, tabFont);
		setButtonAttributesFromBundleProperties(iwb,tab);
		tab.flip(flip);
		tab.generate(filePath);

		String upName = fileVirtualPath + flip + tab.getUpName();
		String downName = fileVirtualPath + flip + tab.getDownName();
		String overName = fileVirtualPath + flip + tab.getOverName();

		image = new Image(textOnTab, upName, overName, downName);
		image.setWidth(tab.getWidth());
		image.setHeight(tab.getHeight());

		addToStoredImages(textOnTab + TAB_SUFFIX + flip, image, local);

		return image;
	}

	private void addToStoredImages(String key, Image image, Locale local) {
		if (local != null) {
			images.put(key + local.toString(), image);
		}
		else {
			images.put(key, image);
		}
	}

	/** delete all generated images in bundles and the (webroot)/iw_generated folder*/
	public static void deleteGeneratedImages(IWMainApplication iwma) {
		FileUtil.deleteAllFilesInDirectory(iwma.getApplicationRealPath() + FileUtil.getFileSeparator() + GENERATED_IMAGES_FOLDER + FileUtil.getFileSeparator());

		List bundles = iwma.getRegisteredBundles();
		List locales = iwma.getAvailableLocales();

		Iterator iter = bundles.iterator();

		while (iter.hasNext()) {
			IWBundle bundle = (IWBundle) iter.next();

			String resourcePath = bundle.getResourcesRealPath();
			FileUtil.deleteAllFilesInDirectory(resourcePath + FileUtil.getFileSeparator() + GENERATED_IMAGES_FOLDER + FileUtil.getFileSeparator());

			Iterator iter2 = locales.iterator();
			while (iter2.hasNext()) {
				Locale item = (Locale) iter2.next();
				resourcePath = bundle.getResourcesRealPath(item);
				FileUtil.deleteAllFilesInDirectory(resourcePath + FileUtil.getFileSeparator() + GENERATED_IMAGES_FOLDER + FileUtil.getFileSeparator());
			}
		}
		
		if(factory!=null){
			factory.images = new HashMap();
		}
	}

	static float getDefaultFontSize() {
		String VMVer = System.getProperty("java.vm.version", "1.3");
		String OS = System.getProperty("os.name", "Windows");
		/**
		 * Special check for "correct" point sizing on MacOS X and JVM 1.4
		 */
		if (OS.startsWith("Mac") || VMVer.startsWith("1.4")) {
			//defaultFont = fontbase.deriveFont(Font.PLAIN,10);
			return 10;
		}
		else {
			//defaultFont = fontbase.deriveFont(Font.PLAIN,8.5f);
			return 8.5f;
		}
	}
	
	private void setButtonAttributesFromBundleProperties(IWBundle iwb, Button button){
	
		if(iwb.getProperty(GENERATED_FILL_COLOR)!=null)
			button.setFillColor(IWColor.getAWTColorFromHex(iwb.getProperty(GENERATED_FILL_COLOR)));
		else{
			String fillColor = coreBundle.getProperty(GENERATED_FILL_COLOR,IWColor.getHexColorString(button.fillColor));
			button.setFillColor(IWColor.getAWTColorFromHex(fillColor));
		}
		
		if(iwb.getProperty(GENERATED_BORDER_COLOR)!=null)
			button.setBorderColor(IWColor.getAWTColorFromHex(iwb.getProperty(GENERATED_BORDER_COLOR)));
		else{
			String borderColor = coreBundle.getProperty(GENERATED_BORDER_COLOR,IWColor.getHexColorString(button.borderColor));
			button.setBorderColor(IWColor.getAWTColorFromHex(borderColor));
		}
		
		if(iwb.getProperty(GENERATED_OVER_COLOR)!=null)
			button.setOverColor(IWColor.getAWTColorFromHex(iwb.getProperty(GENERATED_OVER_COLOR)));
		else{
			String overColor = coreBundle.getProperty(GENERATED_OVER_COLOR,IWColor.getHexColorString(button.overColor));
			button.setOverColor(IWColor.getAWTColorFromHex(overColor));
		}
		
		if(iwb.getProperty(GENERATED_UNDER_COLOR)!=null)
			button.setUnderColor(IWColor.getAWTColorFromHex(iwb.getProperty(GENERATED_UNDER_COLOR)));
		else {
			String underColor = coreBundle.getProperty(GENERATED_UNDER_COLOR,IWColor.getHexColorString(button.underColor));
			button.setUnderColor(IWColor.getAWTColorFromHex(underColor));
		}
					
		if(iwb.getProperty(GENERATED_HIGHLIGHT_COLOR)!=null)
			button.setHighlightColor(IWColor.getAWTColorFromHex(iwb.getProperty(GENERATED_HIGHLIGHT_COLOR)));
		else{
			String highlightColor = coreBundle.getProperty(GENERATED_HIGHLIGHT_COLOR,IWColor.getHexColorString(button.highlightColor));
			button.setHighlightColor(IWColor.getAWTColorFromHex(highlightColor));
		}
					
		if(iwb.getProperty(GENERATED_FONT_COLOR)!=null)
			button.setFontColor(IWColor.getAWTColorFromHex(iwb.getProperty(GENERATED_FONT_COLOR)));
		else{
			String fontColor = coreBundle.getProperty(GENERATED_FONT_COLOR,IWColor.getHexColorString(button.fontColor));
			button.setFontColor(IWColor.getAWTColorFromHex(fontColor));
		}
					
		String fontStr = "dialog-plain-10";
		Font btnFont = button.getFont();
		if(btnFont !=null){
			String styleStr = "plain";
			if(btnFont.isBold() && btnFont.isItalic())
				styleStr = "bold-italic";
			else if(btnFont.isBold())
					styleStr = "bold";
			else if(btnFont.isItalic()) 
				styleStr = "italic";
				
			fontStr = btnFont.getFamily()+"-"+styleStr+"-"+btnFont.getSize();
		}
		if(iwb.getProperty(GENERATED_FONT)!=null)
			button.setFont(Font.decode(iwb.getProperty(GENERATED_FONT)));
		else{
			String font = coreBundle.getProperty(GENERATED_FONT,fontStr);
			button.setFont(Font.decode(font));
		}
		
	}

}