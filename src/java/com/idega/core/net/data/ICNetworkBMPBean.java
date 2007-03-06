package com.idega.core.net.data;

import com.idega.user.data.Group;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class ICNetworkBMPBean extends com.idega.data.GenericEntity implements com.idega.core.net.data.ICNetwork {

  public final static String _COLUMNNAME_IPADDRESS = "ipaddress";

  public void initializeAttributes() {
    addAttribute(this.getIDColumnName());
    addAttribute(_COLUMNNAME_IPADDRESS,"IP Address",true,true,String.class,15);

    addManyToManyRelationShip(Group.class,"ic_group_network");
    addManyToManyRelationShip(ICProtocol.class,"ib_protocol_network");
  }

  public String getEntityName() {
    return "ic_network";
  }
}