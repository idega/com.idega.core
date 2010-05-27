package com.idega.user.data;


import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;
import com.idega.data.IDOException;
import java.util.Collection;

public interface GroupTypeHome extends IDOHome {
	public GroupType create() throws CreateException;

	public GroupType findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAllGroupTypes() throws FinderException;

	public GroupType findGroupTypeByGroupTypeString(String groupType)
			throws FinderException;

	public Collection findVisibleGroupTypes() throws FinderException;

	public int getNumberOfGroupTypes() throws FinderException, IDOException;

	public int getNumberOfVisibleGroupTypes() throws FinderException,
			IDOException;

	public String getVisibleGroupTypesSQLString();

	public String getGeneralGroupTypeString();

	public String getPermissionGroupTypeString();

	public String getAliasGroupTypeString();
}