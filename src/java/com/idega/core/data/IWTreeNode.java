package com.idega.core.data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.CoreConstants;
/**
 * <p>Company: idegaweb </p>
 * @author aron
 */
public class IWTreeNode implements ICTreeNodeAddable<IWTreeNode> {

	protected static final String PATH_DELIMITER = CoreConstants.HASH;

	ICTreeNode<IWTreeNode> parent = null;
	List<IWTreeNode> childs = null;
	String Name = "";
	//Object object = null;
	String path = null;
	String ID = null;
	static int internalID = 1;

	public IWTreeNode(String name){
		this(name,internalID++);
	}

	public IWTreeNode(String name,int ID){
		this(name,ID,null);
	}

	public IWTreeNode(String name,int iID,ICTreeNodeAddable<IWTreeNode> parent){
		this(name,Integer.toString(iID),parent);
	}

	public IWTreeNode(String name,String sID,ICTreeNodeAddable<IWTreeNode> parent){
			this.childs = new ArrayList<IWTreeNode>();
			this.Name = name;
			//this.object = object;
			//this.ID = ID;
			this.ID=sID;
			if(parent!=null) {
				parent.addChild(this);
			}
	}


	public static IWTreeNode createRootNode(String name , int ID){
		IWTreeNode node = new IWTreeNode(name,ID,null);
		node.setAsRootNode();
		return node;
	}

	public static IWTreeNode createRootNode(String name ){
		IWTreeNode node = new IWTreeNode(name);
		node.setAsRootNode();
		return node;
	}

	public void setAsRootNode(){
		this.path = "root";
		this.parent = null;
		internalID = 1;
	}

	@Override
	public void setParent(ICTreeNode<IWTreeNode> parent){
		this.parent = parent;
	}

	/**
	 * @see com.idega.core.ICTreeNodeAddable#addChild(ICTreeNodeAddable)
	 */
	@Override
	public void addChild(ICTreeNodeAddable<IWTreeNode> child) {
		this.childs.add((IWTreeNode) child);
		child.setParent(this);
	}

	public void addChild(String name, int id){
		this.childs.add(new IWTreeNode(name,id));
	}
	/**
	 * @see com.idega.core.ICTreeNode#getChildren()
	 */
	@Override
	public Collection<IWTreeNode> getChildren() {
		return this.childs;
	}
	/**
	 * @see com.idega.core.ICTreeNode#getChildrenIterator()
	 */
	@Override
	public Iterator<IWTreeNode> getChildrenIterator() {
	    Iterator<IWTreeNode> it = null;
	    Collection<IWTreeNode> children = getChildren();
	    if (children != null) {
	        it = children.iterator();
	    }
	    return it;
	}
	/**
	 * @see com.idega.core.ICTreeNode#getAllowsChildren()
	 */
	@Override
	public boolean getAllowsChildren() {
		return true;
	}
	/**
	 * @see com.idega.core.ICTreeNode#getChildAtIndex(int)
	 */
	@Override
	public IWTreeNode getChildAtIndex(int childIndex) {
		return this.childs.get(childIndex);
	}
	/**
	 * @see com.idega.core.ICTreeNode#getChildCount()
	 */
	@Override
	public int getChildCount() {
		return this.childs.size();
	}
	/**
	 * @see com.idega.core.ICTreeNode#getIndex(ICTreeNode)
	 */
	@Override
	public int getIndex(IWTreeNode node) {
		return this.childs.indexOf(node);
	}
	/**
	 * @see com.idega.core.ICTreeNode#getParentNode()
	 */
	@Override
	public IWTreeNode getParentNode() {
		return (IWTreeNode) this.parent;
	}
	/**
	 * @see com.idega.core.ICTreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return this.childs.isEmpty();
	}
	/**
	 * @see com.idega.core.ICTreeNode#getNodeName()
	 */
	@Override
	public String getNodeName() {
		return this.Name;
	}
	/**
	 * @see com.idega.core.ICTreeNode#getNodeID()
	 */
	@Override
	public int getNodeID() {
		String id=getId();
		if(id==null){
			return -1;
		}
		else{
			return Integer.parseInt(id);
		}
	}

	/**
	 * @see com.idega.core.ICTreeNode#getSiblingCount()
	 */
	@Override
	public int getSiblingCount() {
		if(this.parent!=null) {
			return this.parent.getChildCount()-1;
		}
		return 0;
	}

	public String getNodePath(){
		if(this.path==null) {
			this.path = generatePath(this);
		}
		return this.path;
	}

	private String generatePath(IWTreeNode node){
		if(node.getParentNode()!=null ){
			return  generatePath(node.getParentNode())+ PATH_DELIMITER +node.getNodeName();
		}
		return  node.getNodeName();
	}


	/**
	 * @param i
	 */
	public void setNodeID(int i) {
		this.ID = Integer.toString(i);
	}

	/**
	 * @return
	 */
	public String getName() {
		return this.Name;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		this.Name = string;
	}

	/**
	 * @param string
	 */
	public void setNodePath(String string) {
		this.path = string;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeName(java.util.Locale)
	 */
	@Override
	public String getNodeName(Locale locale)
	{
		return getNodeName();
	}

	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getNodeName(locale);
	}

	/**
	 * @return the number of siblings this node has
	 */
	@Override
	public String getId(){
		return this.ID;
	}

}