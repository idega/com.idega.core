package com.idega.util.dbschema;

import com.idega.data.EntityAttribute;


/**
 * 
 * 
 *  Last modified: $Date: 2006/05/26 16:51:49 $ by $Author: thomas $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */

public class DB2SchemaAdapter extends SQLSchemaAdapter {
	
	public String getSQLType(String javaClassName, int maxlength) {
		if (javaClassName.equals("java.lang.Integer")) {
			return "NUMBER";
		}
		if (javaClassName.equals("java.lang.String")) {
			if (maxlength == EntityAttribute.UNLIMITED_LENGTH) {
				return "LONG VARCHAR";
			}
			if (maxlength < 0) {
				return "VARCHAR(255)";
			}
			if (maxlength <= 30000) {
				return "VARCHAR(" + maxlength + ")";
			}
			return "LONG VARCHAR";
		}
		if (javaClassName.equals("java.lang.Boolean")) {
			return "CHAR(1)";
		}
		if (javaClassName.equals("java.lang.Float")) {
			return "FLOAT";
		}
		if (javaClassName.equals("java.lang.Double")) {
			return "FLOAT(15)";
		}
		if (javaClassName.equals("java.sql.Timestamp")) {
			return "TIMESTAMP";
		}
		if (javaClassName.equals("java.sql.Date")
				|| javaClassName.equals("java.util.Date")) {
			return "DATE";
		}
		if (javaClassName.equals("java.sql.Blob")) {
			return "BLOB";
		}
		if (javaClassName.equals("java.sql.Time")) {
			return "TIME";
		}
		if (javaClassName.equals("com.idega.util.Gender")) {
			return "VARCHAR(1)";
		}
		if (javaClassName.equals("com.idega.data.BlobWrapper")) {
			return "BLOB";
		}
		return  "";
	}

	/**
	 * 
	 * Only creates the sequence, not the trigger
	 * 
	 * @todo implement trigger creation
	 *  
	 */

	public void createTrigger( Schema entity)throws Exception {
		createSequence( entity);
	}

	public void createSequence(Schema entity)throws Exception {
		String s = "create sequence "+ entity.getSQLName()+ "_seq INCREMENT BY 1 START WITH 1 MAXVALUE 1.0E28 MINVALUE 1 NOCYCLE CACHE 20 NOORDER";
		executeUpdate(s);
	}

	public void removeSchema( Schema entity)
			throws Exception {

		super.removeSchema( entity);

		/**
		 * 
		 * @todo change
		 *  
		 */

		//deleteTrigger(entity);
		deleteSequence( entity);
	}

	protected void deleteTrigger( Schema entity)throws Exception {
		executeUpdate("drop trigger " + entity.getSQLName()+ "_trig");
	}

	protected void deleteSequence( Schema entity)throws Exception {
		executeUpdate("drop sequence " + entity.getSQLName()+ "_seq");
	}

	protected String getCreateUniqueIDQuery(Schema entity) {
		return "SELECT " + getSequenceName(entity) + ".nextval FROM dual";
	}

	private static String getSequenceName(Schema entity) {
		String entityName = entity.getSQLName();
		return entityName + "_seq";
	}

}