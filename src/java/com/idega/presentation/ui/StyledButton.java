/*
 * Created on 25.8.2004
 */
package com.idega.presentation.ui;

import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;


/**
 * @author laddi
 */
public class StyledButton extends Block {

	private GenericButton button;
	
	private String buttonStyleClass = "formSubmit";
	private String layerStyleClass = "divLayer";
	
	private String alignment;
	
	public StyledButton() {
	}
	
	public StyledButton(GenericButton button) {
		this.button = button;
	}
	
	public void main(IWContext iwc) {
		this.empty();
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass(this.layerStyleClass);
		if (this.alignment != null) {
			layer.setHorizontalAlignment(this.alignment);
		}
		
		if (this.button != null) {
			this.button.setStyleClass(this.buttonStyleClass);
			layer.add(this.button);
		}
		
		add(layer);
	}
	
	public GenericButton getButton() {
		return this.button;
	}

	/**
	 * @param button The button to set.
	 */
	public void setButton(GenericButton button) {
		this.button = button;
	}
	
	/**
	 * @param buttonStyleClass The buttonStyleClass to set.
	 */
	public void setButtonStyleClass(String buttonStyleClass) {
		this.buttonStyleClass = buttonStyleClass;
	}
	
	/**
	 * @param layerStyleClass The layerStyleClass to set.
	 */
	public void setLayerStyleClass(String layerStyleClass) {
		this.layerStyleClass = layerStyleClass;
	}
	
	/**
	 * @param alignment The alignment to set.
	 */
	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		StyledButton obj = null;
		try {
			obj = (StyledButton) super.clone();

			obj.layerStyleClass = this.layerStyleClass;
			obj.buttonStyleClass = this.buttonStyleClass;
			obj.alignment = this.alignment;
			if (this.button != null) {
				obj.button = (GenericButton) this.button.clone();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}

		return obj;
	}
}