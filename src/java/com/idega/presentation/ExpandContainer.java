/*
 * $Id: ExpandContainer.java,v 1.8 2008/12/03 09:38:35 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */

package com.idega.presentation;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWBundle;
import com.idega.presentation.text.Heading4;
import com.idega.util.PresentationUtil;

/**
 * <p>
 * Component that displays its children in a container that can be expanded and minimized
 * </p>
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class ExpandContainer extends Block {

	//private PresentationObject _header;
	//private List _objects;
	//private Image _plusIcon;
	//private Image _minusIcon;
	private String _headerStyle;
	private boolean expanded=false;
	private boolean initialized = false;
  //private IWBundle _iwb;


public ExpandContainer() {
  	this(new Heading4("No text"));
  }

	public ExpandContainer(PresentationObject header) {
		setHeader(header);
		setStyleClass("expandcontainer");
	}

	/**
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	@Override
	public void main(IWContext iwc) throws Exception {

		if(!this.initialized){
			initialize(iwc);
			this.initialized=true;
		}

		/*if (this.addContent) {
			//Table table = new Table(3, 1);
			//table.setWidth(2, 5);
			//table.setCellpaddingAndCellspacing(0);
			//table.setVerticalAlignment(1, 1, Table.VERTICAL_ALIGN_TOP);

			Layer outerLayer = new Layer();
			add(outerLayer);
			outerLayer.setStyleClass("exceptionwrapper");
			UIComponent header = getHeader();

			//if (this._headerStyle != null) {
			//	this._header.setStyleAttribute(this._headerStyle);
			//}
			if(header instanceof PresentationObject){
				PresentationObject pHeader = (PresentationObject)header;
				pHeader.setStyleAttribute(_headerStyle);
			}

			//table.add(this._header, 3, 1);
			Layer lHeader = new Layer();
			lHeader.setStyleClass("header");
			outerLayer.add(header);
			lHeader.add(header);
			//table.add(Text.getBreak(), 3, 1);

			Layer layer = new Layer(Layer.DIV);
			layer.setStyleClass("details");
			layer.setMarkupAttribute("display", "none");
//			if (this._objects != null) {
//				Iterator iter = this._objects.iterator();
//				while (iter.hasNext()) {
//					PresentationObject object = (PresentationObject) iter.next();
//					layer.add(object);
//				}
//			}
			//table.add(layer, 3, 1);
			outerLayer.add(layer);

			IWResourceBundle iwrb = getResourceBundle(iwc);
			Image plusIcon = getPlusIcon();
			Image minusIcon = getMinusIcon();
			if (plusIcon == null) {
				plusIcon = iwrb.getImage("shared/plusicon.gif");
			}
			if (minusIcon == null) {
				minusIcon = iwrb.getImage("shared/minusicon.gif");
			}
			plusIcon.setOnClick("showSection(this, document.getElementById('" + layer.getID() + "'), '" + plusIcon.getMediaURL(iwc) + "','" + minusIcon.getMediaURL(iwc) + "')");
			//table.add(this._plusIcon, 1, 1);

			//super.add(table);
		}
		*/

	}

	@Override
	public void encodeBegin(FacesContext context)throws IOException{
		IWContext iwc = IWContext.getIWContext(context);
		IWBundle iwb = IWContext.getInstance().getIWMainApplication().getCoreBundle();
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/expandcontainer.css"));

//		Script script = new Script();
//		script.addFunction("expandMinimize", getJavascript());
//		renderChild(context, script);

		context.getResponseWriter().startElement("div", this);
		context.getResponseWriter().writeAttribute("class",getStyleClass(),null);
		context.getResponseWriter().startElement("div", this);
		context.getResponseWriter().writeAttribute("class", "info",null);

		UIComponent header = getHeader();
		/*if (this._headerStyle != null) {
			this._header.setStyleAttribute(this._headerStyle);
		}*/
		if(header instanceof PresentationObject){
			PresentationObject pHeader = (PresentationObject)header;
			pHeader.setStyleAttribute(this._headerStyle);
		}
		renderChild(context, header);

		context.getResponseWriter().endElement("div");

		context.getResponseWriter().startElement("div", this);
		//context.getResponseWriter().writeAttribute("href", CoreConstants.NUMBER_SIGN,null);
		if(isExpanded()){
			context.getResponseWriter().writeAttribute("class", "expander expanded",null);
		}
		else{
			context.getResponseWriter().writeAttribute("class", "expander minimized",null);
		}
		context.getResponseWriter().writeAttribute("onClick", "expandMinimizeContents(this.parentNode);",null);
		context.getResponseWriter().endElement("div");

		context.getResponseWriter().startElement("div", this);
		if(isExpanded()){
			context.getResponseWriter().writeAttribute("class", "contents expanded",null);
		}
		else{
			context.getResponseWriter().writeAttribute("class", "contents minimized",null);
		}
		//context.getResponseWriter().writeAttribute("style", "display:none;",null);

	}

	@Override
	public void encodeChildren(FacesContext context)throws IOException{
		if(!goneThroughRenderPhase()){
			Iterator<UIComponent> children = this.getChildren().iterator();
			while (children.hasNext()) {
				UIComponent element = children.next();
				renderChild(context,element);
			}
		}
	}

	@Override
	public void encodeEnd(FacesContext context)throws IOException{
		context.getResponseWriter().endElement("div");
		context.getResponseWriter().endElement("div");
	}

	protected void initialize(IWContext iwc) {
		//this._iwb = this.getBundle(iwc);
//		try {
//			Script script = getParentPage().getAssociatedScript();
//			if (script == null) {
//				script = new Script();
//				getParentPage().setAssociatedScript(script);
//			}
//			script.addFunction("expandMinimize", getJavascript());
//		}
//		catch (Exception e) {
//			e.printStackTrace(System.err);
//			//this.addContent = false;
//		}
	}

	/*private String getJavascript() {
		StringBuffer buffer = new StringBuffer();
//		buffer.append("function showSection(image, error, openURL, closeURL) {").append("\n\t");
//		buffer.append("if (error.style.display == 'none') {").append("\n\t\t");
//		buffer.append("error.style.display = '';").append("\n\t\t");
//		buffer.append("image.src = closeURL;").append("\n\t").append("}").append("\n\t");
//		buffer.append("else {").append("\n\t\t");
//		buffer.append("error.style.display = 'none';").append("\n\t\t");
//		buffer.append("image.src = openURL;").append("\n\t");
//		buffer.append("}").append("\n").append("}");

		buffer.append("function expandMinimize(image) {").append("\n\t");
		buffer.append("if (image.styleClass.display == 'minimized') {").append("\n\t\t");
		buffer.append("error.style.display = '';").append("\n\t\t");
		buffer.append("image.src = closeURL;").append("\n\t").append("}").append("\n\t");
		buffer.append("else {").append("\n\t\t");
		buffer.append("error.style.display = 'none';").append("\n\t\t");
		buffer.append("image.src = openURL;").append("\n\t");
		buffer.append("}").append("}");
		return buffer.toString();
	}*/


  public void setHeader(UIComponent header) {
    //this._header = header;
	  getFacets().put("header", header);
  }

	public void setHeader(String header) {
		UIComponent text = new Heading4(header);
		setHeader(text);
	}

	public UIComponent getHeader(){
		return getFacets().get("header");
	}

	/*
	public void add(PresentationObject object) {
		if (this._objects == null) {
			this._objects = new ArrayList();
		}
		this._objects.add(object);
	}
	*/

	/*
	public Image getMinusIcon() {
		return (Image) getFacet("minusicon");
	}

	public Image getPlusIcon() {
		return (Image) getFacet("plusicon");
	}


	/*
	public void setMinusIcon(Image image) {
		getFacets().put("minusicon", image);
	}

	public void setPlusIcon(Image image) {
		getFacets().put("plusicon", image);
	}
	*/
	public void setHeaderStyle(String string) {
		this._headerStyle = string;
	}



	/**
	 * @return the expanded
	 */
	boolean isExpanded() {
		return this.expanded;
	}


	/**
	 * @param expanded the expanded to set
	 */
	void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(context, values[0]);
		this._headerStyle=(String) values[1];
		this.expanded = ((Boolean)values[2]).booleanValue();
		this.initialized = ((Boolean)values[3]).booleanValue();

	}
	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
	 */
	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[4];
		values[0] = super.saveState(context);
		values[1] = this._headerStyle;
		values[2] = Boolean.valueOf(this.expanded);
		values[3] = Boolean.valueOf(this.initialized);
		return values;
	}
}