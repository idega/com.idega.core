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
import java.util.Map;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.Image;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.FileUtil;
import com.idega.util.IWColor;

public class ImageFactory implements Singleton {
	
	private static Instantiator instantiator = new Instantiator() { 
		public Object getInstance(Object parameter) { 
			return new ImageFactory((IWMainApplication) parameter); 
		}
	};
	
	private static final String BUTTON_SUFFIX = "_button";
	private static final String TAB_SUFFIX = "_tab";
	private static String GENERATED_IMAGES_FOLDER = "/idegaweb/iw_generated";
	
	public final static String GENERATED_FILL_COLOR = "iw_generated_fill_color";
	public final static String GENERATED_BORDER_COLOR = "iw_generated_border_color";
	public final static String GENERATED_FONT = "iw_generated_font";
	public final static String GENERATED_FONT_COLOR = "iw_generated_font_color";
	public final static String GENERATED_OVER_COLOR = "iw_generated_over_color";
	public final static String GENERATED_UNDER_COLOR = "iw_generated_under_color";
	public final static String GENERATED_HIGHLIGHT_COLOR = "iw_generated_highligth_color";	
	
	//Instance variables:
	private IWBundle coreBundle;
	private Font defaultFont;
	private Map generatedCache;
	private Font fontbase;


	public static ImageFactory getInstance(IWMainApplication iwma) {
		return (ImageFactory) SingletonRepository.getRepository().getInstance(ImageFactory.class, instantiator, iwma);
	}
	
	
	protected ImageFactory(IWMainApplication iwma) {
		initialize(iwma);
	}

	private void initialize(IWMainApplication iwma) {
		this.coreBundle = iwma.getCoreBundle();
		//images = new HashMap();
		this.generatedCache = new HashMap();
		//if (!shutdown) {
		String folderPath = this.coreBundle.getResourcesRealPath() + FileUtil.getFileSeparator() + IWMainApplication.CORE_BUNDLE_FONT_FOLDER_NAME + FileUtil.getFileSeparator();
		try {
			//System.out.println(folderPath+iwma.CORE_DEFAULT_FONT);
			File file = new File(folderPath + IWMainApplication.CORE_DEFAULT_FONT);
			FileInputStream fis = new FileInputStream(file);

			this.fontbase = Font.createFont(Font.TRUETYPE_FONT, fis);
			this.defaultFont = this.fontbase.deriveFont(Font.PLAIN, getDefaultFontSize());
		}
		catch (Exception ex) {
			System.err.println("ImageFactory : default font is missing using default java font instead");
		}
	}




	public Image createButton(String textOnButton, IWBundle iwb) {
		return createButton(textOnButton, iwb, null);
	}

	public Image createButton(String textOnButton, IWBundle iwb, Locale local) {
		String filePath;
		String fileVirtualPath;
		//Image image;

		if (local != null) {
			//image = (Image) images.get(textOnButton + BUTTON_SUFFIX + local.toString());
			GeneratedImageCache cache = (GeneratedImageCache) this.generatedCache.get(textOnButton + BUTTON_SUFFIX + local.toString());
			if (cache != null) {
				return cache.createImage();
			}
			//filePath = iwb.getResourcesRealPath(local);
			//fileVirtualPath = iwb.getResourcesURL(local) + "/" + GENERATED_IMAGES_FOLDER + "/";
			filePath = iwb.getApplication().getApplicationRealPath()+GENERATED_IMAGES_FOLDER+"/"+local.toString()+"/";
			fileVirtualPath = iwb.getApplication().getTranslatedURIWithContext(GENERATED_IMAGES_FOLDER + "/"+local.toString()+"/");
		}
		else {
			//image = (Image) images.get(textOnButton + BUTTON_SUFFIX);
			GeneratedImageCache cache = (GeneratedImageCache) this.generatedCache.get(textOnButton + BUTTON_SUFFIX);
			if (cache != null) {
				return cache.createImage();
			}
			//filePath = iwb.getResourcesRealPath();
			//fileVirtualPath = iwb.getResourcesURL() + "/" + GENERATED_IMAGES_FOLDER + "/";
			filePath = iwb.getApplication().getApplicationRealPath()+GENERATED_IMAGES_FOLDER+"/";
			fileVirtualPath = iwb.getApplication().getTranslatedURIWithContext(GENERATED_IMAGES_FOLDER + "/");

		}

		//filePath = filePath + FileUtil.getFileSeparator() + GENERATED_IMAGES_FOLDER + FileUtil.getFileSeparator();

		FileUtil.createFolder(filePath);

		Button button = new Button(textOnButton, this.defaultFont);
		setButtonAttributesFromBundleProperties(iwb,button);
		button.generate(filePath);

		if (iwb.getApplication().getSettings().getIfDebug()) {
			System.out.println("fileVirtualPath :" + fileVirtualPath);
		}

		String upName = fileVirtualPath + button.getUpName();
		String downName = fileVirtualPath + button.getDownName();
		String overName = fileVirtualPath + button.getOverName();
		/*
		image = new Image(textOnButton, upName, overName, downName);
		image.setWidth(button.getWidth());
		image.setHeight(button.getHeight());*/

		GeneratedImageCache cache = new GeneratedImageCache(textOnButton,button.getWidth(),button.getHeight(),upName,downName,overName,local);
		addToStoredImages(textOnButton + BUTTON_SUFFIX, cache, local);

		return cache.createImage();
	}

