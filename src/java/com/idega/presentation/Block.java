/*
 * $Id: Block.java,v 1.78 2008/02/22 08:20:09 valdas Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.idega.core.builder.data.ICPage;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWStyleManager;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.block.presentation.Builderaware;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;

/**
 * This is the base class for all idegaWeb Blocks.<br>
 * Subclasses of this class should not render themselves out (with the print method), and it is presumed that
 * their functionality is done with the main() method in old style idegaWeb.
 * This class has functionality regarding caching and how the main method is processed in JSF.
 * 
 * Last modified: $Date: 2008/02/22 08:20:09 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.78 $
 */
public class Block extends PresentationObjectContainer implements Builderaware {

	//static variables:
	private static Map permissionKeyMap = new Hashtable();
	public final static String IW_BLOCK_CACHE_KEY = "iw_not_cached";
	public static boolean usingNewAcessControlSystem = true;
	static final String newline = "\n";
	
	//Instance variables:
	private String cacheKey = null;
	private String derivedCacheKey = null;
	private boolean cacheable = false;
	private long cacheInterval;
	private int targetObjInst = -1;
	private int targetObjInstset = -2;
	private boolean editPermission = false;
	private boolean debugParameters = false;
	private String blockWidth = null;
	private long iSystemTime = 0;
	
	public Block() {
		setDefaultWidth();
		setTransient(false);
	}

	public String getBundleIdentifier() {
		return IW_CORE_BUNDLE_IDENTIFIER;
	}
	

	/**
	 * Override to add styles (names) to stylesheet.  Add name (String) as key and style (String) as value.
	 */
	public Map getStyleNames() {
		//return IWConstants.getDefaultStyles();
		return null;
	}

	/**
	 * 
	 * @uml.property name="cacheKey"
	 */
	public String getCacheKey() {
		return IW_BLOCK_CACHE_KEY;
	}


	public String getLocalizedNameKey() {
		return "block";
	}

	public String getLocalizedNameValue() {
		return "Block";
	}

	public String getLocalizedName(IWContext iwc) {
		return getLocalizedString(getLocalizedNameKey(), getLocalizedNameValue(), iwc);
	}

	/////// target features ///////
	/////// target code begins ////////////

	public void setTargetObjectInstance(ICObjectInstance instance) {
		if (instance != null) {
			this.targetObjInst = instance.getID();
		}
	}

	public int getTargetObjectInstance() {
		if (this.targetObjInst > 0) {
			return this.targetObjInst;
		}
		else {
			return getICObjectInstanceID();
		}
	}

	public void setAsObjectInstanceTarget(Link link) {
		if (this.targetObjInst > 0) {
			link.setTargetObjectInstance(getTargetObjectInstance());
		}
	}

	public boolean isTarget() {
		return this.targetObjInstset == this.targetObjInst;
	}
	/////// target code ends ////////////

	////// debugging parameters /////////

	public void setToDebugParameters(boolean debugPrm) {
		this.debugParameters = debugPrm;
	}

	public boolean deleteBlock(int ICObjectInstanceId) {
		System.err.println("method deleteBlock(int ICObjectInstanceId) not implemented in class " + this.getClass().getName());
		return true;
	}


	public boolean isAdministrator(IWContext iwc) throws Exception {
		if (usingNewAcessControlSystem) {
			return iwc.hasEditPermission(this);
		}
		else {
			return false;
			//return AccessControl.isAdmin(iwc);
		}
	}

	/**
	 * <H2>Unimplemented</H2>
	 */
	public boolean isDeveloper(IWContext iwc) throws Exception {
		return false;
	}

	/**
	 * <H2>Unimplemented</H2>
	 */
	public boolean isUser(IWContext iwc) throws Exception {
		return false;
	}

	/**
	 * <H2>Unimplemented</H2>
	 */
	public boolean isMemberOf(IWContext iwc, String groupName) throws Exception {
		return false;
	}

	/**
	 * <H2>Unimplemented</H2>
	 */
	public boolean hasPermission(String permissionType, PresentationObject obj, IWContext iwc) throws Exception {
		return iwc.getAccessController().hasPermission(permissionType, obj, iwc);
	}

