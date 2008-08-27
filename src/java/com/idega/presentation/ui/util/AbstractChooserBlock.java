package com.idega.presentation.ui.util;

import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.ui.HiddenInput;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

public abstract class AbstractChooserBlock extends Block {
	
	public static final String GLOBAL_HELPER_NAME = "ChooserHelper";
	
	private static final String ABSTRACT_CHOOSER_BLOCK_ACTIVE_ELEMENT_ID_PARAMETER = "$ABSTRACT_CHOOSER_BLOCK_ACTIVE_ELEMENT_ID_PARAMETER$";
	
	private String containerId = ICBuilderConstants.CHOOSER_PRESENTATION_OBJECT_CONTAINER_ID;
	private String idAttribute = null;
	private String valueAttribute = null;
	private String hiddenInputAttribute = null;
	private String chooserObject = null;
	
	private String value = null;
	private String displayValue = null;
	
	private Layer mainContainer = null;
	
	public AbstractChooserBlock() {
		super();
	}
	
	public AbstractChooserBlock(String idAttribute, String valueAttribute) {
		this();
		
		this.idAttribute = idAttribute;
		this.valueAttribute = valueAttribute;
	}
	
	public AbstractChooserBlock(String idAttribute, String valueAttribute, String hiddenInputAttribute) {
		this(idAttribute, valueAttribute);
		this.hiddenInputAttribute = hiddenInputAttribute;
	}
	
	public String getChooserObject() {
		return chooserObject == null ? GLOBAL_HELPER_NAME : chooserObject;
	}

	public void setChooserObject(String chooserObject) {
		this.chooserObject = chooserObject;
	}

	public void main(IWContext iwc) {
		getChooserAttributes();
		
		if (!StringUtil.isEmpty(getHiddenInputAttribute()) && !StringUtil.isEmpty(getValue())) {
			HiddenInput hiddenInput = new HiddenInput(getHiddenInputAttribute(), getValue());
			hiddenInput.setId(getHiddenInputAttribute());
			getMainContaier().add(hiddenInput);
		}
	}
	
	public Layer getMainContaier() {
		if (mainContainer == null) {
			mainContainer = new Layer();
			mainContainer.setId(getContainerId());
			mainContainer.setStyleAttribute("display: block;");
		}
		return mainContainer;
	}
	
	/**
	 * Action to remove selected property
	 * @param idAttribute
	 * @return
	 */
	public String getRemoveSelectedPropertyAction(String idAttribute) {
		return new StringBuffer(getChooserObject()).append(".removeAdvancedProperty('").append(idAttribute).append("');").toString();
	}
	
	/**
	 * Action to remove selected property
	 * @return
	 */
	public String getRemoveSelectedPropertyAction() {
		return getRemoveSelectedPropertyAction(idAttribute);
	}
	
	/**
	 * Action to choose selected property
	 * @param idAttribute
	 * @param valueAttribute
	 * @return
	 */
	public String getChooserObjectAction(String idAttribute, String valueAttribute) {
		return new StringBuffer(getChooserObject()).append(".chooseObject('").append(ABSTRACT_CHOOSER_BLOCK_ACTIVE_ELEMENT_ID_PARAMETER).append("', '")
			.append(idAttribute).append("', '").append(valueAttribute).append("');").toString();
	}
	
	/**
	 * Action to choose selected property
	 * @param simpleAction
	 * @return
	 */
	public String getChooserObjectAction(boolean simpleAction) {
		if (simpleAction) {
			return getChooserObjectAction(idAttribute, valueAttribute);
		}
		
		return getChooserObjectAction(idAttribute, valueAttribute, hiddenInputAttribute);
	}
	
	/**
	 * Action to choose selected property
	 * @param idAttribute
	 * @param valueAttribute
	 * @param hiddenInputAttribute
	 * @return
	 */
	public String getChooserObjectAction(String idAttribute, String valueAttribute, String hiddenInputAttribute) {
		StringBuffer action = new StringBuffer(getChooserObject()).append(".chooseObjectWithHidden('").append(ABSTRACT_CHOOSER_BLOCK_ACTIVE_ELEMENT_ID_PARAMETER)
			.append("', '").append(idAttribute).append("', '").append(valueAttribute).append("', '").append(hiddenInputAttribute).append("');");
		return action.toString();
	}

	/**
	 * Action to set 'viewable' value of selected property
	 * @param valueAttribute
	 * @return
	 */
	public String getChooserViewAction(String valueAttribute) {
		return new StringBuffer(getChooserObject()).append(".setChooserView('").append(ABSTRACT_CHOOSER_BLOCK_ACTIVE_ELEMENT_ID_PARAMETER).append("', '")
								.append(valueAttribute).append("');").toString();
	}
	
	/**
	 * Action to set 'viewable' value of selected property
	 * @return
	 */
	public String getChooserViewAction() {
		return getChooserViewAction(valueAttribute);
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}
	
	public String getHiddenInputAttribute() {
		return hiddenInputAttribute;
	}

	public void setHiddenInputAttribute(String hiddenInputParameter) {
		this.hiddenInputAttribute = hiddenInputParameter;
	}

	public String getIdAttribute() {
		return idAttribute;
	}

	public void setIdAttribute(String idAttribute) {
		this.idAttribute = idAttribute;
	}

	public String getValueAttribute() {
		return valueAttribute;
	}

	public void setValueAttribute(String valueAttribute) {
		this.valueAttribute = valueAttribute;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	/**
	 * Gets and sets additional attribute(s)
	 * @return
	 */
	public abstract boolean getChooserAttributes();
	
	public static final String getNormalizedAction(String action, String elementId) {
		if (StringUtil.isEmpty(action)) {
			return null;
		}
		String replacement = null;
		String whatToReplace = ABSTRACT_CHOOSER_BLOCK_ACTIVE_ELEMENT_ID_PARAMETER;
		if (StringUtil.isEmpty(elementId)) {
			elementId = "null";
			replacement = elementId;
			whatToReplace = new StringBuilder("'").append(ABSTRACT_CHOOSER_BLOCK_ACTIVE_ELEMENT_ID_PARAMETER).append("'").toString();
		}
		else {
			replacement = elementId;
		}
		
		action = StringHandler.replace(action, whatToReplace, replacement);
		return action;
	}
}