package com.idega.core.persistence;

import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;


/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2009/02/06 19:00:13 $ by $Author: civilis $
 */
public class Param {

	final private String paramName;
	final private Object paramValue;

	public Param(String paramName, Object paramValue) {
		if (StringUtil.isEmpty(paramName) || paramValue == null) {
			throw new IllegalArgumentException("Empty argument(s): paramName="+paramName+", paramValue="+paramValue);
		}

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

	@Override
	public String toString() {
		return "param ("+getParamName()+" : "+getParamValue()+")";
	}

	public Object getParamValue() {
		if (CoreConstants.Y.equals(paramValue) || CoreConstants.N.equals(paramValue)) {
			return Character.valueOf(paramValue.toString().charAt(0));
		}

		return paramValue;
	}

	public String getParamName() {
		return paramName;
	}
}