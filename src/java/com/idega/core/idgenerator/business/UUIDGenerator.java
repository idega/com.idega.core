/*
 * Created on 30.6.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.core.idgenerator.business;

import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;

/**
 * The default UUID Generator.<br>
 * Generates unique id string 36 characters long (128bit).<br>
 * The default implementation generates the string with a combination of a <br>
 * dummy ip address and a time based random number generator and uses the Jug Generator.<br>
 * For more info see the JUG project, http://www.doomdark.org/doomdark/proj/jug/
 * An example uid: ac483688-b6ed-4f45-ac64-c105e599d482 <br>
 * @author tryggvil
 */
public class UUIDGenerator implements IdGenerator, Singleton {

	private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new UUIDGenerator();}};
	
	private org.doomdark.uuid.UUIDGenerator uidGenerator = org.doomdark.uuid.UUIDGenerator.getInstance();

	
	/**
	 * This constructor should not be used by others
	 */
	private UUIDGenerator(){
		// empty
	}
	/**
	 * Generates unique id string 36 characters long (128bit).<br>
	 * The default implementation generates the string with a combination of a <br>
	 * dummy ip address and a time based random number generator.<br>
	 * For more info see the JUG project, http://www.doomdark.org/doomdark/proj/jug/
	 * An example uid: ac483688-b6ed-4f45-ac64-c105e599d482 <br>
	 */
	public String generateUUID(){
		return uidGenerator.generateTimeBasedUUID().toString();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.idgenerator.business.IdGenerator#generateId()
	 */
	public String generateId() {
		return generateUUID();
	}	
	
	public static UUIDGenerator getInstance() {
		return (UUIDGenerator) SingletonRepository.getRepository().getInstance(UUIDGenerator.class, instantiator);
	}
	
}
