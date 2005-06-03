/*
 * Created on Jun 25, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.idega.core.component.data;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.core.search.business.SearchPlugin;
import com.idega.data.GenericEntity;
import com.idega.data.IDOEntity;
import com.idega.data.IDOException;
import com.idega.data.IDOHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOQuery;
import com.idega.presentation.PresentationObject;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.text.TextSoap;

/**
 * @author gimmi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ICObjectTypeBMPBean extends GenericEntity implements ICObjectType, BundleComponent{

	private static final String TABLE_NAME="IC_OBJECT_TYPE";
  private static final String COLUMN_TYPE ="OBJECT_TYPE";
  private static final String COLUMN_NAME ="OBJECT_TYPE_NAME";
  private static final String COLUMN_REQUIRED_SUPER_CLASS = "REQ_SUPER_CLASS";
  private static final String COLUMN_FINAL_REFLECTION_CLASS = "FINAL_REFLECTION_CLASS";
  private static final String COLUMN_REQUIRED_INTERFACES = "REQ_INTERFACES";
  private static final String COLUMN_METHOD_START_FILTERS = "METHOD_START_FILTERS";

  // be careful when changing the order of the columns because of the methods insertData and updateStartData
  private static final String[][] startData = {
  		{ "iw.element","Element", PresentationObject.class.getName(), null, PresentationObject.class.getName(), "set"},
		{"iw.block","Block", PresentationObject.class.getName(), null, PresentationObject.class.getName(), "set"},
		{"iw.application","Application", PresentationObject.class.getName(), null, PresentationObject.class.getName(), "set"},
		{"iw.application.component","Application component", PresentationObject.class.getName(), null, PresentationObject.class.getName(), "set"},
		{"iw.data","Data", null, IDOEntity.class.getName(), null, "get,set"},
		{"iw.home","Home", null, IDOHome.class.getName(), null, "find,get"},
		{"iw.propertyhandler","Property handler", null, ICPropertyHandler.class.getName(), null, "set"},
		{"iw.plugin.user","User Plugin", null, null, null, null},
		{"iw.searchplugin","Search plugin",null,SearchPlugin.class.getName(),null,null}
  };
  

	public String getEntityName() {
		return TABLE_NAME;
	}

	public void initializeAttributes() {
		this.addAttribute(getIDColumnName(),"Type",String.class,100);
		this.setAsPrimaryKey(getIDColumnName(),true);
		this.addAttribute(COLUMN_NAME, "Name", String.class);
		this.addAttribute(COLUMN_REQUIRED_SUPER_CLASS, "superclass", String.class);
		this.addAttribute(COLUMN_REQUIRED_INTERFACES, "interfaces", String.class);
		this.addAttribute(COLUMN_FINAL_REFLECTION_CLASS, "final reflection class", String.class);
		this.addAttribute(COLUMN_METHOD_START_FILTERS, "method start filters", String.class);
	}



	public void insertStartData(){
		for (int i=0; i < startData.length; i++) {
			insertData(startData[i][0], startData[i][1], startData[i][2], startData[i][3], startData[i][4], startData[i][5]);
		}
	}


	private void insertData(String objectType, String objectName, String superClass, String interfaces, String reflection, String filters) {
		try {
		  ICObjectTypeHome home = (ICObjectTypeHome)IDOLookup.getHome(ICObjectType.class);
		
		  ICObjectType type = home.create();
		  type.setType(objectType);
		  type.setName(objectName);
		  type.setRequiredSuperClassName(superClass);
		  type.setRequiredInterfacesString(interfaces);
		  type.setFinalReflectionClassName(reflection);
		  type.setMethodStartFiltersString(filters);
		  type.store();
		}
		catch (RemoteException ex) {
		  throw new EJBException(ex);
		}
		catch (CreateException ex) {
		  ex.printStackTrace();
		}
	}
	
	public String getName() {
		return getStringColumnValue(COLUMN_NAME);
	}
	
	public void setName(String name) {
		setColumn(COLUMN_NAME, name);
	}

	public void setType(String type) {
		setColumn(COLUMN_TYPE, type);
	}
	
	public String getType() {
		return getStringColumnValue(COLUMN_TYPE);
	}
	
	public String getRequiredSuperClassName() {
		return getStringColumnValue(COLUMN_REQUIRED_SUPER_CLASS);
	}
	
	public void setRequiredSuperClassName(String name) {
		setColumn(COLUMN_REQUIRED_SUPER_CLASS, name);
	}
	
	public String getFinalReflectionClassName() {
		return getStringColumnValue(COLUMN_FINAL_REFLECTION_CLASS);
	}
	
	public void setFinalReflectionClassName(String name) {
		setColumn(COLUMN_FINAL_REFLECTION_CLASS, name);
	}
	
	public String getRequiredInterfacesString() {
		return getStringColumnValue(COLUMN_REQUIRED_INTERFACES);
	}	
	
	public void setRequiredInterfacesString(String string) {
		setColumn(COLUMN_REQUIRED_INTERFACES, string);
	}

	public String getMethodStartFiltersString() {
		return getStringColumnValue(COLUMN_METHOD_START_FILTERS);
	}
	
	public void setMethodStartFiltersString(String string) {
		setColumn(COLUMN_METHOD_START_FILTERS, string);
	}

	public String getIDColumnName(){
	  return COLUMN_TYPE;
	}
	
	public Class getPrimaryKeyClass(){
	  return String.class;
	}	
	
	public Collection ejbHomeFindAll() throws FinderException {
		return this.idoFindAllIDsBySQL();
	}

	public boolean  ejbHomeUpdateClassReferences(String oldClassName, Class newClass) throws IDOException {
		String newClassName = newClass.getName();
		// updated is true if an update was executed, that is if the table was changed. 
		// If updated is false it doesn't mean that the execution of the update statement failed.
		boolean updated = updateColumn(COLUMN_FINAL_REFLECTION_CLASS, newClassName, oldClassName);
		updated = updateColumn(COLUMN_REQUIRED_INTERFACES, newClassName, oldClassName) || updated;
		updated = updateColumn(COLUMN_REQUIRED_SUPER_CLASS, newClassName, oldClassName) || updated;
		return updated;
	}
	
	
	private boolean updateColumn(String columnName, String newValue, String oldValue) throws IDOException {
		IDOQuery query = IDOQuery.getStaticInstance();
		query.appendUpdateSet(this);
		query.append(columnName).appendEqualSign().appendQuoted(newValue);
		query.appendWhiteSpace();
		query.appendWhereEqualsQuoted(columnName, oldValue);
		return idoExecuteTableUpdate(query.toString());
	}
	
	public boolean ejbHomeUpdateStartData() throws IDOException  {
		boolean updated = false;
		for (int i=0; i < startData.length; i++) {
			String objectType = startData[i][0];
			int number;
			try {
				number = getNumberOfRecordsForStringColumn(COLUMN_TYPE,"=",objectType);
			}
			catch (SQLException e) {
				throw new IDOException(e, this);
			}
			// does this object type already exist? if not create it.
			if (number < 1) {
				updated = true;
				insertData(objectType, startData[i][1], startData[i][2], startData[i][3], startData[i][4], startData[i][5]);
			}
		}
		return updated;
	}
	
	public String type() {
		return getType();
	}
	
	/** Implementation of BundleComponent */ 
	public Class[] getRequiredInterfaces() {
		String interfaces = getStringColumnValue(COLUMN_REQUIRED_INTERFACES);
		Vector vector = seperateStringIntoVector(interfaces);

		if (vector == null) {
			return null;
		}
		Class[] array = new Class[vector.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = getClassForName((String) vector.get(i));
		}

		return array;
	}


	public Class getRequiredSuperClass() {
		return getClassForName(getStringColumnValue(COLUMN_REQUIRED_SUPER_CLASS));
	}
	
	public Class getFinalReflectionClass() {
		return getClassForName(getStringColumnValue(COLUMN_FINAL_REFLECTION_CLASS));
	}

	public String[] getMethodStartFilters() {
		String filters = getStringColumnValue(COLUMN_METHOD_START_FILTERS);
		Vector vector = seperateStringIntoVector(filters);
		if (vector == null) {
			return null;
		}
		return (String[]) vector.toArray(new String[]{});
	}
	

	public boolean validateInterfaces(Class validatingClass) {
		Class[] requiredInterfaces = this.getRequiredInterfaces();
		boolean returner = false;
		if (requiredInterfaces != null) {
			Class[] implementedInterfaces = validatingClass.getInterfaces();
			for (int i = 0; i < requiredInterfaces.length; i++) {
				System.out.println("checking req "+requiredInterfaces[i].getName());
				for (int j = 0; j < implementedInterfaces.length; j++) {
					System.out.println("checking imp"+implementedInterfaces[i].getName());
					if (requiredInterfaces[i].getName().equals(implementedInterfaces[i].getName()))
						returner = true;
					else {
						Class[] superInterfaces = implementedInterfaces[i].getInterfaces();
						for (int k = 0; k < superInterfaces.length; k++) {
							if (requiredInterfaces[i].getName().equals(superInterfaces[i].getName()))
								returner = true;
						}
					}
				}
				// if we don't have a match after for this round we exit
				if (!returner)
					return returner;
			}
		}
		else{
			return true;
		}
		return returner;
	}
	
	public boolean validateSuperClasses(Class validatingClass) {
		if(getRequiredSuperClass()==null){
			return true;
		}
		//System.out.println("getRequiredSuperClass().getName()                        = '"+getRequiredSuperClass().getName()+"'");
		//System.out.println("validatingClass.getSuperclass().getName()                = '"+validatingClass.getSuperclass().getName()+"'");
		//System.out.println("validatingClass.isAssignableFrom(getRequiredSuperClass()) = '"+validatingClass.isAssignableFrom(getRequiredSuperClass())+"'");
		//System.out.println("getRequiredSuperClass().isAssignableFrom(validatingClass) = '"+getRequiredSuperClass().isAssignableFrom(validatingClass)+"'");
		//return validatingClass.isAssignableFrom(getRequiredSuperClass());
		return getRequiredSuperClass().isAssignableFrom(validatingClass);
	}

	private Class getClassForName(String className) {
		if (className != null) {
			try {
				return RefactorClassRegistry.forName(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Vector seperateStringIntoVector(String string) {
		Vector vector = null;
		if (string != null) {
			vector = new Vector();
			string = TextSoap.findAndReplace(string, " ", "");
			int loc;
			while ( string.indexOf(",") > -1) {
				loc = string.indexOf(",");
				if (loc != -1) {
					vector.add(string.substring(0, loc));
					string = string.substring(loc+1, string.length());
				}
			}
			/** Adding the last one (string.indexOf(",") == -1) */ 
			vector.add(string);					
		}
		return vector;
	}
	
}
