/*
 * $Id: NestedSetNode.java,v 1.1 2004/09/07 10:58:38 gummi Exp $
 * Created on 4.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util.datastructures;


/**
 * 
 * For a small introduction on nested sets see: 
 * @see <a href="http://www.intelligententerprise.com/001020/celko1_1.jhtml?_requestid=224927">Nested Sets</a>
 * 
 *  Last modified: $Date: 2004/09/07 10:58:38 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public class NestedSetNode {

	private Object obj;
	private NestedSetNode parent;
	private int lft;
	private int rgt;
	
	/**
	 * 
	 */
	private NestedSetNode() {
		super();
	}
	
	public NestedSetNode(Object obj1, NestedSetNode parentNode, int left, int right){
		this();
		obj=obj1;
		parent=parentNode;
		lft=left;
		rgt=right;
	}
	
	public int hashCode(){
		return obj.hashCode();
	}
	
	public boolean equals(Object object){
		if(object instanceof NestedSetNode){
			return obj.equals(((NestedSetNode)object).getObject());
		}
		return obj.equals(object);
	}
	
	public int getLeft() {
		return lft;
	}
	public void setLeft(int left) {
		this.lft = left;
	}
	public Object getObject() {
		return obj;
	}
	public void setObject(Object object) {
		this.obj = object;
	}
	public int getRight() {
		return rgt;
	}
	public void setRight(int right) {
		this.rgt = right;
	}
	public NestedSetNode getParent() {
		return parent;
	}
	public boolean hasChildren(){
		return ((lft+1)!=rgt);
	}
}
