/**
 * 
 */
package com.idega.core.component.data.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.idega.core.component.data.BundleComponent;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.text.TextSoap;

@Entity
@Table(name = ICObjectType.ENTITY_NAME)
public class ICObjectType implements Serializable, BundleComponent {

	private static final long serialVersionUID = 1933640010305267773L;

	public static final String ENTITY_NAME = "ic_object_type";
	public static final String COLUMN_TYPE = "object_type";
	private static final String COLUMN_NAME = "object_type_name";
	private static final String COLUMN_REQUIRED_SUPER_CLASS = "req_super_class";
	private static final String COLUMN_FINAL_REFLECTION_CLASS = "final_reflection_class";
	private static final String COLUMN_REQUIRED_INTERFACES = "req_interfaces";
	private static final String COLUMN_METHOD_START_FILTERS = "method_start_filters";

	@Id
	@Column(name = COLUMN_TYPE, length = 100)
	private String type;

	@Column(name = COLUMN_NAME)
	private String name;

	@Column(name = COLUMN_REQUIRED_SUPER_CLASS)
	private String requiredSuperClass;

	@Column(name = COLUMN_FINAL_REFLECTION_CLASS)
	private String finalReflectionClass;

	@Column(name = COLUMN_REQUIRED_INTERFACES)
	private String requiredInterfaces;

	@Column(name = COLUMN_METHOD_START_FILTERS)
	private String methodStartFilters;

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type
	 *          the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *          the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the requiredSuperClass
	 */
	public Class getRequiredSuperClass() {
		return getClassForName(this.requiredSuperClass);
	}

	/**
	 * @param requiredSuperClass
	 *          the requiredSuperClass to set
	 */
	public void setRequiredSuperClass(String requiredSuperClass) {
		this.requiredSuperClass = requiredSuperClass;
	}

	/**
	 * @return the finalReflectionClass
	 */
	public Class getFinalReflectionClass() {
		return getClassForName(this.finalReflectionClass);
	}

	/**
	 * @param finalReflectionClass
	 *          the finalReflectionClass to set
	 */
	public void setFinalReflectionClass(String finalReflectionClass) {
		this.finalReflectionClass = finalReflectionClass;
	}

	/**
	 * @return the requiredInterfaces
	 */
	public Class[] getRequiredInterfaces() {
		Collection<String> interfaces = seperateStringIntoVector(this.requiredInterfaces);
		if (interfaces == null) {
			return null;
		}

		Class[] interfaceArray = new Class[interfaces.size()];
		int row = 0;
		for (String interfaceString : interfaces) {
			interfaceArray[row] = getClassForName(interfaceString);
			row++;
		}

		return interfaceArray;
	}

	/**
	 * @param requiredInterfaces
	 *          the requiredInterfaces to set
	 */
	public void setRequiredInterfaces(String requiredInterfaces) {
		this.requiredInterfaces = requiredInterfaces;
	}

	/**
	 * @return the methodStartFilters
	 */
	public String[] getMethodStartFilters() {
		Collection<String> methods = seperateStringIntoVector(this.methodStartFilters);
		if (methods == null) {
			return null;
		}
		return methods.toArray(new String[methods.size()]);
	}

	/**
	 * @param methodStartFilters
	 *          the methodStartFilters to set
	 */
	public void setMethodStartFilters(String methodStartFilters) {
		this.methodStartFilters = methodStartFilters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.idega.core.component.data.BundleComponent#validateInterfaces(java.lang
	 * .Class)
	 */
	public boolean validateInterfaces(Class validClass) {
		boolean returner = false;

		Class[] requiredInterfaces = this.getRequiredInterfaces();
		if (requiredInterfaces != null) {
			Class[] implementedInterfaces = validClass.getInterfaces();
			for (int i = 0; i < requiredInterfaces.length; i++) {
				for (int j = 0; j < implementedInterfaces.length; j++) {
					if (requiredInterfaces[i].getName().equals(implementedInterfaces[i].getName())) {
						returner = true;
					}
					else {
						Class[] superInterfaces = implementedInterfaces[i].getInterfaces();
						for (int k = 0; k < superInterfaces.length; k++) {
							if (requiredInterfaces[i].getName().equals(superInterfaces[i].getName())) {
								returner = true;
							}
						}
					}
				}
				if (!returner) {
					return returner;
				}
			}
		}
		else {
			return true;
		}

		return returner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.idega.core.component.data.BundleComponent#validateSuperClasses(java
	 * .lang.Class)
	 */
	public boolean validateSuperClasses(Class validClass) {
		if (getRequiredSuperClass() == null) {
			return true;
		}

		return getRequiredSuperClass().isAssignableFrom(validClass);
	}

	private Class getClassForName(String className) {
		if (className != null) {
			try {
				return RefactorClassRegistry.forName(className);
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Collection<String> seperateStringIntoVector(String string) {
		Collection<String> vector = null;
		if (string != null) {
			vector = new ArrayList();
			string = TextSoap.findAndReplace(string, " ", "");
			int loc;
			while (string.indexOf(",") > -1) {
				loc = string.indexOf(",");
				if (loc != -1) {
					vector.add(string.substring(0, loc));
					string = string.substring(loc + 1, string.length());
				}
			}
			vector.add(string);
		}
		return vector;
	}
}