	/* public boolean hasPermission(String permissionType,IWContext iwc)throws Exception{
	    return hasPermission(permissionType,iwc,this);
	  }*/

	public String getModuleName() {
		return this.getClass().getName();
	}

	/**
	 * Implement in subclasses:
	 */
	public void registerPermissionKeys() {
	}

	/*
	protected void registerPermissionKey(String permissionKey,String localizeableKey){
	  Map m = getPermissionKeyClassMap();
	  m.put(permissionKey,localizeableKey);
	}
	*/
	protected void registerPermissionKey(String permissionKey) {
		Map m = getPermissionKeyMap();
		if (m.containsKey(getClassName())) {
			List l = (List) m.get(getClassName());
			l.add(permissionKey);
		}
		else {
			List l = new ArrayList();
			l.add(permissionKey);
			m.put(getClassName(), l);
		}
	}

	/**
	 * 
	 * @uml.property name="permissionKeyMap"
	 */
	private Map getPermissionKeyMap() {
		return permissionKeyMap;
	}

	/*
	  private Map getPermissionKeyClassMap(){
	    Map m = (Map)getPermissionKeyMap().get(this.getClass());
	    if(m==null){
	      m = new Hashtable();
	      getPermissionKeyMap().put(this.getClass(),m);
	    }
	    return m;
	  }
	*/
	public String[] getPermissionKeys() {
		return getPermissionKeys(getClass());
	}

	public String[] getPermissionKeys(Block obj) {
		return getPermissionKeys(obj.getClass());
	}

	public String[] getPermissionKeys(Class PresentationObjectClass) {
		List l = (List) getPermissionKeyMap().get(PresentationObjectClass.getName());
		if (l != null) {
			return (String[]) l.toArray(new String[0]);
		}
		return null;
	}

	private static long twentyMinutes = 60 * 1000 * 20;

	/**
	 * 
	 * @uml.property name="manager"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	private IWStyleManager manager;


	public void setCacheable(String cacheKey) {
		setCacheable(cacheKey, twentyMinutes);
	}
	/**@ todo how do this.cacheKey and setCacheKey work together??**/
	public void setCacheable(String cacheKey, long millisecondsInterval) {
		this.cacheable = true;
		this.cacheKey = cacheKey;
		this.cacheInterval = millisecondsInterval;
	}

	public void beginCacheing(IWContext iwc, StringBuffer buffer) throws Exception {
		PrintWriter servletWriter = iwc.getWriter();
		iwc.setCacheing(true);
		PrintWriter writer = new BlockCacheWriter(servletWriter, buffer);
		if(IWMainApplication.useJSF){
		    ResponseWriter rWriter = new BlockCacheResponseWriter(iwc.getResponseWriter(),buffer);
		    iwc.setCacheResponseWriter(rWriter);
		}
		iwc.setCacheWriter(writer);
	}
	
	public void endCacheing(IWContext iwc, StringBuffer buffer) {
		iwc.setCacheing(false);
		IWCacheManager.getInstance(iwc.getIWMainApplication()).setObject(getOriginalCacheKey(), getDerivedCacheKey(), buffer, this.cacheInterval);
	}

	public boolean hasEditPermission() {
		return this.editPermission;
	}

	public void _main(IWContext iwc) throws Exception {
		this.editPermission = iwc.hasEditPermission(this);
		this.manager = IWStyleManager.getInstance();

		if (this.debugParameters) {
			debugParameters(iwc);
		}

		if (iwc.isParameterSet(TARGET_OBJ_INS)) {
			this.targetObjInstset = Integer.parseInt(iwc.getParameter(TARGET_OBJ_INS));
		}

		if (this.targetObjInst <= 0) {
			this.targetObjInst = getParentObjectInstanceID();
		}

		if (getStyleNames() != null) {
			String prefix = getBundle(this.getIWUserContext()).getBundleName();
			if (prefix != Builderaware.IW_CORE_BUNDLE_IDENTIFIER) {
				prefix = prefix.substring(prefix.lastIndexOf(".") + 1) + "_";
			}

			Map styles = getStyleNames();
			Iterator iter = styles.keySet().iterator();
			while (iter.hasNext()) {
				String style = (String) iter.next();
				if (!this.manager.isStyleSet(prefix + style)) {
					this.manager.setStyle(prefix + style, (String) styles.get(style));
				}
			}
		}

		if (isCacheable(iwc)) {
			setCacheKey(iwc);
			if (isCacheValid(iwc)) {
			}
			else {
				super._main(iwc);
			}
		}
		else {
			super._main(iwc);
		}
	}

