/*
 * $Id: ExpandContainer.java,v 1.3 2003/11/21 19:01:11 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */

package com.idega.presentation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.idega.idegaweb.IWBundle;

import com.idega.presentation.text.Text;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class ExpandContainer extends Block {

	private PresentationObject _header;
  private List _objects;
  private Image _plusIcon;
  private Image _minusIcon;
  private String _headerStyle;
  private boolean addContent = true;
  
  private IWBundle _iwb;

  public ExpandContainer() {
  	this(new Text("No text"));
  }

	public ExpandContainer(PresentationObject header) {
		setHeader(header);
	}

	/**
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		initialize(iwc);

		if (addContent) {
			Table table = new Table(3, 1);
			table.setWidth(2, 5);
			table.setCellpaddingAndCellspacing(0);
			table.setVerticalAlignment(1, 1, Table.VERTICAL_ALIGN_TOP);

			if (_headerStyle != null)
				_header.setStyleAttribute(_headerStyle);
			table.add(_header, 3, 1);
			table.add(Text.getBreak(), 3, 1);

			Layer layer = new Layer(Layer.DIV);
			layer.setMarkupAttribute("display", "none");
			if (_objects != null) {
				Iterator iter = _objects.iterator();
				while (iter.hasNext()) {
					PresentationObject object = (PresentationObject) iter.next();
					layer.add(object);
				}
			}
			table.add(layer, 3, 1);

			if (getPlusIcon() == null)
				_plusIcon = _iwb.getImage("shared/plusicon.gif");
			if (getMinusIcon() == null)
				_minusIcon = _iwb.getImage("shared/minusicon.gif");
			_plusIcon.setOnClick("showSection(this, document.getElementById('" + layer.getID() + "'), '" + _plusIcon.getMediaURL(iwc) + "','" + _minusIcon.getMediaURL(iwc) + "')");
			table.add(_plusIcon, 1, 1);

			super.add(table);
		}
	}
	
	protected void initialize(IWContext iwc) {
		_iwb = this.getBundle(iwc);
		
		try {
			Script script = getParentPage().getAssociatedScript();
			if (script == null) {
				script = new Script();
				getParentPage().setAssociatedScript(script);
			}
			script.addFunction("showSection", getJavascript());			
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			addContent = false;
		}
	}
	
	private String getJavascript() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("function showSection(image, error, openURL, closeURL) {").append("\n\t");
		buffer.append("if (error.style.display == 'none') {").append("\n\t\t");
		buffer.append("error.style.display = '';").append("\n\t\t");
		buffer.append("image.src = closeURL;").append("\n\t").append("}").append("\n\t");
		buffer.append("else {").append("\n\t\t");
		buffer.append("error.style.display = 'none';").append("\n\t\t");
		buffer.append("image.src = openURL;").append("\n\t");
		buffer.append("}").append("\n").append("}");
		return buffer.toString();
	}
	
  protected IWBundle getBundle() {
  	return _iwb;
  }
  
  public void setHeader(PresentationObject header) {
    this._header = header;
  }

	public void setHeader(String header) {
		setHeader(new Text(header));
	}

	/**
	 * @see com.idega.presentation.PresentationObjectContainer#add(com.idega.presentation.PresentationObject)
	 */
	public void add(PresentationObject object) {
		if (_objects == null)
			_objects = new ArrayList();
		_objects.add(object);
	}

	/**
	 * @return
	 */
	public Image getMinusIcon() {
		return _minusIcon;
	}

	/**
	 * @return
	 */
	public Image getPlusIcon() {
		return _plusIcon;
	}

	/**
	 * @param image
	 */
	public void setMinusIcon(Image image) {
		_minusIcon = image;
	}

	/**
	 * @param image
	 */
	public void setPlusIcon(Image image) {
		_plusIcon = image;
	}

	/**
	 * @param string
	 */
	public void setHeaderStyle(String string) {
		_headerStyle = string;
	}
}