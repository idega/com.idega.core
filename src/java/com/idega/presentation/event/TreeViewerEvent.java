package com.idega.presentation.event;

import com.idega.core.data.ICTreeNode;
import com.idega.event.IWPresentationEvent;
import com.idega.event.NoSuchEventException;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.Parameter;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class TreeViewerEvent extends IWPresentationEvent {

//  protected List _openNodes;

  protected String _toOpen = null;
  protected String _toClose = null;
  protected String _refreshTN = null;

  private static final String PRM_OPEN_TREENODES = "ic_opn_trnds";
  private static final String PRM_TREENODE_TO_CLOSE = "ic_cls_trnd";
  private static final String PRM_TREE_CHANGED = "ic_tw_ch";
  private static final String PRM_REFRESH_TN = "ic_ref_tn";

  private static Parameter prmTreeStateChanged;

  public TreeViewerEvent() {
  }

  public TreeViewerEvent(IWContext iwc) throws NoSuchEventException {
    super(iwc);
  }

  public void initializeVariables(){
//    _openNodes = new Vector();
    prmTreeStateChanged = new Parameter(PRM_TREE_CHANGED,"t");
  }

//  public List getOpenNodeList(){
//    return _openNodes;
//  }

  public void setToOpenNode(ICTreeNode node){
    this.addParameter(PRM_OPEN_TREENODES,node.getNodeID());
    if(!this.containsParameter(prmTreeStateChanged)){
      this.addParameter(prmTreeStateChanged);
    }
  }

  public void setToCloseNode(ICTreeNode node){
    this.addParameter(PRM_TREENODE_TO_CLOSE,node.getNodeID());
    if(!this.containsParameter(prmTreeStateChanged)){
      this.addParameter(prmTreeStateChanged);
    }
  }
  
  public void setToTreeStateChanged(Link l) {
  		l.addParameter(PRM_REFRESH_TN,"refresh");
  		if(!this.containsParameter(prmTreeStateChanged)) {
  			this.addParameter(prmTreeStateChanged);
  			l.addParameter(prmTreeStateChanged);
  		}
  }

  public String getOpenNodeAction(){
    return _toOpen;
  }

  public String getCloseNodeAction(){
    return _toClose;
  }
  
  public String getRefreshTNAction() {
  		return _refreshTN;
  }

//  public void setOpenNodes(List openNodes){
//    _openNodes = openNodes;
//    maintainOpenAndClosedNodes();
//  }



  public boolean initializeEvent(IWContext iwc){
//    _openNodes.clear();
//
//    String[] open = iwc.getParameterValues(PRM_OPEN_TREENODES);
//    if(open != null){
//      for (int i = 0; i < open.length; i++) {
//	_openNodes.add(open[i]);
//      }
//    } else if(iwc.getParameter(PRM_TREE_CHANGED) == null){ // set init Open level
//      return false;
//    }
//
//    String close = iwc.getParameter(PRM_TREENODE_TO_CLOSE);
//    if(close != null){
//      _openNodes.remove(close);
//    }
//    maintainOpenAndClosedNodes();


    if(iwc.getParameter(PRM_TREE_CHANGED) != null || iwc.getParameter(PRM_REFRESH_TN) != null){
      _toOpen = iwc.getParameter(PRM_OPEN_TREENODES);
      _toClose = iwc.getParameter(PRM_TREENODE_TO_CLOSE);
    } else {
      return false;
    }


    return true;
  }

//  private void maintainOpenAndClosedNodes(){
//    int size = _openNodes.size();
//    for (int i = 0; i < size; i++) {
//      this.addParameter(PRM_OPEN_TREENODES,(String)_openNodes.get(i));
//    }
//  }


  public Object clone(){
    TreeViewerEvent model = null;

    model = (TreeViewerEvent)super.clone();
//    if(this._openNodes != null){
//      model._openNodes = (List)((Vector)this._openNodes).clone();
//      ListIterator iter = this._openNodes.listIterator();
//      while (iter.hasNext()) {
//        int index = iter.nextIndex();
//        Object item = iter.next();
//        model._openNodes.set(index,item);
//      }
//    } else {
//      model._openNodes = new Vector();
//    }


    return model;
  }
}