	public void encodeBegin(FacesContext fc)throws IOException{
		this.iSystemTime = System.currentTimeMillis();
		super.encodeBegin(fc);
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeEnd(javax.faces.context.FacesContext)
	 */
	public void encodeEnd(FacesContext arg0) throws IOException {
		long endTime = System.currentTimeMillis();
		String renderingText = (endTime - this.iSystemTime) + " ms";
		castToIWContext(arg0).getResponseWriter().writeComment(renderingText);
		super.encodeEnd(arg0);
	}
	 
	/**
	 * <p>
	 * The default implementation for the print function for Blocks. This method
	 * implements the default Cache (based on IWCacheManager) functionality for caching
	 * the rendered output of this Block.<br>
	 * This implementation is final and therefore can not be overridden.
	 * </p>
	 */
	public final void print(IWContext iwc) throws Exception {
		if (isCacheable(iwc)) {
			if (isCacheValid(iwc)) {
				printPreCachedContent(iwc);
			}
			else {
				//the threads will wait locked on the class for this Block
				synchronized(getClass()){
					if(!isCacheValid(iwc)){
						//only the first thread should go in here and actually cache:
						StringBuffer buffer = new StringBuffer();
						beginCacheing(iwc, buffer);
						super.print(iwc);
						endCacheing(iwc, buffer);
					}
					else{
						//the second thread and all after that 
						//should just print out the cached content.
						printPreCachedContent(iwc);
					}
				}
			}
		}
		else {
			super.print(iwc);
		}
	}

	/**
	 * <p>
	 * Prints out the content that has already been cached into a StringBuffer in IWCacheManager.
	 * </p>
	 * @param iwc
	 */
	protected void printPreCachedContent(IWContext iwc){
		IWCacheManager cacher = iwc.getIWMainApplication().getIWCacheManager();
		StringBuffer buffer = (StringBuffer) cacher.getObject(getDerivedCacheKey());
		//iwc.getWriter().print(buffer.toString());
		print(buffer.toString());
	}
	
	public Text getStyleText(String text, String styleName) {
		return getStyleText(new Text(text),styleName);	
	}
	
	public Text getStyleText(Text text, String styleName) {
		return (Text) setStyle(text,styleName);	
	}
	
	public Link getStyleLink(String link, String styleName) {
		return getStyleLink(new Link(link),styleName);	
	}
	
	public Link getStyleLink(Link link, String styleName) {
		return (Link) setStyle(link,styleName);	
	}
	
	public PresentationObject getStyleObject(PresentationObject object, String styleName) {
		return setStyle(object, styleName);
	}
	
	/**
	 * Gets a prefixed stylename to use for objects, with prefix specific for the bundle used by this block
	 * if the block is in the core bundle, no prefix is added
	 * @param styleName
	 * @return stylename
	 */
	private String getStyleName(String styleName, boolean isLink){
		if ( getIWUserContext() != null ) {
			String prefix = getBundle(getIWUserContext()).getBundleName();
			if (prefix != Builderaware.IW_CORE_BUNDLE_IDENTIFIER) {
				prefix = prefix.substring(prefix.lastIndexOf(".") + 1) + "_";
				styleName = prefix+styleName;
			}
		}
		if (this.manager != null) {
			if (!this.manager.isStyleSet(styleName)) {
				this.manager.setStyle(styleName, "");
			}
			if (autoCreateGlobalHoverStyles() && !this.manager.isStyleSet(styleName + ":hover")) {
				this.manager.setStyle(styleName + ":hover", "");
			}
		}
					
		return styleName;
	}

	/**
	 * Gets a prefixed stylename to use for objects, with prefix specific for the bundle used by this block
	 * if the block is in the core bundle, no prefix is added
	 * @param styleName
	 * @return stylename
	 */
	public String getStyleName(String styleName){
		return getStyleName(styleName, false);
	}
	
	private PresentationObject setStyle(PresentationObject obj, String styleName, boolean isLink) {
		obj.setStyleClass(getStyleName(styleName, isLink));
		return obj;
	}

	public PresentationObject setStyle(PresentationObject obj, String styleName) {
		return setStyle(obj, styleName, false);
	}

	private boolean isCacheValid(IWContext iwc) {
		boolean valid = false;
		if (this.cacheable) {
			if (getDerivedCacheKey() != null) {
				valid = IWCacheManager.getInstance(iwc.getIWMainApplication()).isCacheValid(getDerivedCacheKey());
			}
		}

		return valid;
	}

	/**
	 * 
	 * @uml.property name="derivedCacheKey"
	 */
	protected String getDerivedCacheKey() {
		return this.derivedCacheKey;
	}


	private String getOriginalCacheKey() {
		return this.cacheKey;
	}

	/** cache specifically for view right and for edit rights**/
	private void setCacheKey(IWContext iwc) {
		this.derivedCacheKey = this.cacheKey + getCacheState(iwc, getCachePrefixString(iwc));
		//    cacheKey += getCacheState(iwc,getCachePrefixString(iwc));
		/**@todo remove debug**/
		//debug("cachKey = "+cacheKey);
	}

	/**
	 * Default string is currentlocale+hasEditPermission
	 */
	protected String getCachePrefixString(IWContext iwc) {
		int instanceID = this.getICObjectInstanceID();
		boolean edit = hasEditPermission();
		String locale = iwc.getCurrentLocale().toString();
		boolean isSecure = iwc.getRequest().isSecure();
		String[] addedPermissionKeys= getPermissionKeys();
		String addPerm = "";
		if(addedPermissionKeys!=null){
			StringWriter permissions = new StringWriter(addedPermissionKeys.length);
			for (int i = 0; i < addedPermissionKeys.length; i++) {
				boolean perm = iwc.hasPermission(addedPermissionKeys[i],this);
				permissions.write(perm?"1":"0");
			}
			addPerm = permissions.toString();
		}
		
		return (instanceID + locale + edit + isSecure+addPerm);
	}

	/**
	 * Override this method if you want to prevent some states from being cached.
	 * This method is called is before getCacheState()
	 * @param iwc
	 * @return
	 */
	protected boolean isCacheable(IWContext iwc) {
		//if(IWMainApplication.useJSF){
			//cache temporarily disabled in JSF while cache bug is resolved
		//	return false;
		//}
		//else{
			return this.cacheable;
		//}
	}

	/**
	 * 
	 * @uml.property name="cacheable"
	 */
	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}

