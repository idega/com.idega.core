package com.idega.presentation;

import java.util.Collection;
import java.util.Vector;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.util.Edit;
import com.idega.repository.data.RefactorClassRegistry;

/**
 * Title:   idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author  <a href="mailto:aron@idega.is">aron@idega.is
 * @version 1.0
 */

public class BlockMenu extends Block {

  protected IWResourceBundle iwrb;
  protected IWBundle iwb;
  private Collection objects = null;
  private Collection paramtersToMainTain = null;
  private String prmObjectClass = "obj_clss";
  private Class defaultClass = null;
  private boolean showLinks = true;
  private String fontStyle = "color:#000000;font-size:8pt;font-family:Arial,Helvetica,sans-serif;font-weight:bold;" ;
  private String fontStyle2 = "color:#942829;font-size:9pt;font-family:Arial,Helvetica,sans-serif;font-weight:bold;" ;

  protected void control(IWContext iwc){
    iwrb = getResourceBundle(iwc);
    iwb = getBundle(iwc);
    Table T = new Table();
    T.setWidth("100%");
    T.setCellpadding(0);
    T.setCellspacing(10);

    String className = null;
    if(iwc.isParameterSet(prmObjectClass)){
      className = iwc.getParameter(prmObjectClass);
      iwc.setSessionAttribute(prmObjectClass,className);
    }
    else if(iwc.getSessionAttribute(prmObjectClass)!=null){
      className = (String) iwc.getSessionAttribute(prmObjectClass);
    }
    else if(defaultClass !=null){
      className = String.valueOf(defaultClass.hashCode());
    }

    /*
    if(paramtersToMainTain!=null)
      System.err.println("parameter collection size is "+paramtersToMainTain.size());
    else
      System.err.println("parameter collection size is null");
*/
    if(className !=null){
      try{
      if(showLinks)
        T.add(getLinkTable(iwc,className),1,1);
      Object obj =  RefactorClassRegistry.forName(IWMainApplication.decryptClassName(className)).newInstance();
      if(obj instanceof Block)
        T.add((Block)obj,1,2);
      }
      catch(Exception e){}
    }
    else{
      T.add(getBoxedLinks(iwc,className),1,1);
    }
    add(T);
  }

  public void setClassParameterName(String parametername){
    prmObjectClass = parametername;
  }

  public void setDefaultBlock(Class defaultClass){
    this.defaultClass = defaultClass;
  }

  public void setFontStyle(String style){

  }

  public PresentationObject getBoxedLinks(IWContext iwc,String currentClassName){
    Table frame = new Table(3,3);
    frame.setWidth("100%");
    frame.setHeight("100%");
    Table box = new Table();
      int row = 1;
      if(objects != null){
        java.util.Iterator I = objects.iterator();
        Block obj;
        while(I.hasNext()){
          obj = (Block) I.next();
          box.add(getLink(obj.getClass(),formatText(obj.getLocalizedName(iwc))),1,row++);
        }
      }
      box.setColor(Edit.colorLight);
    frame.add(box,2,2);
    return frame;
  }

  public PresentationObject getLinkTable(IWContext iwc,String currentClassName){
     Table frame = new Table();
     //frame.setWidth("100%");
      frame.setCellpadding(2);
      frame.setCellspacing(2);

        int row = 1;
        int col = 1;
        if(objects != null){
          frame.add(Edit.formatText("|"),col,row);
          col++;
          java.util.Iterator I = objects.iterator();
          Block obj;
          String decryptedClassName ;
          boolean highlight;
          while(I.hasNext()){
            obj = (Block) I.next();
            // shorted
            decryptedClassName = String.valueOf(obj.getClass().hashCode());
            highlight = decryptedClassName.equals(currentClassName);
            frame.add(getLink(obj.getClass(),formatText(obj.getLocalizedName(iwc)),highlight),col,row);
            col++;
            frame.add(formatText("|"),col,row);
            col++;
          }
        }
      return frame;
  }

  private Text formatText(String text){
    if ( text == null ) text = "";
    Text T =new Text(text);
    T.setFontStyle(fontStyle);

    return T;
  }


  public Link getLink(Class cl,Text name){
    Link L = new Link(name);
    L.addParameter(getObjectParameter(cl));
    if(paramtersToMainTain !=null){
      java.util.Iterator I = paramtersToMainTain.iterator();
      while(I.hasNext()){
        L.addParameter((Parameter) I.next());
      }
    }
    L.setFontSize(1);
    L.setFontColor(Edit.colorDark);
    L.setFontStyle(fontStyle);
    return L;
  }

  public Link getLink(Class cl,Text name,boolean highlighted){
    Link L = new Link(name);
    L.addParameter(getObjectParameter(cl));
    if(paramtersToMainTain !=null){
      java.util.Iterator I = paramtersToMainTain.iterator();
      while(I.hasNext()){
        L.addParameter((Parameter) I.next());
      }
    }
    L.setFontSize(1);
    L.setFontColor(Edit.colorDark);
    if(highlighted)
      L.setFontStyle(fontStyle2);
    else
      L.setFontStyle(fontStyle);
    return L;
  }

  public void addParameterToMaintain(Parameter prm){
    if(paramtersToMainTain==null)
      paramtersToMainTain = new Vector();
    paramtersToMainTain.add(prm);
  }

  public Parameter getObjectParameter(Class objectClass){
    return new Parameter(prmObjectClass,IWMainApplication.getEncryptedClassName(objectClass));
  }

  public void setShowLinks(boolean show){
    showLinks = show;
  }

  public void addBlock(Class obj){
    if(objects == null)
      objects = new java.util.Vector();
    try {
      objects.add(obj.newInstance());
    }
    catch (Exception ex) {

    }
  }

  public void addBlock(Block obj){
    if(objects == null)
      objects = new java.util.Vector();
    objects.add(obj);
  }

  public void addAll(Collection blocks){
    if(objects == null)
      objects = new java.util.Vector();
    objects.addAll(blocks);
  }

  public void main(IWContext iwc){
    control(iwc);
  }

  public synchronized Object clone() {
    BlockMenu obj = null;
    try {
      obj = (BlockMenu)super.clone();
      obj.objects  = objects;
      obj.paramtersToMainTain = paramtersToMainTain;
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }
}
