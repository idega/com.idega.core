package com.idega.core.data;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Locale;

import com.idega.idegaweb.IWApplicationContext;
/**
 * 
 * <p>Company: idegaweb </p>
 * @author aron
 * 
 *
 */
public class IWTreeNode implements ICTreeNodeAddable {
  
  protected static final String PATH_DELIMITER = "#"; 
	
	ICTreeNode parent = null;
	ArrayList childs = null;
	String Name = "";
	//Object object = null;
	String path =null;
	int ID = -1;
	static int internalID = 1;
	
	public IWTreeNode(String name){
		this(name,internalID++);
	}
	
	public IWTreeNode(String name,int ID){
		this(name,ID,null);
	}
	
	public IWTreeNode(String name,int ID,ICTreeNodeAddable parent){
			childs = new ArrayList();
			this.Name = name;
			//this.object = object;
			this.ID = ID;
			if(parent!=null)
				parent.addChild(this);
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
		path = "root";
		parent = null;
		internalID = 1;
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
	}
	
	public void addChild(String name, int id){
		this.childs.add(new IWTreeNode(name,id));
	}
	/**
	 * @see com.idega.core.ICTreeNode#getChildren()
	 */
	public Collection getChildren() {
		return this.childs;
	}
	/**
	 * @see com.idega.core.ICTreeNode#getChildrenIterator()
	 */
	public Iterator getChildrenIterator() {
	    Iterator it = null;
	    Collection children = getChildren();
	    if (children != null) {
	        it = children.iterator();
	    }
	    return it;
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
		if(path==null)
			path = generatePath(this);
		return path;
	}
	
	private String generatePath(ICTreeNode node){
		if(node.getParentNode()!=null ){
			return  generatePath(node.getParentNode())+ PATH_DELIMITER +node.getNodeName();
		}
		return  node.getNodeName();
	}

	
	/**
	 * @param i
	 */
	public void setNodeID(int i) {
		ID = i;
	}

	/**
	 * @return
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		Name = string;
	}

	/**
	 * @param string
	 */
	public void setNodePath(String string) {
		path = string;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeName(java.util.Locale)
	 */
	public String getNodeName(Locale locale)
	{
		return getNodeName();
	}

	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getNodeName(locale);
	}
}
