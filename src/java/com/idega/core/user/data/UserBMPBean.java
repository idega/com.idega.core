package com.idega.core.user.data;

import java.sql.SQLException;

import com.idega.data.IDOStoreException;
import com.idega.user.data.User;


/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

/**
 * @deprecated Replaced with com.idega.user.data.UserBMPBean
 */
public class UserBMPBean extends com.idega.user.data.UserBMPBean implements com.idega.core.user.data.User,User {

	/**
	 * Overriding insert to invoke store() for backwards compatability
	 **/
	public void insert()throws SQLException{
		try{
			store();	
		}
		catch(Exception e){
			throw new SQLException("Error inserting for old UserBMPBean. Message was: "+e.getMessage());
		}
	}
	
	
	
	/**
	 * Overriding store to avoid circular invokation of store() and insert()
	 **/
	public void store() throws IDOStoreException
	{
		try
		{
			if ((getEntityState() == STATE_NEW) || (getEntityState() == STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE))
			{
				super.insert();
			}
			else if (this.getEntityState() == STATE_NOT_IN_SYNCH_WITH_DATASTORE)
			{
				update();
			}
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			throw new IDOStoreException(e.getMessage());
		}
	}
	
	
}

