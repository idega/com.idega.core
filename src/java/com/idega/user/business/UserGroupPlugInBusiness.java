package com.idega.user.business;

import com.idega.user.data.*;
import com.idega.user.presentation.*;

import com.idega.business.IBOService;

import java.rmi.RemoteException;
import javax.ejb.*;

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
    public void afterUserCreate(User user)throws CreateException,RemoteException;

    public void beforeGroupRemove(Group group)throws RemoveException,RemoteException;
    public void afterGroupCreate(Group group)throws CreateException,RemoteException;


    public Class getPresentationObjectClass()throws RemoteException;
    public UserGroupPlugInPresentable instantiatePresentation(Group group)throws RemoteException;
}