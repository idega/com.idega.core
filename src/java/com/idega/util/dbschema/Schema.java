package com.idega.util.dbschema;



/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:05:31 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface Schema {
	public String getUniqueName();
    public String getSQLName();
    public SchemaColumn[] getColumns();
	public PrimaryKey getPrimaryKey();
	public Index[] getIndexes();
	public boolean hasAutoIncrementColumn();
	public void setHasAutoIncrementColumn(boolean autoIncrementColumn);
	public UniqueKey[] getUniqueKeys();
}
