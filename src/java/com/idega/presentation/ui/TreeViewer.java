package com.idega.presentation.ui;

import com.idega.presentation.text.Link;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.Block;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.PageTreeNode;
import com.idega.core.ICTreeNode;
import com.idega.presentation.Script;
import com.idega.presentation.Table;
import com.idega.presentation.Image;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Layer;

import java.util.Vector;
import java.util.Iterator;
import java.util.List;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class TreeViewer extends AbstractTreeViewer {

  private static final String TREEVIEW_PREFIX = "treeviewer/ui/";

  Image folderAndFileIcons[] = null;
  String folderAndFileIconNames[]={"treeviewer_node_closed.gif","treeviewer_node_open.gif","treeviewer_node_leaf.gif"};

  private static final int FOLDERANDFILE_ICONINDEX_FOLDER_CLOSED = 0;
  private static final int FOLDERANDFILE_ICONINDEX_FOLDER_OPEN = 1;
  private static final int FOLDERANDFILE_ICONINDEX_FILE = 2;

  public static final String PRM_OPEN_TREENODES = "ic_opn_trnds";
  public static final String PRM_TREENODE_TO_CLOSE = "ic_cls_trnd";

  String nodeNameTarget = null;
  String nodeActionPrm = null;
  Link _linkPrototype = null;
  String _linkStyle = null;
  boolean _usesOnClick = false;
  private boolean _nowrap = true;
  private Layer _nowrapLayer = null;

  public static final String ONCLICK_FUNCTION_NAME = "treenodeselect";
  public static final String ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME = "iw_node_id";
  public static final String ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME = "iw_node_name";

  public TreeViewer() {
    super();
    folderAndFileIcons = new Image[3];
    this.setColumns(2);
    this.setTreeColumnWidth(1,"16");
  }


  public static TreeViewer getTreeViewerInstance(ICTreeNode node,IWContext iwc){
    TreeViewer viewer = new TreeViewer();
    viewer.setRootNode(node);
    return viewer;
  }

/*
  public void main(IWContext iwc) throws Exception {
    initTestTree();
    super.main(iwc);
  }

  //tmp
  int tmp_ibPageID = 1;
  public void setTmpIbPageID(int value){
    tmp_ibPageID = value;
  }
  //tmp
  public void initTestTree(){
    try {
      this.setRootNode(new IBPage(tmp_ibPageID));
    }
    catch (Exception ex) {
      DefaultTreeNode rootNode = new DefaultTreeNode("root", 1);
      DefaultTreeNode node2 = new DefaultTreeNode("node2", 2);
      DefaultTreeNode node3 = new DefaultTreeNode("node3", 3);
      DefaultTreeNode node4 = new DefaultTreeNode("node4", 4);
      DefaultTreeNode node5 = new DefaultTreeNode("node5", 5);
      DefaultTreeNode node6 = new DefaultTreeNode("node6", 6);
      DefaultTreeNode node7 = new DefaultTreeNode("node7", 7);
      DefaultTreeNode node8 = new DefaultTreeNode("node8", 8);
      DefaultTreeNode node9 = new DefaultTreeNode("node9", 9);
      DefaultTreeNode node10 = new DefaultTreeNode("node10", 10);
      DefaultTreeNode node11 = new DefaultTreeNode("node11", 12);
      DefaultTreeNode node12 = new DefaultTreeNode("node12", 12);
      DefaultTreeNode node13 = new DefaultTreeNode("node13", 13);
      DefaultTreeNode node14 = new DefaultTreeNode("node14", 14);
      DefaultTreeNode node15 = new DefaultTreeNode("node15", 15);
      DefaultTreeNode node16 = new DefaultTreeNode("node16", 16);
      DefaultTreeNode node17 = new DefaultTreeNode("node17", 17);

      ((DefaultTreeNode)rootNode).addTreeNode(node2);
      ((DefaultTreeNode)rootNode).addTreeNode(node3);
      ((DefaultTreeNode)rootNode).addTreeNode(node4);
      node2.addTreeNode(node5);
      node5.addTreeNode(node6);
      node3.addTreeNode(node7);
      node3.addTreeNode(node8);
      node7.addTreeNode(node9);
      node7.addTreeNode(node10);
      node10.addTreeNode(node11);
      node4.addTreeNode(node12);
      node12.addTreeNode(node13);
      node13.addTreeNode(node14);
      node14.addTreeNode(node15);
      node14.addTreeNode(node16);
      node16.addTreeNode(node17);


      this.setRootNode(rootNode);
    }
  }
*/

  protected void updateIconDimensions(){
    super.updateIconDimensions();
    for (int i = 0; i < folderAndFileIcons.length; i++) {
      Image tmp = folderAndFileIcons[i];
      if(tmp != null){
        //tmp.setWidth(iconWidth);
        tmp.setHeight(iconHeight);
        //tmp.setAlignment("top");
        folderAndFileIcons[i] = tmp;
      }
    }
  }

  public void initIcons(IWContext iwc){
    super.initIcons(iwc);

    IWBundle bundle = getBundle(iwc);
    for (int i = 0; i < folderAndFileIcons.length; i++) {
      if(folderAndFileIcons[i] == null){
        folderAndFileIcons[i] = bundle.getImage(TREEVIEW_PREFIX+getUI()+folderAndFileIconNames[i]);
      }
    }

    updateIconDimensions();
  }




/*
  public void addParameters(Link l, ICTreeNode node, IWContext iwc){

  }
*/

	private void addScript() {
		Script script = getParentPage().getAssociatedScript();
		script.addFunction("save", "function save(URL,target) {   args['href']=URL; args['target']=target; window.returnValue = args; window.close(); }");
		getParentPage().setAssociatedScript(script);
	}

  public PresentationObject getObjectToAddToColumn(int colIndex, ICTreeNode node, IWContext iwc, boolean nodeIsOpen, boolean nodeHasChild, boolean isRootNode){
    //System.out.println("adding into column "+ colIndex + " for node " + node);
    boolean fromEditor = false;
    if ( iwc.isParameterSet("from_editor") ) {
    	fromEditor = true;
    	addScript();
    }

    switch (colIndex) {
      case 1:
        if(!node.isLeaf()){
          if(nodeIsOpen){
            if(isRootNode && !showRootNodeTreeIcons()){
              Link l = new Link();
              l.setImage(folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FOLDER_OPEN]);
              if(!nodeIsOpen){ //   || allowRootNodeToClose ){
                this.setLinkToOpenOrCloseNode(l,node,nodeIsOpen);
              }
              return l;
            } else {
              return folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FOLDER_OPEN];
            }
          } else {
            if(isRootNode && !showRootNodeTreeIcons()){
              Link l = new Link();
              l.setImage(folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FOLDER_CLOSED]);
              this.setLinkToOpenOrCloseNode(l,node,nodeIsOpen);
              return l;
            } else {
              return folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FOLDER_CLOSED];
            }
          }
        } else {
          if(isRootNode && !showRootNodeTreeIcons()){
              Link l = new Link();
              l.setImage(folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FILE]);
              this.setLinkToOpenOrCloseNode(l,node,nodeIsOpen);
              return l;
            } else {
              return folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FILE];
            }
        }
      case 2:
      	String nodeName = null;
      	if (node instanceof PageTreeNode)
      		nodeName = ((PageTreeNode)node).getLocalizedNodeName(iwc);
      	else
      		nodeName = node.getNodeName();
        Link l = this.getLinkPrototypeClone(nodeName);
        if(_usesOnClick){
          l.setURL("#");
          if ( fromEditor )
          	l.setOnClick("save('http://"+iwc.getServerName()+BuilderLogic.getInstance().getIBPageURL(iwc, node.getNodeID())+"','_self')");
          else
          	l.setOnClick(ONCLICK_FUNCTION_NAME+"('"+nodeName+"','"+node.getNodeID()+"')");
        } else if(nodeActionPrm != null){
          l.addParameter(nodeActionPrm,node.getNodeID());
        }
        this.setLinkToMaintainOpenAndClosedNodes(l);
        if(_nowrap){
          return getNoWrapLayerClone(l);
        } else {
          return l;
        }
    }
    return null;
  }

  public void setWrap(){
    _nowrap = false;
  }

  public void setWrap(boolean value){
    _nowrap = value;
  }

  public void setNodeActionParameter(String prm){
    nodeActionPrm = prm;
  }

  public void setTarget(String target){
    nodeNameTarget = target;
  }

  public void setTreeStyle(String style) {
    _linkStyle = style;
  }

  public void setLinkPrototype(Link link){
    _linkPrototype=link;
  }

  private Link getLinkPrototype(){
    if(_linkPrototype==null){
      _linkPrototype=new Link();
    }

    if(nodeNameTarget != null){
      _linkPrototype.setTarget(nodeNameTarget);
    }

    if(_linkStyle != null){
      _linkPrototype.setFontStyle(_linkStyle);
    }

/*
    if ( _linkStyle != null )
      _linkPrototype.setFontStyle(_linkStyle);
*/
    return _linkPrototype;
  }

  public Layer getNoWrapLayer(){
    if(_nowrapLayer == null){
      _nowrapLayer = new Layer();
      _nowrapLayer.setNoWrap();
    }
    return _nowrapLayer;
  }

  private Link getLinkPrototypeClone(){
    return (Link)getLinkPrototype().clone();
  }

  private Link getLinkPrototypeClone(String text){
    Link l = (Link)getLinkPrototype().clone();
    l.setText(text);
    return l;
  }

  private Layer getNoWrapLayerClone(){
    Layer l = (Layer)getNoWrapLayer().clone();
    return l;
  }

  private Layer getNoWrapLayerClone(PresentationObject obj){
    Layer l = getNoWrapLayerClone();
    l.add(obj);
    return l;
  }

  private Link getLinkPrototypeClone(Image image){
    Link l = (Link)getLinkPrototype().clone();
    l.setImage(image);
    return l;
  }


  public void setToUseOnClick(){
    setToUseOnClick(ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME,ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME);
  }

  public void setToUseOnClick(String NodeNameParameterName,String NodeIDParameterName){
    _usesOnClick=true;
    getAssociatedScript().addFunction(ONCLICK_FUNCTION_NAME,"function "+ONCLICK_FUNCTION_NAME+"("+NodeNameParameterName+","+NodeIDParameterName+"){ }");

  }

  public void setOnClick(String action){
     this.getAssociatedScript().addToFunction(ONCLICK_FUNCTION_NAME,action);
  }


}
