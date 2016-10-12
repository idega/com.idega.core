package com.idega.event;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.idega.core.component.data.ICObjectInstance;
import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.EventViewerPage;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.Parameter;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.ArrayUtil;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public abstract class IWPresentationEvent extends EventObject implements Cloneable {

	private static final long serialVersionUID = 3008125680698742615L;

	public final static String PRM_IW_EVENT = "iw_event_type";
	public final static String PRM_IW_EVENT_SOURCE = "iw_ev_src";

	private static String IW_EVENT_HANDLER_SERVLET_URL = "/servlet/IWEventHandler";

	private String eventHandlerURL = null;

	public static String DEFAULT_IW_EVENT_TARGET="iw_event_frame";
	private String eventTarget = null;

	private static final Logger LOGGER = Logger.getLogger(IWPresentationEvent.class.getName());

	/**
	 *
	 * @uml.property name="_parameters"
	 * @uml.associationEnd multiplicity="(0 -1)" elementType="com.idega.presentation.ui.Parameter"
	 */
	private List<Parameter> _parameters = new ArrayList<Parameter>();

	/**
	 *
	 * @uml.property name="_page"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	private Page _page = null;

	/**
	 *
	 * @uml.property name="_iwc"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	private IWContext _iwc = null;

	private String sourceParameterValue = null;

  public IWPresentationEvent(){
    this(PresentationObject.NULL_CLONE_OBJECT);
  }

  public void setIWContext(IWContext iwc){
    this._iwc = iwc;
  }

  public IWContext getIWContext(){
    return this._iwc;
  }

  public Page getPage(){
    return this._page;
  }

  public void setPage(Page page){
    this._page = page;
  }

  public boolean containsParameter(Parameter prm){
    return this._parameters.contains(prm);
  }

  public IWPresentationEvent(PresentationObject source){
    super(source);
    initializeVariables();
    this.addParameter(PRM_IW_EVENT, IWMainApplication.getEncryptedClassName(this.getClass()));
  }

  public IWPresentationEvent(IWContext iwc) throws NoSuchEventException{
    this();
    boolean ok = this.initializeEvent(iwc);
    if(!ok){
      NoSuchEventException ex = new NoSuchEventException("No Event of type: " + this.getClass().getName());
      throw ex;
    }
  }

  public void initializeVariables(){
  }

  public void setSource(PresentationObject source) {
    setSource(source.getCompoundId());
   /* if(source.getICObjectInstanceID() > 0){
      setSource(source.getICObjectInstanceID());
    } else if(source.getLocation() != null){
      setSource(source.getLocation());
    }
    //this.source = source;*/
  }

  public String getSourceParameterValue() {
  	return this.sourceParameterValue;
  }

  public void setSource(IWLocation source) {
  	this.sourceParameterValue = source.getLocationString();
    this.addParameter(PRM_IW_EVENT_SOURCE, this.sourceParameterValue);

  }

  public void setSource(int instanceId) {
  	this.sourceParameterValue = Integer.toString(instanceId);
    this.addParameter(PRM_IW_EVENT_SOURCE, this.sourceParameterValue);
  }

  public void setSource(ICObjectInstance instance) throws RemoteException {
  	this.sourceParameterValue = ((Integer)instance.getPrimaryKey()).toString();
    this.addParameter(PRM_IW_EVENT_SOURCE, this.sourceParameterValue);
  }

  public void setSource(String compoundId) {
  	this.sourceParameterValue = compoundId;
    this.addParameter(PRM_IW_EVENT_SOURCE, this.sourceParameterValue);
  }

