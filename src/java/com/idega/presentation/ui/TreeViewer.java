package com.idega.presentation.ui;

import java.rmi.RemoteException;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.data.ICTreeNode;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.util.AbstractChooserBlock;
import com.idega.util.StringUtil;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class TreeViewer extends AbstractTreeViewer {

	private static final String TREEVIEW_PREFIX = "treeviewer/ui/";

	Image folderAndFileIcons[] = null;
	String folderAndFileIconNames[] = { "treeviewer_node_closed.gif", "treeviewer_node_open.gif", "treeviewer_node_leaf.gif" };

	private static final int FOLDERANDFILE_ICONINDEX_FOLDER_CLOSED = 0;
	private static final int FOLDERANDFILE_ICONINDEX_FOLDER_OPEN = 1;
	private static final int FOLDERANDFILE_ICONINDEX_FILE = 2;

	public static final String PRM_OPEN_TREENODES = "ic_opn_trnds";
	public static final String PRM_TREENODE_TO_CLOSE = "ic_cls_trnd";

	String nodeNameTarget = null;
	String nodeActionPrm = null;
	Link _linkPrototype = null;
	Link _linkOpenClosePrototype = null;
	String _linkStyle = null;
	boolean _usesOnClick = false;
	private Layer _nowrapLayer = null;
	private int _maxNodeNameLength = -1;
	private String onNodeClickEvent = null;

	public static final String ONCLICK_FUNCTION_NAME = "treenodeselect";
	public static final String ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME = "iw_node_id";
	public static final String ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME = "iw_node_name";
	
	private boolean addPageNameAttribute = false;
	private boolean addPageIdAtribute = false;

	public TreeViewer() {
		super();
		this.folderAndFileIcons = new Image[3];
		setColumns(2);
		setTreeColumnWidth(1, "16");
		setWrap(false);
	}

	public static TreeViewer getTreeViewerInstance(ICTreeNode node, IWContext iwc) {
		TreeViewer viewer = new TreeViewer();
		viewer.setRootNode(node);
		return viewer;
	}

	protected void updateIconDimensions() {
		super.updateIconDimensions();
		for (int i = 0; i < this.folderAndFileIcons.length; i++) {
			Image tmp = this.folderAndFileIcons[i];
			if (tmp != null) {
				//tmp.setWidth(iconWidth);
				tmp.setHeight(this.iconHeight);
				//tmp.setAlignment("top");
				this.folderAndFileIcons[i] = tmp;
			}
		}
	}

	public void initIcons(IWContext iwc) {
		super.initIcons(iwc);

		IWBundle bundle = getBundle(iwc);
		for (int i = 0; i < this.folderAndFileIcons.length; i++) {
			if (this.folderAndFileIcons[i] == null) {
				this.folderAndFileIcons[i] = bundle.getImage(TREEVIEW_PREFIX + getUI() + this.folderAndFileIconNames[i]);
			}
		}

		updateIconDimensions();
	}

	private void addScript() {
		Script script = getParentPage().getAssociatedScript();
		script.addFunction("save", "function save(URL,target) {   args['href']=URL; args['target']=target; window.returnValue = args; window.close(); }");
		getParentPage().setAssociatedScript(script);
	}

	public PresentationObject getObjectToAddToColumn(int colIndex, ICTreeNode node, IWContext iwc, boolean nodesOpen, boolean nodeHasChild, boolean isRootNode) {
		boolean fromEditor = false;
		if (iwc.isParameterSet("from_editor")) {
			fromEditor = true;
			addScript();
		}

		switch (colIndex) {
			case 1 :
				return getFirstColumnObject(node, nodesOpen, isRootNode);
			case 2 :
				return getSecondColumnObject(node, iwc, fromEditor);
		}
		return null;
	}
	
	public boolean getShortenedNodeName(String nodeName){
		if (this._maxNodeNameLength > 0) {
			if (nodeName.length() > this._maxNodeNameLength) {
				nodeName = nodeName.substring(0,this._maxNodeNameLength-3) + "...";
				return true;
			} 
		}
		return false;
	}

	public PresentationObject getSecondColumnObject(ICTreeNode node, IWContext iwc, boolean fromEditor) {
		String nodeName = null;
		String titleName = null;
		//if (node instanceof ICTreeNode)
		//	nodeName = ((PageTreeNode) node).getLocalizedNodeName(iwc);
		//else
			nodeName = node.getNodeName(iwc.getCurrentLocale(),iwc);
			
		titleName = nodeName;
		if(!getShortenedNodeName(nodeName)) {
			titleName = null;
		/*	
		if (_maxNodeNameLength > 0) {
			if (nodeName.length() > _maxNodeNameLength) {
				titleName = nodeName;
				nodeName = nodeName.substring(0,_maxNodeNameLength-3) + "...";
			} 
		}
		*/
		}
		
		Link l = getLinkPrototypeClone(nodeName);
		if (titleName != null) {
			l.setMarkupAttribute("title",titleName);
		}

		if (this.onNodeClickEvent != null) {
			BuilderService bservice;
			// Currently a bit of a crap hack
			try
			{
				bservice = getBuilderService(iwc);
				l.setOnClick(this.onNodeClickEvent+"('"+bservice.getPageURI(node.getId())+"');return false;");
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}
		
		if (this._usesOnClick) {
			l.setURL("#");
			if (fromEditor){
				BuilderService bservice;
				try
				{
					bservice = getBuilderService(iwc);
					l.setOnClick("save('http://" + iwc.getServerName() + bservice.getPageURI(node.getId()) + "','_self')");
				}
				catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
			else{
				l.setOnClick(ONCLICK_FUNCTION_NAME + "('" + nodeName + "','" + node.getId() + "')");
			}
		}
		else if (this.nodeActionPrm != null) {
			l.addParameter(this.nodeActionPrm, node.getId());
		}
		setLinkToMaintainOpenAndClosedNodes(l);
		
		if (isAddPageNameAttribute()) {
			l.setMarkupAttribute(ICBuilderConstants.PAGE_NAME_ATTRIBUTE, nodeName);
		}
		
		if (isAddPageIdAtribute()) {
			l.setMarkupAttribute(ICBuilderConstants.PAGE_ID_ATTRIBUTE, node.getId());
		}
		
		return l;
	}

	public PresentationObject getFirstColumnObject(ICTreeNode node, boolean nodesOpen, boolean isRootNode) {
		if (node.isLeaf()) {
			if (isRootNode && !showRootNodeTreeIcons()) {
				Link l = getLinkOpenClosePrototype();
				l.setImage((Image) this.folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FILE].clone());
				setLinkToOpenOrCloseNode(l, node, nodesOpen);
				return l;
			}
			return (Image) this.folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FILE].clone();
		}
		else {
			if (nodesOpen) {
				if (isRootNode && !showRootNodeTreeIcons()) {
					Link l = getLinkOpenClosePrototype();
					l.setImage((Image) this.folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FOLDER_OPEN].clone());
					if (!nodesOpen) {
						setLinkToOpenOrCloseNode(l, node, nodesOpen);
					}
					return l;
				}
				return (Image) this.folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FOLDER_OPEN].clone();
			}
			else {
				if (isRootNode && !showRootNodeTreeIcons()) {
					Link l = getLinkOpenClosePrototype();
					l.setImage((Image) this.folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FOLDER_CLOSED].clone());
					setLinkToOpenOrCloseNode(l, node, nodesOpen);
					return l;
				}
				return (Image) this.folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FOLDER_CLOSED].clone();
			}
		}
	}

	public void setWrap() {
		setWrap(true);
	}

	public void setWrap(boolean value) {
		super.setNowrap(!value);
	}

	public void setNodeActionParameter(String prm) {
		this.nodeActionPrm = prm;
	}

	public void setTarget(String target) {
		this.nodeNameTarget = target;
	}

	public void setTreeStyle(String style) {
		this._linkStyle = style;
	}

	/** Note: Do not forget to set the desired parameter 
	 * in  <code>addOpenCloseParameter(String,String)</code>.
	 * Example:
	 * Link myLink = new Link();
	 * myLink.addParameter("hello", "world");
	 * tree.setLinkPrototype(myLink);
	 * tree.addOpenCloseParameter("hello", "world") 
	 */
	// above comment added by Thomas
	public void setLinkPrototype(Link link) {
		this._linkPrototype = link;
	}
	
	private Link getLinkOpenClosePrototype() {
		if (this._linkOpenClosePrototype == null) {
			this._linkOpenClosePrototype = new Link();
		}
		return (Link) this._linkOpenClosePrototype.clone();
	}
	
	public void setLinkOpenClosePrototype(Link link) {
			this._linkOpenClosePrototype = link;
	}
	
	private Link getLinkPrototype() {
		if (this._linkPrototype == null) {
			this._linkPrototype = new Link();
		}

		if (this.nodeNameTarget != null) {
			this._linkPrototype.setTarget(this.nodeNameTarget);
		}

		if (this._linkStyle != null) {
			this._linkPrototype.setFontStyle(this._linkStyle);
		}

		Link clonedLink = (Link) this._linkPrototype.clone();
		String onClickAction = clonedLink.getOnClick();
		if (!StringUtil.isEmpty(onClickAction)) {
			clonedLink.setMarkupAttribute("onclick", AbstractChooserBlock.getNormalizedAction(onClickAction, clonedLink.getId()));
		}
		return clonedLink;
	}

	public Layer getNoWrapLayer() {
		if (this._nowrapLayer == null) {
			this._nowrapLayer = new Layer();
			this._nowrapLayer.setNoWrap();
		}
		return this._nowrapLayer;
	}

	private Link getLinkPrototypeClone(String text) {
		Link l = getLinkPrototype();
		l.setText(text);
		return l;
	}

	public void setToUseOnClick() {
		setToUseOnClick(ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME, ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME);
	}

	public void setToUseOnClick(String NodeNameParameterName, String NodeIDParameterName) {
		this._usesOnClick = true;
		getAssociatedScript().addFunction(ONCLICK_FUNCTION_NAME, "function " + ONCLICK_FUNCTION_NAME + "(" + NodeNameParameterName + "," + NodeIDParameterName + "){ }");

	}

	public void setOnClick(String action) {
		getAssociatedScript().addToFunction(ONCLICK_FUNCTION_NAME, action);
	}
	
	public void setOnNodeClickEvent(String event) {
		this.onNodeClickEvent = event;
	}
	
	public void setMaxNodeNameLength(int length) {
		this._maxNodeNameLength = length;
	}
	
	public int getMaxNodeNameLength() {
		return this._maxNodeNameLength;
	}
	
	public void setToMaintainParameter(String parameterName, IWContext iwc) {
		getLinkOpenClosePrototype().maintainParameter(parameterName, iwc);
		super.setToMaintainParameter(parameterName, iwc);
	}

	public boolean isAddPageIdAtribute() {
		return addPageIdAtribute;
	}

	public void setAddPageIdAtribute(boolean addPageIdAtribute) {
		this.addPageIdAtribute = addPageIdAtribute;
	}

	public boolean isAddPageNameAttribute() {
		return addPageNameAttribute;
	}

	public void setAddPageNameAttribute(boolean addPageNameAttribute) {
		this.addPageNameAttribute = addPageNameAttribute;
	}
	
}