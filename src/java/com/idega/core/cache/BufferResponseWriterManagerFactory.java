/**
 * 
 */
package com.idega.core.cache;


/**
 * <p>
 * TODO tryggvil Describe Type BufferResponseWriterManagerFactory
 * </p>
 *  Last modified: $Date: 2006/02/28 14:47:17 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class BufferResponseWriterManagerFactory {
	
	static BufferResponseWriterManager instance;
	
	public static BufferResponseWriterManager getInstance(){
		if(instance==null){
			try {
				instance = (BufferResponseWriterManager)Class.forName("com.idega.faces.ResponseWriterUtils").newInstance();
			}
			catch (InstantiationException e) {
			}
			catch (IllegalAccessException e) {
			}
			catch (ClassNotFoundException e) {
			}
		}
		return instance;
	}
	
}
