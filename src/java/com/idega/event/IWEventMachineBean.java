package com.idega.event;

import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;

import com.idega.business.IBOSessionBean;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWLocation;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.util.ArrayUtil;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 **/

public class IWEventMachineBean extends IBOSessionBean implements IWEventMachine {

	private static final long serialVersionUID = -1882304329276711514L;
	private static final Logger LOGGER = Logger.getLogger(IWEventMachineBean.class.getName());
	
	/**
	 * 
	 * @uml.property name="_stateMap"
	 * @uml.associationEnd multiplicity="(0 1)" qualifier="idObj:java.lang.Object list:javax.swing.event.EventListenerList"
	 */
	private Map<Object, EventListenerList> _stateMap = new Hashtable<Object, EventListenerList>();
	
	public EventListenerList getListenersFor(ICObjectInstance instance) {
		return getListenersFor(instance.getPrimaryKey());
	}

	public EventListenerList getListenersFor(IWLocation location) {
		return getListenersFor((Object)location);
	}
	
	public EventListenerList getListenersFor(String location) {
		return getListenersFor((Object)location);
	}
	
	public EventListenerList getListenersForCompoundId(String compoundId) {
		return getListenersFor(compoundId);
	}

	private EventListenerList getListenersFor(Object idObj) {
		EventListenerList list = this.getUserStatesMap().get(idObj);
		if (list == null) {
			list = new EventListenerList();
			getUserStatesMap().put(idObj, list);
		}
		
		return list;
	}
	
	private Map<Object, EventListenerList> getUserStatesMap() {
		return this._stateMap;
	}
	
	public void processEvent(Page page, IWContext iwc) {
		IWPresentationEvent[] events = IWPresentationEvent.getCurrentEvents(iwc);
		if (ArrayUtil.isEmpty(events)) {
			return;
		}
		
		for (int i = 0; i < events.length; i++) {
			events[i].setPage(page);
			events[i].setIWContext(iwc);
		}
		
		Object id = IWPresentationEvent.getSource(iwc);
		if (id == null) {
			LOGGER.warning("Object ID can not be resolved!");
			return;
		}
		
		EventListenerList list = this.getListenersFor(id);
		if (list == null) {
			return;
		}
		
		IWActionListener[] listeners = list.getListeners(IWActionListener.class);
		for (int i = 0; i < listeners.length; i++) {
			IWActionListener listener = listeners[i];
			for (int j = 0; j < events.length; j++) {
				IWPresentationEvent event = events[j];
				try {
					listener.actionPerformed(event);
				} catch (IWException ex) {
					LOGGER.log(Level.WARNING, "Error performing event " + event + " for listener: " + listener, ex);
				}
			}
		}
	}
}