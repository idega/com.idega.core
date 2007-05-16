package com.idega.presentation.ui.util;

import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;

public abstract class AbstractChooserBlock extends Block {
	
	private String containerId = ICBuilderConstants.CHOOSER_PRESENTATION_OBJECT_CONTAINER_ID;
	private String idAttribute = null;
	private String valueAttribute = null;
	private String hiddenInputAttribute = null;
	
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
	
	public void main(IWContext iwc) {
		getChooserAttributes();
	}
	
	public Layer getMainContaier() {
		Layer container = new Layer();
		container.setId(getContainerId());
		container.setStyleAttribute("display: block;");
		return container;
	}
	
	/**
	 * Action to remove selected property
	 * @param idAttribute
	 * @return
	 */
	public String getRemoveSelectedPropertyAction(String idAttribute) {
		return new StringBuffer("removeAdvancedProperty('").append(idAttribute).append("');").toString();
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
		return new StringBuffer("chooseObject(this, '").append(idAttribute).append("', '").append(valueAttribute).append("');").toString();
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
		StringBuffer action = new StringBuffer("chooseObjectWithHidden(this, '").append(idAttribute).append("', '");
		action.append(valueAttribute).append("', '").append(hiddenInputAttribute).append("');");
		return action.toString();
	}

	/**
	 * Action to set 'viewable' value of selected property
	 * @param valueAttribute
	 * @return
	 */
	public String getChooserViewAction(String valueAttribute) {
		return new StringBuffer("setChooserView(this, '").append(valueAttribute).append("');").toString();
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
	
	/**
	 * Gets and sets additional attribute(s)
	 * @return
	 */
	public abstract boolean getChooserAttributes();
}