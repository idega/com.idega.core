package com.idega.util.dbschema;

/**
 * 
 *  An Index is used to index one or more fields in an existing table. 
 * 	You can do this by giving the index a name, 
 * 	and by stating the table and field(s) to which the index will apply.
 * 
 *  Last modified: $Date: 2004/11/01 10:05:31 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface Index {
    /**
     * The name of the index
     * @return
     */
    public String getIndexName();
    /**
     * Gets the fields that make up the index
     * @return
     */
    public IndexColumn[] getColumns();
    /**
     * The schema name to create the index on
     * @return
     */
    public String getSchemaName();
    /**
     * If one wants to prohibit duplicate values in the indexed field or fields, you can use the reserved word UNIQUE
     * @return
     */
    public boolean isUnique();
    
}
