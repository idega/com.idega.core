/*
 * $Id: PresentationObject.java,v 1.183 2009/04/22 12:50:27 valdas Exp $
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
import java.rmi.RemoteException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.event.EventListenerList;

import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.component.data.ICObjectInstanceHome;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.business.ICFileSystemFactory;
import com.idega.core.idgenerator.business.UUIDGenerator;
import com.idega.event.GenericState;
import com.idega.event.IWActionListener;
import com.idega.event.IWEvent;
import com.idega.event.IWEventMachine;
import com.idega.event.IWLinkEvent;
import com.idega.event.IWLinkListener;
import com.idega.event.IWPresentationState;
import com.idega.event.IWStateMachine;
import com.idega.event.IWSubmitEvent;
import com.idega.event.IWSubmitListener;
import com.idega.event.OldEventSystemHelperBridge;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWPresentationLocation;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.UnavailableIWContext;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.RenderUtils;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.logging.LoggingHelper;
import com.idega.util.reflect.Property;
import com.idega.util.reflect.PropertyCache;
import com.idega.util.text.TextSoap;
import com.idega.util.text.TextStyler;
/**
 * This is the base class for all user interface components in old idegaWeb.<br>
 * PresentationObject now extends JavaServerFaces' UIComponent which is now the new standard base component.<br>
 * In all new applications it is recommended to either extend UIComponentBase or IWBaseComponent.
 *
 * Last modified: $Date: 2009/04/22 12:50:27 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.183 $
 */
