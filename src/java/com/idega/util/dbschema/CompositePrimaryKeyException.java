package com.idega.util.dbschema;

/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:05:31 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class CompositePrimaryKeyException extends Exception {
	/**
	 * 
	 */
	public CompositePrimaryKeyException() {
		super("");
	}

	/**
	 * @param s
	 */
	public CompositePrimaryKeyException(String s) {
		super("CompositePrimaryKeyException: PrimaryKey is composite\n"+s);
	}
}
