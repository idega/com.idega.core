/*
 * $Id: AbstractMenuBlock.java,v 1.5 2006/04/09 12:13:13 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;


import java.util.List;
import java.util.Vector;

import com.idega.idegaweb.block.presentation.Builderaware;
import com.idega.presentation.ui.Parameter;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public abstract class AbstractMenuBlock extends Block implements MenuBlock{

  private static String prmClass = "mbl_clss";
  private List objects = null;
  private PresentationObject links = null;
  private BlockMenu menu;

   public void _main(IWContext iwc) throws Exception{

    this.menu = new BlockMenu();
    this.menu.setClassParameterName(getMenuClassParameterName());
    addStandardObjects();
    this.menu.addAll(this.objects);
    this.menu.setDefaultBlock(getDefaultBlockClass());
    this.links = this.menu.getLinkTable(iwc,"");
    //menu.setShowLinks(showLinks);
    add(this.menu);
    super._main(iwc);
  }

  public abstract void addStandardObjects();
  public abstract Class getDefaultBlockClass();

  public void addBlockObject(Builderaware obj){
    if(this.objects == null) {
			this.objects = new Vector();
		}
    this.objects.add(obj);
  }

  public void addBlockObjectAll(java.util.Collection coll){
    if(this.objects == null) {
			this.objects = new Vector();
		}
    this.objects.addAll(coll);
  }

  public String getMenuClassParameterName(){
    return prmClass;
  }

  public void setShowLinks(boolean show){
  }

  public PresentationObject getLinks(){
    return this.links;
  }

  public synchronized Object clone() {
    AbstractMenuBlock obj = null;
    try {
      obj = (AbstractMenuBlock)super.clone();
      obj.objects  = this.objects;
      obj.menu = this.menu;

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

  public void addParameterToMaintain(Parameter prm){
    this.menu.addParameterToMaintain(prm);
  }

  public Parameter getMenuLinkParameter(Class classToOpen){
    return new Parameter(getMenuClassParameterName(),classToOpen.getName());
  }

}
