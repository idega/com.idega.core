package com.idega.presentation.ui;

import com.idega.presentation.event.ResetPresentationEvent;
import java.util.*;
import com.idega.presentation.event.TreeViewerEvent;
import com.idega.idegaweb.IWException;
import com.idega.event.*;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class TreeViewerPS extends IWPresentationStateImpl implements IWActionListener {

  protected List _openNodes = new Vector();
  protected boolean _initLevel = true;
  private String lastOpenedOrClosedNode = null;

  public TreeViewerPS() {
  }

  public void reset() {
    _openNodes.clear();
    _initLevel = true;
    lastOpenedOrClosedNode = null;
  }

  public List getOpenNodeList(){
    return _openNodes;
  }

  public void setOpenNodeList(List list){
    if(list != null){
      _openNodes = list;
    } else {
      throw new NullPointerException();
    }
  }

  public boolean setToInitOpenLevel(){
    return _initLevel;
  }

//  public Iterator getFirstlevelNodes(){
//    /**@todo: Implement this com.idega.event.IWPresentationState method*/
//    throw new java.lang.UnsupportedOperationException("Method getFirstlevelNodes() not yet implemented.");
//
//  }
//
//  public void setFirstlevelNodes(List nodes){
//
//  }


  public Object clone() {
    TreeViewerPS obj = null;
    try {
      obj = (TreeViewerPS)super.clone();
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

  public String getLastOpenedOrClosedNode() {
  	return lastOpenedOrClosedNode;
  }

  public void resetLastOpenedOrClosedNode() {
  	lastOpenedOrClosedNode = null;
  }
  
  public void actionPerformed(IWPresentationEvent e)throws IWException{

    if(e instanceof ResetPresentationEvent){
      this.reset();
      this.fireStateChanged();
    }

    _initLevel = false;
    if(e instanceof TreeViewerEvent){
      String open = ((TreeViewerEvent)e).getOpenNodeAction();
      boolean changed = false;
      if(open != null && !_openNodes.contains(open)){
        _openNodes.add(open);
        changed = true;
      }

      String close = ((TreeViewerEvent)e).getCloseNodeAction();
      if(close != null){
        _openNodes.remove(close);
        changed = true;
      }
      lastOpenedOrClosedNode = ((TreeViewerEvent)e).getOpenNodeAction();
      if (lastOpenedOrClosedNode == null) { 
      	lastOpenedOrClosedNode = ((TreeViewerEvent)e).getCloseNodeAction();
      }

      if(changed){
        this.fireStateChanged();
      }
//      System.out.println("TreeViewerPS: initLevel: " + _initLevel);
      Iterator iter = _openNodes.iterator();
      int counter = 1;
      while (iter.hasNext()) {
        iter.next();
//        System.out.println("TreeViewerPS: openItem"+counter+": "+item);
        counter++;
      }


    }



  }

}