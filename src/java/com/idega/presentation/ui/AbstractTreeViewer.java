package com.idega.presentation.ui;

import com.idega.presentation.text.Link;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.Block;
import com.idega.core.ICTreeNode;
import com.idega.presentation.Table;
import com.idega.presentation.Image;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;

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

public abstract class AbstractTreeViewer extends PresentationObjectContainer {

  DefaultTreeNode defaultRoot = null;
  boolean showRootNode = false;
  boolean showRootNodeTreeIcons = false;
  int defaultOpenLevel = 1;

  private static final String TREEVIEW_PREFIX = "treeviewer/";

  Image icons[] = null;
  String iconNames[]={"treeviewer_trancparent.gif","treeviewer_line.gif",
                      "treeviewer_R_line.gif","treeviewer_R_minus.gif","treeviewer_R_plus.gif",
                      "treeviewer_L_line.gif","treeviewer_L_minus.gif","treeviewer_L_plus.gif",
                      "treeviewer_M_line.gif","treeviewer_M_minus.gif","treeviewer_M_plus.gif",
                      "treeviewer_F_line.gif","treeviewer_F_minus.gif","treeviewer_F_plus.gif"};
  private static final int ICONINDEX_TRANCPARENT = 0;
  private static final int ICONINDEX_LINE = 1;
  private static final int ICONINDEX_ROOT_LINE = 2;
  private static final int ICONINDEX_ROOT_MINUS = 3;
  private static final int ICONINDEX_ROOT_PLUS = 4;
  private static final int ICONINDEX_L_LINE = 5;
  private static final int ICONINDEX_L_MINUS = 6;
  private static final int ICONINDEX_L_PLUS = 7;
  private static final int ICONINDEX_M_LINE = 8;
  private static final int ICONINDEX_M_MINUS = 9;
  private static final int ICONINDEX_M_PLUS = 10;
  private static final int ICONINDEX_F_LINE = 11;
  private static final int ICONINDEX_F_MINUS = 12;
  private static final int ICONINDEX_F_PLUS = 13;


  String iconWidth = "16";
  String iconHeight = "16";

  Table nodeTable = null;
  Table treeTable = null;
  int treeTableIndex = 1;


  Image folderAndFileIcons[] = null;
  String folderAndFileIconNames[]={"treeviewer_folder_closed.gif","treeviewer_folder_open.gif","treeviewer_file.gif"};

  private static final int FOLDERANDFILE_ICONINDEX_FOLDER_CLOSED = 0;
  private static final int FOLDERANDFILE_ICONINDEX_FOLDER_OPEN = 1;
  private static final int FOLDERANDFILE_ICONINDEX_FILE = 2;

  Link openCloseLink = new Link();

  List openNodes = new Vector();
  public static final String PRM_OPEN_TREENODES = "ic_opn_trnds";
  public static final String PRM_TREENODE_TO_CLOSE = "ic_cls_trnd";
  public static final String PRM_TREE_CHANGED = "ic_tw_ch";


  public AbstractTreeViewer() {
    super();
    defaultRoot = new DefaultTreeNode("root",-1);
    icons = new Image[14];
    folderAndFileIcons = new Image[3];
    nodeTable = new Table(3,1);
    nodeTable.setCellpadding(0);
    nodeTable.setCellspacing(0);
    treeTable = new Table();
    treeTable.setCellpadding(0);
    treeTable.setCellspacing(0);
  }


  public Table getNodeTableClone(){
    return (Table)nodeTable.clone();
  }

  public void setIconDimensions(String width, String Height){
    iconWidth = width;
    iconHeight = Height;
    if(initializedInMain){
      updateIconDimansions();
    }
  }

  protected void updateIconDimansions(){
    for (int i = 0; i < icons.length; i++) {
      Image tmp = icons[i];
      if(tmp != null){
        tmp.setWidth(iconWidth);
        tmp.setHeight(iconHeight);
        tmp.setAlignment("top");
        icons[i] = tmp;
      }
    }
  }

  public void initIcons(IWContext iwc){
    IWBundle bundle = getBundle(iwc);
    for (int i = 0; i < icons.length; i++) {
      if(icons[i] == null){
        icons[i] = bundle.getImage(TREEVIEW_PREFIX+iconNames[i]);
      }
    }

    updateIconDimansions();
  }

