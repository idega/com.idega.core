package com.idega.core;
import java.util.Iterator;
import java.util.ArrayList;
/**
 * 
 * <p>Company: idegaweb </p>
 * @author aron
 * 
 *
 */
public class IWTreeNode implements ICTreeNodeAddable {
	
	ICTreeNode parent = null;
	ArrayList childs = null;
	String Name = "";
	String path ="";
	int ID = -1;
	
	public IWTreeNode(String name,int ID){
		this(name,ID,null);
	}
	
	public IWTreeNode(String name,int ID,ICTreeNodeAddable parent){
		childs = new ArrayList();
		this.Name = name;
		this.ID = ID;
		if(parent!=null)
		parent.addChild(this);
	}
	
	public static IWTreeNode createRootNode(String name , int ID){
		IWTreeNode node = new IWTreeNode(name,ID);
		node.path = "root";
		return node;
	}
	
	public void setParent(ICTreeNode parent){
		this.parent = parent;
	}
	
	/**
	 * @see com.idega.core.ICTreeNodeAddable#addChild(ICTreeNodeAddable)
	 */
	public void addChild(ICTreeNodeAddable child) {
		this.childs.add(child);
		child.setParent(this);
		path = this.path + "_" + this.getChildCount() + "#" + child.getNodeID();
	}
	
	public void addChild(String name, int id){
		this.childs.add(new IWTreeNode(name,id));
	}
	/**
	 * @see com.idega.core.ICTreeNode#getChildren()
	 */
	public Iterator getChildren() {
		return this.childs.iterator();
	}
	/**
	 * @see com.idega.core.ICTreeNode#getAllowsChildren()
	 */
	public boolean getAllowsChildren() {
		return true;
	}
	/**
	 * @see com.idega.core.ICTreeNode#getChildAtIndex(int)
	 */
	public ICTreeNode getChildAtIndex(int childIndex) {
		return (ICTreeNode) this.childs.get(childIndex);
	}
	/**
	 * @see com.idega.core.ICTreeNode#getChildCount()
	 */
	public int getChildCount() {
		return this.childs.size();
	}
	/**
	 * @see com.idega.core.ICTreeNode#getIndex(ICTreeNode)
	 */
	public int getIndex(ICTreeNode node) {
		return this.childs.indexOf(node);
	}
	/**
	 * @see com.idega.core.ICTreeNode#getParentNode()
	 */
	public ICTreeNode getParentNode() {
		return parent;
	}
	/**
	 * @see com.idega.core.ICTreeNode#isLeaf()
	 */
	public boolean isLeaf() {
		return childs.isEmpty();
	}
	/**
	 * @see com.idega.core.ICTreeNode#getNodeName()
	 */
	public String getNodeName() {
		return this.Name;
	}
	/**
	 * @see com.idega.core.ICTreeNode#getNodeID()
	 */
	public int getNodeID() {
		return this.ID;
	}
	/**
	 * @see com.idega.core.ICTreeNode#getSiblingCount()
	 */
	public int getSiblingCount() {
		if(parent!=null)
			return parent.getChildCount()-1;
		return 0;
	}
	
	public String getNodePath(){
		return this.path;
	}
}
