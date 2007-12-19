package com.idega.presentation.wizard;

import com.idega.util.CoreConstants;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1.2.1 $
 *
 * Last modified: $Date: 2007/12/19 18:05:05 $ by $Author: civilis $
 *
 */
public class WizardControlValues {

	private String stepIdentifier = CoreConstants.EMPTY;

	public String getStepIdentifier() {
		return stepIdentifier;
	}

	public void setStepIdentifier(String stepIdentifier) {
		this.stepIdentifier = stepIdentifier;
	}
}