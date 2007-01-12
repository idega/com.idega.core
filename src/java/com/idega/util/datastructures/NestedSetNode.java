/*
 * $Id: NestedSetNode.java,v 1.2.2.1 2007/01/12 19:32:19 idegaweb Exp $
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
 *  Last modified: $Date: 2007/01/12 19:32:19 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.2.2.1 $
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
		this.obj=obj1;
		this.parent=parentNode;
		this.lft=left;
		this.rgt=right;
	}
	
	public int hashCode(){
		return this.obj.hashCode();
	}
	
	public boolean equals(Object object){
		if(object instanceof NestedSetNode){
			return this.obj.equals(((NestedSetNode)object).getObject());
		}
		return this.obj.equals(object);
	}
	
	public int getLeft() {
		return this.lft;
	}
	public void setLeft(int left) {
		this.lft = left;
	}
	public Object getObject() {
		return this.obj;
	}
	public void setObject(Object object) {
		this.obj = object;
	}
	public int getRight() {
		return this.rgt;
	}
	public void setRight(int right) {
		this.rgt = right;
	}
	public NestedSetNode getParent() {
		return this.parent;
	}
	public boolean hasChildren(){
		return ((this.lft+1)!=this.rgt);
	}
	
	public String toString(){
		return this.obj.toString()+" left:"+this.lft+" right:"+this.rgt;
	}
}
