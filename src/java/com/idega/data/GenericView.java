/*
 * Created on Jan 20, 2004
 *
 */
package com.idega.data;

/**
 * GenericView
 * @author aron 
 * @version 1.0
 */
public abstract class GenericView extends GenericEntity implements IDOView {
	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#getEntityName()
	 */
	public abstract String getEntityName() ;
	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#initializeAttributes()
	 */
	public abstract void initializeAttributes();
	/* (non-Javadoc)
	 * @see com.idega.data.IDOView#getCreationSQL()
	 */
	public abstract String getCreationSQL();
	
	public String getViewName(){
		return getTableName();
	}
	
	public void addDependantViewClass(Class viewClass){
		
	}
	
	public java.util.Collection getDependantViewClasses(){
		return null;
	}

}
