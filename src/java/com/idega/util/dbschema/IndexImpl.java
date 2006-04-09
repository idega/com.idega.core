/*
 * $Id: IndexImpl.java,v 1.2 2006/04/09 12:13:19 laddi Exp $
 * Created on 28.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util.dbschema;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *  Last modified: $Date: 2006/04/09 12:13:19 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class IndexImpl implements Index {
    
    private String indexName;
    private String tableName;
    private List fields = new ArrayList();
    private boolean unique = false;
    
    public IndexImpl(String indexName, String tableName){
        this.indexName = indexName;
        this.tableName = tableName;
    }

    /* (non-Javadoc)
     * @see com.idega.data.store.IndexDefinition#getIndexName()
     */
    public String getIndexName() {
        return this.indexName;
    }

    /* (non-Javadoc)
     * @see com.idega.data.store.IndexDefinition#getColumns()
     */
    public IndexColumn[] getColumns() {
       return (IndexColumn[]) this.fields.toArray(new IndexColumn[0]);
    }
    
    
    public void addField(String name, boolean isDescending){
        this.fields.add(new Field(name,isDescending));
    }
    
    public void addField(String name){
        this.addField(name,false);
    }
    
    private class Field implements IndexColumn{
        	private String name;
        	private boolean descending;
        	
        	public Field(String name, boolean descending){
        	    this.name = name;
        	    this.descending = descending;
        	}
        /* (non-Javadoc)
         * @see com.idega.data.store.IndexField#getName()
         */
        public String getName() {
            return this.name;
        }

        /* (non-Javadoc)
         * @see com.idega.data.store.IndexField#isDescending()
         */
        public boolean isDescending() {
           return this.descending;
        }
        
    }
    

    /* (non-Javadoc)
     * @see com.idega.data.store.IndexDefinition#getTableName()
     */
    public String getSchemaName() {
        return this.tableName;
    }

    /* (non-Javadoc)
     * @see com.idega.data.store.IndexDefinition#isUnique()
     */
    public boolean isUnique() {
        return this.unique;
    }

}
