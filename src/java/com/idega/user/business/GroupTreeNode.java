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
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.user.data.GroupType;
import com.idega.user.data.GroupTypeHome;
import com.idega.user.event.CreateGroupEvent;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class GroupTreeNode implements ICTreeNode {

	private ICTreeNode _parent = null;

	private ICDomain _domain = null;
	private Group _group = null;
	private int _nodeType;
	private GroupBusiness groupBiz;
	private List _children = null;
	private IWApplicationContext _iwc = null;
	
	public static final int TYPE_DOMAIN = CreateGroupEvent.TYPE_DOMAIN;
	public static final int TYPE_GROUP = CreateGroupEvent.TYPE_GROUP;

	public GroupTreeNode(ICDomain domain,  IWApplicationContext iwc) {
		_iwc = iwc;
		Map m = (Map)iwc.getApplicationAttribute("domain_group_tree");
		if (m == null) {
			m = new Hashtable();
			iwc.setApplicationAttribute("domain_group_tree",m);
		}		

		GroupTreeNode node = (GroupTreeNode)m.get(domain.getPrimaryKey());
		if (node == null) {
			_domain = domain;
			_nodeType = TYPE_DOMAIN;
		
			m.put(domain.getPrimaryKey(),this);
		}
		else {
			_parent = node._parent;
			_domain = node._domain;
			_group = node._group;
			_nodeType = node._nodeType;
			_children = node._children;
		}			
	}

	public GroupTreeNode(Group group,  IWApplicationContext iwc) {
		Map m = (Map)iwc.getApplicationAttribute("group_tree");
		if (m == null) {
			m = new Hashtable();
			iwc.setApplicationAttribute("group_tree",m);
		}		

		GroupTreeNode node = (GroupTreeNode)m.get(group.getPrimaryKey());
		if (node == null) {
			_group = group;
			_nodeType = TYPE_GROUP;
		
			m.put(group.getPrimaryKey(),this);
		}
		else {
			_parent = node._parent;
			_domain = node._domain;
			_group = node._group;
			_nodeType = node._nodeType;
			_children = node._children;
		}
	}

	protected GroupTreeNode(ICDomain domain) {
		_domain = domain;
		_nodeType = TYPE_DOMAIN;
	}

	public GroupTreeNode(Group group) {
		_group = group;
		_nodeType = TYPE_GROUP;
	}

	private GroupTypeHome getGroupTypeHome() throws RemoteException {
		return ((GroupTypeHome) IDOLookup.getHome(GroupType.class));
	}

	private GroupHome getGroupHome() throws RemoteException {
		return ((GroupHome) IDOLookup.getHome(Group.class));
	}

	public int getNodeType() {
		return _nodeType;
	}

	public void setParent(ICTreeNode parent) {
		_parent = parent;
	}

	public Iterator getChildrenIterator() {
	    return getChildren().iterator();
	}
	
	public Collection getChildren() {
		if (_children != null) {
			return _children;
		}
			
		switch (_nodeType) {
			case TYPE_DOMAIN :
				/**
				 * @todo optimize the tree. store the tree nodes in session?
				 */
				try {
					List l = new Vector();
					Iterator iter = this.getGroupHome().findTopNodeVisibleGroupsContained(_domain).iterator();
					
					
					GroupTreeNode node = null;
					while (iter.hasNext()) {
						Group item = (Group) iter.next();
						node = new GroupTreeNode(item);
						node.setParent(this);
						l.add(node);
					}

					_children = l;
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
					g = _group;
				}

				Iterator iter = g.getChildrenIterator();
				GroupTreeNode node = null;
				while (iter.hasNext()) {
					Group item = (Group) iter.next();
					node = new GroupTreeNode(item);
					node.setParent(this);
					l.add(node);
				}

				_children = l;

				return l;
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}

	}

	private boolean isAlias() {
		if (_group == null)
			return false;

		try {
			if (_group.getGroupType().equals("alias")) {
				return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private Group getAlias() {
		if (_group == null)
			return null;

		Group alias = null;

		try {
			alias = _group.getAlias();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return alias;
	}

	public boolean getAllowsChildren() {
		switch (_nodeType) {
			case TYPE_DOMAIN :
				return true;
			case TYPE_GROUP :
				if (isAlias())
					return getAlias().getAllowsChildren();
				else
					return _group.getAllowsChildren();
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}

	public ICTreeNode getChildAtIndex(int childIndex) {
		if (_children != null) {
			System.out.println("Getting child at index from vector");
			return (ICTreeNode)_children.get(childIndex);
		}
			
		switch (_nodeType) {
			case TYPE_DOMAIN :
				try {
					GroupTreeNode node = new GroupTreeNode(((ICDomainHome) _domain.getEJBLocalHome()).findByPrimaryKey(new Integer(childIndex)));
					node.setParent(this);
					return node;
				}
				catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			case TYPE_GROUP :
				try {
					Group g = null;
					if (isAlias())
						g = getAlias();
					else
						g = _group;
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
		if (_children != null){
//			System.out.println("Getting child count from vector");

			return _children.size();
		}
			
		switch (_nodeType) {
			case TYPE_DOMAIN :
				try {
					return this.getGroupHome().getNumberOfTopNodeVisibleGroupsContained(_domain);
				}
				catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			case TYPE_GROUP :
				if (isAlias()){
					Group aliasGroup = getAlias();
					Collection allAncestors = null;
					try {
						allAncestors = getGroupBusiness(_iwc).getParentGroupsRecursive(this.getNodeID());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (allAncestors.contains(aliasGroup)) {
						System.out.println("Alias with ID = "+this.getNodeID()+" links to an ancestor with ID = "+aliasGroup.getPrimaryKey()+" The relationship is disabled to avoid endless loop");
						return 0;
					}
					else if(aliasGroup!=null) return aliasGroup.getChildCount();
					else{
						System.err.println("GroupTreeNode: Error - no alias for group :"+getNodeName()+" id: "+getNodeID());
						return 0;
					}
				}
				else
					return _group.getChildCount();
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}

	public int getIndex(ICTreeNode node) {
		switch (_nodeType) {
			case TYPE_DOMAIN :
				return node.getNodeID();
			case TYPE_GROUP :
				if (isAlias())
					return getAlias().getIndex(node);
				else
					return _group.getIndex(node);
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}

	public ICTreeNode getParentNode() {
		return _parent;
	}

	public boolean isLeaf() {
		switch (_nodeType) {
			case TYPE_DOMAIN :
				return false;
			case TYPE_GROUP :
				return false;
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}

	public String getNodeName() {
		switch (_nodeType) {
			case TYPE_DOMAIN :
				return _domain.getName();
			case TYPE_GROUP :
				return _group.getNodeName();
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
		if (this._nodeType == TYPE_GROUP)
			return _group.getGroupType();
		return null;
	}

	public int getNodeID() {
		switch (_nodeType) {
			case TYPE_DOMAIN :
				try {
					return ((Integer) _domain.getPrimaryKey()).intValue();
				}
				catch (Exception ex) {
					throw new RuntimeException(ex.getMessage());
				}
			case TYPE_GROUP :
				return _group.getNodeID();
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}

	public int getSiblingCount() {
		switch (_nodeType) {
			case TYPE_DOMAIN :
				if (_parent != null) {
					return _parent.getChildCount();
				}
				else {
					return 0;
				}
			case TYPE_GROUP :
				return _group.getSiblingCount();
			default :
				throw new UnsupportedOperationException("Operation not supported for type:" + getNodeType());
		}
	}
	
	public GroupBusiness getGroupBusiness(IWApplicationContext iwc) {
		if (groupBiz == null) {
			try {
				groupBiz = (GroupBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, GroupBusiness.class);
			}
			catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return groupBiz;
	}
}