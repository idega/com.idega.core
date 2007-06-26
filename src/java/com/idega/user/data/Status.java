package com.idega.user.data;


public interface Status extends com.idega.data.IDOEntity
{
 public java.lang.String getStatusKey();
 public void initializeAttributes();
 public void setStatusKey(java.lang.String p0);
 
	/**
	 * @see com.idega.user.data.StatusBMPBean#setOrder
	 */
	public void setStatusOrder(Integer order);
	
	/**
	 * @see com.idega.user.data.StatusBMPBean#getOrder
	 */
	public Integer getStatusOrder();
}
