/*
 * Created on 30.6.2004
 */
package com.idega.core.idgenerator.business;

/**
 * 
 * @author tryggvil
 */
public class IdGeneratorFactory {
	
	public static final String ALGORITHM_UUID="UUID";
	
	/**
	 * Gets the Default IdGenerator.<br>
	 * This is now implemented so that it uses the UUID Generator.
	 * @return
	 */
	public static IdGenerator getDefaultIdGenerator(){
		return getUUIDGenerator();
	}
	
	/**
	 * Gets the idGenerator implementation for the specified algoritm.<br>
	 * Currently only supports UUID.
	 * @param algorithm
	 * @return an algorithm instance or returns null if none is found.
	 */
	public static IdGenerator getIdGenerator(String algorithm){
		if(ALGORITHM_UUID.equals(algorithm)){
			return getUUIDGenerator();
		}
		return null;
	}
	
	/**
	 * Gets an instance of the UUIDGenerator
	 * @return
	 */
	public static IdGenerator getUUIDGenerator(){
		return UUIDGenerator.getInstance();
	}
	
}
