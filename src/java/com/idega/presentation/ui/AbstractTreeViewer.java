package com.idega.presentation.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import com.idega.core.data.ICTreeNode;
import com.idega.event.IWActionListener;
import com.idega.event.IWPresentationEvent;
import com.idega.event.IWPresentationState;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.StatefullPresentation;
import com.idega.presentation.StatefullPresentationImplHandler;
import com.idega.presentation.Table;
import com.idega.presentation.event.TreeViewerEvent;
import com.idega.presentation.text.Anchor;
import com.idega.presentation.text.AnchorLink;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.user.business.GroupTreeComparator;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public abstract class AbstractTreeViewer extends PresentationObjectContainer implements StatefullPresentation {

	DefaultTreeNode defaultRoot = null;
	boolean showRootNode = false;
	boolean showRootNodeTreeIcons = false;
	int defaultOpenLevel = 1;
	int _cols = 1;
	int _extracols = 1;
	boolean _nowrap = true;

	private static final String TREEVIEW_PREFIX = "treeviewer/ui/";

	public static final String _UI_WIN = "win/";
	public static final String _UI_MAC = "mac/";
	public static final String _UI_IW = "iw/";
	private String _ui = _UI_IW;

	private HashMap _extraHorizontal;
	private HashMap _extraVertical;

	Image icons[] = null;
	String iconNames[] = { "treeviewer_trancparent.gif", "treeviewer_line.gif", "treeviewer_R_line.gif", "treeviewer_R_minus.gif", "treeviewer_R_plus.gif", "treeviewer_L_line.gif", "treeviewer_L_minus.gif", "treeviewer_L_plus.gif", "treeviewer_M_line.gif", "treeviewer_M_minus.gif", "treeviewer_M_plus.gif", "treeviewer_F_line.gif", "treeviewer_F_minus.gif", "treeviewer_F_plus.gif" };

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
	
	protected String lightRowStyle;
	protected String darkRowStyle;

	Table frameTable = null;
	Table treeTable = null;
	int treeTableIndex = 1;

	AnchorLink openCloseLink = new AnchorLink();

	List openNodes = new Vector();
	//  public static final String PRM_OPEN_TREENODES = "ic_opn_trnds";
	//  public static final String PRM_TREENODE_TO_CLOSE = "ic_cls_trnd";
	//public static final String PRM_TREE_CHANGED = "ic_tw_ch";

	private boolean _showSuperRootNode = false;
	private String _superRootNodeName = "Root";
	private Image _superRootNodeIcon = null;
	private boolean _showTreeIcons = true;
	private boolean _showTreeIcons_changed = false;
	private boolean _showHeaderRow = false;

	private TreeViewerEvent _eventModel = null;

	private StatefullPresentationImplHandler _stateHandler = null;

	private Map _extraHeadings;
	private Map _extraWidths;
	private String _headingColor;
	public AbstractTreeViewer() {
		super();

		_stateHandler = new StatefullPresentationImplHandler();
		_stateHandler.setPresentationStateClass(TreeViewerPS.class);

		defaultRoot = new DefaultTreeNode("root", -1);
		icons = new Image[14];
		treeTable = new Table(2, 1);
		//treeTable.setBorder(1);
		treeTable.setCellpadding(0);
		treeTable.setCellspacing(0);
		//    treeTable.setWidth("100%");
		frameTable = new Table();
		//frameTable.setBorder(1);
		frameTable.setCellpadding(0);
		frameTable.setCellspacing(0);
		frameTable.setColumnAlignment(1, "left");
	}

	private Table getTreeTableClone() {
		return (Table) treeTable.clone();
	}

	public void setIconDimensions(String width, String Height) {
		iconWidth = width;
		iconHeight = Height;
		if (initializedInMain) {
			updateIconDimensions();
		}
	}

	protected void updateIconDimensions() {
		for (int i = 0; i < icons.length; i++) {
			Image tmp = icons[i];
			if (tmp != null) {
				tmp.setWidth(iconWidth);
				tmp.setHeight(iconHeight);
				tmp.setAlignment("top");
				icons[i] = tmp;
			}
		}
	}

	public void initIcons(IWContext iwc) {
		IWBundle bundle = getBundle(iwc);
		if (_showTreeIcons) {
			for (int i = 0; i < icons.length; i++) {
				if (icons[i] == null) {
					icons[i] = bundle.getImage(TREEVIEW_PREFIX + getUI() + iconNames[i]);
				}
			}
		} else {
			for (int i = 0; i < icons.length; i++) {
				if (icons[i] == null) {
					icons[i] = Table.getTransparentCell(iwc);
				}
			}
		}
		_showTreeIcons_changed = false;
		updateIconDimensions();
	}

	public void setToShowTreeIcons(boolean value) {
		_showTreeIcons = value;
		_showTreeIcons_changed = true;
	}

	public void initializeInMain(IWContext iwc) throws Exception {
		super.initializeInMain(iwc);
		this.addActionListener((IWActionListener) this.getPresentationState(iwc));
		initIcons(iwc);
	}

	public void setUI(String ui) {
		if (ui != null) {
			_ui = ui + ((ui.endsWith("/")) ? "" : "/");
		} else {
			_ui = "";
		}
	}

	public String getUI() {
		return _ui;
	}

	public void drawTree(IWContext iwc) {
		this.empty();
		frameTable.empty();
		frameTable.resize(1, 1);
		this.add(frameTable);
		//frameTable.empty();
		treeTableIndex = ((!_showHeaderRow) ? 1 : 2);
		if (_showSuperRootNode) {
			drawSuperRoot(iwc);
		}
		
		if (defaultRoot.getChildCount() > 0) {
			drawTree(defaultRoot.getChildrenIterator(), null, iwc);
		}
		
		if (lightRowStyle != null && darkRowStyle != null) {
			int startRow = ((!_showHeaderRow) ? 1 : 2);
			int row = 1;
			for (int a = startRow; a <= frameTable.getRows(); a++) {
				if (row % 2 != 0) {
					frameTable.setRowStyleClass(a, lightRowStyle);
				}
				else {
					frameTable.setRowStyleClass(a, darkRowStyle);
				}
				row++;
			}
		}
		
		
	}

	private void drawSuperRoot(IWContext iwc) {
		Table nodeTable = new Table(3, 1);
		nodeTable.setCellpadding(0);
		nodeTable.setCellspacing(0);

		nodeTable.setWidth(1, "16");
		nodeTable.setWidth(2, "3");

		if (_superRootNodeIcon != null) {
			nodeTable.add(_superRootNodeIcon, 1, 1);
		}

		nodeTable.add(new Text(_superRootNodeName), 3, 1);

		frameTable.add(nodeTable, 1, this.getRowIndex());
		
	}

	private synchronized void drawTree(Iterator nodes, Image[] collectedIcons, IWContext iwc) {
		if (nodes != null) {
			Iterator iter = nodes;
			for (int i = 0; iter.hasNext(); i++) {
				ICTreeNode item = (ICTreeNode) iter.next();			
				boolean hasChild = (item.getChildCount() > 0);
				boolean isOpen = false;
				int rowIndex = getRowIndex();
				Table treeColumns = this.getTreeTableClone();
				String anchorName = Integer.toString(item.getNodeID());
//				treeColumns.setBorder(1);
//				frameTable.setBorder(1);
				if (hasChild) {
					isOpen = openNodes.contains(Integer.toString(item.getNodeID()));
				}
				boolean isRoot = (defaultRoot.getIndex(item) >= 0);

				for (int k = 1; k < _cols; k++) {
					PresentationObject obj = this.getObjectToAddToColumn(k, item, iwc, isOpen, hasChild, isRoot);
					if (obj != null) {
						treeColumns.add(obj, k + 1, 1);
						if (_nowrap) {
							treeColumns.setNoWrap(k + 1, 1);
						}
					}
				}

				for (int k = 1; k < _extracols; k++) {
					PresentationObject obj = this.getObjectToAddToParallelExtraColumn(k, item, iwc, isOpen, hasChild, isRoot);
					if (obj != null) {
						if (_nowrap)
							treeColumns.setNoWrap(1, 1);
						frameTable.add(obj, k + 1, rowIndex);
					}
				}

				if (collectedIcons != null) {
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
					if (_nowrap)
						treeColumns.setNoWrap(1, 1);
					for (int j = 0; j < collectedIconslength; j++) {
						treeColumns.add(collectedIcons[j], 1, 1);
					}
				}
				Image[] newCollectedIcons = null;
				if (isRoot && !_showSuperRootNode) {
					if (showRootNodeTreeIcons()) {
						if (i == 0 && !iter.hasNext()) {  //If there is one and only one rootnode
							if (hasChild) {
								if (isOpen) {
									PresentationObject p = null;
									if (_showTreeIcons) {
										p = getOpenCloseLinkClone(icons[ICONINDEX_ROOT_MINUS], anchorName);
										setLinkToOpenOrCloseNode((Link) p, item, isOpen);
									} else {
										p = icons[ICONINDEX_ROOT_MINUS];
									}
									if (_nowrap)
										treeColumns.setNoWrap(1, 1);
									treeColumns.add(p, 1, 1);
									treeColumns.add(new Anchor(anchorName), 1, 1);
									newCollectedIcons = getNewCollectedIconArray(collectedIcons, icons[ICONINDEX_TRANCPARENT]);
								} else {
									PresentationObject p = null;
									if (_showTreeIcons) {
										p = getOpenCloseLinkClone(icons[ICONINDEX_ROOT_PLUS], anchorName);
										setLinkToOpenOrCloseNode((Link) p, item, isOpen);
									} else {
										p = icons[ICONINDEX_ROOT_PLUS];
									}
									if (_nowrap)
										treeColumns.setNoWrap(1, 1);
									treeColumns.add(new Anchor(anchorName), 1, 1);
									treeColumns.add(p, 1, 1);
								}
							} else {
								treeColumns.add(icons[ICONINDEX_ROOT_LINE], 1, 1);
								//newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
							}
						} else {  // if there are more than one rootnotes
							if (i == 0) {  // the first rootnode
								if (hasChild) {
									if (isOpen) {
										PresentationObject p = null;
										if (_showTreeIcons) {
											p = getOpenCloseLinkClone(icons[ICONINDEX_F_MINUS], anchorName);
											setLinkToOpenOrCloseNode((Link) p, item, isOpen);
										} else {
											p = icons[ICONINDEX_F_MINUS];
										}
										treeColumns.add(p, 1, 1);
										treeColumns.add(new Anchor(anchorName), 1, 1);
										newCollectedIcons = getNewCollectedIconArray(collectedIcons, icons[ICONINDEX_LINE]);
									} else {
										PresentationObject p = null;
										if (_showTreeIcons) {
											p = getOpenCloseLinkClone(icons[ICONINDEX_F_PLUS], anchorName);
											setLinkToOpenOrCloseNode((Link) p, item, isOpen);
										} else {
											p = icons[ICONINDEX_F_PLUS];
										}
										if (_nowrap)
											treeColumns.setNoWrap(1, 1);
										treeColumns.add(p, 1, 1);
										treeColumns.add(new Anchor(anchorName), 1, 1);
									}
								} else {
									if (_nowrap)
										treeColumns.setNoWrap(1, 1);
									treeColumns.add(icons[ICONINDEX_F_LINE], 1, 1);
									//newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
								}
							} else if(!iter.hasNext()){ // the last rootnode
								if (hasChild) {  //if this node has a child
									if (isOpen) { // if this node is open
										PresentationObject p = null;
										if (_showTreeIcons) {
											p = getOpenCloseLinkClone(icons[ICONINDEX_L_MINUS], anchorName);
											setLinkToOpenOrCloseNode((Link) p, item, isOpen);
										} else {
											p = icons[ICONINDEX_L_MINUS];
										}
										if (_nowrap)
											treeColumns.setNoWrap(1, 1);
										treeColumns.add(p, 1, 1);
										treeColumns.add(new Anchor(anchorName), 1, 1);
										newCollectedIcons = getNewCollectedIconArray(collectedIcons, icons[ICONINDEX_TRANCPARENT]);
									} else { // if this node is closed
										PresentationObject p = null;
										if (_showTreeIcons) {
											p = getOpenCloseLinkClone(icons[ICONINDEX_L_PLUS], anchorName);
											setLinkToOpenOrCloseNode((Link) p, item, isOpen);
										} else {
											p = icons[ICONINDEX_L_PLUS];
										}
										if (_nowrap)
											treeColumns.setNoWrap(1, 1);
										treeColumns.add(p, 1, 1);
										treeColumns.add(new Anchor(anchorName), 1, 1);
									}
								} else {  //if this node does not have any children
									if (_nowrap)
										treeColumns.setNoWrap(1, 1);
									treeColumns.add(icons[ICONINDEX_L_LINE], 1, 1);
									//newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
								}
							
							}else { //all but first and last rootnodes
								if (hasChild) { // if this node has a child
									if (isOpen) { // if this node is open
										PresentationObject p = null;
										if (_showTreeIcons) {
											p = getOpenCloseLinkClone(icons[ICONINDEX_M_MINUS], anchorName);
											setLinkToOpenOrCloseNode((Link) p, item, isOpen);
										} else {
											p = icons[ICONINDEX_M_MINUS];
										}
										if (_nowrap)
											treeColumns.setNoWrap(1, 1);
										treeColumns.add(p, 1, 1);
										treeColumns.add(new Anchor(anchorName), 1, 1);
										newCollectedIcons = getNewCollectedIconArray(collectedIcons, icons[ICONINDEX_LINE]);
									} else { // if this node is closed
										PresentationObject p = null;
										if (_showTreeIcons) {
											p = getOpenCloseLinkClone(icons[ICONINDEX_M_PLUS], anchorName);
											setLinkToOpenOrCloseNode((Link) p, item, isOpen);
										} else {
											p = icons[ICONINDEX_M_PLUS];
										}
										if (_nowrap)
											treeColumns.setNoWrap(1, 1);
										treeColumns.add(p, 1, 1);
										treeColumns.add(new Anchor(anchorName), 1, 1);
									}
								} else { // if this node does not have any children
									if (_nowrap)
										treeColumns.setNoWrap(1, 1);
									treeColumns.add(icons[ICONINDEX_M_LINE], 1, 1);
									//newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
								}
							}
						}
					}
				} else {  // if rootnode tree icons are not shown
					if (!iter.hasNext()) {  //if this is the last node
						if (hasChild) {  //if this node has a child
							if (isOpen) { // if this node is open
								PresentationObject p = null;
								if (_showTreeIcons) {
									p = getOpenCloseLinkClone(icons[ICONINDEX_L_MINUS], anchorName);
									setLinkToOpenOrCloseNode((Link) p, item, isOpen);
								} else {
									p = icons[ICONINDEX_L_MINUS];
								}
								if (_nowrap)
									treeColumns.setNoWrap(1, 1);
								treeColumns.add(p, 1, 1);
								treeColumns.add(new Anchor(anchorName), 1, 1);
								newCollectedIcons = getNewCollectedIconArray(collectedIcons, icons[ICONINDEX_TRANCPARENT]);
							} else { // if this node is closed
								PresentationObject p = null;
								if (_showTreeIcons) {
									p = getOpenCloseLinkClone(icons[ICONINDEX_L_PLUS], anchorName);
									setLinkToOpenOrCloseNode((Link) p, item, isOpen);
								} else {
									p = icons[ICONINDEX_L_PLUS];
								}
								if (_nowrap)
									treeColumns.setNoWrap(1, 1);
								treeColumns.add(p, 1, 1);
								treeColumns.add(new Anchor(anchorName), 1, 1);
							}
						} else {  //if this node does not have any children
							if (_nowrap)
								treeColumns.setNoWrap(1, 1);
							treeColumns.add(icons[ICONINDEX_L_LINE], 1, 1);
							//newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
						}
					} else {  // if this is not the last node
						if (hasChild) { // if this node has a child
							if (isOpen) { // if this node is open
								PresentationObject p = null;
								if (_showTreeIcons) {
									p = getOpenCloseLinkClone(icons[ICONINDEX_M_MINUS], anchorName);
									setLinkToOpenOrCloseNode((Link) p, item, isOpen);
								} else {
									p = icons[ICONINDEX_M_MINUS];
								}
								if (_nowrap)
									treeColumns.setNoWrap(1, 1);
								treeColumns.add(p, 1, 1);
								treeColumns.add(new Anchor(anchorName), 1, 1);
								newCollectedIcons = getNewCollectedIconArray(collectedIcons, icons[ICONINDEX_LINE]);
							} else { // if this node is closed
								PresentationObject p = null;
								if (_showTreeIcons) {
									p = getOpenCloseLinkClone(icons[ICONINDEX_M_PLUS], anchorName);
									setLinkToOpenOrCloseNode((Link) p, item, isOpen);
								} else {
									p = icons[ICONINDEX_M_PLUS];
								}
								if (_nowrap)
									treeColumns.setNoWrap(1, 1);
								treeColumns.add(p, 1, 1);
								treeColumns.add(new Anchor(anchorName), 1, 1);
							}
						} else { // if this node does not have any children
							if (_nowrap)
								treeColumns.setNoWrap(1, 1);
							treeColumns.add(icons[ICONINDEX_M_LINE], 1, 1);
							//newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
						}
					}
				}

				frameTable.add(treeColumns, 1, rowIndex);

				if (hasChild && isOpen) {
				    Collection children = item.getChildren();
				    Collections.sort((List)children, new GroupTreeComparator(iwc) );
				    drawTree(children.iterator(), newCollectedIcons, iwc);
				}
			}
		}
	}

	private Image[] getNewCollectedIconArray(Image[] oldIcons, Image newIcon) {
		Image[] newArray = null;
		if (newIcon != null) {
			if (oldIcons == null) {
				newArray = new Image[1];
				newArray[0] = newIcon;
				return newArray;
			} else {
				newArray = new Image[oldIcons.length + 1];
				System.arraycopy(oldIcons, 0, newArray, 0, oldIcons.length);
				newArray[newArray.length - 1] = newIcon;
			}
		}
		return newArray;
	}

	private synchronized int getRowIndex() {
		return treeTableIndex++;
	}

	public void setInitOpenLevel() {
		Iterator iter = this.defaultRoot.getChildrenIterator();
		if (defaultOpenLevel > 0) {
			setInitOpenLevel(iter, 1);
		}
	}

	private void setInitOpenLevel(Iterator iter, int level) {
		if (iter != null) {
			for (int i = 0; iter.hasNext(); i++) {
				ICTreeNode node = (ICTreeNode) iter.next();
				Object item = Integer.toString(node.getNodeID());
				if (!openNodes.contains(item)) {
					openNodes.add(item);
				}
				if (level < defaultOpenLevel) {
					setInitOpenLevel(node.getChildrenIterator(), level + 1);
				}
			}
		}
	}

	public void setOpenNodes(List openNodeList) {
		openNodes = openNodeList;
	}

	public void updateOpenNodes(IWContext iwc) {
		TreeViewerPS state = (TreeViewerPS) this.getPresentationState(iwc);
		//    System.out.println("----------------updateOpenNodes-----------------");
		//    System.out.println(this+" STATE = "+ state );
		//
		//    System.out.println("ATV - TreeViewerPS: initLevel: " + state.setToInitOpenLevel());
		//    Iterator iter = state.getOpenNodeList().iterator();
		//    int counter = 1;
		//    while (iter.hasNext()) {
		//      Object item = iter.next();
		//      System.out.println("ATV - TreeViewerPS: openItem"+counter+": "+item);
		//      counter++;
		//    }

		this.setOpenNodes(state.getOpenNodeList());

		if (state.setToInitOpenLevel()) {
			this.setInitOpenLevel();
		}
	}

	public void main(IWContext iwc) throws Exception {
		if (_showTreeIcons_changed) {
			initIcons(iwc);
		}
		updateOpenNodes(iwc);
		drawTree(iwc);
		setHeadings();
		setAlignments();
	}

	private void setAlignments() {
		if (_extraHorizontal != null) {
			Iterator iter = _extraHorizontal.keySet().iterator();
			while (iter.hasNext()) {
				Integer column = (Integer) iter.next();
				String alignment = (String) _extraHorizontal.get(column);
				this.frameTable.setColumnAlignment(column.intValue(), alignment);
			}
		}

		if (_extraVertical != null) {
			Iterator iter = _extraVertical.keySet().iterator();
			while (iter.hasNext()) {
				Integer column = (Integer) iter.next();
				String alignment = (String) _extraVertical.get(column);
				this.frameTable.setColumnVerticalAlignment(column.intValue(), alignment);
			}
		}
	}

	private void setHeadings() {
		if (_extraHeadings != null) {
			Iterator iter = _extraHeadings.keySet().iterator();
			while (iter.hasNext()) {
				Integer column = (Integer) iter.next();
				PresentationObject object = (PresentationObject) _extraHeadings.get(column);
				this.frameTable.add(object, column.intValue(), 1);
			}
		}

		if (_extraWidths != null) {
			Iterator iter = _extraWidths.keySet().iterator();
			while (iter.hasNext()) {
				Integer column = (Integer) iter.next();
				String width = (String) _extraWidths.get(column);
				this.frameTable.setWidth(column.intValue(), width);
			}
		}

		if (_headingColor != null)
			frameTable.setRowColor(1, _headingColor);
	}

	public abstract PresentationObject getObjectToAddToColumn(int colIndex, ICTreeNode node, IWContext iwc, boolean nodeIsOpen, boolean nodeHasChild, boolean isRootNode);

	public PresentationObject getObjectToAddToParallelExtraColumn(int colIndex, ICTreeNode node, IWContext iwc, boolean nodeIsOpen, boolean nodeHasChild, boolean isRootNode) {
		return null;
	}

	public void setToShowSuperRootNode(boolean value) {
		_showSuperRootNode = value;
	}

	public void setSuperRootNodeName(String name) {
		_superRootNodeName = name;
	}

	public void setSuperRootNodeIcon(Image image) {
		_superRootNodeIcon = image;
	}

	public void setRootNode(ICTreeNode root) {
		defaultRoot.clear();
		defaultRoot.addTreeNode(root);
	}

	public void setToShowRootNode(boolean value) {
		showRootNode = value;
	}

	public boolean showRootNodeTreeIcons() {
		return showRootNodeTreeIcons;
	}

	public void setToShowHeaderRow(boolean showRow) {
		_showHeaderRow = showRow;
	}

	public void setToShowRootNodeTreeIcons(boolean value) {
		showRootNodeTreeIcons = value;
	}

	public void setFirstLevelNodes(ICTreeNode[] nodes) {
		defaultRoot.clear();
		if (nodes != null) {
			for (int i = 0; i < nodes.length; i++) {
				defaultRoot.addTreeNode(nodes[i]);
			}
		}
	}

	public void setFirstLevelNodes(Iterator nodes) {
		defaultRoot.clear();
		if (nodes != null) {
			while (nodes.hasNext()) {
				ICTreeNode node = (ICTreeNode) nodes.next();
				defaultRoot.addTreeNode(node);
			}
		}
	}

	public void addFirstLevelNode(ICTreeNode node) {
		if (node != null) {
			defaultRoot.addTreeNode(node);
		}
	}

	public void setToMaintainParameter(String parameterName, IWContext iwc) {
		openCloseLink.maintainParameter(parameterName, iwc);
	}
	
	public void addOpenCloseParameter(String name,String value){
		openCloseLink.addParameter(name,value);
	}

	protected Link getOpenCloseLinkClone(PresentationObject obj, String anchorName) {
		AnchorLink l = (AnchorLink) openCloseLink.clone();
		l.setObject(obj);
		if (anchorName != null) {
			l.addCurrentURLToLink(true);
			l.setAnchorName(anchorName);
		}
		return l;
	}

	protected Link getOpenCloseLinkClone(Image obj, String anchorName) {
		AnchorLink l = (AnchorLink) openCloseLink.clone();
		l.setImage(obj);
		if (anchorName != null) {
			l.addCurrentURLToLink(true);
			l.setAnchorName(anchorName);
		}
		return l;
	}

	public Link setLinkToMaintainOpenAndClosedNodes(Link l) {
		//    l.addEventModel(_eventModel);
		return l;
	}

	public IWPresentationEvent getOpenCloseEventModel() {
		if (_eventModel == null) {
			_eventModel = new TreeViewerEvent();
			/*if (this.getICObjectInstanceID() != 0) {
				_eventModel.setSource(this.getICObjectInstanceID());
			} else {*/
				//_eventModel.setSource(this.getLocation());
				_eventModel.setSource(this);
      //}
		}
		return _eventModel;
	}

	public Link setLinkToOpenOrCloseNode(Link l, ICTreeNode node, boolean nodeIsOpen) {
		TreeViewerEvent event = (TreeViewerEvent) getOpenCloseEventModel().clone();
		if (nodeIsOpen) {
			event.setToCloseNode(node);
		} else {
			event.setToOpenNode(node);
		}

		l.addEventModel(event);
		return l;
	}

	public void setTreeColumns(int cols) {
		_cols = cols + 1;
		treeTable.resize(_cols, frameTable.getRows());
	}

	public void setTreePadding(int padding) {
		frameTable.setCellpadding(padding);
	}

	public void setParallelExtraColumns(int cols) {
		_extracols = cols + 1;
		frameTable.resize(_extracols, frameTable.getRows());
	}

	public void setColumns(int cols) {
		setTreeColumns(cols);
	}

	public void setExtraColumnWidth(int col, String width) {
		if (_extraWidths == null)
			_extraWidths = new HashMap();
		_extraWidths.put(new Integer(col + 1), width);
	}

	public void setTreeColumnWidth(int col, String width) {
		treeTable.setWidth(col + 1, width);
	}

	public void setWidth(String s) {
		frameTable.setWidth(s);
	}

	public void setExtraColumnHorizontalAlignment(int col, String alignment) {
		if (_extraHorizontal == null)
			_extraHorizontal = new HashMap();
		_extraHorizontal.put(new Integer(col + 1), alignment);
	}

	public void setExtraColumnVerticalAlignment(int col, String alignment) {
		if (_extraVertical == null)
			_extraVertical = new HashMap();
		_extraVertical.put(new Integer(col + 1), alignment);
	}

	public void setExtraColumnHeading(int col, PresentationObject obj) {
		if (_extraHeadings == null)
			_extraHeadings = new HashMap();
		_extraHeadings.put(new Integer(col + 1), obj);
	}

	public void setTreeHeading(int col, PresentationObject obj) {
		setExtraColumnHeading(col-1, obj);
	}

	public void setHeadingColor(String color) {
		_headingColor = color;
	}

	public void setExtraHeadingColor(int col, String color) {
		setHeadingColor(color);
	}

	public void setDefaultOpenLevel(int value) {
		defaultOpenLevel = value;
	}

	public int getDefaultOpenLevel() {
		return defaultOpenLevel;
	}

	public void setNestLevelAtOpen(int nodesDown) {
		setDefaultOpenLevel(nodesDown);
	}

	public int getNestLevelAtOpen() {
		return getDefaultOpenLevel();
	}

	//HeaderRow methods

	public void setHeaderRowHeight(String height) {
		frameTable.setHeight(1, height);
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

	public Class getPresentationStateClass() {
		return _stateHandler.getPresentationStateClass();
	}

	public IWPresentationState getPresentationState(IWUserContext iwuc) {
		return _stateHandler.getPresentationState(this, iwuc);
	}

	public StatefullPresentationImplHandler getStateHandler() {
		return _stateHandler;
	}

	public class DefaultTreeNode implements ICTreeNode {

		Vector childrens = new Vector();
		ICTreeNode parentNode = null;
		String name;
		int id;

		public DefaultTreeNode() {
			this("untitled", 0);
		}

		public DefaultTreeNode(String nodeName, int id) {
			name = nodeName;
			this.id = id;
		}

		/**
		 * Returns the children of the reciever as an Iterator.
		 */
		public Iterator getChildrenIterator() {
			return getChildren().iterator();
		}

		/**
		 * Returns the children of the reciever as a Collection.
		 */
		public Collection getChildren() {
			if (childrens != null) {
				return childrens;
			} else {
				return null;
			}
		}

		/**
		 *  Returns true if the receiver allows children.
		 */
		public boolean getAllowsChildren() {
			if (childrens != null) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 *  Returns the child TreeNode at index childIndex.
		 */
		public ICTreeNode getChildAtIndex(int childIndex) {
			return (ICTreeNode) childrens.get(childIndex);
		}

		/**
		 *    Returns the number of children TreeNodes the receiver contains.
		 */
		public int getChildCount() {
			return childrens.size();
		}

		/**
		 * Returns the index of node in the receivers children.
		 */
		public int getIndex(ICTreeNode node) {
			return childrens.indexOf(node);
		}

		/**
		 *  Returns the parent TreeNode of the receiver.
		 */
		public ICTreeNode getParentNode() {
			return parentNode;
		}

		/**
		 *  Returns true if the receiver is a leaf.
		 */
		public boolean isLeaf() {
			return (this.getChildCount() == 0);
		}

		/**
		 *  Returns the name of the Node
		 */
		public String getNodeName() {
			return name;
		}
		
		/**
		 *  Returns the name of the Node
		 */
		public String getNodeName(Locale locale ) {
			return getNodeName();
		}
		
		/**
		 *  Returns the name of the Node
		 */
		public String getNodeName(Locale locale, IWApplicationContext iwac ) {
			return getNodeName(locale);
		}

		/**
		 * Returns the unique ID of the Node in the tree
		 */
		public int getNodeID() {
			return id;
		}

		/**
		 * @return the number of siblings this node has
		 */
		public int getSiblingCount() {
			try {
				return this.getParentNode().getChildCount() - 1;
			} catch (Exception ex) {
				return -1;
			}
		}
		

		public void addTreeNode(ICTreeNode node) {
			if (node instanceof DefaultTreeNode) {
				((DefaultTreeNode) node).setParentNode(this);
			}
			childrens.add(node);
		}

		public void setParentNode(ICTreeNode node) {
			parentNode = node;
		}

		public void clear() {
			if (childrens != null) {
				childrens.clear();
			}
		}

	}

	public void addEventModel(IWPresentationEvent model) {
		openCloseLink.addEventModel(model);
	}

	public void setOpenCloseLinkTarget(String target) {

		openCloseLink.setTarget(target);
	}

	/**
	 * Sets the nowrap.
	 * @param nowrap The nowrap to set
	 */
	public void setNowrap(boolean nowrap) {
		_nowrap = nowrap;
	}

}
