package com.idega.user.data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.EJBException;

public interface GroupNode<G> {

	public String getId();

	public String getType();

	public List<G> getParentGroups();
	public List<G> getParentGroups(Map<String, Collection<Integer>> cachedParents, Map<String, G> cachedGroups) throws EJBException;

}