/*
 * Created on Jul 22, 2003
 *
 */
package com.idega.data;

import com.idega.util.CoreConstants;

/**
 * IDOBoolean a wrapper for database boolean values
 * @author aron
 * @version 1.0
 */
public class IDOBoolean {
	public final static String TRUE = CoreConstants.Y;
	public final static String FALSE = CoreConstants.N;
	Boolean myBoolean;

	public IDOBoolean(boolean bool){
		this.myBoolean = new Boolean(bool);
	}

	public static IDOBoolean getIDOBoolean(String bool){
		if(bool.equals(TRUE)) {
			return new IDOBoolean(true);
		}
		else if(bool.equals(FALSE)) {
			return new IDOBoolean(false);
		}
		else {
			throw new IllegalArgumentException(bool+"is not "+TRUE+" or "+FALSE);
		}
	}

	public static boolean getBoolean(String bool){
		return getIDOBoolean(bool).booleanValue();
	}

	public boolean booleanValue(){
		return this.myBoolean.booleanValue();
	}

	public static String toString(boolean bool){
		IDOBoolean ido = new IDOBoolean(bool);
		return ido.toString();
	}

	@Override
	public String toString(){
		return this.myBoolean.booleanValue()?TRUE:FALSE;
	}


}
