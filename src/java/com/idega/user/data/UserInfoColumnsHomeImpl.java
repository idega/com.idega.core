package com.idega.user.data;


import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;

public class UserInfoColumnsHomeImpl extends IDOFactory implements UserInfoColumnsHome {

	public Class getEntityInterfaceClass() {
		return UserInfoColumns.class;
	}

	public UserInfoColumns create() throws CreateException {
		return (UserInfoColumns) super.createIDO();
	}

	public UserInfoColumns findByPrimaryKey(Object pk) throws FinderException {
		return (UserInfoColumns) super.findByPrimaryKeyIDO(pk);
	}
	
	public Collection findAllByUserIdAndGroupId(int user_id, int group_id) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((UserInfoColumnsBMPBean) entity).ejbFindAllByUserIdAndGroupId(user_id, group_id);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}