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
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.apache.myfaces.component.html.util.HtmlComponentUtils;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1.2.8 $
 *
 * Last modified: $Date: 2007/12/30 15:26:03 $ by $Author: civilis $
 *
 */
public abstract class Wizard extends IWBaseComponent {
	
	private Logger logger;
	
	private static final String stepHolderFacet = "stepHolder";
	private static final String currentStepFacet = "currentStep";
	private static final String stepIdentifierExp = "#{wizardControlValues.stepIdentifier}";
	private static final String wizardControlValuesExp = "#{wizardControlValues}";
	private static final String spanTag = "span";
	private static final String valueAtt = "value";
	private static final String idAtt = "id";
	
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
	}
	
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
		
		renderChild(context, getFacet(stepHolderFacet));
		
		UIComponent stepComponent;
		
		if(latterStepIdentifier == null || !identifier.equals(latterStepIdentifier)) {
			
			latterStepIdentifier = identifier;
			
			WizardStep wizardStep = (WizardStep)wizardStepsMap.get(identifier);
			stepComponent = wizardStep.getStepComponent(context, this);
			getFacets().put(currentStepFacet, stepComponent);
			
		} else {
			
			stepComponent = getFacet(currentStepFacet);
		}
		
		if(stepComponent != null)
			renderChild(context, stepComponent);
	}

	/**
	 * @Override
	 */
	public void processValidators(FacesContext context) {
		super.processValidators(context);
		
		ValueBinding vb = context.getApplication().createValueBinding(wizardControlValuesExp);
		WizardControlValues controlValues = (WizardControlValues)vb.getValue(context);
		
//		validation failed
		if(context.getMessages().hasNext()) {
			
			if(controlValues.getStepIdentifier() == null || CoreConstants.EMPTY.equals(controlValues.getStepIdentifier()))
				controlValues.setStepIdentifier(latterStepIdentifier);

//		validation succeeded
		} else {
			
			if(controlValues.getStepIdentifier() == null || CoreConstants.EMPTY.equals(controlValues.getStepIdentifier()))
				controlValues.setStepIdentifier((String)context.getExternalContext().getRequestParameterMap().get(getFacet(stepHolderFacet).getClientId(context)));
		}
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
		IWContext iwc = IWContext.getIWContext(context);
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWMainApplication.CORE_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		
		HtmlCommandButton nextButton = (HtmlCommandButton)application.createComponent(HtmlCommandButton.COMPONENT_TYPE);
		nextButton.setId(context.getViewRoot().createUniqueId());
		
		nextButton.setValue(iwrb.getLocalizedString("nextButton", "Next"));
		
		nextButton.setOnclick(new StringBuffer("document.getElementById('").append(((UIComponent)getFacets().get(stepHolderFacet)).getClientId(context)).append("').value='").append(wizardStepsComponentsIdentifiersSequence.get(stepIndex+1)).append("';").toString());
		
		return nextButton;
	}
	
	public HtmlCommandButton getPreviousButton(FacesContext context, WizardStep step) {
		
		String stepIdentifier = step.getIdentifier();
		IWContext iwc = IWContext.getIWContext(context);
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWMainApplication.CORE_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		
		int stepIndex = wizardStepsComponentsIdentifiersSequence.lastIndexOf(stepIdentifier);
		
		if(stepIndex < 0)
			throw new RuntimeException("Step provided is not in steps list: "+stepIdentifier);
		
		if(stepIndex == 0)
			return null;
		
		Application application = context.getApplication();
		HtmlCommandButton prevButton = (HtmlCommandButton)application.createComponent(HtmlCommandButton.COMPONENT_TYPE);
		prevButton.setId(context.getViewRoot().createUniqueId());
		prevButton.setValue(iwrb.getLocalizedString("previousButton", "Previous"));
		
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
	
	/**
	 * @Override
	 */
	public void encodeBegin(FacesContext context) throws IOException {
		super.encodeBegin(context);
		
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement(spanTag, this);
		writer.writeAttribute(idAtt, getClientId(context), null);
	}
	
	/**
	 * @Override
	 */
	public void encodeEnd(FacesContext context) throws IOException {
		super.encodeEnd(context);
		
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement(spanTag);
		writer.flush();
	}

	/**
	 * @Override
	 */
	public String getClientId(FacesContext context) {
        String clientId = HtmlComponentUtils.getClientId(this, getRenderer(context), context);
        
        if(clientId == null)
            clientId = super.getClientId(context);
        
        return clientId;
    }
}