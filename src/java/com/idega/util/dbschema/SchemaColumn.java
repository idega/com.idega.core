package com.idega.util.dbschema;


/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:05:31 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface SchemaColumn {
	 public Schema getSchema();
	 public String getUniqueName();
	 public String getSQLName();
	 public Class getDataTypeClass();
	 public boolean isNullAllowed();
	 public boolean isPartOfPrimaryKey();
	 public boolean isUnique();
	 public int getMaxLength();
	 public boolean isPartOfManyToOneRelationship();
	 public Schema getManyToOneRelated();
}
