package com.idega.user.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface UserInfoColumnsHome extends IDOHome {

	public UserInfoColumns create() throws CreateException;

	public UserInfoColumns findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAllByUserIdAndGroupId(int p0, int p1) throws FinderException;
}