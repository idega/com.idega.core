package com.idega.presentation.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idega.core.data.DefaultTreeNode;
import com.idega.core.data.ICTreeNode;
import com.idega.event.IWActionListener;
import com.idega.event.IWPresentationEvent;
import com.idega.event.IWPresentationState;
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
import com.idega.user.business.GroupTreeNode;
import com.idega.util.StringUtil;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public abstract class AbstractTreeViewer<Node extends ICTreeNode<?>> extends PresentationObjectContainer implements StatefullPresentation {

	DefaultTreeNode<Node> defaultRoot = null;
	boolean showRootNode = false;
	boolean showRootNodeTreeIcons = false;
	int defaultOpenLevel = 1;
	int _cols = 1;
	int _extracols = 1;
	boolean _nowrap = true;
	String lastNode = null;

	public static final String TREEVIEW_PREFIX = "treeviewer/ui/";

	public static final String _UI_WIN = "win/";
	public static final String _UI_MAC = "mac/";
	public static final String _UI_IW = "iw/";
	private String _ui = _UI_IW;

	private Map<Integer, String> _extraHorizontal;
	private Map<Integer, String> _extraVertical;

	Image icons[] = null;
	String iconNames[] = { "treeviewer_trancparent.gif", "treeviewer_line.gif", "treeviewer_R_line.gif", "treeviewer_R_minus.gif", "treeviewer_R_plus.gif", "treeviewer_L_line.gif", "treeviewer_L_minus.gif", "treeviewer_L_plus.gif", "treeviewer_M_line.gif", "treeviewer_M_minus.gif", "treeviewer_M_plus.gif", "treeviewer_F_line.gif", "treeviewer_F_minus.gif", "treeviewer_F_plus.gif" };

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

	List<String> openNodes = new ArrayList<String>();
	//  public static final String PRM_OPEN_TREENODES = "ic_opn_trnds";
	//  public static final String PRM_TREENODE_TO_CLOSE = "ic_cls_trnd";
	//public static final String PRM_TREE_CHANGED = "ic_tw_ch";

	private boolean _showSuperRootNode = false;
	private String _superRootNodeName = "Root";
	private Image _superRootNodeIcon = null;
	private Link _refreshTopNodes = null;
	private boolean _showTreeIcons = true;
	private boolean _showTreeIcons_changed = false;
	private boolean _showHeaderRow = false;

	private TreeViewerEvent _eventModel = null;

	private StatefullPresentationImplHandler _stateHandler = null;

	private Map<Integer, PresentationObject> _extraHeadings;
	private Map<Integer, String> _extraWidths;
	private String _headingColor;

	private String closeOrOpenNodesHref = null;

	public AbstractTreeViewer() {
		super();

		this._stateHandler = new StatefullPresentationImplHandler();
		this._stateHandler.setPresentationStateClass(TreeViewerPS.class);

		this.defaultRoot = new DefaultTreeNode<Node>("root", -1);
		this.icons = new Image[14];
		this.treeTable = new Table(2, 1);
		//treeTable.setBorder(1);
		this.treeTable.setCellpadding(0);
		this.treeTable.setCellspacing(0);
		//    treeTable.setWidth("100%");
		this.frameTable = new Table();
		//frameTable.setBorder(1);
		this.frameTable.setCellpadding(0);
		this.frameTable.setCellspacing(0);
		this.frameTable.setColumnAlignment(1, "left");
	}

	private Table getTreeTableClone() {
		return (Table) this.treeTable.clone();
	}

	public void setIconDimensions(String width, String Height) {
		this.iconWidth = width;
		this.iconHeight = Height;
		if (this.initializedInMain) {
			updateIconDimensions();
		}
	}

	protected void updateIconDimensions() {
		for (int i = 0; i < this.icons.length; i++) {
			Image tmp = this.icons[i];
			if (tmp != null) {
				tmp.setWidth(this.iconWidth);
				tmp.setHeight(this.iconHeight);
				tmp.setAlignment("top");
				this.icons[i] = tmp;
			}
		}
	}

	public void initIcons(IWContext iwc) {
		IWBundle bundle = getBundle(iwc);
		if (this._showTreeIcons) {
			for (int i = 0; i < this.icons.length; i++) {
				if (this.icons[i] == null) {
					this.icons[i] = bundle.getImage(TREEVIEW_PREFIX + getUI() + this.iconNames[i]);
				}
			}
		} else {
			for (int i = 0; i < this.icons.length; i++) {
				if (this.icons[i] == null) {
					this.icons[i] = Table.getTransparentCell(iwc);
				}
			}
		}
		this._showTreeIcons_changed = false;
		updateIconDimensions();
	}

	public void setToShowTreeIcons(boolean value) {
		this._showTreeIcons = value;
		this._showTreeIcons_changed = true;
	}

	@Override
	public void initializeInMain(IWContext iwc) throws Exception {
		super.initializeInMain(iwc);
		this.addActionListener((IWActionListener) this.getPresentationState(iwc));
		initIcons(iwc);
	}

	public void setUI(String ui) {
		if (ui != null) {
			this._ui = ui + ((ui.endsWith("/")) ? "" : "/");
		} else {
			this._ui = "";
		}
	}

	public String getUI() {
		return this._ui;
	}

	public void drawTree(IWContext iwc) {
		this.empty();
		this.frameTable.empty();
		this.frameTable.resize(1, 1);
		this.add(this.frameTable);
		//frameTable.empty();
		this.treeTableIndex = ((!this._showHeaderRow) ? 1 : 2);
		if (this._showSuperRootNode) {
			drawSuperRoot(iwc);
		}
		if (this.defaultRoot.getChildCount() > 0) {
			drawTree(this.defaultRoot.getChildrenIterator(), null, iwc);
		}

		if (this.lightRowStyle != null && this.darkRowStyle != null) {
			int startRow = ((!this._showHeaderRow) ? 1 : 2);
			int row = 1;
			for (int a = startRow; a <= this.frameTable.getRows(); a++) {
				if (row % 2 != 0) {
					this.frameTable.setRowStyleClass(a, this.lightRowStyle);
				}
				else {
					this.frameTable.setRowStyleClass(a, this.darkRowStyle);
				}
				row++;
			}
		}
	}

	private void drawSuperRoot(IWContext iwc) {
		Table nodeTable = new Table(5, 1);
		nodeTable.setCellpadding(0);
		nodeTable.setCellspacing(0);
		nodeTable.setWidth(Table.HUNDRED_PERCENT);

		nodeTable.setWidth(1, "16");
		nodeTable.setWidth(2, "3");

		if (this._superRootNodeIcon != null) {
			nodeTable.add(this._superRootNodeIcon, 1, 1);
		}
		nodeTable.add(new Text(this._superRootNodeName), 3, 1);

		if(this._refreshTopNodes != null) {
			setEventModelToLink(this._refreshTopNodes);
			nodeTable.setAlignment(5, 1, Table.HORIZONTAL_ALIGN_RIGHT);
			nodeTable.add(this._refreshTopNodes, 5, 1);
		}

		this.frameTable.add(nodeTable, 1, this.getRowIndex());

	}

	@SuppressWarnings("unchecked")
	private synchronized  void drawTree(Iterator<Node> nodes, Image[] collectedIcons, IWContext iwc) {
		if (nodes != null) {
			Iterator<Node> iter = nodes;
			for (int i = 0; iter.hasNext(); i++) {
				Node item = iter.next();
				boolean hasChild = (item.getChildCount() > 0);
				boolean isOpen = false;
				int rowIndex = getRowIndex();
				Table treeColumns = this.getTreeTableClone();
				String anchorName =item.getId();
				treeColumns.add(new Anchor(anchorName), 1, 1);
				if (hasChild) {
					isOpen = this.openNodes.contains(item.getId());
				}
				boolean isRoot = (this.defaultRoot.getIndex(item) >= 0);

				for (int k = 1; k < this._cols; k++) {
					PresentationObject obj = this.getObjectToAddToColumn(k, item, iwc, isOpen, hasChild, isRoot);
					if (obj != null) {
						treeColumns.add(obj, k + 1, 1);
						if (this._nowrap) {
							treeColumns.setNoWrap(k + 1, 1);
						}
					}
				}

				for (int k = 1; k < this._extracols; k++) {
					PresentationObject obj = this.getObjectToAddToParallelExtraColumn(k, item, iwc, isOpen, hasChild, isRoot);
					if (obj != null) {
						if (this._nowrap) {
							treeColumns.setNoWrap(1, 1);
						}
						this.frameTable.add(obj, k + 1, rowIndex);
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
					if (this._nowrap) {
						treeColumns.setNoWrap(1, 1);
					}
					for (int j = 0; j < collectedIconslength; j++) {
						treeColumns.add((Image) collectedIcons[j].clone(), 1, 1);
					}
				}
				Image[] newCollectedIcons = null;
				if (isRoot && !this._showSuperRootNode) {
					if (showRootNodeTreeIcons()) {
						if (i == 0 && !iter.hasNext()) {  //If there is one and only one rootnode
							if (hasChild) {
								if (isOpen) {
									PresentationObject p = null;
									if (this._showTreeIcons) {
										p = getOpenCloseLinkClone((Image) this.icons[ICONINDEX_ROOT_MINUS].clone(), anchorName);
										setLinkToOpenOrCloseNode((Link) p, item, isOpen);
									} else {
										p = (Image) this.icons[ICONINDEX_ROOT_MINUS].clone();
									}
									if (this._nowrap) {
										treeColumns.setNoWrap(1, 1);
									}
									treeColumns.add(p, 1, 1);
									newCollectedIcons = getNewCollectedIconArray(collectedIcons, (Image) this.icons[ICONINDEX_TRANCPARENT].clone());
								} else {
									PresentationObject p = null;
									if (this._showTreeIcons) {
										p = getOpenCloseLinkClone((Image) this.icons[ICONINDEX_ROOT_PLUS].clone(), anchorName);
										setLinkToOpenOrCloseNode((Link) p, item, isOpen);
									} else {
										p = (Image) this.icons[ICONINDEX_ROOT_PLUS].clone();
									}
									if (this._nowrap) {
										treeColumns.setNoWrap(1, 1);
									}
									treeColumns.add(p, 1, 1);
								}
							} else {
								treeColumns.add((Image) this.icons[ICONINDEX_ROOT_LINE].clone(), 1, 1);
								//newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
							}
						} else {  // if there are more than one rootnotes
							if (i == 0) {  // the first rootnode
								if (hasChild) {
									if (isOpen) {
										PresentationObject p = null;
										if (this._showTreeIcons) {
											p = getOpenCloseLinkClone((Image) this.icons[ICONINDEX_F_MINUS].clone(), anchorName);
											setLinkToOpenOrCloseNode((Link) p, item, isOpen);
										} else {
											p = (Image) this.icons[ICONINDEX_F_MINUS].clone();
										}
										treeColumns.add(p, 1, 1);
										newCollectedIcons = getNewCollectedIconArray(collectedIcons, (Image) this.icons[ICONINDEX_LINE].clone());
									} else {
										PresentationObject p = null;
										if (this._showTreeIcons) {
											p = getOpenCloseLinkClone((Image) this.icons[ICONINDEX_F_PLUS].clone(), anchorName);
											setLinkToOpenOrCloseNode((Link) p, item, isOpen);
										} else {
											p = (Image) this.icons[ICONINDEX_F_PLUS].clone();
										}
										if (this._nowrap) {
											treeColumns.setNoWrap(1, 1);
										}
										treeColumns.add(p, 1, 1);
									}
								} else {
									if (this._nowrap) {
										treeColumns.setNoWrap(1, 1);
									}
									treeColumns.add((Image) this.icons[ICONINDEX_F_LINE].clone(), 1, 1);
									//newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
								}
							} else if(!iter.hasNext()){ // the last rootnode
								if (hasChild) {  //if this node has a child
									if (isOpen) { // if this node is open
										PresentationObject p = null;
										if (this._showTreeIcons) {
											p = getOpenCloseLinkClone((Image) this.icons[ICONINDEX_L_MINUS].clone(), anchorName);
											setLinkToOpenOrCloseNode((Link) p, item, isOpen);
										} else {
											p = (Image) this.icons[ICONINDEX_L_MINUS].clone();
										}
										if (this._nowrap) {
											treeColumns.setNoWrap(1, 1);
										}
										treeColumns.add(p, 1, 1);
										newCollectedIcons = getNewCollectedIconArray(collectedIcons, (Image) this.icons[ICONINDEX_TRANCPARENT].clone());
									} else { // if this node is closed
										PresentationObject p = null;
										if (this._showTreeIcons) {
											p = getOpenCloseLinkClone((Image) this.icons[ICONINDEX_L_PLUS].clone(), anchorName);
											setLinkToOpenOrCloseNode((Link) p, item, isOpen);
										} else {
											p = (Image) this.icons[ICONINDEX_L_PLUS].clone();
										}
										if (this._nowrap) {
											treeColumns.setNoWrap(1, 1);
										}
										treeColumns.add(p, 1, 1);
									}
								} else {  //if this node does not have any children
									if (this._nowrap) {
										treeColumns.setNoWrap(1, 1);
									}
									treeColumns.add((Image) this.icons[ICONINDEX_L_LINE].clone(), 1, 1);
									//newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
								}

							}else { //all but first and last rootnodes
								if (hasChild) { // if this node has a child
									if (isOpen) { // if this node is open
										PresentationObject p = null;
										if (this._showTreeIcons) {
											p = getOpenCloseLinkClone((Image) this.icons[ICONINDEX_M_MINUS].clone(), anchorName);
											setLinkToOpenOrCloseNode((Link) p, item, isOpen);
										} else {
											p = (Image) this.icons[ICONINDEX_M_MINUS].clone();
										}
										if (this._nowrap) {
											treeColumns.setNoWrap(1, 1);
										}
										treeColumns.add(p, 1, 1);
										newCollectedIcons = getNewCollectedIconArray(collectedIcons, (Image) this.icons[ICONINDEX_LINE].clone());
									} else { // if this node is closed
										PresentationObject p = null;
										if (this._showTreeIcons) {
											p = getOpenCloseLinkClone((Image) this.icons[ICONINDEX_M_PLUS].clone(), anchorName);
											setLinkToOpenOrCloseNode((Link) p, item, isOpen);
										} else {
											p = (Image) this.icons[ICONINDEX_M_PLUS].clone();
										}
										if (this._nowrap) {
											treeColumns.setNoWrap(1, 1);
										}
										treeColumns.add(p, 1, 1);
									}
								} else { // if this node does not have any children
									if (this._nowrap) {
										treeColumns.setNoWrap(1, 1);
									}
									treeColumns.add((Image) this.icons[ICONINDEX_M_LINE].clone(), 1, 1);
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
								if (this._showTreeIcons) {
									p = getOpenCloseLinkClone((Image) this.icons[ICONINDEX_L_MINUS].clone(), anchorName);
									setLinkToOpenOrCloseNode((Link) p, item, isOpen);
								} else {
									p = (Image) this.icons[ICONINDEX_L_MINUS].clone();
								}
								if (this._nowrap) {
									treeColumns.setNoWrap(1, 1);
								}
								treeColumns.add(p, 1, 1);
								newCollectedIcons = getNewCollectedIconArray(collectedIcons, (Image) this.icons[ICONINDEX_TRANCPARENT].clone());
							} else { // if this node is closed
								PresentationObject p = null;
								if (this._showTreeIcons) {
									p = getOpenCloseLinkClone((Image) this.icons[ICONINDEX_L_PLUS].clone(), anchorName);
									setLinkToOpenOrCloseNode((Link) p, item, isOpen);
								} else {
									p = (Image) this.icons[ICONINDEX_L_PLUS].clone();
								}
								if (this._nowrap) {
									treeColumns.setNoWrap(1, 1);
								}
								treeColumns.add(p, 1, 1);
							}
						} else {  //if this node does not have any children
							if (this._nowrap) {
								treeColumns.setNoWrap(1, 1);
							}
							treeColumns.add((Image) this.icons[ICONINDEX_L_LINE].clone(), 1, 1);
							//newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
						}
					} else {  // if this is not the last node
						if (hasChild) { // if this node has a child
							if (isOpen) { // if this node is open
								PresentationObject p = null;
								if (this._showTreeIcons) {
									p = getOpenCloseLinkClone((Image) this.icons[ICONINDEX_M_MINUS].clone(), anchorName);
									setLinkToOpenOrCloseNode((Link) p, item, isOpen);
								} else {
									p = (Image) this.icons[ICONINDEX_M_MINUS].clone();
								}
								if (this._nowrap) {
									treeColumns.setNoWrap(1, 1);
								}
								treeColumns.add(p, 1, 1);
								newCollectedIcons = getNewCollectedIconArray(collectedIcons, (Image) this.icons[ICONINDEX_LINE].clone());
							} else { // if this node is closed
								PresentationObject p = null;
								if (this._showTreeIcons) {
									p = getOpenCloseLinkClone((Image) this.icons[ICONINDEX_M_PLUS].clone(), anchorName);
									setLinkToOpenOrCloseNode((Link) p, item, isOpen);
								} else {
									p = (Image) this.icons[ICONINDEX_M_PLUS].clone();
								}
								if (this._nowrap) {
									treeColumns.setNoWrap(1, 1);
								}
								treeColumns.add(p, 1, 1);
							}
						} else { // if this node does not have any children
							if (this._nowrap) {
								treeColumns.setNoWrap(1, 1);
							}
							treeColumns.add((Image) this.icons[ICONINDEX_M_LINE].clone(), 1, 1);
							//newCollectedIcons = getNewCollectedIconArray(collectedIcons,icons[ICONINDEX_TRANCPARENT]);
						}
					}
				}

				this.frameTable.add(treeColumns, 1, rowIndex);

				if (hasChild && isOpen) {
					Collection<Node> children = (Collection<Node>) item.getChildren();
				    if (item instanceof GroupTreeNode && children instanceof List) {
				    	Collections.sort(((List<GroupTreeNode>) children), new GroupTreeComparator(iwc));
				    }
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
		return this.treeTableIndex++;
	}

	public void setInitOpenLevel() {
		Iterator<Node> iter = this.defaultRoot.getChildrenIterator();
		if (this.defaultOpenLevel > 0) {
			setInitOpenLevel(iter, 1);
		}
	}

	private void setInitOpenLevel(Iterator<Node> iter, int level) {
		if (iter != null) {
			while (iter.hasNext()) {
				Node node = iter.next();
				String item = node.getId();
				if (!this.openNodes.contains(item)) {
					this.openNodes.add(item);
				}
				if (level < this.defaultOpenLevel) {
					@SuppressWarnings("unchecked")
					Iterator<Node> childrenIter = (Iterator<Node>) node.getChildrenIterator();
					setInitOpenLevel(childrenIter, level + 1);
				}
			}
		}
	}

	public void setOpenNodes(List<String> openNodeList) {
		this.openNodes = openNodeList;
	}

	public void updateOpenNodes(IWContext iwc) {
		TreeViewerPS state = (TreeViewerPS) this.getPresentationState(iwc);

		this.setOpenNodes(state.getOpenNodeList());
		this.lastNode = state.getLastOpenedOrClosedNode();
		state.resetLastOpenedOrClosedNode();

		if (state.setToInitOpenLevel()) {
			this.setInitOpenLevel();
		}
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		if (this._showTreeIcons_changed) {
			initIcons(iwc);
		}
		updateOpenNodes(iwc);
		drawTree(iwc);
		// Safari is not included because it already works (fluke ?), and will cause an endless reload loop, because location.href
		// causes a reload of the page and not just jumping to the anchor
		if (this.lastNode != null && !iwc.isSafari()) {
			getParentPage().setOnLoad("location.href='#"+this.lastNode+"'");

		}
		setHeadings();
		setAlignments();
	}

	private void setAlignments() {
		if (this._extraHorizontal != null) {
			for (Iterator<Integer> iter = this._extraHorizontal.keySet().iterator(); iter.hasNext();) {
				Integer column = iter.next();
				String alignment = this._extraHorizontal.get(column);
				this.frameTable.setColumnAlignment(column.intValue(), alignment);
			}
		}

		if (this._extraVertical != null) {
			for (Iterator<Integer> iter = this._extraVertical.keySet().iterator(); iter.hasNext();) {
				Integer column = iter.next();
				String alignment = this._extraVertical.get(column);
				this.frameTable.setColumnVerticalAlignment(column.intValue(), alignment);
			}
		}
	}

	private void setHeadings() {
		if (this._extraHeadings != null) {
			for (Iterator<Integer> iter = this._extraHeadings.keySet().iterator(); iter.hasNext();) {
				Integer column = iter.next();
				PresentationObject object = this._extraHeadings.get(column);
				this.frameTable.add(object, column.intValue(), 1);
			}
		}

		if (this._extraWidths != null) {
			Iterator<Integer> iter = this._extraWidths.keySet().iterator();
			while (iter.hasNext()) {
				Integer column = iter.next();
				String width = this._extraWidths.get(column);
				this.frameTable.setWidth(column.intValue(), width);
			}
		}

		if (this._headingColor != null) {
			this.frameTable.setRowColor(1, this._headingColor);
		}
	}

	public abstract PresentationObject getObjectToAddToColumn(
			int colIndex,
			Node node,
			IWContext iwc,
			boolean nodeIsOpen,
			boolean nodeHasChild,
			boolean isRootNode
	);

	public PresentationObject getObjectToAddToParallelExtraColumn(
			int colIndex,
			Node node,
			IWContext iwc,
			boolean nodeIsOpen,
			boolean nodeHasChild,
			boolean isRootNode
	) {
		return null;
	}

	public void setToShowSuperRootNode(boolean value) {
		this._showSuperRootNode = value;
	}

	public void setSuperRootNodeName(String name) {
		this._superRootNodeName = name;
	}

	public void setSuperRootNodeIcon(Image image) {
		this._superRootNodeIcon = image;
	}

	public void setRefreshLink(Link refreshTN) {
		this._refreshTopNodes = refreshTN;
	}

	public Link getRefreshLink() {
		return this._refreshTopNodes;
	}

	public void setRootNode(Node root) {
		this.defaultRoot.clear();
		this.defaultRoot.addTreeNode(root);
	}

	public void setToShowRootNode(boolean value) {
		this.showRootNode = value;
	}

	public boolean showRootNodeTreeIcons() {
		return this.showRootNodeTreeIcons;
	}

	public void setToShowHeaderRow(boolean showRow) {
		this._showHeaderRow = showRow;
	}

	public void setToShowRootNodeTreeIcons(boolean value) {
		this.showRootNodeTreeIcons = value;
	}

	public void setFirstLevelNodes(Node[] nodes) {
		this.defaultRoot.clear();
		if (nodes != null) {
			for (int i = 0; i < nodes.length; i++) {
				this.defaultRoot.addTreeNode(nodes[i]);
			}
		}
	}

	public void setFirstLevelNodes(Iterator<Node> nodes) {
		this.defaultRoot.clear();
		if (nodes != null) {
			while (nodes.hasNext()) {
				Node node = nodes.next();
				this.defaultRoot.addTreeNode(node);
			}
		}
	}

	public void addFirstLevelNode(Node node) {
		if (node != null) {
			this.defaultRoot.addTreeNode(node);
		}
	}

	public void setToMaintainParameter(String parameterName, IWContext iwc) {
		this.openCloseLink.maintainParameter(parameterName, iwc);
	}

	public void addOpenCloseParameter(String name,String value){
		this.openCloseLink.addParameter(name,value);
	}

	protected Link getOpenCloseLinkClone(PresentationObject obj, String anchorName) {
		AnchorLink l = (AnchorLink) this.openCloseLink.clone();
		l.setObject(obj);
		if (anchorName != null) {
			l.addCurrentURLToLink(true);
			l.setAnchorName(anchorName);
		}
		return l;
	}

	protected Link getOpenCloseLinkClone(Image obj, String anchorName) {
		AnchorLink l = (AnchorLink) this.openCloseLink.clone();
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
		if (this._eventModel == null) {
			this._eventModel = new TreeViewerEvent();
			/*if (this.getICObjectInstanceID() > 0) {
				_eventModel.setSource(this.getICObjectInstanceID());
			} else {*/
				//_eventModel.setSource(this.getLocation());
				this._eventModel.setSource(this);
      //}
		}
		return this._eventModel;
	}

	public Link setLinkToOpenOrCloseNode(Link l, Node node, boolean nodeIsOpen) {
		TreeViewerEvent event = (TreeViewerEvent) getOpenCloseEventModel().clone();
		if (nodeIsOpen) {
			event.setToCloseNode(node);
		} else {
			event.setToOpenNode(node);
		}

		l.addEventModel(event);
		if (!StringUtil.isEmpty(closeOrOpenNodesHref)) {
			l.setURL(closeOrOpenNodesHref);
		}
		return l;
	}

	public Link setEventModelToLink(Link l) {
		TreeViewerEvent event = (TreeViewerEvent) getOpenCloseEventModel().clone();
		event.setToTreeStateChanged(l);
		l.addEventModel(event);
		return l;
	}

	public void setTreeColumns(int cols) {
		this._cols = cols + 1;
		this.treeTable.resize(this._cols, this.frameTable.getRows());
	}

	public void setTreePadding(int padding) {
		this.frameTable.setCellpadding(padding);
	}

	public void setParallelExtraColumns(int cols) {
		this._extracols = cols + 1;
		this.frameTable.resize(this._extracols, this.frameTable.getRows());
	}

	public void setColumns(int cols) {
		setTreeColumns(cols);
	}

	public void setExtraColumnWidth(int col, String width) {
		if (this._extraWidths == null) {
			this._extraWidths = new HashMap<Integer, String>();
		}
		this._extraWidths.put(new Integer(col + 1), width);
	}

	public void setTreeColumnWidth(int col, String width) {
		this.treeTable.setWidth(col + 1, width);
	}

	@Override
	public void setWidth(String s) {
		this.frameTable.setWidth(s);
	}

	public void setExtraColumnHorizontalAlignment(int col, String alignment) {
		if (this._extraHorizontal == null) {
			this._extraHorizontal = new HashMap<Integer, String>();
		}
		this._extraHorizontal.put(new Integer(col + 1), alignment);
	}

	public void setExtraColumnVerticalAlignment(int col, String alignment) {
		if (this._extraVertical == null) {
			this._extraVertical = new HashMap<Integer, String>();
		}
		this._extraVertical.put(new Integer(col + 1), alignment);
	}

	public void setExtraColumnHeading(int col, PresentationObject obj) {
		if (this._extraHeadings == null) {
			this._extraHeadings = new HashMap<Integer, PresentationObject>();
		}
		this._extraHeadings.put(new Integer(col + 1), obj);
	}

	public void setTreeHeading(int col, PresentationObject obj) {
		setExtraColumnHeading(col-1, obj);
	}

	public void setHeadingColor(String color) {
		this._headingColor = color;
	}

	public void setExtraHeadingColor(int col, String color) {
		setHeadingColor(color);
	}

	public void setDefaultOpenLevel(int value) {
		this.defaultOpenLevel = value;
	}

	public int getDefaultOpenLevel() {
		return this.defaultOpenLevel;
	}

	public void setNestLevelAtOpen(int nodesDown) {
		setDefaultOpenLevel(nodesDown);
	}

	public int getNestLevelAtOpen() {
		return getDefaultOpenLevel();
	}

	public void setHeaderRowHeight(String height) {
		this.frameTable.setHeight(1, height);
	}

	@Override
	public Class<? extends IWPresentationState> getPresentationStateClass() {
		return this._stateHandler.getPresentationStateClass();
	}

	@Override
	public IWPresentationState getPresentationState(IWUserContext iwuc) {
		return this._stateHandler.getPresentationState(this, iwuc);
	}

	public StatefullPresentationImplHandler getStateHandler() {
		return this._stateHandler;
	}

	public void addEventModel(IWPresentationEvent model) {
		this.openCloseLink.addEventModel(model);
	}

	public void setOpenCloseLinkTarget(String target) {

		this.openCloseLink.setTarget(target);
	}

	/**
	 * Sets the nowrap.
	 * @param nowrap The nowrap to set
	 */
	public void setNowrap(boolean nowrap) {
		this._nowrap = nowrap;
	}

	public String getCloseOrOpenNodesHref() {
		return closeOrOpenNodesHref;
	}

	public void setCloseOrOpenNodesHref(String closeOrOpenNodesHref) {
		this.closeOrOpenNodesHref = closeOrOpenNodesHref;
	}

}