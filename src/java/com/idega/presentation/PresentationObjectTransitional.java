/*
 * $Id: PresentationObjectTransitional.java,v 1.1 2005/03/09 02:13:14 tryggvil Exp $
 * Created on 7.3.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.io.IOException;
import javax.faces.context.FacesContext;


/**
 * <p>
 * This class is a "transitional" class from the older PresentationObject framework to JSF.<br>
 * This class can be subclassed if desired be compatible with some older idegaWeb features such as the Builder but more structured as a pure JSF component.<br>
 * Subclasses of this class can not use older features of the idegaWeb framework such as the main() and print() methods, but
 * are forced to use JSF standard methods such as encodeBegin(),encodeChildren() and encodeBegin().
 * </p>
 *  Last modified: $Date: 2005/03/09 02:13:14 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class PresentationObjectTransitional extends PresentationObjectContainer {

	/**
	 * 
	 */
	public PresentationObjectTransitional() {
		super();
		//default state-aware:
		setTransient(false);
	}

	/**
	 * This method legacy and deprecated.
	 * It is set final and therefore not be overrided in subclasses as it is legacy from older idegaWeb
	 * @deprecated
	 */
	public final void _main(IWContext iwc) throws Exception {
		super._main(iwc);
	}

	/**
	 * This method legacy and deprecated.
	 * It is set final and therefore not be overrided in subclasses as it is legacy from older idegaWeb
	 * @deprecated
	 */
	public final void main(IWContext iwc) throws Exception {
		super.main(iwc);
	}

	/**
	 * This method legacy and deprecated.
	 * It is set final and therefore not be overrided in subclasses as it is legacy from older idegaWeb
	 * @deprecated replaced with encodeXXX Methods in JSF.
	 */
	public final void _print(IWContext iwc) throws Exception {
		super._print(iwc);
	}

	/**
	 * This method legacy and deprecated.
	 * It is set final and therefore not be overrided in subclasses as it is legacy from older idegaWeb
	 * @deprecated replaced with encodeXXX Methods in JSF.
	 */
	public final void print(IWContext iwc) throws Exception {
		super.print(iwc);
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#encodeBegin(javax.faces.context.FacesContext)
	 */
	public void encodeBegin(FacesContext context) throws IOException {
		//Callmain needs to be called to process main in all possible children.
		callMain(context);
		//But no call to callPrint();
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#encodeChildren(javax.faces.context.FacesContext)
	 */
	public void encodeChildren(FacesContext context) throws IOException {
		//super.encodeChildren(context);
	
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#encodeEnd(javax.faces.context.FacesContext)
	 */
	public void encodeEnd(FacesContext arg0) throws IOException {
		super.encodeEnd(arg0);
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	public void restoreState(FacesContext context, Object state) {
		super.restoreState(context, state);
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#saveState(javax.faces.context.FacesContext)
	 */
	public Object saveState(FacesContext context) {
		return super.saveState(context);
	}
	
	
	 /**
	  * Returns wheather the "goneThroughMain" variable is reset back to false in the restore phase.
	  */
	 protected boolean resetGoneThroughMainInRestore(){
	 	return true;
	 }
	 
	
}
