/*
 * $Id: ICApplicationLogBMPBean.java,v 1.1 2006/06/15 17:53:23 tryggvil Exp $ Created on
 * 9.12.2005 in project com.idega.core
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.data;

import java.sql.Timestamp;
import com.idega.data.GenericEntity;

/**
 * <p>
 * Entity bean for the table IC_APPLICATION that holds important
 * application-wide properties.
 * </p>
 * Last modified: $Date: 2006/06/15 17:53:23 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class ICApplicationLogBMPBean extends GenericEntity {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 601035836606742324L;
	public static final String TABLE_NAME = "IC_APPLICATION_LOG";
	public static final String COLUMNNAME_NAME = "LOG_NAME";
	public static final String COLUMNNAME_TYPE = "LOG_TYPE";
	public static final String COLUMNNAME_LEVEL = "LOG_LEVEL";
	public static final String COLUMNNAME_TIMESTAMP = "LOG_TIMESTAMP";

	/**
	 * 
	 */
	public ICApplicationLogBMPBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void initializeAttributes() {
		addAttribute(COLUMNNAME_NAME, "Application Name", String.class);
		addAttribute(COLUMNNAME_TIMESTAMP, "Logging timestamp", Timestamp.class);
		/*
		 * addAttribute(getIDColumnName());
		 * 
		 * addAttribute(getColumnDomainName(),"Domain
		 * name",true,true,String.class); addAttribute(getColumnURL(),"Domain
		 * URL",true,true,String.class,1000);
		 * addAttribute(getColumnStartPage(),"Start
		 * Page",true,true,Integer.class,"many-to-one",ICPage.class);
		 * addAttribute(getColumnStartTemplate(),"Start
		 * Template",true,true,Integer.class,"many-to-one",ICPage.class);
		 * addAttribute(COLUMNNAME_SERVER_NAME,"Server
		 * NAME",true,true,String.class); //
		 * this.addManyToManyRelationShip(Group.class); //
		 * addAttribute(COLUMNNAME_GROUP_ID,"Group
		 * ID",true,true,Integer.class,"one-to-one",Group.class);
		 * 
		 * //Add a UUID column to uniquely identify the domain:
		 * super.addUniqueIDColumn();
		 * addAttribute(COLUMNNAME_SERVER_PORT,"Server port",Integer.class);
		 * addAttribute(COLUMNNAME_SERVER_PROTOCOL,"Server
		 * protocol",String.class,30);
		 * addAttribute(COLUMNNAME_SERVER_CONTEXT_PATH,"Server context
		 * path",String.class);
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.GenericEntity#getEntityName()
	 */
	public String getEntityName() {
		return TABLE_NAME;
	}
}
