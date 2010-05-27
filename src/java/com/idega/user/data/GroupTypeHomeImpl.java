package com.idega.user.data;


import com.idega.data.IDOFactory;
import javax.ejb.CreateException;
import com.idega.data.IDOEntity;
import javax.ejb.FinderException;
import com.idega.data.IDOException;
import java.util.Collection;

public class GroupTypeHomeImpl extends IDOFactory implements GroupTypeHome {
	public Class getEntityInterfaceClass() {
		return GroupType.class;
	}

	public GroupType create() throws CreateException {
		return (GroupType) super.createIDO();
	}

	public GroupType findByPrimaryKey(Object pk) throws FinderException {
		return (GroupType) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAllGroupTypes() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((GroupTypeBMPBean) entity).ejbFindAllGroupTypes();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public GroupType findGroupTypeByGroupTypeString(String groupType)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((GroupTypeBMPBean) entity)
				.ejbFindGroupTypeByGroupTypeString(groupType);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findVisibleGroupTypes() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((GroupTypeBMPBean) entity).ejbFindVisibleGroupTypes();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public int getNumberOfGroupTypes() throws FinderException, IDOException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((GroupTypeBMPBean) entity)
				.ejbHomeGetNumberOfGroupTypes();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public int getNumberOfVisibleGroupTypes() throws FinderException,
			IDOException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((GroupTypeBMPBean) entity)
				.ejbHomeGetNumberOfVisibleGroupTypes();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public String getVisibleGroupTypesSQLString() {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		String theReturn = ((GroupTypeBMPBean) entity)
				.ejbHomeGetVisibleGroupTypesSQLString();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public String getGeneralGroupTypeString() {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		String theReturn = ((GroupTypeBMPBean) entity)
				.ejbHomeGetGeneralGroupTypeString();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public String getPermissionGroupTypeString() {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		String theReturn = ((GroupTypeBMPBean) entity)
				.ejbHomeGetPermissionGroupTypeString();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public String getAliasGroupTypeString() {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		String theReturn = ((GroupTypeBMPBean) entity)
				.ejbHomeGetAliasGroupTypeString();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}
}