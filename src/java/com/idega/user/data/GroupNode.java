package com.idega.user.data;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ejb.EJBException;

public interface GroupNode<G> {

	public String getName();

	public String getId();

	public String getType();

	/**
	 *
	 * @return parent {@link Group}s without ancestors, or {@link Collections#emptyList()} on failure;
	 * @deprecated use {@link GroupHome#findParentGroups(int)} instead
	 */
	@Deprecated
	public List<G> getParentGroups();

	/**
	 *
	 * @param cachedParents is not used;
	 * @param cachedGroups is not used;
	 * @return parent {@link Group}s without ancestors, or {@link Collections#emptyList()} on failure;
	 * @throws EJBException
	 * @deprecated use {@link GroupHome#findParentGroups(int)} instead
	 */
	@Deprecated
	public List<G> getParentGroups(
			Map<String, Collection<Integer>> cachedParents,
			Map<String, G> cachedGroups) throws EJBException;

	public Collection<G> getChildren();

}