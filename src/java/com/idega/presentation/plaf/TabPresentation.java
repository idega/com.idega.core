package com.idega.presentation.plaf;



import javax.swing.SingleSelectionModel;

import java.util.Vector;

import com.idega.presentation.PresentationObject;

import com.idega.presentation.text.Link;

import com.idega.util.IWColor;

import com.idega.presentation.ui.Form;

/**

 * Title:        idegaWeb

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega.is

 * @author

 * @version 1.0

 */



public interface TabPresentation extends SingleSelectionModel {



	public void add(PresentationObject obj, int index);

	public void empty(int index);

//	public void empty(PresentationObject obj);

	public void setWidth(String width);

	public void SetHeight(String height);

        public Vector getAddedTabs();

        public void setAddedTabs(Vector tabs);

        public void setColor(IWColor color);

        public Link getTabLink(PresentationObject obj);

        public IWColor getColor();

        public void setForm(Form form);



}   //  Interface TabPresentation
