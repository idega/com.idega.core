package com.idega.repository.data;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * Interfaces extending this interface are "placeholders for real implementations", that is
 * the <code>ImplementorRepository</code> should be used for looking up 
 * and getting an instance of a real  implementation.
 * The idea is to use an interface without taking care about the implementation.
 * The responsibility for the implementation is moved to other modules - 
 * they have the knowledge to do it. 
 * If a module provides an implementation it has to register the implementation using the class
 * <code>ImplementorRepository</code>, otherwise the module that needs the implementation is not able to find it.
 * Usually that should be done within an <code>IWBundleStartable</code>.
 * Keep in mind that - if needed - different implementations can be defined for different callers.
 * 
 * 
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jul 22, 2004
 */
public interface ImplementorPlaceholder {
	// this interface is used as a flag
}
