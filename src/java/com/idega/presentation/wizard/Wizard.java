package com.idega.presentation.wizard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.idega.presentation.IWBaseComponent;
import com.idega.util.CoreConstants;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1.2.3 $
 *
 * Last modified: $Date: 2007/12/21 15:03:00 $ by $Author: civilis $
 *
 */
public abstract class Wizard extends IWBaseComponent {
	
	private Logger logger;
	private static final String valueAtt = "value";
	private static final String stepHolderFacet = "stepHolder";
	private static final String containerFacet = "container";
	private static final String stepIdentifierExp = "#{wizardControlValues.stepIdentifier}";
	private static final String wizardControlValuesExp = "#{wizardControlValues}";
	
	private String latterStepIdentifier;
	
	/** List<String>*/
	private List wizardStepsComponentsIdentifiersSequence = new ArrayList();
	
	/** Map<String, wizardStep>*/
	private Map wizardStepsMap = new HashMap();

	/**
	 * @Override
	 */
	protected void initializeComponent(FacesContext context) {
		super.initializeComponent(context);
		
		List wizardSteps = getWizardSteps();
		
		if(wizardSteps == null)
			return;
		
		for (Iterator iterator = wizardSteps.iterator(); iterator.hasNext();) {
			WizardStep wizardStep = (WizardStep) iterator.next();
			
			if(wizardStep.getIdentifier() == null || CoreConstants.EMPTY.equals(wizardStep.getIdentifier()))
				continue;
			
			wizardStepsComponentsIdentifiersSequence.add(wizardStep.getIdentifier());
			wizardStepsMap.put(wizardStep.getIdentifier(), wizardStep);
		}
		
		Application application = context.getApplication();
		
		HtmlInputHidden stepHolder = (HtmlInputHidden)application.createComponent(HtmlInputHidden.COMPONENT_TYPE);
		stepHolder.setId(context.getViewRoot().createUniqueId());
		stepHolder.setValueBinding(valueAtt, application.createValueBinding(stepIdentifierExp));
		
		getFacets().put(stepHolderFacet, stepHolder);
		
		UIComponent container = getContainer(context);
		
		if(container == null) {
			container = context.getApplication().createComponent(HtmlForm.COMPONENT_TYPE);
			container.setId(context.getViewRoot().createUniqueId());
		}
		
		getFacets().put(containerFacet, getContainer(context));
	}
	
	protected abstract UIComponent getContainer(FacesContext context);
	
	/**
	 * @Override
	 */
	public void encodeChildren(FacesContext context) throws IOException {
		super.encodeChildren(context);
		
		if(wizardStepsComponentsIdentifiersSequence == null || wizardStepsComponentsIdentifiersSequence.isEmpty())
			return;
		
		ValueBinding vb = context.getApplication().createValueBinding(wizardControlValuesExp);
		WizardControlValues controlValues = (WizardControlValues)vb.getValue(context);
		
		String identifier = controlValues.getStepIdentifier() == null || CoreConstants.EMPTY.equals(controlValues.getStepIdentifier()) ? (String)wizardStepsComponentsIdentifiersSequence.get(0) : controlValues.getStepIdentifier();
		
		UIComponent container = getFacet(containerFacet);
		container.setRendered(true);
		container.getChildren().clear();
		container.getChildren().add(getFacet(stepHolderFacet));
		
		UIComponent stepComponent;
		
		if(latterStepIdentifier == null || !identifier.equals(latterStepIdentifier)) {
			
			latterStepIdentifier = identifier;
			
			WizardStep wizardStep = (WizardStep)wizardStepsMap.get(identifier);
			stepComponent = wizardStep.getStepComponent(context, this);
			
			if(stepComponent != null) {
				
				stepComponent.setRendered(true);
				getFacets().put(identifier, stepComponent);
			}
			
		} else {
			stepComponent = getFacet(identifier);
		}

		if(stepComponent != null)
			container.getChildren().add(stepComponent);
		else {
//			TODO: log
		}
		
		renderChild(context, container);
	}
	
	/**
	 * 
	 * @return List<wizardStep>
	 */
	public abstract List getWizardSteps();
	
	protected Logger getLogger() {
		
		if(logger == null)
			logger = Logger.getLogger(this.getClass().getName());
		
		return logger;
	}
	
	public HtmlCommandButton getNextButton(FacesContext context, WizardStep step) {
		
		String stepIdentifier = step.getIdentifier();
		
		int stepIndex = wizardStepsComponentsIdentifiersSequence.lastIndexOf(stepIdentifier);
		
		if(stepIndex < 0)
			throw new RuntimeException("Step provided is not in steps list: "+stepIdentifier);
		
		if(stepIndex == wizardStepsComponentsIdentifiersSequence.size()-1)
			return null;
		
		Application application = context.getApplication();
		HtmlCommandButton nextButton = (HtmlCommandButton)application.createComponent(HtmlCommandButton.COMPONENT_TYPE);
		nextButton.setId(context.getViewRoot().createUniqueId());
		
		nextButton.setValue("Next");
		
		nextButton.setOnclick(new StringBuffer("document.getElementById('").append(((UIComponent)getFacets().get(stepHolderFacet)).getClientId(context)).append("').value='").append(wizardStepsComponentsIdentifiersSequence.get(stepIndex+1)).append("';").toString());
		
		return nextButton;
	}
	
	public HtmlCommandButton getPreviousButton(FacesContext context, WizardStep step) {
		
		String stepIdentifier = step.getIdentifier();
		
		int stepIndex = wizardStepsComponentsIdentifiersSequence.lastIndexOf(stepIdentifier);
		
		if(stepIndex < 0)
			throw new RuntimeException("Step provided is not in steps list: "+stepIdentifier);
		
		if(stepIndex == 0)
			return null;
		
		Application application = context.getApplication();
		HtmlCommandButton prevButton = (HtmlCommandButton)application.createComponent(HtmlCommandButton.COMPONENT_TYPE);
		prevButton.setId(context.getViewRoot().createUniqueId());
		prevButton.setValue("Previous");
		
		prevButton.setOnclick(new StringBuffer("document.getElementById('").append(((UIComponent)getFacets().get(stepHolderFacet)).getClientId(context)).append("').value='").append(wizardStepsComponentsIdentifiersSequence.get(stepIndex-1)).append("';").toString());
		
		return prevButton;
	}
	
	public HtmlCommandButton getCustomStepButton(WizardStep step) {
		
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	/**
	 * @Override
	 */
	public Object saveState(FacesContext context) {
		
		Object values[] = new Object[4];
		values[0] = super.saveState(context);
		values[1] = wizardStepsComponentsIdentifiersSequence;
		values[2] = latterStepIdentifier;
		values[3] = wizardStepsMap;
		
		return values;
	}
	
	/**
	 * @Override
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(context, values[0]);
		wizardStepsComponentsIdentifiersSequence = (List)values[1];
		latterStepIdentifier = (String)values[2];
		wizardStepsMap = (Map)values[3];
	}
	
	/**
	 * @Override
	 */
	public boolean getRendersChildren() {
		return true;
	}
}