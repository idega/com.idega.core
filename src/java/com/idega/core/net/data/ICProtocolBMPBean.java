package com.idega.core.net.data;

import java.sql.SQLException;

import javax.ejb.FinderException;

import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
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

	private static final long serialVersionUID = 6351516516189745606L;

	public final static String _PROTOCOL_HTTP = "HTTP";
	public final static String _PROTOCOL_HTTPS = "HTTPS";

	public ICProtocolBMPBean() {
		super();
	}

	public ICProtocolBMPBean(int id) throws SQLException {
		super(id);
	}

	@Override
	public void initializeAttributes() {
		super.initializeAttributes();
		// this.addManyToManyRelationShip(GenericGroup.class,"ic_group_protocol");
		this.addManyToManyRelationShip(Group.class, "ic_group_protocol");
		this.addManyToManyRelationShip(ICNetwork.class, "ib_protocol_network");
	}

	@Override
	public String getEntityName() {
		return "ic_protocol";
	}

	@Override
	public void insertStartData() {
		try {
			ICProtocol icp = ((com.idega.core.net.data.ICProtocolHome) com.idega.data.IDOLookup.getHomeLegacy(ICProtocol.class)).createLegacy();
			icp.setName(_PROTOCOL_HTTP);
			icp.setDescription("Hypertext Transfer Protocol");
			icp.insert();
			icp = ((com.idega.core.net.data.ICProtocolHome) com.idega.data.IDOLookup.getHomeLegacy(ICProtocol.class)).createLegacy();
			icp.setName(_PROTOCOL_HTTPS);
			icp.setDescription("Hypertext Transfer Protocol Secure - Secure Socket Layer");
			icp.insert();
		}
		catch (SQLException sql) {
			sql.printStackTrace();
		}
	}

	public Object ejbFindByName(String name) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(getColumnNameDisplayName()), MatchCriteria.EQUALS, name));
		return idoFindOnePKByQuery(query);
	}
}
