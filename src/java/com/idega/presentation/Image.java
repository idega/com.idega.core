/*
 * $Id: Image.java,v 1.88.2.1 2007/01/12 19:31:35 idegaweb Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.file.business.FileSystemConstants;
import com.idega.core.file.data.ICFile;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Window;
import com.idega.repository.data.NonEJBResource;
import com.idega.repository.data.PropertyDescription;
import com.idega.repository.data.PropertyDescriptionHolder;
import com.idega.repository.data.ResourceDescription;
import com.idega.util.text.StyleConstants;
import com.idega.util.text.TextSoap;

/**
 * <p>
 * This is the component to render out Image elements in idegaWeb.<br>
 * In JSF there is now a more recent javax.faces.component.UIGraphic object that is prefered to use in pure JSF applications.
 * </p>
 *  Last modified: $Date: 2007/01/12 19:31:35 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @modified <a href="mailto:eiki@idega.is">Eirikur Hrafnson</a>
 * @version $Revision: 1.88.2.1 $
 */
public class Image extends PresentationObject implements NonEJBResource, PropertyDescriptionHolder
{
	//static variables:
	public static final String ALIGNMENT_BOTTOM = "bottom";
	public static final String ALIGNMENT_MIDDLE = "middle";
	public static final String ALIGNMENT_LEFT = "left";
	public static final String ALIGNMENT_RIGHT = "right";
	public static final String ALIGNMENT_TOP = "top";
	public static final String ALIGNMENT_ABSOLUTE_MIDDLE = "absmiddle";
	public static final String ALIGNMENT_ABSOLUTE_BOTTOM = "absbottom";
	public static final String ALIGNMENT_BASELINE = "baseline";
	public static final String ALIGNMENT_TEXT_TOP = "texttop";
	private static String PARAM_IMAGE_ID = FileSystemConstants.PARAM_FILE_ID;
	private static final String BORDER_COLOR_DEFAULT = "#000000";
	private static final String BORDER_STYLE_DEFAULT = StyleConstants.BORDER_SOLID;
	private static final int BORDER_WIDTH_DEFAULT = 0;
	
	//member variables:
	private String overImageUrl;
	private String downImageUrl;

	private Map _ImageLocalizationMap;
	private Map _overImageLocalizationMap;

