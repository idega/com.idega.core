package com.idega.core.persistence;

import com.idega.util.CoreConstants;


/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/04/26 02:29:01 $ by $Author: civilis $
 */
public class Param {

	final private String paramName;
	final private Object paramValue;
	
	public Param(String paramName, Object paramValue) {
		
		if(paramName == null || CoreConstants.EMPTY.equals(paramName) || paramValue == null)
			throw new IllegalArgumentException("Empty argument(s): paramName="+paramName+", paramValue="+paramValue);
		
		this.paramName = paramName;
		this.paramValue = paramValue;
	}
	
	@Override
	public boolean equals(Object arg0) {
		
		if(super.equals(arg0))
			return true;
		
		if(arg0 instanceof Param) {
			
			return getParamName().equals(((Param)arg0).getParamName());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		
		return getParamName().hashCode();
	}

	public Object getParamValue() {
		return paramValue;
	}

	public String getParamName() {
		return paramName;
	}
}