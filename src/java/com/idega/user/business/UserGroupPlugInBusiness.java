package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import javax.ejb.CreateException;
import javax.ejb.RemoveException;

import com.idega.business.IBOService;
import com.idega.presentation.PresentationObject;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 * Title:        idegaWeb User Subsystem
 * Description:  idegaWeb User Subsystem
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public interface UserGroupPlugInBusiness extends IBOService {

    public void beforeUserRemove(User user)throws RemoveException,RemoteException;
    public void afterUserCreateOrUpdate(User user)throws CreateException,RemoteException;

    public void beforeGroupRemove(Group group)throws RemoveException,RemoteException;
    public void afterGroupCreateOrUpdate(Group group)throws CreateException,RemoteException;

    public Class getPresentationObjectClass()throws RemoteException;
    /*
    public UserGroupPlugInPresentable instanciateEditor(Group group)throws RemoteException;
    public UserGroupPlugInPresentable instanciateViewer(Group group)throws RemoteException;
	*/
	public PresentationObject instanciateEditor(Group group)throws RemoteException;
    public PresentationObject instanciateViewer(Group group)throws RemoteException;
    
    public List getUserPropertiesTabs(User user) throws RemoteException;
    public List getGroupPropertiesTabs(Group group) throws RemoteException;
    public List getMainToolbarElements() throws RemoteException;
    public List getGroupToolbarElements(Group group) throws RemoteException;

    /**
     * Returns a Collection of ListViewerField Objects
     */
    public Collection getListViewerFields()throws RemoteException;

    /**
     * Returns a Collection of Group Objects
     */
    public Collection findGroupsByFields(Collection listViewerFields,Collection finderOperators,Collection listViewerFieldValues)throws RemoteException;
   
   /** Checks if the user is assignable from the specified source to the specified target.
   * 
   * @param user the user that should be moved.
   * @param sourceGroup source, the user should belong to the source
   * @param targetGroup target, where the user should be moved to.
   * @return a message that says what is wrong else null.
   */
    public String isUserAssignableFromGroupToGroup(User user, Group sourceGroup, Group targetGroup) throws RemoteException;
    
   /** Checks if the user is suited for the specified target.
   * 
   * @param user the user 
   * @param targetGroup target
   * @return a message that says what is wrong else null.
   */
    public String isUserSuitedForGroup(User user, Group targetGroup) throws RemoteException;
}
