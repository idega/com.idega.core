package com.idega.util.dbschema;



/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:05:31 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface PrimaryKey {
	public SchemaColumn[] getColumns();
	public SchemaColumn getColumn() throws CompositePrimaryKeyException;
	public boolean isComposite();
	public Class getPrimaryKeyClass();
}
