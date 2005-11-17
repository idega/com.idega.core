/*
 * $Id: LDAPGroupBusiness.java,v 1.1 2005/11/17 15:50:44 tryggvil Exp $
 * Created on 16.11.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap.business;

import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;
import com.idega.core.ldap.client.naming.DN;
import com.idega.user.data.Group;

/**
 * <p>
 * Class for manipulating data for Groups in LDAP and idegaWeb.
 * </p>
 *  Last modified: $Date: 2005/11/17 15:50:44 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface LDAPGroupBusiness {

	/**
	 * Creates or updated a group from an LDAP DN and its attributes and adds it under the root (directly under in the group tree) of the default Domain (ICDomain) and/or the supplied parent group
	 * @throws NamingException,RemoteException,CreateException
	 * @see com.idega.user.business.GroupBusiness#createOrUpdateGroup(DN distinguishedName,Attributes attributes, boolean createUnderRootGroup, Group parentGroup)
	 */
	public abstract Group createOrUpdateGroup(DN distinguishedName, Attributes attributes,
			boolean createUnderRootDomainGroup, Group parentGroup) throws CreateException, NamingException,
			RemoteException;

	/**
	 * Tries to to get the group by its unique id and then tries to find it by the distinguished name (LDAP) if that fails.
	 * Both values do NOT need to be set.
	 * @param distinguishedName
	 * @param uniqueID
	 * @param ldapUtil
	 * @return The group if found but otherwise null
	 * @throws RemoteException
	 */
	public abstract Group getGroupByDNOrUniqueId(DN distinguishedName, String uniqueID) throws RemoteException;

	/**
	 * Adds or changes the groups current metadata with metadatafields contained in the attributes.
	 * It does not currently remove any existing metadata unless the value in the attributes is an empty string ("")
	 * @param group
	 * @param distinguishedName
	 * @param attributes
	 */
	public abstract void setMetaDataFromLDAPAttributes(Group group, DN distinguishedName, Attributes attributes);

	/**
	 * Creates or updated a group from an LDAP DN and its attributes and adds it under the root (directly under in the group tree) of the default Domain (ICDomain)
	 * @throws NamingException,RemoteException,CreateException
	 * @see com.idega.user.business.GroupBusiness#createOrUpdateGroup(DN distinguishedName,Attributes attributes)
	 */
	public abstract Group createOrUpdateGroup(DN distinguishedName, Attributes attributes) throws CreateException,
			NamingException, RemoteException;

	/**
	 * Creates a group from an LDAP DN and its attributes and adds it under the parentGroup supplied
	 * @see com.idega.user.business.GroupBusiness#createOrUpdateGroup(DN distinguishedName,Attributes attributes,Group parentGroup)
	 */
	public abstract Group createOrUpdateGroup(DN distinguishedName, Attributes attributes, Group parentGroup)
			throws CreateException, NamingException, RemoteException;

	/**
	 * Finds the correct group from the database using the directory strings
	 * group structure
	 * 
	 * @param dn
	 * @return a Group data bean
	 */
	public abstract Group getGroupByDirectoryString(DirectoryString dn) throws RemoteException;
}