	/**
	 * Override this method for correct caching. The cacheStateprefix will always be <br>
	 * of the form CurrentLocal+edit(boolean) unless you override getCachePrefixString(iwc) <br>
	 * It is better to return a string with that string prefixed to it unless the block output <br>
	 * is the same for every local and edit/view rights.
	 * @return cacheStatePrefix
	 */
	protected String getCacheState(IWContext iwc, String cacheStatePrefix) {
		return cacheStatePrefix;
	}

	/**
	 * Override this method to invalidate something other than the current state.
	 * Default: iwc.getApplication().getIWCacheManager().invalidateCache(cacheKey);
	 */
	public void invalidateCache(IWContext iwc) {
		invalidateCache(iwc.getIWMainApplication());
		//debug("INVALIDATING : "+getCacheKey(iwc));
	}

	public void invalidateCache(IWMainApplication iwma) {
		if (getDerivedCacheKey() != null) {
			iwma.getIWCacheManager().invalidateCache(getDerivedCacheKey());
		}
	}

	/**
	 * Default: iwc.getApplication().getIWCacheManager().invalidateCache(cacheKey+suffix);
	 */
	public void invalidateCache(IWContext iwc, String suffix) {
		if (getOriginalCacheKey() != null) {
			iwc.getIWMainApplication().getIWCacheManager().invalidateCache(getOriginalCacheKey() + suffix);
		//debug("INVALIDATING : "+getCacheKey(iwc)+suffix);
		}
	}