	public Image createTab(String textOnTab, IWBundle iwb, boolean flip) {
		return createTab(textOnTab, iwb, null, flip);
	}

	public Image createTab(String textOnTab, IWBundle iwb, Locale local, boolean flip) {
		String filePath;
		String fileVirtualPath;
		//Image image;

		if (local != null) {
			//image = (Image) images.get(textOnTab + TAB_SUFFIX + flip + local.toString());
			GeneratedImageCache cache = (GeneratedImageCache) this.generatedCache.get(textOnTab + TAB_SUFFIX + flip + local.toString());
			if (cache != null) {
				return cache.createImage();
			}
			//filePath = iwb.getResourcesRealPath(local);
			//fileVirtualPath = iwb.getResourcesURL(local) + "/" + GENERATED_IMAGES_FOLDER + "/";
			filePath = iwb.getApplication().getApplicationRealPath()+GENERATED_IMAGES_FOLDER+"/"+local.toString()+"/";
			fileVirtualPath = iwb.getApplication().getTranslatedURIWithContext(GENERATED_IMAGES_FOLDER + "/"+local.toString()+"/");

		}
		else {
			//image = (Image) images.get(textOnTab + TAB_SUFFIX + flip);
			GeneratedImageCache cache = (GeneratedImageCache) this.generatedCache.get(textOnTab + TAB_SUFFIX + flip);
			if (cache != null) {
				return cache.createImage();
			}
			//filePath = iwb.getResourcesRealPath();
			//fileVirtualPath = iwb.getResourcesURL() + "/" + GENERATED_IMAGES_FOLDER + "/";
			filePath = iwb.getApplication().getApplicationRealPath()+GENERATED_IMAGES_FOLDER+"/";
			fileVirtualPath = iwb.getApplication().getTranslatedURIWithContext(GENERATED_IMAGES_FOLDER + "/");

		}

		//filePath = filePath + GENERATED_IMAGES_FOLDER + FileUtil.getFileSeparator();

		FileUtil.createFolder(filePath);
		Font tabFont = null;

		tabFont = this.fontbase.deriveFont(Font.PLAIN, getDefaultFontSize());

		Tab tab = new Tab(textOnTab, tabFont);
		setButtonAttributesFromBundleProperties(iwb,tab);
		tab.flip(flip);
		tab.generate(filePath);

		String upName = fileVirtualPath + flip + tab.getUpName();
		String downName = fileVirtualPath + flip + tab.getDownName();
		String overName = fileVirtualPath + flip + tab.getOverName();
		/*
		image = new Image(textOnTab, upName, overName, downName);
		image.setWidth(tab.getWidth());
		image.setHeight(tab.getHeight());
		*/
		GeneratedImageCache cache = new GeneratedImageCache(textOnTab,tab.getWidth(),tab.getHeight(),upName,downName,overName,local);
		addToStoredImages(textOnTab + TAB_SUFFIX + flip, cache, local);

		return cache.createImage();
	}