	private String textBgColor = "#CCCCCC";
	private boolean limitImageWidth = false;
	private boolean zoomView = false;
	private boolean linkOnImage = true;
	protected boolean useCaching = true;
	private String align;
	private String zoomImageID;
	private String zoomPageID;
	private String zoomImageWidth;
	private String zoomImageHeight;
	protected int imageId = -1;
	private int maxImageWidth = 140;
	private String datasource = null;
	

	
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[17];
		values[0] = super.saveState(ctx);
		values[1] = this.overImageUrl;
		values[2] = this.downImageUrl;
		values[3] = this._ImageLocalizationMap;
		values[4] = this._overImageLocalizationMap;
		values[5] = this.textBgColor;
		values[6] = Boolean.valueOf(this.limitImageWidth);
		values[7] = Boolean.valueOf(this.zoomView);
		values[8] = Boolean.valueOf(this.linkOnImage);
		values[9] = Boolean.valueOf(this.useCaching);
		values[10] = this.align;
		values[11] = this.zoomPageID;
		values[12] = this.zoomImageWidth;
		values[13] = this.zoomImageHeight;
		values[14] = new Integer(this.imageId);
		values[15] = new Integer(this.maxImageWidth);
		values[16] = this.datasource;
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this.overImageUrl = (String) values[1];
		this.downImageUrl = (String) values[2];
		this._ImageLocalizationMap = (Map) values[3];
		this._overImageLocalizationMap = (Map) values[4];
		this.textBgColor = (String) values[5];
		this.limitImageWidth = ((Boolean)values[6]).booleanValue();
		this.zoomView = ((Boolean)values[7]).booleanValue();
		this.linkOnImage = ((Boolean)values[8]).booleanValue();
		this.useCaching = ((Boolean)values[9]).booleanValue();
		this.align = (String)values[10];
		this.zoomImageID = (String)values[11];
		this.zoomImageWidth = (String) values[12];
		this.zoomImageHeight = (String)values[13];
		this.imageId = ((Integer)values[14]).intValue();
		this.maxImageWidth = ((Integer)values[15]).intValue();
		this.datasource = (String) values[16];
	}
	
	public Image()
	{
		this("");
		//setBorder(BORDER_WIDTH_DEFAULT, BORDER_COLOR_DEFAULT, BORDER_STYLE_DEFAULT);
	}
	public Image(String url)
	{
		this(url, "");
		//setBorder(BORDER_WIDTH_DEFAULT, BORDER_COLOR_DEFAULT, BORDER_STYLE_DEFAULT);
	}
	public Image(String url, String name)
	{
		super();
		setTransient(false);
		if ("".equalsIgnoreCase(name)) {
			name = this.generateID();
		}
		setName(name);
		setAlt(name);
		setToolTip(name);
		setURL(url);
		//setBorder(BORDER_WIDTH_DEFAULT, BORDER_COLOR_DEFAULT, BORDER_STYLE_DEFAULT);
	}
	public Image(String name, String url, String overImageUrl)
	{
		super();
		setName(name);
		setAlt(name);
		setToolTip(name);
		setURL(url);
		//setBorder(BORDER_WIDTH_DEFAULT, BORDER_COLOR_DEFAULT, BORDER_STYLE_DEFAULT);
		this.overImageUrl = overImageUrl;
		setOnMouseOut("swapImgRestore()");
		setOnMouseOver("swapImage('" + getName() + "','','" + overImageUrl + "',1)");
	}
	public Image(String name, String url, String overImageUrl, String downImageUrl)
	{
		this(name, url, overImageUrl);
		this.downImageUrl = downImageUrl;
		setOnMouseDown("swapImage('" + getName() + "','','" + downImageUrl + "',1)");
		//setAttribute("onMouseUp","swapImage('"+getName()+"','','"+overImageUrl+"',1)");
	}
	public Image(String url, String name, int width, int height)
	{
		super();
		setName(name);
		setAlt(name);
		setToolTip(name);
		setURL(url);
		setWidth(width);
		setHeight(height);
		//setBorder(BORDER_WIDTH_DEFAULT, BORDER_COLOR_DEFAULT, BORDER_STYLE_DEFAULT);
	}
	/**
	*Fetches an image from the database through the imageservlet or blobcache
	 * @throws SQLException 
	 * @throws EJBException 
	*/
	public Image(ICFile file) throws EJBException, SQLException {
		this(((Integer)file.getPrimaryKey()).intValue());
		this.datasource = file.getDatasource(); 
	}
	/**
	*Fetches an image from the database through the imageservlet or blobcache
	*/
	public Image(int imageId) throws SQLException
	{
		super();
		this.imageId = imageId;
		//setBorder(BORDER_WIDTH_DEFAULT, BORDER_COLOR_DEFAULT, BORDER_STYLE_DEFAULT);
		setName(this.generateID());
	}
	public Image(int imageId, String name) throws SQLException
	{
		this(imageId);
		setName(name);
		setAlt(name);
		setToolTip(name);
	}
	public Image(int imageId, int width, int height) throws SQLException
	{
		this(imageId);
		setWidth(width);
		setHeight(height);
	}
	public Image(int imageId, String name, int width, int height) throws SQLException
	{
		this(imageId, name);
		setWidth(width);
		setHeight(height);
	}
	
	protected void setImageURL(IWContext iwc) throws Exception{

	
		String url;
		if(this.datasource != null){
			url = getICFileSystem(iwc).getFileURI(this.imageId, this.datasource);
		} else {
			url = getICFileSystem(iwc).getFileURI(this.imageId);
		}
		setURL(url);
	}
	
	/**
	 * Sets the over image url and its javascript. locales always win
	 * @param iwc
	 * @throws Exception
	 */
	protected void setOverImageURLAndJavascript(IWContext iwc) throws Exception{
		Integer overImageId = null;
		String finalOverImageUrl = null;
		if (this._overImageLocalizationMap != null && !this._overImageLocalizationMap.isEmpty()){
			Locale currLocale = iwc.getCurrentLocale();
			overImageId = (Integer) this.getOverImageLocalizationMap().get(currLocale);
			if (overImageId == null){
				overImageId = (Integer) this.getImageLocalizationMap().get(iwc.getIWMainApplication().getSettings().getDefaultLocale());
			}
		
			if(overImageId!=null){
				finalOverImageUrl = getICFileSystem(iwc).getFileURI(overImageId.intValue());
			}
		}else if(this.overImageUrl!=null){
			finalOverImageUrl = this.overImageUrl;
		}
			
		
		if(finalOverImageUrl!=null){
			setOnMouseOut("swapImgRestore()");
			setOnMouseOver("swapImage('" + getName() + "','','" + finalOverImageUrl + "',1)");
		}
		
	}
	
	
	public void setLocalizedImage(String localeString, int imageID)
	{
		setLocalizedImage(ICLocaleBusiness.getLocaleFromLocaleString(localeString), imageID);
	}
	public void setLocalizedImage(Locale locale, int imageID)
	{
		getImageLocalizationMap().put(locale, new Integer(imageID));
	}
	
	public void setLocalizedOverImage(String localeString, int overImageID)
	{
		setLocalizedOverImage(ICLocaleBusiness.getLocaleFromLocaleString(localeString), overImageID);
	}
	
	public void setLocalizedOverImage(Locale locale, int overImageID)
	{
		getOverImageLocalizationMap().put(locale, new Integer(overImageID));
	}
	
	private Map getImageLocalizationMap()
	{
		if (this._ImageLocalizationMap == null)
		{
			this._ImageLocalizationMap = new HashMap();
		}
		return this._ImageLocalizationMap;
	}
	
	private Map getOverImageLocalizationMap()
	{
		if (this._overImageLocalizationMap == null){
			this._overImageLocalizationMap = new HashMap();
		}
		return this._overImageLocalizationMap;
	}
	
	public void setOverImageLocalizationMap(Map overImagesmap){
		this._overImageLocalizationMap = overImagesmap;
	}
	
	public void setProperty(String key, String values[])
	{
		if (key.equalsIgnoreCase("url")) {
			setURL(values[0]);
		}
		else if (key.equalsIgnoreCase("width"))
		{
			setWidth(values[0]);
		}
		else if (key.equalsIgnoreCase("height"))
		{
			setHeight(values[0]);
		}
		else if (key.equalsIgnoreCase("border"))
		{
			setHeight(Integer.parseInt(values[0]));
		}
		else if (key.equalsIgnoreCase("image_id"))
		{
			this.setImageID(Integer.parseInt(values[0]));
		}
	}
	public void setBorder(int size, String color, String style) {
		setBorder(Integer.toString(size), color, style);
	}
	public void setBorder(String size, String color, String style) {
		setBorder(size);
		setBorderColor(color);
		setBorderStyle(style);
	}
	public void setBorder(String size)
	{
		setStyleAttribute("border-width", size+"px");
	}
	public void setBorderColor(String color)
	{
		setStyleAttribute("border-color", color);
	}
	public void setBorderStyle(String style) {
		setStyleAttribute("border-style", style);
	}
	public void setBorder(int i)
	{
		setBorder(Integer.toString(i));
	}
	public void setURL(String url)
	{
		setSrc(url);
	}
	public void setSrc(String src)
	{
		setMarkupAttribute("src", src);
	}
	public void setWidth(int width)
	{
		setWidth(Integer.toString(width));
	}
	public void setWidth(String width)
	{
		setMarkupAttribute("width", width);
	}
	public void setHeight(int height)
	{
		setHeight(Integer.toString(height));
	}
	public void setHeight(String height)
	{
		setMarkupAttribute("height", height);
	}
	public void setImageID(int imageID)
	{
		this.imageId = imageID;
	}
	public int getDefaultImageID()
	{
		return this.imageId;
	}
	
	/**
	 * Gets the dominating image id, locales always win
	 * @param iwc
	 * @return
	 */
	public int getImageID(IWContext iwc){
		try
		{
			Integer localizedID = null;
			if (this._ImageLocalizationMap != null){
				Locale currLocale = iwc.getCurrentLocale();
				localizedID = (Integer) this.getImageLocalizationMap().get(currLocale);
				if(localizedID==null){
					localizedID = (Integer) this.getImageLocalizationMap().get(iwc.getIWMainApplication().getSettings().getDefaultLocale());
				}
			}

			if (localizedID == null)
			{
				return this.imageId;
			}
			else
			{
				return localizedID.intValue();
			}
		}
		catch (Exception e)
		{
			return this.imageId;
		}
	}
		


	public void setVerticalSpacing(int spacing)
	{
		setMarkupAttribute("vspace", Integer.toString(spacing));
	}
	public void setHorizontalSpacing(int spacing)
	{
		setMarkupAttribute("hspace", Integer.toString(spacing));
	}
	public void setTextBackgroundColor(String color)
	{
		this.textBgColor = color;
	}
	public String getHeight()
	{
		return getMarkupAttribute("height");
	}
	public String getWidth()
	{
		return getMarkupAttribute("width");
	}
	public String getURL()
	{
		return this.getMarkupAttribute("src");
	}
	/**
	 * Returns true if the image has been set to a source, else false
	 */
	public boolean hasSource()
	{
		return ((getURL() != null) || this.imageId != -1);
	}
	public void setOnClick(String action)
	{
		setMarkupAttribute("onclick", action);
	}
	public void setOnMouseOver(String action)
	{
		setMarkupAttribute("onmouseover", action);
	}
	public void setOnMouseOut(String action)
	{
		setMarkupAttribute("onmouseout", action);
	}
	public void setOnMouseDown(String action)
	{
		setMarkupAttribute("onmousedown", action);
	}
	public void setOnClickImageURL(String clickImageURL)
	{
		this.overImageUrl = clickImageURL;
		setOnClick("swapImgRestore(); swapImage('" + getName() + "','','" + clickImageURL + "',1)");
	}
	public void setOnClickImage(Image image)
	{
		setOnClickImageURL(image.getMediaURL());
	}
	public void setOverImageURL(String overImageURL)
	{
		this.overImageUrl = overImageURL;
	}
	public void setOverImage(Image image)
	{
		this.overImageUrl = image.getMediaURL();
	}
	
	public void setImageToOpenInPopUp(Image image)
	{
		this.setOnClick(
			"img_wnd=window.open('','','width=100,height=100,left='+((screen.width/2)-50)+',top='+((screen.height/2)-50)+',resizable=yes,scrollbars=no'); doopen('"
				+ image.getMediaURL()
				+ "'); return true;");
	}
	public String getOverImageURL()
	{
		return this.overImageUrl;
	}
	public void addMarkupAttributes(Map attributeMap)
	{
		if (attributeMap.containsKey(FileSystemConstants.ZOOMIMAGE) && attributeMap.containsKey(FileSystemConstants.ZOOMPAGE))
		{
			this.zoomImageID =
				attributeMap.containsKey(FileSystemConstants.ZOOMIMAGE) ? (String) attributeMap.get(FileSystemConstants.ZOOMIMAGE) : null;
			this.zoomPageID =
				attributeMap.containsKey(FileSystemConstants.ZOOMPAGE) ? (String) attributeMap.get(FileSystemConstants.ZOOMPAGE) : null;
			this.zoomImageWidth =
				attributeMap.containsKey(FileSystemConstants.ZOOMWIDTH)
					? (String) attributeMap.get(FileSystemConstants.ZOOMWIDTH)
					: "400";
			this.zoomImageHeight =
				attributeMap.containsKey(FileSystemConstants.ZOOMHEIGHT)
					? (String) attributeMap.get(FileSystemConstants.ZOOMHEIGHT)
					: "400";
			attributeMap.remove(FileSystemConstants.ZOOMIMAGE);
			attributeMap.remove(FileSystemConstants.ZOOMPAGE);
			attributeMap.remove(FileSystemConstants.ZOOMWIDTH);
			attributeMap.remove(FileSystemConstants.ZOOMHEIGHT);
		}		

		super.addMarkupAttributes(attributeMap);
	}
	//TODO: remove this variable declaration and move totally to facets:
	//This variable is kept because of legacy reasons but should be replaced with a Facet
	private Script theOldAssociatedScript;
	public void setAssociatedScript(Script myScript)
	{
		if(IWMainApplication.useJSF){
			getFacets().put("image_associatedscript",myScript);
		}
		else{
			this.theOldAssociatedScript = myScript;
		}
	}
	public Script getAssociatedScript()
	{
		if(IWMainApplication.useJSF){
			return (Script)getFacet("image_associatedscript");
		}
		else{
			return this.theOldAssociatedScript;
		}
	}
	public void setAlignment(String alignment)
	{
		this.align = alignment;
	}

	public void setImageLinkZoomView()
	{
		this.zoomView = true;
	}
	//TODO remove this variable declaration and move totally to facets:
	//This variable is kept because of legacy reasons but should be replaced with a Facet
	private Link theOldZoomLink;
	public void setImageZoomLink(Link link)
	{
		if(IWMainApplication.useJSF){
			getFacets().put("zoomlink",link);
		}
		else{
			this.theOldZoomLink = link;
		}
	}
	public Link getImageZoomLink(){
		if(IWMainApplication.useJSF){
			return (Link)getFacet("zoomlink");
		}
		else{
			return this.theOldZoomLink;
		}
	}
	public void setNoImageLink()
	{
		this.linkOnImage = false;
	}
	public void setAlt(String alt)
	{
		setMarkupAttribute("alt", alt);
	}
	public String getAlt()
	{
		return getMarkupAttribute("alt");
	}
	private String getHTMLString(IWContext iwc) throws RemoteException
	{
		//Eiki: this does not seem to support over images or anything???
		
		String markup = iwc.getApplicationSettings().getProperty(Page.MARKUP_LANGUAGE, Page.HTML);
		StringBuffer sPrint = new StringBuffer();
		sPrint.append("<img ");
		//alt always added for standards compliancy
		sPrint.append("alt=\"");
		/** @todo Fix this shitty mix!!! */
		if (getAlt() != null && (getAlt().length() > 2 && !getAlt().substring(0, 2).equals("id")))
		{
			sPrint.append(getAlt());
		}
		sPrint.append("\" ");
		removeMarkupAttribute("alt");
		if (markup.equals(Page.HTML)) {
			sPrint.append("name=\"");
			sPrint.append(getName());
			sPrint.append("\" ");
		}
		if (iwc != null)
		{
			BuilderService bs = getBuilderService(iwc);
			ICDomain d = bs.getCurrentDomain();
			if (d.getURL() != null)
			{
				String src = getMarkupAttribute("src");
				if (src.startsWith("/"))
				{
					String protocol;
					//@todo this is case sensitive and could break! move to IWContext. Also done in Link, SubmitButton, Image and PageIncluder
					if (iwc.getRequest().isSecure())
					{
						protocol = "https://";
					}
					else
					{
						protocol = "http://";
					}
					setMarkupAttribute("src", protocol + d.getURL() + src);
				}
			}
		}
		sPrint.append(getMarkupAttributesString());
		if (this.align != null)
		{
			sPrint.append(" align=\"" + this.align + "\" ");
		}
		sPrint.append(" "+(!markup.equals(Page.HTML) ? "/" : "")+">");
		return sPrint.toString();
	}

	private void getHTMLImage(IWContext iwc)
	{ //optimize by writing in pure html
		try
		{
			setImageURL(iwc);
			setOverImageURLAndJavascript(iwc);
			
				String texti = null;
				String link = null;
				String width = null;
				String height = null;
			
				if (!this.limitImageWidth)
				{
					if ((width != null) && (!width.equalsIgnoreCase("")) && (!width.equalsIgnoreCase("-1")))
					{
						setWidth(width);
					}
					if ((height != null) && (!height.equalsIgnoreCase("")) && (!height.equalsIgnoreCase("-1")))
					{
						setHeight(height);
					}
				}
				else
				{
					if ((width != null) && (!width.equalsIgnoreCase("")) && (!width.equalsIgnoreCase("-1")))
					{
						if (Integer.parseInt(width) > this.maxImageWidth)
						{
							setWidth(this.maxImageWidth);
						}
					}
					else
					{
						setWidth(this.maxImageWidth);
					}
				}
				if ((texti != null) && (!"".equalsIgnoreCase(texti)))
				{
					Table imageTable = new Table(1, 2);
					imageTable.setAlignment(1, 1, "center");
					imageTable.setAlignment(1, 2, "left");
					//imageTable.setCellpadding(0);
					//imageTable.setCellspacing(0);
					imageTable.setColor(1, 2, this.textBgColor);
					String sWidth = getWidth();
					if ((sWidth != null) && (!sWidth.equalsIgnoreCase("")) && (!this.limitImageWidth))
					{
						imageTable.setWidth(sWidth);
					}
					else if (this.limitImageWidth)
					{
						imageTable.setWidth(this.maxImageWidth);
					}
					Text imageText = new Text(texti);
					imageText.setFontSize(1);
					if ((link != null) && (!"".equalsIgnoreCase(link)))
					{ //has a link
						Link textLink = new Link(imageText, link);
						textLink.setTarget("_new");
						textLink.setFontSize(1);
						imageTable.add(textLink, 1, 2); //add the text with the link on it
						//should we add the image with a link? or just the image
						if (this.zoomView)
						{
							Link zoomViewLink = getImageZoomLink();
							if (zoomViewLink != null)
							{
								zoomViewLink.setText(getHTMLString(iwc));
								imageTable.add(zoomViewLink, 1, 1);
							}
							else
							{
								Link imageLink = new Link(getHTMLString(iwc));
								imageLink.addParameter("image_id", this.imageId);
								imageTable.add(imageLink, 1, 1);
							}
						}
						else if ((!this.zoomView) && (this.linkOnImage))
						{
							Link imageLink = new Link(getHTMLString(iwc), link);
							imageLink.setTarget("_new");
							imageTable.add(imageLink, 1, 1);
						}
						else {
							imageTable.add(getHTMLString(iwc), 1, 1);
						}
					}
					else
					{ //or no link
						if (this.zoomView)
						{
							Link zoomViewLink = getImageZoomLink();
							if (zoomViewLink != null)
							{
								zoomViewLink.setText(getHTMLString(iwc));
								imageTable.add(zoomViewLink, 1, 1);
							}
							else
							{
								Link imageLink = new Link(getHTMLString(iwc));
								imageLink.addParameter("image_id", this.imageId);
								imageTable.add(imageLink, 1, 1);
							}
						}
						else {
							imageTable.add(getHTMLString(iwc), 1, 1);
						}
						imageTable.add(imageText, 1, 2);
					}
					renderChild(iwc,imageTable);
				}
				else
				{
					if (this.zoomView)
					{
						Link zoomViewLink = getImageZoomLink();
						if (zoomViewLink != null)
						{
							zoomViewLink.setText(getHTMLString(iwc));
							zoomViewLink._print(iwc);
						}
						else
						{
							Link imageLink = new Link(getHTMLString(iwc));
							imageLink.addParameter("image_id", this.imageId);
							imageLink._print(iwc);
						}
					}
					else {
						print(getHTMLString(iwc));
					}
				}
			//} //end debug
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 
	 * @uml.property name="maxImageWidth"
	 */
	public void setMaxImageWidth(int maxImageWidth) {
		this.limitImageWidth = true;
		this.maxImageWidth = maxImageWidth;
	}

	public void limitImageWidth(boolean limitImageWidth)
	{
		this.limitImageWidth = limitImageWidth;
	}
	/*
	* this uses an undocumented access method to the IWContext which is fetched from the current thread
	* it could brake! Use getMediaURL(IWContext) if possible.
	* @todo implement in the main method if possible
	*/
	public String getMediaURL()
	{
		return getMediaURL(IWContext.getInstance());
	}
	public String getMediaURL(IWContext iwc) {
		return getMediaURL((IWApplicationContext) iwc);
	}
	/**
	 * Use this method for getting an images (stored in the database) url
	 * @todo check if fails with usesOldMedia tables
	 * @param iwc The IWContext
	 * @return
	 */
	public String getMediaURL(IWApplicationContext iwc)
	{
		if (this.imageId != -1){
			String theReturn = "";
			//return MediaBusiness.getMediaURL(imageId, ImageEntity.class, iwc.getApplication());
			try
			{
				theReturn = getICFileSystem(iwc).getFileURI(this.imageId);
			}
			catch (RemoteException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return theReturn;
		}
		else{
			return getURL();
		}
	}
	/**
	 *  return a call to image preloadscript
	 *
	 */
	public static String getPreloadScript(String url)
	{
		return "preLoadImages('" + url + "')";
	}
	public Object clone()
	{
		Image obj = null;
		try
		{
			obj = (Image) super.clone();
			if (this.theOldAssociatedScript != null)
			{
				obj.theOldAssociatedScript = (Script) this.theOldAssociatedScript.clone();
			}
			obj.overImageUrl = this.overImageUrl;
			obj.textBgColor = this.textBgColor;
			obj.limitImageWidth = this.limitImageWidth;
			obj.zoomView = this.zoomView;
			obj.linkOnImage = this.linkOnImage;
			obj.imageId = this.imageId;
			obj.maxImageWidth = this.maxImageWidth;
			obj._ImageLocalizationMap = this._ImageLocalizationMap;
			obj._overImageLocalizationMap = this._overImageLocalizationMap;
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace(System.err);
		}
		return obj;
	}
	public void main(IWContext iwc)
	{
		if (iwc.isParameterSet(PARAM_IMAGE_ID))
		{
			this.imageId = Integer.parseInt(iwc.getParameter(PARAM_IMAGE_ID));
		}
	}
	public void print(IWContext iwc) throws Exception
	{
		if (this.zoomImageID != null)
		{
			Link link = new Link();
			link.addParameter(PARAM_IMAGE_ID, this.zoomImageID);
			link.setPage(Integer.parseInt(this.zoomPageID));
			link.setURL("/index.jsp");
			link.setWindowToOpenScript(
				Window.getWindowCallingScript(
					link.getURL(iwc),
					"",
					false,
					false,
					false,
					false,
					false,
					false,
					false,
					true,
					false,
					Integer.parseInt(this.zoomImageWidth),
					Integer.parseInt(this.zoomImageHeight)));
			setImageZoomLink(link);
			setImageLinkZoomView();
		}
		if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_HTML))
		{
		
			this.imageId = this.getImageID(iwc);
			if (this.imageId == -1)
			{ //from an url
				print(getHTMLString(iwc));
			}
			else
			{ //from the database
				getHTMLImage(iwc);
			}
		}
		else if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_PDF_XML))
		{
			setURL(iwc.getServerURL() + getMediaURL(iwc));
			String markup = getHTMLString(iwc);
			markup = TextSoap.findAndReplace(markup, "img", "image");
			markup = TextSoap.findAndReplace(markup, "src", "url");
			markup = TextSoap.findAndReplace(markup, "width", "plainwidth");
			markup = TextSoap.findAndReplace(markup, "height", "plainheight");
			print(markup);
    }
    
  }

	public void setPadding(int padding) {
		setStyleAttribute("padding: "+padding+"px;");
	}
	
	public void setPaddingLeft(int padding) {
		setStyleAttribute("padding-left: "+padding+"px;");
	}
	
	public void setPaddingRight(int padding) {
		setStyleAttribute("padding-right: "+padding+"px;");
	}
	
	public void setPaddingTop(int padding) {
		setStyleAttribute("padding-top: "+padding+"px;");
	}
	
	public void setPaddingBottom(int padding) {
		setStyleAttribute("padding-bottom: "+padding+"px;");
	}

	public String getName() {
		return getMarkupAttribute("name");
	}
	
	public void setName(String name) {
		setMarkupAttribute("name",name);
	}
	
	public void setDatasource(String source) {
		this.datasource = source;
	}
	 /**
	  * Returns wheather the "goneThroughMain" variable is reset back to false in the restore phase.
	  */
	 protected boolean resetGoneThroughMainInRestore(){
	 	return true;
	 }
	 
	 
	 /** 
	  * implements NonEJBResource
	  */
	public ResourceDescription getResourceDescription() {
		return new ResourceDescription(ICFile.class.getName(), ICFile.class.getName(), true);
	}

	/**
	 * 
	 * implements PropertyDescriptionHolder
	 */
	public List getPropertyDescriptions() {
		List list = new ArrayList();
		list.add( 
			new PropertyDescription(
					"image_id",
					"1",
					getResourceDescription()));
		return list;
	}	

	
}
