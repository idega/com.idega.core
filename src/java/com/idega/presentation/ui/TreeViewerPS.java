package com.idega.presentation.ui;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.ejb.RemoveException;
import com.idega.event.IWActionListener;
import com.idega.event.IWPresentationEvent;
import com.idega.event.IWPresentationStateImpl;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWException;
import com.idega.presentation.IWContext;
import com.idega.presentation.event.ResetPresentationEvent;
import com.idega.presentation.event.TreeViewerEvent;
import com.idega.user.business.UserBusiness;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class TreeViewerPS extends IWPresentationStateImpl implements IWActionListener {

  protected List _openNodes = new Vector();
  protected boolean _initLevel = true;
  private String lastOpenedOrClosedNode = null;

  public TreeViewerPS() {
  }

  public void reset() {
    this._openNodes.clear();
    this._initLevel = true;
    this.lastOpenedOrClosedNode = null;
  }

  public List getOpenNodeList(){
    return this._openNodes;
  }

  public void setOpenNodeList(List list){
    if(list != null){
      this._openNodes = list;
    } else {
      throw new NullPointerException();
    }
  }

  public boolean setToInitOpenLevel(){
    return this._initLevel;
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
  	return this.lastOpenedOrClosedNode;
  }

  public void resetLastOpenedOrClosedNode() {
  	this.lastOpenedOrClosedNode = null;
  }
  
  public void actionPerformed(IWPresentationEvent e)throws IWException{

    if(e instanceof ResetPresentationEvent){
      this.reset();
      this.fireStateChanged();
    }

    this._initLevel = false;
    if(e instanceof TreeViewerEvent){
      String open = ((TreeViewerEvent)e).getOpenNodeAction();
      boolean changed = false;
      if(open != null && !this._openNodes.contains(open)){
        this._openNodes.add(open);
        changed = true;
      }

      String close = ((TreeViewerEvent)e).getCloseNodeAction();
      if(close != null){
        this._openNodes.remove(close);
        changed = true;
      }
      this.lastOpenedOrClosedNode = ((TreeViewerEvent)e).getOpenNodeAction();
      if (this.lastOpenedOrClosedNode == null) { 
      	this.lastOpenedOrClosedNode = ((TreeViewerEvent)e).getCloseNodeAction();
      }
      IWContext iwc = ((TreeViewerEvent)e).getIWContext();
      String refresh = ((TreeViewerEvent)e).getIWContext().getParameter("ic_ref_tn");
      if(refresh != null) {
      		try {
  					getUserBusiness(iwc).removeStoredTopGroupNodes(iwc.getCurrentUser());
  					changed = true;
  				}
  				catch (RemoteException e1) {
  					e1.printStackTrace();
  				}
  				catch (RemoveException e1) {
  					e1.printStackTrace();
  				}
      }

      if(changed){
        this.fireStateChanged();
      }
//      System.out.println("TreeViewerPS: initLevel: " + _initLevel);
      Iterator iter = this._openNodes.iterator();
      int counter = 1;
      while (iter.hasNext()) {
        iter.next();
//        System.out.println("TreeViewerPS: openItem"+counter+": "+item);
        counter++;
      }


    }



  }
  
	public UserBusiness getUserBusiness(IWApplicationContext iwc) {
		UserBusiness userBiz = null;
		try {
			userBiz = (UserBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, UserBusiness.class);
		}
		catch (java.rmi.RemoteException rme) {
			throw new RuntimeException(rme.getMessage());
		}
		return userBiz;
	}


}