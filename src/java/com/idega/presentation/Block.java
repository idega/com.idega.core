//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.idega.core.component.data.ICObjectInstance;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWStyleManager;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.block.presentation.Builderaware;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;

/**
 *
 * A base class for idegaWeb Blocks
 *
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public class Block extends PresentationObjectContainer implements Builderaware {

	//static variables:
	private static Map permissionKeyMap = new Hashtable();
	public final static String IW_BLOCK_CACHE_KEY = "iw_not_cached";
	public static boolean usingNewAcessControlSystem = false;
	private static final String concatter = "_";
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
		if (instance != null)
			this.targetObjInst = instance.getID();
	}

	public int getTargetObjectInstance() {
		if (targetObjInst > 0)
			return targetObjInst;
		else
			return getICObjectInstanceID();
	}

	public void setAsObjectInstanceTarget(Link link) {
		if (targetObjInst > 0)
			link.setTargetObjectInstance(getTargetObjectInstance());
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
		cacheable = true;
		this.cacheKey = cacheKey;
		this.cacheInterval = millisecondsInterval;
	}

	public void beginCacheing(IWContext iwc, StringBuffer buffer) throws Exception {
		PrintWriter servletWriter = iwc.getWriter();
		iwc.setCacheing(true);
		PrintWriter writer = new BlockCacheWriter(servletWriter, buffer);
		iwc.setCacheWriter(writer);
	}
	
	public void endCacheing(IWContext iwc, StringBuffer buffer) {
		iwc.setCacheing(false);
		IWCacheManager.getInstance(iwc.getIWMainApplication()).setObject(getOriginalCacheKey(), getDerivedCacheKey(), buffer, cacheInterval);
	}

	public boolean hasEditPermission() {
		return editPermission;
	}

	public void _main(IWContext iwc) throws Exception {
		editPermission = iwc.hasEditPermission(this);
		manager = new IWStyleManager();

		if (debugParameters) {
			debugParameters(iwc);
		}

		if (iwc.isParameterSet(TARGET_OBJ_INS))
			targetObjInstset = Integer.parseInt(iwc.getParameter(TARGET_OBJ_INS));

		if (targetObjInst <= 0)
			targetObjInst = getParentObjectInstanceID();

		if (getStyleNames() != null) {
			String prefix = getBundle(this.getIWUserContext()).getBundleName();
			if (prefix != this.IW_CORE_BUNDLE_IDENTIFIER)
				prefix = prefix.substring(prefix.lastIndexOf(".") + 1) + "_";

			Map styles = getStyleNames();
			Iterator iter = styles.keySet().iterator();
			while (iter.hasNext()) {
				String style = (String) iter.next();
				if (!manager.isStyleSet(prefix + style))
					manager.setStyle(prefix + style, (String) styles.get(style));
			}
		}

		if (isCacheable(iwc)) {
			setCacheKey(iwc);
			if (isCacheValid(iwc)) {
			}
			else {
				//beginCacheing(iwc);
				super._main(iwc);
				//endCacheing(iwc);
			}
		}
		else {
			super._main(iwc);
		}
	}

	/**
	 * The default implementation for the print function for Blocks. This implementation is final and therefore can not be overrided.
	 */
	public final void print(IWContext iwc) throws Exception {
		if (isCacheable(iwc)) {
			if (isCacheValid(iwc)) {
				StringBuffer buffer = (StringBuffer) IWCacheManager.getInstance(iwc.getIWMainApplication()).getObject(getDerivedCacheKey());
				iwc.getWriter().print(buffer.toString());
			}
			else {
				StringBuffer buffer = new StringBuffer();
				beginCacheing(iwc, buffer);
				super.print(iwc);
				endCacheing(iwc, buffer);
			}
		}
		else {
			super.print(iwc);
		}
	}

	public Text getStyleText(String text, String styleName) {
		return (Text) getStyleText(new Text(text),styleName);	
	}
	
	public Text getStyleText(Text text, String styleName) {
		return (Text) setStyle(text,styleName);	
	}
	
	public Link getStyleLink(String link, String styleName) {
		return (Link) getStyleLink(new Link(link),styleName);	
	}
	
	public Link getStyleLink(Link link, String styleName) {
		return (Link) setStyle(link,styleName);	
	}
	
	public PresentationObject getStyleObject(PresentationObject object, String styleName) {
		return (PresentationObject) setStyle(object, styleName);
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
			if (prefix != this.IW_CORE_BUNDLE_IDENTIFIER) {
				prefix = prefix.substring(prefix.lastIndexOf(".") + 1) + "_";
				styleName = prefix+styleName;
			}
		}
		if (manager != null) {
			if (!manager.isStyleSet(styleName))
				manager.setStyle(styleName, "");
			if (autoCreateGlobalHoverStyles() && !manager.isStyleSet(styleName + ":hover"))
				manager.setStyle(styleName + ":hover", "");
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
		if (cacheable) {
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
		return derivedCacheKey;
	}


	private String getOriginalCacheKey() {
		return cacheKey;
	}

	/** cache specifically for view right and for edit rights**/
	private void setCacheKey(IWContext iwc) {
		derivedCacheKey = cacheKey + getCacheState(iwc, getCachePrefixString(iwc));
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
		if(IWMainApplication.USE_JSF){
			//cache temporarily disabled in JSF while cache bug is resolved
			return false;
		}
		else{
			return this.cacheable;
		}
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
		if (getDerivedCacheKey() != null)
			iwma.getIWCacheManager().invalidateCache(getDerivedCacheKey());
	}

	/**
	 * Default: iwc.getApplication().getIWCacheManager().invalidateCache(cacheKey+suffix);
	 */
	public void invalidateCache(IWContext iwc, String suffix) {
		if (getOriginalCacheKey() != null)
			iwc.getIWMainApplication().getIWCacheManager().invalidateCache(getOriginalCacheKey() + suffix);
		//debug("INVALIDATING : "+getCacheKey(iwc)+suffix);
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
			if (iwc.hasViewPermission(this)) {
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
		values[1] = (String)cacheKey;
		values[2] = derivedCacheKey;
		values[3] = Boolean.valueOf(cacheable);
		values[4] = new Long(cacheInterval);
		values[5] = new Integer(targetObjInst);
		values[6] = new Integer(targetObjInstset);
		values[7] = Boolean.valueOf(editPermission);
		values[8] = Boolean.valueOf(debugParameters);
		values[9] = blockWidth;
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
		blockWidth = width;
	}
	
	public String getWidth() {
		return blockWidth;
	}
	
	protected void setDefaultWidth() {
		blockWidth = Table.HUNDRED_PERCENT;
	}
	
	
	 /**
	  * Returns wheather the "goneThroughMain" variable is reset back to false in the restore phase.
	  */
	 protected boolean resetGoneThroughMainInRestore(){
	 	return true;
	 }
	 	
}