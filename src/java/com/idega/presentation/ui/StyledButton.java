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
	
	public StyledButton() {
	}
	
	public StyledButton(GenericButton button) {
		this.button = button;
	}
	
	public void main(IWContext iwc) {
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass(layerStyleClass);
		
		if (button != null) {
			button.setStyleClass(buttonStyleClass);
			layer.add(button);
		}
		
		add(layer);
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
}