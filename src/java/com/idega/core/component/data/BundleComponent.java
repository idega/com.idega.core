/*
 * Created on Jun 21, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.core.component.data;
/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */
public interface BundleComponent {
	/**
	 * Gets the component type identifier
	 * @return type identifier
	 */
	public String getType();
	/**
	 * Gets the interfaces the component should implement
	 * @return interfaces, null if do not require any 
	 */
	public Class[] getRequiredInterfaces();
	/**
	* Gets the super classes the component should extend
	* @return interfaces, null if do not require any 
	*/
	public Class getRequiredSuperClass();
	/**
	 * Gets the final method reflection super class
	 * @return super class, null if we dont allow deep reflection
	 */
	public Class getFinalReflectionClass();
	/**
	 * Gets the method start filters 
	 * Used in reflection of object methods
	 * @return filters
	 */
	public String[] getMethodStartFilters();
	/**
	 * 
	 * @return true if the class implements the required interfaces
	 */
	public boolean validateInterfaces(Class validClass);
	/**
	* 
	* @return true if the class extends the required superclass
	*/
	public boolean validateSuperClasses(Class validClass);
}
