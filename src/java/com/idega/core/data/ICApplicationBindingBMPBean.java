/*
 * Created on 29.3.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.core.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;
import com.idega.data.query.SelectQuery;

/**
 * Title:		ICApplicationBindingBMPBean
 * Description:
 * Copyright:	Copyright (c) 2004
 * Company:		idega Software
 * @author		2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class ICApplicationBindingBMPBean extends GenericEntity implements ICApplicationBinding{
	
	// UFN - UNIQUE_FIELD_NAME
	//${iConst} public final static String UNIQUE_ENTITY_NAME = "IC_APPLICATION_BINDING";
	//${iConst} public final static String UFN_KEY = "BINDING_KEY";
	//${iConst} public final static String UFN_VALUE = "BINDING_VALUE";
	//${iConst} public final static String UFN_BINDING_TYPE = "BINDING_TYPE";
	
	public final static String ENTITY_NAME = "IC_APPLICATION_BINDING";
	public final static String COLUMNNAME_KEY = "BINDING_KEY";
	public final static String COLUMNNAME_VALUE = "BINDING_VALUE";
	public final static String COLUMNNAME_BINDING_TYPE = "BINDING_TYPE";
	
	public final static int MAX_KEY_LENGTH = 30;
	
	/**
	 * 
	 */
	public ICApplicationBindingBMPBean() {
		super();
	}


	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#getEntityName()
	 */
	public String getEntityName() {
		return ENTITY_NAME;
	}
	
	public String getIDColumnName(){
		return COLUMNNAME_KEY;
	}
	
	public Class getPrimaryKeyClass(){
		return String.class;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addAttribute(COLUMNNAME_KEY,"Key",true,true,String.class, MAX_KEY_LENGTH);
		setAsPrimaryKey(COLUMNNAME_KEY,true);
		addAttribute(COLUMNNAME_VALUE,"Value",true,true,String.class);
		addAttribute(COLUMNNAME_BINDING_TYPE,"Type",true,true,String.class);
		
		getEntityDefinition().setBeanCachingActiveByDefault(true,1000);
	}
	
	
	
	public String getKey(){
		return getStringColumnValue(COLUMNNAME_KEY);
	}
	
	public String getValue(){
		return getStringColumnValue(COLUMNNAME_VALUE);
	}
	
	public String getBindingType(){
		return getStringColumnValue(COLUMNNAME_BINDING_TYPE);
	}
	
	public void setKey(String key){
		setColumn(COLUMNNAME_KEY,key);
	}
	
	public void setValue(String value){
		setColumn(COLUMNNAME_VALUE,value);
	}
	
	public void setBindingType(String type){
		setColumn(COLUMNNAME_BINDING_TYPE,type);
	}
	
	public Collection ejbFindByBindingType(String type) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(COLUMNNAME_BINDING_TYPE,type);
		return idoFindPKsByQuery(query);
	}
	
	public Collection ejbFindAll() throws FinderException {
		SelectQuery sql = idoSelectQuery();
		//sql.appendSelectAllFrom(this.getEntityName());
		//return this.idoFindPKsByQuery(sql);
		return idoFindPKsByQueryIgnoringCacheAndUsingLoadBalance(sql,1000);
	}

}
