package com.idega.presentation.ui;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.apache.myfaces.component.html.util.HtmlComponentUtils;

import com.idega.presentation.IWBaseInputComponent;
import com.idega.util.CoreConstants;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1.2.5 $
 *
 * Last modified: $Date: 2007/12/28 20:52:19 $ by $Author: civilis $
 *
 */
public class UICreditCardNumber extends IWBaseInputComponent {

	public 	static final String COMPONENT_TYPE = 	"idega_CreditCardNumber";
	private static final String inputFacet = 		"input";
	private static final String spanTag = 			"span";
	private static final String valueAtt = 			"value";
	private static final String idAtt = 			"id";
	private String[] inputsIdentifiers;
	
	/**
	 * @Override
	 */
	protected void initializeComponent(FacesContext context) {
		super.initializeComponent(context);
		Application application = context.getApplication();
		
		for (int i = 0; i < 4; i++) {

			HtmlInputText input = (HtmlInputText)application.createComponent(HtmlInputText.COMPONENT_TYPE);
			input.setId(context.getViewRoot().createUniqueId());
			input.setMaxlength(4);
			input.setSize(4);
			getFacets().put(inputFacet+i, input);
		}
	}
	
	public boolean getRendersChildren() {
		return true;
	}
	
	public void encodeChildren(FacesContext context) throws IOException {
		super.encodeChildren(context);
		
		CreditCardNumber ccNumber = (CreditCardNumber)getValue();
		
		if(ccNumber == null)
			return;
		
		int i = 0;
		String[] inputsIndentifiers = getInputsIndentifiers();
		
		HtmlInputText inputText = (HtmlInputText)getFacet(inputFacet+i);
		
		inputText.setValue(ccNumber.getNumber1());
		inputsIndentifiers[i++] = inputText.getClientId(context);
		renderChild(context, inputText);
		
		inputText = (HtmlInputText)getFacet(inputFacet+i);
		inputText.setValue(ccNumber.getNumber2());
		inputsIndentifiers[i++] = inputText.getClientId(context);
		renderChild(context, inputText);
		
		inputText = (HtmlInputText)getFacet(inputFacet+i);
		inputText.setValue(ccNumber.getNumber3());
		inputsIndentifiers[i++] = inputText.getClientId(context);
		renderChild(context, inputText);
		
		inputText = (HtmlInputText)getFacet(inputFacet+i);
		inputText.setValue(ccNumber.getNumber4());
		inputsIndentifiers[i++] = inputText.getClientId(context);
		renderChild(context, inputText);	
	}
	
	private String[] getInputsIndentifiers() {
		
		if(inputsIdentifiers == null)
			inputsIdentifiers = new String[4];
		
		return inputsIdentifiers;
	}
	
	/**
	 * @Override
	 */
	public Object saveState(FacesContext context) {
		
		Object values[] = new Object[2];
		values[0] = super.saveState(context);
		values[1] = inputsIdentifiers;
		
		return values;
	}
	
	/**
	 * @Override
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(context, values[0]);
		inputsIdentifiers = (String[])values[1];
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
	
	public String getRendererType() {
		return null;
	}
	
	public Object getSubmittedValue() {
	
		FacesContext context = FacesContext.getCurrentInstance();
		Map requestParams = context.getExternalContext().getRequestParameterMap();
		String[] submitted = new String[4];
		
		for (int i = 0; i < inputsIdentifiers.length; i++) {
			
			String number = (String)requestParams.get(inputsIdentifiers[i]);
			submitted[i] = number;
		}
		
		return submitted;
	}
	
	public Object getValue() {
		
		CreditCardNumber ccNumber = (CreditCardNumber)super.getValue();
		
		if(ccNumber == null) {
			ValueBinding vb = getValueBinding(valueAtt);
			
			if(vb != null) {
				ccNumber = new CreditCardNumber();
				vb.setValue(FacesContext.getCurrentInstance(), ccNumber);
			}
		}
		
		return ccNumber;
	}
	
	protected Object getConvertedValue(FacesContext context, Object submittedValue) {
		
		CreditCardNumber ccNumber = (CreditCardNumber)getValue();
		
		if(ccNumber == null)
			return null;
		
		String[] submitted = (String[])submittedValue;
		
		boolean empty = true;
		
		for (int i = 0; i < submitted.length; i++) {
			
			if(submitted[i] != null && !CoreConstants.EMPTY.equals(submitted[i])) {
				
				empty = false;
				break;
			}
		}
		
		if(empty)
			return null;
		
		ccNumber.setNumber1(submitted[0]);
		ccNumber.setNumber2(submitted[1]);
		ccNumber.setNumber3(submitted[2]);
		ccNumber.setNumber4(submitted[3]);
		
		return ccNumber;
	}
}