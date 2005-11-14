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

public class ICProtocolBMPBean extends com.idega.core.data.GenericTypeBMPBean implements com.idega.core.net.data.ICProtocol {

  public final static String _PROTOCOL_HTTP = "HTTP";
  public final static String _PROTOCOL_HTTPS = "HTTPS";

  public ICProtocolBMPBean() {
    super();
  }

  public ICProtocolBMPBean(int id) throws SQLException {
    super(id);
  }

  public void initializeAttributes(){
    super.initializeAttributes();
    //this.addManyToManyRelationShip(GenericGroup.class,"ic_group_protocol");
    this.addManyToManyRelationShip(Group.class,"ic_group_protocol");
    this.addManyToManyRelationShip(ICNetwork.class,"ib_protocol_network");
  }

  public String getEntityName() {
    return "ic_protocol";
  }

  public void insertStartData() {
    try {
      ICProtocol icp = ((com.idega.core.net.data.ICProtocolHome)com.idega.data.IDOLookup.getHomeLegacy(ICProtocol.class)).createLegacy();
      icp.setName(_PROTOCOL_HTTP);
      icp.setDescription("Hypertext Transfer Protocol");
      icp.insert();

      icp = ((com.idega.core.net.data.ICProtocolHome)com.idega.data.IDOLookup.getHomeLegacy(ICProtocol.class)).createLegacy();
      icp.setName(_PROTOCOL_HTTPS);
      icp.setDescription("Hypertext Transfer Protocol Secure - Secure Socket Layer");
      icp.insert();
    }
    catch (SQLException sql) {
      sql.printStackTrace();
    }
  }


}
