/*
 * $Id: IWPhaseListener.java,v 1.2 2004/12/07 15:09:55 tryggvil Exp $
 * Created on 3.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces;

import java.util.Iterator;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import com.idega.event.IWEventProcessor;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;


public class IWPhaseListener implements PhaseListener{

	private static Logger log = Logger.getLogger(IWPhaseListener.class.getName());
	
	/* (non-Javadoc)
	 * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
	 */
	public void afterPhase(PhaseEvent arg0) {
		callMain(arg0.getFacesContext(),arg0.getFacesContext().getViewRoot());
	}
	
	protected void callMain(FacesContext context,UIViewRoot root){
		log.fine("IWPhaseListener.callMain");
		IWContext iwc = IWContext.getIWContext(context);
		//recurseMain(iwc,root);
		call_Main(iwc,root);
	}
	
	/*
	protected void recurseMain(IWContext iwc,UIComponent comp){
		if(comp!=null){
			if(comp instanceof PresentationObject){
				PresentationObject po = (PresentationObject)comp;
				try {
					System.out.println("IWPhaseListener.recurseMain for "+po);
					po.facesMain(iwc);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			List children = comp.getChildren();
			for (Iterator iter = children.iterator(); iter.hasNext();) {
				UIComponent child = (UIComponent) iter.next();
				recurseMain(iwc,child);
			}
		}
	}*/

	/**
	 * This method goes down the tree to call the _main(iwc) methods of PresentationObjects
	 * @param iwc
	 * @param comp
	 */
	protected void call_Main(IWContext iwc,UIComponent comp){
		if(comp!=null){
			if(comp instanceof PresentationObject){
				PresentationObject po = (PresentationObject)comp;
				try {
					po._main(iwc);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				findNextInstanceOfNotPresentationObject(iwc,po);
			}
			else{
				//List children = comp.getChildren();
				//for (Iterator iter = children.iterator(); iter.hasNext();) {
				for (Iterator iter = comp.getFacetsAndChildren(); iter.hasNext();) {
					UIComponent child = (UIComponent) iter.next();
					call_Main(iwc,child);
				}
			}
		}
	}
	
	/**
	 * This method goes down the component tree to find a child that is not an instance
	 * of PresentationObject and calls call_Main for those components.<br>
	 * This is to handle if we have the case PresentationObject->UIComponent->PresentationObject 
	 * in the tree hierarchy and make sure _main(iwc) is called for all PresentationObjects.
	 * @param iwc
	 * @param comp
	 */
	protected void findNextInstanceOfNotPresentationObject(IWContext iwc, UIComponent comp) {
		if(comp!=null){
			if(comp instanceof PresentationObject){
				//List children = comp.getChildren();
				//for (Iterator iter = children.iterator(); iter.hasNext();) {
				for (Iterator iter = comp.getFacetsAndChildren(); iter.hasNext();) {
					UIComponent child = (UIComponent) iter.next();
					findNextInstanceOfNotPresentationObject(iwc,child);
				}
			}
			else{
				//List children = comp.getChildren();
				//for (Iterator iter = children.iterator(); iter.hasNext();) {
				for (Iterator iter = comp.getFacetsAndChildren(); iter.hasNext();) {
					UIComponent child = (UIComponent) iter.next();
					call_Main(iwc,child);
				}
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
	 */
	public void beforePhase(PhaseEvent arg0) {
		System.out.println("IWPhaseListener.processAllEvents:");
		IWEventProcessor.getInstance().processAllEvents(IWContext.getIWContext(arg0.getFacesContext()));
	}

	/* (non-Javadoc)
	 * @see javax.faces.event.PhaseListener#getPhaseId()
	 */
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
}