	public static Block getCacheableObject(PresentationObject objectToCache, String cacheKey, long millisecondsInterval) {
		Block obj = new Block();
		obj.add(objectToCache);
		obj.setCacheable(cacheKey, millisecondsInterval);
		return obj;
	}

	public Object clonePermissionChecked(IWUserContext iwc, boolean askForPermission) {
		if (iwc != null) {
			//this.setIWApplicationContext(iwc.getApplicationContext());
			//this.setIWUserContext(iwc);
		}
		if (askForPermission || iwc != null) {
			if (iwc.getApplicationContext().getIWMainApplication().getAccessController().hasViewPermission(this,iwc)) {
				return this.clone();
			}
			else {
				return NULL_CLONE_OBJECT;
			}
		}
		else {
			return this.clone();
		}
	}

	public Object clone() {
		Block obj = (Block) super.clone();

		obj.cacheable = this.cacheable;
		obj.cacheInterval = this.cacheInterval;
		obj.targetObjInst = this.targetObjInst;
		if (this.cacheKey != null) {
			obj.cacheKey = this.cacheKey;
		}
		if (this.derivedCacheKey != null) {
			obj.derivedCacheKey = this.derivedCacheKey;
		}
		return obj;
	}
	
	protected boolean autoCreateGlobalHoverStyles() {
		return false;
	}
	
	/*
	 * Begin Overrided methods from JSF's UIComponent:
	 */	
	
	public String getComponentType(){
		return "iw.block";
	}
	
	public void decode(FacesContext fc){
		super.decode(fc);
	}
	
	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(context, values[0]);
		this.cacheKey = (String) values[1];
		this.derivedCacheKey = (String) values[2];
		this.cacheable = ((Boolean) values[3]).booleanValue();
		this.cacheInterval = ((Long)values[4]).longValue();
		this.targetObjInst = ((Integer)values[5]).intValue();
		this.targetObjInstset = ((Integer)values[6]).intValue();
		this.editPermission = ((Boolean)values[7]).booleanValue();
		this.debugParameters = ((Boolean)values[8]).booleanValue();
		this.blockWidth = (String)values[9];
		
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
	 */
	public Object saveState(FacesContext context) {
		Object values[] = new Object[10];
		values[0] = super.saveState(context);
		values[1] = this.cacheKey;
		values[2] = this.derivedCacheKey;
		values[3] = Boolean.valueOf(this.cacheable);
		values[4] = new Long(this.cacheInterval);
		values[5] = new Integer(this.targetObjInst);
		values[6] = new Integer(this.targetObjInstset);
		values[7] = Boolean.valueOf(this.editPermission);
		values[8] = Boolean.valueOf(this.debugParameters);
		values[9] = this.blockWidth;
		return values;
	}
	
	/*
	 * End Overrided methods from JSF's UIComponent:
	 */		
	
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
	
	public void setWidth(String width) {
		this.blockWidth = width;
	}
	
	public String getWidth() {
		return this.blockWidth;
	}
	
	protected void setDefaultWidth() {
		this.blockWidth = Table.HUNDRED_PERCENT;
	}
	
	
	/**
	** Returns wheather the "goneThroughMain" variable is reset back to false in the restore phase.
	**/
	protected boolean resetGoneThroughMainInRestore(){
		return true;
	}

	protected ICPage getPage(IWApplicationContext iwac, String pageId) {
		try {
			return getBuilderService(iwac).getICPage(pageId);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected boolean isPagePublished(ICPage page) {		
		return page == null ? false : page.isPublished();
	}
	 
	protected boolean isPageHiddenInMenu(ICPage page) {
		return page == null ? false : page.isHidePageInMenu();
	}
	 	
}