//  public IWPresentationEvent(PresentationObject source) {
//    this();
//    setSource(source);
//  }
//
//  public void setSource(PresentationObject source){
//    if(source.getICObjectInstanceID() > 0){
//      this.addParameter(PRM_EVENT_SOURCE, Integer.toString(source.getICObjectInstanceID()));
//    } else {
//
//    }
//  }

  protected void addParameter(String prmName, String value){
    Parameter prm = new Parameter(prmName,value);
    this.addParameter(prm);
  }

  protected void addParameter(String prmName, int value){
    Parameter prm = new Parameter(prmName,Integer.toString(value));
    this.addParameter(prm);
  }

  public void addParameter(Parameter prm){
    this._parameters.add(prm);
  }

  public Iterator<Parameter> getParameters(){
    return this._parameters.iterator();
  }

  public abstract boolean initializeEvent(IWContext iwc);

  @SuppressWarnings("unchecked")
  @Override
  public Object clone() {
	  IWPresentationEvent model = null;

	  try {
		  model = (IWPresentationEvent) super.clone();
		  if (this._parameters != null) {
			  model._parameters = (List<Parameter>) ((ArrayList<Parameter>) this._parameters).clone();
		  } else {
			  model._parameters = new ArrayList<Parameter>();
		  }
	  } catch (CloneNotSupportedException ex) {
		  ex.printStackTrace(System.err);
	  }

	  return model;
  }

  public static IWPresentationEvent[] getCurrentEvents(IWContext iwc){
	  String[] classNames = iwc.getParameterValues(PRM_IW_EVENT);

	  if (ArrayUtil.isEmpty(classNames)) {
		  return new IWPresentationEvent[0];
	  }

      IWPresentationEvent[] events = new IWPresentationEvent[classNames.length];
      int index = 0;
      for (int i = 0; i < classNames.length; i++) {
        String className = IWMainApplication.decryptClassName(classNames[i]);

        boolean ok = false;
        IWPresentationEvent event = null;
        try {
          event = (IWPresentationEvent) RefactorClassRegistry.forName(className).newInstance();
          ok = event.initializeEvent(iwc);
        } catch(ClassCastException cce) {
          ok = false;
        } catch(ClassNotFoundException cnfe) {
          ok = false;
        } catch(IllegalAccessException iae) {
          ok = false;
        } catch(InstantiationException ie) {
          ok = false;
        } catch (Exception e) {
        	ok = false;
        }

        if (ok) {
          events[index++] = event;
        }
      }

      if (index < classNames.length) {
        IWPresentationEvent[] newEvents = new IWPresentationEvent[index];
        System.arraycopy(events,0,newEvents,0,index);
        return newEvents;
      } else {
        return events;
      }
  }

  public static boolean anyEvents(IWContext iwc){
    return (iwc.getParameter(PRM_IW_EVENT) != null);
  }

  public static Object getSource(IWContext iwc){
    String sourceString = iwc.getParameter(PRM_IW_EVENT_SOURCE);
   // try {
   //   Integer primaryKey = new Integer(sourceString);
//      ICObjectInstance instance = (ICObjectInstance)IDOLookup.findByPrimaryKey(ICObjectInstance.class,primaryKey);
//      return instance;
    //  return primaryKey;
    //}
    //catch (NumberFormatException ex) {
      // Source is location
    //}
//    catch (RemoteException rex) {
//      throw new RuntimeException(rex.getMessage());
//    }
//    catch (FinderException fe) {
//      throw new RuntimeException(fe.getMessage());
//    }
    return sourceString;
    //return IWPresentationLocation.getLocationObject(sourceString);
  }

	/**
	 * Returns the eventHandlerURL.
	 * @return String
	 */
	public String getEventHandlerURL(IWContext iwc) {
		if (this.eventHandlerURL == null) {
			this.eventHandlerURL = getEventHandlerFrameURL(iwc);
		}
		return this.eventHandlerURL;
	}

	/**
	 * Returns the URL to the Event handling frame prefixed with the servlet context path if any.
	 * @return String
	 */
	public static String getEventHandlerFrameURL(IWContext iwc) {
		if (IWMainApplication.useNewURLScheme) {
			return iwc.getIWMainApplication().getWindowOpenerURI(EventViewerPage.class);
		} else {
			return iwc.getIWMainApplication().getTranslatedURIWithContext(IW_EVENT_HANDLER_SERVLET_URL);
		}
	}

	/**
	 * Sets the eventHandlerURL.
	 * @param eventHandlerURL The eventHandlerURL to set
	 *
	 * @uml.property name="eventHandlerURL"
	 */
	public void setEventHandlerURL(String eventHandlerURL) {
		this.eventHandlerURL = eventHandlerURL;
	}

	/**
	 * Returns the eventTarget.
	 * @return String
	 *
	 * @uml.property name="eventTarget"
	 */
	public String getEventTarget() {
		if (this.eventTarget == null) {
			this.eventTarget = DEFAULT_IW_EVENT_TARGET;
		}
		return this.eventTarget;
	}

	/**
	 * Sets the eventTarget.
	 * @param eventTarget The eventTarget to set
	 *
	 * @uml.property name="eventTarget"
	 */
	public void setEventTarget(String eventTarget) {
		this.eventTarget = eventTarget;
	}
}