public class PresentationObject
//implements Cloneable{
extends UIComponentBase
implements Cloneable, PresentationObjectType{//,UIComponent{
	//private final static String IW_BUNDLE_IDENTIFIER="com.idega.idegaweb";

	//Static variables
	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.core";
	public final static String CORE_IW_BUNDLE_IDENTIFIER = IW_BUNDLE_IDENTIFIER;
	public final static String WIDTH = "width";
	public final static String HEIGHT = "height";
	public final static String HORIZONTAL_ALIGNMENT = "align";
	protected static final String slash = CoreConstants.SLASH;
	public static String sessionEventStorageName = IWMainApplication.IWEventSessionAddressParameter;
	public static final PresentationObject NULL_CLONE_OBJECT = new PresentationObject();
	public static final String TARGET_OBJ_INS = "tois";
	// constant for compoundId
	public static String COMPOUNDID_COMPONENT_DELIMITER = ":";
	// constant for compoundId
	public static String COMPOUNDID_CHILD_NUMBER_DELIMITER = "_";

	public static final String CLICK = "onclick";


	//temporary legacy variables will be removed in future versions.
	//private transient HttpServletRequest _request;
	//private transient HttpServletResponse _response;
	//private transient PrintWriter out;
	private transient String markupLanguage;
	//private transient IWApplicationContext _iwac;
	//private transient IWUserContext _iwuc;
	//private transient IWContext eventIWContext = null;
	private transient PresentationObject _templateObject =null;
	private transient boolean goneThroughRenderPhase=false;

	//state hold variables:
	public Map<String, String> attributes;
	private String name;
	//protected UIComponent parentObject;
	private boolean doPrint = true;
	private String errorMessage;
	protected boolean hasBeenAdded = false;
	protected String treeID;
	private boolean goneThroughMain = false;
	private int ic_object_instance_id=-1;
	private int ic_object_id=-1;
	/**
	 * @deprecated Do not use this function
	 */
	@Deprecated
	public EventListenerList listenerList = null;
	public EventListenerList _listenerList = null;
	private Dictionary<String, Object> eventAttributes = null;
	private String UniqueInstanceName;
	private boolean listenerAdded = false;
	public String eventLocationString = "";
	protected boolean initializedInMain = false;
	private boolean _useBuilderObjectControl = true;
	private boolean _belongsToParent = false;
	private boolean _changeInstanceIDOnInheritance = false;
	private boolean _allowPagePermissionInheritance = true;
	private IWLocation _location = new IWPresentationLocation();
	private GenericState defaultState = null;
	// artificial prefix compound_id
	private String artificialCompoundId = null;
	// former compound id (necessary to watch changes of the compoundId)
	private String formerCompoundId = null;
	private TextStyler _styler;
	private String _objTemplateID = null;

	//JSF variables duplicated and overridden because of cloning:
	protected Map<String, UIComponent> facetMap;
	protected List<UIComponent> childrenList;

	//Marker to mark if this component instance is restored via the JSF state restoring mechanism
	private boolean isStateRestored=false;
	private String xmlId;
	private boolean resetGoneThroughMainInRestore=false;
	private boolean supportsMultipleMainCalls=false;
	private boolean renderForLoggedOut = true;
	private boolean renderForLoggedIn = true;

	/**
	 * Default constructor.
	 * Should only be called by sublasses.
	 */
	protected PresentationObject() {
		setTransient(true);
	}

	/**
	 * @return The parent (subclass of PresentationObjectContainer) of the
	 *         current object.
	 * If the parent is not an instance of PresentationObject then this method will return null
	 * to maintain backwards compatability.
	 */
	protected PresentationObject getParentObject() {
		try{
			return (PresentationObject)getParent();
		} catch(ClassCastException e){
			//If the parent is not a PresentationObject then return null
			//to maintain backwards compatability.
			return null;
		}
	}

	protected String generateID() {
		String UUID = UUIDGenerator.getInstance().generateId();
		return "iwid" + UUID.substring(UUID.lastIndexOf("-") + 1);
	}
	protected String setID()
	{
		return setID(generateID());
	}

	public String getID() {
		String theReturn = getMarkupAttribute("id");
		if (StringUtil.isEmpty(theReturn)) {
			return setID();
		}

		return theReturn;
	}

	public PresentationObject getRootParent()
	{
		PresentationObject tempobj=null;
		try{
			tempobj = getParentObject();
		}
		catch(ClassCastException cce){}
		if (tempobj == null)
		{
			return null;
		}
		else
		{
			while (tempobj.getParentObject() != null)
			{
				tempobj = tempobj.getParentObject();
			}
			return tempobj;
		}
	}
	public void setParentObject(PresentationObject pObject)
	{
		setParent(pObject);
	}
	/**
	 * Initializes variables contained in the IWContext object
	 */
	public void initVariables(IWContext iwc) throws IOException
	{
		/*if(!IWMainApplication.useJSF){
			//These variables are not set when the JSF environment is enabled.
			this._request = iwc.getRequest();
			this._response = iwc.getResponse();
			this.out = iwc.getWriter();
		}*/
		this.markupLanguage = iwc.getMarkupLanguage();
		if (this.markupLanguage == null)
		{
			this.markupLanguage = IWConstants.MARKUP_LANGUAGE_HTML;
		}

	}
	protected void cleanVariables(IWContext iwc){
		/*this._request=null;
		this._response=null;
		this.markupLanguage=null;
		this.out=null;*/
	}

	protected void initInMain(IWContext iwc) throws Exception
	{
		initializeInMain(iwc);
		this.initializedInMain = true;
	}
	/**
	 *
	 */
	public void initializeInMain(IWContext iwc) throws Exception
	{
	}

	/**
	 *
	 * @uml.property name="doPrint"
	 */
	public void setDoPrint(boolean ifDoPrint) {
		this.doPrint = ifDoPrint;
	}

	public boolean doPrint(IWContext iwc)
	{
		if (this.doPrint)
		{
			UIComponent parent = getParent();
			if (parent == null)
			{
				return this.doPrint;
			}
			else
			{
				if(parent instanceof PresentationObject){
					return ((PresentationObject)parent).doPrint(iwc);
				}

			}
		}
		else
		{
			return false;
		}
		return this.doPrint;
	}
	protected void setMarkupAttributes(Map<String, String> attributes)
	{
		getMarkupAttributes().putAll(attributes);
	}
	public void setMarkupAttribute(String attributeName, String attributeValue)
	{
		if(attributeName!=null && attributeValue!=null){
			getMarkupAttributes().put(attributeName,attributeValue);
		}
	}
	public void removeMarkupAttribute(String attributeName)
	{
		if (attributeName != null)
		{
			getMarkupAttributes().remove(attributeName);
		}
	}
	public void setMarkupAttribute(String attributeName, boolean attributeValue)
	{
		setMarkupAttribute(attributeName, String.valueOf(attributeValue));
	}
	public void setMarkupAttribute(String attributeName, int attributeValue)
	{
		setMarkupAttribute(attributeName, Integer.toString(attributeValue));
	}

	/**
	 * Sets the attribute with value attributeValue but, if there was previously a value
	 * there old value will be kept and the new value be added with the semicolon separator ;
	 * @param attributeName
	 * @param attributeValue
	 */
	public void setMarkupAttributeMultivalued(String attributeName, String attributeValue) {
		setMarkupAttributeMultivalued(attributeName, attributeValue, ";");
	}

	/**
	 * Sets the attribute with value attributeValue but, if there was previously a value
	 * there old value will be kept and the new value be added with the supplied seperator
	 * @param attributeName
	 * @param attributeValue
	 * @param seperator
	 */
	public void setMarkupAttributeMultivalued(String attributeName, String attributeValue, String seperator) {
		String previousAttribute = getMarkupAttribute(attributeName);
		if (previousAttribute == null)
		{
			setMarkupAttribute(attributeName, attributeValue);
		}
		else
		{
			if (previousAttribute.indexOf(attributeValue) == -1)
			{
				String parameterValue = previousAttribute;
				if (previousAttribute.endsWith(seperator)) {
					parameterValue = parameterValue + attributeValue;
				}
				else {
					parameterValue = parameterValue + seperator + attributeValue;
				}
				setMarkupAttribute(attributeName, parameterValue);
			}
		}
	}

	public void setMarkupAttributeMultivalued(String attributeName, boolean attributeValue) {
		setMarkupAttributeMultivalued(attributeName, attributeValue, ";");
	}

	public void setMarkupAttributeMultivalued(String attributeName, boolean attributeValue, String seperator) {
		setMarkupAttributeMultivalued(attributeName, String.valueOf(attributeValue), seperator);
	}

	public void setMarkupAttributeWithoutValue(String attributeName) {
		setMarkupAttribute(attributeName, slash);
	}

	public void setStyleClass(String styleName) {
		setMarkupAttributeMultivalued("class", styleName, " ");
	}

	public String getStyleClass(){
		return getMarkupAttribute("class");
	}
	/**
	 * Sets or adds to the style tag of this object <br><br>Preserves
	 * previous set values with this method
	 */
	public void setStyleAttribute(String style)
	{
		if (this._styler == null) {
			this._styler = new TextStyler();
		}
		this._styler.parseStyleString(style);
		setMarkupAttribute("style", this._styler.getStyleString());
	}
	public void setStyleAttribute(String attribute, String value) {
		if (this._styler == null) {
			this._styler = new TextStyler();
		}
		this._styler.setStyleValue(attribute, value);
		setMarkupAttribute("style", this._styler.getStyleString());
	}
	public String getStyleAttribute()
	{
		if (isMarkupAttributeSet("style")) {
			return this.getMarkupAttribute("style");
		}
		return "";
	}
	public void removeStyleAttribute(String styleAttribute){
		if(this._styler==null) {
			this._styler = new TextStyler();
		}
		this._styler.parseStyleString(getStyleAttribute());
		this._styler.removeStyleValue(styleAttribute);
		setMarkupAttribute("style",this._styler.getStyleString());

	}

	public void setTitle(String title) {
		setMarkupAttribute("title", title);
	}

	public String getTitle() {
		if (isMarkupAttributeSet("title")) {
			return this.getMarkupAttribute("title");
		}
		return "";
	}

	/* @deprecated
	 * Use setTitle(String) instead
	 */
	@Deprecated
	public void setToolTip(String toolTip) {
		setTitle(toolTip);
	}

	/* @deprecated
	 * Use setTitle(String) instead
	 */
	@Deprecated
	public String getToolTip() {
		return getTitle();
	}

	/**
	 * Copies all of the attribute mappings from the specified map to
	 * attributes. These mappings will replace attibutes that this map had for
	 * any of the keys currently in the specified map.
	 */
	public void addMarkupAttributes(Map<String, String> attributeMap)
	{
		getMarkupAttributes().putAll(attributeMap);
	}
	/**
	 *
	 */
	protected static Map<String, String> getAttributeMap(String attributeString)
	{
		Map<String, String> map = new Hashtable<String, String>();
		if (attributeString != null && attributeString.length() > 1)
		{
			StringTokenizer tokens = new StringTokenizer(attributeString), tok;
			while (tokens.hasMoreTokens())
			{
				String s = tokens.nextToken();
				tok = new StringTokenizer(s, "=\"");
				if (tok.countTokens() == 2)
				{
					map.put(tok.nextToken(), tok.nextToken());
				}
			}
		}
		return map;
	}
	public String getMarkupAttribute(String attributeName)
	{
		return getMarkupAttributes().get(attributeName);
	}
	protected static String getAttribute(String attributeName, Map<String, String> map)
	{
		if (map != null)
		{
			return map.get(attributeName);
		}
		else
		{
			return null;
		}
	}
	public boolean isMarkupAttributeSet(String attributeName)
	{
		if (getMarkupAttribute(attributeName) == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	public Map<String, String> getMarkupAttributes()
	{
		if(this.attributes==null){
			this.attributes = new Hashtable<String, String>();
		}
		return this.attributes;
	}
	protected static String getAttributesString(Map<String, String> map)
	{
		StringBuffer returnString = new StringBuffer();
		String attributeKey = "";
		String attributeValue = "";
		Map.Entry<String, String> mapEntry;
		if (map != null)
		{
			for (Iterator<Map.Entry<String, String>> i = map.entrySet().iterator(); i.hasNext();)
			{
				mapEntry = i.next();
				attributeKey = mapEntry.getKey();
				returnString.append(CoreConstants.SPACE);
				returnString.append(attributeKey);
				attributeValue = mapEntry.getValue();
				if (!attributeValue.equals(slash))
				{
					returnString.append("=\"");
					returnString.append(attributeValue);
					returnString.append("\"");
				}
				returnString.append(CoreConstants.EMPTY);
			}
		}
		return returnString.toString();
	}
	public String getMarkupAttributesString()
	{
		/*
		 * StringBuffer returnString = new StringBuffer(); String Attribute
		 * ="";
		 *
		 * if (this.attributes != null) { Enumeration e = attributes.keys();
		 * while (e.hasMoreElements()) { Attribute = (String)e.nextElement();
		 * returnString.append(" "); returnString.append(Attribute); String
		 * attributeValue=getAttribute(Attribute);
		 * if(!attributeValue.equals(slash)){ returnString.append("=\"");
		 * returnString.append(attributeValue); returnString.append("\""); }
		 * returnString.append(""); } }
		 *
		 * return returnString.toString();
		 */
		//return getAttributesString(this.attributes);
		return getAttributesString(getMarkupAttributes());
	}

	/**
	 * Gets the name of this object
	 *
	 * @uml.property name="name"
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of this object
	 *
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Flushes the buffer in the printwriter out
	 */
	public void flush()
	{
		this.getPrintWriter().flush();
	}
	/**
	 * Uses the default PrintWriter object to print out a string
	 */
	public void print(String string)
	{
		if(IWMainApplication.useJSF){
			try {
				if(string!=null){
					// Here we call IWContext.getInstance rather than FacesContext.getCurrentInstance()
					// because we want to have the overridden method in IWContext for IWCacheManager Cacheing
					IWContext.getInstance().getResponseWriter().write(string);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			getPrintWriter().print(string);
		}
	}
	/**
	 * Uses the default PrintWriter object to print out a string with the
	 * endline character
	 */
	public void println(String string)
	{
		if(IWMainApplication.useJSF){
			try {
				// Here we call IWContext.getInstance rather than FacesContext.getCurrentInstance()
				// because we want to have the overridden method in IWContext for IWCacheManager Cacheing
				ResponseWriter writer = IWContext.getInstance().getResponseWriter();
				if(string!=null){
					writer.write(string);
				}
				writer.write(StringHandler.NEWLINE);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			getPrintWriter().println(string);
		}
	}
	public void renderComponent(IWContext iwc) throws Exception
	{
		//this.out = iwc.getWriter();
		//_print(iwc);
	    renderChild(iwc,this);
	}
	public void _print(IWContext iwc) throws Exception
	{
		if(!this.goneThroughRenderPhase()){
			initVariables(iwc);
			print(iwc);
			cleanVariables(iwc);
		}
	}
	/**
	 * The default implementation for the print function
	 *
	 * This function is invoked on each request by the user for each
	 * PresentationObject instance (after main(iwc)).
	 *
	 * Override this function where it is needed to print out the specified
	 * content. This function should only be overrided in idegaWeb Elements.
	 */
	public void print(IWContext iwc) throws Exception
	{
		if (iwc.getMarkupLanguage().equals("WML"))
		{
			iwc.setContentType("text/vnd.wap.wml");
		}
	}
	/**
	 * This method is only used for old idegaWeb style rendering
	 * @return The esponse object for the page
	 */
	protected HttpServletRequest getRequest()
	{
		/*if(this._request!=null){
			return this._request;
		}
		else{*/
			return IWContext.getInstance().getRequest();
		/*}*/
	}
	/**
	 * This method is only used for old idegaWeb style rendering
	 * @return The esponse object for the page
	 */
	protected HttpServletResponse getResponse()
	{
		/*if(this._response!=null){
			return this._response;
		}
		else{*/
			return IWContext.getInstance().getResponse();
		/*}*/
	}
	/**
	 * @deprecated Replaced with getMarkupLanguage().
	 * Only preserved for backwards compatability.
	 */
	@Deprecated
	public String getLanguage() {
	    return getMarkupLanguage();
	}

	/**
	 * @return The "layout" language used and supplied by the IWContext
	 *
	 */
	public String getMarkupLanguage() {
		if (this.markupLanguage != null) {
			return this.markupLanguage;
		}
		return IWConstants.MARKUP_LANGUAGE_HTML;
	}

	public String setID(String ID){
		setId(ID);
		return ID;
	}

	/**
	 * @see UIComponentBase#setId(java.lang.String)
	 */
	@Override
	public void setId(String id){
		if (Character.isDigit(id.charAt(0))) {
			id = "id" + id;
		}
		setMarkupAttribute("id", id);
		super.setId(id);
	}

	public void setID(int ID){
		setID(Integer.toString(ID));
	}

	/**
	 * <p>
	 * Called by the Builder to set all id's on the object from the IBXML page
	 * to maintain backwards compatibility.
	 * </p>
	 * @param xmlId the id of the module tag
	 * @param icObjectInstanceId
	 * @param icObjectId
	 */
	public void setBuilderIds(String xmlId, int icObjectInstanceId,int icObjectId){
		setICObjectID(icObjectId);
		setICObjectInstanceID(icObjectInstanceId);
		this.xmlId=xmlId;
		try{
			setId(xmlId);
		}
		catch(Exception e){}
	}

	/**
	 * <p>
	 * Returns the id set in xml (by Builder).<br/>
	 * This Id can be either an ICObjectInstanceId (an integer) if the older
	 * method is used or a string like: 'uuid_a4cd4fd...' if the newer
	 * method is used.
	 * </p>
	 */
	public String getXmlId(){
		return this.xmlId;
	}

	/**
	 * This method is deprecated and only used for old style (pre-JSF) style rendering.
	 * Get the printWriter for the current render response.
	 */
	protected PrintWriter getPrintWriter()
	{
		/*if(this.out!=null){
			return this.out;
		}
		else{*/
			try {
				return IWContext.getInstance().getWriter();
			}
			catch (UnavailableIWContext e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		/*}*/
	}
	/**
	 * @return The Class name of the Object
	 */
	public String getClassName()
	{
		return this.getClass().getName();
	}
	/**
	 * Encodes a string to call special request such as pop-up windows in HTML
	 */
	public String encodeSpecialRequestString(String RequestType, String RequestName, IWContext iwc)
	{
		String theOutput = "";
		theOutput = iwc.getRequestURI();
		theOutput = theOutput + "?idegaspecialrequesttype=" + RequestType + "&idegaspecialrequestname=" + RequestName;
		return theOutput;
	}
	/**
	 * Sets the associated (attached) script object to this object
	 */
	public void setAssociatedScript(Script myScript) {
		if (getRootParent() != null) {
			getRootParent().setAssociatedScript(myScript);
		}
	}
	/**
	 * @return The associated (attached) script or null if there is no Script
	 *         associated
	 */
	public Script getAssociatedScript()
	{
		if (getRootParent() != null)
		{
			return getRootParent().getAssociatedScript();
		}
		else
		{
			return null;
		}
	}
	/**
	 * @return The enclosing Page object
	 */
	public Page getParentPage()
	{
		return PresentationObjectUtil.getParentPage(this);
	}
	public int getParentPageID()
	{
		Page obj = getParentPage();
		if (obj != null)
		{
			return obj.getPageID();
		}
		else
		{
			return -1;
		}
	}
	/**
	 * @return The enclosing Form object
	 */
	public Form getParentForm()
	{
		Form returnForm = null;
		//PresentationObject obj = getParentObject();
		UIComponent obj = getParent();
		while (obj != null)
		{
			if (obj instanceof Form)
			{
				returnForm = (Form) obj;
			}
			//obj = obj.getParentObject();
			obj = obj.getParent();
		}
		return returnForm;
	}
	/**
	 * returns the objectinstance this object is part of
	 */
	// getContainingObjectInstance
	public PresentationObject getParentObjectInstance()
	{
		PresentationObject obj = this;
		while (obj != null)
		{
			if (obj.getICObjectInstanceID() > 0)
			{
				return obj;
			}
			if (obj instanceof IFrameContent)
			{
				obj = ((IFrameContent) obj).getOwnerInstance();
			}
			else
			{
				try{
					obj = obj.getParentObject();
				}
				catch(ClassCastException cce){
					obj=null;
				}
			}
		}
		return null;
	}
	// getContainingObjectInstanceID
	public int getParentObjectInstanceID()
	{
		PresentationObject obj = getParentObjectInstance();
		if (obj != null)
		{
			return obj.getICObjectInstanceID();
		}
		else
		{
			return 0;
		}
	}
	/**
	 * Override this function for needed funcionality.
	 *
	 * This funcion is invoked on each request by the user (before print(iwc) )
	 * on a PresentationObject Instance.
	 */
	public void main(IWContext iwc) throws Exception
	{
	}


	protected void prepareClone(PresentationObject newObjToCreate)
	{
	}



	/**
	 * This clone method checks for permission for "this" instance if askForPermission is true
	 * This method should generally not be overridden unless there is need to alter the default permission behaviour.
	 * @param iwc
	 * @param askForPermission
	 * @return
	 */
	public Object clonePermissionChecked(IWUserContext iwc, boolean askForPermission)	{
		Object object = null;
		if (askForPermission || iwc != null) {
			if (iwc.getApplicationContext().getIWMainApplication().getAccessController().hasViewPermission(this, iwc)) {
				object = this.clone();
			} else {
				return NULL_CLONE_OBJECT;
			}
		} else {
			object = this.clone();
		}
		return object;
	}
	/**
	 * This method calls by default clonePermissionChecked(iwc,askForPermission) with askForPermission=true
	 * This method should generally not be overridden unless there is need to alter the default permission behaviour.
	 * @param iwc
	 * @return
	 */
	public Object clonePermissionChecked(IWUserContext iwc)
	{
		return this.clonePermissionChecked(iwc, true);
	}
	/**
	 * The default clone implementation
	 */
	@Override
	public Object clone()
	{
		PresentationObject obj = null;
		/*
		 * System.err.println("--"); System.err.println("Cloning class of type: "+
		 * this.getClassName()); System.err.println("--");
		 */
		try
		{
			//This is forbidden in clone i.e. "new":
			//obj =
			// (PresentationObject)Class.forName(this.getClassName()).newInstance();
			obj = (PresentationObject) super.clone();
			Map<String, String> markupAttributes = getMarkupAttributes();
			if (markupAttributes != null)
			{
				if(markupAttributes instanceof Hashtable){
					obj.attributes = ((Hashtable) ((Hashtable) markupAttributes).clone());
				}
				else{
					for (Iterator<String> iter = markupAttributes.keySet().iterator(); iter.hasNext();) {
						String key = iter.next();
						String value = markupAttributes.get(key);
						obj.getAttributes().put(key,value);
					}
				}
			}


			//TODO: Resolve this:
			//Copying the attributes probably doesn't work like this:
			obj.getAttributes().putAll(this.getAttributes());

			obj.setName(this.getName());
			if(this.getParent()!=null){
				obj.setParent(this.getParent());
			}
			//obj.setParentObject(this.parentObject);
			this.prepareClone(obj);
			obj.initializedInMain = this.initializedInMain;
			obj.ic_object_instance_id = this.ic_object_instance_id;
			obj.ic_object_id = this.ic_object_id;
			obj._location = this._location;
			obj._objTemplateID=this._objTemplateID;
			obj._templateObject = null;
			obj.xmlId=this.xmlId;
			obj.resetGoneThroughMainInRestore=this.resetGoneThroughMainInRestore;
			obj.supportsMultipleMainCalls=this.supportsMultipleMainCalls;
			//obj.defaultState = this.defaultState; //same object, unnecessary
			// to clone
			if (obj.getID().startsWith("iwid")) {
				obj.setID();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace(System.err);
		}
		return obj;
	}
	/*
	 * protected void initICObjectInstanceId(IWContext iwc){ String sID =
	 * iwc.getParameter(_PARAMETER_IC_OBJECT_INSTANCE_ID); try { if(sID !=
	 * null){ System.err.println("sID: "+sID);
	 * this.setICObjectInstanceID(Integer.parseInt(sID));
	 * //this.ic_object_instance_id = Integer.parseInt(sID);
	 * System.err.println("Integer.parseInt(sID): "+Integer.parseInt(sID));
	 * System.err.println("getICObjectInstanceID:
	 * "+this.getICObjectInstanceID()); }else{ System.err.println("sID ==
	 * null"); } } catch (NumberFormatException ex) {
	 * System.err.println(this+": cannot init ic_object_instance_id"); }
	 *  }
	 */
	/**
	 * Function invoked before the print function
	 */
	public void _main(IWContext iwc) throws Exception
	{

		initVariables(iwc);

		if (!this.initializedInMain)
		{
			this.initInMain(iwc);
		}
		//if (!goneThroughMain)
		if(mayGoThroughMain())
		{
			main(iwc);
		}
		//goneThroughMain = true;
		setGoneThroughMain();
	}

	/**
	 *
	 * @uml.property name="errorMessage"
	 */
	protected void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 *
	 * @uml.property name="errorMessage"
	 */
	protected String getErrorMessage() {
		return this.errorMessage;
	}

	public void setAsPrinted(boolean printed)
	{
		this.doPrint = printed;
	}
	/*
	 * public void setTreeID(String treeID) { this.treeID = treeID; }
	 *
	 * public String getTreeID() { return treeID;
	 */
	public String getTemplateId() {
		return (this._objTemplateID);
	}
	public PresentationObject getTemplateObject() {
		if(this._templateObject == null) {
			this._templateObject = (PresentationObject)this.clone();
			this._templateObject._objTemplateID = null;
			try {
				this._templateObject.ic_object_instance_id = Integer.parseInt(this._objTemplateID);
			} catch (NumberFormatException e) {
				this._templateObject.ic_object_instance_id=-1;
				//e.printStackTrace();
			}
		}
		return this._templateObject;
	}
	public void setTemplateId(int id) {
		if(id!=-1) {
			setTemplateId(String.valueOf(id));
		} else {
			setTemplateId(null);
		}
	}
	public void setTemplateId(String id) {
		this._objTemplateID=id;
	}

	public void changeInstanceIdForInheritedObject(int id) {
		setTemplateId(getICObjectInstanceID());
		setICObjectInstanceID(id);
	}

	public void setICObjectInstanceID(int id)
	{
		this.ic_object_instance_id = id;
		this.getLocation().setICObjectInstanceID(id);
	}
	public void setICObjectInstance(ICObjectInstance instance)
	{
		int instanceId = ((Integer)(instance.getPrimaryKey())).intValue();
		setICObjectInstanceID(instanceId);
	}
	public void setICObjectID(int id)
	{
		this.ic_object_id = id;
	}
	public void setICObject(ICObject obj)
	{
		int objectId = ((Integer)(obj.getPrimaryKey())).intValue();
		this.ic_object_id = objectId;
	}
	/**
	 * owerwrite in module
	 */
	public int getICObjectInstanceID(IWContext iwc) throws Exception
	{
		return getICObjectInstanceID();
	}
	public int getICObjectInstanceID()
	{
		return this.ic_object_instance_id;
	}
	public ICObjectInstance getICObjectInstance(IWContext iwc) throws Exception
	{
		return getICObjectInstance();
	}
	public ICObjectInstance getICObjectInstance() throws Exception
	{
		if (getICObjectInstanceID() > 0)
		{
			return (
				(com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(
					ICObjectInstance.class)).findByPrimaryKeyLegacy(
				getICObjectInstanceID());
		}
		else
		{
			return null;
		}
	}
	public int getICObjectID(IWContext iwc) throws Exception
	{
		return this.ic_object_id;
		/*
		 * ICObjectInstance inst = this.getICObjectInstance();
		 *
		 * System.out.println("Getting ICObjectInstance"); if(inst != null){
		 *
		 * ICObject ob = inst.getObject(); if(ob != null){ return ob.getID(); } }
		 */
	}
	public int getICObjectID()
	{
		return this.ic_object_id;
	}
	public ICObject getICObject() throws Exception {
		return this.getICObject(this.getClass());
	}
	protected ICObject getICObject(Class<?> c) throws Exception {
		try {
			ICObjectHome icohome = (ICObjectHome) com.idega.data.IDOLookup.getHome(ICObject.class);
			ICObject ico = icohome.findByClassName(c.getName());
			return ico;
		}
		catch (Exception e) {
			log(Level.SEVERE, "[PresentationObject] " + c.getName() + " not found in ic_object");
			return null;
		}
	}
	public ICObjectInstance getICInstance(IWContext iwc) throws IWException
	{
		try
		{
			ICObjectInstanceHome icoihome = (ICObjectInstanceHome) com.idega.data.IDOLookup.getHome(ICObjectInstance.class);
			return icoihome.findByPrimaryKey(getICObjectInstanceID(iwc));
		}
		catch (Exception excep)
		{
			IWException iwexcep =
				new IWException(
					"Exception in PresentationObject.getICInstance(): " + excep.getClass().getName() + " : " + excep.getMessage());
			throw iwexcep;
			//throw (IWException) excep.fillInStackTrace();
		}
	}
	/**
	 * @deprecated Do not use this function
	 */
	@Deprecated
	public void addIWLinkListener(IWLinkListener l, IWContext iwc)
	{
		//System.err.println(this.getClass().getName() + " : listener added of
		// type -> " + l.getClass().getName());
		/**
		 * temp
		 */
		getEventListenerList().remove(IWLinkListener.class, l);
		getEventListenerList().add(IWLinkListener.class, l);
	}
	/**
	 * @deprecated Do not use this function
	 */
	@Deprecated
	public IWLinkListener[] getIWLinkListeners()
	{
		return getEventListenerList().getListeners(IWLinkListener.class);
	}
	/**
	 * @deprecated Do not use this function
	 */
	@Deprecated
	public void addIWSubmitListener(IWSubmitListener l, IWContext iwc)
	{
		getEventListenerList().add(IWSubmitListener.class, l);
	}
	/**
	 * @deprecated Do not use this function
	 */
	@Deprecated
	public IWSubmitListener[] getIWSubmitListeners()
	{
		if (this.listenerList == null)
		{
			this.listenerList = new EventListenerList();
		}
		return this.listenerList.getListeners(IWSubmitListener.class);
	}
	public void setEventAttribute(String attributeName, Object attributeValue)
	{
		if (this.eventAttributes == null)
		{
			this.eventAttributes = new Hashtable<String, Object>();
		}
		this.eventAttributes.put(attributeName, attributeValue);
	}
	public Object getEventAttribute(String attributeName)
	{
		if (this.eventAttributes != null)
		{
			return this.eventAttributes.get(attributeName);
		}
		else
		{
			return null;
		}
	}
	public void dispatchEvent(IWEvent e)
	{
		processEvent(e);
	}
	protected void processEvent(IWEvent e)
	{
		if (e instanceof IWLinkEvent)
		{
			processIWLinkEvent((IWLinkEvent) e);
		}
		else if (e instanceof IWSubmitEvent)
		{
			processIWSubmitEvent((IWSubmitEvent) e);
		}
		else
		{
			System.err.println("unable to prosess event: " + e);
		}
	}
	protected void processIWLinkEvent(IWLinkEvent e)
	{
		PresentationObject obj = (PresentationObject) e.getSource();
		// Guaranteed to return a non-null array
		IWLinkListener[] listeners = obj.getIWLinkListeners();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 1; i >= 0; i--)
		{
			listeners[i].actionPerformed(e);
		}
	}
	protected void processIWSubmitEvent(IWSubmitEvent e)
	{
		PresentationObject obj = (PresentationObject) e.getSource();
		// Guaranteed to return a non-null array
		IWSubmitListener[] listeners = obj.getIWSubmitListeners();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 1; i >= 0; i--)
		{
			listeners[i].actionPerformed(e);
		}
	}
	/**
	 * unimplemented
	 */
	public void fireEvent()
	{
	}
	public void endEvent(IWContext iwc)
	{
		iwc.removeSessionAttribute(this.eventLocationString);
	}
	public void listenerAdded(boolean added)
	{
		this.listenerAdded = added;
	}
	public boolean listenerAdded()
	{
		return this.listenerAdded;
	}
	//public void setIWContext(IWContext iwc)
	//{
	//	//  System.err.println(this.getClass().getName() + ": iwc set");
	//	eventIWContext = iwc;
	//}
	/**
	 * @deprecated Do not use this function
	 */
	@Deprecated
	public EventListenerList getEventListenerList()
	{
		if (this.listenerList == null)
		{
			this.listenerList = new EventListenerList();
		}
		return this.listenerList;
	}

	/**
	 * @deprecated Do not use this function, it is not safe
	 */
	@Deprecated
	public IWContext getEventIWContext() {
		//return eventIWContext;
		return IWContext.getInstance();
	}

	/**
	 * Needs to be overrided to get the right IWBundle identifier for the
	 * object
	 */
	public String getBundleIdentifier()
	{
		return IW_BUNDLE_IDENTIFIER;
	}
	public IWBundle getBundle(IWUserContext iwuc)
	{
		IWMainApplication iwma = iwuc == null ? IWMainApplication.getDefaultIWMainApplication() : iwuc.getApplicationContext().getIWMainApplication();
		return iwma.getBundle(getBundleIdentifier());
	}
	public IWResourceBundle getResourceBundle(IWUserContext iwuc)
	{
		IWBundle bundle = getBundle(iwuc);
		if (bundle != null)
		{
			return bundle.getResourceBundle(iwuc.getCurrentLocale());
		}
		return null;
	}
	public IWPropertyList getUserProperties(IWUserContext iwuc)
	{
		IWBundle bundle = getBundle(iwuc);
		if (bundle != null)
		{
			return bundle.getUserProperties(iwuc);
		}
		return null;
	}
	public String getLocalizedString(String key, IWUserContext iwuc)
	{
		//TODO use such method everywhere for getting localised messages
//		IWContext iwc = (IWContext)iwuc;
//		return iwc.getIWMainApplication().getLocalisedStringMessage(key, null, getBundleIdentifier(), iwc.getCurrentLocale());

		IWResourceBundle bundle = getResourceBundle(iwuc);
		if (bundle != null)
		{
			return bundle.getLocalizedString(key, null);
		}
		return null;
	}
	public String getLocalizedString(String key, String defaultValue, IWUserContext iwuc)
	{
//		IWContext iwc = (IWContext)iwuc;
//		return ((IWContext)iwuc).getIWMainApplication().getLocalisedStringMessage(key, defaultValue, getBundleIdentifier(), iwc.getCurrentLocale());

		IWResourceBundle bundle = getResourceBundle(iwuc);
		if (bundle != null)
		{
			return bundle.getLocalizedString(key, defaultValue);
		}
		return null;
	}
	public void setUseBuilderObjectControl(boolean use)
	{
		this._useBuilderObjectControl = use;
	}
	public boolean getUseBuilderObjectControl()
	{
		return (this._useBuilderObjectControl);
	}
	public void setBelongsToParent(boolean belongs)
	{
		this._belongsToParent = belongs;
	}
	public boolean getBelongsToParent()
	{
		return (this._belongsToParent);
	}
	public boolean getChangeInstanceIDOnInheritance()
	{
		return (this._changeInstanceIDOnInheritance);
	}
	public void setChangeInstanceIDOnInheritance(boolean change)
	{
		this._changeInstanceIDOnInheritance = change;
	}
	public boolean allowPagePermissionInheritance()
	{
		return this._allowPagePermissionInheritance;
	}
	/*
	 * New Event system
	 */
	public String changeState(PresentationObject source, IWContext iwc)
	{
		System.err.println(this +" state not changed, method not implemented");
		System.err.println("source = " + source + " : " + source.getParentPageID() + "_" + source.getParentObjectInstanceID());
		return null;
	}
	public GenericState getStateInstance(IWContext iwc)
	{
		return new GenericState(this, iwc);
	}
	public GenericState getState(IWContext iwc)
	{
		GenericState state = null;
		String stateString = null;
		if (this instanceof IFrameContent)
		{
			stateString = iwc.getCurrentState(((IFrameContent) this).getOwnerInstance().getICObjectInstanceID());
			//System.err.println("stateString =
			// iwc.getCurrentState("+((IFrameContent)this).getOwnerInstance().getICObjectInstanceID()+");");
			//System.err.println(this.getClassName()+" -
			// stateString:"+((stateString==null)?"objectNull":stateString)+"
			// for instance
			// "+((IFrameContent)this).getOwnerInstance().getICObjectInstanceID());
			//System.err.println("IWContext.hashCode(): "+iwc.hashCode());
		}
		else
		{
			stateString = iwc.getCurrentState(this.getICObjectInstanceID());
			//System.err.println(this.getClassName()+" -
			// stateString:"+((stateString==null)?"objectNull":stateString)+"
			// for instance "+this.getICObjectInstanceID());
			//System.err.println("IWContext.hashCode(): "+iwc.hashCode());
		}
		if (stateString != null)
		{
			state = getStateInstance(iwc);
			if (state != null)
			{
				state.updateState(stateString);
			}
		}
		else
		{
			state = getDefaultState();
		}
		return state;
	}
	public void setDefaultState(GenericState state)
	{
		this.defaultState = state;
	}
	public GenericState getDefaultState()
	{
		if(this.defaultState!=null){
			return (GenericState) this.defaultState.clone();
		}
		return null;
	}
	public boolean equals(PresentationObject obj)
	{
		if (this.getICObjectInstanceID() == obj.getICObjectInstanceID() && this.getICObjectInstanceID() > 0)
		{
			return true;
		}
		return super.equals(obj);
	}
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof PresentationObject)
		{
			return this.equals((PresentationObject) obj);
		}
		else
		{
			return super.equals(obj);
		}
	}
	/**
	 * Parameter debugger
	 */
	public void debugParameters(IWContext iwc) {
		System.err.println("DEBUG: Parameter debugging : " + this.getClassName());
		System.err.println("AT  :" + new java.util.Date(System.currentTimeMillis()).toString());
		Enumeration<String> enumer = iwc.getParameterNames();
		String prm;
		while (enumer.hasMoreElements()) {
			prm = enumer.nextElement();
			String[] values = iwc.getParameterValues(prm);
			for (int i = 0; i < values.length; i++)
			{
				//debug("Name: "+prm+(values.length>1?" # "+(i+1):"")+"\t
				// Value: "+values[i]);
				System.err.println("Name: " + prm + (values.length > 1 ? " # " + (i + 1) : "") + "\t Value: " + values[i]);
			}
			//System.err.println("Name: "+prm+"\t Value:
			// "+iwc.getParameter(prm));
		}
		System.err.println();
	}

	public String getBuilderName(IWUserContext iwuc)
	{
		return this.getBundle(iwuc).getComponentName(this.getClass(), iwuc == null ? Locale.ENGLISH : iwuc.getCurrentLocale());
	}

	public void setWidth(String width)
	{
		setMarkupAttribute(WIDTH, width);
	}
	public void setVisible(boolean visible)
	{
		if (visible) {
			setStyleAttribute("visibility:visible");
		}
		else {
			setStyleAttribute("visibility:hidden");
		}
	}
	public void setHeight(String height)
	{
		setMarkupAttribute(HEIGHT, height);
	}
	public String getHeight()
	{
		String height = getMarkupAttribute(HEIGHT);
		if (height == null) {
			if (this._styler != null) {
				height = this._styler.getStyleValue(HEIGHT);
				if (height != null && height.indexOf("px") != -1) {
					height = height.substring(0, height.indexOf("px"));
				}
			}
		}
		return height;
	}
	public String getWidth()
	{
		String width = getMarkupAttribute(WIDTH);
		if (width == null) {
			if (this._styler != null) {
				width = this._styler.getStyleValue(WIDTH);
				if (width != null && width.indexOf("px") != -1) {
					width = width.substring(0, width.indexOf("px"));
				}
			}
		}
		return width;
	}
	public boolean isWidthSet() {
		return getWidth() != null;
	}
	public boolean isHeightSet() {
		return getHeight() != null;
	}
	public void setHorizontalAlignment(String align)
	{
		setMarkupAttribute(HORIZONTAL_ALIGNMENT, align);
	}
	public String getHorizontalAlignment()
	{
		return getMarkupAttribute(HORIZONTAL_ALIGNMENT);
	}
	/**
	 * Returns the IWApplicationContext that this object is running in.
	 *
	 * @throws RuntimeException
	 *             if the context is not set
	 */
	protected IWApplicationContext getIWApplicationContext()
	{
		//if (_iwac == null)
		//{
		//	setIWApplicationContext(IWContext.getInstance().getIWMainApplication().getIWApplicationContext());
		//}
		//return _iwac;
		return IWContext.getInstance().getIWMainApplication().getIWApplicationContext();
	}
	//protected void setIWApplicationContext(IWApplicationContext iwac)
	//{
	//	_iwac = iwac;
	//}
	/**
	 * Returns the IWUserContext that this object is running in.
	 *
	 * @throws RuntimeException
	 *             if the context is not set
	 */
	protected IWUserContext getIWUserContext()
	{
		//if (_iwuc == null)
		//{
		//	setIWUserContext(IWContext.getInstance());
		//}
		//return _iwuc;
		return IWContext.getInstance();
	}
	//protected void setIWUserContext(IWUserContext iwuc)
	//{
	//	_iwuc = iwuc;
	//}
	public void setLocation(IWLocation location)
	{
		this.setLocation(location, this.getIWUserContext());
	}
	public void setLocation(IWLocation location, IWUserContext iwuc)
	{
		this._location = location;
		if (this instanceof StatefullPresentation)
		{
			IWPresentationState state = ((StatefullPresentation) this).getPresentationState(iwuc);
			if (state != null)
			{
				state.setLocation(location);
			}
		}
	}
	public IWLocation getLocation()
	{
		//    if(this instanceof StatefullPresentation){
		//      return
		// ((StatefullPresentation)this).getPresentationState().getLocation();
		//    } else {
		return this._location;
		//    }
	}
	public EventListenerList getEventListenerList(IWUserContext iwuc)
	{
		if (this._listenerList != null)
		{
			return this._listenerList;
		}
		else
		{
			try
			{
				//<<<<<<< PresentationObject.java
				IWEventMachine machine = (IWEventMachine) IBOLookup.getSessionInstance(iwuc, IWEventMachine.class);
				//        System.out.println();
				//=======
				//	IWEventMachine machine =
				// (IWEventMachine)IBOLookup.getSessionInstance(iwuc,IWEventMachine.class);
				//	System.out.println();
				//>>>>>>> 1.52
				//        System.out.println("getEventListenerList: machine = "+
				// machine);
				//        System.out.println("getEventListenerList: location =
				// "+this.getLocation());
				//<<<<<<< PresentationObject.java
				//        System.out.println();
				if (this.getICObjectInstanceID() <= 0)
				{
					if (this.getLocation() != null)
					{
						this._listenerList = machine.getListenersFor(this.getLocation());
						return this._listenerList;
					}
					else
					{
						throw new RuntimeException(
							"ERROR: "
								+ this
								+ ".getEventListenerList(IWUserContext iwuc): Object has neither instanceId nor IWLocationObject");
					}
				}
				else
				{
					this._listenerList = machine.getListenersFor(this.getICObjectInstance());
					return this._listenerList;
				}
				//=======
				//	System.out.println();
				//	if(this.getICObjectInstanceID() <= 0){
				//	  if(this.getLocation() != null){
				//	    _listenerList = machine.getListenersFor(this.getLocation());
				//	    return _listenerList;
				//	  } else {
				//	    throw new RuntimeException("ERROR:
				// "+this+".getEventListenerList(IWUserContext iwuc): Object
				// has neither instanceId nor IWLocationObject");
				//	  }
				//	} else {
				//	  _listenerList =
				// machine.getListenersFor(this.getICObjectInstance());
				//	  return _listenerList;
				//	}
				//>>>>>>> 1.52
			}
			catch (RemoteException ex)
			{
				throw new RuntimeException(ex.getMessage());
			}
			catch (Exception e2)
			{
				throw new RuntimeException(e2.getMessage());
			}
		}
	}
	/**
	 * This method is similar to the method getEventListenerList but uses the
	 * compoundId as key instead of the location object to fetch the listener
	 * list
	 *
	 * @param iwuc
	 * @return EventListenerList
	 */
	public EventListenerList getEventListener(IWUserContext iwuc)
	{
		if (this._listenerList != null)
		{
			return this._listenerList;
		}
		else
		{
			try
			{
				IWEventMachine machine = (IWEventMachine) IBOLookup.getSessionInstance(iwuc, IWEventMachine.class);
				if (IWMainApplication.useNewURLScheme) {
					// register the machine as ApplicationEventListener using the helper bridge
					iwuc.getApplicationContext().getIWMainApplication().addApplicationEventListener(OldEventSystemHelperBridge.class);
				}
				return machine.getListenersForCompoundId(getCompoundId());
			}
			catch (RemoteException ex)
			{
				throw new RuntimeException(ex.getMessage());
			}
		}
	}
	public void addIWActionListener(IWActionListener l)
	{
		//    getEventListenerList(this.getIWUserContext()).remove(IWActionListener.class,l);
		Object[] list = getEventListenerList(this.getIWUserContext()).getListenerList();
		boolean hasBeenAdded = false;
		// Is l on the list?
		for (int i = list.length - 2; i >= 0; i -= 2)
		{
			if ((list[i] == IWActionListener.class) && (list[i + 1].equals(l) == true))
			{
				hasBeenAdded = true;
				break;
			}
		}
		if (!hasBeenAdded)
		{
			getEventListenerList(this.getIWUserContext()).add(IWActionListener.class, l);
		}
		//    System.out.println();
		//    System.out.println("addIWActionListener: _listenerList =
		// "+getEventListenerList(this.getIWUserContext()));
		//    System.out.println("addIWActionListener: IWActionListener = " + l);
		//    System.out.println("addIWActionListener: location =
		// "+this.getLocation());
		//    System.out.println();
	}
	/**
	 * This method is similar to the method addIWActionListener but uses the
	 * method getEventListener() instead of getEventListenerList()
	 *
	 * @param l
	 */
	public void addActionListener(IWActionListener l)
	{
		Object[] list = getEventListener(this.getIWUserContext()).getListenerList();
		boolean hasBeenAdded = false;
		// Is l on the list?
		for (int i = list.length - 2; i >= 0; i -= 2)
		{
			if ((list[i] == IWActionListener.class) && (list[i + 1].equals(l) == true))
			{
				hasBeenAdded = true;
				break;
			}
		}
		if (!hasBeenAdded)
		{
			getEventListener(this.getIWUserContext()).add(IWActionListener.class, l);
		}
	}
	public void removeIWActionListener(IWActionListener l)
	{
		getEventListenerList(this.getIWUserContext()).remove(IWActionListener.class, l);
	}
	/**
	 * This method is similar to the method addIWActionListener but uses the
	 * method getListener() instead of getEventListenerList()
	 *
	 */
	public void removeActionListener(IWActionListener l)
	{
		getEventListener(this.getIWUserContext()).remove(IWActionListener.class, l);
	}
	public void debugEventListanerList(IWContext iwc)
	{
		System.out.println("--[DEBUG: " + this +"]: listenerList values:");
		EventListenerList list = this.getEventListenerList(iwc);
		Object[] listeners = list.getListenerList();
		if (listeners != null)
		{
			for (int i = 0; i < listeners.length; i++)
			{
				System.out.println(listeners[i]);
			}
		}
		System.out.println("--[DEBUG: " + this +"]: listenerList: DONE");
	}
	/**
	 * Sets the width in pixels or percents Sets the width inside a style
	 * attribute
	 */
	protected void setWidthStyle(String width)
	{
		if (width != null && !width.equals("")) {
	    	if (width.charAt(width.length()-1) == '%')  {
		        this.setStyleAttribute(WIDTH + ":" + width);
		    }
		    else if (width.lastIndexOf("px")>-1) {
	    	    	this.setStyleAttribute(WIDTH + ":" + width);
	    	}
		    else {
				try
				{
					this.setStyleAttribute(WIDTH + ":" + Integer.parseInt(width) + "px");
				}
				catch (NumberFormatException e)
				{
					this.setStyleAttribute(WIDTH + ":" + width);
				}
		    }
		}
	}
	/**
	 * Sets the height in pixels or percents Sets the height inside a style
	 * attribute
	 */
	protected void setHeightStyle(String height)
	{
		if (height != null && !height.equals("")) {
	    	if (height.charAt(height.length()-1) == '%')  {
		        this.setStyleAttribute(HEIGHT + ":" + height);
		    }
	    	else if (height.lastIndexOf("px")>-1) {
	    	    	this.setStyleAttribute(HEIGHT + ":" + height);
	    	}
		    else {
			    try
				{
					this.setStyleAttribute(HEIGHT + ":" + Integer.parseInt(height) + "px");
				}
				catch (NumberFormatException e)
				{
					this.setStyleAttribute(HEIGHT + ":" + height);
				}
		    }
		}
	}
	/**
	 * @see javax.faces.component.UIComponent#getComponentId()
	 * @return String
	 */
	public String getComponentId()
	{
		String id = IWMainApplication.getEncryptedClassName(this.getClass());
		if (this.artificialCompoundId != null) {
			return id;
		}
		PresentationObject mother=null;
		try{
			mother= getParentObject();
		}
		catch(ClassCastException cse){}
		// keep in mind that a parent object is not necessarily a container.
		// Therefore check if a cast is possible.
		if (mother != null && (PresentationObjectContainer.class).isAssignableFrom(mother.getClass()))
		{
			StringBuffer buffer = new StringBuffer(id);
			List<UIComponent> list = ((PresentationObjectContainer) mother).getChildren();
			int myIndex = list.indexOf(this);
			// add underscore
			buffer.append(PresentationObject.COMPOUNDID_CHILD_NUMBER_DELIMITER);
			buffer.append(myIndex);
			return buffer.toString();
		}
		else
		{
			return id;
		}
	}
	/**
	 * @see javax.faces.component.UIComponent#getComponentId()
	 *
	 * Create a path (e.g. "/1234/123/123") that corresponds to the path of the
	 * parent - child relation using the encrypted class name.
	 *
	 * @return String
	 */
	private String calculateCompoundId()
	{
		StringBuffer buffer = new StringBuffer();
		// if artificial compound id is set use this one
		if (this.artificialCompoundId != null)
		{
			buffer.append(this.artificialCompoundId).append(PresentationObject.COMPOUNDID_COMPONENT_DELIMITER).append(getComponentId());
			return buffer.toString();
		}
		// if there is a object instance id use this one
		int instanceId;
		if ((instanceId = getICObjectInstanceID()) > 0) {
			return Integer.toString(instanceId);
		}
		// first fetch my component id
		buffer.append(PresentationObject.COMPOUNDID_COMPONENT_DELIMITER);
		buffer.append(getComponentId());
		// now add the compound id of my mother at the beginning
		PresentationObject mother = null;
		try{
			mother = getParentObject();
		}
		catch(ClassCastException cse){}
		if (mother != null) {
			buffer.insert(0, mother.calculateCompoundId());
		}
		return buffer.toString();
	}

	/**
	 * something has changed, therefore change ALSO the value of the
	 * presentation state object if formerCompoundId equals null the compoundId
	 * has never been fetched and therefore the presentation state object was
	 * not set yet (if so everything is fine). usually you should first set the
	 * artificialCompoundId (if necessary) and then call the
	 * getPresentationState method. The part of this method that calls the
	 * state mashine is only for old code!
	 *
	 * @param artificialCompoundId
	 * @param iwuc
	 *
	 * @uml.property name="artificialCompoundId"
	 */
	public void setArtificialCompoundId(
		String artificialCompoundId,
		IWUserContext iwuc) {
		this.artificialCompoundId = artificialCompoundId;
		// something has changed, therefore change also the value of the
		// presentation state object
		// if formerCompoundId equals null the compoundId has never been
		// fetched and therefore the
		// presentation state object was not set yet (if so everything is
		// fine).
		// Usually you should first set the artificialCompoundId (if necessary)
		// and then call the getPresentationState() method.
		// The part of this method that calls the state mashine is only for old
		// code!
		if (this.formerCompoundId != null && this instanceof StatefullPresentation) {
			try {
				Class<?> stateClass = ((StatefullPresentation) this)
					.getPresentationStateClass();
				IWStateMachine stateMachine = (IWStateMachine) IBOLookup
					.getSessionInstance(iwuc, IWStateMachine.class);
				IWPresentationState state = stateMachine.getStateFor(
					this.formerCompoundId,
					stateClass);
				// update compoundId
				state.setArtificialCompoundId(artificialCompoundId);
			} catch (RemoteException re) {
				throw new RuntimeException(re.getMessage());
			}
		}
	}

	public String getCompoundId()
	{
		// wrap the private method to do something...
		// at the moment there is nothing to do.
		String calculatedCompoundId = calculateCompoundId();
		// Do not forget: set former compound id to the new one
		this.formerCompoundId = calculatedCompoundId;
		return calculatedCompoundId;
	}
	/**
	 * Convenience method to return the instance of BuilderService
	 *
	 * @param iwc
	 * @return @throws
	 *         RemoteException
	 */
	protected BuilderService getBuilderService(IWApplicationContext iwc) throws RemoteException
	{
		return BuilderServiceFactory.getBuilderService(iwc);
	}
	/**
	 * Convenience method to return the instance of ICFileSystem
	 *
	 * @param iwc
	 * @return @throws
	 *         RemoteException
	 */
	protected ICFileSystem getICFileSystem(IWApplicationContext iwc) throws RemoteException
	{
		return ICFileSystemFactory.getFileSystem(iwc);
	}

	/*
	 * Overrided methods from JSF's UIComponent:
	 */

	public String getComponentType(){
		return "iw.element";
	}

	public void addChild(UIComponent child){
		throw new UnsupportedOperationException("Method add(UIComponent) is not supported yet in PresentationObject");
	}

	public void addChild(int index,UIComponent child){
		throw new UnsupportedOperationException("Method add(int,UIComponent) is not supported yet in PresentationObject");
	}

	@Override
	public UIComponent getParent(){
		//return parentObject;
		return super.getParent();
	}


	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#setParent(javax.faces.component.UIComponent)
	 */
	@Override
	public void setParent(UIComponent arg0) {
		//this.parentObject=arg0;
		super.setParent(arg0);
	}



	/*
	 * BEGIN JSF SPECIFIC IMPLEMENTAION METHODS
	 */
	@Override
	public void processDecodes(FacesContext context){
        // Process all facets and children of this component
        for (Iterator<UIComponent> kids = getFacetsAndChildren(); kids.hasNext();) {
        	kids.next().processDecodes(context);
        }

        // Process this component itself
        try {
            decode(context);
        } catch (RuntimeException e) {
            context.renderResponse();
            throw e;
        }
	}

	@Override
	public void decode(FacesContext fc){
		super.decode(fc);
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(context, values[0]);
		this.attributes=(Map<String, String>)values[1];
		this.name=(String)values[2];
		this.doPrint=((Boolean)values[3]).booleanValue();
		this.errorMessage=(String)values[4];
		this.hasBeenAdded=((Boolean)values[5]).booleanValue();
		this.treeID=(String)values[6];
		this.resetGoneThroughMainInRestore=((Boolean)values[28]).booleanValue();

		if(resetGoneThroughMainInRestore()){
			this.goneThroughMain=false;
		}
		else{
			this.goneThroughMain=((Boolean)values[7]).booleanValue();
		}
		this.ic_object_instance_id=((Integer)values[8]).intValue();
		this.ic_object_id=((Integer)values[9]).intValue();
		this.listenerList=(EventListenerList)values[10];
		this._listenerList=(EventListenerList)values[11];
		this.eventAttributes=(Dictionary<String, Object>)values[12];
		this.UniqueInstanceName=(String)values[13];
		this.listenerAdded=((Boolean)values[14]).booleanValue();
		this.eventLocationString=(String)values[15];
		this.initializedInMain=((Boolean)values[16]).booleanValue();
		this._useBuilderObjectControl=((Boolean)values[17]).booleanValue();
		this._belongsToParent=((Boolean)values[18]).booleanValue();
		this._changeInstanceIDOnInheritance=((Boolean)values[19]).booleanValue();
		this._allowPagePermissionInheritance=((Boolean)values[20]).booleanValue();
		this._location=(IWLocation)values[21];
		this.defaultState=(GenericState)values[22];
		this.artificialCompoundId=(String)values[23];
		this.formerCompoundId=(String)values[24];
		this._styler=(TextStyler)values[25];
		this._objTemplateID=(String)values[26];
		this.xmlId=(String)values[27];
		//28 is handled above because it needs to be handled before 7
		this.supportsMultipleMainCalls=((Boolean)values[29]).booleanValue();
		//This variable is set only to know that the object is recreated from serialized state
		this.isStateRestored=true;
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
	 */
	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[30];
		values[0]=super.saveState(context);
		values[1]=this.attributes;
		values[2]=this.name;
		values[3]=Boolean.valueOf(this.doPrint);
		values[4]=this.errorMessage;
		values[5]=Boolean.valueOf(this.hasBeenAdded);
		values[6]=this.treeID;
		values[7]=Boolean.valueOf(this.goneThroughMain);
		values[8]=new Integer(this.ic_object_instance_id);
		values[9]=new Integer(this.ic_object_id);
		values[10]=this.listenerList;
		values[11]=this._listenerList;
		values[12]=this.eventAttributes;
		values[13]=this.UniqueInstanceName;
		values[14]=Boolean.valueOf(this.listenerAdded);
		values[15]=this.eventLocationString;
		values[16]=Boolean.valueOf(this.initializedInMain);
		values[17]=Boolean.valueOf(this._useBuilderObjectControl);
		values[18]=Boolean.valueOf(this._belongsToParent);
		values[19]=Boolean.valueOf(this._changeInstanceIDOnInheritance);
		values[20]=Boolean.valueOf(this._allowPagePermissionInheritance);
		values[21]=this._location;
		values[22]=this.defaultState;
		values[23]=this.artificialCompoundId;
		values[24]=this.formerCompoundId;
		values[25]=this._styler;
		values[26]=this._objTemplateID;
		values[27]=this.xmlId;
		values[28]=Boolean.valueOf(this.resetGoneThroughMainInRestore);
		values[29]=Boolean.valueOf(this.supportsMultipleMainCalls);
		return values;
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#processRestoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	@Override
	public void processRestoreState(FacesContext fc, Object arg1) {
		super.processRestoreState(fc, arg1);
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#processSaveState(javax.faces.context.FacesContext)
	 */
	@Override
	public Object processSaveState(FacesContext arg0) {
		// TODO Auto-generated method stub
		return super.processSaveState(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#processUpdates(javax.faces.context.FacesContext)
	 */
	@Override
	public void processUpdates(FacesContext fc) {
		super.processUpdates(fc);
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#processValidators(javax.faces.context.FacesContext)
	 */
	@Override
	public void processValidators(FacesContext arg0) {
		// TODO Auto-generated method stub
		super.processValidators(arg0);
	}

	/**
	 * Bridging method to call the old idegaWeb _main(IWContext method) to work inside JavaServerFaces.
	 * This can be done in three ways, from the IWPhaseListener, from encodeBegin(FacesContext) or from encodeChildren(FacesContext)
	 * @param fc
	 * @throws IOException
	 */
	public void callMain(FacesContext fc)throws IOException{
		try {
			//if(!goneThroughRenderPhase()){
				IWContext iwc = castToIWContext(fc);
				//This should only happen when the component is restored and before main is called the first time on the (restored)component
				if(isRestoredFromState()){
					if(!this.goneThroughMain&&resetGoneThroughMainInRestore()){
						resetBeforeMain(fc);
					}
				}
				//initVariables(iwc);
				//if(this instanceof AbstractTreeViewer){
				//	boolean check=true;
				//}
				if(mayGoThroughMain()){
					this._main(iwc);
				}
				setGoneThroughMain();
			//}
		}
		catch(NotLoggedOnException noex){
			//handle this exception specially and re-throw it
			throw noex;
		}
		catch (Exception e) {
			//e.printStackTrace();
			if(e instanceof IOException){
				throw (IOException)e;
			}
			else{
				//e.printStackTrace();
				//throw new IOException(e.getMessage());
			}
			getChildren().add(new Text(e.getMessage()));
		}
	}

	/**
	 * Resets the component before main is called (again in a state restored instance).
	 * This is done to be compatible with the JSF store/restore-state mechanism.
	 * @param context
	 */
	protected void resetBeforeMain(FacesContext context){
		empty();
		restoreFromReflectionProperties();
	}


	/**
	 * Bridging method to call the old idegaWeb print(IWContext method) to work inside JavaServerFaces.
	 * This is usually done from encodeBegin(FacesContext) or encodeChildren(FacesContext)
	 * @param fc
	 * @throws IOException
	 */
	protected void callPrint(FacesContext fc)throws IOException{
		try {
			//if(!goneThroughRenderPhase()){
				IWContext iwc = castToIWContext(fc);
				initVariables(iwc);
				this.print(iwc);
			//}
		}
		catch(NotLoggedOnException noex){
			//handle this exception specially and re-throw it
			throw noex;
		}
		catch (Exception e) {
			e.printStackTrace();
			if(e instanceof IOException){
				throw (IOException)e;
			}
			else{
				e.printStackTrace();
				//throw new IOException(e.getMessage());
			}
		}
	}

	@Override
	public void encodeBegin(FacesContext fc)throws IOException{
		CoreUtil.doEnsureScopeIsSet(fc);

		callMain(fc);
		callPrint(fc);
	}

	@Override
	public boolean isRendered() {
		IWContext iwc = castToIWContext(getFacesContext());
		boolean loggedIn = iwc.isLoggedOn();
		if ((renderForLoggedIn && renderForLoggedOut) || (renderForLoggedIn && loggedIn) || (renderForLoggedOut && !loggedIn)) {
			return super.isRendered();
		}
		else {
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeChildren(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		if(!goneThroughRenderPhase()) {
			for (Iterator<UIComponent> children = this.getChildren().iterator(); children.hasNext();) {
				renderChild(context, children.next());
			}
		}
	}


	/**
	 * Renders a child component for the current component. This operation is handy when implementing
	 * renderes that perform child rendering themselves (eg. a layout renderer/grid renderer/ etc..).
	 * Passes on any IOExceptions thrown by the child/child renderer.
	 *
	 * @param context the current FacesContext
	 * @param child which child to render
	 */
	protected void renderChild(FacesContext context, UIComponent child) throws IOException {
		if(child!=null){
			if(IWMainApplication.useJSF){
				/*if(child.isRendered()){
					child.encodeBegin(context);
					if(child.getRendersChildren()){
						child.encodeChildren(context);
					}
					if(child instanceof UIForm){
						boolean b = child.getRendersChildren();
						child.encodeChildren(context);
						Collection fChildren = child.getChildren();
						for (Iterator iter = fChildren.iterator(); iter.hasNext();) {
							UIComponent fChild = (UIComponent) iter.next();
							renderChild(context,fChild);
						}
					}
					child.encodeEnd(context);
				}*/
				RenderUtils.renderChild(context,child);
			}
			else{
				IWContext iwc = this.castToIWContext(context);
				try {
					((PresentationObject)child)._print(iwc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeEnd(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeEnd(FacesContext arg0) throws IOException {
		super.encodeEnd(arg0);
		this.setRenderedPhaseDone();
		resetGoneThroughMain();
	}

	protected IWContext castToIWContext(FacesContext fc){
		return IWContext.getIWContext(fc);
	}

	@Override
	public boolean getRendersChildren(){
		//This is overrided because PresentationObjects have no extra Renderer by default
		return true;
	}

	@Override
	public String getId(){
		if(super.getId()==null){
			return getID();
		}
		return super.getId();
	}

	@Override
	public String getFamily(){
		return "idegaweb";
	}


	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#getFacet(java.lang.String)
	 */
	@Override
	public UIComponent getFacet(String name) {
		return this.facetMap == null ? null : (UIComponent)this.facetMap.get(name);
	}

	@Override
	public Map<String, UIComponent> getFacets() {
		if (this.facetMap == null) {
			this.facetMap = new PresentationObjectComponentFacetMap(this);
		}
		return this.facetMap;
	}

	@Override
	public Iterator<UIComponent> getFacetsAndChildren() {
		//Overridded because Myfaces getFacetsAndChildren() doesn't call getFacets() and getChildren() properly
		return new FacetsAndChildrenIterator(getFacets(), getChildren());
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#getChildCount()
	 */
	@Override
	public int getChildCount() {
		 return this.childrenList == null ? 0 : this.childrenList.size();
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#getChildren()
	 */
	@Override
	public List<UIComponent> getChildren() {
		if (this.childrenList == null)
		{
			this.childrenList = new PresentationObjectComponentList(this);
		}
		return this.childrenList;
	}

	/**
	 * This method has a bug in the Builder, the attributes do not clone correctly.
	 */
	@Override
	public Map<String, Object> getAttributes(){
		//TODO: TL override this method because of the clone issue.
		//		There is a problem with that because of the Myfaces implementation of restoreSate() and saveState() in UIComponentBase
		return super.getAttributes();
	}


	protected void setRenderedPhaseDone(){
		this.goneThroughRenderPhase=true;
	}

	protected void setRenderedPhaseNotDone(){
		this.goneThroughRenderPhase=false;
	}

	protected boolean goneThroughRenderPhase(){
		return this.goneThroughRenderPhase;
	}

	/*
	 * END JSF SPECIFIC IMPLEMENTAION METHODS
	 */


	 //STANDARD LOGGING METHODS:

	 /**
	  * Logs out to the default log level (which is by default INFO)
	  * @param msg The message to log out
	  */
	 protected void log(String msg) {
		 //System.out.println(string);
		 getLogger().log(getDefaultLogLevel(),msg);
	 }

	 /**
	  * Logs out to the error log level (which is by default WARNING) to the default Logger
	  * @param e The Exception to log out
	  */
	 protected void log(Exception e) {
		 LoggingHelper.logException(e,this,getLogger(),getErrorLogLevel());
	 }

	 /**
	  * Logs out to the specified log level to the default Logger
	  * @param level The log level
	  * @param msg The message to log out
	  */
	 protected void log(Level level,String msg) {
		 //System.out.println(msg);
		 getLogger().log(level,msg);
	 }

	 /**
	  * Logs out to the error log level (which is by default WARNING) to the default Logger
	  * @param msg The message to log out
	  */
	 protected void logError(String msg) {
		 //System.err.println(msg);
		 getLogger().log(getErrorLogLevel(),msg);
	 }

	 /**
	  * Logs out to the debug log level (which is by default FINER) to the default Logger
	  * @param msg The message to log out
	  */
	 protected void logDebug(String msg) {
		 //System.err.println(msg);
		 getLogger().log(getDebugLogLevel(),msg);
	 }

	 /**
	  * Logs out to the SEVERE log level to the default Logger
	  * @param msg The message to log out
	  */
	 protected void logSevere(String msg) {
		 //System.err.println(msg);
		 getLogger().log(Level.SEVERE,msg);
	 }


	 /**
	  * Logs out to the WARNING log level to the default Logger
	  * @param msg The message to log out
	  */
	 protected void logWarning(String msg) {
		 //System.err.println(msg);
		 getLogger().log(Level.WARNING,msg);
	 }

	 /**
	  * Logs out to the CONFIG log level to the default Logger
	  * @param msg The message to log out
	  */
	 protected void logConfig(String msg) {
		 //System.err.println(msg);
		 getLogger().log(Level.CONFIG,msg);
	 }

	 /**
	  * Logs out to the debug log level to the default Logger
	  * @param msg The message to log out
	  */
	 protected void debug(String msg) {
		 logDebug(msg);
	 }

	 /**
	  * Gets the default Logger. By default it uses the package and the class name to get the logger.<br>
	  * This behaviour can be overridden in subclasses.
	  * @return the default Logger
	  */
	 protected Logger getLogger(){
		 return Logger.getLogger(this.getClass().getName());
	 }

	 /**
	  * Gets the log level which messages are sent to when no log level is given.
	  * @return the Level
	  */
	 protected Level getDefaultLogLevel(){
		 return Level.INFO;
	 }
	 /**
	  * Gets the log level which debug messages are sent to.
	  * @return the Level
	  */
	 protected Level getDebugLogLevel(){
		 return Level.FINER;
	 }
	 /**
	  * Gets the log level which error messages are sent to.
	  * @return the Level
	  */
	 protected Level getErrorLogLevel(){
		 return Level.WARNING;
	 }

	///**
	// * This method outputs the outputString to System.out if the Application
	//  * property "debug" is set to "TRUE"
	// */
	//public void debug(String outputString)
	//{
	//	 if (IWMainApplicationSettings.isDebugActive())
	//	 {
	//		 System.out.println("[DEBUG] \"" + outputString + "\" : " + this.getClassName());
	//	 }
	// }

	 public boolean isContainer() {
	 	return false;
	 }

	 private List getReflectionProperties(){

		 int icObjectInstanceId = getICObjectInstanceID();
		 String cacheKey=null;
		 if(icObjectInstanceId!=-1){
			 cacheKey = Integer.toString(icObjectInstanceId);
		 }
		 else{
			 cacheKey = getId();
		 }

		 if(cacheKey!=null){
			 List l = PropertyCache.getInstance().getPropertyList(cacheKey);
			 if (l != null && l.isEmpty()) {
				 l = PropertyCache.getInstance().getPropertyList(getId());
			 }
			 return l;
		 }

		 return null;
	 }

	 protected void restoreFromReflectionProperties(){
 		/*if(this.getClass().getName().indexOf("Navigation")!=-1){
 			boolean check=true;
 		}*/
	 	List properties = getReflectionProperties();
	 	if(properties!=null){
		 	for (Iterator iter = properties.iterator(); iter.hasNext();) {
				Property property = (Property) iter.next();
				try{
					property.setPropertyOnInstance(this);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
	 	}
	 }


	 /**
	  * Ancient way of setting property, handlers should now register properties with methodinvocation!
	  * Also if you are calling this method in code just use getAttributes().put(key,values); where values is preferably a List.
	  * This method is overridden in some old PO's like Image,Table and Page.
	  * @deprecated
	  * @param key
	  * @param values
	  */
	 @Deprecated
	public void setProperty(String key, String[] values){
		getAttributes().put(key,ListUtil.convertStringArrayToList(values));
	 }

	 /**
	  * Gets wether the object is recreated in JSFs restoreState phase. This is false if the object is just newly created
	  * @return
	  */
	 protected boolean isRestoredFromState(){
	 	return this.isStateRestored;
	 }

	 /**
	  * Returns wheather the "goneThroughMain" variable is reset back to false in the restore phase.
	  */
	 protected boolean resetGoneThroughMainInRestore(){
	 	return this.resetGoneThroughMainInRestore;
	 }

	 protected void setResetGoneThroughMainInRestore(boolean ifReset){
		 this.resetGoneThroughMainInRestore=ifReset;
	 }

	 /**
	  * Gets if the class allows to call the main(iwcontext) method
	  * @return
	  */
	 protected boolean mayGoThroughMain(){
	 	if(supportsMultipleMainCalls()){
	 		return true;
	 	}
	 	else{
	 		return !this.goneThroughMain;
	 	}
	 }
	 /**
	  * Gets if the class allows to call the main(iwcontext) method more than once on the same instance
	  * @return
	  */
	 protected boolean supportsMultipleMainCalls(){
	 	return this.supportsMultipleMainCalls;
	 }

	 protected void setSupportsMultipleMainCalls(boolean ifSupports){
		 this.supportsMultipleMainCalls=ifSupports;
	 }
	 /**
	  * Gets if the main(iwcontext) method has been called for this object
	  * @return
	  */
	 protected void setGoneThroughMain(){
	 	this.goneThroughMain=true;
	 }

	 protected void resetGoneThroughMain(){
	 	this.goneThroughMain=false;
	 }

	/**
	 * Removes the children of this component
	 */
	public void empty()
	{
		getChildren().clear();
		//theObjects.removeAll(theObjects);
	}

/**
	 * Gets the set Markup Language (HTML/XHTML/XHTML11) for the current IW Application.
	 * @return
	 */
	protected String getSetApplicationMarkupLanguage(){
		//changed by Sigtryggur 13.6.2005 IWContext.getInstance() returned null in some instances
		//return IWContext.getInstance().getApplicationSettings().getProperty(MARKUP_LANGUAGE, HTML);
	    return IWMainApplication.getDefaultIWMainApplication().getSettings().getDefaultMarkupLanguage();
	}

	/**
	 * <p>
	 * Checks if XHTML is set a the default markup language for the application. Returns true if that is the case.
	 * </p>
	 * @return true if XHTML is set.
	 */
	protected boolean isXhtmlSet(){
		String markup = getSetApplicationMarkupLanguage();
		if(markup.equals(Page.XHTML)){
			return true;
		}
		else if(markup.equals(Page.XHTML1_1)){
			return true;
		}
		return false;
	}

	/**
	 * <p>
	 * Encode a string to escape xhtml specific characters to an excaped xml format.<br/>
	 * For example: '& => &amp;', '< = &lt;'
	 * </p>
	 * @param unEncoded	The string to convert.
	 * @return
	 */
	protected String xhtmlEncode(String unEncoded){
		boolean xhtmlSet=isXhtmlSet();

		if(xhtmlSet){
			return TextSoap.convertSpecialCharacters(unEncoded);
		}
		return unEncoded;
	}


	/**
	 * Method to use in subclasses if facets should be cloned.  DOES NOT CHECK PERMISSIONS.
	 * @param obj The clone.
	 */
	protected void cloneJSFFacets(PresentationObject obj){
		//First clone the facet Map:
		if(this.facetMap!=null){
			obj.facetMap=(Map) ((PresentationObjectComponentFacetMap)this.facetMap).clone();
			((PresentationObjectComponentFacetMap)obj.facetMap).setComponent(obj);

			//Iterate over the children to clone each child:
			for (Iterator<String> iter = getFacets().keySet().iterator(); iter.hasNext();) {
				String key = iter.next();
				UIComponent component = getFacet(key);
				if(component instanceof PresentationObject){
					PresentationObject newObject = (PresentationObject)((PresentationObject)component).clone();
					newObject.setParentObject(obj);
					newObject.setLocation(this.getLocation());
					obj.getFacets().put(key,newObject);
				}
			}
		}
	}

	protected Object getValueBindingByAttributeExp(FacesContext ctx, String attributeName) {

		ValueBinding vb = getValueBinding(attributeName);

		if(vb != null) {

			String exp = vb.getExpressionString();

			if(exp != null) {

				return ctx.getApplication().createValueBinding(exp).getValue(ctx);
			}
		}

		return null;
	}

	public void setToRenderForLoggedIn(boolean render) {
		this.renderForLoggedIn = render;
	}

	public void setToRenderForLoggedOut(boolean render) {
		this.renderForLoggedOut = render;
	}

	@SuppressWarnings("unchecked")
    protected <T>T getExpressionValue(FacesContext ctx, String expression) {

    	ValueExpression vexp = getValueExpression(expression);
    	return vexp != null ? (T)vexp.getValue(ctx.getELContext()) : null;
    }
}