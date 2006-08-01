/*
 * $Id: Window.java,v 1.48.2.2 2006/08/01 17:04:54 gimmi Exp $ Created in 2000 by
 * Tryggvi Larusson Copyright (C) 2000-2005 Idega Software hf. All Rights
 * Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 * 
 */
package com.idega.presentation.ui;

import java.util.Iterator;
import java.util.Map;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.datastructures.list.KeyValueList;
import com.idega.util.datastructures.list.KeyValuePair;

/**
 * <p>
 * An instance of this class is a Page but can be used for pages displayed in
 * pop-up windows and such. This class has therefore properties to set
 * width,height etc. of the pop-up window that is opened.
 * </p>
 * Last modified: $Date: 2006/08/01 17:04:54 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.48.2.2 $
 */
public class Window extends Page {

	//static constants:
	private static String emptyString = "";
	//member variables:
	//settings for the window:
	private String title;
	private int width;
	private int height;
	private String url;
	private String xCoordinate = null;
	private String yCoordinate = null;
	private boolean toolbar=false;
	private boolean location=false;
	private boolean scrollbar=false;
	private boolean directories=false;
	private boolean menubar=false;
	private boolean status=false;
	private boolean titlebar=false;
	private boolean resizable=true;
	private boolean fullscreen=false;
	private boolean autoResize = false;
	private boolean autoPosition = false;
	private int autoXCoordinateOffset = 0;
	private int autoYCoordinateOffset = 0;
	// If this window is constructed to open an instance of an object in a new
	// Window via ObjectInstanciator
	private Class classToInstanciate;
	private Class templatePageClass;
	private String templateForObjectInstanciation;

	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[23];
		values[0] = super.saveState(ctx);
		values[1] = this.title;
		values[2] = new Integer(this.width);
		values[3] = new Integer(this.height);
		values[4] = this.url;
		values[5] = this.xCoordinate;
		values[6] = this.yCoordinate;
		values[7] = Boolean.valueOf(this.toolbar);
		values[8] = Boolean.valueOf(this.location);
		values[9] = Boolean.valueOf(this.scrollbar);
		values[10] = Boolean.valueOf(this.directories);
		values[11] = Boolean.valueOf(this.menubar);
		values[12] = Boolean.valueOf(this.status);
		values[13] = Boolean.valueOf(this.titlebar);
		values[14] = Boolean.valueOf(this.resizable);
		values[15] = Boolean.valueOf(this.fullscreen);
		values[16] = Boolean.valueOf(this.autoResize);
		values[17] = Boolean.valueOf(this.autoPosition);
		values[18] = new Integer(this.autoXCoordinateOffset);
		values[19] = new Integer(this.autoYCoordinateOffset);
		values[20] = this.classToInstanciate;
		values[21] = this.templatePageClass;
		values[22] = this.templateForObjectInstanciation;
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this.title = (String)values[1];
		this.width = ((Integer)values[2]).intValue();
		this.height = ((Integer)values[3]).intValue();
		this.url = (String)values[4];
		this.xCoordinate= (String)values[5];
		this.yCoordinate = (String)values[6];
		this.toolbar = ((Boolean)values[7]).booleanValue();
		this.location = ((Boolean)values[8]).booleanValue();
		this.scrollbar = ((Boolean)values[9]).booleanValue();
		this.directories = ((Boolean)values[10]).booleanValue();
		this.menubar = ((Boolean)values[11]).booleanValue();
		this.status = ((Boolean)values[12]).booleanValue();
		this.titlebar = ((Boolean)values[13]).booleanValue();
		this.resizable = ((Boolean)values[14]).booleanValue();
		this.fullscreen = ((Boolean)values[15]).booleanValue();
		this.autoResize = ((Boolean)values[16]).booleanValue();
		this.autoPosition = ((Boolean)values[17]).booleanValue();
		this.autoXCoordinateOffset = ((Integer)values[18]).intValue();
		this.autoYCoordinateOffset = ((Integer)values[19]).intValue();
		this.classToInstanciate = (Class)values[20];
		this.templatePageClass = (Class)values[21];
		this.templateForObjectInstanciation = (String)values[22];
	}
	
	
	public Window() {
		this(emptyString);
		String className = this.getClass().getName();
		setTitle(className.substring(className.lastIndexOf(".") + 1));
	}

	/**
	 * Opens a window displaying only 1 image
	 * 
	 * @param image Image to be displayed in the window
	 */
	public Window(Image image) {
		image.setID("onlyImage");
		this.setName(image.getName());
		this.setAllMargins(0);
		add(image);
		this.setOnLoad("window.resizeTo(document.onlyImage.width +10, document.onlyImage.height +22)");
		initialize();
	}

	public Window(String name) {
		this(name, 400, 400);
	}

	public Window(int width, int heigth) {
		this(emptyString, width, heigth);
		String className = this.getClass().getName();
		setTitle(className.substring(className.lastIndexOf(".") + 1));
	}

	public Window(String name, int width, int height) {
		// super();
		// setTitle(name);
		// this.height=height;
		// this.width=width;
		// newURL=false;
		// setSettings();
		// this(name,width,height,IWMainApplication.windowOpenerURL);
		this.setName(name);
		this.setWidth(width);
		this.setHeight(height);
		initialize();
	}

	public Window(String name, String url) {
		this(name, 400, 400, url);
	}

	public Window(String name, int width, int height, String url) {
		// super();
		setTitle(name);
		this.height = height;
		this.width = width;
		this.url = url;
		// newURL=true;
		setSettings();
		initialize();
	}
	


	public Window(String name, String classToInstanciate, String template) {
		// this(name,400,400,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
		this(name, 400, 400);
		try {
			this.setClassToInstanciate(RefactorClassRegistry.forName(classToInstanciate), template);
		}
		catch (Exception e) {
			throw new RuntimeException(e.toString() + e.getMessage());
		}
	}

	/*
	 * public Window(String name,Class classToInstanciate,Class template){
	 * //this(name,400,400,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
	 * this(name,400,400);
	 * this.setClassToInstanciate(classToInstanciate,template); }
	 */
	public Window(String name, Class classToInstanciate) {
		// this(name,400,400,IWMainApplication.getObjectInstanciatorURL(classToInstanciate));
		initialize();
	}
	
	private void initialize() {
		setTransient(false);
	}

	private void setSettings() {
		setID();
		setToolbar(false);
		setLocation(false);
		setScrollbar(true);
		setDirectories(false);
		setMenubar(false);
		setStatus(false);
		setTitlebar(false);
		setResizable(false);
	}

	public void setToolbar(boolean ifToolbar) {
		this.toolbar = ifToolbar;
	}

	public void setLocation(boolean ifLocation) {
		this.location = ifLocation;
	}

	public void setScrollbar(boolean ifScrollbar) {
		this.scrollbar = ifScrollbar;
	}

	public void setDirectories(boolean ifDirectories) {
		this.directories = ifDirectories;
	}

	public void setMenubar(boolean ifMenubar) {
		this.menubar = ifMenubar;
	}

	public void setStatus(boolean ifStatus) {
		this.status = ifStatus;
	}

	public void setTitlebar(boolean ifTitlebar) {
		this.titlebar = ifTitlebar;
	}

	public void setResizable(boolean ifResizable) {
		this.resizable = ifResizable;
	}

	public void setFullScreen(boolean ifFullScreen) {
		this.fullscreen = ifFullScreen;
	}

	/**
	 * <p>
	 * Returns the URL for the window.<br>
	 * For new systems (i.e. with IWMainApplication.newUrlScheme set to true) this includes the identifier 
	 * to the window but in older systems it only contains the base url for the WindowOpener Servlet.<br>
	 * e.g. in old systems this returns: '/servlet/WindowOpener' <br>
	 * in new systems it returns '/workspace/window/6CBA9ED8-E26C-11D9-BEAE-000A95AE300E'
	 * </p>
	 */
	public String getURL(IWContext iwc) {
		String ret = null;
		if (this.url == null) {
			if(IWMainApplication.useNewURLScheme){
				ret = iwc.getIWMainApplication().getWindowOpenerURI(this.getClass());
			} else {
				ret = iwc.getIWMainApplication().getWindowOpenerURI();
			}
		}
		else {
			ret = this.url;
		}
		// System.out.println("ret1 = " + ret);
		/*ICDomain d = iwc.getDomain();
		String url = d.getURL();
		if (url != null) {
			if (ret.startsWith("/")) {
				ret = url + ret;
			}
		}*/
		// System.out.println("ret2 = " + ret);
		return ret;
	}

	public void setBackgroundColor(String color) {
		setMarkupAttribute("bgcolor", color);
	}

	public void setTextColor(String color) {
		setMarkupAttribute("text", color);
	}

	public void setAlinkColor(String color) {
		setMarkupAttribute("alink", color);
	}

	public void setVlinkColor(String color) {
		setMarkupAttribute("vlink", color);
	}

	public void setLinkColor(String color) {
		setMarkupAttribute("link", color);
	}

	public void setMarginWidth(int width) {
		setMarkupAttribute("marginwidth", Integer.toString(width));
	}

	public void setMarginHeight(int height) {
		setMarkupAttribute("marginheight", Integer.toString(height));
	}

	public void setLeftMargin(int leftmargin) {
		setMarkupAttribute("leftmargin", Integer.toString(leftmargin));
	}

	public void setTopMargin(int topmargin) {
		setMarkupAttribute("topmargin", Integer.toString(topmargin));
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public String getName() {
		return this.getTitle();
	}

	public int getWindowWidth() {
		return this.width;
	}

	public String getWidth() {
		return String.valueOf(this.width);
	}

	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * This method overrides the one in PresentationObject and width must be and
	 * integer
	 * 
	 * @param width
	 *            the int width
	 */
	public void setWidth(String width) {
		this.width = Integer.parseInt(width);
	}

	public int getWindowHeight() {
		return this.height;
	}

	public String getHeight() {
		return String.valueOf(this.height);
	}

	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * This method overrides the one in PresentationObject and height must be
	 * and integer
	 * 
	 * @param height
	 *            the int height
	 */
	public void setHeight(String height) {
		this.height = Integer.parseInt(height);
	}

	/*
	 * public String getUrl(){ return this.url; }
	 */
	public void setCoordinateX(String coordX) {
		this.xCoordinate = coordX;
	}

	public String getCoordinateX() {
		if (this.xCoordinate != null) {
			return this.xCoordinate;
		}
//		else if (autoPosition) {
//			return getMouseCoordinateX() + "+(" + autoXCoordinateOffset + ")";
//		}
		return null;
	}

	public void setCoordinateY(String coordY) {
		this.yCoordinate = coordY;
	}

	public String getCoordinateY() {
		if (this.yCoordinate != null) {
			return this.yCoordinate;
		}
//		else if (autoPosition) {
//			return getMouseCoordinateY() + "+(" + autoYCoordinateOffset + ")";
//		}
		return null;
	}

	public void setURL(String url) {
		this.url = url;
		// newURL=true;
	}

	/**
	 * Gets the URL to a (popup) Window class of class windowClass
	 * 
	 * @param windowClass
	 *            the Class of the Window to instanciate
	 * @param iwc
	 * @return the URL (without http:// and hostname)
	 */
	public static String getWindowURL(Class windowClass, IWApplicationContext iwc) {
		return iwc.getIWMainApplication().getWindowOpenerURI(windowClass);
	}
	
	/**
	 * Gets the URL to a (popup) Window class of class windowClass that does not require the used to be logged it
	 * 
	 * @param windowClass
	 *            the Class of the Window to instanciate
	 * @param iwc
	 * @return the URL (without http:// and hostname)
	 */
	public static String getPublicWindowURL(Class windowClass, IWApplicationContext iwc) {
		return iwc.getIWMainApplication().getPublicWindowOpenerURI(windowClass);
	}

	/**
	 * Gets the URL to a (popup) Window class of class windowClass with added
	 * extra parameters to send to the window.
	 * 
	 * @param windowClass
	 *            the Class of the Window to instanciate
	 * @param iwc
	 * @param parameterName
	 *            name of parameter to send
	 * @param parameterValue
	 *            value of parameter to send
	 * @return the URL (without http:// and hostname)
	 */
	public static String getWindowURLWithParameter(Class windowClass, IWApplicationContext iwc, String parameterName,
			String parameterValue) {
		StringBuffer buffer = new StringBuffer(getWindowURL(windowClass, iwc));
		if (buffer.indexOf("?") < 0) {
			buffer.append('?');
		}
		else {
			buffer.append('&');
		}
		buffer.append(parameterName).append('=').append(parameterValue);
		return buffer.toString();
	}

	
	/**
	 * Gets the URL to a (popup) Window class of class windowClass with added
	 * extra parameters to send to the window.
	 * 
	 * @param windowClass
	 *            the Class of the Window to instanciate
	 * @param iwc
	 * @param parameterMap
	 *            a Map of key=value (parameter=parameterValue) parameters to
	 *            add to the URL
	 * @return the URL (without http:// and hostname)
	 */
	public static String getPublicWindowURLWithParameters(Class windowClass, IWApplicationContext iwc, KeyValueList parameterMap) {
		return getWindowURLWithParameters(windowClass, iwc, parameterMap, true);
	}	
	public static String getWindowURLWithParameters(Class windowClass, IWApplicationContext iwc, KeyValueList parameterMap) {
		return getWindowURLWithParameters(windowClass, iwc, parameterMap, false);
	}

	private static String getWindowURLWithParameters(Class windowClass, IWApplicationContext iwc, KeyValueList parameterMap, boolean publicWindow) {
		String url = null;
		if (publicWindow) {
			url = getPublicWindowURL(windowClass, iwc);
		} else {
			url = getWindowURL(windowClass, iwc);
		}

		if (parameterMap == null || parameterMap.isEmpty()) {
			return url;
		}
		StringBuffer buffer = new StringBuffer(url);
		if (buffer.indexOf("?") < 0) {
			buffer.append('?');
		}
		else {
			buffer.append('&');
		}
		Iterator iterator = parameterMap.iterator();
		boolean firstParameter = true;
		while (iterator.hasNext()) {
			if (firstParameter) {
				firstParameter = false;
			}
			else {
				buffer.append('&');
			}
			KeyValuePair pair = (KeyValuePair) iterator.next();
			String parameterName = (String) pair.getKey();
			String parameterValue = (String) pair.getValue();
			buffer.append(parameterName).append('=').append(parameterValue);
		}
		return buffer.toString();

	}
	
	
	/**
	 * Gets the URL to a (popup) Window class of class windowClass with added
	 * extra parameters to send to the window.
	 * 
	 * @param windowClass
	 *            the Class of the Window to instanciate
	 * @param iwc
	 * @param parameterMap
	 *            a Map of key=value (parameter=parameterValue) parameters to
	 *            add to the URL
	 * @return the URL (without http:// and hostname)
	 */
	public static String getWindowURLWithParameters(Class windowClass, IWApplicationContext iwc, Map parameterMap) {
		KeyValueList list = new KeyValueList(parameterMap.size());
		list.putAll(parameterMap);
		return getWindowURLWithParameters(windowClass, iwc, list);
	}

	public static String getCallingScriptString(Class windowClass, IWApplicationContext iwac) {
		return getCallingScriptString(windowClass, true, iwac);
	}

	public static Window getStaticInstance(Class windowClass) {
		Window windowInstance = (Window) getIWMainApplication().getStaticWindowInstances().get(windowClass);
		if (windowInstance == null) {
			try {
				windowInstance = (Window) windowClass.newInstance();
				getIWMainApplication().getStaticWindowInstances().put(windowClass, windowInstance);
			}
			catch (Exception e) {
			}
		}
		return windowInstance;
	}

	protected static IWMainApplication getIWMainApplication() {
		return IWMainApplication.getDefaultIWMainApplication();
	}

	public static String getCallingScriptString(Class windowClass, boolean includeURL, IWApplicationContext iwac) {
		String url = getWindowURL(windowClass, iwac);
		return getCallingScriptString(windowClass, url, includeURL, iwac);
	}

	public static String getCallingScriptString(Class windowClass, String url, boolean includeURL,
			IWApplicationContext iwac) {
		String theURL = null;
		Window win = getStaticInstance(windowClass);
		if (includeURL) {
			theURL = url;
		}
		else {
			theURL = "";
		}
		if (win == null) {
			// return
			// "window.open('"+theURL+"','tempwindow','resizable=yes,toolbar=yes,location=no,directories=no,status=yes,scrollbars=yes,menubar=yes,titlebar=yes,width=500,height=500')";
			return getWindowCallingScript(theURL, "tempwindow", true, true, true, true, true, true, true, true, false,
					500, 500, win.getCoordinateX(), win.getCoordinateY());
		}
		// return
		// "window.open('"+theURL+"','"+win.getTarget()+"','resizable="+win.returnCheck(windowInstance.resizable)+",toolbar="+win.returnCheck(windowInstance.toolbar)+",location="+win.returnCheck(win.location)+",directories="+win.returnCheck(win.directories)+",status="+win.returnCheck(win.status)+",scrollbars="+win.returnCheck(win.scrollbar)+",menubar="+win.returnCheck(win.menubar)+",titlebar="+win.returnCheck(win.titlebar)+win.returnFullScreen()+",width="+win.getWidth()+",height="+win.getHeight()+"')";
		return getWindowCallingScript(theURL, win.getTarget(), win.toolbar, win.location, win.directories, win.status,
				win.menubar, win.titlebar, win.scrollbar, win.resizable, win.fullscreen, win.getWindowWidth(),
				win.getWindowHeight(), win.getCoordinateX(), win.getCoordinateY());
	}

	public static String getCallingScriptString(Class windowClass, String url, String target, boolean includeURL,
			IWApplicationContext iwac) {
		String theURL = null;
		Window win = getStaticInstance(windowClass);
		if (includeURL) {
			theURL = url;
		}
		else {
			theURL = "";
		}
		if (win == null) {
			// return
			// "window.open('"+theURL+"','tempwindow','resizable=yes,toolbar=yes,location=no,directories=no,status=yes,scrollbars=yes,menubar=yes,titlebar=yes,width=500,height=500')";
			return getWindowCallingScript(theURL, "tempwindow", true, true, true, true, true, true, true, true, false,
					500, 500, win.getCoordinateX(), win.getCoordinateY());
		}
		// return
		// "window.open('"+theURL+"','"+win.getTarget()+"','resizable="+win.returnCheck(windowInstance.resizable)+",toolbar="+win.returnCheck(windowInstance.toolbar)+",location="+win.returnCheck(win.location)+",directories="+win.returnCheck(win.directories)+",status="+win.returnCheck(win.status)+",scrollbars="+win.returnCheck(win.scrollbar)+",menubar="+win.returnCheck(win.menubar)+",titlebar="+win.returnCheck(win.titlebar)+win.returnFullScreen()+",width="+win.getWidth()+",height="+win.getHeight()+"')";
		return getWindowCallingScript(theURL, target, win.toolbar, win.location, win.directories, win.status,
				win.menubar, win.titlebar, win.scrollbar, win.resizable, win.fullscreen, win.getWindowWidth(),
				win.getWindowHeight(), win.getCoordinateX(), win.getCoordinateY());
	}

	public static String getCallingScript(String URL, int width, int height) {
		return getWindowCallingScript(URL, "Window", true, true, true, true, true, true, true, true, false, width,
				height, null, null);
	}

	public static String getCallingScript(String URL, int width, int height, int coordX, int coordY) {
		return getWindowCallingScript(URL, "Window", true, true, true, true, true, true, true, true, false, width,
				height, String.valueOf(coordX), String.valueOf(coordY));
	}

	public static String getCallingScript(String URL) {
		return getCallingScript(URL, 500, 500);
	}

	public String getCallingScriptString(IWContext iwc, String url) {
		// return
		// "window.open('"+url+"','"+getTarget()+"','resizable="+returnCheck(resizable)+",toolbar="+returnCheck(toolbar)+",location="+returnCheck(location)+",directories="+returnCheck(directories)+",status="+returnCheck(status)+",scrollbars="+returnCheck(scrollbar)+",menubar="+returnCheck(menubar)+",titlebar="+returnCheck(titlebar)+returnFullScreen()+",width="+getWidth()+",height="+getHeight()+"')";
		return getWindowCallingScript(url, getTarget(), this.toolbar, this.location, this.directories, this.status, this.menubar, this.titlebar,
				this.scrollbar, this.resizable, this.fullscreen, getWindowWidth(), getWindowHeight(), getCoordinateX(),
				getCoordinateY());
	}

	public String getCallingScriptString(IWContext iwc) {
		return getCallingScriptString(iwc, getURL(iwc));
	}

	protected String getCallingScriptStringForForm(IWContext iwc) {
		// return
		// "window.open('"+getURL(iwc)+"','"+getName()+"','resizable="+returnCheck(resizable)+",toolbar="+returnCheck(toolbar)+",location="+returnCheck(location)+",directories="+returnCheck(directories)+",status="+returnCheck(status)+",scrollbars="+returnCheck(scrollbar)+",menubar="+returnCheck(menubar)+",titlebar="+returnCheck(titlebar)+",width="+getWidth()+",height="+getHeight()+"')";
		/*
		 * if (this.getName().equalsIgnoreCase("untitled")){ setID();
		 * setName(getID()); }
		 */
		// return
		// "window.open('','"+getTarget()+"','resizable="+returnCheck(resizable)+",toolbar="+returnCheck(toolbar)+",location="+returnCheck(location)+",directories="+returnCheck(directories)+",status="+returnCheck(status)+",scrollbars="+returnCheck(scrollbar)+",menubar="+returnCheck(menubar)+",titlebar="+returnCheck(titlebar)+returnFullScreen()+",width="+getWidth()+",height="+getHeight()+"')";
		return getWindowCallingScript("", getTarget(), this.toolbar, this.location, this.directories, this.status, this.menubar, this.titlebar,
				this.scrollbar, this.resizable, this.fullscreen, getWindowWidth(), getWindowHeight(), this.xCoordinate, this.yCoordinate);
	}

	/**
	 * 
	 * using js function iwopenwindow from iw_core.js
	 * (Address,Name,ToolBar,Location,Directories,Status,Menubar,Titlebar,Scrollbars,Resizable,Width,Height)
	 */
	public static String getWindowCallingScript(String url, String name, boolean tool, boolean loc, boolean dir,
			boolean stat, boolean menu, boolean title, boolean scroll, boolean resize, boolean fullscr, int theWidth,
			int theHeight) {
		return getWindowCallingScript(url, name, tool, loc, dir, stat, menu, title, scroll, resize, fullscr, theWidth,
				theHeight, null, null);
	}

	/**
	 * 
	 * using js function iwopenwindow from iw_core.js
	 * (Address,Name,ToolBar,Location,Directories,Status,Menubar,Titlebar,Scrollbars,Resizable,Width,Height,Xcoord,Ycoord)
	 */
	public static String getWindowCallingScript(String url, String name, boolean tool, boolean loc, boolean dir,
			boolean stat, boolean menu, boolean title, boolean scroll, boolean resize, boolean fullscr, int theWidth,
			int theHeight, String xCoordinate, String yCoordinate) {
		String no = "0";
		String yes = "1";
		String sp = "'";
		StringBuffer buf = new StringBuffer("iwOpenWindow('").append(url).append("','").append(name).append("',");
		buf.append(sp).append(tool ? yes : no).append("','").append(loc ? yes : no).append("',");
		buf.append(sp).append(dir ? yes : no).append("','").append(stat ? yes : no).append("',");
		buf.append(sp).append(menu ? yes : no).append("','").append(title ? yes : no).append("',");
		buf.append(sp).append(scroll ? yes : no).append("','").append(resize ? yes : no).append("',");
		buf.append(sp).append(theWidth).append("','").append(theHeight).append("'");
		if (xCoordinate != null) {
			buf.append(",").append(xCoordinate);
		}
		if (yCoordinate != null) {
			buf.append(",").append(yCoordinate);
		}
		buf.append(")");
		return buf.toString();
	}

	public static String getWindowArgumentCallingScript(Class windowClass) {
		Window win = getStaticInstance(windowClass);
		if (win == null) {
			return getWindowArgumentCallingScript(true, true, true, true, true, true, true, true, false, 500, 500,
					win.getCoordinateX(), win.getCoordinateY());
		}
		return getWindowArgumentCallingScript(win.toolbar, win.location, win.directories, win.status, win.menubar,
				win.titlebar, win.scrollbar, win.resizable, win.fullscreen, win.getWindowWidth(),
				win.getWindowHeight(), win.getCoordinateX(), win.getCoordinateY());
	}

	public static String getWindowArgumentCallingScript(boolean tool, boolean loc, boolean dir, boolean stat,
			boolean menu, boolean title, boolean scroll, boolean resize, boolean fullscr, int theWidth, int theHeight,
			String xCoordinate, String yCoordinate) {
		String no = "0";
		String yes = "1";
		StringBuffer buf = new StringBuffer("toolbar=").append(tool ? yes : no).append(",").append("location=").append(
				loc ? yes : no).append(",");
		buf.append("directories=").append(dir ? yes : no).append(",").append("status").append(stat ? yes : no).append(
				",");
		buf.append("menubar=").append(menu ? yes : no).append(",").append("titlebar=").append(title ? yes : no).append(
				",");
		buf.append("scrollbars=").append(scroll ? yes : no).append(",").append("resizable=").append(resize ? yes : no).append(
				",");
		buf.append("width=").append(theWidth).append(",").append("height=").append(theHeight).append("");
		if (xCoordinate != null) {
			buf.append(",").append("left=").append(xCoordinate);
		}
		if (yCoordinate != null) {
			buf.append(",").append("top=").append(yCoordinate);
		}
		return buf.toString();
	}

//This script is now a standard script called iwOpenWindow in iw_core.js	
//	public static String windowScript() {
//		StringBuffer js = new StringBuffer();
//		// getCoordinate script tracks the mouse coordinates
//		js.append(getCoordinateScript());
//		js.append("\n\n");
//		js.append("\tfunction openwindow(Address,Name,ToolBar,Location,Directories,Status,Menubar,Titlebar,Scrollbars,Resizable,Width,Height,Xcoord,Ycoord) {  \n");
//		js.append("\t\t// usage openwindow(addr,name,yes/no,yes/no,yes/no,yes/no,yes/no,yes/no,yes/no,yes/no,width,height,xcoord,ycoord) \n");
//		js.append("\t\tvar option = \"toolbar=\" + ToolBar ");
//		js.append("+ \",location=\" + Location  ");
//		js.append("+ \",directories=\" + Directories  ");
//		js.append("+ \",status=\" + Status  ");
//		js.append("+ \",menubar=\" + Menubar  ");
//		js.append("+ \",titlebar=\" + Titlebar  ");
//		js.append("+ \",scrollbars=\" + Scrollbars  ");
//		js.append("+ \",resizable=\"  + Resizable  ");
//		// js.append("+ \",fullscreen=\" + FullScreen \n");
//		js.append("+ \",width=\" + Width  ");
//		js.append("+ \",height=\" + Height; \n");
//		js.append("\t if(Xcoord) option+=\",left=\" + Xcoord; \n");
//		js.append("\t if(Ycoord) option+=\",top=\" + Ycoord; \n");
//		js.append("\t\tvar new_win = window.open(Address, Name, option );\n");
//		// js.append("new_win.document.write(option)");
//		js.append("\t}");
//		return js.toString();
//	}

//	public static String getCoordinateScript() {
//		StringBuffer script = new StringBuffer();
//		script.append("var IE = document.all?true:false; ");
//		script.append("if (!IE){ document.captureEvents(Event.MOUSEMOVE);}");
//		script.append("document.onmousemove = getMouseXY;");
//		script.append("var coordX = 0;");
//		script.append("var coordY = 0;");
//		script.append("function getMouseXY(e) { ");
//		script.append("	if (IE) { ");
//		script.append("      coordX = event.clientX + document.body.scrollLeft;");
//		script.append("      coordY = event.clientY + document.body.scrollTop;");
//		script.append("   }");
//		script.append("   else {  ");
//		script.append("	   coordX = e.pageX;");
//		script.append("      coordY = e.pageY;");
//		script.append("   }  ");
//		script.append("	if (coordX < 0){coordX = 0;}");
//		script.append("   if (coordY < 0){cordY = 0;}  ");
//		// script.append("window.defaultStatus =\"x=\"+coordX+ \" y=\"+coordY
//		// ;");
//		script.append("   return true;");
//		script.append("} ");
//		return script.toString();
//	}

//	public String getMouseCoordinateX() {
//		return "coordX";
//	}
//
//	public String getMouseCoordinateY() {
//		return "coordY";
//	}

	public void setBackgroundImage(String imageURL) {
		setMarkupAttribute("background", imageURL);
	}

	public void setBackgroundImage(Image backgroundImage) {
		setBackgroundImage(backgroundImage.getURL());
	}

	public boolean doPrint(IWContext iwc) {
		boolean returnBoole;
		if (iwc.getParameter("idegaspecialrequesttype") == null) {
			/* no special request */
			/* Check if there is a parent object */
			if (getParentObject() == null) {
				/* if there is no parent object then do print directly out */
				returnBoole = true;
			}
			else {
				/* if there is a parent object then do not print directly out */
				returnBoole = false;
			}
		}
		else if (iwc.getParameter("idegaspecialrequesttype").equals("window")
				&& iwc.getParameter("idegaspecialrequestname").equals(this.getName())) {
			returnBoole = true;
		}
		else {
			returnBoole = false;
		}
		return returnBoole;
	}

	public String getTarget() {
		return getID();
	}

	public Object clone() {
		Window obj = null;
		try {
			obj = (Window) super.clone();
			obj.title = this.title;
			obj.width = this.width;
			obj.height = this.height;
			obj.url = this.url;
			obj.classToInstanciate = this.classToInstanciate;
			obj.templateForObjectInstanciation = this.templateForObjectInstanciation;
			obj.templatePageClass = this.templatePageClass;
			obj.toolbar = this.toolbar;
			obj.location = this.location;
			obj.scrollbar = this.scrollbar;
			obj.directories = this.directories;
			obj.menubar = this.menubar;
			obj.status = this.status;
			obj.titlebar = this.titlebar;
			obj.resizable = this.resizable;
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	public void setClassToInstanciate(Class presentationObjectClass) {
		this.classToInstanciate = presentationObjectClass;
		this.setURL(IWContext.getInstance().getIWMainApplication().getObjectInstanciatorURI(presentationObjectClass));
	}

	/*
	 * public void setClassToInstanciate(Class presentationObjectClass,Class
	 * pageTemplateClass){ setClassToInstanciate(presentationObjectClass);
	 * this.templatePageClass=pageTemplateClass;
	 * this.setURL(IWContext.getInstance().getIWMainApplication().getObjectInstanciatorURI(presentationObjectClass,pageTemplateClass)); }
	 */
	public void setClassToInstanciate(Class presentationObjectClass, String template) {
		setClassToInstanciate(presentationObjectClass);
		this.templateForObjectInstanciation = template;
		this.setURL(IWContext.getInstance().getIWMainApplication().getObjectInstanciatorURI(presentationObjectClass,
				template));
	}

	/*
	 * public void print(IWContext iwc)throws IOException{
	 * 
	 * if ( doPrint(iwc) ){ if (! isAttributeSet("bgcolor")){
	 * setBackgroundColor(iwc.getDefaultPrimaryInterfaceColor()); }
	 * 
	 * if (getLanguage().equals("HTML")){
	 * 
	 * //if (getInterfaceStyle().equals(" something ")){ //} //else{ if
	 * (this.url == null){ println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML
	 * 4.0 Transitional//EN\" \"http://www.w3.org/TR/REC-html40/loose.dtd\">\n<html>");
	 * println("\n<head>"); if ( getAssociatedScript() != null){
	 * getAssociatedScript()._print(iwc); } println("\n<meta
	 * http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n<META
	 * NAME=\"generator\" CONTENT=\"idega arachnea\">\n"); println("<title>"+getTitle()+"</title>");
	 * println("</head>\n<body "+getAttributeString()+" >\n");
	 * super.print(iwc); println("\n</body>\n</html>"); } //} } } }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObject#initInMain(com.idega.presentation.IWContext)
	 */
	protected void initInMain(IWContext iwc) throws Exception {
		if (isFocusAllowedOnLoad() && !isChildOfOtherPage() && !isInFrameSet()) {
			setOnLoad("focus()");
		}
		/*
		 * if(autoResize){ setOnLoad(getAutoResizeScript()); }
		 */
		/*
		 * if(autoPosition) setOnLoad(getMoveToScript());
		 */
		super.initInMain(iwc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.Page#getLocalizedTitle(com.idega.presentation.IWContext)
	 */
	public String getLocalizedTitle(IWContext iwc) {
		// TODO Auto-generated method stub
		return getTitle();
	}

	/*
	 * public void setAutoResize(boolean auto){ setAutoResize(auto,0,0); }
	 * 
	 * public void setAutoResize(boolean auto,int xcoord,int ycoord){
	 * this.autoResize = auto; this.autoCoordinateX = xcoord;
	 * this.autoCoordinateY = ycoord; }
	 */
	public void setToMousePosition(boolean flag) {
		this.autoPosition = flag;
	}

	/**
	 * @deprecated
	 * <p>
	 * Does not work any more since coordX and coordY no longer exist
	 * </p>
	 * @param xCoordinateOffset
	 * @param yCoordinateOffset
	 */
	public void setMousePositionOffsets(int xCoordinateOffset, int yCoordinateOffset) {
		this.autoPosition = true;
		this.autoXCoordinateOffset = xCoordinateOffset;
		this.autoYCoordinateOffset = yCoordinateOffset;
	}

	/*
	 * public String getAutoResizeScript(){ StringBuffer script = new
	 * StringBuffer(); //script.append("if (top.frames.length!=0){");
	 * //script.append(" top.location=self.document.location; ");
	 * script.append(" self.resizeTo(screen.availWidth,screen.availHeight) ;");
	 * script.append(getMoveToScript()); return script.toString(); }
	 * 
	 * public String getMoveToScript(){ StringBuffer script = new
	 * StringBuffer(); script.append("
	 * self.moveTo(").append(autoCoordinateX).append(",").append(autoCoordinateY).append(")
	 * ;"); return script.toString(); }
	 */
	/**
	 * Flag allowing window to get the focus on load, when not a child of
	 * another page or To be overwritten in subclasses
	 * 
	 * @return true by default
	 */
	protected boolean isFocusAllowedOnLoad() {
		return true;
	}
}// End class
