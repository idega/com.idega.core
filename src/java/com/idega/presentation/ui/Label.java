/*
 * $Id: Label.java,v 1.3 2005/03/02 09:18:49 laddi Exp $
 *
 * Copyright (C) 2002-2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWConstants;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Text;
import com.idega.util.ListUtil;

/**
 * <p>
 * This class renders out a &lt;label&gt; element used in forms.
 * </p>
 *  Last modified: $Date: 2005/03/02 09:18:49 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">Laddi</a>,<a href="mailto:tryggvi@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.3 $
 */
public class Label extends InterfaceObject {
		
	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[1];
		values[0] = super.saveState(ctx);
		return values;
	}
	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
	}
	
	public Label() {
		initialize(null);
	}
	
	public Label(InterfaceObject object) {
		this((String) null, object);
	}
	
	public Label(UIInput inputObject){
		this(null,inputObject);
	}
	
	public Label(String label, UIInput object) {
		setFor(object.getClientId(FacesContext.getCurrentInstance()));
		initialize(label);
	}
	
	public Label(String label, InterfaceObject object) {
		setFor(object.getId());
		initialize(label);
	}
	
	public Label(UIComponent labelObject, InterfaceObject object) {
		this((String) null, object);
		add(labelObject);
	}
	
	private void initialize(String label) {
		if (label != null) {
			add(new Text(label));
		}
		setTransient(false);
	}
	
	public void setFor(UIInput input,FacesContext context){
		setFor(input.getClientId(context));
	}
	
	public void setFor(UIInput input){
		setFor(input,FacesContext.getCurrentInstance());
	}
	
	public void setFor(String forId){
		setMarkupAttribute("for", forId);
	}
	
	@Override
	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			print("<label "+getMarkupAttributesString()+" >");

			List<UIComponent> children = this.getChildren();
			if (!ListUtil.isEmpty(children)) {
				for (UIComponent item: children) {
					renderChild(iwc, item);
				}
			}

			println("</label>");	
		} else if (IWConstants.MARKUP_LANGUAGE_WML.equals(getMarkupLanguage())) {	
			print(this._label);
			print("<br/>");	
		}
	}	
	
	@Override
	public void setLabel(String label) {
		add(new Text(label));
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	@Override
	public void handleKeepStatus(IWContext iwc) {
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	@Override
	public boolean isContainer() {
		return true;
	}
}