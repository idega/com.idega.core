package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import com.idega.business.IBOService;
import com.idega.presentation.PresentationObject;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 * Title:        This inteface defines methods you can implement to customize the behavior of the user system. <br>
 * Methods returning a boolean should return true by default other methods can return null objects or empty lists where applicable.
 * Description:  idegaWeb User Subsystem
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>,<a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public interface UserGroupPlugInBusiness extends IBOService {

    public void beforeUserRemove(User user)throws RemoveException,RemoteException;
    public void afterUserCreateOrUpdate(User user)throws CreateException,RemoteException;

    public void beforeGroupRemove(Group group)throws RemoveException,RemoteException;
    public void afterGroupCreateOrUpdate(Group group)throws CreateException,RemoteException;
 
	public PresentationObject instanciateEditor(Group group)throws RemoteException;
    public PresentationObject instanciateViewer(Group group)throws RemoteException;
    
    
    /**
     * Return a list of Collectable presentation objects that extend UserTab.
     * These objects will be used in the User Property Window as tabs.
     * @param user The currently selected user
     * @return a list of Collectable presentation object, preferably something that extends UserTab OR null
     * @throws RemoteException
     */
    public List getUserPropertiesTabs(User user) throws RemoteException;
    
    /**
     * Return a list of Collectable presentation objects
     * These objects will be used as Tabs in the Group Property Window
     * @param group The currently selected group
     * @return a list of Collectable presentation object or null
     * @throws RemoteException
     */
    public List getGroupPropertiesTabs(Group group) throws RemoteException;
    
    /**
     * Return a list of com.idega.user.app.ToolbarElement's that you want to be added to the main toolbar
     * @return A list of com.idega.user.app.ToolbarElement's that you want to be added to the group view toolbar
     * @throws RemoteException
     */
    public List getMainToolbarElements() throws RemoteException;
    
    /**
     * Return a list of com.idega.user.app.ToolbarElement's that you want to be added to the group view toolbar
     * @param group The currently selected group
     * @return A list of com.idega.user.app.ToolbarElement's that you want to be added to the group view toolbar
     * @throws RemoteException
     */
    public List getGroupToolbarElements(Group group) throws RemoteException;

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

    /**
     * Checks if it is allowed to create a child (sub) group under the supplied group.
     * @param group
     * @return Should return null by default but a string (explanation) if the plugin doesn't allow subgroups
     * @throws RemoteException
     */
    public String canCreateSubGroup(Group group) throws RemoteException;
    
    
}