	private void addToStoredImages(String key, GeneratedImageCache image, Locale local) {
		if (local != null) {
			this.generatedCache.put(key + local.toString(), image);
		}
		else {
			this.generatedCache.put(key, image);
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
		
		ImageFactory factory = (ImageFactory) SingletonRepository.getRepository().getExistingInstanceOrNull(ImageFactory.class);
		if (factory != null) {
			factory.generatedCache = new HashMap();
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
	
		if(iwb.getProperty(GENERATED_FILL_COLOR)!=null) {
			button.setFillColor(IWColor.getAWTColorFromHex(iwb.getProperty(GENERATED_FILL_COLOR)));
		}
		else{
			String fillColor = this.coreBundle.getProperty(GENERATED_FILL_COLOR,IWColor.getHexColorString(button.fillColor));
			button.setFillColor(IWColor.getAWTColorFromHex(fillColor));
		}
		
		if(iwb.getProperty(GENERATED_BORDER_COLOR)!=null) {
			button.setBorderColor(IWColor.getAWTColorFromHex(iwb.getProperty(GENERATED_BORDER_COLOR)));
		}
		else{
			String borderColor = this.coreBundle.getProperty(GENERATED_BORDER_COLOR,IWColor.getHexColorString(button.borderColor));
			button.setBorderColor(IWColor.getAWTColorFromHex(borderColor));
		}
		
		if(iwb.getProperty(GENERATED_OVER_COLOR)!=null) {
			button.setOverColor(IWColor.getAWTColorFromHex(iwb.getProperty(GENERATED_OVER_COLOR)));
		}
		else{
			String overColor = this.coreBundle.getProperty(GENERATED_OVER_COLOR,IWColor.getHexColorString(button.overColor));
			button.setOverColor(IWColor.getAWTColorFromHex(overColor));
		}
		
		if(iwb.getProperty(GENERATED_UNDER_COLOR)!=null) {
			button.setUnderColor(IWColor.getAWTColorFromHex(iwb.getProperty(GENERATED_UNDER_COLOR)));
		}
		else {
			String underColor = this.coreBundle.getProperty(GENERATED_UNDER_COLOR,IWColor.getHexColorString(button.underColor));
			button.setUnderColor(IWColor.getAWTColorFromHex(underColor));
		}
					
		if(iwb.getProperty(GENERATED_HIGHLIGHT_COLOR)!=null) {
			button.setHighlightColor(IWColor.getAWTColorFromHex(iwb.getProperty(GENERATED_HIGHLIGHT_COLOR)));
		}
		else{
			String highlightColor = this.coreBundle.getProperty(GENERATED_HIGHLIGHT_COLOR,IWColor.getHexColorString(button.highlightColor));
			button.setHighlightColor(IWColor.getAWTColorFromHex(highlightColor));
		}
					
		if(iwb.getProperty(GENERATED_FONT_COLOR)!=null) {
			button.setFontColor(IWColor.getAWTColorFromHex(iwb.getProperty(GENERATED_FONT_COLOR)));
		}
		else{
			String fontColor = this.coreBundle.getProperty(GENERATED_FONT_COLOR,IWColor.getHexColorString(button.fontColor));
			button.setFontColor(IWColor.getAWTColorFromHex(fontColor));
		}
					
		String fontStr = "dialog-plain-10";
		Font btnFont = button.getFont();
		if(btnFont !=null){
			String styleStr = "plain";
			if(btnFont.isBold() && btnFont.isItalic()) {
				styleStr = "bold-italic";
			}
			else if(btnFont.isBold()) {
				styleStr = "bold";
			}
			else if(btnFont.isItalic()) {
				styleStr = "italic";
			}
				
			fontStr = btnFont.getFamily()+"-"+styleStr+"-"+btnFont.getSize();
		}
		if(iwb.getProperty(GENERATED_FONT)!=null) {
			button.setFont(Font.decode(iwb.getProperty(GENERATED_FONT)));
		}
		else{
			String font = this.coreBundle.getProperty(GENERATED_FONT,fontStr);
			button.setFont(Font.decode(font));
		}
		
	}

}