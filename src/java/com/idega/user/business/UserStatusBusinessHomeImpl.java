/*
 * Created on 24.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.user.business;



import com.idega.business.IBOHomeImpl;

/**
 * @author aron
 *
 * UserStatusBusinessHomeImpl TODO Describe this type
 */
public class UserStatusBusinessHomeImpl extends IBOHomeImpl implements
        UserStatusBusinessHome {
    protected Class getBeanInterfaceClass() {
        return UserStatusBusiness.class;
    }

    public UserStatusBusiness create() throws javax.ejb.CreateException {
        return (UserStatusBusiness) super.createIBO();
    }

}
