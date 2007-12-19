package com.idega.presentation.wizard;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1.2.1 $
 *
 * Last modified: $Date: 2007/12/19 18:05:05 $ by $Author: civilis $
 *
 */
public interface WizardStep extends Serializable {

	public String getIdentifier();
	
	public UIComponent getStepComponent(FacesContext context, Wizard wizard);
}