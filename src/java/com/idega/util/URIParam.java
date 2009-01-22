package com.idega.util;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $ Last modified: $Date: 2009/01/22 12:44:34 $ by $Author: civilis $
 */
public class URIParam {
	
	final private String paramName;
	final private String paramValue;
	
	public URIParam(String paramName, String paramValue) {
		
		if (StringUtil.isEmpty(paramName) || StringUtil.isEmpty(paramValue))
			throw new IllegalArgumentException("Empty argument(s): paramName="
			        + paramName + ", paramValue=" + paramValue);
		
		this.paramName = paramName;
		this.paramValue = paramValue;
	}
	
	@Override
	public boolean equals(Object arg0) {
		
		if (super.equals(arg0))
			return true;
		
		if (arg0 instanceof URIParam) {
			
			return getParamName().equals(((URIParam) arg0).getParamName());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		
		return getParamName().hashCode();
	}
	
	public String getParamValue() {
		return paramValue;
	}
	
	public String getParamName() {
		return paramName;
	}
}