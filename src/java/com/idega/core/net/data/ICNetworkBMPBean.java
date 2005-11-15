package com.idega.core.net.data;

import java.sql.SQLException;
import com.idega.user.data.Group;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class ICNetworkBMPBean extends com.idega.data.GenericEntity implements com.idega.core.net.data.ICNetwork {

  public final static String _COLUMNNAME_IPADDRESS = "ipaddress";
  //public final static String _COLUMNNAME_SUBNET_MASK = "subnet_mask";

//  public final static String _MASK_SINGLE_IPADDRESS = "255.255.255.255";
//  public final static String _MASK_SUBNET_255 = "255.255.255.0";
//  public final static String _MASK_SUBNET_255_255 = "255.255.0.0";

  public ICNetworkBMPBean() {
    super();
  }

  public ICNetworkBMPBean(int id)  throws SQLException {
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(_COLUMNNAME_IPADDRESS,"IP Address",true,true,String.class,15);
    //this.addAttribute(_COLUMNNAME_SUBNET_MASK,"Subnet Mask",true,true,String.class,15);
    //this.addManyToManyRelationShip(GenericGroup.class,"ic_group_network");
    this.addManyToManyRelationShip(Group.class,"ic_group_network");
    this.addManyToManyRelationShip(ICProtocol.class,"ib_protocol_network");
  }
  public String getEntityName() {
    return "ic_network";
  }

}