  public void initializeInMain(IWContext iwc){
    initIcons(iwc);
  }

  public void drawTree(IWContext iwc){
    this.empty();
    this.add(treeTable);
    treeTable.empty();
    treeTableIndex = 1;
    drawTree(defaultRoot.getChildren(),null);
  }

  private synchronized void drawTree(Iterator nodes, Image[] collectedIcons){
    if(nodes != null){
      Iterator iter = nodes;
      for (int i = 0; iter.hasNext(); i++) {
        ICTreeNode item = (ICTreeNode)iter.next();
        boolean hasChild = (item.getChildCount() > 0);
        boolean isOpen = false;
        if(hasChild){
          isOpen = openNodes.contains(Integer.toString(item.getNodeID()));
        }
        boolean isRoot = (defaultRoot.getIndex(item) >= 0);
        Table nodeTable = this.getNodeTableClone();

        for (int k = 1; k < nodeTable.getColumns(); k++) {
          PresentationObject obj = this.getObjectToAddToColumn(k,item,null,isOpen,hasChild,isRoot);
          if(obj != null){
            nodeTable.add(obj,k+1,1);
          }
        }

        if(collectedIcons != null){
          for (int j = 0; j < collectedIcons.length; j++) {
            nodeTable.add(collectedIcons[j],1,1);
          }
        }
        Image[] newCollectedIcons = null;
        if(isRoot){
          if(showRootNodeTreeIcons()){
            if(i == 0 && !iter.hasNext()){
              if(hasChild){
                if(isOpen){
                  Link l = getOpenCloseLinkClone( icons[ICONINDEX_ROOT_MINUS] );
                  setLinkToOpenOrCloseNode(l,item,isOpen);
                  nodeTable.add(l,1,1);
                  newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
                } else {
                  Link l = getOpenCloseLinkClone( icons[ICONINDEX_ROOT_PLUS] );
                  setLinkToOpenOrCloseNode(l,item,isOpen);
                  nodeTable.add(l,1,1);
                }
              } else {
                nodeTable.add(icons[ICONINDEX_ROOT_LINE],1,1);
                //newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
              }
            } else {
              if(i == 0){
                if(hasChild){
                  if(isOpen){
                    Link l = getOpenCloseLinkClone( icons[ICONINDEX_F_MINUS] );
                    setLinkToOpenOrCloseNode(l,item,isOpen);
                    nodeTable.add(l,1,1);
                    newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_LINE]);
                  } else {
                    Link l = getOpenCloseLinkClone( icons[ICONINDEX_F_PLUS] );
                    setLinkToOpenOrCloseNode(l,item,isOpen);
                    nodeTable.add(l,1,1);
                  }
                } else {
                  nodeTable.add(icons[ICONINDEX_F_LINE],1,1);
                  //newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
                }
              } else if(hasChild){
                if(!item.isLeaf()){
                  if(isOpen){
                    Link l = getOpenCloseLinkClone( icons[ICONINDEX_L_MINUS] );
                    setLinkToOpenOrCloseNode(l,item,isOpen);
                    nodeTable.add(l,1,1);
                    newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
                  } else{
                    Link l = getOpenCloseLinkClone( icons[ICONINDEX_L_PLUS] );
                    setLinkToOpenOrCloseNode(l,item,isOpen);
                    nodeTable.add(l,1,1);
                  }
                } else {
                  nodeTable.add(icons[ICONINDEX_L_LINE],1,1);
                  //newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
                }
              } else {
                if(hasChild){
                  if(isOpen){
                    Link l = getOpenCloseLinkClone( icons[ICONINDEX_M_MINUS] );
                    setLinkToOpenOrCloseNode(l,item,isOpen);
                    nodeTable.add(l,1,1);
                    newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_LINE]);
                  } else {
                    Link l = getOpenCloseLinkClone( icons[ICONINDEX_M_PLUS] );
                    setLinkToOpenOrCloseNode(l,item,isOpen);
                    nodeTable.add(l,1,1);
                  }
                } else {
                  nodeTable.add(icons[ICONINDEX_M_LINE],1,1);
                  //newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
                }
              }
            }
          }
        }else{
          if(!iter.hasNext()){
            if(hasChild){
              if(isOpen){
                Link l = getOpenCloseLinkClone( icons[ICONINDEX_L_MINUS] );
                setLinkToOpenOrCloseNode(l,item,isOpen);
                nodeTable.add(l,1,1);
                newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
              } else {
                Link l = getOpenCloseLinkClone( icons[ICONINDEX_L_PLUS] );
                setLinkToOpenOrCloseNode(l,item,isOpen);
                nodeTable.add(l,1,1);
              }
            } else {
              nodeTable.add(icons[ICONINDEX_L_LINE],1,1);
              //newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
            }
          } else{
            if(hasChild){
              if(isOpen){
                Link l = getOpenCloseLinkClone( icons[ICONINDEX_M_MINUS] );
                setLinkToOpenOrCloseNode(l,item,isOpen);
                nodeTable.add(l,1,1);
                newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_LINE]);
              } else {
                Link l = getOpenCloseLinkClone( icons[ICONINDEX_M_PLUS] );
                setLinkToOpenOrCloseNode(l,item,isOpen);
                nodeTable.add(l,1,1);
              }
            } else {
              nodeTable.add(icons[ICONINDEX_M_LINE],1,1);
              //newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
            }
          }
        }

        addNodeTableInTreeTable(nodeTable);
        if(hasChild && isOpen){
          drawTree(item.getChildren(),newCollectedIcons);
        }
      }
    }
  }

  private Image[] getNewCollectedIconArray(Image[] oldIcons, Image newIcon){
    Image[] newArray = null;
    if(newIcon != null){
      if(oldIcons == null){
        newArray = new Image[1];
        newArray[0] = newIcon;
        return newArray;
      } else {
        newArray = new Image[oldIcons.length+1];
        System.arraycopy(oldIcons,0,newArray,0,oldIcons.length);
        newArray[newArray.length-1] = newIcon;
      }
    }
    return newArray;
  }


  private synchronized void addNodeTableInTreeTable(Table table){
    treeTable.add(table,1,treeTableIndex);
    treeTableIndex++;
  }

  public void setInitOpenLevel(){
    Iterator iter = this.defaultRoot.getChildren();
    if(defaultOpenLevel > 0){
      setInitOpenLevel(iter,1);
    }
  }

  private void setInitOpenLevel(Iterator iter, int level){
    if(iter != null){
      for (int i = 0; iter.hasNext() ; i++) {
        ICTreeNode node = (ICTreeNode)iter.next();
        openNodes.add(Integer.toString(node.getNodeID()));
        if( level < defaultOpenLevel ){
          setInitOpenLevel(node.getChildren(),level+1);
        }
      }
    }
  }

  public void updateOpenNodes(IWContext iwc){
    openNodes.clear();
    String[] open = iwc.getParameterValues(PRM_OPEN_TREENODES);
    if(open != null){
      for (int i = 0; i < open.length; i++) {
        openNodes.add(open[i]);
      }
    } else if(iwc.getParameter(PRM_TREE_CHANGED) == null){ // set init Open level
      setInitOpenLevel();
    }

    String close = iwc.getParameter(PRM_TREENODE_TO_CLOSE);
    if(close != null){
      openNodes.remove(close);
    }

  }

  public void main(IWContext iwc) throws Exception {
    updateOpenNodes(iwc);
    drawTree(iwc);

  }


  public abstract PresentationObject getObjectToAddToColumn(int colIndex, ICTreeNode node, IWContext iwc, boolean nodeIsOpen, boolean nodeHasChild, boolean isRootNode);


  public void setRootNode(ICTreeNode root){
    defaultRoot.clear();
    defaultRoot.addTreeNode(root);
  }

  public void setToShowRootNode(boolean value){
    showRootNode = value;
  }

  public boolean showRootNodeTreeIcons(){
    return showRootNodeTreeIcons;
  }

  public void setToShowRootNodeTreeIcons(boolean value){
    showRootNodeTreeIcons = value;
  }

  public void setFirstLevelNodes(ICTreeNode[] nodes){
    defaultRoot.clear();
    if(nodes != null){
      for (int i = 0; i < nodes.length; i++) {
        defaultRoot.addTreeNode(nodes[i]);
      }
    }
  }

  public void setToMaintainParameter(String parameterName,IWContext iwc){
    openCloseLink.maintainParameter(parameterName,iwc);
  }

  protected Link getOpenCloseLinkClone(PresentationObject obj){
    Link l = (Link)openCloseLink.clone();
    l.setObject(obj);
    return l;
  }

  protected Link getOpenCloseLinkClone(Image obj){
    Link l = (Link)openCloseLink.clone();
    l.setImage(obj);
    return l;
  }



  public Link setLinkToMaintainOpenAndClosedNodes(Link l){
    int size = openNodes.size();
    for (int i = 0; i < size; i++) {
      l.addParameter(PRM_OPEN_TREENODES,(String)openNodes.get(i));
    }
    l.addParameter(PRM_TREE_CHANGED,"t");
    return l;
  }

  public Link setLinkToOpenOrCloseNode(Link l, ICTreeNode node, boolean nodeIsOpen){
    this.setLinkToMaintainOpenAndClosedNodes(l);

    if(nodeIsOpen){
      l.addParameter(PRM_TREENODE_TO_CLOSE,node.getNodeID());
    } else {
      l.addParameter(PRM_OPEN_TREENODES,node.getNodeID());
    }

    return l;

  }


  public void setColumns(int cols){
    nodeTable.resize(cols,nodeTable.getRows());
  }

  public void setColumnWidth(int col, String width){
    nodeTable.setWidth(col,width);
  }

  public void setColumnHorizontalAlignemnt(int col, String alignment){
    int rows = nodeTable.getRows();
    for (int i = 1; i <= rows; i++) {
      nodeTable.setAlignment(col,i,alignment);
    }
  }

  public void setColumnVerticalAlignemnt(int col, String alignment){
    int rows = nodeTable.getRows();
    for (int i = 1; i <= rows; i++) {
      nodeTable.setVerticalAlignment(col,i,alignment);
    }
  }


  public void setDefaultOpenLevel(int value){
    defaultOpenLevel = value;
  }

  public int getDefaultOpenLevel(){
    return defaultOpenLevel;
  }


  public void setNestLevelAtOpen(int nodesDown){
    setDefaultOpenLevel(nodesDown);
  }

  public int getNestLevelAtOpen(){
    return getDefaultOpenLevel();
  }



  /*
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

*/


  public class DefaultTreeNode implements ICTreeNode {

    Vector childrens = new Vector();
    ICTreeNode parentNode = null;
    String name;
    int id;

    public DefaultTreeNode(){
      this("untitled",0);
    }

    public DefaultTreeNode(String nodeName, int id){
      name = nodeName;
      id = id;
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     */
    public Iterator getChildren(){
      if(childrens != null){
        return childrens.iterator();
      } else {
        return null;
      }
    }

    /**
     *  Returns true if the receiver allows children.
     */
    public boolean getAllowsChildren(){
      if(childrens != null){
        return true;
      }else{
        return false;
      }
    }

    /**
     *  Returns the child TreeNode at index childIndex.
     */
    public ICTreeNode getChildAtIndex(int childIndex){
      return (ICTreeNode)childrens.get(childIndex);
    }

    /**
     *    Returns the number of children TreeNodes the receiver contains.
     */
    public int getChildCount(){
      return childrens.size();
    }

    /**
     * Returns the index of node in the receivers children.
     */
    public int getIndex(ICTreeNode node){
      return childrens.indexOf(node);
    }

    /**
     *  Returns the parent TreeNode of the receiver.
     */
    public ICTreeNode getParentNode(){
      return parentNode;
    }

    /**
     *  Returns true if the receiver is a leaf.
     */
    public boolean isLeaf(){
      return (this.getChildCount() == 0);
    }

    /**
     *  Returns the name of the Node
     */
    public String getNodeName(){
      return name;
    }


    /**
     * Returns the unique ID of the Node in the tree
     */
    public int getNodeID(){
      return id;
    }


    /**
     * @return the number of siblings this node has
     */
    public int getSiblingCount(){
      try {
        return this.getParentNode().getChildCount()-1;
      }
      catch (Exception ex) {
        return -1;
      }
    }



    public void addTreeNode(ICTreeNode node){
      if(node instanceof DefaultTreeNode){
        ((DefaultTreeNode)node).setParentNode(this);
      }
      childrens.add(node);
    }

    public void setParentNode(ICTreeNode node){
      parentNode = node;
    }

    public void clear(){
      if(childrens != null){
        childrens.clear();
      }
    }




  }



}