package com.idega.presentation.ui;

import java.rmi.RemoteException;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.data.ICTreeNode;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;
import com.idega.presentation.text.Link;

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
	private boolean _nowrap = true;
	private Layer _nowrapLayer = null;
	private int _maxNodeNameLength = -1;
	private String onNodeClickEvent = null;

	public static final String ONCLICK_FUNCTION_NAME = "treenodeselect";
	public static final String ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME = "iw_node_id";
	public static final String ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME = "iw_node_name";

	public TreeViewer() {
		super();
		folderAndFileIcons = new Image[3];
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
		for (int i = 0; i < folderAndFileIcons.length; i++) {
			Image tmp = folderAndFileIcons[i];
			if (tmp != null) {
				//tmp.setWidth(iconWidth);
				tmp.setHeight(iconHeight);
				//tmp.setAlignment("top");
				folderAndFileIcons[i] = tmp;
			}
		}
	}

	public void initIcons(IWContext iwc) {
		super.initIcons(iwc);

		IWBundle bundle = getBundle(iwc);
		for (int i = 0; i < folderAndFileIcons.length; i++) {
			if (folderAndFileIcons[i] == null) {
				folderAndFileIcons[i] = bundle.getImage(TREEVIEW_PREFIX + getUI() + folderAndFileIconNames[i]);
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
		if (_maxNodeNameLength > 0) {
			if (nodeName.length() > _maxNodeNameLength) {
				nodeName = nodeName.substring(0,_maxNodeNameLength-3) + "...";
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
		if(!getShortenedNodeName(nodeName))
			titleName = null;
		/*	
		if (_maxNodeNameLength > 0) {
			if (nodeName.length() > _maxNodeNameLength) {
				titleName = nodeName;
				nodeName = nodeName.substring(0,_maxNodeNameLength-3) + "...";
			} 
		}
		*/
		
		Link l = getLinkPrototypeClone(nodeName);
		if (titleName != null)
			l.setMarkupAttribute("title",titleName);

		if (onNodeClickEvent != null) {
			BuilderService bservice;
			// Currently a bit of a crap hack
			try
			{
				bservice = getBuilderService(iwc);
				l.setOnClick(onNodeClickEvent+"('"+bservice.getPageURI(node.getNodeID())+"');return false;");
			}
			catch (RemoteException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (_usesOnClick) {
			l.setURL("#");
			if (fromEditor){
				BuilderService bservice;
				try
				{
					bservice = getBuilderService(iwc);
					l.setOnClick("save('http://" + iwc.getServerName() + bservice.getPageURI(node.getNodeID()) + "','_self')");
				}
				catch (RemoteException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				l.setOnClick(ONCLICK_FUNCTION_NAME + "('" + nodeName + "','" + node.getNodeID() + "')");
			}
		}
		else if (nodeActionPrm != null) {
			l.addParameter(nodeActionPrm, node.getNodeID());
		}
		setLinkToMaintainOpenAndClosedNodes(l);
		return l;
	}

	public PresentationObject getFirstColumnObject(ICTreeNode node, boolean nodesOpen, boolean isRootNode) {
		if (!node.isLeaf()) {
			if (nodesOpen) {
				if (isRootNode && !showRootNodeTreeIcons()) {
					//Link l = new Link();
					Link l = getLinkOpenClosePrototype();
					l.setImage(folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FOLDER_OPEN]);
					if (!nodesOpen) {
						setLinkToOpenOrCloseNode(l, node, nodesOpen);
					}
					return l;
				}
				else {
					return folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FOLDER_OPEN];
				}
			}
			else {
				if (isRootNode && !showRootNodeTreeIcons()) {
					//Link l = new Link();
					Link l = getLinkOpenClosePrototype();
					l.setImage(folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FOLDER_CLOSED]);
					setLinkToOpenOrCloseNode(l, node, nodesOpen);
					return l;
				}
				else {
					return folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FOLDER_CLOSED];
				}
			}
		}
		else {
			if (isRootNode && !showRootNodeTreeIcons()) {
				//Link l = new Link();
				Link l = getLinkOpenClosePrototype();
				l.setImage(folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FILE]);
				setLinkToOpenOrCloseNode(l, node, nodesOpen);
				return l;
			}
			else {
				return folderAndFileIcons[FOLDERANDFILE_ICONINDEX_FILE];
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
		nodeActionPrm = prm;
	}

	public void setTarget(String target) {
		nodeNameTarget = target;
	}

	public void setTreeStyle(String style) {
		_linkStyle = style;
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
		_linkPrototype = link;
	}
	
	private Link getLinkOpenClosePrototype() {
		if (_linkOpenClosePrototype == null) {
			_linkOpenClosePrototype = new Link();
		}
		return _linkOpenClosePrototype;
	}
	
	public void setLinkOpenClosePrototype(Link link) {
			_linkOpenClosePrototype = link;
	}
	
	private Link getLinkOpenClosePrototypeClone() {
			return (Link) getLinkOpenClosePrototype().clone();
	}

	private Link getLinkPrototype() {
		if (_linkPrototype == null) {
			_linkPrototype = new Link();
		}

		if (nodeNameTarget != null) {
			_linkPrototype.setTarget(nodeNameTarget);
		}

		if (_linkStyle != null) {
			_linkPrototype.setFontStyle(_linkStyle);
		}

		return _linkPrototype;
	}

	public Layer getNoWrapLayer() {
		if (_nowrapLayer == null) {
			_nowrapLayer = new Layer();
			_nowrapLayer.setNoWrap();
		}
		return _nowrapLayer;
	}

	private Link getLinkPrototypeClone() {
		return (Link) getLinkPrototype().clone();
	}

	private Link getLinkPrototypeClone(String text) {
		Link l = (Link) getLinkPrototype().clone();
		l.setText(text);
		return l;
	}

	private Layer getNoWrapLayerClone() {
		Layer l = (Layer) getNoWrapLayer().clone();
		return l;
	}

	private Layer getNoWrapLayerClone(PresentationObject obj) {
		Layer l = getNoWrapLayerClone();
		l.add(obj);
		return l;
	}

	private Link getLinkPrototypeClone(Image image) {
		Link l = (Link) getLinkPrototype().clone();
		l.setImage(image);
		return l;
	}

	public void setToUseOnClick() {
		setToUseOnClick(ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME, ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME);
	}

	public void setToUseOnClick(String NodeNameParameterName, String NodeIDParameterName) {
		_usesOnClick = true;
		getAssociatedScript().addFunction(ONCLICK_FUNCTION_NAME, "function " + ONCLICK_FUNCTION_NAME + "(" + NodeNameParameterName + "," + NodeIDParameterName + "){ }");

	}

	public void setOnClick(String action) {
		getAssociatedScript().addToFunction(ONCLICK_FUNCTION_NAME, action);
	}
	
	public void setOnNodeClickEvent(String event) {
		this.onNodeClickEvent = event;
	}
	
	public void setMaxNodeNameLength(int length) {
		_maxNodeNameLength = length;
	}
	
	public int getMaxNodeNameLength() {
		return _maxNodeNameLength;
	}
	
	public void setToMaintainParameter(String parameterName, IWContext iwc) {
		getLinkOpenClosePrototype().maintainParameter(parameterName, iwc);
		super.setToMaintainParameter(parameterName, iwc);
	}
}