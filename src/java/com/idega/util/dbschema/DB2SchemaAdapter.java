package com.idega.util.dbschema;


/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:05:31 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */

public class DB2SchemaAdapter extends SQLSchemaAdapter {

	public String getSQLType(String javaClassName, int maxlength) {
		String theReturn;
		if (javaClassName.equals("java.lang.Integer")) {
			theReturn = "NUMBER";
		}
		else if (javaClassName.equals("java.lang.String")) {
			if (maxlength < 0) {
				theReturn = "VARCHAR(255)";
			}
			else if (maxlength <= 30000) {
				theReturn = "VARCHAR(" + maxlength + ")";
			}
			else {
				theReturn = "LONG VARCHAR";
			}
		}
		else if (javaClassName.equals("java.lang.Boolean")) {
			theReturn = "CHAR(1)";
		}
		else if (javaClassName.equals("java.lang.Float")) {
			theReturn = "FLOAT";
		}
		else if (javaClassName.equals("java.lang.Double")) {
			theReturn = "FLOAT(15)";
		}
		else if (javaClassName.equals("java.sql.Timestamp")) {
			theReturn = "TIMESTAMP";
		}
		else if (javaClassName.equals("java.sql.Date")
				|| javaClassName.equals("java.util.Date")) {
			theReturn = "DATE";
		}
		else if (javaClassName.equals("java.sql.Blob")) {
			theReturn = "BLOB";
		}
		else if (javaClassName.equals("java.sql.Time")) {
			theReturn = "TIME";
		}
		else if (javaClassName.equals("com.idega.util.Gender")) {
			theReturn = "VARCHAR(1)";
		}
		else if (javaClassName.equals("com.idega.data.BlobWrapper")) {
			theReturn = "BLOB";
		}
		else {
			theReturn = "";
		}
		return theReturn;
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