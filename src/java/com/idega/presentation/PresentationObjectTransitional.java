/*
 * $Id: PresentationObjectTransitional.java,v 1.1 2005/03/09 02:13:14 tryggvil
 * Exp $ Created on 7.3.2005 in project com.idega.core
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.presentation;

import java.io.IOException;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWMainApplication;

/**
 * <p>
 * This class is a "transitional" class from the older PresentationObject
 * framework to JSF.<br>
 * This class can be subclassed if desired be compatible with some older
 * idegaWeb features such as the Builder but more structured as a pure JSF
 * component.<br>
 * Subclasses of this class can not use older features of the idegaWeb framework
 * such as the main() and print() methods, but are forced to use JSF standard
 * methods such as encodeBegin(),encodeChildren() and encodeBegin().
 * </p>
 * Last modified: $Date: 2006/02/01 13:54:42 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.3 $
 */
public class PresentationObjectTransitional extends PresentationObjectContainer {

	private boolean isInitialized = false;

	/**
	 * 
	 */
	public PresentationObjectTransitional() {
		super();
		// default state-aware:
		setTransient(false);
	}

	/**
	 * This method legacy and deprecated. It is set final and therefore not be
	 * overrided in subclasses as it is legacy from older idegaWeb
	 * 
	 * @deprecated
	 */
	public final void _main(IWContext iwc) throws Exception {
		super._main(iwc);
	}

	/**
	 * This method legacy and deprecated. It is set final and therefore not be
	 * overrided in subclasses as it is legacy from older idegaWeb
	 * 
	 * @deprecated
	 */
	public final void main(IWContext iwc) throws Exception {
		super.main(iwc);
	}

	/**
	 * This method legacy and deprecated. It is set final and therefore not be
	 * overrided in subclasses as it is legacy from older idegaWeb
	 * 
	 * @deprecated replaced with encodeXXX Methods in JSF.
	 */
	public final void _print(IWContext iwc) throws Exception {
		super._print(iwc);
	}

	/**
	 * This method legacy and deprecated. It is set final and therefore not be
	 * overrided in subclasses as it is legacy from older idegaWeb
	 * 
	 * @deprecated replaced with encodeXXX Methods in JSF.
	 */
	public final void print(IWContext iwc) throws Exception {
		super.print(iwc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
	 */
	public void encodeBegin(FacesContext context) throws IOException {
		callMain(context);

		super.encodeBegin(context);
		if (!isInitialized()) {
			initializeComponent(context);
			setInitialized();
		}
		else {
			updateComponent(context);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObjectContainer#encodeChildren(javax.faces.context.FacesContext)
	 */
	public void encodeChildren(FacesContext context) throws IOException {
		// super.encodeChildren(context);
		super.encodeChildren(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObject#encodeEnd(javax.faces.context.FacesContext)
	 */
	public void encodeEnd(FacesContext arg0) throws IOException {
		super.encodeEnd(arg0);
	}

	/**
	 * Returns wheather the "goneThroughMain" variable is reset back to false in
	 * the restore phase.
	 */
	protected boolean resetGoneThroughMainInRestore() {
		return false;
	}

	/**
	 * <p>
	 * This is a method that is ensured that is only called once in
	 * initalization in a state saved component. This method is intended to be
	 * implemented in subclasses for example to add components.<br/> This
	 * method is called from the standard encodeBegin() method.
	 * </p>
	 * 
	 * @param context
	 *            the FacesContext for the request
	 */
	protected void initializeComponent(FacesContext context) {
		// does nothing by default
	}

	/**
	 * <p>
	 * This method is called when the component is already initialized (i.e. the
	 * second time and onwards when a faces rendering is called upon this
	 * component when it is state saved) and usually happens when the component
	 * is restored after a "POST".<br/> This callback method could be overrided
	 * in sublcasses if something is meant to happen when a new request is sent
	 * on an already initialized component.<br/> This method is called from the
	 * standard encodeBegin() method.
	 * </p>
	 * 
	 * @param context
	 */
	protected void updateComponent(FacesContext context) {
		// Does nothing by default
	}

	/**
	 * <p>
	 * Returns if this component instance has been initialized, i.e. the
	 * initializeComponent() method called.
	 * </p>
	 * 
	 * @return
	 */
	protected boolean isInitialized() {
		return this.isInitialized;
	}

	protected void setInitialized() {
		this.isInitialized = true;
	}

	protected void setInitialized(boolean initialized) {
		this.isInitialized = initialized;
	}

	/**
	 * @see javax.faces.component.UIComponentBase#saveState(javax.faces.context.FacesContext)
	 */
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = new Boolean(isInitialized);
		return values;
	}

	/**
	 * @see javax.faces.component.UIComponentBase#restoreState(javax.faces.context.FacesContext,
	 *      java.lang.Object)
	 */
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		isInitialized = ((Boolean) values[1]).booleanValue();
	}

	/**
	 * <p>
	 * Get the IWMainapplication from the context
	 * </p>
	 * 
	 * @param context
	 * @return
	 */
	protected IWMainApplication getIWMainApplication(FacesContext context) {
		return IWMainApplication.getIWMainApplication(context);
	}
}
