/*
 * $Id: TreeViewer.java,v 1.5 2001/10/18 22:43:01 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.ui;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.idega.presentation.text.Link;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Image;
import com.idega.core.ICTreeNode;
import com.idega.idegaweb.IWBundle;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class TreeViewer extends PresentationObjectContainer {
  private static final String NODE_PARAMETER = "iw_tv_node_id";
  private static final String NODE_ACTION_OPEN_PARAMETER = "iw_tv_open";
  private static final String NODE_ACTION_CLOSE_PARAMETER = "iw_tv_close";
  private static final String NODE_ACTION_NODE_SELECTED = "iw_tv_node";

  private static final String TREEVIEW_PREFIX = "treeviewer_";
  private static final String TREEVIEW_MINUS = "minus_";
  private static final String TREEVIEW_PLUS = "plus_";
  private static final String TREEVIEW_LINE = "line_";
  private static final String TREEVIEW_LINE_SIMPLE = "line";

  private static final String GIF_SUFFIX = ".gif";

  private static final String TREEVIEWER_NODE_LEAF = "node_leaf";
  private static final String TREEVIEWER_NODE_OPEN = "node_open";
  private static final String TREEVIEWER_NODE_CLOSED = "node_closed";

  public static final String ONCLICK_FUNCTION_NAME = "treenodeselect";
  public static final String ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME = "iw_node_id";
  public static final String ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME = "iw_node_name";

  //private ICTreeNode _startNode;
  private List _startNodes;
  private Table _mainTable;
  private int _nestLevelAtOpen = 0;
  private Link _linkPrototype;
  private String _target;
  private int _tableColumns = 10;
  private int _tableRows = 1;
  private String _nodeActionParameter = NODE_ACTION_NODE_SELECTED;
  private boolean _usesOnClick = false;
  private Link _linesLink;
  private String _linkStyle;

  private TreeViewer(ICTreeNode rootNode){
    this.addRootNode(rootNode);
  }

  public static TreeViewer getTreeViewerInstance(ICTreeNode node,IWContext iwc){
      TreeViewer viewer = new TreeViewer(node);
      return viewer;
  }

  //private ICTreeNode getStartNode(){
  //  return _startNode;
  //}

  /**
   * Sets the Root nodes , takes in a List of ICTreeNode Objects
   */
  public void setRootNodes(List nodes){
    this._startNodes=nodes;
  }

  private boolean hasOneRoot(){
    List nodes = this.getRootNodeList();
    if(nodes.size()==1){
     return true;
    }
    return false;
  }

  public void addRootNode(ICTreeNode node){
    getRootNodeList().add(node);
  }

  private List getRootNodeList(){
    if(this._startNodes==null){
      _startNodes=new Vector();
    }
    return _startNodes;
  }

  private boolean isRootNode(ICTreeNode node){
    return getRootNodeList().contains(node);
  }

  private Table getTable(){
      if(_mainTable==null){
        _mainTable = new Table(_tableColumns,_tableRows);
        _mainTable.setResizable(true);
        _mainTable.setCellpadding(0);
        _mainTable.setCellspacing(0);
      }
      return _mainTable;
  }


  public void main(IWContext iwc){
      IWBundle bundle = getBundle(iwc);
      Table tab = getTable();
      add(tab);
      List startNodes = this.getRootNodeList();
      Iterator iter = startNodes.iterator();
      while (iter.hasNext()) {
        if(this.hasOneRoot())this._nestLevelAtOpen=1;

        ICTreeNode startNode = (ICTreeNode)iter.next();
        //ICTreeNode startNode = getStartNode();
        int[] parentArray = {};
        addNode(iwc,tab,1,1,startNode,parentArray,'F',0,bundle);
      }


  }
  /**@todo : find a better and faster way to store the Parent id's and use them to
   * keep the tree open
   *
   */
  private ICTreeNode getParent(ICTreeNode node){
   return node.getParentNode();
  }

  private PresentationObject getNodeText(ICTreeNode node,boolean nodeIsOpen,IWBundle bundle){
    Link proto = (Link)getLinkPrototype().clone();
    String nodeName = node.getNodeName();
    proto.setText(nodeName);

    ICTreeNode parentNode = getParent(node);//change this method to optimize this procedure

    if(_usesOnClick){
      proto.setURL("#");
      proto.setOnClick(ONCLICK_FUNCTION_NAME+"('"+nodeName+"','"+node.getNodeID()+"')");
    }
    else{
      proto.addParameter(_nodeActionParameter,node.getNodeID());

      /**@todo : find a better and faster way to store the Parent id's and use them here to
       * keep the tree open
       *
       */
      boolean useNodeActionOpenParameter = true;
      while( parentNode!=null ){
        if(useNodeActionOpenParameter){
          proto.addParameter(NODE_ACTION_OPEN_PARAMETER,parentNode.getNodeID());
        }
        else proto.addParameter(NODE_PARAMETER,parentNode.getNodeID());

        useNodeActionOpenParameter = false;
        parentNode=getParent(parentNode);//change this method to optimize this procedure
      }
    }
    return proto;
  }

  private PresentationObject getNodeIcon(ICTreeNode node,boolean nodeIsOpen,IWBundle bundle){
    Link proto = (Link)getLinkPrototype().clone();
    Image image = null;

    ICTreeNode parentNode = getParent(node);//change this method to optimize this procedure


    if(this.isLeafNode(node)){
      if( isRootNode(node) ) image = bundle.getImage(TREEVIEW_PREFIX+TREEVIEWER_NODE_LEAF+GIF_SUFFIX);
      else image = bundle.getImage(TREEVIEW_PREFIX+TREEVIEWER_NODE_CLOSED+GIF_SUFFIX);
    }
    else if(nodeIsOpen){
      image = bundle.getImage(TREEVIEW_PREFIX+TREEVIEWER_NODE_OPEN+GIF_SUFFIX);
    }
    else{
      image = bundle.getImage(TREEVIEW_PREFIX+TREEVIEWER_NODE_CLOSED+GIF_SUFFIX);
    }
    proto.setPresentationObject(image);
    if(_usesOnClick){
      String nodeName = node.getNodeName();
      proto.setURL("#");
      proto.setOnClick(ONCLICK_FUNCTION_NAME+"('"+nodeName+"','"+node.getNodeID()+"')");
    }
    else{
      proto.addParameter(_nodeActionParameter,node.getNodeID());
      /**@todo : find a better and faster way to store the Parent id's and use them here to
       * keep the tree open
       *
       */
      boolean useNodeActionOpenParameter = true;
      while( parentNode!=null ){
        if(useNodeActionOpenParameter){
          proto.addParameter(NODE_ACTION_OPEN_PARAMETER,parentNode.getNodeID());
        }
        else proto.addParameter(NODE_PARAMETER,parentNode.getNodeID());

        useNodeActionOpenParameter = false;
        parentNode=getParent(parentNode);//change this method to optimize this procedure
      }

    }
    return proto;
  }


  private PresentationObject getTreeLines(ICTreeNode node,boolean nodeOpen, int[] parentarray,char typeOfNode,IWBundle bundle){
    Image image=null;
    if(isLeafNode(node)){
        if( isRootNode(node) )
        {
          if(this.hasOneRoot()){
            image = null;
          }
          else{
            image = bundle.getImage(TREEVIEW_PREFIX+TREEVIEW_LINE+typeOfNode+GIF_SUFFIX);
          }
        }
        else{
          //if(this.hasMoreThanOneRoot()){
            image = bundle.getImage("transparentcell.gif");//if it is the top node
            image.setWidth(17);
          //}
        }
        return image;
    }
    else{
      if( isRootNode(node) ){
          if(hasOneRoot()){
            return null;
          }
      }

      if(nodeOpen){
        image = bundle.getImage(TREEVIEW_PREFIX+TREEVIEW_MINUS+typeOfNode+GIF_SUFFIX);
        Link link = getLinesLinkCloned();
        link.setPresentationObject(image);
        link.setToMaintainGlobalParameters();
        link.addParameter(NODE_ACTION_CLOSE_PARAMETER,Integer.toString(node.getNodeID()));
        addParametersToLink(link,parentarray);
        return link;
      }
      else{
        image = bundle.getImage(TREEVIEW_PREFIX+TREEVIEW_PLUS+typeOfNode+GIF_SUFFIX);
        Link link = getLinesLinkCloned();
        link.setPresentationObject(image);
        link.setToMaintainGlobalParameters();
        link.addParameter(NODE_ACTION_OPEN_PARAMETER,Integer.toString(node.getNodeID()));
        addParametersToLink(link,parentarray);
        return link;
      }
    }
  }

  private PresentationObject getSimpleTreeLine(IWBundle bundle){
    return bundle.getImage(TREEVIEW_PREFIX+TREEVIEW_LINE_SIMPLE+GIF_SUFFIX);
  }

  private void addParametersToLink(Link link,int[] parentArray){
    for (int i = 0; i < parentArray.length; i++) {
        link.addParameter(NODE_PARAMETER,Integer.toString(parentArray[i]));
    }
  }

  /**
   *
   */
  private int addNode(IWContext iwc, Table table, int xpos, int ypos, ICTreeNode node, int parentarray[], char typeOfNode, int recurseLevel, IWBundle bundle) {
    boolean nodeIsOpen = isNodeOpen(node,recurseLevel,iwc);
    int newparentarray[] = new int[parentarray.length+1];
    System.arraycopy(parentarray,0,newparentarray,0,parentarray.length);
    newparentarray[parentarray.length] = node.getNodeID();

    _tableRows = ypos;

    if((xpos + 2) > _tableColumns){
      _tableColumns = xpos + 2;
      getTable().resize(_tableColumns,_tableRows);
    }

    table.add(getNodeIcon(node,nodeIsOpen,bundle),xpos+1,ypos);
    if (table.isEmpty(1,ypos)) {
      table.add("",1,ypos);
    }

    table.setAlignment(xpos+1,ypos,"left");
    table.add(getNodeText(node,nodeIsOpen,bundle),xpos+2,ypos);
    table.setAlignment(xpos+2,ypos,"left");
    table.mergeCells(xpos+2,_tableRows,_tableColumns,_tableRows);

    PresentationObject line = getTreeLines(node,nodeIsOpen,parentarray,typeOfNode,bundle);
    if(line!=null){
      table.add(line,xpos,ypos);
    }
    if (recurseLevel > 0) {
      int dummy = ypos - 1;
      while (table.containerAt(xpos,dummy).isEmpty()) {
        table.add(getSimpleTreeLine(bundle),xpos,dummy);
        dummy--;
      }
    }

    if (nodeIsOpen) {
      Iterator iter = node.getChildren();
      ICTreeNode oldNode = null;
      int oldypos = 1;
      if (iter != null) {
        while (iter.hasNext()) {
          ICTreeNode newNode = (ICTreeNode)iter.next();
          if (oldNode != null) {
            ypos = addNode(iwc,table,xpos+1,oldypos,oldNode,newparentarray,'M',recurseLevel+1,bundle);
          }
          oldypos = ypos + 1;
          oldNode = newNode;
          ypos++;
          _tableRows = ypos;
        }

        ypos = addNode(iwc,table,xpos+1,oldypos,oldNode,newparentarray,'L',recurseLevel+1,bundle);
      }
    }

    return(ypos);
  }

  public void setTarget(String target){
    _target=target;
  }

  public String getTarget(){
    return _target;
  }

  public void setLinkProtototype(Link link){
    _linkPrototype=link;
  }

  private Link getLinkPrototype(){
    if(_linkPrototype==null){
      _linkPrototype=new Link();
      String target=getTarget();
      if(target!=null){
        _linkPrototype.setTarget(target);
      }
    }

    if ( _linkStyle != null )
      _linkPrototype.setFontStyle(_linkStyle);

    return _linkPrototype;
  }

  private boolean isLeafNode(ICTreeNode node){
    return node.isLeaf();
  }

  private boolean isNodeOpen(ICTreeNode node,int nestLevel,IWContext iwc){
    String actionClose=iwc.getParameter(NODE_ACTION_CLOSE_PARAMETER);
    String actionOpen=iwc.getParameter(NODE_ACTION_OPEN_PARAMETER);

    boolean doingOpen = actionOpen!=null;
    boolean doingClose = actionClose!=null;

    boolean check = (doingOpen)||(doingClose);

    if(check){
    String[] parvalues = iwc.getParameterValues(NODE_PARAMETER);
      if(parvalues!=null){
        for (int i = 0; i < parvalues.length; i++) {
          try{
            if(node.getNodeID()==Integer.parseInt(parvalues[i])){
              if(doingClose){
                if(Integer.parseInt(actionClose)==node.getNodeID()){
                  return false;
                }
                else{
                  return true;
                }
              }
              else{
                return true;
              }
            }
            else if(doingClose){
              if(Integer.parseInt(actionClose)==node.getNodeID()){
                return false;
              }
            }
            else if(doingOpen){
              if(Integer.parseInt(actionOpen)==node.getNodeID()){
                return true;
              }
            }
          }
          catch(NumberFormatException e){
          }
        }
        return false;
      }
      else if(doingClose){
        if(Integer.parseInt(actionClose)==node.getNodeID()){
          return false;
        }
      }
      else if(doingOpen){
        if(Integer.parseInt(actionOpen)==node.getNodeID()){
          return true;
        }
      }
      return false;
    }
    else{
      if(nestLevel < getNestLevelAtOpen()){
        return true;
      }
      return false;
    }

  }

  public void setNestLevelAtOpen(int nodesDown){
    _nestLevelAtOpen = nodesDown;
  }

  public int getNestLevelAtOpen(){
    return _nestLevelAtOpen;
  }

  public void setNodeActionParameter(String parameterName){
    _nodeActionParameter=parameterName;
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

  public void setToMaintainParameter(String parameterName,IWContext iwc){
    Link link = getLinesLink();
    link.maintainParameter(parameterName,iwc);
  }

  public void setTreeStyle(String style) {
    _linkStyle = style;
  }

  private Link getLinesLink(){
    if(_linesLink==null){
      _linesLink=new Link();
    }
    return _linesLink;
  }

  private Link getLinesLinkCloned(){
    return (Link)getLinesLink().clone();
  }
}
