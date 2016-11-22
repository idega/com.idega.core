package com.idega.repository.bean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;

import com.idega.core.data.ICTreeNode;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;

public abstract class RepositoryItem extends File implements ICTreeNode<RepositoryItem>, Serializable {

	private static final long serialVersionUID = -5591183900265522785L;

	private static Logger LOGGER = null;

	protected Logger getLogger() {
		if (LOGGER == null)
			LOGGER = Logger.getLogger(getClass().getName());
		return LOGGER;
	}

	private int id;
	private String name;

	public abstract InputStream getInputStream() throws IOException;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long length() {
		return getLength();
	}

	public abstract long getLength();
	public abstract long getCreationDate();
	public abstract long getLastModified();

	@Override
	public abstract boolean delete();

	public abstract RepositoryItem getParenItem();

	public abstract Collection<RepositoryItem> getChildResources();
	public abstract Collection<RepositoryItem> getSiblingResources();

	public abstract boolean isCollection();

	@Override
	public abstract boolean exists();

	@Override
	public abstract boolean createNewFile();

	@Override
	public abstract String getPath();
	public abstract String getParentPath();

	public abstract URL getHttpURL();

	public abstract String getMimeType();

	public RepositoryItem(String name) {
		super(name);
		this.name = name;

		id = new Random().nextInt();
	}

	public String getEncodedPath() {
		try {
			return URLEncoder.encode(getPath(), CoreConstants.ENCODING_UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getPath();
	}

	@Override
	public Collection<RepositoryItem> getChildren() {
		return getChildResources();
	}

	@Override
	public Iterator<RepositoryItem> getChildrenIterator() {
		Collection<RepositoryItem> children = getChildren();
		if (children == null) {
			return null;
		}
		return children.iterator();
	}

	@Override
	public boolean getAllowsChildren() {
		return isCollection();
	}

	@Override
	public RepositoryItem getChildAtIndex(int childIndex) {
		Collection<RepositoryItem> children = getChildren();
		if (ListUtil.isEmpty(children))
			return null;

		if (childIndex >= children.size())
			return null;

		List<RepositoryItem> childrenList = new ArrayList<RepositoryItem>(children);
		return childrenList.get(childIndex);
	}

	@Override
	public int getChildCount() {
		Collection<RepositoryItem> children = getChildren();
		return ListUtil.isEmpty(children) ? 0 : children.size();
	}

	@Override
	public int getIndex(RepositoryItem node) {
		Collection<RepositoryItem> children = getChildren();
		if (ListUtil.isEmpty(children))
			return -1;

		List<RepositoryItem> childrenList = new ArrayList<RepositoryItem>(children);
		return childrenList.indexOf(node);
	}

	@Override
	public RepositoryItem getParentNode() {
		return getParenItem();
	}

	@Override
	public boolean isLeaf() {
		return !isCollection();
	}

	@Override
	public String getNodeName() {
		return getName();
	}

	@Override
	public String getNodeName(Locale locale) {
		return getName();
	}

	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getName();
	}

	@Override
	public int getNodeID() {
		return id;
	}

	@Override
	public int getSiblingCount() {
		Collection<RepositoryItem> siblings = getSiblingResources();
		return ListUtil.isEmpty(siblings) ? 0 : siblings.size();
	}

	@Override
	public String getId() {
		return String.valueOf(getNodeID());
	}

	@Override
	public String toString() {
		return getPath();
	}

	@Override
	public boolean isHidden() {
		return getName().startsWith(CoreConstants.DOT);
	}

	@Override
	public boolean isDirectory() {
		return isCollection();
	}

}