//idega 2001 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.app;
import java.util.List;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.data.EntityFinder;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.FrameSet;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Window;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class IWApplication extends FrameSet
{
	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.core";
	private String applicationName;
	public IWApplication()
	{
		this("idegaWeb Application");
	}
	public IWApplication(String applicationName)
	{
		this(applicationName, 600, 400);
	}
	public IWApplication(String applicationName, int initialWidth, int initialHeight)
	{
		this.setApplicationName(applicationName);
		this.setWidth(initialWidth);
		this.setHeight(initialHeight);
	}
	public void _main(IWContext iwc) throws Exception
	{
		if (isChildOfOtherPage())
		{
		}
		else
		{
			super._main(iwc);
		}
	}
	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
		setTitle(applicationName);
	}
	public String getApplicationName()
	{
		return applicationName;
	}
	public Image getIcon()
	{
		return new Image("");
	}
	public static PresentationObject getIWApplicationIcon(Class iwApplicationClass, IWContext iwc)
	{
		ICObject obj = ICObjectBMPBean.getICObject(iwApplicationClass.getName());
		return getIWApplicationIcon(obj, iwApplicationClass, iwc);
	}
	public static PresentationObject getIWApplicationIcon(ICObject obj, IWContext iwc)
	{
		Class iwApplicationClass = null;
		try
		{
			iwApplicationClass = obj.getObjectClass();
		}
		catch (ClassNotFoundException e)
		{
		}
		return getIWApplicationIcon(obj, iwApplicationClass, iwc);
	}
	private static PresentationObject getIWApplicationIcon(ICObject obj, Class iwApplicationClass, IWContext iwc)
	{
		Table icon = new Table(1, 2);
		String name = null;
		if (obj != null)
		{
			name = obj.getName();
		}
		else
		{
			Window instance = Window.getStaticInstance(iwApplicationClass);
			name = instance.getName();
		}
		/**
		
		 * @todo get the right Image
		
		 */
		Image iconImage = null;
		IWBundle bundle = null;
		IWResourceBundle iwrb = null;
		if (obj == null)
		{
			bundle = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
			iconImage = bundle.getImage("IWApplicationIcon.gif");
		}
		else
		{
			bundle = obj.getBundle(iwc.getIWMainApplication());
			if (bundle == null)
			{
				bundle = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
				iconImage = bundle.getImage("IWApplicationIcon.gif");
			}
			else
			{
				iconImage = bundle.getImage("IWApplicationIcon.gif");
			}
			
		}
		iwrb = bundle.getResourceBundle(iwc);
		if(iwrb!=null){
		    name = iwrb.getLocalizedString("iwapplication_name."+name,name);
		}
		Link icon_image = new Link(iconImage);
		icon_image.setWindowToOpen(iwApplicationClass);
		Text icon_text = new Text(name);
		icon_text.setFontSize(1);
		icon_text.setFontColor("black");
		Link icon_link = new Link(name);
		icon_link.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_LARGE);
		icon_link.setWindowToOpen(iwApplicationClass);
		icon.setAlignment(1, 1, "center");
		icon.add(icon_image, 1, 1);
		icon.setAlignment(1, 2, "center");
		icon.add(icon_link, 1, 2);
		return icon;
	}
	public static List getApplictionICObjects()
	{
		try
		{
			return EntityFinder.findAllByColumn(
				com.idega.data.GenericEntity.getStaticInstance(ICObject.class),
				ICObjectBMPBean.getObjectTypeColumnName(),
				ICObjectBMPBean.COMPONENT_TYPE_APPLICATION);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	public void print(IWContext iwc) throws Exception
	{
		if (isChildOfOtherPage())
		{
			//getIWApplicationIcon(this.getClass(), iwc).print(iwc);
			this.renderChild(iwc,getIWApplicationIcon(this.getClass(), iwc));
		}
		else
		{
			super.print(iwc);
		}
	}
} //End class
