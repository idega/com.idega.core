package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDomainHome;
import com.idega.core.data.ICTreeNode;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.user.data.GroupType;
import com.idega.user.data.GroupTypeHome;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class GroupTreeNode implements ICTreeNode {

	private ICTreeNode _parent = null;

	private ICDomain _domain = null;
	private Group _group = null;
	private int _nodeType;
	private GroupBusiness groupBiz;
	private List _children = null;
	private IWApplicationContext _iwac = null;
	
	public static final int TYPE_DOMAIN = 0;
	public static final int TYPE_GROUP = 1;

	public GroupTreeNode(ICDomain domain,  IWApplicationContext iwc) {
		setIWApplicationContext(iwc);
		Map m = (Map)iwc.getApplicationAttribute("domain_group_tree");
		if (m == null) {
			m = new Hashtable();
			iwc.setApplicationAttribute("domain_group_tree",m);
		}		

		GroupTreeNode node = (GroupTreeNode)m.get(domain.getPrimaryKey());
		if (node == null) {
			this._domain = domain;
			this._nodeType = TYPE_DOMAIN;
		
			m.put(domain.getPrimaryKey(),this);
		}
		else {
			this._parent = node._parent;
			this._domain = node._domain;
			this._group = node._group;
			this._nodeType = node._nodeType;
			this._children = node._children;
		}			
	}

	protected void setIWApplicationContext(IWApplicationContext iwac){
		IWApplicationContext iwacToSet = iwac;
		if(iwac instanceof IWContext){
			IWContext iwc = (IWContext)iwac;
			iwacToSet = iwc.getApplicationContext();
		}
		this._iwac=iwacToSet;
	}
	
	public GroupTreeNode(Group group,  IWApplicationContext iwc) {
		Map m = (Map)iwc.getApplicationAttribute("group_tree");
		if (m == null) {
			m = new Hashtable();
			iwc.setApplicationAttribute("group_tree",m);
		}		

		GroupTreeNode node = (GroupTreeNode)m.get(group.getPrimaryKey());
		if (node == null) {
			this._group = group;
			this._nodeType = TYPE_GROUP;
		
			m.put(group.getPrimaryKey(),this);
		}
		else {
			this._parent = node._parent;
			this._domain = node._domain;
			this._group = node._group;
			this._nodeType = node._nodeType;
			this._children = node._children;
		}
	}

	protected GroupTreeNode(ICDomain domain) {
		this._domain = domain;
		this._nodeType = TYPE_DOMAIN;
	}

	public GroupTreeNode(Group group) {
		this._group = group;
		this._nodeType = TYPE_GROUP;
	}

	private GroupTypeHome getGroupTypeHome() throws RemoteException {
		return ((GroupTypeHome) IDOLookup.getHome(GroupType.class));
	}

	private GroupHome getGroupHome() throws RemoteException {
		return ((GroupHome) IDOLookup.getHome(Group.class));
	}

	public int getNodeType() {
		return this._nodeType;
	}

	public void setParent(ICTreeNode parent) {
		this._parent = parent;
	}

	public Iterator getChildrenIterator() {
	    Iterator it = null;
	    Collection children = getChildren();
	    if (children != null) {
	        it = children.iterator();
	    }
	    return it;
	}
	
	public Collection getChildren() {
		if (this._children != null) {
			return this._children;
		}
			
		switch (this._nodeType) {
			case TYPE_DOMAIN :
				/**
				 * @todo optimize the tree. store the tree nodes in session?
				 */
				try {
					List l = new Vector();
					Iterator iter = this.getGroupHome().findTopNodeVisibleGroupsContained(this._domain).iterator();
					
					
					GroupTreeNode node = null;
					while (iter.hasNext()) {
						Group item = (Group) iter.next();
						node = new GroupTreeNode(item);
						node.setParent(this);
						l.add(node);
					}

					this._children = l;
					return l;

				}
				catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			case TYPE_GROUP :
				List l = new Vector();

				Group g = null;
				if (isAlias()) {
					g = getAlias();
					if( g == null){
						return null;
					}
				}
				else {
					g = this._group;
				}

				Iterator iter = g.getChildrenIterator();
				GroupTreeNode node = null;
				while (iter.hasNext()) {
					Group item = (Group) iter.next();
					node = new GroupTreeNode(item);
					node.setParent(this);
					l.add(node);
				}

				this._children = l;

				return l;
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}

	}

	private boolean isAlias() {
		if (this._group == null) {
			return false;
		}

		try {
			if (this._group.getGroupType().equals("alias")) {
				return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private Group getAlias() {
		if (this._group == null) {
			return null;
		}

		Group alias = null;

		try {
			alias = this._group.getAlias();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return alias;
	}

	public boolean getAllowsChildren() {
		switch (this._nodeType) {
			case TYPE_DOMAIN :
				return true;
			case TYPE_GROUP :
				if (isAlias()) {
					return getAlias().getAllowsChildren();
				}
				else {
					return this._group.getAllowsChildren();
				}
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}

	public ICTreeNode getChildAtIndex(int childIndex) {
		if (this._children != null) {
			System.out.println("Getting child at index from vector");
			return (ICTreeNode)this._children.get(childIndex);
		}
			
		switch (this._nodeType) {
			case TYPE_DOMAIN :
				try {
					GroupTreeNode node = new GroupTreeNode(((ICDomainHome) this._domain.getEJBLocalHome()).findByPrimaryKey(new Integer(childIndex)));
					node.setParent(this);
					return node;
				}
				catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			case TYPE_GROUP :
				try {
					Group g = null;
					if (isAlias()) {
						g = getAlias();
					}
					else {
						g = this._group;
					}
					GroupTreeNode node = new GroupTreeNode((Group) g.getChildAtIndex(childIndex));
					node.setParent(this);
					return node;
				}
				catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}

	public int getChildCount() {
		if (this._children != null){
//			System.out.println("Getting child count from vector");

			return this._children.size();
		}
			
		switch (this._nodeType) {
			case TYPE_DOMAIN :
				try {
					return this.getGroupHome().getNumberOfTopNodeVisibleGroupsContained(this._domain);
				}
				catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			case TYPE_GROUP :
				if (isAlias()){
					Group aliasGroup = getAlias();
					Collection allAncestors = null;
					try {
						allAncestors = getGroupBusiness(this._iwac).getParentGroupsRecursive(this.getNodeID());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (allAncestors != null && allAncestors.contains(aliasGroup)) {
						System.out.println("Alias with ID = "+this.getNodeID()+" links to an ancestor with ID = "+aliasGroup.getPrimaryKey()+" The relationship is disabled to avoid endless loop");
						return 0;
					}
					else if(aliasGroup!=null) {
						return aliasGroup.getChildCount();
					}
					else{
						System.err.println("GroupTreeNode: Error - no alias for group :"+getNodeName()+" id: "+getNodeID());
						return 0;
					}
				}
				else {
					return this._group.getChildCount();
				}
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}

	public int getIndex(ICTreeNode node) {
		switch (this._nodeType) {
			case TYPE_DOMAIN :
				return node.getNodeID();
			case TYPE_GROUP :
				if (isAlias()) {
					return getAlias().getIndex(node);
				}
				else {
					return this._group.getIndex(node);
				}
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}

	public ICTreeNode getParentNode() {
		return this._parent;
	}

	public boolean isLeaf() {
		switch (this._nodeType) {
			case TYPE_DOMAIN :
				return false;
			case TYPE_GROUP :
				return false;
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}

	public String getNodeName() {
		switch (this._nodeType) {
			case TYPE_DOMAIN :
				return this._domain.getName();
			case TYPE_GROUP :
				return this._group.getNodeName();
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}
	
	public String getNodeName(Locale locale) {
		return getNodeName();
	}
	
	public String getNodeName(Locale locale, IWApplicationContext iwac){
		return getNodeName(locale);
	}
	
	public String getGroupType() {
		if (this._nodeType == TYPE_GROUP) {
			return this._group.getGroupType();
		}
		return null;
	}

	public int getNodeID() {
		switch (this._nodeType) {
			case TYPE_DOMAIN :
				try {
					return ((Integer) this._domain.getPrimaryKey()).intValue();
				}
				catch (Exception ex) {
					throw new RuntimeException(ex.getMessage());
				}
			case TYPE_GROUP :
				return this._group.getNodeID();
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}

	public String getShortNodeName() {
		if (this._nodeType == TYPE_GROUP) {
			return this._group.getShortName();
		}
		return null;
	}

	public int getSiblingCount() {
		switch (this._nodeType) {
			case TYPE_DOMAIN :
				if (this._parent != null) {
					return this._parent.getChildCount();
				}
				else {
					return 0;
				}
			case TYPE_GROUP :
				return this._group.getSiblingCount();
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}
	
	public GroupBusiness getGroupBusiness(IWApplicationContext iwc) {
		if (this.groupBiz == null) {
			try {
				this.groupBiz = (GroupBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, GroupBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.groupBiz;
	}
}