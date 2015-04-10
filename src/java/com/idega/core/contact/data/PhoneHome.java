package com.idega.core.contact.data;

import java.util.Collection;
import java.util.Collections;


public interface PhoneHome extends com.idega.data.IDOHome
{
 public Phone create() throws javax.ejb.CreateException;
 public Phone createLegacy();
 public Phone findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Phone findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Phone findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

public Phone findUsersHomePhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException;
public Phone findUsersWorkPhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException;
public Phone findUsersMobilePhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException;
public Phone findUsersFaxPhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException;

	/**
	 *
	 * <p>Creates/updates {@link Phone} in data source.</p>
	 * @param primaryKey is {@link Phone#getPrimaryKey()}, if not <code>null</code>
	 * then existing entity will be updated;
	 * @param phoneNumber is {@link Phone#getNumber()}, not <code>null</code>;
	 * @param phoneTypeId TODO
	 * @return updated/created {@link Phone} or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Phone update(String primaryKey, String phoneNumber, String phoneTypeId);

	/**
	 *
	 * @param phoneNumber is {@link Phone#getNumber()}, not <code>null</code>;
	 * @return {@link Phone}s by given number or {@link Collections#emptyList()}
	 * on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Collection<Phone> findByPhoneNumber(String phoneNumber);

	public Collection<Phone> findUsersPhones(int userId,int type);
}