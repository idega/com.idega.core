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
import com.idega.presentation.text.Text;

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
  int _cols = 1;
  int _extracols = 1;

  private static final String TREEVIEW_PREFIX = "treeviewer/ui/";

  public static final String _UI_WIN = "win/";
  public static final String _UI_MAC = "mac/";
  public static final String _UI_IW = "iw/";
  private String _ui = _UI_IW;

  Image icons[] = null;
  String iconNames[]={"treeviewer_trancparent.gif","treeviewer_line.gif",
                      "treeviewer_R_line.gif","treeviewer_R_minus.gif","treeviewer_R_plus.gif",
                      "treeviewer_L_line.gif","treeviewer_L_minus.gif","treeviewer_L_plus.gif",
                      "treeviewer_M_line.gif","treeviewer_M_minus.gif","treeviewer_M_plus.gif",
                      "treeviewer_F_line.gif","treeviewer_F_minus.gif","treeviewer_F_plus.gif"};

  private String trancparentImageUrl = "treeviewer_trancparent.gif";
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


  protected String iconWidth = "16";
  protected String iconHeight = "16";

  Table frameTable = null;
  Table treeTable = null;
  int treeTableIndex = 1;


  Link openCloseLink = new Link();

  List openNodes = new Vector();
  public static final String PRM_OPEN_TREENODES = "ic_opn_trnds";
  public static final String PRM_TREENODE_TO_CLOSE = "ic_cls_trnd";
  public static final String PRM_TREE_CHANGED = "ic_tw_ch";

  private boolean _showSuperRootNode = false;
  private String _superRootNodeName = "Root";
  private Image _superRootNodeIcon = null;
  private boolean _showTreeIcons = true;
  private boolean _showTreeIcons_changed = false;

  public AbstractTreeViewer() {
    super();
    defaultRoot = new DefaultTreeNode("root",-1);
    icons = new Image[14];
    treeTable = new Table(2,1);
    //treeTable.setBorder(1);
    treeTable.setCellpadding(0);
    treeTable.setCellspacing(0);
//    treeTable.setWidth("100%");
    frameTable = new Table();
    //frameTable.setBorder(1);
    frameTable.setCellpadding(0);
    frameTable.setCellspacing(0);
    frameTable.setColumnAlignment(1,"left");
  }


  private Table getTreeTableClone(){
    return (Table)treeTable.clone();
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
    if(_showTreeIcons){
      for (int i = 0; i < icons.length; i++) {
        if(icons[i] == null){
          icons[i] = bundle.getImage(TREEVIEW_PREFIX+getUI()+iconNames[i]);
        }
      }
    }else {
      for (int i = 0; i < icons.length; i++) {
        if(icons[i] == null){
          icons[i] = Table.getTransparentCell(iwc);
        }
      }
    }
    _showTreeIcons_changed = false;
    updateIconDimansions();
  }

  public void setToShowTreeIcons(boolean value){
    _showTreeIcons = value;
    _showTreeIcons_changed = true;
  }

  public void initializeInMain(IWContext iwc){
    initIcons(iwc);
  }


  public void setUI(String ui){
    if(ui != null){
      _ui = ui+((ui.endsWith("/"))?"":"/");
    } else {
      _ui = "";
    }
  }

  public String getUI(){
    return _ui;
  }


  public void drawTree(IWContext iwc){
    this.empty();
    this.add(frameTable);
    frameTable.empty();
    treeTableIndex = 1;
    if(_showSuperRootNode){
      drawSuperRoot(iwc);
    }else{
      drawTree(defaultRoot.getChildren(),null,iwc);
    }
  }



  private void drawSuperRoot(IWContext iwc){
    Table nodeTable = new Table(3,1);
    nodeTable.setCellpadding(0);
    nodeTable.setCellspacing(0);

    nodeTable.setWidth(1,"16");
    nodeTable.setWidth(2,"3");

    if(_superRootNodeIcon != null){
      nodeTable.add(_superRootNodeIcon,1,1);
    }

    nodeTable.add(new Text(_superRootNodeName),3,1);


    frameTable.add(nodeTable,1,this.getRowIndex());
    if(defaultRoot.getChildCount() > 0){
      drawTree(defaultRoot.getChildren(),null, iwc);
    }
  }

  private synchronized void drawTree(Iterator nodes, Image[] collectedIcons, IWContext iwc){
    if(nodes != null){
      Iterator iter = nodes;
      for (int i = 0; iter.hasNext(); i++) {
        ICTreeNode item = (ICTreeNode)iter.next();
        boolean hasChild = (item.getChildCount() > 0);
        boolean isOpen = false;
        int rowIndex = getRowIndex();
        Table treeColumns = this.getTreeTableClone();
        if(hasChild){
          isOpen = openNodes.contains(Integer.toString(item.getNodeID()));
        }
        boolean isRoot = (defaultRoot.getIndex(item) >= 0);

        for (int k = 1; k < _cols; k++) {
          PresentationObject obj = this.getObjectToAddToColumn(k,item,iwc,isOpen,hasChild,isRoot);
          if(obj != null){
            treeColumns.add(obj,k+1,1);
          }
        }

        for (int k = 1; k < _extracols; k++) {
          PresentationObject obj = this.getObjectToAddToParallelExtraColumn(k,item,iwc,isOpen,hasChild,isRoot);
          if(obj != null){
            frameTable.add(obj,k+1,rowIndex);
          }
        }

        if(collectedIcons != null){
          int collectedIconslength = collectedIcons.length;
         /* try {
            int width = Integer.parseInt(iconWidth)*(collectedIconslength+1);
            treeColumns.setWidth(1,Integer.toString(width));
          }
          catch (NumberFormatException ex) {
            System.err.println("AbstractTreeViewer iconWidth: "+ iconWidth);
            // doNothing iconWidth is x%
          }
*/
          for (int j = 0; j < collectedIconslength; j++) {
            treeColumns.add(collectedIcons[j],1,1);
          }
        }
        Image[] newCollectedIcons = null;
        if(isRoot && !_showSuperRootNode){
          if(showRootNodeTreeIcons()){
            if(i == 0 && !iter.hasNext()){
              if(hasChild){
                if(isOpen){
                  PresentationObject p = null;
                  if(_showTreeIcons){
                    p = getOpenCloseLinkClone( icons[ICONINDEX_ROOT_MINUS] );
                    setLinkToOpenOrCloseNode((Link)p,item,isOpen);
                  }else{
                    p = icons[ICONINDEX_ROOT_MINUS];
                  }
                  treeColumns.add(p,1,1);
                  newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
                } else {
                  PresentationObject p = null;
                  if(_showTreeIcons){
                    p = getOpenCloseLinkClone( icons[ICONINDEX_ROOT_PLUS] );
                    setLinkToOpenOrCloseNode((Link)p,item,isOpen);
                  }else{
                    p = icons[ICONINDEX_ROOT_PLUS];
                  }
                  treeColumns.add(p,1,1);
                }
              } else {
                treeColumns.add(icons[ICONINDEX_ROOT_LINE],1,1);
                //newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
              }
            } else {
              if(i == 0){
                if(hasChild){
                  if(isOpen){
                    PresentationObject p = null;
                    if(_showTreeIcons){
                    p = getOpenCloseLinkClone( icons[ICONINDEX_F_MINUS] );
                    setLinkToOpenOrCloseNode((Link)p,item,isOpen);
                    }else{
                      p = icons[ICONINDEX_F_MINUS];
                    }
                    treeColumns.add(p,1,1);
                    newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_LINE]);
                  } else {
                    PresentationObject p = null;
                    if(_showTreeIcons){
                    p = getOpenCloseLinkClone( icons[ICONINDEX_F_PLUS] );
                    setLinkToOpenOrCloseNode((Link)p,item,isOpen);
                    }else{
                      p = icons[ICONINDEX_F_PLUS];
                    }
                    treeColumns.add(p,1,1);
                  }
                } else {
                  treeColumns.add(icons[ICONINDEX_F_LINE],1,1);
                  //newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
                }
              } else if(hasChild){
                if(!item.isLeaf()){
                  if(isOpen){
                    PresentationObject p = null;
                    if(_showTreeIcons){
                    p = getOpenCloseLinkClone( icons[ICONINDEX_L_MINUS] );
                    setLinkToOpenOrCloseNode((Link)p,item,isOpen);
                    }else{
                      p = icons[ICONINDEX_L_MINUS];
                    }
                    treeColumns.add(p,1,1);
                    newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
                  } else{
                    PresentationObject p = null;
                    if(_showTreeIcons){
                    p = getOpenCloseLinkClone( icons[ICONINDEX_L_PLUS] );
                    setLinkToOpenOrCloseNode((Link)p,item,isOpen);
                    }else{
                      p = icons[ICONINDEX_L_PLUS];
                    }
                    treeColumns.add(p,1,1);
                  }
                } else {
                  treeColumns.add(icons[ICONINDEX_L_LINE],1,1);
                  //newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
                }
              } else {
                if(hasChild){
                  if(isOpen){
                    PresentationObject p = null;
                    if(_showTreeIcons){
                    p = getOpenCloseLinkClone( icons[ICONINDEX_M_MINUS] );
                    }else{
                      p = icons[ICONINDEX_M_MINUS];
                    }
                    setLinkToOpenOrCloseNode((Link)p,item,isOpen);
                    treeColumns.add(p,1,1);
                    newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_LINE]);
                  } else {
                    PresentationObject p = null;
                    if(_showTreeIcons){
                    p = getOpenCloseLinkClone( icons[ICONINDEX_M_PLUS] );
                    }else{
                      p = icons[ICONINDEX_M_PLUS];
                    }
                    setLinkToOpenOrCloseNode((Link)p,item,isOpen);
                    treeColumns.add(p,1,1);
                  }
                } else {
                  treeColumns.add(icons[ICONINDEX_M_LINE],1,1);
                  //newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
                }
              }
            }
          }
        }else{
          if(!iter.hasNext()){
            if(hasChild){
              if(isOpen){
                PresentationObject p = null;
                if(_showTreeIcons){
                p = getOpenCloseLinkClone( icons[ICONINDEX_L_MINUS] );
                setLinkToOpenOrCloseNode((Link)p,item,isOpen);
                }else{
                  p = icons[ICONINDEX_L_MINUS];
                }
                treeColumns.add(p,1,1);
                newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
              } else {
                PresentationObject p = null;
                if(_showTreeIcons){
                p = getOpenCloseLinkClone( icons[ICONINDEX_L_PLUS] );
                setLinkToOpenOrCloseNode((Link)p,item,isOpen);
                }else{
                  p = icons[ICONINDEX_L_PLUS];
                }
                treeColumns.add(p,1,1);
              }
            } else {
              treeColumns.add(icons[ICONINDEX_L_LINE],1,1);
              //newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
            }
          } else{
            if(hasChild){
              if(isOpen){
                PresentationObject p = null;
                if(_showTreeIcons){
                  p = getOpenCloseLinkClone( icons[ICONINDEX_M_MINUS] );
                  setLinkToOpenOrCloseNode((Link)p,item,isOpen);
                }else{
                  p = icons[ICONINDEX_M_MINUS];
                }
                treeColumns.add(p,1,1);
                newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_LINE]);
              } else {
                PresentationObject p = null;
                if(_showTreeIcons){
                  p = getOpenCloseLinkClone( icons[ICONINDEX_M_PLUS] );
                  setLinkToOpenOrCloseNode((Link)p,item,isOpen);
                }else{
                  p = icons[ICONINDEX_M_PLUS];
                }
                treeColumns.add(p,1,1);
              }
            } else {
              treeColumns.add(icons[ICONINDEX_M_LINE],1,1);
              //newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
            }
          }
        }

        frameTable.add(treeColumns,1,rowIndex);

        if(hasChild && isOpen){
          drawTree(item.getChildren(),newCollectedIcons, iwc);
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


  private synchronized int getRowIndex(){
    return treeTableIndex++;
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
    if(_showTreeIcons_changed){
      initIcons(iwc);
    }
    updateOpenNodes(iwc);
    drawTree(iwc);

  }


  public abstract PresentationObject getObjectToAddToColumn(int colIndex, ICTreeNode node, IWContext iwc, boolean nodeIsOpen, boolean nodeHasChild, boolean isRootNode);

  public PresentationObject getObjectToAddToParallelExtraColumn(int colIndex, ICTreeNode node, IWContext iwc, boolean nodeIsOpen, boolean nodeHasChild, boolean isRootNode){
    return null;
  }


  public void setToShowSuperRootNode(boolean value){
    _showSuperRootNode = value;
  }

  public void setSuperRootNodeName(String name){
    _superRootNodeName = name;
  }

  public void setSuperRootNodeIcon(Image image){
    _superRootNodeIcon = image;
  }

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

  public void setFirstLevelNodes(Iterator nodes){
    defaultRoot.clear();
    if(nodes != null){
      while (nodes.hasNext()) {
        ICTreeNode node = (ICTreeNode) nodes.next();
        defaultRoot.addTreeNode(node);
      }
    }
  }


  public void addFirstLevelNode(ICTreeNode node){
    if(node != null){
      defaultRoot.addTreeNode(node);
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


  public void setTreeColumns(int cols){
    _cols = cols+1;
    treeTable.resize(_cols,frameTable.getRows());
  }

  public void setParallelExtraColumns(int cols){
    _extracols = cols+1;
    frameTable.resize(_extracols,frameTable.getRows());
  }


  public void setColumns(int cols){
    setTreeColumns(cols);
  }

  public void setExtraColumnWidth(int col, String width){
    frameTable.setWidth(col+1,width);
  }

  public void setTreeColumnWidth(int col, String width){
    treeTable.setWidth(col+1,width);
  }

  public void setWidth(String s){
    frameTable.setWidth(s);
  }

  public void setExtraColumnHorizontalAlignment(int col, String alignment){
      frameTable.setColumnAlignment(col+1,alignment);
  }

  public void setExtraColumnVerticalAlignment(int col, String alignment){
      frameTable.setColumnVerticalAlignment(col+1,alignment);
  }

  public void setExtraColumnHeading(int col, PresentationObject obj){
      frameTable.add(obj,col+1,1);
  }

  public void setColumnHeading(int col, PresentationObject obj){
      frameTable.add(obj,col,1);
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