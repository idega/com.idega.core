package com.idega.io.serialization;

import java.io.File;
import java.rmi.RemoteException;
import com.idega.core.file.data.ICFile;
import com.idega.presentation.IWContext;
import com.idega.util.xml.XMLData;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * A Writer stores Storable objects.
 * 
 * Implementation of the Visitor pattern.
 * 
 * Use this interface it in the following way:
 * 
 * a class Apple implements Storable,
 * a class Banana implements Storable.  
 * 
 * Here is the implementation of the write method in the 
 * class FruitWriter (that implements Writer)
 * 
 * public Object write(Apple anApple) {
 * 	do something, store the apple
 * 	return something if you like
 * }
 * 
 * public Object write(Banana aBanana) {
 * 	do something, store the banana
 * 	return something if you like
 * }
 * 
 * Here is the implementation of the write method in 
 * the class Apple and in the class Banana:
 * 
 *	public Object write(Writer writer) {
 *	  return writer.write(this);
 *	}
 * 	
 * Here is a implementation of a FruitFactory....
 * 
 * FruitWriter writer = new FruitWriter();
 * Collection list  -- a collection of fruits
 * for all elements do {
 * 	element.write(writer);
 * }
 * 
 * See also {@link com.idega.io.serialization.Storable Storable}
 *  
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 18, 2004
 */
public interface ObjectWriter {
	
	public Object write(File file, IWContext iwc) throws RemoteException;
	
	public Object write(XMLData xmlData, IWContext iwc) throws RemoteException;
	
	public Object write(ICFile file, IWContext iwc) throws RemoteException;
	
	// add more write methods !!
}
